package simplejcqrs.events;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Event  implements Serializable{
	@Retention(RetentionPolicy.RUNTIME)
	public @interface CreationEvent {} 
	
	private int aggregateVersion=-1;

	public int getAggregateVersion() {
		return aggregateVersion;
	}

	public void setAggregateVersion(int aggregateVersion) {
		this.aggregateVersion = aggregateVersion;
	}

}
