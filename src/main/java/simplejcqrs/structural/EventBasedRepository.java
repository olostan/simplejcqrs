package simplejcqrs.structural;

import simplejcqrs.domain.AggregateRoot;
import simplejcqrs.domain.Repository;
import simplejcqrs.events.Event;
import simplejcqrs.events.EventStore;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton 
public class EventBasedRepository implements Repository {
	private EventStore store;
	
	@Inject
	public EventBasedRepository(EventStore store) {
		super();
		this.store = store;
	}

	@Override
	public <T extends AggregateRoot> T load(Class<T> rootClass, String id) {
		try {
			T root = rootClass.newInstance();
			Iterable<Event> events = store.getEventsForAggregate(rootClass, id);
			root.loadFromHistory(events);
			return root;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}			
		return null;
	}

	@Override
	public void save(AggregateRoot root, int expectedVersion) {
		store.saveEvents(root.getClass(), root.getId(), root.getChanges(), expectedVersion);
	}
	
}