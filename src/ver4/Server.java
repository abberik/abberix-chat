package ver4;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import javax.swing.plaf.synth.SynthSeparatorUI;

public class Server {

	public static void main(String[] args) {
		new Server();
	}

	private Runnable host; // tar emot alla nya anslutningar och skapar nya
							// anvandarobjekt
	private ServerSocket serverSocket; // serversocketen

	private ArrayList<User> users; // lista av anvandare

	public Server() {

		users = new ArrayList<User>(); // initiera anvandarlista

		try { // serversocketen startas
			serverSocket = new ServerSocket(1342); // kor pa 1342 darfor att den
													// inte verkade upptagen med
													// nagot annat program.
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace(); // om denna sker ar nagonting fel,
									// administratoren bor alerteras.
		}

		// lagger konstant till nya hostar
		host = new Runnable() {

			@Override
			public void run() {

				while (true) {

					try {

						Socket socket = serverSocket.accept(); // vanta pa en ny
																// anslutning

						User user = new User(socket, getThis()); // skapa nytt
																	// anvandarobjekt
						users.add(user); // lagg till anvandaren i listan.

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			}

		};
		new Thread(host).start(); // borja vanta efter klienter i huvudtraden

	}

	public void systemMessageRecieved(SystemMessage message, User user) {

		// metod som behandlar mottagna systemmedelanden

		if (!user.getKanal().equals(message.getChannel())) { // informera
																// administrator
																// om att
																// anvandaren
																// bytt kanal om
																// kanalen
																// forandrats
			System.out.println(
					user.getUsername() + " has changed channel " + user.getKanal() + " -> " + message.getChannel());
		}

		if (!user.getUsername().equals(message.getUsername())) { // informera
																	// administrator
																	// om att
																	// anvandaren
																	// bytt namn
																	// om namnet
																	// forandrats
			System.out.println(user.getUsername() + " has changed name into " + user.getUsername());
		}

		user.setKanal(message.getChannel()); // uppdatera anvandarens kanal
		user.setUsername(message.getUsername()); // uppdatera anvandarens namn

	}

	public void messageRecieved(Message m,User user) { // metod som
												// behandlar
												// mottaget
												// meddelande

		System.out.println(m.getSenderName() + ":\t" + m.getText() + "\t" + m.getKanal()); // notifiera
		// om
		// meddelandet
		// i
		// terminalen

		m.setMe(false);
		
		for (User u : users) { // skicka meddelandet
								// till varje
								// anvandare
			try {

				if (u != user &&u.isConnected() && m.getKanal().equals(u.getKanal())) { // om
																			// anvandaren
																			// ar
																			// ansluten
																			// och
																			// meddelandet
																			// gar
																			// i
																			// ratt
																			// kanal
					u.getOutputStream().writeObject(m); // skicka meddelandet
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	private Server getThis() {
		return this;
	}

}
