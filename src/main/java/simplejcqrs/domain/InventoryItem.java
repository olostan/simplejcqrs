package simplejcqrs.domain;

import simplejcqrs.events.InventoryEvents;

public class InventoryItem extends AggregateRoot {
	@SuppressWarnings("unused")
	private String name;

	public InventoryItem(String id, String name) {
		super(id);
		this.name = name;
		ApplyChange(new InventoryEvents.InventoryItemCreated(name));
	}
}
