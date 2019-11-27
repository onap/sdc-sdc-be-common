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

package org.onap.sdc.common.session;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.sdc.common.session.impl.SdcSessionContextProvider;
import org.onap.sdc.common.session.impl.SessionException;

public class SdcSessionContextProviderTest {

    private SdcSessionContextProvider sdcSessionContextProvider;

    @Before
    public void init() {
        sdcSessionContextProvider = new SdcSessionContextProvider();
    }

    @Test(expected = SessionException.class)
    public void createSessionNoTenenat() {
        sdcSessionContextProvider.create("user", null);
        sdcSessionContextProvider.get();
    }

    @Test(expected = SessionException.class)
    public void createSessionNoUser() {
        sdcSessionContextProvider.create(null, "t1");
        sdcSessionContextProvider.get();
    }

    @Test
    public void createSessionSuccess() {
        String user = "user";
        String tenent = "tenent";
        sdcSessionContextProvider.create(user, tenent);
        SessionContext sessionContext = sdcSessionContextProvider.get();
        assertEquals(user, sessionContext.getUserId());
        assertEquals(tenent, sessionContext.getTenant());
    }

    @After
    public void cleanup() {
        sdcSessionContextProvider.close();
    }
}
