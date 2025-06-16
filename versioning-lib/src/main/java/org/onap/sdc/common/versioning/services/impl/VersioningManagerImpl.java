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

package org.onap.sdc.common.versioning.services.impl;


import static org.onap.sdc.common.versioning.services.types.VersionStatus.Certified;
import static org.onap.sdc.common.versioning.services.types.VersionStatus.Draft;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.onap.sdc.common.versioning.persistence.ItemDao;
import org.onap.sdc.common.versioning.persistence.VersionDao;
import org.onap.sdc.common.versioning.persistence.types.InternalItem;
import org.onap.sdc.common.versioning.persistence.types.InternalVersion;
import org.onap.sdc.common.versioning.services.VersioningManager;
import org.onap.sdc.common.versioning.services.exceptions.VersioningException;
import org.onap.sdc.common.versioning.services.types.Revision;
import org.onap.sdc.common.versioning.services.types.SynchronizationState;
import org.onap.sdc.common.versioning.services.types.Version;
import org.onap.sdc.common.versioning.services.types.VersionCreationMethod;
import org.onap.sdc.common.versioning.services.types.VersionStatus;
import org.springframework.stereotype.Service;

@Service
public class VersioningManagerImpl implements VersioningManager {

    private final ItemDao itemDao;
    private final VersionDao versionDao;
    private final VersionCalculator versionCalculator;

    public VersioningManagerImpl(VersionDao versionDao, VersionCalculator versionCalculator, ItemDao itemDao) {
        this.itemDao = itemDao;
        this.versionDao = versionDao;
        this.versionCalculator = versionCalculator;
    }


    @Override
    public List<Version> list(String itemId) {
        List<InternalVersion> versions = versionDao.list(itemId);
        Set<String> versionsNames = versions.stream().map(Version::getName).collect(Collectors.toSet());
        versions.forEach(version -> versionCalculator.injectAdditionalInfo(version, versionsNames));
        return versions.stream().map(version -> (Version) version).collect(Collectors.toList());
    }

    @Override
    public Version get(String itemId, String versionId) {
        return getVersion(itemId, versionId);
    }

    @Override
    public Version create(String itemId, String baseVersionId, Version version, VersionCreationMethod creationMethod) {
        InternalVersion internalVersion = new InternalVersion();
        internalVersion.setId(version.getId());
        internalVersion.setBaseId(baseVersionId);
        internalVersion.populateExternalFields(version);

        String baseVersionName = null;
        if (baseVersionId != null) {
            Version baseVersion = getVersion(itemId, baseVersionId);
            validateBaseVersion(itemId, baseVersion);
            baseVersionName = baseVersion.getName();
        }
        String versionName = versionCalculator.calculate(baseVersionName, creationMethod);
        validateVersionName(itemId, versionName);
        internalVersion.setName(versionName);

        InternalVersion createdVersion = versionDao.create(itemId, internalVersion);

        updateStatusOnItem(itemId, Draft, null);

        publish(itemId, createdVersion.getId(), String.format("Create version: %s", versionName));
        return createdVersion;
    }

    @Override
    public Version update(String itemId, String versionId, Version version) {
        InternalVersion internalVersion = getVersion(itemId, versionId);
        internalVersion.populateExternalFields(version);
        versionDao.update(itemId, internalVersion);
        return internalVersion;
    }

    @Override
    public void updateStatus(String itemId, String versionId, VersionStatus status, String message) {
        InternalVersion version = getVersion(itemId, versionId);

        VersionStatus prevStatus = version.getStatus();
        if (prevStatus == status) {
            throw new VersioningException(
                String.format("Item %s: update version status failed, version %s is already in status %s", itemId,
                    version.getId(), status));
        }

        version.setStatus(status);
        versionDao.update(itemId, version);

        publish(itemId, versionId, message);

        updateStatusOnItem(itemId, status, prevStatus);
    }

    @Override
    public void publish(String itemId, String versionId, String message) {
        versionDao.publish(itemId, versionId, message);
    }

    @Override
    public void sync(String itemId, String versionId) {
        versionDao.sync(itemId, versionId);
    }

    @Override
    public void forceSync(String itemId, String versionId) {
        versionDao.forceSync(itemId, versionId);
    }

    @Override
    public void revert(String itemId, String versionId, String revisionId) {
        versionDao.revert(itemId, versionId, revisionId);
    }

    @Override
    public List<Revision> listRevisions(String itemId, String versionId) {
        return versionDao.listRevisions(itemId, versionId);
    }

    @Override
    public void clean(String itemId, String versionId) {
        versionDao.clean(itemId, versionId);
    }

    private InternalVersion getVersion(String itemId, String versionId) {
        return versionDao.get(itemId, versionId)
            .map(retrievedVersion -> getUpdateRetrievedVersion(itemId, retrievedVersion))
            .orElseGet(() -> getSyncedVersion(itemId, versionId));
    }

    private InternalVersion getUpdateRetrievedVersion(String itemId, InternalVersion version) {
        if (version.getStatus() == Certified
            && version.getState().getSynchronizationState() == SynchronizationState.OutOfSync) {
            forceSync(itemId, version.getId());
            version = versionDao.get(itemId, version.getId()).orElseThrow(() -> new IllegalStateException(
                "Get version after a successful force sync must return the version"));
        }
        return version;
    }

    private InternalVersion getSyncedVersion(String itemId, String versionId) {
        sync(itemId, versionId);
        return versionDao.get(itemId, versionId).orElseThrow(
            () -> new IllegalStateException("Get version after a successful sync must return the version"));
    }

    private void validateBaseVersion(String itemId, Version baseVersion) {
        if (Certified != baseVersion.getStatus()) {
            throw new VersioningException(
                String.format("Item %s: base version %s must be Certified", itemId, baseVersion.getId()));
        }
    }

    private void validateVersionName(String itemId, String versionName) {
        if (versionDao.list(itemId).stream().anyMatch(version -> versionName.equals(version.getName()))) {
            throw new VersioningException(
                String.format("Item %s: create version failed, a version with the name %s already exist", itemId,
                    versionName));
        }
    }

    private void updateStatusOnItem(String itemId, VersionStatus addedVersionStatus,
        VersionStatus removedVersionStatus) {
        InternalItem item = itemDao.get(itemId);
        if (item == null) {
            throw new VersioningException(String.format("Item with Id %s does not exist", itemId));
        }
        item.addVersionStatus(addedVersionStatus);
        if (removedVersionStatus != null) {
            item.removeVersionStatus(removedVersionStatus);
        }
        itemDao.update(item);
    }
}
