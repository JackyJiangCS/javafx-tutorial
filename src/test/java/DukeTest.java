import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

/** Dependency-free integration checks, run by Gradle check. */
public class DukeTest {
    public static void main(String[] args) throws Exception {
        Path directory = Files.createTempDirectory("duke-test-");
        try {
            Path file = directory.resolve("nested/tasks.txt");
            Duke duke = new Duke(file);
            contains(duke.getResponse("list"), "No tasks found");
            contains(duke.getResponse("  todo   Read | 书\tbook  "), "[T][ ] Read | 书\tbook");
            equal(duke.getCommandType(), "AddCommand");
            contains(duke.getResponse("deadline Homework /by Friday"), "(by: Friday)");
            contains(duke.getResponse("event Meeting /from 2pm /to 3pm"), "(from: 2pm to: 3pm)");
            contains(duke.getResponse("mark 2"), "[D][X]");
            equal(duke.getCommandType(), "ChangeMarkCommand");
            contains(new Duke(file).getResponse("list"), "2. [D][X] Homework");
            contains(duke.getResponse("unmark 2"), "[D][ ]");
            contains(duke.getResponse("find MEET"), "3. [E]");
            contains(duke.getResponse("delete 1"), "Deleted");
            equal(duke.getCommandType(), "DeleteCommand");
            equal(new Duke(file).getResponse("list"), duke.getResponse("list"));
            for (String invalid : new String[] {null, "", " ", "unknown", "todo", "deadline test",
                    "deadline test /by", "event test /from 2pm", "event /from 2 /to 3",
                    "event test /from 2 /to", "mark 0", "mark -1", "mark abc", "delete 99",
                    "mark 99999999999999", "find", "list extra", "bye extra"}) {
                contains(duke.getResponse(invalid), "Error:");
                equal(duke.getCommandType(), "");
            }
            contains(duke.getResponse("help"), "todo DESCRIPTION");
            contains(duke.getResponse("bye"), "Bye");

            Path broken = directory.resolve("broken.txt");
            Files.writeString(broken, "invalid task data");
            contains(new Duke(broken).getResponse("todo test"), "Error:");
            equal(Files.readString(broken), "invalid task data");

            // A non-empty directory at the save path forces failure on every platform.
            Files.delete(file);
            Files.createDirectory(file);
            Files.writeString(file.resolve("keep.txt"), "keep");
            String before = duke.getResponse("list");
            for (String mutation : new String[] {"todo cannot save", "mark 1", "delete 1"}) {
                contains(duke.getResponse(mutation), "Error: Could not save tasks");
                equal(duke.getCommandType(), "");
                equal(duke.getResponse("list"), before);
            }
            System.out.println("All Duke integration checks passed.");
        } finally {
            try (var paths = Files.walk(directory)) {
                for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                    Files.deleteIfExists(path);
                }
            }
        }
    }

    private static void contains(String actual, String expected) {
        if (!actual.contains(expected)) {
            throw new AssertionError("Expected <" + expected + "> in <" + actual + ">");
        }
    }

    private static void equal(String actual, String expected) {
        if (!actual.equals(expected)) {
            throw new AssertionError("Expected <" + expected + "> but got <" + actual + ">");
        }
    }
}
