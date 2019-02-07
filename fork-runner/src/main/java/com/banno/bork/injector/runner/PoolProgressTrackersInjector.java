package com.banno.bork.injector.runner;

import com.banno.bork.model.Pool;
import com.banno.bork.runner.PoolProgressTracker;

import java.util.Map;

import static com.beust.jcommander.internal.Maps.newHashMap;

public class PoolProgressTrackersInjector {

    private PoolProgressTrackersInjector() {}

    public static Map<Pool, PoolProgressTracker> poolProgressTrackers() {
        return newHashMap();
    }
}
