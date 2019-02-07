package com.banno.bork.model;

import com.google.common.base.Objects;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.SIMPLE_STYLE;

public class TestCaseEvent {

    private final List<String> testMethods;
    private final List<String> ignoredMethods;
    private final String testClass;
    private final boolean isClassIgnored;
    private final List<String> permissionsToRevoke;

    private TestCaseEvent(String testClass, List<String> testMethods, List<String> ignoredMethods, boolean isClassIgnored, List<String> permissionsToRevoke) {
        this.testMethods = testMethods;
        this.testClass = testClass;
        this.ignoredMethods = ignoredMethods;
        this.isClassIgnored = isClassIgnored;
        this.permissionsToRevoke = permissionsToRevoke;
    }

    public static TestCaseEvent newTestCase(String testClass,
                                            boolean isClassIgnored,
                                            List<String> testMethods,
                                            List<String> ignoredMethods) {
        return new TestCaseEvent(testClass, testMethods, ignoredMethods, isClassIgnored, emptyList());
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

    public List<String> getPermissionsToRevoke() {
        return permissionsToRevoke;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.testMethods, this.testClass, this.ignoredMethods, this.isClassIgnored, this.permissionsToRevoke);
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
                && Objects.equal(this.testClass, other.testClass)
                && Objects.equal(this.permissionsToRevoke, other.permissionsToRevoke);
    }

    @Override
    public String toString() {
        return reflectionToString(this, SIMPLE_STYLE);
    }
}
