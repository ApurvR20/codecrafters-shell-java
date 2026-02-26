package shell.core;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import shell.CommandResult;
import shell.ExeHandler;

public final class ResponseBuilder {

    ShellContext shellContext;
    public ResponseBuilder(ShellContext shellContext){
        this.shellContext = shellContext;
    }

    public CommandResult handle(String input) {

        CommandResult resObj = new CommandResult(), tempObj;
        ExeHandler exeHandlerObj = new ExeHandler();
        Tokenizer obj = new Tokenizer();
        List<String> tokens = obj.tokenizer(input);
        Builtins builtinObj = new Builtins(shellContext);
        String token, nextToken;
        int nextRedirection;
        List<String> currInput;
        Path filePath, parentFilePath;

        for(int i = 0;i < tokens.size(); i++){
            token = tokens.get(i);
            nextRedirection = getNextRedirection(tokens,i);

            //correct for all cases where an object is t be passed instead of a single string, including response
            // builder
            if(builtinObj.isBuiltin(token)){
                if(tokens.size() > 1) {
                    tempObj = builtinObj.runBuiltin(token, tokens.subList(i + 1,nextRedirection));
                } else {
                    tempObj = builtinObj.runBuiltin(token,new ArrayList<>());
                }

                if(tempObj.getRunning()){
                    resObj.appendOutput(tempObj.getOutput());
                } else {
                    resObj.setRunning(false);
                    break;
                }
            }
            else if(token.equals(">")) {
                nextToken = tokens.get(i+1);
                i++;
                if(nextToken.contains("/")){
                    // this is already a path
                    filePath = Paths.get(nextToken);
                } else {
                    filePath = shellContext.getHome().resolve(nextToken);
                }

                filePath = filePath.toAbsolutePath();
                try {
                    parentFilePath = filePath.getParent();
                    if(parentFilePath != null){
                        Files.createDirectories(parentFilePath);
                    }
                    Files.writeString(filePath,resObj.getOutput());
                    resObj.resetOutput();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
            else {
                currInput = tokens.subList(i, nextRedirection);
                filePath = exeHandlerObj.findExec(shellContext.getPathDirs(), token);
                if(filePath == null){
                    resObj.appendOutput(token+": command not found");
                } else {
                    exeHandlerObj.runExe(currInput.toArray(new String[0]),resObj.getOutput(), shellContext.getCWD());
                }
            }

            i+=nextRedirection-1;
        }
        return resObj;
    }

    private int getNextRedirection(List<String> tokens, int i){

        for(;i < tokens.size(); i++){
            if(tokens.get(i).equals(">")){
                break;
            }
        }

        return i;
    }


}
