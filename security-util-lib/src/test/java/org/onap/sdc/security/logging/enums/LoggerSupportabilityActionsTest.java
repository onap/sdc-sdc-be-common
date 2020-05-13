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

public class LoggerSupportabilityActionsTest {

    @Test
    public void testGetName() {
        assertEquals(LoggerSupportabilityActions.ARCHIVE.getName(), "ARCHIVE");
        assertEquals(LoggerSupportabilityActions.ASSOCIATE_RI_TO_RI.getName(),
            "ASSOCIATE RI TO RI");
        assertEquals(LoggerSupportabilityActions.CHANGELIFECYCLESTATE.getName(),
            "CHANGE LIFECYCLE STATE");
        assertEquals(LoggerSupportabilityActions.CREATE_ARTIFACTS.getName(), "ADD ARTIFACTS");
        assertEquals(LoggerSupportabilityActions.CREATE_CAPABILITY_REQUIREMENTS.getName(),
            "CREATE CAPABILITY REQUIREMENTS");
        assertEquals(LoggerSupportabilityActions.CREATE_GROUP_POLICY.getName(), "ADD GROUP POLICY");
        assertEquals(LoggerSupportabilityActions.CREATE_GROUPS.getName(), "ADD GROUPS");



    }
}
