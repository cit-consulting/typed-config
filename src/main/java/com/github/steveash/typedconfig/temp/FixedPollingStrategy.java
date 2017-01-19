package com.github.steveash.typedconfig.temp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FixedPollingStrategy implements PollingStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(FixedPollingStrategy.class);
    private final ScheduledExecutorService executor;
    private final long interval;
    private final TimeUnit units;


    public FixedPollingStrategy(long interval, TimeUnit units) {
        this.executor = Executors.newSingleThreadScheduledExecutor(ThreadFactories.newNamedDaemonThreadFactory());
        this.interval = interval;
        this.units = units;

    }

    @Override
    public Future<?> execute(final Runnable callback) {
        while (true) {
            try {
                callback.run();
                break;
            } catch (Exception e) {
                try {
                    LOG.warn("Fail to poll the polling source", e);
                    units.sleep(interval);
                } catch (InterruptedException e1) {
                    Thread.currentThread().interrupt();
                    return Futures.immediateFailure(e);
                }
            }
        }
        return executor.scheduleWithFixedDelay(() -> {
            try {
                callback.run();
            } catch (Exception e) {
                LOG.warn("Failed to load properties", e);
            }
        }, interval, interval, units);
    }

    @Override
    public void shutdown() {
        executor.shutdown();
    }

}
