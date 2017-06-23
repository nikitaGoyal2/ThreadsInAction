/**
 * Created by nikigoya on 6/23/2017.
 */
public class BoundedSemaphore {

    private int maxBound;
    private int signal;

    public BoundedSemaphore(int maxBound) {
        this.maxBound = maxBound;
    }

    public synchronized void acquire() throws InterruptedException {
        while (signal == maxBound)
                wait();
        signal++;
        System.out.println("Acquired signal " + signal);
        notify();
    }

    public synchronized void release() throws InterruptedException {
        while (signal <= 0) {
            wait();
        }
        System.out.println("Released signal " + signal);
        signal--;
        notify();
    }

    public static void main(String[] args) {
        BoundedSemaphore simpleSemaphore = new BoundedSemaphore(5);
        final String[] message = new String[1];
        Thread producer1 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    message[0] = "I am producer1 at the iteraion no. " + i;
                    System.out.println("Produced Message - " + message[0]);
                    try {
                        simpleSemaphore.acquire();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }, "Producer1");

        Thread producer2 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    message[0] = "I am producer2 at the iteraion no. " + i;
                    System.out.println("Produced Message - " + message[0]);
                    try {
                        simpleSemaphore.acquire();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }, "Producer2");

        Thread consumer1 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    try {
                        simpleSemaphore.release();
                        System.out.println("Consumerd Message  - " + message[0]);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }, "Consumer1");

        Thread consumer2 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    try {
                        simpleSemaphore.release();
                        System.out.println("Consumerd Message  - " + message[0]);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }, "Consumer2");

        producer1.start();
        producer2.start();
        consumer1.start();
        consumer2.start();
    }

}
