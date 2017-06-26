import java.util.concurrent.TimeUnit;

/**
 * Created by nikigoya on 6/22/2017.
 */
public class ReentrantLock {

    private boolean isLocked;
    private Thread currentThread;
    private int currentLockedTimes;

    public synchronized void lock() throws InterruptedException {
        //System.out.println("Got call for locking " + Thread.currentThread().getName());
        Thread thread = Thread.currentThread();
        while (isLocked && !thread.equals(currentThread)) {
            this.wait();
        }
        currentLockedTimes++;
        isLocked = true;
        currentThread = thread;
        notifyAll();
    }

    public synchronized boolean tryLock() throws InterruptedException {
        //System.out.println("Got call for locking " + Thread.currentThread().getName());
        Thread thread = Thread.currentThread();
        if (!isLocked || currentThread == null || currentThread.equals(thread)){
            currentLockedTimes++;
            isLocked = true;
            currentThread = thread;
            notifyAll();
            return true;
        }
        return false;
    }

    public synchronized boolean tryLock(int timeout, TimeUnit timeUnit) throws InterruptedException {
        //System.out.println("Got call for locking " + Thread.currentThread().getName());
        long expiryTime = (System.currentTimeMillis() + timeUnit.toMillis(timeout));
        while (expiryTime > 0 ) {
            Thread thread = Thread.currentThread();
            if (!isLocked || currentThread == null || currentThread.equals(thread)){
                currentLockedTimes++;
                isLocked = true;
                currentThread = thread;
                notifyAll();
                return true;
            }
            expiryTime =  (System.currentTimeMillis() - expiryTime);
        }
        return false;
    }

    public synchronized void unlock() throws InterruptedException {

        if (Thread.currentThread().equals(currentThread)) {

            currentLockedTimes--;
            if (currentLockedTimes == 0) {
                System.out.println("Got call for unlocking " + Thread.currentThread().getName());
                isLocked = false;
                currentThread = null;
                this.notify();
            }
        }

    }


    public static void main(String[] args) {
        ReentrantLock lock = new ReentrantLock();
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    for (int i = 0; i < 10; i++) {
                        (new Sample(lock)).outerMethod();
                        Thread.sleep(500);
                    }



                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "Thread2" );
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    for (int i = 0; i < 50; i++) {
                        (new Sample(lock)).outerMethod();
                        Thread.sleep(200);
                    }


                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "Thread1") ;

        thread1.start();
        thread2.start();
    }
}

class Sample{
    private ReentrantLock lock;

    public Sample(ReentrantLock lock) {
        this.lock = lock;
    }

    public void outerMethod() throws InterruptedException {
        lock.lock();
        System.out.println(Thread.currentThread().getName() + " - Inside outer method!");
        innerMethod();
        lock.unlock();
    }

    private void innerMethod() throws InterruptedException {
        lock.lock();
        System.out.println(Thread.currentThread().getName() + " - Inside inner method!");
        lock.unlock();
    }
}