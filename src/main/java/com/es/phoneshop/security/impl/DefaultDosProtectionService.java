package com.es.phoneshop.security.impl;

import com.es.phoneshop.security.DosProtectionService;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultDosProtectionService implements DosProtectionService {
    private static final long THRESHOLD = 20;
    private static final long THRESHOLD_TRACK_TIME = 60000;
    private final Timer timer;

    private Map<String, Long> countMap = new ConcurrentHashMap();

    private static final class InstanceHolder {
        static final DefaultDosProtectionService instance = new DefaultDosProtectionService();
    }

    public static DefaultDosProtectionService getInstance() {
        return DefaultDosProtectionService.InstanceHolder.instance;
    }

    private DefaultDosProtectionService() {
        timer = new Timer();
        timer.scheduleAtFixedRate(getTimerTask(), 1000, THRESHOLD_TRACK_TIME);
    }

    @Override
    public boolean isAllowed(String ip) {
        Long count = countMap.getOrDefault(ip, 0L);
        countMap.put(ip, ++count);
        return count <= THRESHOLD;
    }

    @Override
    public void shutdown() {
        if (timer != null) {
            timer.cancel();
        }
    }

    private TimerTask getTimerTask() {
        return new TimerTask() {
            @Override
            public void run() {
                countMap.clear();
            }
        };
    }

}
