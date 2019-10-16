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
package com.banno.bork.gradle

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.api.ApkVariant
import com.android.build.gradle.api.ApkVariantOutput
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.api.TestVariant
import com.shazam.fork.ForkConfigurationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.tasks.TaskProvider

/**
 * Gradle plugin for Fork.
 */
class ForkPlugin implements Plugin<Project> {

    /** Task name prefix. */
    private static final String TASK_PREFIX = "bork"

    @Override
    void apply(final Project project) {

        if (!project.plugins.findPlugin(AppPlugin) && !project.plugins.findPlugin(LibraryPlugin)) {
            throw new IllegalStateException("Android plugin is not found")
        }

        if (project.extensions.findByType(ForkConfigurationExtension) == null) {
            project.extensions.add "fork", ForkConfigurationExtension
        }

        def forkTask = project.tasks.register(TASK_PREFIX) {
            group = JavaBasePlugin.VERIFICATION_GROUP
            description = "Runs all the instrumentation test variations on all the connected devices"
        }

        BaseExtension android = project.android
        android.testVariants.all { TestVariant variant ->
            def forkTaskForTestVariant = registerTask(variant, project)
            forkTask.configure {
                dependsOn forkTaskForTestVariant
            }
        }
    }

    private static TaskProvider<ForkRunTask> registerTask(final TestVariant variant, final Project project) {
        return project.tasks.register("${TASK_PREFIX}${variant.name.capitalize()}", ForkRunTask) {
            checkTestVariants(variant)
            ApkVariant testedVariant = variant.testedVariant
            checkTestedVariants(testedVariant)

            ForkConfigurationExtension config = project.fork

            description = "Runs instrumentation tests on all the connected devices for '${variant.name}' variation and generates a report with screenshots"
            group = JavaBasePlugin.VERIFICATION_GROUP

            ApkVariantOutput variantOutput = variant.outputs.asList().first()
            instrumentationApk = new File(variant.packageApplicationProvider.get().outputDirectory.path + "/" + variantOutput.outputFileName)

            title = config.title
            subtitle = config.subtitle
            testClassRegex = config.testClassRegex
            testPackage = config.testPackage
            testOutputTimeout = config.testOutputTimeout
            testSize = config.testSize
            excludedSerials = config.excludedSerials
            fallbackToScreenshots = config.fallbackToScreenshots
            totalAllowedRetryQuota = config.totalAllowedRetryQuota
            retryPerTestCaseQuota = config.retryPerTestCaseQuota
            isCoverageEnabled = config.isCoverageEnabled
            poolingStrategy = config.poolingStrategy
            autoGrantPermissions = config.autoGrantPermissions
            ignoreFailures = config.ignoreFailures
            excludedAnnotation = config.excludedAnnotation

            ApkVariantOutput testedVariantOutput = testedVariant.outputs.first()
            applicationApk = new File(testedVariant.packageApplicationProvider.get().outputDirectory.path + "/" + testedVariantOutput.outputFileName)

            String baseOutputDir = config.baseOutputDir
            File outputBase
            if (baseOutputDir) {
                outputBase = new File(baseOutputDir)
            } else {
                outputBase = new File(project.buildDir, "reports/fork")
            }
            output = new File(outputBase, variant.name)

            dependsOn testedVariant.assembleProvider, variant.assembleProvider

            outputs.upToDateWhen { false }
        }
    }

    private static checkTestVariants(TestVariant testVariant) {
        if (testVariant.outputs.size() > 1) {
            throw new UnsupportedOperationException("The Fork plugin does not support abi/density splits for test APKs")
        }
    }

    /**
     * Checks that if the base variant contains more than one outputs (and has therefore splits), it is the universal APK.
     * Otherwise, we can test the single output. This is a workaround until Fork supports test & app splits properly.
     *
     * @param baseVariant the tested variant
     */
    private static checkTestedVariants(BaseVariant baseVariant) {
        if (baseVariant.outputs.size() > 1) {
            throw new UnsupportedOperationException(
                    "The Fork plugin does not support abi splits for app APKs, but supports testing via a universal APK. " +
                            "Add the flag \"universalApk true\" in the android.splits.abi configuration."
            )
        }
    }
}