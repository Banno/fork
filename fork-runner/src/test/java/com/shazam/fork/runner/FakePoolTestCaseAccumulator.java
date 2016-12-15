package com.shazam.fork.runner;

import com.android.ddmlib.testrunner.TestIdentifier;
import com.shazam.fork.model.Pool;
import com.shazam.fork.model.PoolTestCaseAccumulator;
import com.shazam.fork.model.TestCaseEvent;

public class FakePoolTestCaseAccumulator implements PoolTestCaseAccumulator {

    private int count = 0;

    public static FakePoolTestCaseAccumulator aFakePoolTestCaseAccumulator(){
        return new FakePoolTestCaseAccumulator();
    }

    public FakePoolTestCaseAccumulator thatAlwaysReturns(int count){
        this.count = count;
        return this;
    }

    @Override
    public void record(Pool pool, TestIdentifier testCaseEvent) {
    }

    @Override
    public int getCount(Pool pool, TestIdentifier testCaseEvent) {
        return count;
    }

    @Override
    public int getCount(TestIdentifier testCaseEvent) {
        return count;
    }
}
