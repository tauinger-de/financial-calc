package com.financial;

import com.financial.util.InputReader;
import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.asciitable.CWC_LongestLine;
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment;

import java.util.Scanner;

public class CompoundInterestCalculator {

    public void run(Scanner scanner) {
        System.out.println("\n--- Compound Interest Calculator ---");
        System.out.println("Formula: A = P * (1 + r/n)^(n*t)\n");

        double principal    = InputReader.readPositiveDouble(scanner, "Principal amount ($):          ");
        double annualRate   = InputReader.readPositiveDouble(scanner, "Annual interest rate (%):      ") / 100.0;
        int    compoundsPerYear = InputReader.readPositiveInt(scanner,
                "Compounds per year\n  (1=annually, 4=quarterly, 12=monthly, 365=daily): ");
        int    years        = InputReader.readPositiveInt(scanner,   "Number of years:               ");

        AsciiTable table = new AsciiTable();
        table.getRenderer().setCWC(new CWC_LongestLine());

        table.addRule();
        table.addRow("Year", "Beginning Balance", "Interest Earned", "Ending Balance", "Total Interest")
             .getCells().forEach(c -> c.getContext().setTextAlignment(TextAlignment.CENTER));
        table.addRule();

        double balance = principal;
        double totalInterest = 0;

        for (int year = 1; year <= years; year++) {
            double beginning = balance;
            balance = beginning * Math.pow(1.0 + annualRate / compoundsPerYear, compoundsPerYear);
            double earned = balance - beginning;
            totalInterest += earned;

            table.addRow(
                year,
                fmt(beginning),
                fmt(earned),
                fmt(balance),
                fmt(totalInterest)
            );
            table.addRule();
        }

        System.out.println();
        System.out.println(table.render());
        System.out.printf("  Final amount:          %s%n", fmt(balance));
        System.out.printf("  Total interest earned: %s%n", fmt(totalInterest));
        System.out.printf("  Effective return:      %.2f%%%n", (balance / principal - 1) * 100);
    }

    private static String fmt(double amount) {
        return String.format("$%,.2f", amount);
    }
}
