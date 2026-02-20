package shell;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import static shell.PathUtils.getCurrDir;

public class Builtins {
//    private
    public static boolean isBuiltin(String command){

        Set<String> builtin = Set.of("echo","exit","type","pwd","cd");
        return builtin.contains(command);
    }

    public static String runBuiltin(String command, List<String> arguments){

        ExeHandler exeHandler = new ExeHandler();
        PathUtils pathUtils = new PathUtils();
        Path currPath,dirPath = null;
        StringBuilder res = new StringBuilder();
        String newPathString;
        //type command
        switch (command) {
            case "type" -> {

                for(String arg : arguments) {
                    if (isBuiltin(arg)) {
                        res.append(arg).append(" is a shell builtin");
                    } else {
                        currPath = exeHandler.findExec(pathUtils.getPathDirs(), arg);
                        if (currPath == null) {
                            res.append(arg).append(": not found");
                        } else {
                            res.append(arg).append(" is ").append(currPath);
                        }
                    }
                }
            }
            //echo command
            case "echo" -> res = new StringBuilder(String.join("", arguments));

            //pwd command
            case "pwd" -> res = new StringBuilder(getCurrDir());

            //cd command
            case "cd" -> {
                currPath = Paths.get(getCurrDir());
                newPathString = arguments.getFirst();

                if (newPathString.charAt(0) == '.') {
                    currPath = currPath.resolve(newPathString);
                } else if (newPathString.charAt(0) == '~') {
                    currPath = Paths.get(System.getenv("HOME"));
                } else {
                    currPath = Paths.get(newPathString);
                }

                try {
                    dirPath = currPath.toRealPath();
                } catch (Exception e) {

                    System.out.println("cd: " + currPath + ": No such file or directory");
                }

                if (dirPath != null) {
                    System.setProperty("user.dir", dirPath.toString());
                }
            }
            //exit command
            case "exit" -> this.stopShell();
        }

        return res.toString();
    }
}
