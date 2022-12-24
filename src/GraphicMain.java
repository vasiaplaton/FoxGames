import graphic.*;

import javax.swing.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GraphicMain {
    public static void main(String[] args) {
        int WIDTH = 800;
        int HEIGHT = 800;
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(WIDTH, HEIGHT);

        DrawPanel drawPanel = new DrawPanel();

        frame.add(drawPanel);

        frame.setVisible(true);

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> SwingUtilities.updateComponentTreeUI(frame),
                0, 1000 / 60, TimeUnit.MILLISECONDS);
    }
}