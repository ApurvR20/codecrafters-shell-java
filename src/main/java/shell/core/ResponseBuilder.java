package shell.core;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import shell.ExeHandler;
import static shell.Builtins.isBuiltin;
import static shell.Builtins.runBuiltin;
import shell.PathUtils;

public final class ResponseBuilder {

    public static String handle(String input) {
        StringBuilder response = new StringBuilder();
        Tokenizer obj = new Tokenizer();
        PathUtils pathObj = new PathUtils();
        List<String> tokens = obj.tokenizer(input);
        String token, nextToken;
        int nextRedirection;
        List<String> currInput;
        Path filePath, parentFilePath;
        for(int i = 0;i < tokens.size(); i++){
            token = tokens.get(i);
            nextRedirection = getNextRedirection(tokens,i);

            if(isBuiltin(token)){
                if(tokens.size() > 1) {
                    response.append(runBuiltin(token, tokens.subList(i + 1,nextRedirection)));
                } else {
                    response.append(runBuiltin(token,new ArrayList<>()));
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


}
