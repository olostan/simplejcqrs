package simplejcqrs.structural;

import simplejcqrs.commands.CommandSender;
import simplejcqrs.events.EventPublisher;
import simplejcqrs.events.SimpleEventBus;

import com.google.inject.ImplementedBy;

@ImplementedBy(SimpleEventBus.class)
public abstract class EventBus implements EventPublisher, CommandSender 
{
	public abstract void registerHandler(Object handler);
}
