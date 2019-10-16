package com.banno.bork.runner.listeners;

import com.android.ddmlib.testrunner.ITestRunListener;
import com.android.ddmlib.testrunner.TestIdentifier;
import com.banno.bork.model.*;
import com.banno.bork.system.io.FileManager;
import com.banno.bork.system.io.RemoteFileManager;
import com.shazam.fork.model.Device;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;

import static com.banno.bork.system.io.FileType.COVERAGE;

public class CoverageListener implements ITestRunListener {

    private final Device device;
    private final FileManager fileManager;
    private final Pool pool;
    private final Logger logger = LoggerFactory.getLogger(CoverageListener.class);
    private final TestCaseEvent testCase;
    private TestIdentifier currentTest;

    public CoverageListener(Device device, FileManager fileManager, Pool pool, TestCaseEvent testCase) {
        this.device = device;
        this.fileManager = fileManager;
        this.pool = pool;
        this.testCase = testCase;
    }

    @Override
    public void testRunStarted(String runName, int testCount) {
    }

    @Override
    public void testStarted(TestIdentifier test) {
        currentTest = test;
    }

    @Override
    public void testFailed(TestIdentifier test, String trace) {
    }

    @Override
    public void testAssumptionFailure(TestIdentifier test, String trace) {
    }

    @Override
    public void testIgnored(TestIdentifier test) {
    }

    @Override
    public void testEnded(TestIdentifier test, Map<String, String> testMetrics) {
    }

    @Override
    public void testRunFailed(String errorMessage) {
    }

    @Override
    public void testRunStopped(long elapsedTime) {
    }

    @Override
    public void testRunEnded(long elapsedTime, Map<String, String> runMetrics) {
        final String remoteFile = RemoteFileManager.getCoverageFileName(currentTest);
        final File file = fileManager.createFile(COVERAGE, pool, device, currentTest);
        try {
            device.getDeviceInterface().pullFile(remoteFile, file.getAbsolutePath());
        } catch (Exception e) {
            logger.error("Something went wrong while pulling coverage file", e);
        }
    }
}