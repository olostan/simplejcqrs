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
import simplejcqrs.events.HouseEvents;
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
	private static class TestHandler {
		protected boolean called = false;
		public void AssertCalled() {
			assertTrue("Expecting handlder was not called",called);			
		}
		public void AssertWasNotCalled() {
			assertFalse("Handler was called, but it was not expected",called);
		}
	}
	
	public static class InventoryItemCreationTestHandler extends TestHandler{
		private final long expectingId;
		private final String expectingName;
				
		public InventoryItemCreationTestHandler(long expectingId, String expectingName) {
			super();
			this.expectingId = expectingId;
			this.expectingName = expectingName;
		}
		@EventHandler
		public void handleInventoryItemCreated(InventoryEvents.InventoryItemCreated event, long Id) {			
			if (event.name.equals(expectingName) && Id==expectingId) {
				assertFalse("Creation handler should be called only once",called);
				called = true;
			}
		}
		public long getExpectingId() {
			return expectingId;
		}
		public String getExpectingName() {
			return expectingName;
		}	
		
	}
	public static class HouseCreationTestHandler extends TestHandler {
		private final long expectingId;
		private final String expectingAddress;
		public HouseCreationTestHandler(long expectingId,
				String expectingAddress) {
			super();
			this.expectingId = expectingId;
			this.expectingAddress = expectingAddress;
		}
		public long getExpectingId() {
			return expectingId;
		}
		public String getExpectingAddress() {
			return expectingAddress;
		}
		@EventHandler 
		public void handleHouseCreated(HouseEvents.HouseCreated event, long id) {
			if (event.getAddress().equals(expectingAddress) && id==expectingId) {
				assertFalse("Creation handler should be called only once",called);
				called = true;
			}
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
		
		final long inventoryId = 123;
		final String inventoryName = "name1";
		final InventoryItemCreationTestHandler testing = new InventoryItemCreationTestHandler(inventoryId, inventoryName);

		bus.registerHandler(testing);
		bus.send(new InventoryCommands.CreateInventoryItem(inventoryId, inventoryName));
		testing.AssertCalled();
	}
	@Test
	public void testCreateInventoryTwoItem() {
		EventBus bus  = createBus();
				
		final InventoryItemCreationTestHandler testing = new InventoryItemCreationTestHandler(111, "name1");
		final InventoryItemCreationTestHandler testing2 = new InventoryItemCreationTestHandler(222, "name2");

		bus.registerHandler(testing);
		bus.registerHandler(testing2);
		bus.send(new InventoryCommands.CreateInventoryItem(testing.getExpectingId(), testing.getExpectingName()));
		bus.send(new HouseCommands.CreateHouse(333, "a1"));
		bus.send(new InventoryCommands.CreateInventoryItem(testing2.getExpectingId(), testing2.getExpectingName()));
		testing.AssertCalled();
		testing2.AssertCalled();
	}	
	
	@Test
	public void testCreateDublicateInventoryItem() {
		EventBus bus  = createBus();

		final InventoryItemCreationTestHandler testing = new InventoryItemCreationTestHandler(777, "name1");
		final InventoryItemCreationTestHandler testing2 = new InventoryItemCreationTestHandler(777, "name2");	

		bus.registerHandler(testing);
		bus.registerHandler(testing2);
		bus.send(new InventoryCommands.CreateInventoryItem(testing.getExpectingId(), testing.getExpectingName()));
		boolean error = false;
		try {
			bus.send(new InventoryCommands.CreateInventoryItem(testing2.getExpectingId(), testing2.getExpectingName()));
		} catch (Error e) {
			error = true;
		}
		testing.AssertCalled();
		testing2.AssertWasNotCalled();
		assertTrue("Exception should happen when creating dublicate items", error);
	}
	@Test
	public void testCreateDifferentAggregates() {
		EventBus bus = createBus();
		
		final InventoryItemCreationTestHandler invHandler = new InventoryItemCreationTestHandler(444, "invname1");
		final HouseCreationTestHandler houseHandler = new HouseCreationTestHandler(3333, "Some address");
		bus.registerHandler(invHandler);
		bus.registerHandler(houseHandler);
		
		bus.send(new InventoryCommands.CreateInventoryItem(invHandler.getExpectingId(), invHandler.getExpectingName()));
		bus.send(new HouseCommands.CreateHouse(houseHandler.getExpectingId(), houseHandler.getExpectingAddress()));
		
		invHandler.AssertCalled();
		houseHandler.AssertCalled();
		
		
	}

}
