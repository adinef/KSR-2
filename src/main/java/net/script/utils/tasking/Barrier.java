package net.script.utils.tasking;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Barrier {
    private AtomicInteger maxCount = new AtomicInteger(0);
    public Map<String, Boolean> checkedIn = new ConcurrentHashMap<>();

    public static Barrier of(int maxCount) {
        Barrier barrier = new Barrier();
        barrier.maxCount = new AtomicInteger(maxCount);
        return barrier;
    }

    public synchronized boolean isReached() {
        return maxCount.get() <= this.checkedIn.size();
    }

    public synchronized boolean checkIn(String name) {
        this.checkedIn.put(name, true);
        return this.isReached();
    }
}
