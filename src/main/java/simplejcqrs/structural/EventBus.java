package simplejcqrs.structural;

import com.google.inject.ImplementedBy;

import simplejcqrs.commands.CommandSender;
import simplejcqrs.events.EventPublisher;
import simplejcqrs.events.SimpleEventBus;
import simplejcqrs.events.SimpleEventStore;

@ImplementedBy(SimpleEventBus.class)
public abstract class EventBus implements EventPublisher, CommandSender 
{
	//public abstract <T extends Event> void registerHandler(EventHandler<T> handler);
	public abstract void registerHandler(Object handler);
}
