/*
 * Copyright © 2016-2018 European Support Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.onap.sdc.common.versioning.services;

import java.util.Collection;
import java.util.function.Predicate;
import org.onap.sdc.common.versioning.services.types.Item;
import org.onap.sdc.common.versioning.services.types.ItemStatus;

public interface ItemManager {

    Collection<Item> list(Predicate<Item> predicate);

    Item get(String itemId);

    Item create(Item item);

    Item update(String itemId, Item item);

    void delete(String itemId);

    void updateStatus(String itemId, ItemStatus status);
}
