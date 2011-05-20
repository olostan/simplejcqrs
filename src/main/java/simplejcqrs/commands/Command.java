package simplejcqrs.commands;

import java.io.Serializable;

@SuppressWarnings("serial")
public abstract class Command implements Serializable {
	private int originalVersion = -1;

	public Command() {
		super();
	}

	public Command(int originalVersion) {
		super();
		this.originalVersion = originalVersion;
	}

	public int getOriginalVersion() {
		return originalVersion;
	}

	public void setOriginalVersion(int originalVersion) {
		this.originalVersion = originalVersion;
	}
	
}
