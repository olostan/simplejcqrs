package simplejcqrs.structural;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/*
public interface EventHandler<T extends Event> {
	void handle(T event);
}*/
@Retention(RetentionPolicy.RUNTIME)
public @interface EventHandler {
}
