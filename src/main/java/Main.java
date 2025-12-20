import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        String input = "", command, allPath, newPath, homePath;
        String[] arguments;
        int i;
        allPath = System.getenv("PATH");
        homePath = System.getenv("HOME");
        String[] dirs = allPath.split(":");
        List<Path> envPaths = new ArrayList<>();
        Path pathObj, filePath, dirPath = null,currPath, tempPath;
        for(i = 0; i < dirs.length; i++){
            pathObj = Paths.get(dirs[i]);
            if(Files.exists(pathObj)){
                envPaths.add(pathObj);
            }
        }

        int idx;
        Set<String> builtin = new HashSet<>();
        builtin.add("echo");
        builtin.add("exit");
        builtin.add("type");
        builtin.add("pwd");
        builtin.add("cd");
        while (true) {
            System.out.print("$ ");
            input = sc.nextLine();
            if(input.startsWith("type")){
                idx = input.indexOf(' ') ;
                command = input.substring(idx+1);
                if(builtin.contains(command)){
                    System.out.println(command +" is a shell builtin");
                } else {
                    filePath = findExec(envPaths, command);
                    if(filePath == null){
                        System.out.println(command+": not found");
                    } else {
                        System.out.println(command+" is "+filePath);
                    }
                }
            } else if(input.startsWith("echo")){
                idx = input.indexOf(' ');
                System.out.println(input.substring(idx+1));
            } else if (input.startsWith("pwd")) {
                System.out.println(System.getProperty("user.dir").toString());
            } else if(input.startsWith("cd")){
                currPath = Paths.get(System.getProperty("user.dir"));
                idx = input.indexOf(' ');
                newPath = input.substring(idx+1);

                if(newPath.charAt(0) == '.'){
                    tempPath = currPath.resolve(newPath);
                } else if(newPath.charAt(0) == '~'){
                    tempPath = Paths.get(homePath);
                } else {
                    tempPath = Paths.get(newPath);
                }

                try {
                    dirPath = tempPath.toRealPath();
                } catch (Exception e) {
                    
                    System.out.println("cd: "+tempPath.toString()+": No such file or directory");
                }

                System.setProperty("user.dir",dirPath.toString());
            }
            else if(input.equals("exit")){
                break;
            } else {
                arguments = input.split(" ");
                filePath = findExec(envPaths, arguments[0]);
                if(filePath == null){
                    System.out.println(input+": command not found");
                } else {
                    runExe(arguments);
                }
            }
        }

        sc.close();
    }

    // function to find executable by file name. Returns null if executable version of the file doesn't exist.
    public static Path findExec(List<Path> envPaths, String fileName){
        Path filePath = null;
        for(Path p : envPaths){
            filePath = p.resolve(fileName);
            if(Files.exists(filePath) && Files.isExecutable(filePath)){
                return filePath;
            }
        }

        return null;
    }

    public static void runExe(String[] arguments)
    {
        try {
            //ProcessBuilder runs in a different process, and is non-blocking. JVM main process doesn't wait for it and keeps. Hence we have to "wait for" this child process to end.
            Process p = new ProcessBuilder(arguments).inheritIO().start();
            p.waitFor();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
