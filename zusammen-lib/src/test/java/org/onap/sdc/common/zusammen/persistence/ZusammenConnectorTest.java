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

package org.onap.sdc.common.zusammen.persistence;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amdocs.zusammen.adaptor.inbound.api.health.HealthAdaptor;
import com.amdocs.zusammen.adaptor.inbound.api.health.HealthAdaptorFactory;
import com.amdocs.zusammen.adaptor.inbound.api.item.ElementAdaptorFactory;
import com.amdocs.zusammen.adaptor.inbound.api.item.ItemAdaptor;
import com.amdocs.zusammen.adaptor.inbound.api.item.ItemAdaptorFactory;
import com.amdocs.zusammen.adaptor.inbound.api.item.ItemVersionAdaptor;
import com.amdocs.zusammen.adaptor.inbound.api.item.ItemVersionAdaptorFactory;
import com.amdocs.zusammen.commons.health.data.HealthInfo;
import com.amdocs.zusammen.commons.health.data.HealthStatus;
import com.amdocs.zusammen.datatypes.Id;
import com.amdocs.zusammen.datatypes.SessionContext;
import com.amdocs.zusammen.datatypes.Space;
import com.amdocs.zusammen.datatypes.item.Info;
import com.amdocs.zusammen.datatypes.item.Item;
import com.amdocs.zusammen.datatypes.item.ItemVersion;
import com.amdocs.zusammen.datatypes.item.ItemVersionData;
import com.amdocs.zusammen.datatypes.item.ItemVersionStatus;
import com.amdocs.zusammen.datatypes.item.SynchronizationStatus;
import com.amdocs.zusammen.datatypes.itemversion.Tag;
import com.amdocs.zusammen.datatypes.response.ErrorCode;
import com.amdocs.zusammen.datatypes.response.Module;
import com.amdocs.zusammen.datatypes.response.Response;
import com.amdocs.zusammen.datatypes.response.ReturnCode;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.onap.sdc.common.zusammen.persistence.impl.ZusammenConnectorImpl;

@RunWith(MockitoJUnitRunner.class)
public class ZusammenConnectorTest {

    @Mock
    private ItemAdaptorFactory itemAdaptorFactoryMock;
    @Mock
    private ItemVersionAdaptorFactory versionAdaptorFactoryMock;
    @Mock
    private ElementAdaptorFactory elementAdaptorFactoryMock;
    @Mock
    private HealthAdaptorFactory healthAdaptorFactoryMock;
    private ZusammenConnector zusammenConnector;
    @Mock
    private SessionContext sessionContext;

    @Before
    public void init() {
        zusammenConnector =
                new ZusammenConnectorImpl(itemAdaptorFactoryMock, versionAdaptorFactoryMock, elementAdaptorFactoryMock,
                        healthAdaptorFactoryMock);

    }

    @Test
    public void checkHealthTest() {
        HealthAdaptor healthAdaptor = mock(HealthAdaptor.class);
        when(healthAdaptorFactoryMock.createInterface(sessionContext)).thenReturn(healthAdaptor);
        String test = "test";
        HealthInfo healthInfos = new HealthInfo(test, HealthStatus.UP, test);
        List<HealthInfo> healtInfoList = ImmutableList.of(healthInfos);
        when(healthAdaptor.getHealthStatus(sessionContext)).thenReturn(healtInfoList);
        Collection<HealthInfo> healthInfosCollection = zusammenConnector.checkHealth(sessionContext);
        assertEquals(healtInfoList.size(), healthInfosCollection.size());
    }

    @Test
    public void checkReleaseVersion() {
        HealthAdaptor healthAdaptor = mock(HealthAdaptor.class);
        when(healthAdaptorFactoryMock.createInterface(sessionContext)).thenReturn(healthAdaptor);
        String version = "version";
        when(healthAdaptor.getVersion()).thenReturn(version);
        String releaseVersion = zusammenConnector.getReleaseVersion(sessionContext);
        assertEquals(version, releaseVersion);
    }

