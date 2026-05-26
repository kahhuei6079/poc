public class Account {
    Long id;
    Integer balance;   // nullable — NPE risk in add/reduce
    String status;     // no enum; "CLOSED" is a magic string

    public void addBalance(int amount) {
        balance = balance + amount;
    }

    public void reduceBalance(int amount) {
        balance = balance - amount;   // no overdraft guard
    }
    // no version / updatedAt — optimistic locking impossible
}
