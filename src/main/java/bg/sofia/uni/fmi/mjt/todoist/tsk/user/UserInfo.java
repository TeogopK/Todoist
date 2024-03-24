package bg.sofia.uni.fmi.mjt.todoist.tsk.user;

import bg.sofia.uni.fmi.mjt.todoist.tsk.user.security.PasswordHasher;

import java.util.Arrays;
import java.util.Objects;

public class UserInfo {

    private final static String REGEX_ONLY_LATIN_LETTERS_NUMBERS_AND_UNDERSCORE = "[a-zA-Z0-9_]+";

    private final String username;
    private byte[] password;
    private byte[] salt;

    public static boolean isUsernameValid(String str) {
        return str.matches(REGEX_ONLY_LATIN_LETTERS_NUMBERS_AND_UNDERSCORE);
    }

    public UserInfo(String username, String password) {
        this.username = username;

        setSalt();
        setPassword(password);
    }

    public String getUsername() {
        return username;
    }

    private void setPassword(String str) {
        this.password = PasswordHasher.getHashedPassword(str, salt);
    }

    private void setSalt() {
        this.salt = PasswordHasher.getSalt();
    }

    public boolean isEnteredPasswordCorrect(String stringPassword) {
        var enteredPassword = PasswordHasher.getHashedPassword(stringPassword, salt);

        return Arrays.equals(password, enteredPassword);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserInfo account = (UserInfo) o;
        return username.equals(account.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
