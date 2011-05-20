package simplejcqrs.events;

import simplejcqrs.domain.AggregateRoot;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton 
public class PublishingEventStore implements EventStore {
	
	private final EventPublisher publisher;
	private final EventStore underlying;
	
	//private final Map<String,LinkedList<PublishingEventStore.EventStoreData>> current = new HashMap<String,LinkedList<PublishingEventStore.EventStoreData>>();
	
	@Inject
	public PublishingEventStore(EventPublisher publisher,@Named("ActualStore")  EventStore underlyingStore) {
		super();
		this.publisher = publisher;
		this.underlying = underlyingStore;
	}

	@Override
	public void saveEvents(Class<? extends AggregateRoot> rootClass,
			long aggregateId, Iterable<Event> events,
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
		if (expectedVersion!=-1 && !checkVersion(rootClass, aggregateId, expectedVersion))
			//throw new RuntimeException("Concurrancy exception for aggregate "+rootClass.getName()+": expecting version "+expectedVersion);
			throw new ConcurrancyException(expectedVersion, rootClass.getName());
		
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
			Class<? extends AggregateRoot> rootClass, long id) 
	{
		return underlying.getEventsForAggregate(rootClass, id);
	}

	@Override
	public boolean hasEventsForAggregate(
			Class<? extends AggregateRoot> rootClass, long id) {
		return underlying.hasEventsForAggregate(rootClass, id);
	}

	@Override
	public boolean checkVersion(Class<? extends AggregateRoot> rootClass,
			long id, int version) {
		return underlying.checkVersion(rootClass, id, version);
	}
	
}