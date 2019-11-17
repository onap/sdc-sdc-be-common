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


import com.amdocs.zusammen.datatypes.Id;
import com.amdocs.zusammen.datatypes.SessionContext;
import com.amdocs.zusammen.datatypes.item.Info;
import com.amdocs.zusammen.datatypes.item.ItemVersion;
import com.amdocs.zusammen.datatypes.item.ItemVersionData;
import com.amdocs.zusammen.datatypes.item.ItemVersionStatus;
import com.amdocs.zusammen.datatypes.item.SynchronizationStatus;
import com.amdocs.zusammen.datatypes.itemversion.ItemVersionRevisions;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.onap.sdc.common.versioning.persistence.VersionDao;
import org.onap.sdc.common.versioning.persistence.types.InternalVersion;
import org.onap.sdc.common.versioning.services.exceptions.VersioningException;
import org.onap.sdc.common.versioning.services.types.Revision;
import org.onap.sdc.common.versioning.services.types.SynchronizationState;
import org.onap.sdc.common.versioning.services.types.VersionState;
import org.onap.sdc.common.versioning.services.types.VersionStatus;
import org.onap.sdc.common.zusammen.services.ZusammenAdaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class VersionZusammenDao implements VersionDao {

    private static final String STATUS_PROPERTY = "status";

    private final ZusammenSessionContextCreator contextCreator;
    private final ZusammenAdaptor zusammenAdaptor;

    @Autowired
    public VersionZusammenDao(ZusammenSessionContextCreator contextCreator, ZusammenAdaptor zusammenAdaptor) {
        this.contextCreator = contextCreator;
        this.zusammenAdaptor = zusammenAdaptor;
    }

    @Override
    public List<InternalVersion> list(String itemId) {
        return zusammenAdaptor.listPublicVersions(contextCreator.create(), new Id(itemId)).stream()
                       .map(VersionZusammenDao::convertFromZusammen).collect(Collectors.toList());
    }

    @Override
    public InternalVersion create(String itemId, InternalVersion version) {
        Id versionId = zusammenAdaptor.createVersion(contextCreator.create(), new Id(itemId),
                version.getBaseId() == null ? null : new Id(version.getBaseId()), mapToZusammenVersion(version));

        version.setId(versionId.getValue());
        return version;
    }

    @Override
    public void update(String itemId, InternalVersion version) {
        zusammenAdaptor.updateVersion(contextCreator.create(), new Id(itemId), new Id(version.getId()),
                mapToZusammenVersion(version));
    }

    @Override
    public Optional<InternalVersion> get(String itemId, String versionId) {
        SessionContext context = contextCreator.create();
        Id itemIdObj = new Id(itemId);
        Id versionIdObj = new Id(versionId);
        ItemVersion itemVersion = zusammenAdaptor.getVersion(context, itemIdObj, versionIdObj);

        if (itemVersion == null) {
            return Optional.empty();
        }

        VersionState versionState = convertState(zusammenAdaptor.getVersionStatus(context, itemIdObj, versionIdObj));
        updateVersionStatus(context, itemIdObj, versionIdObj, versionState, itemVersion);

        InternalVersion result = convertFromZusammen(itemVersion);
        result.setState(versionState);
        return Optional.of(result);
    }

    @Override
    public void delete(String itemId, String versionId) {
        throw new UnsupportedOperationException("Delete version operation is not yet supported.");
    }

    @Override
    public void publish(String itemId, String versionId, String message) {
        zusammenAdaptor.publishVersion(contextCreator.create(), new Id(itemId), new Id(versionId), message);
    }

    @Override
    public void sync(String itemId, String versionId) {
        zusammenAdaptor.syncVersion(contextCreator.create(), new Id(itemId), new Id(versionId));
    }

    @Override
    public void forceSync(String itemId, String versionId) {
        zusammenAdaptor.forceSyncVersion(contextCreator.create(), new Id(itemId), new Id(versionId));
    }

    @Override
    public void clean(String itemId, String versionId) {
        zusammenAdaptor.cleanVersion(contextCreator.create(), new Id(itemId), new Id(versionId));
    }

    @Override
    public void revert(String itemId, String versionId, String revisionId) {
        zusammenAdaptor.revert(contextCreator.create(), new Id(itemId), new Id(versionId), new Id(revisionId));
    }

    @Override
    public List<Revision> listRevisions(String itemId, String versionId) {
        ItemVersionRevisions itemVersionRevisions =
                zusammenAdaptor.listRevisions(contextCreator.create(), new Id(itemId), new Id(versionId));

        return itemVersionRevisions == null || itemVersionRevisions.getItemVersionRevisions() == null
                       || itemVersionRevisions.getItemVersionRevisions().isEmpty() ? new ArrayList<>() :
                       itemVersionRevisions.getItemVersionRevisions().stream().map(this::convertRevision)
                               .sorted(this::compareRevisionsTime).collect(Collectors.toList());
    }

    private void updateVersionStatus(SessionContext context, Id itemId, Id versionId, VersionState versionState,
            ItemVersion itemVersion) {
        if (versionState.getSynchronizationState() != SynchronizationState.UpToDate) {
            String versionStatus = zusammenAdaptor.getPublicVersion(context, itemId, versionId).getData().getInfo()
                                           .getProperty(STATUS_PROPERTY);
            itemVersion.getData().getInfo().addProperty(STATUS_PROPERTY, versionStatus);
        }
    }

    private ItemVersionData mapToZusammenVersion(InternalVersion version) {
        Info info = new Info();
        info.addProperty(STATUS_PROPERTY, version.getStatus().name());
        info.setName(version.getName());
        info.setDescription(version.getDescription());
        version.getProperties().forEach(info::addProperty);
        ItemVersionData itemVersionData = new ItemVersionData();
        itemVersionData.setInfo(info);
        return itemVersionData;
    }

    private VersionState convertState(ItemVersionStatus versionStatus) {
        VersionState state = new VersionState();
        state.setSynchronizationState(getSyncState(versionStatus.getSynchronizationStatus()));
        state.setDirty(versionStatus.isDirty());
        return state;
    }

    private SynchronizationState getSyncState(SynchronizationStatus synchronizationStatus) {
        switch (synchronizationStatus) {
            case UP_TO_DATE:
                return SynchronizationState.UpToDate;
            case OUT_OF_SYNC:
                return SynchronizationState.OutOfSync;
            case MERGING:
                return SynchronizationState.Merging;
            default:
                throw new VersioningException("Version state is unknown");
        }
    }

    private Revision convertRevision(com.amdocs.zusammen.datatypes.itemversion.Revision zusammenRevision) {
        Revision revision = new Revision();
        revision.setId(zusammenRevision.getRevisionId().getValue());
        revision.setTime(zusammenRevision.getTime());
        revision.setUser(zusammenRevision.getUser());
        revision.setMessage(zusammenRevision.getMessage());
        return revision;
    }

    private int compareRevisionsTime(Revision revision1, Revision revision2) {
        return revision1.getTime().before(revision2.getTime()) ? 1 : -1;
    }

    private static InternalVersion convertFromZusammen(ItemVersion itemVersion) {
        if (itemVersion == null) {
            return null;
        }
        InternalVersion version = new InternalVersion();
        version.setId(itemVersion.getId().getValue());
        if (itemVersion.getBaseId() != null) {
            version.setBaseId(itemVersion.getBaseId().getValue());
        }
        version.setName(itemVersion.getData().getInfo().getName());
        version.setDescription(itemVersion.getData().getInfo().getDescription());
        version.setCreationTime(itemVersion.getCreationTime());
        version.setModificationTime(itemVersion.getModificationTime());

        itemVersion.getData().getInfo().getProperties()
                .forEach((key, value) -> addPropertyToVersion(key, value, version));

        return version;
    }

    private static void addPropertyToVersion(String propertyKey, Object propertyValue, InternalVersion version) {
        if (STATUS_PROPERTY.equals(propertyKey)) {
            version.setStatus(VersionStatus.valueOf((String) propertyValue));
        } else {
            version.addProperty(propertyKey, propertyValue);
        }
    }
}
