package com.banno.bork.model;

import com.android.ddmlib.testrunner.TestIdentifier;

public interface PoolTestCaseAccumulator {
    void record(Pool pool, TestIdentifier testIdentifier);

    int getCount(Pool pool, TestIdentifier testIdentifier);

    int getCount(TestIdentifier testIdentifier);
}
