package simplejcqrs.commandhandlers;

import simplejcqrs.commands.InventoryCommands.CreateInventoryItem;
import simplejcqrs.domain.InventoryItem;
import simplejcqrs.domain.Repository;

public class InventoryCommandHandlers {
	private final Repository repository;

	public InventoryCommandHandlers(Repository repository) {
		super();
		this.repository = repository;
	}
	
	@CommandHandler
	public void Handle(CreateInventoryItem message)
    {
		InventoryItem item = new InventoryItem(message.getInventoryId(), message.getName());
        repository.save(item, -1);
    }
}
