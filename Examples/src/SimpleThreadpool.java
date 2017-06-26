import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by nikigoya on 6/25/2017.
 */
//We can also introduce flag to stop threadpool
public class SimpleThreadpool {
    private BlockingQueue<Thread> waitingthreads;

    private List<ThreadTask> threadTasks ;

    public SimpleThreadpool(int size) {
        this.waitingthreads = new LinkedBlockingQueue();
        this.threadTasks = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            threadTasks.add(new ThreadTask(waitingthreads));
        }
        for (int i = 0; i < size; i++) {
            System.out.println("Starting thread " + i);
            threadTasks.get(i).start();
        }
    }

    public void addWaitingthread(Runnable waitingthreads, String name) {
        this.waitingthreads.add(new Thread(waitingthreads, name));
    }

    static class ThreadTask extends Thread{
        BlockingQueue queue ;

        public ThreadTask(BlockingQueue queue) {
            this.queue = queue;
        }

        @Override
        public void run() {

            while (true) {
                Thread thread = (Thread) queue.poll();
                if (thread != null){
                    thread.run();
                }
            }
        }
    }

    public static void main(String[] args) {
        SimpleThreadpool simpleThreadpool = new SimpleThreadpool(2);


        simpleThreadpool.addWaitingthread(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + " -> " + "First" );
            }
        } , "First");

        simpleThreadpool.addWaitingthread(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + " -> " + "Second" );
            }
        } , "Second");

        simpleThreadpool.addWaitingthread(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + " -> " + "Thirth" );
            }
        } , "Thirth");

        simpleThreadpool.addWaitingthread(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + " -> " + "Forth" );
            }
        } , "Forth");

        simpleThreadpool.addWaitingthread(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + " -> " + "Sixth" );
            }
        } , "Sixth");


    }
}
