/*
 *
 *  Copyright © 2017-2018 European Support Limited
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  * Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.onap.sdc.common.versioning.persistence.types;

import java.util.Date;
import java.util.EnumMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.onap.sdc.common.versioning.services.types.Item;
import org.onap.sdc.common.versioning.services.types.ItemStatus;
import org.onap.sdc.common.versioning.services.types.VersionStatus;


@Setter
@Getter
public class InternalItem extends Item {

    private Map<VersionStatus, Integer> versionStatusCounters = new EnumMap<>(VersionStatus.class);

    public void setId(String id) {
        this.id = id;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public void setModificationTime(Date modificationTime) {
        this.modificationTime = modificationTime;
    }

    public void setStatus(ItemStatus status) {
        this.status = status;
    }

    public void addVersionStatus(VersionStatus versionStatus) {
        Integer counter = versionStatusCounters.get(versionStatus);
        versionStatusCounters.put(versionStatus, counter == null ? 1 : counter + 1);
    }

    public void removeVersionStatus(VersionStatus versionStatus) {
        Integer counter = versionStatusCounters.get(versionStatus);
        if (counter == null) {
            return;
        }
        if (counter == 1) {
            versionStatusCounters.remove(versionStatus);
        } else {
            versionStatusCounters.put(versionStatus, counter - 1);
        }
    }

    public void populateExternalFields(Item item) {
        setType(item.getType());
        setName(item.getName());
        setOwner(item.getOwner());
        setDescription(item.getDescription());
        item.getProperties().forEach(this::addProperty);
    }
}
