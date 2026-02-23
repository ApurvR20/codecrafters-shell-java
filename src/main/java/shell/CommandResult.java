package shell;

public class CommandResult {
    private final String output;
    private final boolean running;

    public CommandResult(String output, boolean running){
        this.output = output;
        this.running = running;
    }

    public String getOutput(){
        return output;
    }

    public boolean getRunning(){
        return running;
    }
}
