package simplejcqrs.tests;

import java.util.Iterator;
import java.util.LinkedList;

import simplejcqrs.domain.AggregateRoot;
import simplejcqrs.events.Event;
import simplejcqrs.events.EventStore;
import simplejcqrs.events.EventStoreDataIterator;
import simplejcqrs.events.PublishingEventStore;

public class InMemoryEventStore implements EventStore {

	private static class EventStoreData {
		public final Event event;
		public int version;
		public EventStoreData(Event event, int version) {
			super();
			this.event = event;
			this.version = version;
		}					
	}
	private class EventStoreDataIterator implements Iterator<Event> {
		private final Iterator<EventStoreData> iterator; 

		@Override
		public boolean hasNext() {				
			return iterator.hasNext();
		}

		@Override
		public Event next() {				
			return iterator.next().event;
		}

		@Override
		public void remove() {
			iterator.remove();				
		}

		public EventStoreDataIterator(Iterator<EventStoreData> iterator) {
			super();
			this.iterator = iterator;
		}
		
	}
	@Override
	public void saveEvents(Class<? extends AggregateRoot> rootClass,
			String aggregateId, Iterable<Event> events, int expectedVersion) {

	}
	
	@Override
	public boolean hasEventsForAggregate(
			Class<? extends AggregateRoot> rootClass, String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean checkVersion(Class<? extends AggregateRoot> rootClass,
			String id, int version) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override	
	public Iterable<Event> getEventsForAggregate(
			Class<? extends AggregateRoot> rootClass, String id) 
	{
		final LinkedList<PublishingEventStore.EventStoreData> rootEvents = current.get(id);
		if (rootEvents == null)
			throw new RuntimeException("Aggregate not found");			
		return new Iterable<Event>() {
			@Override
			public Iterator<Event> iterator() {
				return new EventStoreDataIterator(rootEvents.iterator());
			}			
		};
	}

}
