package simplejcqrs.commandhandlers;

import simplejcqrs.commands.HumanCommands;
import simplejcqrs.domain.Human;
import simplejcqrs.domain.Repository;

import com.google.inject.Inject;

@CommandHandler
public class HumanCommandHandlers {
	private final Repository repository;
	@Inject
	public HumanCommandHandlers(Repository repository) {
		super();
		this.repository = repository;
	}
	@CommandHandler
	public void handleHumanRegistred(HumanCommands.RegisterHuman command) {
		if (repository.exists(Human.class, command.getHumanId())) {
			throw new RuntimeException("Human with same id already exists: "+command.getHumanId());
		}
		Human human = new Human(command.getHumanId(),command.getFirstName(), command.getLastName());
        repository.save(human, -1);
	}
}
