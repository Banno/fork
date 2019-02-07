/*
 * Copyright 2014 Shazam Entertainment Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License.
 *
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.banno.bork.injector;

import com.banno.bork.ForkRunner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.banno.bork.injector.pooling.PoolLoaderInjector.poolLoader;
import static com.banno.bork.injector.runner.PoolTestRunnerFactoryInjector.poolTestRunnerFactory;
import static com.banno.bork.injector.runner.ProgressReporterInjector.progressReporter;
import static com.banno.bork.injector.suite.TestSuiteLoaderInjector.testSuiteLoader;
import static com.banno.bork.injector.summary.SummaryGeneratorHookInjector.summaryGeneratorHook;
import static com.banno.bork.utils.Utils.millisSinceNanoTime;
import static java.lang.System.nanoTime;

public class ForkRunnerInjector {

    private static final Logger logger = LoggerFactory.getLogger(ForkRunnerInjector.class);

    private ForkRunnerInjector() {}

    public static ForkRunner forkRunner() {
        long startNanos = nanoTime();

        ForkRunner forkRunner = new ForkRunner(
                poolLoader(),
                testSuiteLoader(),
                poolTestRunnerFactory(),
                progressReporter(),
                summaryGeneratorHook());

        logger.debug("Bootstrap of ForkRunner took: {} milliseconds", millisSinceNanoTime(startNanos));

        return forkRunner;
    }
}
