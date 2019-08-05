package org.onap.sdc.common.versioning.services.convertors;

import org.onap.sdc.common.versioning.services.types.Item;

public interface ItemConvertor<T> {

    String getItemType();

    //Item toItem(T model);

    void toItem(T source, Item target);

   // void fromItem(Item source, T target);

    T fromItem(Item item);

}
