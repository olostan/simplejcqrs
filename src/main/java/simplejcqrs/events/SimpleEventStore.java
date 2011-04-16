package simplejcqrs.events;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import simplejcqrs.domain.AggregateRoot;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton 
public class SimpleEventStore implements EventStore {
	
	private final EventPublisher publisher;
	
	private static class EventStoreData {
		public final Event event;
		public int version;
		public EventStoreData(Event event, int version) {
			super();
			this.event = event;
			this.version = version;
		}					
	}
	private final Map<String,LinkedList<SimpleEventStore.EventStoreData>> current = new HashMap<String,LinkedList<SimpleEventStore.EventStoreData>>();
	
	@Inject
	public SimpleEventStore(EventPublisher publisher) {
		super();
		this.publisher = publisher;
	}

	@Override
	public void saveEvents(Class<? extends AggregateRoot> rootClass,
			String aggregateId, Iterable<Event> events,
			int expectedVersion) {
		LinkedList<SimpleEventStore.EventStoreData> rootEvents = current.get(aggregateId);
		if (rootEvents==null) {
			rootEvents = new LinkedList<SimpleEventStore.EventStoreData>();
			current.put(aggregateId, rootEvents);
		}					
		else if (expectedVersion != -1 && rootEvents.getLast().version != expectedVersion) {
			throw new RuntimeException("Concurrancy exception for aggregate "+rootClass.getName()+": "+rootEvents.getLast().version+" != "+expectedVersion);
		}
		int versionCounter = expectedVersion;
		for(Event event : events) {
			versionCounter++;
			event.setAggregateVersion(versionCounter);			
			rootEvents.add(new EventStoreData(event, versionCounter));
			publisher.publish(event);
		}		
	}
	private class EventStoreDataIterator implements Iterator<Event> {
		//private final Collection<EventStoreData> events;
		private final Iterator<SimpleEventStore.EventStoreData> iterator; 

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

		public EventStoreDataIterator(Iterator<SimpleEventStore.EventStoreData> iterator) {
			super();
			this.iterator = iterator;
		}
		
	}
	@Override
	public Iterable<Event> getEventsForAggregate(
			Class<? extends AggregateRoot> rootClass, String id) 
	{
		final LinkedList<SimpleEventStore.EventStoreData> rootEvents = current.get(id);
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