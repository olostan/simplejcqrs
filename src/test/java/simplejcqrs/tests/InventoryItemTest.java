package simplejcqrs.tests;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Test;

import simplejcqrs.commandhandlers.CommandHandler;
import simplejcqrs.commandhandlers.InventoryCommandHandlers;
import simplejcqrs.commands.Command;
import simplejcqrs.commands.InventoryCommands;
import simplejcqrs.domain.AggregateRoot;
import simplejcqrs.domain.Repository;
import simplejcqrs.events.Event;
import simplejcqrs.events.EventPublisher;
import simplejcqrs.events.EventStore;
import simplejcqrs.events.InventoryEvents;
import simplejcqrs.structural.EventBus;
import simplejcqrs.structural.EventHandler;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Singleton;

public class InventoryItemTest extends TestCase {
	@Singleton
	private static class TestEventStore implements EventStore {
		
		private final EventPublisher publisher;
		
		private static class EventStoreData {
			public final Event event;
			public final String id;
			public int version;
			public EventStoreData(Event event, String id, int version) {
				super();
				this.event = event;
				this.id = id;
				this.version = version;
			}					
		}
		private final Map<String,LinkedList<EventStoreData>> current = new HashMap<String,LinkedList<EventStoreData>>();
		
		@Inject
		public TestEventStore(EventPublisher publisher) {
			super();
			this.publisher = publisher;
		}

		@Override
		public void saveEvents(Class<? extends AggregateRoot> rootClass,
				String aggregateId, Iterable<Event> events,
				int expectedVersion) {
			LinkedList<EventStoreData> rootEvents = current.get(aggregateId);
			if (rootEvents==null) {
				rootEvents = new LinkedList<EventStoreData>();
				current.put(aggregateId, rootEvents);
			}					
			else if (expectedVersion != -1 && rootEvents.getLast().version != expectedVersion) {
				throw new RuntimeException("Concurrancy exception for aggregate "+rootClass.getName()+": "+rootEvents.getLast().version+" != "+expectedVersion);
			}
			int versionCounter = expectedVersion;
			for(Event event : events) {
				versionCounter++;
				event.setAggregateVersion(versionCounter);
				publisher.publish(event);
			}		
		}
		private class EventStoreDataIterator implements Iterator<Event> {
			//private final Collection<EventStoreData> events;
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
		public Iterable<Event> getEventsForAggregate(
				Class<? extends AggregateRoot> rootClass, String id) 
		{
			final LinkedList<EventStoreData> rootEvents = current.get(id);
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
	@Singleton
	private static class TestRepository implements Repository {
		private EventStore store;
		
		@Inject
		public TestRepository(EventStore store) {
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
	
	private static class Handler {
		public Object instance;
		public Method method;
		void Invoke(Object param) {
			try {
				method.invoke(instance, param);
			} catch (IllegalArgumentException e) {			
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}			
		}
	}
	@Singleton
	private static class TestEventBus extends EventBus {
		
		private Map<Class<?>,List<Handler> > handlerRegistry = new HashMap<Class<?>,List<Handler>>();
		
		@Override
		public void publish(Event event) 
		{
			System.out.println("Publishing event:"+event.getClass().getName());
			List<Handler> handlers = handlerRegistry.get(event.getClass());
			if (handlers == null || handlers.size()==0) return;
			for(Handler handler : handlers) handler.Invoke(event);
		}

		@Override
		public void send(Command command) 
		{
			System.out.println("sending command:"+command.getClass().getName());
			List<Handler> handlers = handlerRegistry.get(command.getClass());
			if (handlers == null) throw new RuntimeException("No command handlers registred for command ");
			if (handlers.size()!=1) throw new RuntimeException("There should be only one command handler");
			handlers.get(0).Invoke(command);			
		}		

		@Override
		public void registerHandler(Object handler) 
		{
			for(Method method : handler.getClass().getMethods()) {
				if (
						method.isAnnotationPresent(EventHandler.class) ||
						method.isAnnotationPresent(CommandHandler.class)
					)
				{
					Class<?> targetClass = method.getParameterTypes()[0];
					addHandler(targetClass, method, handler);					
				}
			}
		}
		private void addHandler(Class<?> type, Method m, Object instance) {
			List<Handler> registred = handlerRegistry.get(type);
			if (registred == null) {
				registred = new LinkedList<Handler>();
				handlerRegistry.put(type, registred);
			}
			Handler handler = new Handler();
			handler.instance = instance;
			handler.method = m;
			registred.add(handler);
		}
		
	}
	public class InventoryTestModule implements Module  {		
		@Override
		public void configure(Binder binder) {
			binder.bind(EventBus.class).to(TestEventBus.class);
			binder.bind(EventPublisher.class).to(TestEventBus.class);
			binder.bind(Repository.class).to(TestRepository.class);
			binder.bind(EventStore.class).to(TestEventStore.class);
		}
	}
	private class CreationTestingHandler {
		private final String expectingId;
		private final String expectingName;
		private boolean called = false;
		
		public CreationTestingHandler(String expectingId, String expectingName) {
			super();
			this.expectingId = expectingId;
			this.expectingName = expectingName;
		}
		@EventHandler
		public void handleInventoryItemCreated(InventoryEvents.InventoryItemCreated event) {
			if (event.name.equals(expectingName)) called = true;
		}
		public void AssertCalled() {
			assertTrue(called);
		}
	}


	@Test
	public void testCreateInventoryItem() {
		Injector injector = Guice.createInjector(new InventoryTestModule());
		EventBus bus  = injector.getInstance(EventBus.class);
		Repository repository = injector.getInstance(Repository.class);
		bus.registerHandler(new InventoryCommandHandlers(repository));
		
		final String inventoryId = "id1";
		final String inventoryName = "id2";
		final CreationTestingHandler testing = new CreationTestingHandler(inventoryId, inventoryName);

		bus.registerHandler(testing);
		bus.send(new InventoryCommands.CreateInventoryItem(inventoryId, inventoryName));
		testing.AssertCalled();
	}
}
