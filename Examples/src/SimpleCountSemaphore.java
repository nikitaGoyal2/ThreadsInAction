/**
 * Created by nikigoya on 6/22/2017.
 */

public class SimpleCountSemaphore {

    private int signal;

    public synchronized void acquire(){
        signal++;
        System.out.println("Acquired signal " + signal);
        notify();
    }

    public synchronized void release() throws InterruptedException {
        while (signal <= 0){
            wait();
        }
        System.out.println("Released signal " + signal);
        signal--;
    }

    public static void main(String[] args) {
        SimpleCountSemaphore simpleSemaphore = new SimpleCountSemaphore();
        final String[] message = new String[1];
        Thread producer = new Thread( new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10 ; i++) {
                    message[0] = "I am producer at the iteraion no. " + i;
                    System.out.println("Produced Message - " + message[0]);
                    simpleSemaphore.acquire();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }, "Producer");

        Thread consumer = new Thread( new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10 ; i++) {
                    try {
                        simpleSemaphore.release();
                        System.out.println("Consumerd Message  - " + message[0]);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }, "Producer");

        producer.start();
        consumer.start();
    }
}
