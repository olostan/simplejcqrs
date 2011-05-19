package simplejcqrs.events;

import simplejcqrs.domain.AggregateRoot;

import com.google.inject.ImplementedBy;

@ImplementedBy(PublishingEventStore.class)
public interface EventStore {
	void saveEvents(Class<? extends AggregateRoot> rootClass,long aggregateId,Iterable<Event> events,int expectedVersion);
	Iterable<Event> getEventsForAggregate(Class<? extends AggregateRoot> rootClass, long id);
	boolean hasEventsForAggregate(Class<? extends AggregateRoot> rootClass, long id);
	boolean checkVersion(Class<? extends AggregateRoot> rootClass, long id, int version);
}
