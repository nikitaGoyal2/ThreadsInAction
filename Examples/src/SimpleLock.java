/**
 * Created by nikigoya on 6/22/2017.
 */
public class SimpleLock {

    private boolean isLocked;

    public synchronized void lock() throws InterruptedException {
        System.out.println("Got call for locking " + Thread.currentThread().getName());
        while (isLocked){
            this.wait();
        }

        isLocked = true;
    }

    public synchronized void unlock() throws InterruptedException {
        System.out.println("Got call for unlocking " + Thread.currentThread().getName());
        isLocked = false;
        this.notify();
    }

    public static void main(String[] args) {
        SimpleLock lock = new SimpleLock();



        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    for (int i = 0; i < 10; i++) {
                        lock.lock();
                        System.out.println("Thread2 - " + 2);
                        lock.unlock();
                        Thread.yield();
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
                        lock.lock();
                        System.out.println("Thread1 - " + 1);
                        lock.unlock();
                        Thread.yield();
                    }


                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "Thread1") ;
        Thread thread3 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    for (int i = 0; i < 500; i++) {
                        lock.lock();
                        System.out.println("Thread3 - " + 3);
                        lock.unlock();
                        Thread.yield();
                    }


                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "Thread3") ;
        thread1.start();
        thread2.start();
        thread3.start();
    }

}
