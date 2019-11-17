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

package org.onap.sdc.common.versioning.persistence.zusammen;

import com.amdocs.zusammen.datatypes.SessionContext;
import com.amdocs.zusammen.datatypes.UserInfo;
import org.onap.sdc.common.session.SessionContextProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ZusammenSessionContextCreator {

    private final SessionContextProvider sessionContextProvider;

    @Autowired
    public ZusammenSessionContextCreator(SessionContextProvider sessionContextProvider) {
        this.sessionContextProvider = sessionContextProvider;
    }


    public SessionContext create() {
        org.onap.sdc.common.session.SessionContext sdcSessionContext = sessionContextProvider.get();

        SessionContext sessionContext = new SessionContext();
        sessionContext.setUser(new UserInfo(sdcSessionContext.getUserId()));
        sessionContext.setTenant(sdcSessionContext.getTenant());
        return sessionContext;
    }
}
