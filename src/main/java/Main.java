/*

input is always in two parts, command and arguments[optional], separated by a space

do I store each step in a queue-like (or stack-like?) structure
 do I need to write a processor separately

argument shouldn't contain the starting command. why??

need to convert argument to real path, unless it is already a real path

clean up main and split code properly

*/

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Main {

    private static final List<Path> pathDirs = new ArrayList<>();
    private static boolean running = true;

    private static void stopShell(){
        running = false;
    }

    private static String getHomePath(){
        return System.getenv("HOME");
    }

    private static void setPathDirs(){
        Path pathObj;
        for(String dir : System.getenv("PATH").split(":")){
            pathObj = Paths.get(dir);
            if(Files.exists(pathObj)){
                pathDirs.add(pathObj);
            }
        }
    }

    public static void main() {
        Scanner sc = new Scanner(System.in);
        String input;
        StringBuilder output = new StringBuilder();
        List<String> tokens;
        setPathDirs();

        while (running) {
            System.out.print("$ ");
            input = sc.nextLine();
            tokens = tokenizer(input);
            output.append(responseBuilder(tokens));
            if(!output.isEmpty()) {
                System.out.println(output);
            }
            output.setLength(0);
        }

        if(!output.isEmpty()){
            System.out.println(output);
        }
    }

    // function to find executable by file name. Returns null if executable version of the file doesn't exist.

    public static Path findExec(List<Path> pathDirs, String fileName){
        Path filePath;
        for(Path p : pathDirs){
            filePath = p.resolve(fileName);
            if(Files.exists(filePath) && Files.isExecutable(filePath)){
                return filePath;
            }
        }

        return null;
    }

    public static void runExe(String[] arguments, StringBuilder sb) {
        try {
            Process p = new ProcessBuilder(arguments).start();

            try (BufferedReader br =
                         new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
            }


            p.waitFor();
        } catch (Exception e) {
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
                    addBufToTokens(arguments,sb);
                    if(!arguments.isEmpty() && !arguments.getLast().equals(" ") && !arguments.getLast().equals(">")){
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
                        addBufToTokens(arguments,sb);
                    }
                }
                else if(ch == '>'){
                    addBufToTokens(arguments,sb);
                    sb.append(ch);
                    addBufToTokens(arguments,sb);
                } else if(ch == '1' && i < l-1 && input.charAt(i+1) == '>'){
                    i++;
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
                }
                else {
                    sb.append(ch);
                }
            }
            else {
                //single quotes accept everything literally
                sb.append(ch);
            }
        }

        //if something remains in buffer
        addBufToTokens(arguments,sb);
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

    public static boolean isBuiltin(String command){

        Set<String> builtin = Set.of("echo","exit","type","pwd","cd");
        return builtin.contains(command);
    }

    public static String runBuiltin(String command, List<String> arguments){

//        System.out.println("first time : "+command);
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
                        currPath = findExec(getValidDirs(), arg);
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
            case "exit" -> stopShell();
        }

        return res.toString();
    }

    // function to return all valid Paths that exist within the PATH variable
    public static List<Path> getValidDirs (){

        String allPaths = System.getenv("PATH");
        String[] dirs = allPaths.split(":");
        Path pathObj;
        List<Path> envPaths = new ArrayList<>();
        for (String dir : dirs) {
            pathObj = Paths.get(dir);
            if (Files.exists(pathObj)) {
                envPaths.add(pathObj);
            }
        }
        return envPaths;
    }

    //adds buffer to args and reset it
    public static void addBufToTokens(List<String> arguments,StringBuilder sb){
        if(!sb.isEmpty()) {
            arguments.add(sb.toString());
            sb.setLength(0);
        }
    }

    public static StringBuilder responseBuilder(List<String> tokens){

        StringBuilder response = new StringBuilder();

        String token, nextToken;
        int nextRedirection;
        List<String> currInput;
        Path filePath;
        for(int i = 0;i < tokens.size(); i++){
            token = tokens.get(i);
            if(token.equals(" ")){
                continue;
            }

            nextRedirection = getNextRedirection(tokens,i);
            //run builtin
            if(isBuiltin(token)){

                if(tokens.size() > 2) {
                    response.append(runBuiltin(token, tokens.subList(i + 2, nextRedirection)));
                } else {
                    response.append(runBuiltin(token,new ArrayList<>()));
                }
            }
            else if(token.equals(">")) {
                nextToken = tokens.get(i+1);
                if(nextToken.contains("/")){
                    // this is already a path
                    filePath = Paths.get(nextToken);
                } else {
                    filePath = Paths.get(getHomePath()).resolve(nextToken);
                }

                filePath = filePath.toAbsolutePath();
                try {
                    Files.createFile(filePath);
                    Files.writeString(filePath,response);
                    response.setLength(0);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
            else {
                spaceRemover(tokens.subList(i, nextRedirection));
                nextRedirection = getNextRedirection(tokens,i);
                currInput = tokens.subList(i, nextRedirection);
                filePath = findExec(pathDirs, token);
                if(filePath == null){
                    response.append(token).append(": command not found");
                } else {
                    runExe(currInput.toArray(new String[0]),response);
                }
            }

            i+=nextRedirection;
        }

        return response;
    }

    public static int getNextRedirection(List<String> tokens, int i){

        int j = i+1;
        while (j < tokens.size() && !tokens.get(j).equals(">")){
            j++;
        }

        return j;
    }

    public static String getCurrDir(){

        return System.getenv("user.dir");
    }
}
