package com.github.bwca.message;

import com.github.tomaslanger.chalk.Chalk;

import java.util.Scanner;

public class Messenger {

    private final Scanner scanner = new Scanner(System.in);

    public void info(String message) {
        printLine(Chalk.on(message));
    }

    public void error(String message) {
        printLine(Chalk.on(message).bold().red());
    }

    public void warn(String message) {
        printLine(Chalk.on(message).yellow());
    }

    public void success(String message) {
        printLine(Chalk.on(message).green());
    }

    public String prompt(String message) {
        if (message != null) {
            printLine(Chalk.on(message).blue());
        }
        return scanner.nextLine();
    }


    private void printLine(Chalk line) {
        System.out.println(line);
    }
}
