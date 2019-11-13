package org.onap.sdc.common.versioning.persistence.zusammen;

import com.amdocs.zusammen.datatypes.Id;
import com.amdocs.zusammen.datatypes.item.Info;
import com.amdocs.zusammen.datatypes.item.Item;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.onap.sdc.common.versioning.persistence.ItemDao;
import org.onap.sdc.common.versioning.persistence.types.InternalItem;
import org.onap.sdc.common.versioning.services.types.ItemStatus;
import org.onap.sdc.common.versioning.services.types.VersionStatus;
import org.onap.sdc.common.zusammen.services.ZusammenAdaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ItemZusammenDao implements ItemDao {

    private final ZusammenSessionContextCreator contextCreator;
    private final ZusammenAdaptor zusammenAdaptor;

    @Autowired
    public ItemZusammenDao(ZusammenSessionContextCreator contextCreator, ZusammenAdaptor zusammenAdaptor) {
        this.contextCreator = contextCreator;
        this.zusammenAdaptor = zusammenAdaptor;
    }

    @Override
    public Collection<InternalItem> list() {
        return zusammenAdaptor.listItems(contextCreator.create()).stream().map(this::mapFromZusammenItem)
                       .collect(Collectors.toList());
    }

    @Override
    public InternalItem get(String itemId) {
        return mapFromZusammenItem(zusammenAdaptor.getItem(contextCreator.create(), new Id(itemId)));
    }

    @Override
    public InternalItem create(InternalItem item) {
        Id itemId = zusammenAdaptor.createItem(contextCreator.create(), mapToZusammenItemInfo(item));
        item.setId(itemId.getValue());
        return item;
    }

    @Override
    public void delete(String itemId) {
        zusammenAdaptor.deleteItem(contextCreator.create(), new Id(itemId));
    }

    @Override
    public void update(InternalItem item) {
        zusammenAdaptor.updateItem(contextCreator.create(), new Id(item.getId()), mapToZusammenItemInfo(item));
    }

    private InternalItem mapFromZusammenItem(Item zusammenItem) {
        if (zusammenItem == null) {
            return null;
        }
        InternalItem item = new InternalItem();
        item.setId(zusammenItem.getId().getValue());
        item.setName(zusammenItem.getInfo().getName());
        item.setDescription(zusammenItem.getInfo().getDescription());
        item.setCreationTime(zusammenItem.getCreationTime());
        item.setModificationTime(zusammenItem.getModificationTime());

        zusammenItem.getInfo().getProperties().forEach((key, value) -> addPropertyToItem(key, value, item));
        if (item.getStatus() == null) {
            item.setStatus(ItemStatus.ACTIVE);
        }

        return item;
    }

    private void addPropertyToItem(String propertyKey, Object propertyValue, InternalItem item) {
        switch (propertyKey) {
            case InfoPropertyName.ITEM_TYPE:
                item.setType((String) propertyValue);
                break;
            case InfoPropertyName.ITEM_OWNER:
                item.setOwner((String) propertyValue);
                break;
            case InfoPropertyName.ITEM_STATUS:
                item.setStatus(ItemStatus.valueOf((String) propertyValue));
                break;
            case InfoPropertyName.ITEM_VERSIONS_STATUSES:
                Map<VersionStatus, Integer> versionStatusCounters = new EnumMap<>(VersionStatus.class);
                for (Map.Entry<String, Number> statusCounter : ((Map<String, Number>) propertyValue).entrySet()) {
                    versionStatusCounters
                            .put(VersionStatus.valueOf(statusCounter.getKey()), statusCounter.getValue().intValue());
                }
                item.setVersionStatusCounters(versionStatusCounters);
                break;
            default:
                item.addProperty(propertyKey, propertyValue);
        }
    }

    private Info mapToZusammenItemInfo(InternalItem item) {
        Info info = new Info();
        info.setName(item.getName());
        info.setDescription(item.getDescription());
        info.addProperty(InfoPropertyName.ITEM_TYPE, item.getType());
        info.addProperty(InfoPropertyName.ITEM_OWNER, item.getOwner());
        if (item.getStatus() != null) {
            info.addProperty(InfoPropertyName.ITEM_STATUS, item.getStatus());
        }
        info.addProperty(InfoPropertyName.ITEM_VERSIONS_STATUSES, item.getVersionStatusCounters());
        item.getProperties().forEach(info::addProperty);
        return info;
    }

    private static final class InfoPropertyName {

        private static final String ITEM_TYPE = "item_type";
        private static final String ITEM_VERSIONS_STATUSES = "item_versions_statuses";
        private static final String ITEM_OWNER = "Owner";
        private static final String ITEM_STATUS = "status";

        private InfoPropertyName() {
            throw new IllegalStateException("Constants class");
        }
    }
}
