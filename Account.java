public class Account {
    Long id;
    Integer balance;
    String status;

    public void addBalance(int amount) {
        balance = balance + amount;
    }

    public void reduceBalance(int amount) {
        balance = balance - amount;
    }

}
