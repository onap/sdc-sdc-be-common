package org.onap.sdc.common.versioning.persistence.types;

import java.util.Date;
import org.onap.sdc.common.versioning.services.types.Version;
import org.onap.sdc.common.versioning.services.types.VersionState;
import org.onap.sdc.common.versioning.services.types.VersionStatus;

public class InternalVersion extends Version {

    public void setId(String id) {
        this.id = id;
    }

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
        setDescription(version.getDescription());
        version.getProperties().forEach(this::addProperty);
    }
}
