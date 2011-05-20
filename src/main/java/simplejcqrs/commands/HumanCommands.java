package simplejcqrs.commands;

import java.io.Serializable;

public final class HumanCommands {
	public static class HumanCommand extends Command implements Serializable {
		private long humanId;

		public HumanCommand(long humanId,int originalVersion) {
			super(originalVersion);
			this.humanId = humanId;
		}

		public long getHumanId() {
			return humanId;
		}

		public void setHumanId(long humanId) {
			this.humanId = humanId;
		}

		// needed for RPC
		public HumanCommand() {
			super();			
		}		
	}
	public static class RegisterHuman extends HumanCommand implements Serializable {
		private String firstName;
		private String lastName;
		public String getFirstName() {
			return firstName;
		}
		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}
		public String getLastName() {
			return lastName;
		}
		public void setLastName(String lastName) {
			this.lastName = lastName;
		}
		public RegisterHuman(long humanId, String firstName, String lastName) {
			super(humanId,-1);
			this.firstName = firstName;
			this.lastName = lastName;
		}
		public RegisterHuman() {
			super();			
		}		
	}
	
	public static class RenameHuman extends HumanCommand implements Serializable
	{
		private String firstName;
		private String lastName;
		public RenameHuman(long humanId, String firstName, String lastName,int originalVersion) {
			super(humanId,originalVersion);
			this.firstName = firstName;
			this.lastName = lastName;
		}
		public String getFirstName() {
			return firstName;
		}
		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}
		public String getLastName() {
			return lastName;
		}
		public void setLastName(String lastName) {
			this.lastName = lastName;
		}
		public RenameHuman() {
			super();
		}
		
	}
	
}
