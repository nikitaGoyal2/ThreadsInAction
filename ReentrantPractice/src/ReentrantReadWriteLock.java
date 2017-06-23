import java.util.HashMap;
import java.util.Map;

/**
 * Created by nikigoya on 6/22/2017.
 */
public class ReentrantReadWriteLock {

    private int writeRequests;
    private int writeAcesss;
    private Thread writingThread;
    private Map<Thread, Integer> readingThreads;

    public ReentrantReadWriteLock() {
        readingThreads = new HashMap<>();
    }


    public synchronized void lockRead() throws InterruptedException {

        //check no writer
        while (!canGrantReadAccess(Thread.currentThread())) {
            this.wait();
        }

        Integer currentReaderCount = readingThreads.get(Thread.currentThread());
        if (currentReaderCount == null) currentReaderCount = 0;
        readingThreads.put(Thread.currentThread(), currentReaderCount.intValue() + 1);
    }


    public synchronized void unlockRead() {
        Integer currentReaderCount = readingThreads.get(Thread.currentThread());
        if (currentReaderCount == null) {
            throw new IllegalMonitorStateException("Does't hold read lock on current thread");
        }
        currentReaderCount--;
        if (currentReaderCount == 0) {
            readingThreads.remove(Thread.currentThread());
        } else {

            readingThreads.put(Thread.currentThread(), currentReaderCount);
        }

        notifyAll();
    }

    public synchronized void lockWrite() throws InterruptedException {

        //check no writer
        writeRequests++;
        while (!canGrantWriteAccess(Thread.currentThread())) {
            this.wait();
        }

        writeRequests--;
        writeAcesss++;
        writingThread = Thread.currentThread();
    }

    private boolean canGrantWriteAccess(Thread callingThread) {
        if (readingThreads.size() == 1 && readingThreads.get(callingThread) != null)
            return true;
        if (readingThreads.size() > 0) return false;
        if (writingThread == null) return true;
        if (!isWriter(callingThread)) return false;
        return true;
    }


    public synchronized void unlockWrite() {
        if (!isWriter(Thread.currentThread())) {
            throw new IllegalMonitorStateException("Calling Thread does not hold the write lock");
        }
        writeAcesss--;
        if (writeAcesss == 0) {
            writingThread = null;
            notifyAll();
        }
    }


    private boolean canGrantReadAccess(Thread callingThread) {
        if (isWriter(callingThread)) return true;
        if (hasWriter()) return false;
        if (isReader(callingThread)) return true;
        if (hasWriteRequests()) return false;
        return true;
    }

    private boolean isWriter(Thread callingThread) {
        return writingThread == callingThread;
    }

    private boolean hasWriteRequests() {
        return this.writeRequests > 0;
    }

    private boolean hasWriter() {
        return writingThread != null;
    }

    private boolean isReader(Thread callingThread) {
        return readingThreads.get(callingThread) != null;
    }
}
