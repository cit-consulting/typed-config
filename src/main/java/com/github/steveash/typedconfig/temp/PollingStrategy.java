package com.github.steveash.typedconfig.temp;

import java.util.concurrent.Future;

public interface PollingStrategy {
    Future<?> execute(Runnable run);

    void shutdown();
}
