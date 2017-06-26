/**
 * Created by nikigoya on 6/25/2017.
 */
public class SimpleCyclicBarrier {

    private int count ;
    private Thread barrierAction;

    public SimpleCyclicBarrier(int count, Thread barrierAction) {
        this.count = count;
        this.barrierAction = barrierAction;
    }
}
