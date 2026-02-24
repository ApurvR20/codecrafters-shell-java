package shell.core;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import shell.CommandResult;
import shell.PathUtils;

public final class ResponseBuilder {

    public CommandResult handle(String input) {

        CommandResult resObj = new CommandResult(), tempObj;
        StringBuilder response = new StringBuilder();
        Tokenizer obj = new Tokenizer();
        PathUtils pathObj = new PathUtils();
        List<String> tokens = obj.tokenizer(input);
        Builtins builtinObj = new Builtins();
        String token, nextToken;
        int nextRedirection;
        List<String> currInput;
        Path filePath, parentFilePath;

        for(int i = 0;i < tokens.size(); i++){
            token = tokens.get(i);
            nextRedirection = getNextRedirection(tokens,i);

            //correct for all cases where an object is t be passed instead of a single string, including responsebuilder
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
                    return resObj;
                }
            }
            else if(token.equals(">")) {
                nextToken = tokens.get(i+1);
                i++;
                if(nextToken.contains("/")){
                    // this is already a path
                    filePath = Paths.get(nextToken);
                } else {
                    filePath = Paths.get(PathUtils.getHomePath()).resolve(nextToken);
                }

                filePath = filePath.toAbsolutePath();
                try {
                    parentFilePath = filePath.getParent();
                    if(parentFilePath != null){
                        Files.createDirectories(parentFilePath);
                    }
                    Files.writeString(filePath,response);
                    response.setLength(0);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
            else {
                currInput = tokens.subList(i, nextRedirection);
                filePath = findExec(pathDirs, token);
                if(filePath == null){
                    response.append(token).append(": command not found");
                } else {
                    runExe(currInput.toArray(new String[0]),response);
                }
            }

            i+=nextRedirection-1;
        }
        return response;
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
