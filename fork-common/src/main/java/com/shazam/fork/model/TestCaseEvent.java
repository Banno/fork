package com.shazam.fork.model;

import com.google.common.base.Objects;

import java.util.List;

import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.SIMPLE_STYLE;

public class TestCaseEvent {

    private final List<String> testMethods;
    private final List<String> ignoredMethods;
    private final String testClass;
    private final boolean isClassIgnored;

    private TestCaseEvent(String testClass, List<String> testMethods, List<String> ignoredMethods, boolean isClassIgnored) {
        this.testMethods = testMethods;
        this.testClass = testClass;
        this.ignoredMethods = ignoredMethods;
        this.isClassIgnored = isClassIgnored;
    }

    public static TestCaseEvent newTestCase(String testClass,
                                            boolean isClassIgnored,
                                            List<String> testMethods,
                                            List<String> ignoredMethods) {
        return new TestCaseEvent(testClass, testMethods, ignoredMethods, isClassIgnored);
    }

    public List<String> getTestMethods() {
        return testMethods;
    }

    public String getTestClass() {
        return testClass;
    }

    public List<String> ignoredMethods() {
        return ignoredMethods;
    }

    public boolean isClassIgnored() {
        return isClassIgnored;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.testMethods, this.testClass, this.ignoredMethods, this.isClassIgnored);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TestCaseEvent other = (TestCaseEvent) obj;
        return Objects.equal(this.testMethods, other.testMethods)
                && Objects.equal(this.isClassIgnored, other.isClassIgnored)
                && Objects.equal(this.ignoredMethods, other.ignoredMethods)
                && Objects.equal(this.testClass, other.testClass);
    }

    @Override
    public String toString() {
        return reflectionToString(this, SIMPLE_STYLE);
    }
}
