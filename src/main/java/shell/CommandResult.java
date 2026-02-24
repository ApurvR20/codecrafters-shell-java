package shell;

public class CommandResult {
    private String output;
    private boolean running;

    public CommandResult(){
        this.output = "";
        this.running = true;
    }
    public CommandResult(boolean running, String output){
        this.output = output;
        this.running = running;
    }

    public String getOutput(){
        return output;
    }

    public boolean getRunning(){
        return running;
    }

    public void setRunning(boolean newRunning){
        running = newRunning;
    }

    public void appendOutput(String partialOutput){
        output += partialOutput;
    }


}
