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


package org.onap.sdc.common.session.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.onap.sdc.common.session.SessionContext;
import org.onap.sdc.common.session.SessionContextProvider;
import org.springframework.stereotype.Service;

@Service
public class SdcSessionContextProvider implements SessionContextProvider {

    private static final InheritableThreadLocal<String> threadUserId = new InheritableThreadLocal<>();
    private static final InheritableThreadLocal<String> threadTenant = new InheritableThreadLocal<>();

    @Override
    public void create(String userId, String tenant) {
        threadUserId.set(userId);
        threadTenant.set(tenant);
    }

    @Override
    public SessionContext get() {
        if (threadUserId.get() == null) {
            throw new SessionException("UserId was not set for this thread");
        }

        if (threadTenant.get() == null) {
            throw new SessionException("Tenant was not set for this thread");
        }

        return new SdcSessionContext(threadUserId.get(), threadTenant.get());
    }

    @Override
    public void close() {
        threadUserId.remove();
        threadTenant.remove();
    }

    @Getter
    @AllArgsConstructor
    private static class SdcSessionContext implements SessionContext {

        private final String userId;
        private final String tenant;
    }
}
