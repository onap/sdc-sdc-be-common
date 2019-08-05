package org.onap.sdc.common.versioning.persistence;

import java.util.Collection;
import org.onap.sdc.common.versioning.persistence.types.InternalItem;

public interface ItemDao {

    Collection<InternalItem> list();

    InternalItem get(String itemId);

    InternalItem create(InternalItem item);

    void update(InternalItem item);

    void delete(String itemId);
}
