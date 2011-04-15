package simplejcqrs.commands;

public interface CommandSender {
	void send(Command command);
}
