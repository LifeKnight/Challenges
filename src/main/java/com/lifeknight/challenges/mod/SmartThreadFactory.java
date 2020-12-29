package com.lifeknight.challenges.mod;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class SmartThreadFactory implements ThreadFactory
{
    private final AtomicInteger threadNumber;
    
    public SmartThreadFactory() {
        this.threadNumber = new AtomicInteger(1);
    }
    
    @Override
    public Thread newThread(final Runnable r) {
        return new Thread(r, "LifeKnightThread" + this.threadNumber.getAndIncrement());
    }
}
