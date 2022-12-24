import console.BoardConsole;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ConsoleMain {
    public static void main(String[] args) {
        BoardConsole boardConsole = new BoardConsole();
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(boardConsole::draw, 0, 1000 / 60, TimeUnit.MILLISECONDS);
    }
}
