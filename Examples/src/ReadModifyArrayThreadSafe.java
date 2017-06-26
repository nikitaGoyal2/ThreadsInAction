import java.util.ArrayList;
import java.util.List;

/**
 * Created by nikigoya on 6/22/2017.
 */
public class ReadModifyArrayThreadSafe {

    private ReentrantReadWriteLock lock;
    private List<Integer> list;

    public ReadModifyArrayThreadSafe(ReentrantReadWriteLock lock, List<Integer> list) {
        this.lock = lock;
        this.list = list;
    }

    public void add(Integer ele) throws InterruptedException {
        System.out.println(Thread.currentThread().getName() + "Inside add ");
        lock.lockWrite();
        list.add(ele);
        lock.unlockWrite();
    }

    public int read(int index) throws InterruptedException {
        System.out.println("incoming index " + index);
        if (index >= list.size()) {
            throw new IllegalStateException("index doesn't exist");
        }
        lock.lockRead();
        int x = list.get(index);
        lock.unlockRead();
        return x;
    }

    public static void main(String[] args) throws InterruptedException {

        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        List<Integer> list = new ArrayList<Integer>();
        ReadModifyArrayThreadSafe readModifyArrayThreadSafe = new ReadModifyArrayThreadSafe(lock, list);

        Thread write1 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    try {
                        System.out.println(Thread.currentThread().getName() + " - Writing");
                        readModifyArrayThreadSafe.add(i);
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        Thread write2 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 10; i < 50; i++) {
                    try {
                        System.out.println(Thread.currentThread().getName() + " - Writing");
                        readModifyArrayThreadSafe.add(i);
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        Thread read1 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    try {
                        lock.lockRead();
                        System.out.println("Read 1 " + list.get(i));
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    lock.unlockRead();

                }
            }
        });

        Thread read2 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 10; i < 50; i++) {
                    try {
                        lock.lockRead();
                        System.out.println("Read 2 " + list.get(i));
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    lock.unlockRead();

                }
            }
        });
        write1.start();
        write2.start();

        write1.join();
        write2.join();


        read1.start();
        read2.start();
      /*  System.out.println("Final list - ");
        for (Integer integer : list) {
            System.out.println(integer);
        }*/
    }

}
