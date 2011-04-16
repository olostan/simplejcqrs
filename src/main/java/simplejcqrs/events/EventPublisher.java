package simplejcqrs.events;

import com.google.inject.ImplementedBy;

@ImplementedBy(SimpleEventBus.class)
public interface EventPublisher {
	void publish(Event event);
}
