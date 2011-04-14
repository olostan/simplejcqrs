package simplejcqrs.events;

final class IntentoryEvents {
	 public class InventoryItemCreated extends Event {
		public final String name;

		public InventoryItemCreated(String name) {
			this.name = name;
		} 			
	 }
}
