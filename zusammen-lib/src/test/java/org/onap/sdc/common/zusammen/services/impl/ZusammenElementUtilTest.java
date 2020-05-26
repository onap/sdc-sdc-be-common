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

import static org.junit.Assert.assertEquals;

public class ZusammenElementUtilTest {

    @Test
    public void testBuildElement() {

        ZusammenElement zusammenElement =
            ZusammenElementUtil.buildElement(new Id("1"), Action.CREATE);
        assertEquals(zusammenElement.getElementId().getValue(), "1");
        assertEquals(zusammenElement.getAction(), Action.CREATE);
    }

    @Test
    public void testBuildStructuralElement() {

        ZusammenElement zusammenElement =
            ZusammenElementUtil.buildStructuralElement("Type", Action.CREATE);
        assertEquals(zusammenElement.getInfo().getName(), "Type");
    }

}
