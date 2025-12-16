import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        String input = "";
        int idx;
        while (true) {
            System.out.print("$ ");
            input = sc.nextLine();
            if(input.startsWith("echo")){
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
