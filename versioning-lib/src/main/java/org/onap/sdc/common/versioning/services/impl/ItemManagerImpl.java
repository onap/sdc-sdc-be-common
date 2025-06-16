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

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.onap.sdc.common.versioning.persistence.ItemDao;
import org.onap.sdc.common.versioning.persistence.types.InternalItem;
import org.onap.sdc.common.versioning.services.ItemManager;
import org.onap.sdc.common.versioning.services.exceptions.VersioningException;
import org.onap.sdc.common.versioning.services.types.Item;
import org.onap.sdc.common.versioning.services.types.ItemStatus;
import org.springframework.stereotype.Service;

@Service
public class ItemManagerImpl implements ItemManager {

    private final ItemDao itemDao;

    public ItemManagerImpl(ItemDao itemDao) {
        this.itemDao = itemDao;

    }

    @Override
    public Collection<Item> list(Predicate<Item> predicate) {
        return itemDao.list().stream().filter(predicate).collect(Collectors.toList());
    }

    @Override
    public Item get(String itemId) {
        return itemDao.get(itemId);
    }

    @Override
    public Item create(Item item) {
        InternalItem internalItem = new InternalItem();
        internalItem.setId(item.getId());
        internalItem.populateExternalFields(item);
        return itemDao.create(internalItem);
    }

    @Override
    public Item update(String itemId, Item item) {
        InternalItem internalItem = getItem(itemId);
        internalItem.populateExternalFields(item);
        itemDao.update(internalItem);
        return internalItem;
    }

    @Override
    public void delete(String itemId) {
        itemDao.delete(itemId);
    }

    @Override
    public void updateStatus(String itemId, ItemStatus status) {
        InternalItem item = getItem(itemId);
        if (item.getStatus() == status) {
            throw new VersioningException(
                    String.format("Update status of item %s failed, it is already in status %s", item.getId(), status));
        }

        item.setStatus(status);
        itemDao.update(item);
    }

    private InternalItem getItem(String itemId) {
        InternalItem item = itemDao.get(itemId);
        if (item == null) {
            throw new VersioningException(String.format("Item with Id %s does not exist", itemId));
        }
        return item;
    }
}
