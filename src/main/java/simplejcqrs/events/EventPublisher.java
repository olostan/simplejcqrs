package simplejcqrs.events;

public interface EventPublisher {
	void publish(Event event);
}
