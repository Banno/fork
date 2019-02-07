package com.banno.bork.injector.accumulator;

import com.banno.bork.model.PoolTestCaseFailureAccumulator;

public class PoolTestCaseFailureAccumulatorInjector {

    private PoolTestCaseFailureAccumulatorInjector() {}

    public static PoolTestCaseFailureAccumulator poolTestCaseFailureAccumulator() {
        return new PoolTestCaseFailureAccumulator();
    }
}
