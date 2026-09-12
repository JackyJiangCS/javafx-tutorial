This is a starter repo for https://se-education.org/guides/tutorials/javaFx.html

Run the chat with `./gradlew run` (Windows: `.\gradlew.bat run`) using JDK 25.
Type `help` to see the commands:

- `todo Read a book`
- `deadline Submit homework /by Friday`
- `event Team meeting /from 2pm /to 3pm`
- `list` or `find book`
- `mark 1`, `unmark 1`, or `delete 1` (numbers refer to the full list)
- `bye` displays a farewell; close the window to exit.

Task changes are saved in `data/duke.txt` relative to the working directory and
loaded on the next launch. Dates and times are stored as entered.
Run `./gradlew check` to verify the task commands and persistence.
