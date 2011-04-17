package simplejcqrs.tests;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import simplejcqrs.domain.AggregateRoot;
import simplejcqrs.events.Event;
import simplejcqrs.events.EventStore;

public class InMemoryEventStore implements EventStore {
	
	//private final Map<String,LinkedList<PublishingEventStore.EventStoreData>> current = new HashMap<String,LinkedList<PublishingEventStore.EventStoreData>>();
	private final Map<Class<?>,AggregateRootStore> store = new HashMap<Class<?>, InMemoryEventStore.AggregateRootStore>();
	
	private static class AggregateRootStore {
		private static class EventStoreData {
			public final Event event;
			public int version;
			public EventStoreData(Event event, int version) {
				super();
				this.event = event;
				this.version = version;
			}					
		}
		
		private final Map<String,LinkedList<EventStoreData>> store = new HashMap<String,LinkedList<EventStoreData>>();
		
		private static class EventStoreDataIterator implements Iterator<Event> {
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
		
		public void saveEvents(
				String aggregateId, Iterable<Event> events,
				int expectedVersion) 
		{
			LinkedList<EventStoreData> rootEvents = store.get(aggregateId);
			if (rootEvents==null) {
				rootEvents = new LinkedList<EventStoreData>();
				store.put(aggregateId, rootEvents);
			}					
			for (Event event : events) {
				rootEvents.add(new EventStoreData(event, event.getAggregateVersion()));
			}
		}
		public Iterable<Event> getEvents(String id) {
			final LinkedList<EventStoreData> rootEvents = store.get(id);
			if (rootEvents==null) throw new RuntimeException("No aggregate found with id "+id);
			return new Iterable<Event>() {
				@Override
				public Iterator<Event> iterator() {
					return new EventStoreDataIterator(rootEvents!=null?rootEvents.iterator():null);
				}				
			};			
		}
		public boolean hasEvents(String id) {			
			return store.containsKey(id);
		}
		public boolean checkVersion(String id, int version) {
			final LinkedList<EventStoreData> rootEvents = store.get(id);			
			return rootEvents != null && rootEvents.getLast().version == version;
		}
		
	}
	
	@Override
	public void saveEvents(Class<? extends AggregateRoot> rootClass,
			String aggregateId, Iterable<Event> events, int expectedVersion) {
		AggregateRootStore rootStore = store.get(rootClass);
		if (rootStore==null) {
			rootStore = new AggregateRootStore();
			store.put(rootClass, rootStore);
		}
		rootStore.saveEvents(aggregateId, events, expectedVersion);
	}
	
	@Override
	public boolean hasEventsForAggregate(
			Class<? extends AggregateRoot> rootClass, String id) {
		AggregateRootStore rootStore = store.get(rootClass);		
		return rootStore != null && rootStore.hasEvents(id);
	}

	@Override
	public boolean checkVersion(Class<? extends AggregateRoot> rootClass,
			String id, int version) {		
		AggregateRootStore rootStore = store.get(rootClass);
		return rootStore != null && rootStore.checkVersion(id,version);		
	}
	@Override	
	public Iterable<Event> getEventsForAggregate(
			Class<? extends AggregateRoot> rootClass, String id) 
	{
		AggregateRootStore rootStore = store.get(rootClass);
		if (rootStore == null)
			throw new RuntimeException("Aggregate not found: "+rootClass.getName());			
		return rootStore.getEvents(id);
	}

}
