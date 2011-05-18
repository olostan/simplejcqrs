package simplejcqrs.domain;

import simplejcqrs.events.HumanEvents;

public class Human extends AggregateRoot {
	private String firstName;
	private String lastName;		

	public Human(long id, String firstName, String lastName) {
		super(id);
		this.firstName = firstName;
		this.lastName = lastName;
		ApplyChange(new HumanEvents.HumanRegistred(firstName, lastName));
	}
	
	
}
