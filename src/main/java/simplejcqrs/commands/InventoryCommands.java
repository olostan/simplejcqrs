package simplejcqrs.commands;

public abstract class InventoryCommands {
	private static abstract class IntentoryCommand {
		private final String inventoryId;
		public IntentoryCommand(String inventoryId) {
			super();
			this.inventoryId = inventoryId;
		}
		public String getInventoryId() {
			return inventoryId;
		}
		
		
	}
	public static class CreateInventoryItem extends IntentoryCommand {
		private final String name;

		public CreateInventoryItem(String inventoryId, String name) {
			super(inventoryId);
			this.name = name;
		}

		public String getName() {
			return name;
		}
		
	}
}
