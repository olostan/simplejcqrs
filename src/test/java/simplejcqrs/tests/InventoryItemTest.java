package simplejcqrs.tests;

import junit.framework.TestCase;

import org.junit.Test;

import simplejcqrs.commandhandlers.InventoryCommandHandlers;
import simplejcqrs.commands.InventoryCommands;
import simplejcqrs.domain.Repository;
import simplejcqrs.events.InventoryEvents;
import simplejcqrs.structural.EventBus;
import simplejcqrs.structural.EventHandler;

import com.google.inject.Guice;
import com.google.inject.Injector;

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
			assertFalse("Expecting "+expectingName+" ("+expectingId+")",called);
		}
	}


	@Test
	public void testCreateInventoryOneItem() {
		Injector injector = Guice.createInjector();
		EventBus bus  = injector.getInstance(EventBus.class);
		Repository repository = injector.getInstance(Repository.class);
		bus.registerHandler(new InventoryCommandHandlers(repository));
		
		final String inventoryId = "id1";
		final String inventoryName = "name1";
		final CreationTestingHandler testing = new CreationTestingHandler(inventoryId, inventoryName);

		bus.registerHandler(testing);
		bus.send(new InventoryCommands.CreateInventoryItem(inventoryId, inventoryName));
		testing.AssertCalled();
	}
	@Test
	public void testCreateInventoryTwoItem() {
		Injector injector = Guice.createInjector();
		EventBus bus  = injector.getInstance(EventBus.class);
		Repository repository = injector.getInstance(Repository.class);
		bus.registerHandler(new InventoryCommandHandlers(repository));
		
		final String inventoryId = "id1";
		final String inventoryName = "name1";
		final CreationTestingHandler testing = new CreationTestingHandler(inventoryId, inventoryName);
		
		final String inventoryId2 = "id2";
		final String inventoryName2 = "name2";
		final CreationTestingHandler testing2 = new CreationTestingHandler(inventoryId2, inventoryName2);

		bus.registerHandler(testing);
		bus.registerHandler(testing2);
		bus.send(new InventoryCommands.CreateInventoryItem(inventoryId, inventoryName));
		bus.send(new InventoryCommands.CreateInventoryItem(inventoryId2, inventoryName2));
		testing.AssertCalled();
		testing2.AssertCalled();
	}	
	
	@Test
	public void testCreateDublicateInventoryItem() {
		Injector injector = Guice.createInjector();
		EventBus bus  = injector.getInstance(EventBus.class);
		Repository repository = injector.getInstance(Repository.class);
		bus.registerHandler(new InventoryCommandHandlers(repository));
		
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
