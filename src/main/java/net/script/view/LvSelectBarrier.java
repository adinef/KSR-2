package net.script.view;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class LvSelectBarrier {
    private AtomicInteger maxCount = new AtomicInteger(0);
    public Map<String, Boolean> checkedIn = new ConcurrentHashMap<>();

    public static LvSelectBarrier of(int maxCount) {
        LvSelectBarrier lvSelectBarrier = new LvSelectBarrier();
        lvSelectBarrier.maxCount = new AtomicInteger(maxCount);
        return lvSelectBarrier;
    }

    public synchronized boolean isReached() {
        return maxCount.get() <= this.checkedIn.size();
    }

    public synchronized boolean checkIn(String name) {
        this.checkedIn.put(name, true);
        return this.isReached();
    }
}
