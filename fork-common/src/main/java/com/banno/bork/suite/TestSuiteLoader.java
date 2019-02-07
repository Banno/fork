/*
 * Copyright 2016 Shazam Entertainment Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.banno.bork.suite;

import com.banno.bork.io.DexFileExtractor;
import com.banno.bork.model.TestCaseEvent;

import org.jf.dexlib.AnnotationDirectoryItem;
import org.jf.dexlib.AnnotationItem;
import org.jf.dexlib.AnnotationSetItem;
import org.jf.dexlib.ClassDefItem;
import org.jf.dexlib.EncodedValue.ArrayEncodedValue;
import org.jf.dexlib.EncodedValue.StringEncodedValue;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;

import static com.banno.bork.model.TestCaseEvent.newTestCase;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

public class TestSuiteLoader {
    private static final String IGNORE_ANNOTATION = "Lorg/junit/Ignore;";
    private static final String REVOKE_PERMISSION_ANNOTATION = "Lcom/shazam/fork/RevokePermission;";

    private final File instrumentationApkFile;
    private final DexFileExtractor dexFileExtractor;
    private final TestClassMatcher testClassMatcher;

    public TestSuiteLoader(File instrumentationApkFile, DexFileExtractor dexFileExtractor, TestClassMatcher testClassMatcher) {
        this.instrumentationApkFile = instrumentationApkFile;
        this.dexFileExtractor = dexFileExtractor;
        this.testClassMatcher = testClassMatcher;
    }

    public Collection<TestCaseEvent> loadTestSuite() throws NoTestCasesFoundException {
        List<TestCaseEvent> testCaseEvents
                = dexFileExtractor.getDexFiles(instrumentationApkFile)
                                  .stream()
                                  .map(dexFile -> dexFile.ClassDefsSection.getItems())
                                  .flatMap(Collection::stream)
                                  .filter(c -> testClassMatcher.matchesPatterns(c.getClassType().getTypeDescriptor()))
                                  .map(this::convertClassToTestCaseEvents)
                                  .flatMap(Collection::stream)
                                  .collect(toList());

        if (testCaseEvents.isEmpty()) {
            throw new NoTestCasesFoundException("No tests cases were found in the test APK: " + instrumentationApkFile.getAbsolutePath());
        }

        return testCaseEvents;
    }

    @Nonnull
    private List<TestCaseEvent> convertClassToTestCaseEvents(ClassDefItem classDefItem) {
        AnnotationDirectoryItem annotationDirectoryItem = classDefItem.getAnnotations();
        if (annotationDirectoryItem == null) {
            return emptyList();
        }

        return Collections.singletonList(convertToTestCaseEvent(classDefItem, annotationDirectoryItem));
    }

    @Nonnull
    private TestCaseEvent convertToTestCaseEvent(ClassDefItem classDefItem,
                                                 AnnotationDirectoryItem annotationDirectoryItem) {
        List<String> testMethods = annotationDirectoryItem.getMethodAnnotations()
                .stream()
                .filter(methodAnnotation -> !isMethodIgnored(methodAnnotation.annotationSet.getAnnotations()))
                .map(methodAnnotation -> methodAnnotation.method.getMethodName().getStringValue())
                .collect(Collectors.toList());

        List<String> ignoredMethods = annotationDirectoryItem.getMethodAnnotations()
                .stream()
                .filter(methodAnnotation -> isMethodIgnored(methodAnnotation.annotationSet.getAnnotations()))
                .map(methodAnnotation -> methodAnnotation.method.getMethodName().getStringValue())
                .collect(Collectors.toList());

        return newTestCase(getClassName(classDefItem), isClassIgnored(annotationDirectoryItem),
                           testMethods,
                           ignoredMethods
        );
    }

    private String getClassName(ClassDefItem classDefItem) {
        String typeDescriptor = classDefItem.getClassType().getTypeDescriptor();
        return typeDescriptor.substring(1, typeDescriptor.length() - 1).replace('/', '.');
    }

    private boolean isMethodIgnored(AnnotationItem... annotationItems) {
        return containsAnnotation(IGNORE_ANNOTATION, annotationItems);
    }

    private List<String> getPermissionsToRevoke(AnnotationItem[] annotations) {
        return stream(annotations)
                .filter(annotationItem -> REVOKE_PERMISSION_ANNOTATION.equals(stringType(annotationItem)))
                .map(annotationItem -> annotationItem.getEncodedAnnotation().values)
                .flatMap(encodedValues -> stream(encodedValues)
                        .flatMap(encodedValue -> stream(((ArrayEncodedValue) encodedValue).values)
                                .map(stringEncoded -> ((StringEncodedValue)stringEncoded).value.getStringValue())))
                .collect(toList());
    }

    private boolean isClassIgnored(AnnotationDirectoryItem annotationDirectoryItem) {
        AnnotationSetItem classAnnotations = annotationDirectoryItem.getClassAnnotations();
        if (classAnnotations == null) {
            return false;
        }
        return containsAnnotation(IGNORE_ANNOTATION, classAnnotations.getAnnotations());
    }

    private boolean containsAnnotation(String comparisonAnnotation, AnnotationItem... annotations) {
        return stream(annotations).anyMatch(annotation -> comparisonAnnotation.equals(stringType(annotation)));
    }

    private String stringType(AnnotationItem annotation) {
        return annotation.getEncodedAnnotation().annotationType.getTypeDescriptor();
    }
}
