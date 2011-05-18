package simplejcqrs.domain;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;

import simplejcqrs.events.Event;

public abstract class AggregateRoot {
	private final LinkedList<Event> changes = new LinkedList<Event>();
	
	private final long id;
	private int version;
			
	public AggregateRoot(long id) {
		super();
		this.id = id;
	}
	
		
	public long getId() {
		return id;
	}

	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}

	public Collection<Event> getChanges() {
		return changes;
	}
	public void clearChanges() {
		changes.clear();
	}
	public void loadFromHistory(Iterable<Event> events) {
		for (Event event : events) {
			ApplyChange(event,false);
		}
	}
	
	protected void ApplyChange(Event event) {
		ApplyChange(event, true);
	}
	
	private void ApplyChange(Event event, boolean isNew) {
		invokeApplyUsingReflection(event);
		if (isNew) changes.add(event);
		
	}
	private void invokeApplyUsingReflection(Event event) {
		for (Method method : this.getClass().getMethods()) {
			Class<?>[] params = method.getParameterTypes();
			if (params.length == 1 && params[0] == event.getClass()) {
				try {
					method.invoke(this,event);
					break;
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
					throw new RuntimeException("Could not call apply method with one argument",e);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					throw new RuntimeException("Couldn't get access to call apply mehtod",e);
				} catch (InvocationTargetException e) {
					e.printStackTrace();
					throw new RuntimeException("There was error while invoking apply mehtod",e);
				}				
			}
		}		
	}
	
}
