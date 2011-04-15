package simplejcqrs.events;

import java.util.Collection;

import simplejcqrs.domain.AggregateRoot;

public interface EventStore {
	void saveEvents(Class<? extends AggregateRoot> rootClass,String aggregateId,Iterable<Event> events,int expectedVersion);
	Iterable<Event> getEventsForAggregate(Class<? extends AggregateRoot> rootClass, String id);
}