    @Test
    public void checkListItems() {
        ItemAdaptor itemAdaptor = mock(ItemAdaptor.class);
        when(itemAdaptorFactoryMock.createInterface(sessionContext)).thenReturn(itemAdaptor);
        List<Item> items = ImmutableList.of(new Item(), new Item());
        Response<Collection<Item>> response = new Response<>(items);
        when(itemAdaptor.list(sessionContext)).thenReturn(response);
        Collection<Item> itemList = zusammenConnector.listItems(sessionContext);
        assertEquals(items.size(), itemList.size());
    }

    @Test(expected = org.onap.sdc.common.zusammen.services.exceptions.ZusammenException.class)
    public void checkListItemFailure() {
        ItemAdaptor itemAdaptor = mock(ItemAdaptor.class);
        when(itemAdaptorFactoryMock.createInterface(sessionContext)).thenReturn(itemAdaptor);
        List<Item> items = ImmutableList.of(new Item(), new Item());
        Response<Collection<Item>> response = new Response<>(items);
        setResponseErrorReturnCode(response);
        when(itemAdaptor.list(sessionContext)).thenReturn(response);
        zusammenConnector.listItems(sessionContext);
        fail("Should never get here");
    }

    @Test
    public void testCreateItem() {
        ItemAdaptor itemAdaptor = mock(ItemAdaptor.class);
        when(itemAdaptorFactoryMock.createInterface(sessionContext)).thenReturn(itemAdaptor);
        String idStr = "idStr";
        Id id = new Id(idStr);
        Response<Id> response = new Response<>(id);
        Info info = new Info();
        when(itemAdaptor.create(sessionContext, info)).thenReturn(response);
        Id retId = zusammenConnector.createItem(sessionContext, info);
        assertEquals(idStr, retId.getValue());
    }

    @Test(expected = org.onap.sdc.common.zusammen.services.exceptions.ZusammenException.class)
    public void testCreateItemFailure() {
        ItemAdaptor itemAdaptor = mock(ItemAdaptor.class);
        when(itemAdaptorFactoryMock.createInterface(sessionContext)).thenReturn(itemAdaptor);
        String idStr = "idStr";
        Id id = new Id(idStr);
        Response<Id> response = new Response<>(id);
        setResponseErrorReturnCode(response);
        Info info = new Info();
        when(itemAdaptor.create(sessionContext, info)).thenReturn(response);
        zusammenConnector.createItem(sessionContext, info);

    }


    @Test
    public void testDeleteItem() {
        ItemAdaptor itemAdaptor = mock(ItemAdaptor.class);
        when(itemAdaptorFactoryMock.createInterface(sessionContext)).thenReturn(itemAdaptor);
        Response<Void> response = new Response<>(null);

        Id itemId = new Id();
        when(itemAdaptor.delete(sessionContext, itemId)).thenReturn(response);
        zusammenConnector.deleteItem(sessionContext, itemId);
        assertNotNull(zusammenConnector);
    }

    @Test
    public void testUpdateItem() {
        ItemAdaptor itemAdaptor = mock(ItemAdaptor.class);
        when(itemAdaptorFactoryMock.createInterface(sessionContext)).thenReturn(itemAdaptor);
        Response<Void> response = new Response<>(null);
        Id itemId = new Id();
        Info info = new Info();
        when(itemAdaptor.update(sessionContext, itemId, info)).thenReturn(response);
        zusammenConnector.updateItem(sessionContext, itemId, info);
        assertNotNull(zusammenConnector);
    }


    @Test
    public void testPublicVersionAdaptor() {
        ItemVersionAdaptor itemAdaptor = mock(ItemVersionAdaptor.class);
        when(versionAdaptorFactoryMock.createInterface(sessionContext)).thenReturn(itemAdaptor);
        List<ItemVersion> itemVersions = ImmutableList.of(new ItemVersion(), new ItemVersion());
        Response<Collection<ItemVersion>> response = new Response<>(itemVersions);
        Id id = new Id();
        when(itemAdaptor.list(sessionContext, Space.PUBLIC, id)).thenReturn(response);
        Collection<ItemVersion> itemVersionsResult = zusammenConnector.listPublicVersions(sessionContext, id);
        assertEquals(itemVersions.size(), itemVersionsResult.size());
    }

