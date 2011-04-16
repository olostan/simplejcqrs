package simplejcqrs.commandhandlers;

import com.google.inject.Inject;

import simplejcqrs.commands.HouseCommands;
import simplejcqrs.domain.House;
import simplejcqrs.domain.Repository;

@CommandHandler
public class HouseCommandHandlers {
	private final Repository repository;

	@Inject
	public HouseCommandHandlers(Repository repository) {
		super();
		this.repository = repository;
	}
	
	@CommandHandler
	public void handleHouseCreated(HouseCommands.CreateHouse command) {
		if (repository.exists(House.class, command.getHouseId())) {
			throw new RuntimeException("House with same id already exists: "+command.getHouseId());
		}
		House house = new House(command.getHouseId(), command.getAddress());
        repository.save(house, -1);
	}
	
}
