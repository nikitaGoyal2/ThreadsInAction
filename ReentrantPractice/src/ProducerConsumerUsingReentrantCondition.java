import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by nikigoya on 6/23/2017.
 */
public class ProducerConsumerUsingReentrantCondition {

    private ReentrantLock lock;

    private List<Integer> queue;

    private Condition isNotFull;

    private Condition isNotEmpty;

    private int upperlimit;
    public ProducerConsumerUsingReentrantCondition(ReentrantLock lock, Condition isFull, Condition isEmpty, int upperlimit) {
        this.lock = lock;
        this.isNotFull = isFull;
        this.isNotEmpty = isEmpty;
        queue = new LinkedList<>();
        this.upperlimit = upperlimit;

    }

    public void producer(int num) throws InterruptedException {

        lock.lock();
        while (queue.size() == upperlimit)
            isNotFull.await();
        System.out.println(Thread.currentThread().getName() + "=> Produced = " + num);
        queue.add(num);
        isNotEmpty.signalAll();
        lock.unlock();


    }

    public void consumer() throws InterruptedException {

        lock.lock();
        while (queue.size() == 0)
            isNotEmpty.await();
        System.out.println("Consumed = " + queue.remove(0));
        isNotFull.signalAll();
        lock.unlock();
    }

    public static void main(String[] args) {

        ReentrantLock lock = new ReentrantLock();

        ProducerConsumerUsingReentrantCondition producerConsumer = new
                ProducerConsumerUsingReentrantCondition(lock, lock.newCondition(), lock.newCondition(), 30);

        Thread producer = new Thread( new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10 ; i++) {
                    try {
                        producerConsumer.producer(i);
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }, "Producer");

        Thread producer1 = new Thread( new Runnable() {
            @Override
            public void run() {
                for (int i = 10; i < 20 ; i++) {
                    try {
                        producerConsumer.producer(i);
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }, "Producer1");


        Thread consumer = new Thread( new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 20 ; i++) {
                    try {
                        producerConsumer.consumer();
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }, "Producer");

        producer.start();
        producer1.start();
        consumer.start();
    }
}
