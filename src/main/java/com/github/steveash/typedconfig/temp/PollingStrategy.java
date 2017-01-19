package com.github.steveash.typedconfig.temp;

public interface PollingStrategy {
    void execute(Runnable run);

    void shutdown();
}
