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

package org.onap.sdc.common.zusammen.persistence.impl;

import com.amdocs.zusammen.adaptor.inbound.api.health.HealthAdaptorFactory;
import com.amdocs.zusammen.adaptor.inbound.api.item.ElementAdaptorFactory;
import com.amdocs.zusammen.adaptor.inbound.api.item.ItemAdaptorFactory;
import com.amdocs.zusammen.adaptor.inbound.api.item.ItemVersionAdaptorFactory;
import com.amdocs.zusammen.adaptor.inbound.api.types.item.Element;
import com.amdocs.zusammen.adaptor.inbound.api.types.item.ElementConflict;
import com.amdocs.zusammen.adaptor.inbound.api.types.item.ElementInfo;
import com.amdocs.zusammen.adaptor.inbound.api.types.item.ItemVersionConflict;
import com.amdocs.zusammen.adaptor.inbound.api.types.item.MergeResult;
import com.amdocs.zusammen.commons.health.data.HealthInfo;
import com.amdocs.zusammen.datatypes.Id;
import com.amdocs.zusammen.datatypes.SessionContext;
import com.amdocs.zusammen.datatypes.Space;
import com.amdocs.zusammen.datatypes.item.ElementContext;
import com.amdocs.zusammen.datatypes.item.Info;
import com.amdocs.zusammen.datatypes.item.Item;
import com.amdocs.zusammen.datatypes.item.ItemVersion;
import com.amdocs.zusammen.datatypes.item.ItemVersionData;
import com.amdocs.zusammen.datatypes.item.ItemVersionStatus;
import com.amdocs.zusammen.datatypes.item.Resolution;
import com.amdocs.zusammen.datatypes.itemversion.ItemVersionRevisions;
import com.amdocs.zusammen.datatypes.itemversion.Tag;
import com.amdocs.zusammen.datatypes.response.Response;
import java.util.Collection;
import org.onap.sdc.common.zusammen.persistence.ZusammenConnector;
import org.onap.sdc.common.zusammen.services.exceptions.ZusammenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ZusammenConnectorImpl implements ZusammenConnector {

    private static final String GET_ELEMENT_ERR_MSG =
            "Failed to get element. Item Id: %s, version Id: %s, element Id: %s message: %s";
    private static final String GET_ELEMENT_IN_REV_ERR_MSG =
            "Failed to get element. Item Id: %s, version Id: %s, revision Id: %s, element Id: %s message: %s";
    private final ItemAdaptorFactory itemAdaptorFactory;
    private final ItemVersionAdaptorFactory versionAdaptorFactory;
    private final ElementAdaptorFactory elementAdaptorFactory;
    private final HealthAdaptorFactory healthAdaptorFactory;

    @Autowired
    public ZusammenConnectorImpl(ItemAdaptorFactory itemAdaptorFactory, ItemVersionAdaptorFactory versionAdaptorFactory,
            ElementAdaptorFactory elementAdaptorFactory, HealthAdaptorFactory healthAdaptorFactory) {
        this.itemAdaptorFactory = itemAdaptorFactory;
        this.versionAdaptorFactory = versionAdaptorFactory;
        this.elementAdaptorFactory = elementAdaptorFactory;
        this.healthAdaptorFactory = healthAdaptorFactory;
    }

    @Override
    public Collection<HealthInfo> checkHealth(SessionContext sessionContext) {
        return healthAdaptorFactory.createInterface(sessionContext).getHealthStatus(sessionContext);
    }

    @Override
    public String getReleaseVersion(SessionContext sessionContext) {
        return healthAdaptorFactory.createInterface(sessionContext).getVersion();
    }

    @Override
    public Collection<Item> listItems(SessionContext context) {
        Response<Collection<Item>> response = itemAdaptorFactory.createInterface(context).list(context);
        return getResponseValue(response, "list items");
    }

    @Override
    public Item getItem(SessionContext context, Id itemId) {
        Response<Item> response = itemAdaptorFactory.createInterface(context).get(context, itemId);
        return getResponseValue(response, String.format("get item %s", itemId));
    }


    @Override
    public Id createItem(SessionContext context, Info info) {
        Response<Id> response = itemAdaptorFactory.createInterface(context).create(context, info);
        return getResponseValue(response, "create item");
    }

    @Override
    public void deleteItem(SessionContext context, Id itemId) {
        Response<Void> response = itemAdaptorFactory.createInterface(context).delete(context, itemId);
        getResponseValue(response, String.format("get item %s", itemId));
    }

    @Override
    public void updateItem(SessionContext context, Id itemId, Info info) {
        Response<Void> response = itemAdaptorFactory.createInterface(context).update(context, itemId, info);
        getResponseValue(response, String.format("update item %s", itemId));
    }

    @Override
    public Collection<ItemVersion> listPublicVersions(SessionContext context, Id itemId) {
        Response<Collection<ItemVersion>> response =
                versionAdaptorFactory.createInterface(context).list(context, Space.PUBLIC, itemId);
        return getResponseValue(response, String.format("list public versions of item %s", itemId));
    }

    @Override
    public ItemVersion getPublicVersion(SessionContext context, Id itemId, Id versionId) {
        Response<ItemVersion> response =
                versionAdaptorFactory.createInterface(context).get(context, Space.PUBLIC, itemId, versionId);
        return getResponseValue(response, String.format("get public version %s of item %s", versionId, itemId));
    }

    @Override
    public Id createVersion(SessionContext context, Id itemId, Id baseVersionId, ItemVersionData itemVersionData) {
        Response<Id> response =
                versionAdaptorFactory.createInterface(context).create(context, itemId, baseVersionId, itemVersionData);
        return getResponseValue(response,
                String.format("create version for item %s based on version %s", itemId, baseVersionId));
    }

    @Override
    public void updateVersion(SessionContext context, Id itemId, Id versionId, ItemVersionData itemVersionData) {
        Response<Void> response =
                versionAdaptorFactory.createInterface(context).update(context, itemId, versionId, itemVersionData);
        getResponseValue(response, String.format("update version %s of item %s", versionId, itemId));
    }

    @Override
    public ItemVersion getVersion(SessionContext context, Id itemId, Id versionId) {
        Response<ItemVersion> response =
                versionAdaptorFactory.createInterface(context).get(context, Space.PRIVATE, itemId, versionId);
        return getResponseValue(response, String.format("get version %s of item %s", versionId, itemId));
    }

    @Override
    public ItemVersionStatus getVersionStatus(SessionContext context, Id itemId, Id versionId) {
        Response<ItemVersionStatus> response =
                versionAdaptorFactory.createInterface(context).getStatus(context, itemId, versionId);
        return getResponseValue(response, String.format("get status of version %s of item %s", versionId, itemId));
    }

    @Override
    public void tagVersion(SessionContext context, Id itemId, Id versionId, Tag tag) {
        Response<Void> response =
                versionAdaptorFactory.createInterface(context).tag(context, itemId, versionId, null, tag);
        getResponseValue(response,
                String.format("tag version %s of item %s with tag %s", versionId, itemId, tag.getName()));
    }

    @Override
    public void resetVersionRevision(SessionContext context, Id itemId, Id versionId, Id revisionId) {
        Response<Void> response =
                versionAdaptorFactory.createInterface(context).resetRevision(context, itemId, versionId, revisionId);
        getResponseValue(response,
                String.format("reset version %s of item %s to revision %s", versionId, itemId, revisionId));
    }

    @Override
    public void revertVersionRevision(SessionContext context, Id itemId, Id versionId, Id revisionId) {
        Response<Void> response =
                versionAdaptorFactory.createInterface(context).revertRevision(context, itemId, versionId, revisionId);
        getResponseValue(response,
                String.format("revert version %s of item %s to revision %s", versionId, itemId, revisionId));
    }

    @Override
    public ItemVersionRevisions listVersionRevisions(SessionContext context, Id itemId, Id versionId) {
        Response<ItemVersionRevisions> response =
                versionAdaptorFactory.createInterface(context).listRevisions(context, itemId, versionId);
        return getResponseValue(response, String.format("list revisions of version %s of item %s", versionId, itemId));
    }


    @Override
    public void publishVersion(SessionContext context, Id itemId, Id versionId, String message) {
        Response<Void> response =
                versionAdaptorFactory.createInterface(context).publish(context, itemId, versionId, message);
        getResponseValue(response, String.format("publish version %s of item %s", versionId, itemId));
    }

    @Override
    public void syncVersion(SessionContext context, Id itemId, Id versionId) {
        Response<MergeResult> response =
                versionAdaptorFactory.createInterface(context).sync(context, itemId, versionId);
        getResponseValue(response, String.format("sync version %s of item %s", versionId, itemId));
    }

    @Override
    public void forceSyncVersion(SessionContext context, Id itemId, Id versionId) {
        Response<MergeResult> response =
                versionAdaptorFactory.createInterface(context).forceSync(context, itemId, versionId);
        getResponseValue(response, String.format("force sync version %s of item %s", versionId, itemId));
    }

    @Override
    public void cleanVersion(SessionContext context, Id itemId, Id versionId) {
        Response<Void> response = versionAdaptorFactory.createInterface(context).delete(context, itemId, versionId);
        getResponseValue(response, String.format("clean version %s of item %s", versionId, itemId));
    }

    @Override
    public ItemVersionConflict getVersionConflict(SessionContext context, Id itemId, Id versionId) {
        Response<ItemVersionConflict> response =
                versionAdaptorFactory.createInterface(context).getConflict(context, itemId, versionId);
        return getResponseValue(response, String.format("get conflict of version %s of item %s", versionId, itemId));
    }

    @Override
    public Collection<ElementInfo> listElements(SessionContext context, ElementContext elementContext,
            Id parentElementId) {
        Response<Collection<ElementInfo>> response =
                elementAdaptorFactory.createInterface(context).list(context, elementContext, parentElementId);
        return getResponseValue(response,
                String.format("list elements of version %s of item %s", elementContext.getVersionId(),
                        elementContext.getItemId()));
    }


    @Override
    public ElementInfo getElementInfo(SessionContext context, ElementContext elementContext, Id elementId) {
        Response<ElementInfo> response =
                elementAdaptorFactory.createInterface(context).getInfo(context, elementContext, elementId);
        return getResponseValue(response, String.format("get info of element %s of version %s of item %s", elementId,
                elementContext.getVersionId(), elementContext.getItemId()));
    }

    @Override
    public Element getElement(SessionContext context, ElementContext elementContext, Id elementId) {
        Response<Element> response =
                elementAdaptorFactory.createInterface(context).get(context, elementContext, elementId);
        return getResponseValue(response,
                String.format("get element %s of version %s of item %s", elementId, elementContext.getVersionId(),
                        elementContext.getItemId()));
    }

    @Override
    public ElementConflict getElementConflict(SessionContext context, ElementContext elementContext, Id elementId) {
        Response<ElementConflict> response =
                elementAdaptorFactory.createInterface(context).getConflict(context, elementContext, elementId);
        return getResponseValue(response,
                String.format("get conflict of element %s of version %s of item %s", elementId,
                        elementContext.getVersionId(), elementContext.getItemId()));
    }

    @Override
    public Element saveElement(SessionContext context, ElementContext elementContext, Element element, String message) {
        Response<Element> response =
                elementAdaptorFactory.createInterface(context).save(context, elementContext, element, message);
        return getResponseValue(response,
                String.format("save element %s of version %s of item %s", element.getElementId(),
                        elementContext.getVersionId(), elementContext.getItemId()));
    }

    @Override
    public void resolveElementConflict(SessionContext context, ElementContext elementContext, Element element,
            Resolution resolution) {
        Response<Void> response = elementAdaptorFactory.createInterface(context)
                                          .resolveConflict(context, elementContext, element, resolution);
        getResponseValue(response,
                String.format("resolve conflict of element %s of version %s of item %s", element.getElementId(),
                        elementContext.getVersionId(), elementContext.getItemId()));
    }

    private <T> T getResponseValue(Response<T> response, String action) {
        if (!response.isSuccessful()) {
            throw new ZusammenException(String.format("Failed to %s: %s", action, response.getReturnCode().toString()));
        }
        return response.getValue();
    }
}
