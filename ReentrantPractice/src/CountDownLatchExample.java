import java.util.concurrent.CountDownLatch;

/**
 * Created by nikigoya on 6/25/2017.
 */
public class CountDownLatchExample {

    public static void main(String[] args) {
        CountDownLatch countDownLatch = new CountDownLatch(2);

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
                countDownLatch.countDown();
                System.out.println(Thread.currentThread().getName() + " completed 2 service");
                countDownLatch.countDown();
                System.out.println(Thread.currentThread().getName() + " count down done!");
            }
        } , "services thread") ;

        waiter.start();
        services.start();
    }
}
