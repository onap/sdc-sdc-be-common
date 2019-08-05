package org.onap.sdc.common.versioning.persistence;

import java.util.List;
import java.util.Optional;
import org.onap.sdc.common.versioning.persistence.types.InternalVersion;
import org.onap.sdc.common.versioning.services.types.Revision;

public interface VersionDao {

    List<InternalVersion> list(String itemId);

    InternalVersion create(String itemId, InternalVersion version);

    void update(String itemId, InternalVersion version);

    Optional<InternalVersion> get(String itemId, String versionId);

    void delete(String itemId, String versionId);

    void publish(String itemId, String versionId, String message);

    void sync(String itemId, String versionId);

    void forceSync(String itemId, String versionId);

    void clean(String itemId, String versionId);

    void revert(String itemId, String versionId, String revisionId);

    List<Revision> listRevisions(String itemId, String versionId);
}
