package simplejcqrs.commands;

import java.io.Serializable;

public final class HouseCommands {
	@SuppressWarnings("serial")
	public static class HouseCommand extends Command  implements Serializable{
		private String houseId;

		public HouseCommand(String houseId) {
			super();
			this.houseId = houseId;
		}
		public String getHouseId() {
			return houseId;
		}	
		public HouseCommand() {
			super();
			this.houseId = "[none]";
		}
	}
	
	@SuppressWarnings("serial")
	public static class CreateHouse extends HouseCommand implements Serializable{
		private String address;

		public CreateHouse(String houseId, String address) {
			super(houseId);
			this.address = address;
		}
		public String getAddress() {
			return address;
		}
		public CreateHouse() {
			super("[none]");
			address = "[none]";
		}						
	}
}
