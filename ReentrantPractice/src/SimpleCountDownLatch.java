import java.util.concurrent.Semaphore;

/**
 * Created by nikigoya on 6/25/2017.
 */
public class SimpleCountDownLatch {

    private int count;

    public SimpleCountDownLatch(int upperlimit) {
        this.count = upperlimit;
    }

    public  synchronized void countDown() throws InterruptedException {
        count--;
        if (count == 0){
            this.notify();
        }
    }

    public synchronized void await() throws InterruptedException {
        while (count != 0){
            this.wait();
        }
    }

    public static void main(String[] args) {
        SimpleCountDownLatch countDownLatch = new SimpleCountDownLatch(2);

        Thread waiter = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + " -> Entered!");
                System.out.println(Thread.currentThread().getName() + " waiting for latch");
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + " count down done!");
            }
        } ," waiter") ;

        Thread services = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + " -> Entered!");
                System.out.println(Thread.currentThread().getName() + " completed 1 service");
                try {
                    countDownLatch.countDown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + " completed 2 service");
                try {
                    countDownLatch.countDown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + " count down done!");
            }
        } , "services thread") ;

        waiter.start();
        services.start();
    }
}
