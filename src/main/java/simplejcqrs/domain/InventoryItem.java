package simplejcqrs.domain;

import simplejcqrs.events.InventoryEvents;

public class InventoryItem extends AggregateRoot {

	public InventoryItem(String id, String name) {
		super(id);
		ApplyChange(new InventoryEvents.InventoryItemCreated(name));
	}
}
