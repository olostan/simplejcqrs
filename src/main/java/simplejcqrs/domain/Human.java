package simplejcqrs.domain;

import java.security.InvalidParameterException;

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

	public void ChangeName(String first, String last) {
		if (first==null || first.isEmpty() || last == null || last.isEmpty()) throw new InvalidParameterException("Names should not be empty");
		ApplyChange(new HumanEvents.HumanRenamed(first,last));		
	}

	public Human(Long id) {
		super(id);	
	}
	
	public void applyRenamed(HumanEvents.HumanRenamed ev) {
		this.firstName = ev.getFirstName();
		this.lastName = ev.getLastName();
	}
	
	
	
	
}
