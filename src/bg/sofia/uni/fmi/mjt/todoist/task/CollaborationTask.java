package bg.sofia.uni.fmi.mjt.todoist.task;

public final class CollaborationTask extends AbstractTask {
    private String assignee;

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    private CollaborationTask(String name) {
        super(name);
    }

    public static class CollaborationTaskBuilder
        extends AbstractTask.AbstractBuilder<CollaborationTask, CollaborationTaskBuilder> {

        @Override
        protected CollaborationTask getObject(String name) {
            return new CollaborationTask(name);
        }

        @Override
        protected CollaborationTaskBuilder thisObject() {
            return this;
        }

        public CollaborationTaskBuilder setAssignee(String assignee) {
            object.assignee = assignee;
            return this;
        }

        public CollaborationTaskBuilder(String name) {
            super(name);
        }
    }
}

