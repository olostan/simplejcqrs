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
			throw new RuntimeException("Can't create aggregate root class: instantiation",e);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException("Can't create and load aggregate root class: no access",e);
		}			
	}

	@Override
	public void save(AggregateRoot root, int expectedVersion) {
		store.saveEvents(root.getClass(), root.getId(), root.getChanges(), expectedVersion);
	}

	@Override
	public <T extends AggregateRoot> boolean exists(Class<T> rootClass, String id) {
		return store.hasEventsForAggregate(rootClass, id);
	}
	
}