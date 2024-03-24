package bg.sofia.uni.fmi.mjt.todoist.tsk.task;

import java.time.LocalDate;
import java.util.Objects;

// Thanks to https://stackoverflow.com/a/24372759
public abstract class AbstractTask {

    protected final String name;

    protected LocalDate date;
    protected LocalDate dueDate;
    protected String description;

    protected boolean isFinished;


    public String getName() {
        return name;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalDate getDueData() {
        return dueDate;
    }

    public String getDescription() {
        return description;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setDueData(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    protected AbstractTask(String name) {
        this.name = name;

        this.date = null;
        this.dueDate = null;
        this.description = null;
        this.isFinished = false;
    }

    protected abstract static class AbstractBuilder<T extends AbstractTask, B extends AbstractBuilder<T, B>> {

        protected final T object;
        protected final B thisObject;

        protected abstract T getObject(String name);

        protected abstract B thisObject();

        protected AbstractBuilder(String name) {
            object = getObject(name);
            thisObject = thisObject();
        }

        public B setDate(LocalDate date) {
            object.date = date;
            return thisObject();
        }

        public B setDueDate(LocalDate dueDate) {
            object.dueDate = dueDate;
            return thisObject();
        }

        public B setDescription(String description) {
            object.description = description;
            return thisObject();
        }

        public T build() {
            return object;
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractTask that = (AbstractTask) o;
        return name.equals(that.name) && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, date);
    }
}
