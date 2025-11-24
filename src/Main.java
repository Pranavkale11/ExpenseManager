import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        ExpenseManager manager = new ExpenseManager("data/transactions.csv");
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== PERSONAL EXPENSE MANAGER =====");
            System.out.println("1. Add Income");
            System.out.println("2. Add Expense");
            System.out.println("3. View All Transactions");
            System.out.println("4. Monthly Summary");
            System.out.println("5. Category-wise Summary");
            System.out.println("6. Exit");
            System.out.print("Enter choice (1-6): ");

            int choice;
            try {
                choice = Integer.parseInt(sc.nextLine().trim());
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number between 1 and 6.");
                continue;
            }

            if (choice < 1 || choice > 6) {
                System.out.println("Invalid choice! Enter a number between 1 and 6.");
                continue;
            }

            switch (choice) {

                case 1:
                    addTransactionFlow(sc, manager, Transaction.Type.INCOME);
                    break;

                case 2:
                    addTransactionFlow(sc, manager, Transaction.Type.EXPENSE);
                    break;

                case 3:
                    System.out.println("\n--- All Transactions ---");
                    manager.getAll().forEach(System.out::println);
                    break;

                case 4:
                    YearMonth ym = readYearMonth(sc);
                    ExpenseManager.Summary s = manager.getMonthlySummary(ym);

                    System.out.println("\n--- Monthly Summary for " + ym + " ---");
                    System.out.println("Total Income : " + s.income);
                    System.out.println("Total Expense: " + s.expense);
                    System.out.println("Balance      : " + s.balance);
                    break;

                case 5:
                    YearMonth ym2 = readYearMonth(sc);

                    System.out.println("\n--- Category-wise Summary for " + ym2 + " ---");
                    manager.categorySummary(ym2).forEach((cat, amt) ->
                            System.out.println(cat + " : " + amt));
                    break;

                case 6:
                    System.out.println("Exiting... Goodbye!");
                    sc.close();
                    return;

                default:
                    System.out.println("Invalid choice! Try again.");
            }
        }
    }

    private static void addTransactionFlow(Scanner sc, ExpenseManager manager, Transaction.Type type) {
        System.out.print("Enter date (YYYY-MM-DD) or press Enter for today: ");
        String dateStr = sc.nextLine().trim();
        LocalDate date = dateStr.isEmpty() ? LocalDate.now() : LocalDate.parse(dateStr);

        System.out.print("Enter amount: ");
        double amount = Double.parseDouble(sc.nextLine().trim());

        System.out.print("Enter category: ");
        String category = sc.nextLine().trim();

        System.out.print("Enter note (optional): ");
        String note = sc.nextLine().trim();

        manager.addTransaction(date, type, amount, category, note);

        System.out.println("Transaction saved successfully!");
    }

    // --- Improved readYearMonth: accepts YYYY-MM or YYYY-MM-DD or empty ---
    private static YearMonth readYearMonth(Scanner sc) {
        while (true) {
            System.out.print("Enter year-month (YYYY-MM) or press Enter for current month (you may also enter full date YYYY-MM-DD): ");
            String s = sc.nextLine().trim();
            if (s.isEmpty()) return YearMonth.now();

            // if user provided full date like YYYY-MM-DD, extract first 7 chars
            String candidate = s;
            if (s.length() >= 7 && s.charAt(4) == '-' && s.charAt(7) == '-') {
                // looks like YYYY-MM-DD
                candidate = s.substring(0, 7);
            }

            try {
                return YearMonth.parse(candidate);
            } catch (DateTimeParseException ex) {
                System.out.println("Invalid format. Please enter in YYYY-MM or YYYY-MM-DD format. Try again.");
            }
        }
    }
}
