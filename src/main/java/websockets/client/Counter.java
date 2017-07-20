package websockets.client;

import java.util.concurrent.atomic.AtomicLong;

public class Counter {

    private static AtomicLong atomicLong = new AtomicLong();

    public static long incrementAndGet() {
        return atomicLong.incrementAndGet();
    }
}
