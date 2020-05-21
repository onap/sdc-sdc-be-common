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

import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class EcompLoggerErrorCodeTest {

    @Test
    public void getByValueTest() {

        EcompLoggerErrorCode ecompLoggerErrorCode = EcompLoggerErrorCode.getByValue("E_0");
        Assert.assertEquals(0, ecompLoggerErrorCode.getErrorCode());
        ecompLoggerErrorCode = EcompLoggerErrorCode.getByValue("E_100");
        Assert.assertEquals(100, ecompLoggerErrorCode.getErrorCode());
        ecompLoggerErrorCode = EcompLoggerErrorCode.getByValue("E_200");
        Assert.assertEquals(200, ecompLoggerErrorCode.getErrorCode());
        ecompLoggerErrorCode = EcompLoggerErrorCode.getByValue("E_300");
        Assert.assertEquals(300, ecompLoggerErrorCode.getErrorCode());
        ecompLoggerErrorCode = EcompLoggerErrorCode.getByValue("E_400");
        Assert.assertEquals(400, ecompLoggerErrorCode.getErrorCode());
        ecompLoggerErrorCode = EcompLoggerErrorCode.getByValue("E_500");
        Assert.assertEquals(500, ecompLoggerErrorCode.getErrorCode());
        ecompLoggerErrorCode = EcompLoggerErrorCode.getByValue("none");
        Assert.assertEquals(900, ecompLoggerErrorCode.getErrorCode());

    }


}
