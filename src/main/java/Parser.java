/** Converts chat input into validated commands. Dates are kept as user-entered text. */
public class Parser {
    public static Command parse(String input) throws DukeException {
        if (input == null || input.isBlank()) {
            throw new DukeException("Enter a command. Type help to see the available commands.");
        }
        String[] parts = input.strip().split("\\s+", 2);
        String argument = parts.length == 2 ? parts[1].strip() : "";
        return switch (parts[0]) {
        case "todo" -> new AddCommand(new Task(Task.Type.TODO, required(argument, "todo DESCRIPTION"),
                "", "", false));
        case "deadline" -> timedTask(argument, false);
        case "event" -> timedTask(argument, true);
        case "mark", "unmark" -> new ChangeMarkCommand(number(argument), parts[0].equals("mark"));
        case "delete" -> new DeleteCommand(number(argument));
        case "find" -> new ListCommand(required(argument, "find KEYWORD"));
        case "list" -> {
            noArgument(argument);
            yield new ListCommand("");
        }
        case "bye" -> {
            noArgument(argument);
            yield new ReplyCommand("Bye. Hope to see you again soon!");
        }
        case "help" -> {
            noArgument(argument);
            yield new ReplyCommand("Commands:\n"
                    + "todo DESCRIPTION\ndeadline DESCRIPTION /by WHEN\n"
                    + "event DESCRIPTION /from START /to END\n"
                    + "list\nfind KEYWORD\nmark NUMBER\nunmark NUMBER\ndelete NUMBER\nbye");
        }
        default -> throw new DukeException("Unknown command. Type help to see the available commands.");
        };
    }

    private static Command timedTask(String argument, boolean event) throws DukeException {
        String usage = event ? "event DESCRIPTION /from START /to END" : "deadline DESCRIPTION /by WHEN";
        String[] fields = argument.split(event ? "\\s+/from\\s+" : "\\s+/by\\s+", 2);
        if (fields.length != 2) {
            throw new DukeException("Use: " + usage);
        }
        String description = required(fields[0].strip(), usage);
        if (!event) {
            return new AddCommand(new Task(Task.Type.DEADLINE, description, "",
                    required(fields[1].strip(), usage), false));
        }
        String[] times = fields[1].split("\\s+/to\\s+", 2);
        if (times.length != 2) {
            throw new DukeException("Use: " + usage);
        }
        return new AddCommand(new Task(Task.Type.EVENT, description, required(times[0].strip(), usage),
                required(times[1].strip(), usage), false));
    }

    private static String required(String value, String usage) throws DukeException {
        if (value.isBlank()) {
            throw new DukeException("Use: " + usage);
        }
        return value;
    }

    private static int number(String argument) throws DukeException {
        try {
            int number = Integer.parseInt(argument);
            if (number > 0) {
                return number;
            }
        } catch (NumberFormatException ignored) {
            // Report the same friendly error for non-numeric and oversized inputs.
        }
        throw new DukeException("Provide a positive task number, for example: mark 1.");
    }

    private static void noArgument(String argument) throws DukeException {
        if (!argument.isEmpty()) {
            throw new DukeException("This command does not take any arguments.");
        }
    }
}
