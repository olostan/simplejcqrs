package simplejcqrs.domain;

public class House extends AggregateRoot {
	@SuppressWarnings("unused")
	private String address;

	public House(String id, String address) {
		super(id);
		this.address = address;
	}
	
	
}
