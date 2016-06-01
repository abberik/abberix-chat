package ver4;

import java.io.Serializable;

public class SystemMessage implements Serializable {

	/*
	 * Denna klass ar till for att skicka information fran klientprogrammet till
	 * servern ifall att anvandaren byter kanal eller anvandarnamn bor servern
	 * notifieras da skickas ett SystemMessage objekt med uppdaterad information
	 * 
	 */

	private String username; // anvandarens namn
	private String channel; // anvandarens kanal

	public SystemMessage(String username, String channel) {

		// initiera data
		this.username = username;
		this.channel = channel;

	}

	// getters och setters

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

}
