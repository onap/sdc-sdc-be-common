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

package org.onap.sdc.common.versioning.persistence.types;

import java.util.Date;
import org.onap.sdc.common.versioning.services.types.Version;
import org.onap.sdc.common.versioning.services.types.VersionState;
import org.onap.sdc.common.versioning.services.types.VersionStatus;

public class InternalVersion extends Version {

    public void setBaseId(String baseId) {
        this.baseId = baseId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(VersionStatus status) {
        this.status = status;
    }

    public void setState(VersionState state) {
        this.state = state;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public void setModificationTime(Date modificationTime) {
        this.modificationTime = modificationTime;
    }

    public void populateExternalFields(Version version) {
        setId(version.getId());
        setDescription(version.getDescription());
        version.getProperties().forEach(this::addProperty);
    }
}
