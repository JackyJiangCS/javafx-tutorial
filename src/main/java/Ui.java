import java.util.Locale;
import java.util.List;

/** Formats replies for the JavaFX chat. */
public class Ui {
    public String list(TaskList tasks, String keyword) {
        StringBuilder reply = new StringBuilder();
        List<Task> entries = tasks.all();
        for (int i = 0; i < entries.size(); i++) {
            Task task = entries.get(i);
            if (task.getDescription().toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT))) {
                reply.append(i + 1).append(". ").append(task).append('\n');
            }
        }
        return reply.isEmpty() ? "No tasks found." : reply.toString().stripTrailing();
    }
}
