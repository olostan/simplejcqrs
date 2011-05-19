package simplejcqrs.commands;

public final class InventoryCommands {
	@SuppressWarnings("serial")
	private static abstract class InventoryCommand extends Command {
		private final long inventoryId;
		public InventoryCommand(long inventoryId) {
			super();
			this.inventoryId = inventoryId;
		}
		public long getInventoryId() {
			return inventoryId;
		}				
	}
	@SuppressWarnings("serial")
	public static class CreateInventoryItem extends InventoryCommand {
		private final String name;

		public CreateInventoryItem(long inventoryId, String name) {
			super(inventoryId);
			this.name = name;
		}

		public String getName() {
			return name;
		}
		
	}
}
