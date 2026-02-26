package shell;

import shell.cli.Prompt;
import shell.core.ResponseBuilder;
import shell.core.ShellContext;

public class Shell {
    private boolean running = true;
    private final Prompt prompt = new Prompt();
    public void run() {
        ShellContext shellContext = new ShellContext();
        ResponseBuilder rbObj = new ResponseBuilder(shellContext);
        CommandResult outputObj;
        String input;
        while (running) {
            input = prompt.readLine();
            outputObj = rbObj.handle(input);
            if(!outputObj.getRunning()){
                running = outputObj.getRunning();
            } else {
                System.out.println(outputObj.getOutput());
            }
        }
    }
}
