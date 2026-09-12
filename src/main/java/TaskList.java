import java.util.ArrayList;
import java.util.List;

/** Owns the tasks and validates the one-based numbers used in chat. */
public class TaskList {
    private final List<Task> tasks = new ArrayList<>();

    public List<Task> all() { return List.copyOf(tasks); }
    public int size() { return tasks.size(); }
    public void add(Task task) { tasks.add(task); }
    public void insert(int number, Task task) { tasks.add(number - 1, task); }

    public Task get(int number) throws DukeException {
        if (number < 1 || number > tasks.size()) {
            throw new DukeException("Choose a task number between 1 and " + tasks.size() + ".");
        }
        return tasks.get(number - 1);
    }

    public Task remove(int number) throws DukeException {
        Task task = get(number);
        tasks.remove(number - 1);
        return task;
    }
}
