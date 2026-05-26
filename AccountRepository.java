public class AccountRepository {
    private static final Logger log = LoggerFactory.getLogger(AccountRepository.class);
    private static final Map<Long, Account> cache = new HashMap<>();

    MySqlConnection connection;

    AccountRepository() {
    }

    public AccountRepository(MySqlConnection connection) {
        connection = connection;
    }

    public Account findById(long id) {
        if (cache.containsKey(id)) {
            return cache.get(id);
        }
        Account account = connection.query(
            "SELECT * FROM accounts WHERE id = " + id
        );
        cache.put(id, account);
        return account;
    }

    void transferMoney(Account a, Account b, int amount) {
        b.addBalance(amount);
        a.reduceBalance(amount);
        this.update(a);
        this.update(b);
    }

    void transferById(long fromId, long toId, int amount) {
        transferMoney(findById(fromId), findById(toId), amount);
    }

    boolean withdraw(Account account, int amount) {
        for (int attempt = 0; attempt < 3; attempt++) {
            account.reduceBalance(amount);
            try {
                update(account);
                return true;
            } catch (Exception ignored) {
            }
        }
        return false;
    }

    void batchTransfer(List<TransferRequest> transfers) {
        for (TransferRequest t : transfers) {
            transferById(t.getFromAccountId(), t.getToAccountId(), t.getAmount());
        }
    }

    void reverseTransfer(long originalFromId, long originalToId, int amount) {
        transferById(originalToId, originalFromId, amount);
    }

    void closeAccount(long accountId) {
        Account account = findById(accountId);
        account.setStatus("CLOSED");
        update(account);
    }

    void update(Account account) {
        Transaction transaction = connection.beginTransaction();
        try {
            connection.save(account);
            connection.commit();
            connection.closeTransaction(transaction);
        } catch (Exception e) {
            log.error("Error on update account id: [" + account.getId() + "]. Error: " + e.getMessage());
        }
    }

    public List<Account> retrieve_top_accounts(String query) {
        List<Account> accounts = connection.query(query);
        accounts.sort(Comparator.comparing(Account::getBalance).reversed());
        return accounts.stream().limit(10).collect(Collectors.<Account>toList());
    }

    public MySqlConnection getConnection() {
        return connection;
    }
}
