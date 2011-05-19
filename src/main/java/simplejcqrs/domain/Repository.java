package simplejcqrs.domain;

import simplejcqrs.structural.EventBasedRepository;

import com.google.inject.ImplementedBy;

@ImplementedBy(EventBasedRepository.class)
public interface Repository {
	<T extends AggregateRoot> T load(Class<T> rootClass, long id);
	void save(AggregateRoot root, int expectedVersion);
	<T extends AggregateRoot> boolean exists(Class<T> rootClass, long id);
}
