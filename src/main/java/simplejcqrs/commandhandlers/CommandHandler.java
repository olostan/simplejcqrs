package simplejcqrs.commandhandlers;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/*
import simplejcqrs.commands.Command;

public interface CommandHandler<T extends Command> {
	void handle(T command);
}
*/
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandHandler {
	
}
