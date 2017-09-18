/*
 * Copyright 2016 Shazam Entertainment Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.shazam.fork.suite;

import com.shazam.fork.io.DexFileExtractor;
import com.shazam.fork.model.TestCaseEvent;

import org.hamcrest.Matcher;
import org.jf.dexlib.DexFile;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import static com.shazam.fork.io.FakeDexFileExtractor.fakeDexFileExtractor;
import static com.shazam.fork.io.Files.convertFileToDexFile;
import static com.shazam.fork.model.TestCaseEvent.newTestCase;
import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.containsInAnyOrder;

/**
 * This test is based on the <code>tests.dex</code> file, which contains test classes with the following code:
 * <blockquote><pre>
 *{@literal}@Ignore
 *public class IgnoredClassTest {
 *    {@literal}@Test
 *    public void methodOfAnIgnoredTestClass() {
 *    }
 *}
 *
 *public class ClassWithNoIgnoredMethodsTest {
 *    {@literal}@Test
 *    public void firstTestMethod() {
 *    }
 *
 *    {@literal}@Test
 *    public void secondTestMethod() {
 *    }
 *}
 *
 *public class ClassWithSomeIgnoredMethodsTest {
 *    {@literal}@Test
 *    public void nonIgnoredTestMethod() {
 *    }
 *
 *    {@literal}@Test
 *    {@literal}@Ignore
 *    public void ignoredTestMethod() {
 *    }
 *}
 * </pre></blockquote>
 */
public class TestSuiteLoaderTest {
    private static final File ANY_INSTRUMENTATION_APK_FILE = null;

    private final DexFileExtractor fakeDexFileExtractor = fakeDexFileExtractor().thatReturns(testDexFile());
    private final TestClassMatcher fakeTestClassMatcher = new PackageAndClassNameMatcher(Pattern.compile("com.shazam.forktest"),
                                                                                         Pattern.compile("^((?!Abstract).)*Test$"));

    private DexFile testDexFile() {
        URL testDexResourceUrl = this.getClass().getResource("/tests.dex");
        String testDexFile = testDexResourceUrl.getFile();
        File file = new File(testDexFile);
        return convertFileToDexFile().apply(file);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void populatesTestCaseEvents() throws Exception {
        TestSuiteLoader testSuiteLoader = new TestSuiteLoader(ANY_INSTRUMENTATION_APK_FILE, fakeDexFileExtractor,
                fakeTestClassMatcher);

        System.out.println(testSuiteLoader.loadTestSuite());

        assertThat(testSuiteLoader.loadTestSuite(), containsInAnyOrder(
                sameTestEventAs("com.shazam.forktest.IgnoredClassTest",
                                true,
                                asList("methodOfAnIgnoredTestClass"),
                                Collections.emptyList()),
                sameTestEventAs("com.shazam.forktest.ClassWithNoIgnoredMethodsTest",
                                false,
                                asList("firstTestMethod",
                                       "secondTestMethod"),
                                Collections.emptyList()),
                sameTestEventAs("com.shazam.forktest.ClassWithSomeIgnoredMethodsTest",
                                false,
                                asList("nonIgnoredTestMethod"),
                                asList("ignoredTestMethod"))
        ));
    }

    @Nonnull
    private Matcher<TestCaseEvent> sameTestEventAs(String testClass,
                                                   boolean isIgnored,
                                                   List<String> testMethods,
                                                   List<String> ignoredTests) {
        return sameBeanAs(newTestCase(testClass, isIgnored, testMethods, ignoredTests));
    }
}
