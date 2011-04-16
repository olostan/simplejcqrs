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
		void Invoke(Object... params) {
			try {
				method.invoke(instance, params);
			} catch (IllegalArgumentException e) {			
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.getTargetException().printStackTrace();
				throw new Error("Exception while calling handler",e);
			}			
		}
	}
	
	private Map<Class<?>,List<Handler> > handlerRegistry = new HashMap<Class<?>,List<Handler>>();
	
	@Override
	public void publish(Event event, String aggregateId) 
	{
		List<Handler> handlers = handlerRegistry.get(event.getClass());
		if (handlers == null || handlers.size()==0) return;
		for(Handler handler : handlers) handler.Invoke(event, aggregateId);
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
			boolean isHandler = false;
			if (method.isAnnotationPresent(EventHandler.class)) {
				Class<?>[] params = method.getParameterTypes();
				if (params.length != 2) throw new RuntimeException("Invalid number of parameters of event handler:"+method.getName()+" of "+handler.getClass().getName());
				if (!Event.class.isAssignableFrom(params[0])) new RuntimeException("First parameter should inherit from Event in event handler:"+method.getName()+" of "+handler.getClass().getName());
				if (params[1] != String.class) new RuntimeException("Second parameter should be String in event handler:"+method.getName()+" of "+handler.getClass().getName());
				isHandler = true;
			} else
			if (method.isAnnotationPresent(CommandHandler.class)) {
					Class<?>[] params = method.getParameterTypes();
					if (params.length != 1) throw new RuntimeException("Invalid number of parameters of command handler:"+method.getName()+" of "+handler.getClass().getName());
					if (!Command.class.isAssignableFrom(params[0])) new RuntimeException("First parameter should inherit from Command in command handler:"+method.getName()+" of "+handler.getClass().getName());
					isHandler = true;
			}

			if (isHandler) {
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