    @Test
    public void testListPublicVersions() {
        ItemVersionAdaptor itemAdaptor = mock(ItemVersionAdaptor.class);
        when(versionAdaptorFactoryMock.createInterface(sessionContext)).thenReturn(itemAdaptor);
        ItemVersion itemVersion = new ItemVersion();
        Response<ItemVersion> response = new Response<>(itemVersion);
        Id id = new Id();
        when(itemAdaptor.get(sessionContext, Space.PUBLIC, id, id)).thenReturn(response);
        ItemVersion itemVersionsResult = zusammenConnector.getPublicVersion(sessionContext, id, id);
        assertEquals(itemVersion, itemVersionsResult);
    }


    @Test
    public void testListPublicVersion() {
        ItemVersionAdaptor itemAdaptor = mock(ItemVersionAdaptor.class);
        when(versionAdaptorFactoryMock.createInterface(sessionContext)).thenReturn(itemAdaptor);
        List<ItemVersion> itemVersions = ImmutableList.of(new ItemVersion(), new ItemVersion());
        Response<Collection<ItemVersion>> response = new Response<>(itemVersions);
        Id id = new Id();
        when(itemAdaptor.list(sessionContext, Space.PUBLIC, id)).thenReturn(response);
        Collection<ItemVersion> itemVersionsResult = zusammenConnector.listPublicVersions(sessionContext, id);
        assertEquals(itemVersions.size(), itemVersionsResult.size());
    }

    @Test
    public void testCreateVersion() {
        ItemVersionAdaptor itemAdaptor = mock(ItemVersionAdaptor.class);
        when(versionAdaptorFactoryMock.createInterface(sessionContext)).thenReturn(itemAdaptor);
        List<ItemVersion> itemVersions = ImmutableList.of(new ItemVersion(), new ItemVersion());
        Response<Collection<ItemVersion>> response = new Response<>(itemVersions);
        Id id = new Id();
        when(itemAdaptor.list(sessionContext, Space.PUBLIC, id)).thenReturn(response);
        Collection<ItemVersion> itemVersionsResult = zusammenConnector.listPublicVersions(sessionContext, id);
        assertEquals(itemVersions.size(), itemVersionsResult.size());
    }

    @Test
    public void testGetPublicVersion() {
        ItemVersionAdaptor itemAdaptor = mock(ItemVersionAdaptor.class);
        when(versionAdaptorFactoryMock.createInterface(sessionContext)).thenReturn(itemAdaptor);
        ItemVersion itemVersionIn = new ItemVersion();
        Id id = new Id();
        String myId = "myId";
        id.setValue(myId);
        itemVersionIn.setId(id);
        Response<ItemVersion> response = new Response<>(itemVersionIn);
        when(itemAdaptor.get(sessionContext, Space.PUBLIC, id, id)).thenReturn(response);
        ItemVersion itemVersion = zusammenConnector.getPublicVersion(sessionContext, id, id);
        assertEquals(myId, itemVersion.getId().getValue());
    }

    @Test
    public void testCreateVersionVersion() {
        ItemVersionAdaptor itemAdaptor = mock(ItemVersionAdaptor.class);
        when(versionAdaptorFactoryMock.createInterface(sessionContext)).thenReturn(itemAdaptor);
        ItemVersionData itemVersionData = new ItemVersionData();
        Id id = new Id();
        String myId = "myId";
        id.setValue(myId);
        Response<Id> response = new Response<>(id);
        when(itemAdaptor.create(sessionContext, id, id, itemVersionData)).thenReturn(response);
        Id outId = zusammenConnector.createVersion(sessionContext, id, id, itemVersionData);
        assertEquals(myId, outId.getValue());
    }

    @Test
    public void testUpdateVersionVersion() {
        ItemVersionAdaptor itemAdaptor = mock(ItemVersionAdaptor.class);
        when(versionAdaptorFactoryMock.createInterface(sessionContext)).thenReturn(itemAdaptor);
        ItemVersionData itemVersionData = new ItemVersionData();
        Id id = new Id();
        Response<Void> response = new Response<>(null);
        Response<Void> spyResponse = spy(response);
        when(itemAdaptor.update(sessionContext, id, id, itemVersionData)).thenReturn(spyResponse);
        zusammenConnector.updateVersion(sessionContext, id, id, itemVersionData);
        verify(spyResponse).getValue();

    }

