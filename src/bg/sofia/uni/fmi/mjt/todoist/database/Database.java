package bg.sofia.uni.fmi.mjt.todoist.database;

import bg.sofia.uni.fmi.mjt.todoist.database.serializers.AllAccountsSerializer;
import bg.sofia.uni.fmi.mjt.todoist.database.serializers.AllCollaborationsSerializer;
import bg.sofia.uni.fmi.mjt.todoist.exceptions.CollaborationNotFoundException;
import bg.sofia.uni.fmi.mjt.todoist.exceptions.CollaborationRightsExceededException;
import bg.sofia.uni.fmi.mjt.todoist.exceptions.UserDoesNotExistException;
import bg.sofia.uni.fmi.mjt.todoist.task.collaboration.Collaboration;
import bg.sofia.uni.fmi.mjt.todoist.database.serializers.UserPasswordsSerializer;
import bg.sofia.uni.fmi.mjt.todoist.task.CollaborationTask;
import bg.sofia.uni.fmi.mjt.todoist.task.TaskKey;
import bg.sofia.uni.fmi.mjt.todoist.user.Account;
import bg.sofia.uni.fmi.mjt.todoist.user.UserInfo;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Database {
    private Map<String, UserInfo> userPasswords;

    private Map<String, Account> accounts;

    private Map<String, Map<String, Collaboration>> collaborations; // <adminUser, <name, collaboration>>

    public Database() {
        loadDatabase();
    }

    public Account getAccount(String accountName) {
        return accounts.get(accountName);
    }

    public boolean doesAccountAlreadyExist(String accountName) {
        return accounts.containsKey(accountName);
    }

    public void loadDatabase() {
        userPasswords = UserPasswordsSerializer.getUserPasswords();

        accounts = AllAccountsSerializer.loadAllUserAccounts(userPasswords);

        collaborations = AllCollaborationsSerializer.loadAllCollaborations(userPasswords.keySet());
    }

    public void saveDatabase() {
        UserPasswordsSerializer.saveUserPasswords(userPasswords.values());

        AllAccountsSerializer.saveAllUserAccounts(accounts);

        AllCollaborationsSerializer.saveAllCollaborations(collaborations);
    }

    public void addAccount(UserInfo userInfo) {
        Account account = new Account(userInfo);

        String username = userInfo.getUsername();

        accounts.put(username, account);
        userPasswords.put(username, userInfo);

        UserPasswordsSerializer.saveUserPasswords(userPasswords.values());
        AllAccountsSerializer.setAllForUser(username);
        AllCollaborationsSerializer.createUserCollaborations(username);

        collaborations.putIfAbsent(username, new HashMap<>());
    }

    public void saveOnlyTasksOfUser(String username) {
        AllAccountsSerializer.saveOneUserTasks(username, accounts.get(username).getOwnTasks().values());
    }

    public void saveCollaborationsOfUser(String username) {
        AllAccountsSerializer.saveOneCollaborationIDs(username, accounts.get(username).getCollaborationsIDs());
        AllCollaborationsSerializer.saveCollaboration(username, collaborations.get(username).values());
    }

    private Collaboration getCollaboration(String username, String collaborationName) {
        Account user = accounts.get(username);

        String adminName = user.getAdminOfCollaboration(collaborationName);

        if (!collaborations.get(adminName).containsKey(collaborationName)) {
            throw new CollaborationNotFoundException("Can not find collaboration with desired name");
        }

        return collaborations.get(adminName).get(collaborationName);
    }

    public void addUserInCollaboration(String username, String collaborationName, String addedUser) {
        if (!accounts.containsKey(addedUser)) {
            throw new UserDoesNotExistException("Can not add a user to task if user does not exist");
        }

        Collaboration collaboration = getCollaboration(username, collaborationName);

        collaboration.addParticipant(addedUser);
        accounts.get(addedUser).addCollaboration(collaborationName, collaboration.getCreatorName());

        saveCollaborationsOfUser(username);
        AllAccountsSerializer.saveOneCollaborationIDs(addedUser, accounts.get(addedUser).getCollaborationsIDs());
    }

    public void assignTaskInCollaboration(String username, String collaborationName, TaskKey taskKey, String assignee) {
        Collaboration collaboration = getCollaboration(username, collaborationName);

        collaboration.addAssignee(taskKey, assignee);

        saveCollaborationsOfUser(collaboration.getCreatorName());
    }

    public Collection<CollaborationTask> listTasksInCollaboration(String username, String collaborationName) {
        return getCollaboration(username, collaborationName).getTasks();
    }

    public Collection<String> listUsersInCollaboration(String username, String collaborationName) {
        return getCollaboration(username, collaborationName).getParticipants();
    }

    public void addCollaboration(String adminName, String collaborationName) {
        Account user = accounts.get(adminName);

        user.addCollaborationAsAdmin(collaborationName);

        Collaboration newCollaboration = new Collaboration(collaborationName, adminName);

        collaborations.putIfAbsent(adminName, new HashMap<>());
        collaborations.get(adminName).put(collaborationName, newCollaboration);

        saveCollaborationsOfUser(adminName);
    }

    public void deleteCollaboration(String adminName, String collaborationName) {
        Account admin = accounts.get(adminName);

        if (!admin.checkIsCreatorOfCollaboration(collaborationName)) {
            throw new CollaborationRightsExceededException("Only the creator can delete a collaboration");
        }

        Set<String> participants = getCollaboration(adminName, collaborationName).getParticipants();

        participants.forEach(p -> accounts.get(p).removeCollaboration(collaborationName));

        collaborations.get(adminName).remove(collaborationName);

        saveCollaborationsOfUser(adminName);
        participants.forEach(
            p -> AllAccountsSerializer.saveOneCollaborationIDs(p, accounts.get(p).getCollaborationsIDs()));
    }

    public Collection<Collaboration> listCollaborations(String username) {
        Account user = accounts.get(username);

        var collaborationsIDs = user.getCollaborationsIDs();

        return collaborationsIDs.entrySet().stream().map(e -> collaborations.get(e.getValue()).get(e.getKey()))
            .toList();
    }
}
