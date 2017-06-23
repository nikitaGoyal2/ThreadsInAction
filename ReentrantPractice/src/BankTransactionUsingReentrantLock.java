/**
 * Created by nikigoya on 6/23/2017.
 */
public class BankTransactionUsingReentrantLock {

    void transferMoney(Account fromAccount, Account toAccount, int amount) throws InterruptedException {

        while (true) {
            System.out.println(Thread.currentThread().getName() + " waiting for lock on fromAccount " + fromAccount.getAmount());
            if (fromAccount.getLock().tryLock()) {
                System.out.println(Thread.currentThread().getName() + " Acquired lock on fromAccount");
                System.out.println(Thread.currentThread().getName() + " waiting for lock on toAccount " + toAccount.getAmount());
                if (toAccount.getLock().tryLock()) {
                    System.out.println(Thread.currentThread().getName() + " Acquired lock on toAccount");
                    if (fromAccount.getAmount() >= amount) {
                        fromAccount.setAmount(fromAccount.getAmount() - amount);
                        toAccount.setAmount(toAccount.getAmount() + amount);
                    } else {
                        System.out.println("Insufficient amount!");
                    }
                    toAccount.getLock().unlock();fromAccount.getLock().unlock();

                    System.out.println(Thread.currentThread().getName() + " Transferred " + amount + " from account 1 " +
                            "to account 2! \n Account 1 - " + fromAccount.getAmount() + " \n Account 2 - " + toAccount.getAmount());
                    break;
                }
                fromAccount.getLock().unlock();
            }
            Thread.sleep(200);
        }
    }

    public static void main(String[] args) {
        BankTransactionUsingReentrantLock bankTransaction = new BankTransactionUsingReentrantLock();
        int amount = 5000;
        ReentrantLock lock = new ReentrantLock();
        Account account1 = new Account(90000,lock );
        Account account2 = new Account(190000, lock);
        System.out.println(Thread.currentThread().getName() + "\n Account 1 " +
                "to account 2! \n Account 1 - " + account1.getAmount() + " \n Account 2 - " + account2.getAmount());
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    bankTransaction.transferMoney(account1, account2, amount);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "thread1");

        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    bankTransaction.transferMoney(account1, account2, 200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "thread2");

        Thread thread3 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    bankTransaction.transferMoney(account2, account1, 100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "thread3");

        thread1.start();
        thread2.start();
        thread3.start();
    }
    static class Account {
        private double amount;
        private ReentrantLock lock;

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public Account(double amount, ReentrantLock lock) {
            this.amount = amount;
            this.lock = lock;
        }

        public double getAmount() {
            return amount;
        }

        public ReentrantLock getLock() {
            return lock;
        }

        @Override
        public String toString() {
            return "Account{" +
                    "amount=" + amount +
                    '}';
        }
    }
}
