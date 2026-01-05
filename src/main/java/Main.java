//probably need to escape the spaces
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
            arguments = parser(input);
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
//                idx = input.indexOf(' ');
//                arguments = someParser(input.substring(idx+1));
//                System.out.println(arguments);

                for(int j = 1; j < arguments.length; j++){
                    System.out.print(arguments[i]);
                }
                System.out.println();

//                System.out.println(i);
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
            } else if(input.equals("exit")){
                break;
            } else {
                arguments = input.split(" ");
                System.out.println(arguments.toString());
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
        } catch (Exception e ) {
            System.out.println(e);

        }
    }


    public static String singleQuoteParser(String input){

        boolean quoteStatus = false;
        StringBuilder sb = new StringBuilder();
        StringBuilder tempSt = new StringBuilder();
        char prev = input.charAt(0);
        if(prev == '\''){
            quoteStatus = true;
        } else {
            sb.append(prev);

        }

        char ch;
        for(int i = 1; i < input.length(); i++){

            ch = input.charAt(i);
            if(quoteStatus){
                if(ch == '\''){
                    quoteStatus = false;
                    sb.append(tempSt);
                    tempSt.setLength(0);;
                } else {
                    tempSt.append(ch);
                }
            } else if(ch == '\''){
                quoteStatus = true;
            } else if(Character.isWhitespace(prev) && Character.isWhitespace(ch)){
            }
            else {
                sb.append(ch);
            }

            prev = ch;
        }

        sb.append(tempSt);
        return sb.toString();
    }

    public static String[] someParser(String input){

        List<String> inputList = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        char ch, open ='\0';
        int l = input.length();
        for(int i = 0; i < l; i++){
            ch = input.charAt(i);

            if(open == '\0' && ch == ' ' && !sb.isEmpty() && sb.charAt(sb.length()-1) == ' '){
                continue;
            }
            sb.append(ch);

            if(ch == '\'' || ch == '\"'){
                if(open == '\0'){
                    open = ch;
                } else if(open == ch){

                    //handling same consecutive quotes
                    if(i < l-1 && input.charAt(i+1) == ch){
                        sb.deleteCharAt(sb.length()-1);
                        i++;
                        continue;
                    }

                    open = '\0';
                    inputList.add(sb.toString());
                    sb.setLength(0);
                }
            }
        }



        return inputList.toArray(new String[0]);
    }

    public static List<String> splitter(String input){
        List<String> arguments = new ArrayList<>();
        StringBuffer sb = new StringBuffer();
        char open = '\0',ch;
        int l = input.length();
        for(int i = 0; i < l;i++){
            ch = input.charAt(i);
            if(Character.isAlphabetic(ch) || Character.isWhitespace(ch)){
                sb.append(ch);
            } else if(ch == '\"' || ch == '\'') {

                if(open == '\0'){
                    //this line handles consec same quotes while opening
                    if(i < l-1 && ch == input.charAt(i+1)){
                        i++;
                    } else {
                        arguments.add(sb.toString());
                        sb.setLength(0);
                        open = ch;
                    }
                } else if(open == ch){
                    //this line handles consec same quotes while closing
                    if(i < l-1 && ch == input.charAt(i+1)){
                        i++;
                    } else {
                        arguments.add(sb.toString());
                        sb.setLength(0);
                        open = '\0';
                    }
                } else {
                    sb.append(ch);
                }
            }

        }

        return arguments;
    }

    public static List<String> spaceHandler(List<String> arguments){

        String s;
        for(int i = 0; i < arguments.size(); i++){
            s = arguments.get(i);
            if(s.isBlank()){
                arguments.set(i, " ");
            } else {
                arguments.set(i, s.trim());
            }
        }

        return arguments;
    }

    public static String[] parser(String input){

        return spaceHandler(splitter(input)).toArray(new String[0]);
    }
}
