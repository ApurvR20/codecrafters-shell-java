package shell.cli;

import java.util.Scanner;

public final class Prompt {

    private final Scanner scanner;

    public Prompt() {
        this.scanner = new Scanner(System.in);
    }

    public String readLine() {
        System.out.print("$ ");
        return scanner.nextLine();
    }
}
