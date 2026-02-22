package com.financial;

import com.financial.util.InputReader;
import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.asciitable.CWC_LongestLine;
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment;

import java.util.Scanner;

public class LoanPayoffCalculator {

    public void run(Scanner scanner) {
        System.out.println("\n--- Loan Payoff Calculator ---");
        System.out.println("Formula: M = P * [r(1+r)^n] / [(1+r)^n - 1]\n");

        double principal = InputReader.readPositiveDouble(scanner, "Loan amount ($):          ");
        double annualRate = InputReader.readPositiveDouble(scanner, "Annual interest rate (%): ") / 100.0;
        int    termMonths = InputReader.readPositiveInt(scanner,   "Loan term (months):       ");

        double monthlyRate = annualRate / 12.0;
        double monthlyPayment = computeMonthlyPayment(principal, monthlyRate, termMonths);

        System.out.printf("%nMonthly Payment: $%,.2f%n", monthlyPayment);
        System.out.printf("Total payments:  %d%n%n", termMonths);

        boolean showFull = askShowFull(scanner, termMonths);

        if (showFull) {
            printFullSchedule(principal, monthlyRate, monthlyPayment, termMonths);
        } else {
            printYearlySummary(principal, monthlyRate, monthlyPayment, termMonths);
        }
    }

    private static double computeMonthlyPayment(double principal, double monthlyRate, int termMonths) {
        if (monthlyRate == 0) return principal / termMonths;
        double factor = Math.pow(1 + monthlyRate, termMonths);
        return principal * (monthlyRate * factor) / (factor - 1);
    }

    private static boolean askShowFull(Scanner scanner, int termMonths) {
        if (termMonths <= 36) return true;
        System.out.printf("Schedule has %d monthly rows. Show (F)ull or (Y)early summary? [F/Y]: ", termMonths);
        String choice = scanner.nextLine().trim().toUpperCase();
        return !choice.equals("Y");
    }

    private static void printFullSchedule(double principal, double monthlyRate,
                                          double monthlyPayment, int termMonths) {
        AsciiTable table = new AsciiTable();
        table.getRenderer().setCWC(new CWC_LongestLine());

        table.addRule();
        table.addRow("Month", "Payment", "Principal", "Interest", "Balance")
             .getCells().forEach(c -> c.getContext().setTextAlignment(TextAlignment.CENTER));
        table.addRule();

        double balance = principal;
        double totalInterest = 0;
        double totalPaid = 0;

        for (int month = 1; month <= termMonths; month++) {
            double interest = balance * monthlyRate;
            double principalPart = Math.min(monthlyPayment - interest, balance);
            balance = Math.max(balance - principalPart, 0);
            totalInterest += interest;
            totalPaid += principalPart + interest;

            table.addRow(month, fmt(monthlyPayment), fmt(principalPart), fmt(interest), fmt(balance));
            table.addRule();
        }

        System.out.println(table.render());
        printSummary(totalPaid, totalInterest, principal);
    }

    private static void printYearlySummary(double principal, double monthlyRate,
                                           double monthlyPayment, int termMonths) {
        AsciiTable table = new AsciiTable();
        table.getRenderer().setCWC(new CWC_LongestLine());

        table.addRule();
        table.addRow("Year", "Payments", "Principal Paid", "Interest Paid", "Remaining Balance")
             .getCells().forEach(c -> c.getContext().setTextAlignment(TextAlignment.CENTER));
        table.addRule();

        double balance = principal;
        double totalInterest = 0;
        double totalPaid = 0;

        int years = (int) Math.ceil(termMonths / 12.0);

        for (int year = 1; year <= years; year++) {
            int startMonth = (year - 1) * 12 + 1;
            int endMonth = Math.min(year * 12, termMonths);

            double yearPrincipal = 0;
            double yearInterest = 0;

            for (int m = startMonth; m <= endMonth; m++) {
                double interest = balance * monthlyRate;
                double principalPart = Math.min(monthlyPayment - interest, balance);
                balance = Math.max(balance - principalPart, 0);
                yearPrincipal += principalPart;
                yearInterest += interest;
            }

            totalInterest += yearInterest;
            totalPaid += yearPrincipal + yearInterest;

            table.addRow(year, endMonth - startMonth + 1,
                    fmt(yearPrincipal), fmt(yearInterest), fmt(balance));
            table.addRule();
        }

        System.out.println(table.render());
        printSummary(totalPaid, totalInterest, principal);
    }

    private static void printSummary(double totalPaid, double totalInterest, double principal) {
        System.out.printf("  Total paid:           %s%n", fmt(totalPaid));
        System.out.printf("  Total interest paid:  %s%n", fmt(totalInterest));
        System.out.printf("  Interest/Principal:   %.1f%%%n", totalInterest / principal * 100);
    }

    private static String fmt(double amount) {
        return String.format("$%,.2f", amount);
    }
}
