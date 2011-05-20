package simplejcqrs.tests;

import java.util.Set;

import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;

import simplejcqrs.commandhandlers.CommandHandler;
import simplejcqrs.commandhandlers.HouseCommandHandlers;
import simplejcqrs.commandhandlers.HumanCommandHandlers;
import simplejcqrs.commandhandlers.InventoryCommandHandlers;
import simplejcqrs.commands.HumanCommands;
import simplejcqrs.commands.InventoryCommands;
import simplejcqrs.events.EventStore;
import simplejcqrs.events.HumanEvents;
import simplejcqrs.events.InventoryEvents;
import simplejcqrs.structural.EventBus;
import simplejcqrs.structural.EventHandler;
import junit.framework.TestCase;

public class HumanTest extends TestCase {
	private static class TestHandler {
		protected boolean called = false;
		protected int lastARVersion = -1;
		public void AssertCalled() {
			assertTrue("Expecting handlder was not called",called);			
		}
		public void AssertWasNotCalled() {
			assertFalse("Handler was called, but it was not expected",called);
		}
		public int getLastARVersion() {
			return lastARVersion;
		}		
		
	}
	public static class HumanCreationTestHandler extends TestHandler{
		private final long expectingId;
		private final String expectingName;
				
		public HumanCreationTestHandler(long expectingId, String expectingName) {
			super();
			this.expectingId = expectingId;
			this.expectingName = expectingName;
		}
		@EventHandler
		public void handleInventoryItemCreated(HumanEvents.HumanRegistred event, long Id) {
			if (Id==expectingId) {
				
				assertFalse("Creation handler should be called only once",called);
				assertEquals("Name should be correct", event.getFirstName(),expectingName);
				called = true;
				lastARVersion = event.getAggregateVersion();				
			}
		}
		public long getExpectingId() {
			return expectingId;
		}
		public String getExpectingName() {
			return expectingName;
		}			
	}
	private final class TestModule extends AbstractModule {
		@Override
		public void configure() {
			Multibinder<Object> m = Multibinder.newSetBinder(binder(), Object.class, CommandHandler.class);
			m.addBinding().to(HumanCommandHandlers.class);
			bind(EventStore.class)
			 .annotatedWith(Names.named("ActualStore"))
			.to(InMemoryEventStore.class);
		}		
	}	
	private EventBus createBus() {
		Injector injector = Guice.createInjector(new TestModule());
		EventBus bus  = injector.getInstance(EventBus.class);		  
		TypeLiteral<Set<Object>> t = new TypeLiteral<Set<Object>>() {};
		
		Set<Object> handlers = injector.getInstance(Key.get(t,CommandHandler.class));
		for (Object handler : handlers) {
			bus.registerHandler(handler);
		}
		return bus;
	}
	
	@Test
	public void testCreateHuman() {		
		EventBus bus = createBus();		
		final long id = 123;
		final String name = "name1";
		final HumanCreationTestHandler testing = new HumanCreationTestHandler(id, name);

		bus.registerHandler(testing);
		bus.send(new HumanCommands.RegisterHuman(id, name,name));
		testing.AssertCalled();
	}
	public static class HumanRenameHandler extends TestHandler {
		private final String expectingName;
		private final long expectingId;		
				
		public HumanRenameHandler(String expectingName, long expectingId) {
			super();
			this.expectingName = expectingName;
			this.expectingId = expectingId;
		}

		@EventHandler
		public void handleHumanRenamed(HumanEvents.HumanRenamed ev,long id) {
			if (id==expectingId) {
				if (ev.getFirstName().equals(expectingName)) {
					assertFalse("Rename should be called once",called);
					called = true;
				}
				lastARVersion = ev.getAggregateVersion();
			}
		}

		
		
	}
	@Test
	public void testRenameHuman() {		
		EventBus bus = createBus();		
		final long id = 123;
		final String name = "name1";
		final String name2 = "name2";
		final String name3 = "name3";
		final HumanCreationTestHandler testing = new HumanCreationTestHandler(id, name);
		final HumanRenameHandler testing2 = new HumanRenameHandler(name2,id);
		final HumanRenameHandler testing3 = new HumanRenameHandler(name3,id);
		bus.registerHandler(testing);
		bus.registerHandler(testing2);
		bus.registerHandler(testing3);
		bus.send(new HumanCommands.RegisterHuman(id, name,name));
		bus.send(new HumanCommands.RenameHuman(id, name2,name2,testing.getLastARVersion()));
		bus.send(new HumanCommands.RenameHuman(id, name3,name3,testing2.getLastARVersion()));
		
		testing.AssertCalled();
		testing2.AssertCalled();
		testing3.AssertCalled();
	}

}
