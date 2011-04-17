package simplejcqrs.tests;

import java.util.Set;

import junit.framework.TestCase;

import org.junit.Test;

import simplejcqrs.commandhandlers.CommandHandler;
import simplejcqrs.commandhandlers.HouseCommandHandlers;
import simplejcqrs.commandhandlers.InventoryCommandHandlers;
import simplejcqrs.commands.HouseCommands;
import simplejcqrs.commands.InventoryCommands;
import simplejcqrs.events.EventStore;
import simplejcqrs.events.InventoryEvents;
import simplejcqrs.structural.EventBus;
import simplejcqrs.structural.EventHandler;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;

public class InventoryItemTest extends TestCase {
	
	public static class CreationTestingHandler {
		private final String expectingId;
		private final String expectingName;
		private boolean called = false;
		
		public CreationTestingHandler(String expectingId, String expectingName) {
			super();
			this.expectingId = expectingId;
			this.expectingName = expectingName;
		}
		@EventHandler
		public void handleInventoryItemCreated(InventoryEvents.InventoryItemCreated event, String Id) {			
			if (event.name.equals(expectingName) && Id.equals(expectingId)) called = true;
		}
		public void AssertCalled() {
			assertTrue("Expecting "+expectingName+" ("+expectingId+")",called);			
		}
		public void AssertWasNotCalled() {
			assertFalse("Not expecting "+expectingName+" ("+expectingId+")",called);
		}
		public String getExpectingId() {
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
			m.addBinding().to(InventoryCommandHandlers.class);
			m.addBinding().to(HouseCommandHandlers.class);
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
	public void testCreateInventoryOneItem() {
		
		EventBus bus = createBus();
		
		final String inventoryId = "id1";
		final String inventoryName = "name1";
		final CreationTestingHandler testing = new CreationTestingHandler(inventoryId, inventoryName);

		bus.registerHandler(testing);
		bus.send(new InventoryCommands.CreateInventoryItem(inventoryId, inventoryName));
		testing.AssertCalled();
	}
	@Test
	public void testCreateInventoryTwoItem() {
		EventBus bus  = createBus();
				
		final CreationTestingHandler testing = new CreationTestingHandler("id1", "name1");
		final CreationTestingHandler testing2 = new CreationTestingHandler("id2", "name2");

		bus.registerHandler(testing);
		bus.registerHandler(testing2);
		bus.send(new InventoryCommands.CreateInventoryItem(testing.getExpectingId(), testing.getExpectingName()));
		bus.send(new HouseCommands.CreateHouse("h1", "a1"));
		bus.send(new InventoryCommands.CreateInventoryItem(testing2.getExpectingId(), testing2.getExpectingName()));
		testing.AssertCalled();
		testing2.AssertCalled();
	}	
	
	@Test
	public void testCreateDublicateInventoryItem() {
		EventBus bus  = createBus();

		
		final String inventoryId = "id1";
		final String inventoryName = "name1";
		final CreationTestingHandler testing = new CreationTestingHandler(inventoryId, inventoryName);
		
		final String inventoryId2 = "id1";
		final String inventoryName2 = "name2";
		final CreationTestingHandler testing2 = new CreationTestingHandler(inventoryId2, inventoryName2);

		bus.registerHandler(testing);
		bus.registerHandler(testing2);
		bus.send(new InventoryCommands.CreateInventoryItem(inventoryId, inventoryName));
		boolean error = false;
		try {
			bus.send(new InventoryCommands.CreateInventoryItem(inventoryId2, inventoryName2));
		} catch (Error e) {
			error = true;
		}
		testing.AssertCalled();
		testing2.AssertWasNotCalled();
		assertTrue("Exception should happen when creating dublicate items", error);
	}	

}
