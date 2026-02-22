package com.financial;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("╔══════════════════════════════╗");
        System.out.println("║   Financial Calculator CLI   ║");
        System.out.println("╚══════════════════════════════╝");

        while (true) {
            printMenu();
            System.out.print("Select: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> new CompoundInterestCalculator().run(scanner);
                case "2" -> new LoanPayoffCalculator().run(scanner);
                case "q", "Q", "3" -> {
                    System.out.println("\nGoodbye!");
                    return;
                }
                default -> System.out.println("  Unknown option. Enter 1, 2, or q.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n  1. Compound Interest");
        System.out.println("  2. Loan Payoff");
        System.out.println("  q. Quit");
    }
}
