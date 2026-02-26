package shell;

public class CommandResult {
    private StringBuilder output = new StringBuilder();
    private boolean running;

    public CommandResult(){
        this.output.setLength(0);
        this.running = true;
    }
    public CommandResult(boolean running, StringBuilder output){
        this.output.setLength(0);
        this.output.append(output);
        this.running = running;
    }

    public StringBuilder getOutput(){
        return output;
    }

    public void resetOutput(){
        output.setLength(0);
    }

    public void setOutput(StringBuilder newOutput){
        resetOutput();
        output.append(newOutput);
    }

    public boolean getRunning(){
        return running;
    }

    public void setRunning(boolean newRunning){
        running = newRunning;
    }

    public void appendOutput(String partialOutput){
        output.append(partialOutput);
    }
    public void appendOutput(StringBuilder partialOutput){
        output.append(partialOutput);
    }


}
