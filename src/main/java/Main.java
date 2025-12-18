import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
/*
- extract path
- split path into all folders
- extract all folders
- recursively search in all folders
- if found, display the path
*/
public class Main {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        String input = "", command, path;
        int i;
        path = System.getenv("PATH");
        String[] dirs = path.split(":");
        List<Path> envPaths = new ArrayList<>();
        Path pathObj, filePath;
        for(i = 0; i < dirs.length; i++){
            pathObj = Paths.get(dirs[i]);
            if(Files.exists(pathObj)){
                envPaths.add(pathObj);
            }
        }

        int idx;
        boolean exeFound;
        Set<String> builtin = new HashSet<>();
        builtin.add("echo");
        builtin.add("exit");
        builtin.add("type");
        while (true) {
            System.out.print("$ ");
            input = sc.nextLine();
            if(input.startsWith("type")){

                exeFound = false;
                idx = input.indexOf(' ') ;
                command = input.substring(idx+1);

                if(builtin.contains(command)){
                    System.out.println(command +" is a shell builtin");
                } else {
                    for(Path p : envPaths){
                        filePath = p.resolve(command);
                        if(Files.exists(filePath) && Files.isExecutable(filePath)){
                            exeFound = true;
                            System.out.println(command + " is "+filePath);
                            break;
                        }
                    }

                    if(!exeFound){
                        System.out.println(command+": not found");
                    }
                }
            } else if(input.startsWith("echo")){
                idx = input.indexOf(' ');
                System.out.println(input.substring(idx+1));
            } else if(input.equals("exit")){
                break;
            } else {
                System.out.println(input+": command not found");
            }
        }

        sc.close();
    }
}
