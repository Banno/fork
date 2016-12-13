package com.shazam.fork.model;

import com.android.ddmlib.testrunner.TestIdentifier;
import com.google.common.base.Objects;

import java.util.concurrent.atomic.AtomicInteger;

public class TestCaseEventCounter {

    public static final TestCaseEventCounter EMPTY = new TestCaseEventCounter(null, 0);

    private TestIdentifier testIdentifier;
    private AtomicInteger count;

    public TestCaseEventCounter(TestIdentifier testIdentifier, int initialCount) {
        this.testIdentifier = testIdentifier;
        this.count = new AtomicInteger(initialCount);
    }

    public int increaseCount() {
        return count.incrementAndGet();
    }

    public TestIdentifier getTestIdentifier() {
        return testIdentifier;
    }

    public int getCount() {
        return count.get();
    }

    public TestCaseEventCounter withIncreasedCount() {
        increaseCount();
        return this;
    }

    @Override
    public int hashCode() {
        return testIdentifier.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final TestCaseEventCounter other = (TestCaseEventCounter) obj;
        return Objects.equal(this.testIdentifier, other.testIdentifier);
    }
}
