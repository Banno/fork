package com.shazam.fork.model;

import com.android.ddmlib.testrunner.TestIdentifier;

import org.junit.Before;
import org.junit.Test;

import static com.shazam.fork.model.Device.Builder.aDevice;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class PoolTestCaseAccumulatorTestFailure {

    private final Device A_DEVICE = aDevice()
            .withSerial("a_device")
            .build();
    private final Device ANOTHER_DEVICE = aDevice()
            .withSerial("another_device")
            .build();

    private final Pool A_POOL = Pool.Builder.aDevicePool()
            .withName("a_pool")
            .addDevice(A_DEVICE)
            .build();

    private final Pool ANOTHER_POOL = Pool.Builder.aDevicePool()
            .withName("another_pool")
            .addDevice(ANOTHER_DEVICE)
            .build();

    private final TestIdentifier A_TEST_CASE = new TestIdentifier("a_class",
                                                                  "a_method");
    private final TestIdentifier ANOTHER_TEST_CASE = new TestIdentifier("a_class",
                                                                        "another_method");

    PoolTestCaseFailureAccumulator subject;

    @Before
    public void setUp() throws Exception {
        subject = new PoolTestCaseFailureAccumulator();
    }

    @Test
    public void shouldAggregateCountForSameTestCaseAcrossMultipleDevices() throws Exception {

        subject.record(A_POOL, A_TEST_CASE);
        subject.record(A_POOL, A_TEST_CASE);

        int actualCount = subject.getCount(A_TEST_CASE);

        assertThat(actualCount, equalTo(2));
    }

    @Test
    public void shouldCountTestsPerPool() throws Exception {
        subject.record(A_POOL, A_TEST_CASE);
        subject.record(A_POOL, A_TEST_CASE);

        int actualCount = subject.getCount(A_POOL, A_TEST_CASE);

        assertThat(actualCount, equalTo(2));
    }

    @Test
    public void shouldAggregateCountForSameTestCaseAcrossMultiplePools() throws Exception {

        subject.record(A_POOL, A_TEST_CASE);
        subject.record(ANOTHER_POOL, A_TEST_CASE);

        int actualCount = subject.getCount(A_TEST_CASE);

        assertThat(actualCount, equalTo(2));
    }

    @Test
    public void shouldNotReturnTestCasesForDifferentPool() throws Exception {
        subject.record(A_POOL, A_TEST_CASE);

        int actualCountForAnotherDevice = subject.getCount(ANOTHER_POOL, A_TEST_CASE);

        assertThat(actualCountForAnotherDevice, equalTo(0));
    }

    @Test
    public void shouldAccumulateDifferentTestCasesForSamePool() throws Exception {
        subject.record(A_POOL, A_TEST_CASE);
        subject.record(A_POOL, ANOTHER_TEST_CASE);

        int actualCount = subject.getCount(A_POOL, A_TEST_CASE);
        int anotherActualCount = subject.getCount(A_POOL, ANOTHER_TEST_CASE);

        assertThat(actualCount, equalTo(1));
        assertThat(anotherActualCount, equalTo(1));
    }
}
