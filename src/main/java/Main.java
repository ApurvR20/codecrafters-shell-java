import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Main {
    static void main() {
        Scanner sc = new Scanner(System.in);
        String input, command, allPath, newPath, homePath;
        List<String> arguments;
        allPath = System.getenv("PATH");
        homePath = System.getenv("HOME");
        String[] dirs = allPath.split(":");
        List<Path> envPaths = new ArrayList<>();
        Path pathObj, filePath, dirPath = null,currPath, tempPath;
        for (String dir : dirs) {
            pathObj = Paths.get(dir);
            if (Files.exists(pathObj)) {
                envPaths.add(pathObj);
            }
        }

        Set<String> builtin = new HashSet<>();
        builtin.add("echo");
        builtin.add("exit");
        builtin.add("type");
        builtin.add("pwd");
        builtin.add("cd");
        while (true) {
            System.out.print("$ ");
            input = sc.nextLine();
            arguments = tokenizer(input);
            if(arguments.getFirst().equals("type")){
                command = arguments.get(2);
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
            }
            else if(arguments.getFirst().equals("echo")){

                for (int j = 2; j < arguments.size(); j++) {
                    String arg = arguments.get(j);
                    System.out.print(arg);
                }

                System.out.println();
            }
            else if (arguments.getFirst().equals("pwd")) {
                System.out.println(System.getProperty("user.dir"));
            }
            else if(arguments.getFirst().equals("cd")){
                currPath = Paths.get(System.getProperty("user.dir"));
                newPath = arguments.get(2);

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

                    System.out.println("cd: "+tempPath+": No such file or directory");
                }

                if (dirPath != null) {
                    System.setProperty("user.dir",dirPath.toString());
                }
            }
            else if(arguments.getFirst().equals("exit")){
                break;
            }
            else {
                spaceRemover(arguments);
                filePath = findExec(envPaths, arguments.getFirst());
                if(filePath == null){
                    System.out.println(input+": command not found");
                } else {
                    runExe(arguments.toArray(new String[0]));
                }
            }
        }
        sc.close();
    }

    // function to find executable by file name. Returns null if executable version of the file doesn't exist.
    public static Path findExec(List<Path> envPaths, String fileName){
        Path filePath;
        for(Path p : envPaths){
            filePath = p.resolve(fileName);
            if(Files.exists(filePath) && Files.isExecutable(filePath)){
                return filePath;
            }
        }

        return null;
    }

    public static void runExe(String[] arguments) {
        try {
            //ProcessBuilder runs in a different process, and is non-blocking. JVM main process doesn't wait for it and keeps. Hence, we have to "wait for" this child process to end.
            Process p = new ProcessBuilder(arguments).inheritIO().start();
            p.waitFor();
        } catch (Exception e ) {
            e.printStackTrace();

        }
    }

    public static List<String> tokenizer(String input){
        List<String> arguments = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        char open = '\0',ch, next_ch;
        int l = input.length();

        for(int i = 0; i < l;i++){
            ch = input.charAt(i);

            //open can be only 3 values, null, ' (single quote), " (double quote)
            if(open == ch){ //quotes
                if(i < l-1 && ch == input.charAt(i+1)){
                    i++;
                } else {
                    open = '\0';
                    if(!sb.isEmpty()) {
                        arguments.add(sb.toString());
                        sb.setLength(0);
                    }

                }
            }
            else if(open == '\0') {
                //checking if no quotes
                //checking whitespace
                if(ch == ' '){

                    if(!sb.isEmpty()){
                        arguments.add(sb.toString());
                        sb.setLength(0);
                    }

                    if(!arguments.isEmpty() && !arguments.getLast().equals(" ")){
                        arguments.add(" ");
                    }
                }
                else if (ch == '\\') { //checking for escape sequence
                    sb.append(input.charAt(i+1));
                    i++;
                }
                else if(ch == '\"' || ch == '\''){

                    //since opening quotes, hence next_ch definitely exists
                    next_ch = input.charAt(i+1);
                    if(ch == next_ch){
                        i++;
                    } else {
                        open = ch; //setting opening quotes
                        if (!sb.isEmpty()) {
                            arguments.add(sb.toString());
                            sb.setLength(0);
                        }
                    }
                }
                else {
                    sb.append(ch);
                }
            }
            else if(open == '\"'){ //double quotes open
                if(ch == '\\'){

                    next_ch = input.charAt(i+1); //cant be last char, since quotes are open

                    if(next_ch == '\"' || next_ch == '\\'){
                        sb.append(next_ch);
                        i++;
                    } else {
                        sb.append(ch);
                    }
                } else {
                    sb.append(ch);
                }
            }
            else {
                //single quotes accept everything literally
                sb.append(ch);
            }
        }

        if(!sb.isEmpty()){
            arguments.add(sb.toString());
        }
        return arguments;
    }

    public static void spaceRemover(List<String> arguments){

        //remove spaces from arguments list
        String s;
        for(int i = 0; i < arguments.size(); i++){
            s = arguments.get(i);
            if(s.equals(" ")){
                arguments.remove(i);
                i--;
            }
        }
    }

}
