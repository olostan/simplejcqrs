package simplejcqrs.events;

import java.io.Serializable;

public final class HumanEvents {	
	public static class HumanRenamed extends Event implements Serializable {
		private final String firstName;
		private final String lastName;
		public HumanRenamed(String firstName, String lastName) {
			super();
			this.firstName = firstName;
			this.lastName = lastName;
		}
		public String getFirstName() {
			return firstName;
		}
		public String getLastName() {
			return lastName;
		}		
	}

	@Event.CreationEvent
	public static class HumanRegistred extends Event implements Serializable {
		private final String firstName;
		private final String lastName;
		public HumanRegistred(String firstName, String lastName) {
			super();
			this.firstName = firstName;
			this.lastName = lastName;
		}
		public String getFirstName() {
			return firstName;
		}
		public String getLastName() {
			return lastName;
		}
	}
}
