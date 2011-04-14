package simplejcqrs.events;

import java.util.Collection;

public interface EventStore {
	void saveEvents(String aggregateId,Collection<Event> events,int expectedVersion);
	Collection<Event> getEventsForAggregate(String id);
}
