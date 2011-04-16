package simplejcqrs.events;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import simplejcqrs.domain.AggregateRoot;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton 
public class PublishingEventStore implements EventStore {
	
	private final EventPublisher publisher;
	private final EventStore underlying;
	
	//private final Map<String,LinkedList<PublishingEventStore.EventStoreData>> current = new HashMap<String,LinkedList<PublishingEventStore.EventStoreData>>();
	
	@Inject
	public PublishingEventStore(EventPublisher publisher, EventStore underlyingStore) {
		super();
		this.publisher = publisher;
		this.underlying = underlyingStore;
	}

	@Override
	public void saveEvents(Class<? extends AggregateRoot> rootClass,
			String aggregateId, Iterable<Event> events,
			int expectedVersion) {
		/*
		LinkedList<PublishingEventStore.EventStoreData> rootEvents = current.get(aggregateId);
		if (rootEvents==null) {
			rootEvents = new LinkedList<PublishingEventStore.EventStoreData>();
			current.put(aggregateId, rootEvents);
		}					
		else if (expectedVersion != -1 && rootEvents.getLast().version != expectedVersion) {
			throw new RuntimeException("Concurrancy exception for aggregate "+rootClass.getName()+": "+rootEvents.getLast().version+" != "+expectedVersion);
		}*/
		if (!checkVersion(rootClass, aggregateId, expectedVersion))
			throw new RuntimeException("Concurrancy exception for aggregate "+rootClass.getName()+": expecting version "+expectedVersion);		
		
		int versionCounter = expectedVersion;
		for(Event event : events) {
			versionCounter++;
			event.setAggregateVersion(versionCounter);			
			//rootEvents.add(new EventStoreData(event, versionCounter));
			publisher.publish(event, aggregateId);
		}
		underlying.saveEvents(rootClass, aggregateId, events, expectedVersion);
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

	@Override
	public boolean hasEventsForAggregate(
			Class<? extends AggregateRoot> rootClass, String id) {
		return underlying.hasEventsForAggregate(rootClass, id);
	}

	@Override
	public boolean checkVersion(Class<? extends AggregateRoot> rootClass,
			String id, int version) {
		return underlying.checkVersion(rootClass, id, version);
	}
	
}