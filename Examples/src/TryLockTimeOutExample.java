import java.util.concurrent.TimeUnit;

/**
 * Created by nikigoya on 6/23/2017.
 */
public class TryLockTimeOutExample {
    public static void main(String[] args) {


        TryLockTimeOutExample tryLockTimeOutExample = new TryLockTimeOutExample();
        ReentrantLock lock = new ReentrantLock();
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while (true){
                    try {
                        lock.lock();
                        System.out.println("Print by " + Thread.currentThread().getName() + "and i = " + i++);
                        lock.unlock();
                        Thread.sleep(200);
                        if (i == 19) break;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, "thread1");

        Thread thread3 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        if (lock.tryLock(100, TimeUnit.MILLISECONDS))
                        {
                            System.out.println("Got lock by " + Thread.currentThread().getName());
                            lock.unlock();
                            break;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, "thread3");

        thread1.start();
        thread3.start();
    }
}
