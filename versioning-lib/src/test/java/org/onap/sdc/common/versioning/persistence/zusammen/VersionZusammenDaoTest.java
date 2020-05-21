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

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import com.amdocs.zusammen.datatypes.Id;
import com.amdocs.zusammen.datatypes.SessionContext;
import com.amdocs.zusammen.datatypes.UserInfo;
import com.amdocs.zusammen.datatypes.item.Info;
import com.amdocs.zusammen.datatypes.item.ItemVersion;
import com.amdocs.zusammen.datatypes.item.ItemVersionData;
import com.amdocs.zusammen.datatypes.item.ItemVersionStatus;
import com.amdocs.zusammen.datatypes.item.SynchronizationStatus;
import com.amdocs.zusammen.datatypes.itemversion.ItemVersionRevisions;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.onap.sdc.common.versioning.persistence.types.InternalVersion;
import org.onap.sdc.common.versioning.services.types.Revision;
import org.onap.sdc.common.versioning.services.types.VersionStatus;
import org.onap.sdc.common.zusammen.services.ZusammenAdaptor;

public class VersionZusammenDaoTest {

    private static final String STATUS_PROPERTY = "status";
    private static final SessionContext SESSION_CONTEXT = new SessionContext();

    static {
        SESSION_CONTEXT.setUser(new UserInfo("user"));
        SESSION_CONTEXT.setTenant("tenant");
    }

    private final ZusammenSessionContextCreator contextCreatorMock = Mockito.mock(ZusammenSessionContextCreator.class);
    private final ZusammenAdaptor zusammenAdaptorMock = Mockito.mock(ZusammenAdaptor.class);
    @InjectMocks
    private VersionZusammenDao versionDao;

    @BeforeEach
    public void mockSessionContext() {
        MockitoAnnotations.initMocks(this);
        doReturn(SESSION_CONTEXT).when(contextCreatorMock).create();

    }

    @Test
    public void testListWhenNone() {
        String itemId = "itemId";

        doReturn(new ArrayList<>()).when(zusammenAdaptorMock)
                .listPublicVersions(eq(SESSION_CONTEXT), eq(new Id(itemId)));

        List<InternalVersion> versions = versionDao.list(itemId);

        Assert.assertTrue(versions.isEmpty());
    }

    @Test
    public void testList() {
        String itemId = "itemId";
        Id versionId1 = new Id("v1_id");
        Id versionId2 = new Id("v2_id");
        Id versionId3 = new Id("v3_id");

        List<ItemVersion> zusammenVersions =
                Stream.of(createZusammenVersion(versionId1, null, "version desc", "1.0", VersionStatus.Certified),
                        createZusammenVersion(versionId2, versionId1, "version desc", "2.0", VersionStatus.Certified),
                        createZusammenVersion(versionId3, versionId2, "version desc", "3.0", VersionStatus.Draft))
                        .collect(Collectors.toList());
        doReturn(zusammenVersions).when(zusammenAdaptorMock)
                .listPublicVersions(eq(SESSION_CONTEXT), eq(new Id(itemId)));

        List<InternalVersion> versions = versionDao.list(itemId);
        Assert.assertEquals(3, versions.size());

        int zusammenVersionIndex;
        for (InternalVersion version : versions) {
            zusammenVersionIndex = versionId1.getValue().equals(version.getId()) ? 0 :
                                           versionId2.getValue().equals(version.getId()) ? 1 : 2;
            assetVersionEquals(version, zusammenVersions.get(zusammenVersionIndex), null);
        }
    }

    @Test
    public void testCreate() {
        testCreate(null, null);
    }

