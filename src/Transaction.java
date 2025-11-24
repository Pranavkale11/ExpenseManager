import java.time.LocalDate;

public class Transaction {

    public enum Type {
        INCOME, EXPENSE
    }

    private int id;
    private LocalDate date;
    private Type type;
    private double amount;
    private String category;
    private String note;

    public Transaction(int id, LocalDate date, Type type, double amount, String category, String note) {
        this.id = id;
        this.date = date;
        this.type = type;
        this.amount = amount;
        this.category = category;
        this.note = note;
    }

    public int getId() { return id; }
    public LocalDate getDate() { return date; }
    public Type getType() { return type; }
    public double getAmount() { return amount; }
    public String getCategory() { return category; }
    public String getNote() { return note; }

    public String toCSV() {
        return id + "," +
                date + "," +
                type + "," +
                amount + "," +
                safe(category) + "," +
                safe(note);
    }

    private String safe(String text) {
        if (text == null) return "";
        return text.replace(",", ";");
    }

    @Override
    public String toString() {
        return "[" + id + "] " + date + " " + type + " " + amount +
                " - " + category + " (" + note + ")";
    }
}
