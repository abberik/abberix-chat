package ver4;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

public class User {

	private String kanal; // kanalen anvandaren befinner sig i, uppdateras
							// automatiskt via systemmedelanden om anvandaren
							// byter kanal.
	private String username; // anvandarens anvandarnamn, om anvandaren byter sa
								// uppdateras det likt kanalen.

	private Socket socket; // natsaker
	private ObjectInputStream inputStream;
	private ObjectOutputStream outputStream;
	private Server server;
	private boolean connected; // om anslutningen blir dalig

	private Runnable messageWaiter; // Runnable som vantar pa meddelanden
	private Thread messageWaiterThread; // trad dar man vantar pa nya
										// meddelanden.

	public User(Socket socket, Server server) {

		connected = true; // anta att anslutningen fungerar fran borjan.

		kanal = ""; // initiera objekt
		username = "";

		this.socket = socket;
		this.server = server;

		try {
			inputStream = new ObjectInputStream(socket.getInputStream()); // skapa
																			// strom
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			outputStream = new ObjectOutputStream(socket.getOutputStream());// skapa
																			// strom
		} catch (IOException e) {
			e.printStackTrace();
		}

		messageWaiter = new Runnable() { // skapa messageWaiter runnablen

			@Override
			public void run() {

				while (connected) {

					try {

						Object o = inputStream.readObject(); // hamta ett objekt
																// av hitils
																// okand typ

						if (o.getClass() == Message.class) { // om den ar av
																// typen Message

							// ett meddelande mottaget

							Message m = (Message) o;

							server.messageRecieved(m,getThis());

						} else if (o.getClass() == SystemMessage.class) { // om
																			// den
																			// ar
																			// av
																			// typen
																			// SystemMessage

							// ett systemmeddelande togs emot

							SystemMessage m = (SystemMessage) o;

							server.systemMessageRecieved(m, getThis());

						}

					} catch (SocketException se) { // natverksproblem,
													// anslutningen ar
													// troligtvis paj, battre
													// att anvandaren manuellt
													// far ateransluta.

						// SocketException kastas om klienten kor windows
						connected = false;

					} catch (EOFException eofex) {// natverksproblem,
													// anslutningen ar
													// troligtvis paj, battre
													// att anvandaren manuellt
													// far ateransluta.

						// EOFE kastas om klienten kor unix-baserat
						connected = false;

					} catch (Exception ex) {
						ex.printStackTrace();
					}

				}

				if (!connected) { // om anslutningen inte fungerar
					// sparka ut anvandaren ur systemet.

				}

			}

		};

		messageWaiterThread = new Thread(this.messageWaiter); // skapa trad
		messageWaiterThread.start(); // starta trad

	}

	// getters och setters

	public String getKanal() {
		return kanal;
	}

	public void setKanal(String kanal) {
		this.kanal = kanal;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public ObjectInputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(ObjectInputStream inputStream) {
		this.inputStream = inputStream;
	}

	public ObjectOutputStream getOutputStream() {
		return outputStream;
	}

	public void setOutputStream(ObjectOutputStream outputStream) {
		this.outputStream = outputStream;
	}

	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
	}

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	public Runnable getMessageWaiter() {
		return messageWaiter;
	}

	public void setMessageWaiter(Runnable messageWaiter) {
		this.messageWaiter = messageWaiter;
	}

	public Thread getMessageWaiterThread() {
		return messageWaiterThread;
	}

	public void setMessageWaiterThread(Thread messageWaiterThread) {
		this.messageWaiterThread = messageWaiterThread;
	}

	private User getThis() {
		return this;
	}

}
