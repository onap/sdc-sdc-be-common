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

package org.onap.sdc.common.zusammen.services.impl;

import com.amdocs.zusammen.adaptor.inbound.api.types.item.ZusammenElement;
import com.amdocs.zusammen.datatypes.Id;
import com.amdocs.zusammen.datatypes.item.Action;
import org.junit.jupiter.api.Test;
import org.onap.sdc.common.zusammen.services.ZusammenElementUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ZusammenElementUtilTest {

    private static final String ELEMENT_ID = "1";
    private static final String ELEMENT_TYPE = "Type";

    @Test
    public void testBuildElement() {

        ZusammenElement zusammenElement =
                ZusammenElementUtil.buildElement(new Id(ELEMENT_ID), Action.CREATE);
        assertEquals(ELEMENT_ID, zusammenElement.getElementId().getValue());
        assertEquals(Action.CREATE, zusammenElement.getAction());
    }

    @Test
    public void testBuildStructuralElement() {

        ZusammenElement zusammenElement =
                ZusammenElementUtil.buildStructuralElement(ELEMENT_TYPE, Action.CREATE);
        assertEquals(ELEMENT_TYPE, zusammenElement.getInfo().getName());
    }

}