    @Test
    public void testCreateBasedOn() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("key", "value");
        testCreate("baseId", properties);
    }

    private void testCreate(String baseId, Map<String, Object> properties) {
        String itemId = "itemId";
        InternalVersion version = new InternalVersion();
        version.setBaseId(baseId);
        version.setName("1.0");
        version.setDescription("version description");
        version.setStatus(VersionStatus.Draft);
        if (properties != null) {
            properties.forEach(version::addProperty);
        }
        ArgumentCaptor<ItemVersionData> capturedZusammenVersion = ArgumentCaptor.forClass(ItemVersionData.class);

        String versionId = "versionId";
        doReturn(new Id(versionId)).when(zusammenAdaptorMock)
                .createVersion(eq(SESSION_CONTEXT), eq(new Id(itemId)), baseId == null ? isNull() : eq(new Id(baseId)),
                        capturedZusammenVersion.capture());


        versionDao.create(itemId, version);

        Assert.assertEquals(version.getId(), versionId);

        Info capturedInfo = capturedZusammenVersion.getValue().getInfo();
        Assert.assertEquals(capturedInfo.getName(), version.getName());
        Assert.assertEquals(capturedInfo.getDescription(), version.getDescription());
        Assert.assertEquals(VersionStatus.valueOf(capturedInfo.getProperty(STATUS_PROPERTY)), version.getStatus());
        String capturedInfoProperty = capturedInfo.getProperty("key");
        Assert.assertEquals(capturedInfoProperty, version.getProperty("key"));
    }

    @Test
    public void testCreateWithId() {
        String itemId = "itemId";
        String versionId = "versionId";
        InternalVersion version = new InternalVersion();
        version.setId(versionId);
        version.setName("1.0");
        version.setDescription("version description");
        version.setStatus(VersionStatus.Draft);

        ArgumentCaptor<ItemVersionData> capturedZusammenVersion = ArgumentCaptor.forClass(ItemVersionData.class);

        doReturn(new Id(versionId)).when(zusammenAdaptorMock)
                .createVersion(eq(SESSION_CONTEXT), eq(new Id(itemId)), eq(new Id(version.getId())), isNull(),
                        capturedZusammenVersion.capture());

        versionDao.create(itemId, version);

        Assert.assertEquals(version.getId(), versionId);

        Info capturedInfo = capturedZusammenVersion.getValue().getInfo();
        Assert.assertEquals(capturedInfo.getName(), version.getName());
        Assert.assertEquals(capturedInfo.getDescription(), version.getDescription());
        Assert.assertEquals(VersionStatus.valueOf(capturedInfo.getProperty(STATUS_PROPERTY)), version.getStatus());
    }

    @Test
    public void testUpdate() {
        String itemId = "itemId";
        InternalVersion version = new InternalVersion();
        version.setId("versionId");
        version.setBaseId("baseId");
        version.setName("1.0");
        version.setDescription("version description");
        version.setStatus(VersionStatus.Certified);

        ArgumentCaptor<ItemVersionData> capturedZusammenVersion = ArgumentCaptor.forClass(ItemVersionData.class);

        versionDao.update(itemId, version);

        verify(zusammenAdaptorMock).updateVersion(eq(SESSION_CONTEXT), eq(new Id(itemId)), eq(new Id(version.getId())),
                capturedZusammenVersion.capture());

        Info capturedInfo = capturedZusammenVersion.getValue().getInfo();
        Assert.assertEquals(capturedInfo.getName(), version.getName());
        Assert.assertEquals(capturedInfo.getDescription(), version.getDescription());
        Assert.assertEquals(VersionStatus.valueOf(capturedInfo.getProperty(STATUS_PROPERTY)), version.getStatus());
    }

    @Test
    public void testGetNonExisting() {
        Optional<InternalVersion> version = versionDao.get("itemId", "versionId");

        Assert.assertEquals(version, Optional.empty());
    }

    @Test
    public void testGetSynced() {
        String itemId = "itemId";
        String versionId = "versionId";

        SessionContext zusammenContext = SESSION_CONTEXT;
        Id itemIdObj = new Id(itemId);
        Id versionIdObj = new Id(versionId);

        ItemVersion zusammenPrivateVersion =
                createZusammenVersion(versionIdObj, new Id("baseId"), "version desc  updated", "2.0",
                        VersionStatus.Draft);
        doReturn(zusammenPrivateVersion).when(zusammenAdaptorMock)
                .getVersion(eq(zusammenContext), eq(itemIdObj), eq(versionIdObj));

        ItemVersionStatus zusammenVersionStatus = new ItemVersionStatus(SynchronizationStatus.UP_TO_DATE, true);
        doReturn(zusammenVersionStatus).when(zusammenAdaptorMock)
                .getVersionStatus(eq(zusammenContext), eq(itemIdObj), eq(versionIdObj));

        Optional<InternalVersion> version = versionDao.get(itemId, versionId);

        Assert.assertTrue(version.isPresent());
        assetVersionEquals(version.get(), zusammenPrivateVersion, zusammenVersionStatus);
    }

    @Test
    public void testGetOutOfSync() {
        String itemId = "itemId";
        String versionId = "versionId";

        SessionContext zusammenContext = SESSION_CONTEXT;
        Id itemIdObj = new Id(itemId);
        Id versionIdObj = new Id(versionId);

        ItemVersion zusammenPrivateVersion =
                createZusammenVersion(versionIdObj, new Id("baseId"), "version desc updated", "2.0",
                        VersionStatus.Draft);
        doReturn(zusammenPrivateVersion).when(zusammenAdaptorMock)
                .getVersion(eq(zusammenContext), eq(itemIdObj), eq(versionIdObj));

        ItemVersionStatus zusammenVersionStatus = new ItemVersionStatus(SynchronizationStatus.OUT_OF_SYNC, true);
        doReturn(zusammenVersionStatus).when(zusammenAdaptorMock)
                .getVersionStatus(eq(zusammenContext), eq(itemIdObj), eq(versionIdObj));

        VersionStatus statusOnPublic = VersionStatus.Certified;
        ItemVersion zusammenPublicVersion =
                createZusammenVersion(versionIdObj, new Id("baseId"), "version desc", "2.0", statusOnPublic);
        doReturn(zusammenPublicVersion).when(zusammenAdaptorMock)
                .getPublicVersion(eq(zusammenContext), eq(itemIdObj), eq(versionIdObj));

        Optional<InternalVersion> version = versionDao.get(itemId, versionId);

        Assert.assertTrue(version.isPresent());
        zusammenPrivateVersion.getData().getInfo().addProperty(STATUS_PROPERTY, statusOnPublic.name());
        assetVersionEquals(version.get(), zusammenPrivateVersion, zusammenVersionStatus);
    }

    @Test
    public void testGetMerging() {
        String itemId = "itemId";
        String versionId = "versionId";

        SessionContext zusammenContext = SESSION_CONTEXT;
        Id itemIdObj = new Id(itemId);
        Id versionIdObj = new Id(versionId);

        ItemVersion zusammenPrivateVersion =
                createZusammenVersion(versionIdObj, new Id("baseId"), "version desc", "2.0", VersionStatus.Draft);
        doReturn(zusammenPrivateVersion).when(zusammenAdaptorMock)
                .getVersion(eq(zusammenContext), eq(itemIdObj), eq(versionIdObj));

        ItemVersionStatus zusammenVersionStatus = new ItemVersionStatus(SynchronizationStatus.MERGING, true);
        doReturn(zusammenVersionStatus).when(zusammenAdaptorMock)
                .getVersionStatus(eq(zusammenContext), eq(itemIdObj), eq(versionIdObj));

        ItemVersion zusammenPublicVersion =
                createZusammenVersion(versionIdObj, new Id("baseId"), "version desc", "2.0", VersionStatus.Draft);
        doReturn(zusammenPublicVersion).when(zusammenAdaptorMock)
                .getPublicVersion(eq(zusammenContext), eq(itemIdObj), eq(versionIdObj));

        Optional<InternalVersion> version = versionDao.get(itemId, versionId);

        Assert.assertTrue(version.isPresent());
        assetVersionEquals(version.get(), zusammenPrivateVersion, zusammenVersionStatus);
    }

    @Test
    public void testPublish() {
        String itemId = "itemId";
        String versionId = "versionId";
        String message = "publish message";

        versionDao.publish(itemId, versionId, message);

        verify(zusammenAdaptorMock)
                .publishVersion(eq(SESSION_CONTEXT), eq(new Id(itemId)), eq(new Id(versionId)), eq(message));
    }

    @Test
    public void testSync() {
        String itemId = "itemId";
        String versionId = "versionId";

        versionDao.sync(itemId, versionId);

        verify(zusammenAdaptorMock).syncVersion(eq(SESSION_CONTEXT), eq(new Id(itemId)), eq(new Id(versionId)));
    }

    @Test
    public void testForceSync() {
        String itemId = "itemId";
        String versionId = "versionId";

        versionDao.forceSync(itemId, versionId);

        verify(zusammenAdaptorMock).forceSyncVersion(eq(SESSION_CONTEXT), eq(new Id(itemId)), eq(new Id(versionId)));
    }

    @Test
    public void testRevert() {
        String itemId = "itemId";
        String versionId = "versionId";
        String revisionId = "revisionId";

        versionDao.revert(itemId, versionId, revisionId);

        verify(zusammenAdaptorMock)
                .revert(eq(SESSION_CONTEXT), eq(new Id(itemId)), eq(new Id(versionId)), eq(new Id(revisionId)));
    }

    @Test
    public void testListRevisionsWhenNone() {
        String itemId = "itemId";
        String versionId = "versionId";

        List<Revision> revisions = versionDao.listRevisions(itemId, versionId);

        Assert.assertTrue(revisions.isEmpty());
    }

    @Test
    public void testListRevisions() {
        String itemId = "itemId";
        String versionId = "versionId";

        long currentTime = System.currentTimeMillis();
        Date rev4time = new Date(currentTime);            // latest
        Date rev3time = new Date(currentTime - 1);
        Date rev2time = new Date(currentTime - 2);
        Date rev1time = new Date(currentTime - 3);  // oldest
        List<com.amdocs.zusammen.datatypes.itemversion.Revision> zusammenRevisions =
                Stream.of(createZusammenRevision("rev4", "forth rev", "user1", rev4time),
                        createZusammenRevision("rev1", "first rev", "user2", rev1time),
                        createZusammenRevision("rev3", "third rev", "user2", rev3time),
                        createZusammenRevision("rev2", "second rev", "user1", rev2time)).collect(Collectors.toList());
        ItemVersionRevisions toBeReturned = new ItemVersionRevisions();
        toBeReturned.setItemVersionRevisions(zusammenRevisions);
        doReturn(toBeReturned).when(zusammenAdaptorMock)
                .listRevisions(eq(SESSION_CONTEXT), eq(new Id(itemId)), eq(new Id(versionId)));

        List<Revision> revisions = versionDao.listRevisions(itemId, versionId);

        Assert.assertEquals(4, revisions.size());
        assertRevisionEquals(revisions.get(0), zusammenRevisions.get(0)); // rev4 - latest
        assertRevisionEquals(revisions.get(1), zusammenRevisions.get(2)); // rev3
        assertRevisionEquals(revisions.get(2), zusammenRevisions.get(3)); // rev2
        assertRevisionEquals(revisions.get(3), zusammenRevisions.get(1)); // rev1 - oldest
    }

    private ItemVersion createZusammenVersion(Id id, Id baseId, String description, String name, VersionStatus status) {
        ItemVersion version = new ItemVersion();
        version.setId(id);
        version.setBaseId(baseId);
        Info info = new Info();
        info.setName(name);
        info.setDescription(description);
        info.addProperty(STATUS_PROPERTY, status.name());
        ItemVersionData data = new ItemVersionData();
        data.setInfo(info);
        version.setData(data);
        version.setCreationTime(new Date());
        version.setModificationTime(new Date());
        return version;
    }

    private void assetVersionEquals(InternalVersion version, ItemVersion zusammenVersion,
            ItemVersionStatus zusammenVersionStatus) {
        Assert.assertEquals(version.getId(), zusammenVersion.getId().getValue());
        Assert.assertEquals(version.getBaseId(),
                zusammenVersion.getBaseId() == null ? null : zusammenVersion.getBaseId().getValue());
        Info info = zusammenVersion.getData().getInfo();
        Assert.assertEquals(version.getName(), info.getName());
        Assert.assertEquals(version.getDescription(), info.getDescription());
        Assert.assertEquals(version.getStatus(), VersionStatus.valueOf(info.getProperty(STATUS_PROPERTY)));
        Assert.assertEquals(version.getCreationTime(), zusammenVersion.getCreationTime());
        Assert.assertEquals(version.getModificationTime(), zusammenVersion.getModificationTime());

        if (zusammenVersionStatus != null) {
            Assert.assertEquals(version.getState().isDirty(), zusammenVersionStatus.isDirty());
            Assert.assertEquals(version.getState().getSynchronizationState().toString(),
                    zusammenVersionStatus.getSynchronizationStatus().toString());
        }
    }

    private com.amdocs.zusammen.datatypes.itemversion.Revision createZusammenRevision(String id, String message,
            String user, Date time) {
        com.amdocs.zusammen.datatypes.itemversion.Revision revision =
                new com.amdocs.zusammen.datatypes.itemversion.Revision();
        revision.setRevisionId(new Id(id));
        revision.setMessage(message);
        revision.setUser(user);
        revision.setTime(time);
        return revision;
    }

    private void assertRevisionEquals(Revision revision,
            com.amdocs.zusammen.datatypes.itemversion.Revision zusammenRevision) {
        Assert.assertEquals(revision.getId(), zusammenRevision.getRevisionId().getValue());
        Assert.assertEquals(revision.getMessage(), zusammenRevision.getMessage());
        Assert.assertEquals(revision.getUser(), zusammenRevision.getUser());
        Assert.assertEquals(revision.getTime(), zusammenRevision.getTime());
    }
}
