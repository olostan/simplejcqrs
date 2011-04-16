package simplejcqrs.commandhandlers;

import com.google.inject.Inject;

import simplejcqrs.commands.InventoryCommands.CreateInventoryItem;
import simplejcqrs.domain.InventoryItem;
import simplejcqrs.domain.Repository;

@CommandHandler
public class InventoryCommandHandlers {
	private final Repository repository;

	@Inject
	public InventoryCommandHandlers(Repository repository) {
		super();
		this.repository = repository;
	}
	
	@CommandHandler
	public void Handle(CreateInventoryItem message)
    {
		if (repository.exists(InventoryItem.class, message.getInventoryId())) {
			throw new RuntimeException("Inventory Item with same id already exists: "+message.getInventoryId());
		}
		InventoryItem item = new InventoryItem(message.getInventoryId(), message.getName());
        repository.save(item, -1);
    }
}
