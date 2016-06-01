package ver4;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class Client extends JFrame {

	private static final long serialVersionUID = -2418884018480598940L;

	public static void main(String[] args) {
		new Client();
	}

	private String username; // anvandarnamnet
	private String kanal;
	private Panel messagePanel; // meddelandepanelen
	private JScrollPane jScrollPane; // scrollpanelen runt meddelandepanelen
	private JButton sendImageButton; // knappen for att skicka en bild
	private Action sendImage; // skickar en bild
	private JTextField messageField; // textfaltet for att skicka ett meddelande
	private Action submit; // skickar meddelande / kommando

	private Socket socket; // programmets socket till servern
	private ObjectOutputStream outputStream; // out och in strom till servern
	private ObjectInputStream inputStream;

	private String adress; // serverns IP
	private int portnr; // serverns port
	private JPanel fieldPanel;
	
	private Runnable messageWaiter; // behandlar inkommande meddelanden fran
									// servern.
	
	private GridBagLayout gridBagLayout;
	private GridBagConstraints gridBagConstraints;
	
	public Client() {

		this.username = "anonymous"; // man borjar med anvandarnamnet
										// "anonymous" man kan byta det ifall
										// man vill

		this.setSize(640, 480); // storleken pa fonstret ar 640,480 dock gar
								// storleken att andra
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // programmet bor
																// avslutas da
																// anvandaren
																// trycker pa
																// 'x'
		this.setLocationRelativeTo(null); // placera fonstret i mitten av
											// skarmen
		this.setLayout(new BorderLayout()); // framen (inte framen men framens
											// panel) bor ha en borderlayout
		this.setTitle("Anonymous\t@\tnot connected"); // ursprungliga titeln da
														// man varken ar
														// ansluten eller
														// namngiven
		messagePanel = new Panel(); // initiera meddelandepanelen

		sendImageButton = new JButton(); // initiera
																// skicka
																// bildknappen
		messageField = new JTextField(); // initiera meddelande/kommandofaltet

		kanal = "#root"; // nar man forst borjar hamnar man i root kanalen

		sendImage = new AbstractAction() { // initiera skicka bild actionen

			private static final long serialVersionUID = 3382152465205052735L;

			@Override
			public void actionPerformed(ActionEvent e) {

				// appna en JFileChooser och skicka bilden nar den ar klar

				JFileChooser fileChooser = new JFileChooser();

				File selectedFile = null; // den valda filen

				if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) { // om
																						// anvandaren
																						// valjer
																						// en
																						// fil,
																						// skicka
																						// den
					selectedFile = fileChooser.getSelectedFile(); // spara filen

					try {

						BufferedImage image = ImageIO.read(selectedFile); // las
																			// in
																			// bilden
																			// bakom
																			// filen
						Message message = new Message(getUsername(), getKanal(), image); // skapa
						// ett
						// meddelande
						// som
						// ska
						// skickas
						// till
						// server
						
						message.setMe(true);
						messagePanel.addMessage(message);	//lägg till meddelande på panel
						messagePanel.repaint(); // rita om meddelandepanelen
						jScrollPane.updateUI(); // uppdatera scrollpanelen
						
						outputStream.writeObject(message);// skicka bilden

					} catch (IOException e1) {
						e1.printStackTrace();
					}

				} else {
					return; // om anvandaren angrat sig mitt och stangt dialogen
							// behover intet mer goras
				}

			}

		};

		sendImageButton.addActionListener(sendImage); // koppla ihop skicka
														// bildhandlingen och
														// skicka bildknappen

		jScrollPane = new JScrollPane(messagePanel); // skapa scrollpanelen
		jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); // scrollpanelen
																						// bor
																						// alltid
																						// visa
																						// en
																						// vertikal
																						// scrollbar
		jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); // scrollpanelen
																								// bor
																								// endast
																								// visa
																								// en
																								// vertikal
																								// scrollbar
																								// om
																								// det
																								// behovs
		this.add(jScrollPane, BorderLayout.CENTER); // placera scrollpanelen i
													// mitten av fonstret
		this.fieldPanel = new JPanel();
		this.gridBagConstraints = new GridBagConstraints();
		this.gridBagLayout = new GridBagLayout();
		
		this.fieldPanel.setLayout(this.gridBagLayout);
		
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 0.875;
		
		fieldPanel.add(messageField,this.gridBagConstraints);
		
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weightx = 0.125;		
		
		fieldPanel.add(sendImageButton,this.gridBagConstraints);
		
		this.add(fieldPanel, BorderLayout.SOUTH); // placera meddelandefaltet och bildknappen längst ned
													// langst ned.


		messageWaiter = new Runnable() { // lyssnare efter nya meddelanden

			@Override
			public void run() {
				boolean connected = true; // sa lange anslutningen till servern
											// fungerar
				while (connected) {

					try {
						Object o = inputStream.readObject(); // las in
																// meddelandet

						messagePanel.addMessage((Message) o); // lagg till
																// meddelandet
																// pa
																// messagepaneln

						messagePanel.repaint(); // rita om meddelandepanelen
						jScrollPane.updateUI(); // uppdatera scrollpanelen

					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SocketException ex) { // om anslutningen inte
													// langre fungerar
						connected = false; // spara att ansluteningen inte
											// langre fungerar sa att loopen
											// inte langre fortsatter
						updateTitle(); // uppdatera titeln sa att det inte
										// langre star "connected"
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			}

		};

		submit = new AbstractAction() { // skicka meddelande eller kommando

			private static final long serialVersionUID = 4439840324610534071L;

			@Override
			public void actionPerformed(ActionEvent e) {

				if (messageField.getText().equals("")){
					
					
					
				}else if(messageField.getText().charAt(0) == '/') { // ett slash i
																// borjan
																// symboliserar
																// ett kommando

					// omvandla till en String array

					String[] command = messageField.getText().split(" ");

					if (command[0].equals("/connect")) { // anvandaren vill
															// ansluta till en
															// server

						try {

							String[] ip = command[1].split(":"); // dela ip'n
																	// till port
																	// och ip

							socket = new Socket(ip[0], Integer.parseInt(ip[1])); // anslut

							adress = ip[0]; // spara IP och portnr
							portnr = Integer.parseInt(ip[1]);

							messageField.setText("");

							outputStream = new ObjectOutputStream(socket.getOutputStream()); // spara
																								// strommarna
							inputStream = new ObjectInputStream(socket.getInputStream());

							updateTitle(); // uppdatera titeln
							updateServersInfo(); // informera servern om
													// information

							Thread t = new Thread(messageWaiter); // starta
																	// meddelandevantaren
																	// i en ny
																	// trad.
							t.start(); // starta traden

						} catch (NullPointerException ex) {
							// anvandaren har formaterat kommandot fel.
							JOptionPane.showMessageDialog(getClient(), "Fel: kommando ej uppfattat."); // meddela
																										// anvandaren
						} catch (IOException ex) {
							ex.printStackTrace();
						} catch (StringIndexOutOfBoundsException ex) {
							// om anvandaren inte skrivit nagot kommando eller
							// meddelande
							JOptionPane.showMessageDialog(getClient(), "Fel: kommando ej uppfattat."); // meddela
																										// anvandaren
						}

					} else if (command[0].equals("/nick")) { // anvandaren vill
																// byta namn

						String username = "";
						for (int i = 1; i < command.length; i++)
							username += command[i]; // satt namnet till allting
													// efter "/nick",
													// anvandarnamnet kan alltsa
													// innehalla space
						messageField.setText("");
						setUsername(username); // spara anvandarnamnet

						updateTitle(); // uppdatera fonstertiteln
						updateServersInfo(); // uppdatera serverns info

					} else if (command[0].equals("/join")) { // anvandaren gar
																// med i en
																// kanal

						// kanalnamnet far inte innehalla nagra mellanslag

						setKanal(command[1]); // uppdatera sparad information
						updateTitle(); // uppdatera GUIn
						updateServersInfo(); // skicka ett systemmedelande
						messageField.setText(""); // nollstall textfaltet
					} else if (command[0].equals("/leave")) { // anvandaren gar
																// ur en kanal

						// kanalnamnet far inte innehalla nagra mellanslag

						setKanal("#root"); // gar tillbaka till root kanalen
						updateTitle();
						updateServersInfo(); // talar om detta for servern
						messageField.setText("");

					} else if (command[0].equals("/exit")) {// anvandaren vill
															// avsluta
						System.exit(0); // avsluta programmet
					}

				} else { // annars ar det ett meddelandes

					// spara texten fran meddelanderutan till ett
					// meddelandeobjekt
					Message message = new Message(getUsername(), kanal, messageField.getText());
					message.setMe(true);
					messagePanel.addMessage(message);	//lägg till meddelande på panel
					messagePanel.repaint(); // rita om meddelandepanelen
					jScrollPane.updateUI(); // uppdatera scrollpanelen
					System.out.println("sent message");
					try {
						outputStream.writeObject(message); // skicka
															// meddelandeobjektet
					} catch (IOException e1) {
					} catch (NullPointerException ex) {
						if (outputStream == null) {
							// anvandaren forsoker skicka ett meddelande utan
							// att ansluta en server
							JOptionPane.showMessageDialog(getClient(),
									"Du maste ansluta till en server for att kunna chatta."); // meddela
																								// anvandaren
																								// att
																								// den
																								// har
																								// glomt
																								// ansluta
																								// en
																								// server
						} else {
							ex.printStackTrace();
						}
					}
				}
				messageField.setText(""); // rensa textfaltet

			}

		};

		messageField.addActionListener(submit); // lagg till lyssnaren till
												// meddelandefaltet sa att en
												// handelse genereras da
												// anvandaren trycker enter

		this.setVisible(true); // visa fonstret

	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Panel getMessagePanel() {
		return messagePanel;
	}

	public void setMessagePanel(Panel panel) {
		this.messagePanel = panel;
	}

	public JScrollPane getjScrollPane() {
		return jScrollPane;
	}

	public void setjScrollPane(JScrollPane jScrollPane) {
		this.jScrollPane = jScrollPane;
	}

	public JButton getSendImageButton() {
		return sendImageButton;
	}

	public void setSendImageButton(JButton sendImageButton) {
		this.sendImageButton = sendImageButton;
	}

	public Action getSendImage() {
		return sendImage;
	}

	public void setSendImage(Action sendImage) {
		this.sendImage = sendImage;
	}

	public JTextField getMessageField() {
		return messageField;
	}

	public void setMessageField(JTextField messageField) {
		this.messageField = messageField;
	}

	public Action getSubmit() {
		return submit;
	}

	public void setSubmit(Action submit) {
		this.submit = submit;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public ObjectOutputStream getOutputStream() {
		return outputStream;
	}

	public void setOutputStream(ObjectOutputStream outputStream) {
		this.outputStream = outputStream;
	}

	public ObjectInputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(ObjectInputStream inputStream) {
		this.inputStream = inputStream;
	}

	public Runnable getMessageWaiter() {
		return messageWaiter;
	}

	public void setMessageWaiter(Runnable messageWaiter) {
		this.messageWaiter = messageWaiter;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Client getClient() {
		return this;
	}

	public void updateTitle() {

		if (adress != null) { // om anvandaren anslutit en server
			setTitle(username + "  @ " + adress + ":" + portnr + "      " + kanal); // set
																					// titeln
			// till
			// anvandarnamnet
			// och
			// serverns
			// address
		} else { // om anvandaaren inte anslutit en server
			setTitle(username + "  @ not connected"); // satt titeln till
														// anvandarnamnet
														// (vilket eventuellt
														// kan vara "anonymous")
														// och att anvandaren
														// inte anslutit sig.
		}
	}

	public void updateServersInfo() { // ger servern aktuell information

		if (this.adress != null) {
			try {

				SystemMessage mes = new SystemMessage(this.getUsername(), this.getKanal()); // skapa
																							// ett
																							// meddelandeobjekt
				outputStream.writeObject(mes); // skicka meddelandet

			} catch (IOException e) {

				e.printStackTrace();

			}

		}
	}

	public String getKanal() {
		return kanal;
	}

	public void setKanal(String kanal) {
		this.kanal = kanal;
	}

	public String getAdress() {
		return adress;
	}

	public void setAdress(String adress) {
		this.adress = adress;
	}

	public int getPortnr() {
		return portnr;
	}

	public void setPortnr(int portnr) {
		this.portnr = portnr;
	}

}
