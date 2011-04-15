package simplejcqrs.events;

public final class InventoryEvents {
	 public class InventoryItemCreated extends Event {
		public final String name;

		public InventoryItemCreated(String name) {
			this.name = name;
		} 			
	 }
}
