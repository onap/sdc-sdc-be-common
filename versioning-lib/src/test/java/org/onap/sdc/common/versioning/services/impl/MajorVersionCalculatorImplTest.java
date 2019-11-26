/*
 * Copyright © 2019 European Support Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.onap.sdc.common.versioning.services.impl;

import org.junit.Before;
import org.junit.Test;
import org.onap.sdc.common.versioning.services.types.VersionCreationMethod;

import static org.junit.Assert.assertEquals;

public class MajorVersionCalculatorImplTest {

    private MajorVersionCalculatorImpl calculator;
    private static final String INITIAL_VERSION = "1.0";

    @Before
    public void init() {
        calculator = new MajorVersionCalculatorImpl();
    }

    @Test
    public void calculateInitialVersionWithNullAndMajor() {
        String expectedInitialVersion = calculator.calculate(null, VersionCreationMethod.major);
        assertEquals(INITIAL_VERSION, expectedInitialVersion);
    }

    @Test
    public void calculateInitialVersionWithNullAndMinor() {
        String expectedValue = "0.1";
        String actualValue = calculator.calculate(null, VersionCreationMethod.minor);
        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void calculateMajorIncrement() {
        String expectedValue = "2.0";
        String actualValue = calculator.calculate(INITIAL_VERSION, VersionCreationMethod.major);
        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void calculateMajorIncrementTwice() {
        String expectedValue = "3.0";
        String firstValue = calculator.calculate(INITIAL_VERSION, VersionCreationMethod.major);
        String actualValue = calculator.calculate(firstValue, VersionCreationMethod.major);
        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void calculateMinorIncrement() {
        String expectedValue = "1.1";
        String actualValue = calculator.calculate(INITIAL_VERSION, VersionCreationMethod.minor);
        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void calculateMinorIncrementTwice() {
        String expectedValue = "1.2";
        String firstValue = calculator.calculate(INITIAL_VERSION, VersionCreationMethod.minor);
        String actualValue = calculator.calculate(firstValue, VersionCreationMethod.minor);
        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void calculateMinorIncrementEdgeCase() {
        String expectedValue = "1.10";
        String initialValue = "1.9";
        String actualValue = calculator.calculate(initialValue, VersionCreationMethod.minor);
        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void calculateMajorIncrementEdgeCase() {
        String expectedValue = "2.0";
        String initialValue = "1.7";
        String actualValue = calculator.calculate(initialValue, VersionCreationMethod.major);
        assertEquals(expectedValue, actualValue);
    }

    @Test(expected = IllegalArgumentException.class)
    public void calculateException() {
        String initialValue = "1";
        calculator.calculate(initialValue, VersionCreationMethod.minor);
    }

}