package simplejcqrs.events;

public class Event {
	private int aggregateVersion;
	private final String aggregateRootId;

	public int getAggregateVersion() {
		return aggregateVersion;
	}

	public void setAggregateVersion(int aggregateVersion) {
		this.aggregateVersion = aggregateVersion;
	}

	public Event(String aggregateRootId) {
		super();
		this.aggregateRootId = aggregateRootId;
	}

	public String getAggregateRootId() {
		return aggregateRootId;
	}
	
	
}
