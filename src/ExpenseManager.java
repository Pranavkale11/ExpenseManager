import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

public class ExpenseManager {

    private List<Transaction> transactions = new ArrayList<>();
    private int nextId = 1;
    private Path dataFile;

    public ExpenseManager(String filePath) {
        this.dataFile = Paths.get(filePath);
        loadFromFile();
    }

    private void loadFromFile() {
        try {
            if (!Files.exists(dataFile)) {
                Files.createDirectories(dataFile.getParent());
                Files.createFile(dataFile);
                Files.writeString(dataFile, "id,date,type,amount,category,note\n");
                return;
            }

            List<String> lines = Files.readAllLines(dataFile);
            for (int i = 1; i < lines.size(); i++) { // skip header
                String line = lines.get(i).trim();
                if (line.isEmpty()) continue;

                String[] p = line.split(",", 6);

                int id = Integer.parseInt(p[0]);
                LocalDate date = LocalDate.parse(p[1]);
                Transaction.Type type = Transaction.Type.valueOf(p[2]);
                double amount = Double.parseDouble(p[3]);
                String category = p[4].replace(";", ",");
                String note = p[5].replace(";", ",");

                transactions.add(new Transaction(id, date, type, amount, category, note));
                nextId = Math.max(nextId, id + 1);
            }

        } catch (Exception e) {
            System.out.println("Error loading file: " + e.getMessage());
        }
    }

    private void saveToFile() {
        try (BufferedWriter bw = Files.newBufferedWriter(dataFile)) {
            bw.write("id,date,type,amount,category,note\n");
            for (Transaction t : transactions) {
                bw.write(t.toCSV());
                bw.newLine();
            }
        } catch (Exception e) {
            System.out.println("Saving error: " + e.getMessage());
        }
    }

    public void addTransaction(LocalDate date, Transaction.Type type, double amount, String category, String note) {
        Transaction t = new Transaction(nextId++, date, type, amount, category, note);
        transactions.add(t);
        saveToFile();
    }

    public List<Transaction> getAll() {
        return Collections.unmodifiableList(transactions);
    }

    public List<Transaction> getByMonth(YearMonth ym) {
        return transactions.stream()
                .filter(t -> YearMonth.from(t.getDate()).equals(ym))
                .sorted(Comparator.comparing(Transaction::getDate))
                .collect(Collectors.toList());
    }

    public Summary getMonthlySummary(YearMonth ym) {
        double inc = 0, exp = 0;

        for (Transaction t : getByMonth(ym)) {
            if (t.getType() == Transaction.Type.INCOME) inc += t.getAmount();
            else exp += t.getAmount();
        }

        return new Summary(inc, exp, inc - exp);
    }

    public Map<String, Double> categorySummary(YearMonth ym) {
        Map<String, Double> map = new HashMap<>();

        for (Transaction t : getByMonth(ym)) {
            map.putIfAbsent(t.getCategory(), 0.0);

            if (t.getType() == Transaction.Type.EXPENSE)
                map.put(t.getCategory(), map.get(t.getCategory()) + t.getAmount());
            else
                map.put(t.getCategory(), map.get(t.getCategory()) - t.getAmount());
        }

        return map;
    }

    public static class Summary {
        public final double income, expense, balance;

        public Summary(double income, double expense, double balance) {
            this.income = income;
            this.expense = expense;
            this.balance = balance;
        }
    }
}