    @Test
    public void testGetVersion() {
        ItemVersionAdaptor itemAdaptor = mock(ItemVersionAdaptor.class);
        when(versionAdaptorFactoryMock.createInterface(sessionContext)).thenReturn(itemAdaptor);
        ItemVersion itemVersion = new ItemVersion();
        Id id = new Id();
        String value = "myId";
        id.setValue(value);
        itemVersion.setId(id);
        Response<ItemVersion> response = new Response<>(null);
        response.setValue(itemVersion);
        when(itemAdaptor.get(sessionContext, Space.PRIVATE, id, id)).thenReturn(response);
        ItemVersion version = zusammenConnector.getVersion(sessionContext, id, id);
        assertEquals(id.getValue(), itemVersion.getId().getValue());
    }

    @Test
    public void testGetVersionStatus() {
        ItemVersionAdaptor itemAdaptor = mock(ItemVersionAdaptor.class);
        when(versionAdaptorFactoryMock.createInterface(sessionContext)).thenReturn(itemAdaptor);
        ItemVersionStatus itemVersionStatus = new ItemVersionStatus(SynchronizationStatus.OUT_OF_SYNC, true);
        Response<ItemVersionStatus> response = new Response<>(null);
        response.setValue(itemVersionStatus);
        Id id = new Id();
        when(itemAdaptor.getStatus(sessionContext, id, id)).thenReturn(response);
        ItemVersionStatus versionStatus = zusammenConnector.getVersionStatus(sessionContext, id, id);
        assertEquals(SynchronizationStatus.OUT_OF_SYNC, versionStatus.getSynchronizationStatus());
    }

    @Test
    public void testTagVersion() {
        ItemVersionAdaptor itemAdaptor = mock(ItemVersionAdaptor.class);
        when(versionAdaptorFactoryMock.createInterface(sessionContext)).thenReturn(itemAdaptor);
        Response<Void> response = new Response<>(null);
        Id id = new Id();
        String name = "mame";
        Tag tag = new Tag(name, name);
        Tag spyTag = spy(tag);
        when(itemAdaptor.tag(sessionContext, id, id, null, spyTag)).thenReturn(response);
        zusammenConnector.tagVersion(sessionContext, id, id, spyTag);
        verify(spyTag).getName();

    }


    @Test
    public void testResetVersionRevision() {
        ItemVersionAdaptor itemAdaptor = mock(ItemVersionAdaptor.class);
        ItemVersionAdaptor itemVersionAdaptor = spy(itemAdaptor);
        Response<Void> response = new Response<>(null);
        Id id = new Id();
        when(versionAdaptorFactoryMock.createInterface(sessionContext)).thenReturn(itemVersionAdaptor);
        when(itemVersionAdaptor.resetRevision(sessionContext, id, id, id)).thenReturn(response);
        zusammenConnector.resetVersionRevision(sessionContext, id, id, id);
        verify(itemVersionAdaptor).resetRevision(sessionContext, id, id, id);

    }

    @Test
    public void testRevertVersionRevision() {
        ItemVersionAdaptor itemAdaptor = mock(ItemVersionAdaptor.class);
        ItemVersionAdaptor itemVersionAdaptor = spy(itemAdaptor);
        Response<Void> response = new Response<>(null);
        when(versionAdaptorFactoryMock.createInterface(sessionContext)).thenReturn(itemVersionAdaptor);
        Id id = new Id();
        when(itemVersionAdaptor.revertRevision(sessionContext, id, id, id)).thenReturn(response);
        zusammenConnector.revertVersionRevision(sessionContext, id, id, id);
        verify(itemVersionAdaptor).revertRevision(sessionContext, id, id, id);
    }


    private void setResponseErrorReturnCode(Response response) {
        response.setReturnCode(new ReturnCode(ErrorCode.CL_ELEMENT_GET, Module.ZSTM, "bla bla", null));
    }
}
