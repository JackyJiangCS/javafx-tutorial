/** A task and its optional deadline or event times. */
public class Task {
    enum Type { TODO, DEADLINE, EVENT }

    private final Type type;
    private final String description;
    private final String start;
    private final String end;
    private boolean done;

    public Task(Type type, String description, String start, String end, boolean done) {
        this.type = type;
        this.description = description;
        this.start = start;
        this.end = end;
        this.done = done;
    }

    public Type getType() { return type; }
    public String getDescription() { return description; }
    public String getStart() { return start; }
    public String getEnd() { return end; }
    public boolean isDone() { return done; }
    public void setDone(boolean done) { this.done = done; }

    @Override
    public String toString() {
        String prefix = switch (type) {
        case TODO -> "[T]";
        case DEADLINE -> "[D]";
        case EVENT -> "[E]";
        };
        String timing = switch (type) {
        case TODO -> "";
        case DEADLINE -> " (by: " + end + ")";
        case EVENT -> " (from: " + start + " to: " + end + ")";
        };
        return prefix + "[" + (done ? "X" : " ") + "] " + description + timing;
    }
}
