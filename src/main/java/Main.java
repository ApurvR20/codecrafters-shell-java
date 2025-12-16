import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        String input = "";
        while (true) {
            System.out.print("$ ");
            input = sc.nextLine();
            if(input.equals("exit")){
                return;
            }
            System.out.println(input+": command not found");    
        }

        // sc.close();
    }
}
