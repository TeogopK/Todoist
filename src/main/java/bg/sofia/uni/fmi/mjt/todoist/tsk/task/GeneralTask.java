package bg.sofia.uni.fmi.mjt.todoist.tsk.task;

public final class GeneralTask extends AbstractTask {
    private GeneralTask(String name) {
        super(name);
    }

    public static class TaskBuilder extends AbstractTask.AbstractBuilder<GeneralTask, TaskBuilder> {

        @Override
        protected GeneralTask getObject(String name) {
            return new GeneralTask(name);
        }

        @Override
        protected TaskBuilder thisObject() {
            return this;
        }

        public TaskBuilder(String name) {
            super(name);
        }
    }
}
