package simplejcqrs.tests;

import junit.framework.TestCase;

import org.junit.Test;

import simplejcqrs.commandhandlers.InventoryCommandHandlers;
import simplejcqrs.commands.InventoryCommands;
import simplejcqrs.domain.Repository;
import simplejcqrs.events.EventPublisher;
import simplejcqrs.events.EventStore;
import simplejcqrs.events.InventoryEvents;
import simplejcqrs.events.SimpleEventBus;
import simplejcqrs.events.SimpleEventStore;
import simplejcqrs.structural.EventBasedRepository;
import simplejcqrs.structural.EventBus;
import simplejcqrs.structural.EventHandler;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

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
		public void handleInventoryItemCreated(InventoryEvents.InventoryItemCreated event) {
			if (event.name.equals(expectingName)) called = true;
		}
		public void AssertCalled() {
			assertTrue(called);
		}
	}


	@Test
	public void testCreateInventoryItem() {
		Injector injector = Guice.createInjector();
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
