import java.util.ArrayList;
import java.util.List;

/**
 * Created by nikigoya on 6/25/2017.
 */
public class SimpleBlockingQueue {

    private List<Integer> list;

    private int maxlimit ;

    public SimpleBlockingQueue(int maxlimit) {
        this.list = new ArrayList<>(maxlimit);
        this.maxlimit = maxlimit;
    }

    public synchronized void enqueue(Integer ele) throws InterruptedException {
        while (list.size() == maxlimit) {
            this.wait();
        }
        if(this.list.size() == 0) {
            notifyAll();
        }
        list.add(ele);
        //this.notify();
    }

    public synchronized Object dequeue()
            throws InterruptedException{
        while(this.list.size() == 0){
            wait();
        }
        if(this.list.size() == this.maxlimit){
            notify();
        }

        return this.list.remove(0);
    }

    public static void main(String[] args) {

        SimpleBlockingQueue simpleBlockingQueue = new SimpleBlockingQueue(8);

        Thread writer = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 55 ; i++) {
                    System.out.println(Thread.currentThread().getName() + " writing to queue -> " + i);
                    try {
                        simpleBlockingQueue.enqueue(i);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
        Thread writer2 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 55; i < 70 ; i++) {
                    System.out.println(Thread.currentThread().getName() + " writing to queue -> " + i);
                    try {
                        simpleBlockingQueue.enqueue(i);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

        Thread reader = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i <70 ; i++) {
                    //System.out.println(Thread.currentThread().getName() + " reading from queue -> " + i);
                    try {
                        System.out.println(Thread.currentThread().getName() +
                                " - Reading from queue ->" + simpleBlockingQueue.dequeue());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

        writer.start();
        reader.start();
        writer2.start();
    }
}
