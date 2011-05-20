package simplejcqrs.events;

public class ConcurrancyException extends RuntimeException {
	private static final long serialVersionUID = 6452002608360046179L;
	private int expected;
	private String rootName;
	public ConcurrancyException(int expected, String rootName) {
		super();
		this.expected = expected;
		this.rootName = rootName;
	}
	@Override
	public String getMessage() {
		return "Concurancy: Expected version "+expected+" for AggregateRoot "+rootName;
	}
	

}
