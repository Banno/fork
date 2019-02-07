/*
 * Copyright 2015 Shazam Entertainment Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.banno.bork.injector.runner;

import com.banno.bork.runner.OverallProgressReporter;
import com.banno.bork.runner.ProgressReporter;

import static com.banno.bork.injector.ConfigurationInjector.configuration;
import static com.banno.bork.injector.runner.PoolProgressTrackersInjector.poolProgressTrackers;
import static com.banno.bork.injector.accumulator.PoolTestCaseFailureAccumulatorInjector.poolTestCaseFailureAccumulator;

public class ProgressReporterInjector {

    private ProgressReporterInjector() {}

    public static ProgressReporter progressReporter() {
        return new OverallProgressReporter(
                configuration().getTotalAllowedRetryQuota(),
                configuration().getRetryPerTestCaseQuota(),
                poolProgressTrackers(),
                poolTestCaseFailureAccumulator());
    }
}
