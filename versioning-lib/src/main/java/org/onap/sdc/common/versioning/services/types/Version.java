/*-
 * ============LICENSE_START=======================================================
 * SDC
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
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
 * ============LICENSE_END=========================================================
 */

package org.onap.sdc.common.versioning.services.types;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class Version {
    @Setter
    private String id;
    protected String baseId;
    protected String name;
    protected VersionStatus status = VersionStatus.Draft;
    protected VersionState state;
    protected Date creationTime;
    protected Date modificationTime;
    @Setter
    private String description;
    private Map<String, Object> properties = new HashMap<>();

    public boolean isNew() {
        return id == null;
    }

    public void addProperty(String key, Object value) {
        properties.put(key, value);
    }

    public <T> T getProperty(String key) {
        return (T) properties.get(key);
    }
}