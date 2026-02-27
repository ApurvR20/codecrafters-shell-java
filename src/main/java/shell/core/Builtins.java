package shell.core;

import shell.CommandResult;
import shell.ExeHandler;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

public class Builtins {

    ShellContext shellContext;

    public Builtins(ShellContext shellContext){
        this.shellContext = shellContext;
    }

    public boolean isBuiltin(String command){

        Set<String> builtin = Set.of("echo","exit","type","pwd","cd");
        return builtin.contains(command);
    }

    public CommandResult runBuiltin(String command, List<String> arguments){

        ExeHandler exeHandler = new ExeHandler();
        Path currPath,dirPath = null;
        StringBuilder res = new StringBuilder();
        String newPathString;
        boolean running = true;

        //type command
        switch (command) {
            case "type" -> {

                for(String arg : arguments) {
                    if (isBuiltin(arg)) {
                        res.append(arg).append(" is a shell builtin");
                    } else {
                        currPath = exeHandler.findExec(shellContext.getPathDirs(), arg);
                        if (currPath == null) {
                            res.append(arg).append(": not found");
                        } else {
                            res.append(arg).append(" is ").append(currPath);
                        }
                    }
                }
            }
            //echo command
            case "echo" -> res = new StringBuilder(String.join(" ", arguments).trim());

            //pwd command
            case "pwd" -> res = new StringBuilder(shellContext.getCWD().toString());

            //cd command
            case "cd" -> {
                currPath = shellContext.getCWD();
                newPathString = arguments.getFirst();

                if (newPathString.charAt(0) == '.') {
                    currPath = currPath.resolve(newPathString);
                } else if (newPathString.charAt(0) == '~') {
                    currPath = shellContext.getHome();
                } else {
                    currPath = Paths.get(newPathString);
                }

                try {
                    dirPath = currPath.toRealPath();
                } catch (Exception e) {
                    System.out.println("cd: " + currPath + ": No such file or directory");
                }

                if (dirPath != null) {
                    shellContext.setCWD(dirPath);
                }
            }
            //exit command
            case "exit" -> {
                res.setLength(0);
                running = false;
            }
        }

        return new CommandResult(running, res);
    }
}
