import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * Created by nikigoya on 6/26/2017.
 */
public class DelayQueueExample {

    static class Token implements Delayed{

        Date expiryTime;
        String value;

        public Token(Date expiryTime, String value) {
            this.expiryTime = expiryTime;
            this.value = value;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            long exTime = expiryTime.getTime();
            long curTime = (new Date()).getTime();
            long diff = exTime - curTime;
            return unit.convert(diff, TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            long diff = this.getDelay(TimeUnit.MILLISECONDS) - ((Token) o).getDelay(TimeUnit.MILLISECONDS);
            return diff < 0 ? -1 : (diff > 0 ? 1 : 0);
        }
    }

    public static void main(String[] args) {
        DelayQueue queue = new DelayQueue();

        Thread writer = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 1 ; i <= 5; i++) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.MILLISECOND, i * 1000);
                    Date cur = calendar.getTime();
                    queue.add(new Token(cur, "Value" + i));
                    System.out.println(Thread.currentThread().getName() + " Wrote token to queue!");
                    try {
                        Thread.sleep(200 );
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        Thread reader = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                while (queue.size() > 0){
                    Token token = (Token) queue.poll();
                    if (token == null) {
                        System.out.println("No expiry token found");
                    }
                    else {
                        System.out.println("Expiry token found " + token.value + " cur time " + (new Date()) + "  expired time "+ token.expiryTime);
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        writer.start();
        reader.start();
    }
}
