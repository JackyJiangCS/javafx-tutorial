import java.nio.file.Path;

public class Duke {

    private String commandType = "";
    private TaskList tasks;
    private final Ui ui = new Ui();
    private final Storage storage;

    public Duke() {
        this(Path.of("data", "duke.txt"));
    }

    public Duke(Path storagePath) {
        storage = new Storage(storagePath);
    }
    public static void main(String[] args) {
        Launcher.main(args);
    }

    /**
     * Generates a response for the user's chat message.
     */
    public String getResponse(String input) {
        commandType = "";
        try {
            if (tasks == null) {
                tasks = storage.load();
            }
            Command c = Parser.parse(input);
            c.execute(tasks, ui, storage);
            commandType = c.getClass().getSimpleName();
            return c.getString();
        } catch (DukeException e) {
            return "Error: " + e.getMessage();
        }
    }

    public String getCommandType() {
        return commandType;
    }
}
