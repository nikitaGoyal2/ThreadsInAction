import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * Created by nikigoya on 6/26/2017.
 */
public class SelfExpiryCache<K, V> {

    private static final long DEFAULTEXPIRYTIME = 1000;
    private final Map<K, V> cacheMap;
    private final Map<K, ExpiryKey> keyExpiryMap;
    private final DelayQueue<ExpiryKey> expiredKeys;

    public SelfExpiryCache() {

        cacheMap = new ConcurrentHashMap<>();
        keyExpiryMap = new WeakHashMap<>();
        expiredKeys = new DelayQueue<>();
    }


    public void put(K key, V value) {
        removeExpiredKeys();
        put(key, value, DEFAULTEXPIRYTIME);
    }

    public V put(K key, V value, long aliveTime) {
        removeExpiredKeys();

        ExpiryKey newExpiry = new ExpiryKey(key, new Date((new Date()).getTime() + aliveTime));


        ExpiryKey oldExpiryValue = keyExpiryMap.put(key, newExpiry);
        if (oldExpiryValue != null) {
            oldExpiryValue.expired();
            removeExpiredKeys();
        }
        expiredKeys.offer(newExpiry);
        return cacheMap.put(key, value);
    }

    public V get(K key) {
        removeExpiredKeys();
        renewKey(key);
        return cacheMap.get(key);
    }

    private void renewKey(K key) {
        ExpiryKey expiryKey = keyExpiryMap.get(key);
        if (expiryKey != null)
            keyExpiryMap.get(key).renew();
    }

    private void removeExpiredKeys() {

        ExpiryKey expiredkey = expiredKeys.poll();
        while (expiredkey != null) {
            cacheMap.remove(expiredkey.getKey());
            keyExpiryMap.remove(expiredkey.getKey());
            expiredkey = expiredKeys.poll();
        }

    }


    private class ExpiryKey implements Delayed {
        private final K key;
        private Date expiryTime;

        public ExpiryKey(K key, Date date) {
            this.key = key;
            this.expiryTime = date;
        }

        public K getKey() {
            return key;
        }

        public Date getExpiryTime() {
            return expiryTime;
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
            long diff = this.getDelay(TimeUnit.MILLISECONDS) - ((ExpiryKey) o).getDelay(TimeUnit.MILLISECONDS);
            return diff < 0 ? -1 : (diff > 0 ? 1 : 0);
        }

        public void renew() {
            this.expiryTime = new Date();
        }

        public void expired() {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.SECOND, -10);
            this.expiryTime = c.getTime();
        }
    }


    public static void main(String[] args) {
            SelfExpiryCache<String, Integer> cache = new SelfExpiryCache<>();

            Thread writer1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i <= 20 ; i+=2) {
                        cache.put("Key" + i , i , (i+1) * 1000);
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

        Thread writer2 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 1; i < 20 ; i+=2) {
                    cache.put("Key" + i , i , (i) * 1000);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        Thread reader1  = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i <= 20 ; i++) {
                    Integer val = cache.get("Key" + i);
                    if (val == null){
                        System.out.println(Thread.currentThread().getName() + " Key" + i + " -> value not found! ");
                    } else {
                        System.out.println(Thread.currentThread().getName() + " Key" + i + " -> value is " + val);
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        writer1.start();
        writer2.start();
        reader1.start();

    }


}
