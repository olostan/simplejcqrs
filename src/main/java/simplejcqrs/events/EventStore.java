package simplejcqrs.events;

import com.google.inject.ImplementedBy;

import simplejcqrs.domain.AggregateRoot;

@ImplementedBy(SimpleEventStore.class)
public interface EventStore {
	void saveEvents(Class<? extends AggregateRoot> rootClass,String aggregateId,Iterable<Event> events,int expectedVersion);
	Iterable<Event> getEventsForAggregate(Class<? extends AggregateRoot> rootClass, String id);
}
