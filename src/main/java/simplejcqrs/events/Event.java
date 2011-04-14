package simplejcqrs.events;

public class Event {
	private int aggregateVersion;

	public int getAggregateVersion() {
		return aggregateVersion;
	}

	public void setAggregateVersion(int aggregateVersion) {
		this.aggregateVersion = aggregateVersion;
	}

}
