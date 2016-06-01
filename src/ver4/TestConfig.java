package ver4;

public class TestConfig {

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
