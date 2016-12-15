package com.shazam.fork.model;

import com.android.ddmlib.testrunner.TestIdentifier;
import com.google.common.base.Predicate;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.SetMultimap;

import static com.google.common.collect.FluentIterable.from;

/**
 * Class that keeps track of the number of times each testCase is executed for device.
 */
public class PoolTestCaseFailureAccumulator implements PoolTestCaseAccumulator {

    private SetMultimap<Pool, TestCaseEventCounter> map = HashMultimap.create();

    @Override
    public void record(Pool pool, TestIdentifier testIdentifier) {
        if (!map.containsKey(pool)) {
            map.put(pool, createNew(testIdentifier));
        }

        if (!from(map.get(pool)).anyMatch(isSameTestCase(testIdentifier))) {
            map.get(pool).add(
                    createNew(testIdentifier)
                            .withIncreasedCount());
        } else {
            from(map.get(pool))
                    .firstMatch(isSameTestCase(testIdentifier)).get()
                    .increaseCount();
        }
    }

    @Override
    public int getCount(Pool pool, TestIdentifier testIdentifier) {
        if (map.containsKey(pool)) {
            return from(map.get(pool))
                    .firstMatch(isSameTestCase(testIdentifier)).or(TestCaseEventCounter.EMPTY)
                    .getCount();
        } else {
            return 0;
        }
    }

    @Override
    public int getCount(TestIdentifier testIdentifier) {
        int result = 0;
        ImmutableList<TestCaseEventCounter> counters = from(map.values())
                .filter(isSameTestCase(testIdentifier)).toList();
        for (TestCaseEventCounter counter : counters) {
            result += counter.getCount();
        }
        return result;
    }

    private static TestCaseEventCounter createNew(final TestIdentifier testIdentifier) {
        return new TestCaseEventCounter(testIdentifier, 0);
    }

    private static Predicate<TestCaseEventCounter> isSameTestCase(final TestIdentifier testCaseEvent) {
        return input -> input.getTestIdentifier() != null && testCaseEvent.equals(input.getTestIdentifier());
    }
}
