package simplejcqrs.structural;

import simplejcqrs.commands.CommandSender;
import simplejcqrs.events.Event;
import simplejcqrs.events.EventPublisher;

public abstract class EventBus implements EventPublisher, CommandSender 
{
	//public abstract <T extends Event> void registerHandler(EventHandler<T> handler);
	public abstract void registerHandler(Object handler);
}
