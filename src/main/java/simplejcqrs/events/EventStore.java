package simplejcqrs.events;

import simplejcqrs.domain.AggregateRoot;

import com.google.inject.ImplementedBy;

@ImplementedBy(SimpleEventStore.class)
public interface EventStore {
	void saveEvents(Class<? extends AggregateRoot> rootClass,String aggregateId,Iterable<Event> events,int expectedVersion);
	Iterable<Event> getEventsForAggregate(Class<? extends AggregateRoot> rootClass, String id);
	boolean hasEventsForAggregate(Class<? extends AggregateRoot> rootClass, String id);
}
