package simplejcqrs.domain;

public class InventoryItem extends AggregateRoot {
	private String name;

	public InventoryItem(String id, String name) {
		super(id);
		this.name = name;
	}
}
