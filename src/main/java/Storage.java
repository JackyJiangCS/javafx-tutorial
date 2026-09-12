import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/** Persists tasks as tab-separated records with Base64-encoded text fields. */
public class Storage {
    private final Path path;

    public Storage(Path path) { this.path = path.toAbsolutePath(); }

    public TaskList load() throws DukeException {
        TaskList tasks = new TaskList();
        List<String> lines;
        try {
            lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (NoSuchFileException e) {
            return tasks;
        } catch (IOException e) {
            throw new DukeException("Could not read tasks from " + path + ": " + e.getMessage());
        }
        for (int i = 0; i < lines.size(); i++) {
            try {
                String[] fields = lines.get(i).split("\t", -1);
                if (fields.length != 5 || !(fields[1].equals("true") || fields[1].equals("false"))) {
                    throw new IllegalArgumentException();
                }
                Task.Type type = Task.Type.valueOf(fields[0]);
                String description = decode(fields[2]);
                String start = decode(fields[3]);
                String end = decode(fields[4]);
                if (description.isBlank() || (type != Task.Type.TODO && end.isBlank())
                        || (type == Task.Type.EVENT && start.isBlank())
                        || (type != Task.Type.EVENT && !start.isEmpty())
                        || (type == Task.Type.TODO && !end.isEmpty())) {
                    throw new IllegalArgumentException();
                }
                tasks.add(new Task(type, description, start, end, Boolean.parseBoolean(fields[1])));
            } catch (IllegalArgumentException e) {
                throw new DukeException("Invalid task data on line " + (i + 1) + " in " + path
                        + ". Fix the file before continuing; it has not been overwritten.");
            }
        }
        return tasks;
    }

    public void save(TaskList tasks) throws DukeException {
        Path temporary = null;
        try {
            Files.createDirectories(path.getParent());
            List<String> lines = new ArrayList<>();
            for (Task task : tasks.all()) {
                lines.add(task.getType() + "\t" + task.isDone() + "\t" + encode(task.getDescription())
                        + "\t" + encode(task.getStart()) + "\t" + encode(task.getEnd()));
            }
            temporary = Files.createTempFile(path.getParent(), "duke-", ".tmp");
            Files.write(temporary, lines, StandardCharsets.UTF_8);
            try {
                Files.move(temporary, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporary, path, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new DukeException("Could not save tasks: " + e.getMessage());
        } finally {
            if (temporary != null) {
                try {
                    Files.deleteIfExists(temporary);
                } catch (IOException ignored) {
                    // Keep the original storage error if temporary-file cleanup also fails.
                }
            }
        }
    }

    private static String encode(String text) {
        return Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }

    private static String decode(String text) {
        return new String(Base64.getDecoder().decode(text), StandardCharsets.UTF_8);
    }
}
