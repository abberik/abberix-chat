package ver4;

public class TestConfig {
	
	
	/*
	*
	* This file is for testing, its intendet to start a local config with two clients and a server,
	* not neccessary for actually using the program but it is handy for testing
	*
	*/
	
	public static void main(String[] args) {
		
		Client a = new Client();
		Client b = new Client();
		
		Server s = new Server();
		
		a.getMessageField().setText("/nick a");
		b.getMessageField().setText("/nick b");
		
		a.getSubmit().actionPerformed(null);
		b.getSubmit().actionPerformed(null);
		
		a.getMessageField().setText("/connect 127.0.0.1:1342");
		b.getMessageField().setText("/connect 127.0.0.1:1342");
		
		a.getSubmit().actionPerformed(null);
		b.getSubmit().actionPerformed(null);
	}
	
}
