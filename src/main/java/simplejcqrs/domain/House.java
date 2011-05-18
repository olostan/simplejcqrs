package simplejcqrs.domain;

import simplejcqrs.events.HouseEvents;

public class House extends AggregateRoot {
	
	public House(long id, String address) {
		super(id);
		ApplyChange(new HouseEvents.HouseCreated(address));
	}
	
	
}
