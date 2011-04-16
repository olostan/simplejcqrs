package simplejcqrs.commands;

public final class HouseCommands {
	private static class HouseCommand extends Command {
		private final String houseId;

		public HouseCommand(String houseId) {
			super();
			this.houseId = houseId;
		}
		public String getHouseId() {
			return houseId;
		}				
	}
	
	public static class CreateHouse extends HouseCommand {
		private final String address;

		public CreateHouse(String houseId, String address) {
			super(houseId);
			this.address = address;
		}
		public String getAddress() {
			return address;
		}				
	}
}
