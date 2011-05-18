package simplejcqrs.events;

public final class HumanEvents {
	public static class HumanRegistred extends Event {
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
