package simplejcqrs.events;

final class IntentoryEvents {

	 public class InventoryItemCreated extends Event {
		public final String name;

		public InventoryItemCreated(String intentoryId, String name) {
			super(intentoryId);
			this.name = name;
		} 
			
	 }
			
		
}
