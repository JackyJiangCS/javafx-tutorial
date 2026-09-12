/** A parsed command with a reply that can be displayed in the chat. */
public abstract class Command {
    protected String response;

    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws DukeException;

    public String getString() { return response; }
}

class AddCommand extends Command {
    private final Task task;

    AddCommand(Task task) { this.task = task; }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws DukeException {
        tasks.add(task);
        try {
            storage.save(tasks);
        } catch (DukeException e) {
            tasks.remove(tasks.size());
            throw e;
        }
        response = "Added:\n" + task + "\nYou now have " + tasks.size() + " task(s).";
    }
}

class ChangeMarkCommand extends Command {
    private final int number;
    private final boolean done;

    ChangeMarkCommand(int number, boolean done) {
        this.number = number;
        this.done = done;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws DukeException {
        Task task = tasks.get(number);
        boolean previous = task.isDone();
        task.setDone(done);
        try {
            storage.save(tasks);
        } catch (DukeException e) {
            task.setDone(previous);
            throw e;
        }
        response = "Marked as " + (done ? "done" : "not done") + ":\n" + task;
    }
}

class DeleteCommand extends Command {
    private final int number;

    DeleteCommand(int number) { this.number = number; }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws DukeException {
        Task task = tasks.remove(number);
        try {
            storage.save(tasks);
        } catch (DukeException e) {
            tasks.insert(number, task);
            throw e;
        }
        response = "Deleted:\n" + task + "\nYou now have " + tasks.size() + " task(s).";
    }
}

class ListCommand extends Command {
    private final String keyword;

    ListCommand(String keyword) { this.keyword = keyword; }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        response = ui.list(tasks, keyword);
    }
}

class ReplyCommand extends Command {
    ReplyCommand(String reply) { response = reply; }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        // The reply is already available; no task changes are needed.
    }
}
