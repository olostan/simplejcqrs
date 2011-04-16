package simplejcqrs.events;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import simplejcqrs.commandhandlers.CommandHandler;
import simplejcqrs.commands.Command;
import simplejcqrs.structural.EventBus;
import simplejcqrs.structural.EventHandler;

import com.google.inject.Singleton;

@Singleton 
public class SimpleEventBus extends EventBus {
	
	private static class Handler {
		public Object instance;
		public Method method;
		void Invoke(Object param) {
			try {
				method.invoke(instance, param);
			} catch (IllegalArgumentException e) {			
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}			
		}
	}
	
	private Map<Class<?>,List<Handler> > handlerRegistry = new HashMap<Class<?>,List<Handler>>();
	
	@Override
	public void publish(Event event) 
	{
		List<Handler> handlers = handlerRegistry.get(event.getClass());
		if (handlers == null || handlers.size()==0) return;
		for(Handler handler : handlers) handler.Invoke(event);
	}

	@Override
	public void send(Command command) 
	{
		List<Handler> handlers = handlerRegistry.get(command.getClass());
		if (handlers == null) throw new RuntimeException("No command handlers registred for command ");
		if (handlers.size()!=1) throw new RuntimeException("There should be only one command handler");
		handlers.get(0).Invoke(command);			
	}		

	@Override
	public void registerHandler(Object handler) 
	{
		for(Method method : handler.getClass().getMethods()) {
			if (
					method.isAnnotationPresent(EventHandler.class) ||
					method.isAnnotationPresent(CommandHandler.class)
				)
			{
				Class<?> targetClass = method.getParameterTypes()[0];
				addHandler(targetClass, method, handler);					
			}
		}
	}
	private void addHandler(Class<?> type, Method m, Object instance) {
		List<Handler> registred = handlerRegistry.get(type);
		if (registred == null) {
			registred = new LinkedList<Handler>();
			handlerRegistry.put(type, registred);
		}
		Handler handler = new Handler();
		handler.instance = instance;
		handler.method = m;
		registred.add(handler);
	}
	
}