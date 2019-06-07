package net.script.utils.tasking;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;

public class Barrier {
    private AtomicInteger maxCount = new AtomicInteger(0);
    private Map<String, Boolean> checkedIn = new ConcurrentHashMap<>();
    private Set<String> nessecaryKeys = new HashSet<>();

    public static Barrier of(int maxCount, String... necessaryKeys) {
        Barrier barrier = new Barrier();
        barrier.maxCount = new AtomicInteger(maxCount);
        if (necessaryKeys != null) {
            barrier.nessecaryKeys.addAll(Arrays.asList(necessaryKeys));
        }
        return barrier;
    }

    public synchronized boolean isReached() {
        if (!this.nessecaryKeys.isEmpty()) {
            if (nessecaryKeys.containsAll(checkedIn.keySet())) {
                return true;
            }
        }
        return maxCount.get() <= this.checkedIn.size();
    }

    public synchronized boolean checkIn(String name) {
        this.checkedIn.put(name, true);
        return this.isReached();
    }

    public synchronized boolean areAtBarrier(String... objs) {
        if (objs == null) {
            return false;
        }
        for (String obj : objs) {
            if (!checkedIn.containsKey(obj)) {
                return false;
            }
        }
        return true;
    }
}
