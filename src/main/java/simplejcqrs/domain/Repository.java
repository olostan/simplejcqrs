package simplejcqrs.domain;

public interface Repository {
	<T extends AggregateRoot> T load(Class<T> rootClass, String id);
	void save(AggregateRoot root, int expectedVersion);		
}
