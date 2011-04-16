package simplejcqrs.commandhandlers;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.google.inject.BindingAnnotation;

/*
import simplejcqrs.commands.Command;

public interface CommandHandler<T extends Command> {
	void handle(T command);
}
*/
@Retention(RetentionPolicy.RUNTIME)
@BindingAnnotation
public @interface CommandHandler {
	
}
