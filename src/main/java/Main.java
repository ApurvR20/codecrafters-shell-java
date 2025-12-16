import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        String input = "", command;
        int idx;
        Set<String> builtin = new HashSet<>();
        builtin.add("echo");
        builtin.add("exit");
        builtin.add("type");
        while (true) {
            System.out.print("$ ");
            input = sc.nextLine();
            if(input.startsWith("type")){
                idx = input.indexOf(' ') ;
                command = input.substring(idx+1);
                if(builtin.contains(command)){
                    System.out.println(command +" is a shell builtin");
                } else {
                    System.out.println(command +": not found");
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
