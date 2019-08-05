/*
 * Copyright © 2016-2018 European Support Limited
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

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import com.amdocs.zusammen.datatypes.Id;
import com.amdocs.zusammen.datatypes.SessionContext;
import com.amdocs.zusammen.datatypes.UserInfo;
import com.amdocs.zusammen.datatypes.item.Info;
import com.amdocs.zusammen.datatypes.item.Item;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.onap.sdc.common.versioning.persistence.types.InternalItem;
import org.onap.sdc.common.versioning.services.types.ItemStatus;
import org.onap.sdc.common.versioning.services.types.VersionStatus;
import org.onap.sdc.common.zusammen.services.ZusammenAdaptor;

@RunWith(MockitoJUnitRunner.class)
public class ItemZusammenDaoTest {

    private static final String ITEM_TYPE = "item_type";
    private static final String ITEM_VERSIONS_STATUSES = "item_versions_statuses";
    private static final String APP_PROP_1 = "app_prop1";
    private static final String APP_PROP_2 = "app_prop2";
    private static final SessionContext SESSION_CONTEXT = new SessionContext();

    static {
        SESSION_CONTEXT.setUser(new UserInfo("user"));
        SESSION_CONTEXT.setTenant("tenant");
    }

    @Mock
    private ZusammenSessionContextCreator contextCreatorMock;
    @Mock
    private ZusammenAdaptor zusammenAdaptorMock;
    @InjectMocks
    private ItemZusammenDao itemDao;

    @Before
    public void mockSessionContext() {
        doReturn(SESSION_CONTEXT).when(contextCreatorMock).create();
    }


    @Test
    public void testListWhenNone() {
        doReturn(new ArrayList<>()).when(zusammenAdaptorMock).listItems(eq(SESSION_CONTEXT));

        Collection<InternalItem> items = itemDao.list();

        Assert.assertTrue(items.isEmpty());
    }

    @Test
    public void testList() {
        Map<String, Number> vlm1versionStatuses = new HashMap<>();
        vlm1versionStatuses.put(VersionStatus.Draft.name(), 1);

        Map<String, Number> vsp2versionStatuses = new HashMap<>();
        vsp2versionStatuses.put(VersionStatus.Draft.name(), 3);
        vsp2versionStatuses.put(VersionStatus.Certified.name(), 2);


        List<Item> returnedItems =
                Stream.of(createItem("1", "vsp1", "vsp 1", "vsp", new Date(), new Date(), new HashMap<>()),
                        createItem("2", "vlm1", "vlm 1", "vlm", new Date(), new Date(), vlm1versionStatuses),
                        createItem("3", "vsp2", "vsp 2", "vsp", new Date(), new Date(), vsp2versionStatuses))
                        .collect(Collectors.toList());
        doReturn(returnedItems).when(zusammenAdaptorMock).listItems(eq(SESSION_CONTEXT));

        Collection<InternalItem> items = itemDao.list();
        assertEquals(items.size(), 3);

        Iterator<InternalItem> itemIterator = items.iterator();
        assertItemEquals(itemIterator.next(), returnedItems.get(0));
        assertItemEquals(itemIterator.next(), returnedItems.get(1));
        assertItemEquals(itemIterator.next(), returnedItems.get(2));
    }

    @Test
    public void testGetNonExisting() {
        InternalItem item = itemDao.get("nonExisting");

        Assert.assertNull(item);
    }

    @Test
    public void testGet() {
        String itemId = "1";

        Map<String, Number> versionStatuses = new HashMap<>();
        versionStatuses.put(VersionStatus.Draft.name(), 3);
        versionStatuses.put(VersionStatus.Certified.name(), 2);

        Item toBeReturned =
                createItem("1", "vsp1", "vsp 1", "vsp", new Date(System.currentTimeMillis() - 100), new Date(),
                        versionStatuses);
        doReturn(toBeReturned).when(zusammenAdaptorMock).getItem(eq(SESSION_CONTEXT), eq(new Id(itemId)));

        InternalItem item = itemDao.get(itemId);

        Assert.assertNotNull(item);
        assertItemEquals(item, toBeReturned);
        assertEquals(item.getStatus(), ItemStatus.ACTIVE);

    }

    @Test
    public void testCreate() {
        InternalItem inputItem = new InternalItem();
        inputItem.setName("vsp1");
        inputItem.setDescription("VSP 1");
        inputItem.setType("vsp");

        ArgumentCaptor<Info> capturedZusammenInfo = ArgumentCaptor.forClass(Info.class);

        String itemId = "1";
        doReturn(new Id(itemId)).when(zusammenAdaptorMock)
                .createItem(eq(SESSION_CONTEXT), capturedZusammenInfo.capture());

        InternalItem item = itemDao.create(inputItem);

        Info capturedInfo = capturedZusammenInfo.getValue();
        assertEquals(capturedInfo.getName(), inputItem.getName());
        assertEquals(capturedInfo.getDescription(), inputItem.getDescription());
        assertEquals(capturedInfo.getProperty(ITEM_TYPE), inputItem.getType());
        assertEquals(capturedInfo.getProperty(ITEM_VERSIONS_STATUSES), inputItem.getVersionStatusCounters());

        assertEquals(item.getId(), itemId);
        assertEquals(item.getName(), inputItem.getName());
        assertEquals(item.getDescription(), inputItem.getDescription());
        assertEquals(item.getType(), inputItem.getType());
        assertEquals(item.getVersionStatusCounters(), inputItem.getVersionStatusCounters());
    }

    @Test
    public void testUpdate() {
        InternalItem item = new InternalItem();
        item.setId("1");
        item.setName("vsp1");
        item.setDescription("VSP 1");
        item.setType("vsp");
        item.addVersionStatus(VersionStatus.Draft);
        item.addVersionStatus(VersionStatus.Draft);
        item.addVersionStatus(VersionStatus.Certified);

        ArgumentCaptor<Info> capturedZusammenInfo = ArgumentCaptor.forClass(Info.class);

        itemDao.update(item);

        verify(zusammenAdaptorMock)
                .updateItem(eq(SESSION_CONTEXT), eq(new Id(item.getId())), capturedZusammenInfo.capture());

        Info capturedInfo = capturedZusammenInfo.getValue();
        assertEquals(capturedInfo.getName(), item.getName());
        assertEquals(capturedInfo.getDescription(), item.getDescription());
        assertEquals(capturedInfo.getProperty(ITEM_TYPE), item.getType());
        assertEquals(capturedInfo.getProperty(ITEM_VERSIONS_STATUSES), item.getVersionStatusCounters());
    }

    private Item createItem(String id, String name, String description, String type, Date creationTime,
            Date modificationTime, Map<String, Number> versionStatusCounters) {
        Item item = new Item();
        item.setId(new Id(id));
        Info info = new Info();
        info.setName(name);
        info.setDescription(description);
        info.addProperty(ITEM_TYPE, type);
        info.addProperty(ITEM_VERSIONS_STATUSES, versionStatusCounters);
        info.addProperty(APP_PROP_1, "app_prop1_value");
        info.addProperty(APP_PROP_2, 8);
        item.setInfo(info);
        item.setCreationTime(creationTime);
        item.setModificationTime(modificationTime);
        return item;
    }

    private void assertItemEquals(InternalItem item, Item zusammenItem) {
        assertEquals(item.getId(), zusammenItem.getId().getValue());
        assertEquals(item.getName(), zusammenItem.getInfo().getName());
        assertEquals(item.getDescription(), zusammenItem.getInfo().getDescription());
        assertEquals(item.getType(), zusammenItem.getInfo().getProperty(ITEM_TYPE));
        assertEquals(item.getProperties().get(APP_PROP_1), zusammenItem.getInfo().getProperty(APP_PROP_1));
        assertEquals(item.getProperties().get(APP_PROP_2), zusammenItem.getInfo().getProperty(APP_PROP_2));

        Map<String, Number> zusammenStatusesMap = zusammenItem.getInfo().getProperty(ITEM_VERSIONS_STATUSES);
        Map<VersionStatus, Integer> statusesMap = item.getVersionStatusCounters();

        zusammenStatusesMap.forEach((key, value) -> assertEquals(statusesMap.get(VersionStatus.valueOf(key)), value));

        assertEquals(item.getCreationTime(), zusammenItem.getCreationTime());
        assertEquals(item.getModificationTime(), zusammenItem.getModificationTime());
    }

}
