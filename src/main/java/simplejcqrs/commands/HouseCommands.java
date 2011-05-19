package simplejcqrs.commands;

import java.io.Serializable;

public final class HouseCommands {
	@SuppressWarnings("serial")
	public static class HouseCommand extends Command  implements Serializable{
		private long houseId;

		public HouseCommand(long houseId) {
			super();
			this.houseId = houseId;
		}
		public long getHouseId() {
			return houseId;
		}	
		public HouseCommand() {
			super();
			this.houseId = -1;
		}
	}
	
	@SuppressWarnings("serial")
	public static class CreateHouse extends HouseCommand implements Serializable{
		private String address;

		public CreateHouse(long houseId, String address) {
			super(houseId);
			this.address = address;
		}
		public String getAddress() {
			return address;
		}
		public CreateHouse() {
			super(-1);
			address = "[none]";
		}						
	}
}
