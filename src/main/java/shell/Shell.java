package shell;

import shell.cli.Prompt;
import shell.core.ResponseBuilder;
import shell.PathUtils.*;
import java.nio.file.Path;
import java.util.List;

public class Shell {
    private boolean running = true;
    private final Prompt prompt = new Prompt();
    public void run() {
        PathUtils.setPathDirs();
        List<Path> envPaths = PathUtils.getPathDirs();
        while (running) {
            String input = prompt.readLine();
            String output = ResponseBuilder.handle(input);
            if (!output.isEmpty()) {
                System.out.println(output);
            }
        }
    }

    public void stopShell() {
        running = false;
    }
}
