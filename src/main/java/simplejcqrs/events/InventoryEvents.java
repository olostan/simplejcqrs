package simplejcqrs.events;

public final class InventoryEvents {
	 public  static class InventoryItemCreated extends Event {
		public final String name;

		public InventoryItemCreated(String name) {
			this.name = name;
		} 			
	 }
}
