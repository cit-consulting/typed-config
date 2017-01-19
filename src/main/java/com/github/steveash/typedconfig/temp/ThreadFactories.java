package com.github.steveash.typedconfig.temp;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

class ThreadFactories {
    static ThreadFactory newNamedDaemonThreadFactory() {
        final AtomicInteger counter = new AtomicInteger();

        return runnable -> {
            Thread thread = Executors.defaultThreadFactory().newThread(runnable);
            thread.setDaemon(true);
            thread.setName(String.format("Archaius-Poller-%d", counter.incrementAndGet()));
            return thread;
        };
    }
}