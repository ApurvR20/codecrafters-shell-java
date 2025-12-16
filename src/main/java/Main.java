import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();
        System.out.print("$ "+input+"\n"+input+": command not found");
        sc.close();
    }
}
