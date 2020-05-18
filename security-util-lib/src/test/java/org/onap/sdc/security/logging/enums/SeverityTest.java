/*-
 * ============LICENSE_START=======================================================
 * SDC
 * ================================================================================
 * Copyright (C) 2020 Samsung. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END============================================
 * ===================================================================
 *
 */

package org.onap.sdc.security.logging.enums;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SeverityTest {

    @Test
    public void testGetSeverityType() {
        assertEquals(Severity.OK.getSeverityType(), 0);
        assertEquals(Severity.WARNING.getSeverityType(), 1);
        assertEquals(Severity.CRITICAL.getSeverityType(), 2);
        assertEquals(Severity.DOWN.getSeverityType(), 3);
        assertEquals(Severity.UNREACHABLE.getSeverityType(), 4);
    }
}
