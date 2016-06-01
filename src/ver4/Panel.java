package ver4;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class Panel extends JPanel implements MouseListener { 

	/**
	 * 
	 */
	private static final long serialVersionUID = 8952237730640738855L;
	private ArrayList<Message> messages; // dar meddelandena sparas ( med
											// bilderna i fullt format).
	private ArrayList<Image> scaledImages;// har sparas skalade versioner av
											// bilderna som direkt anvands for
											// att spara prestanda
	private int aWidth; // hur bred meddelandeytan bar vara
	private int aHeight; // hur hag meddelandeytan bar vara
	private static int tpr = 50; // hur manga bokstaver som ska fa finnas per
									// rad
	private ArrayList<Rectangle> imageBorders;
	

	public Panel() {

		messages = new ArrayList<Message>(); // initiera arraylist far
												// meddelanden
		scaledImages = new ArrayList<Image>();
		aWidth = 480; // bredden ska vara konstant
		aHeight = 0; // hajden ska vara 0 fran barjan, nar ett meddelande laggs
						// till kalkuleras dess paverkan pa hajden
		imageBorders = new ArrayList<Rectangle>();
		this.addMouseListener(this);
	}

	public void addMessage(Message m) {
		imageBorders.add(null);		//så att man inte får en out of boundsexception
		messages.add(m);

		// om det ar en bild lagg till 100 bildpunkter (tanken ar att alla
		// bilder ska skalas sa dess langsta dimension ar mindre an 100
		if (m.isImage()) {
			aHeight += (150 + 15); // 15 punkter far anvandarnamnet, 115 far
									// bilden o luft

			// skala bilden och lagg till den i bild listan

			// skala bild
			Image image = null;
			if (m.getBild().getHeight(null) > 100) { // hajden begransar
				image = m.getBild().getScaledInstance(-1, 100, Image.SCALE_DEFAULT); // skala
																						// bilden
			} else if (m.getBild().getWidth(null) > 100) { // bredden begransar
				image = m.getBild().getScaledInstance(100, -1, Image.SCALE_DEFAULT); // skala
																						// bilden
			}

			scaledImages.add(image); // spara den skalade kopian
			
		} else {
			// rakna ut hur mycket plats strangen vill ha
			int rader = m.getText().length() / tpr; // 50 tecken per rad
			rader++; // rakna med en extra rad efter
			aHeight += ((rader * 15) + 30); // 15 pixlar per rad

			// lagg till en tom plats i bild listan
			scaledImages.add(null);
		}

		this.setPreferredSize(new Dimension(aWidth, aHeight)); // tvinga
																// jscrollPanen
																// att anvanda
																// den bestamda
																// dimensionen
		this.setMinimumSize(new Dimension(aWidth, aHeight));
		this.setMaximumSize(new Dimension(aWidth, aHeight));
	}

	@Override
	protected void paintComponent(Graphics g) {

		g.setColor(Color.WHITE); // rita bakgrunden vit
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		g.setColor(Color.BLACK); // textfargen bor vara svart

		int usedSpace = 15; // lamna lite utrymme

		g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12)); // satt font

		// rita alla meddelanden
		for (int a = 0; a < messages.size(); a++) {

			Message m = messages.get(a);

			if (m.isImage()) { // om meddelandet innehaller en bild

			
				
				if(m.isMe()){
					g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));
					g.drawString(m.getSenderName() + ":", 10, usedSpace);
					g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
					
				}else{
					g.drawString(m.getSenderName() + ":", 10, usedSpace);
				}
				
				
				usedSpace += 15; // rita anvandarnamn
				g.drawImage(scaledImages.get(a), 10, usedSpace, scaledImages.get(a).getWidth(null),
						scaledImages.get(a).getHeight(null), null);
				imageBorders.set(a,new Rectangle(10,usedSpace,scaledImages.get(a).getWidth(null), scaledImages.get(a).getHeight(null)));
				usedSpace += 115; // rita bilden pa avsedd plats
				
				

			} else {
				
				// Dela upp i substrings (en per rad)
				String string = ""; // den nuvarande substringen som det arbetas
									// med
				ArrayList<String> rader = new ArrayList<String>(); // lista med
																	// rader av
																	// meddelandet
				char[] bokstaver = m.getText().toCharArray(); // dela upp i
																// char[] for
																// att lattare
																// kunna hantera

				for (int i = 0; i < bokstaver.length; i++) { // for varje
																// bokstav

					if ((i % tpr == 0) && (i != 0)) { // dela upp det pa korrekt
														// satt med tpr tecken
														// per rad

						if (bokstaver[i - 1] != ' ') {
							string += "-";
						} // om ett ord bryts ska det visas med ett streck
						rader.add(string); // lagg till raden i rader
						string = ""; // ny rad

					}

					string += bokstaver[i]; // lagg till bokstaven

				}

				rader.add(string); // lagg till den sista raden

				// rita texten

				if(m.isMe()){
					g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));
					g.drawString(m.getSenderName() + ":", 10, usedSpace);
					g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
					
				}else{
					g.drawString(m.getSenderName() + ":", 10, usedSpace);
				}
				
				
				usedSpace += 15;

				for (String s : rader) {// for varje rad

					g.drawString(s, 10, usedSpace);
					usedSpace += 15; // rita rad och lamna 15px per rad

				}

				usedSpace += 15; // lamna en blank rad efter

			}

		}

	}

	public ArrayList<Message> getMessages() {
		return messages;
	}

	public void setMessages(ArrayList<Message> messages) {
		this.messages = messages;
	}

	public ArrayList<Image> getScaledImages() {
		return scaledImages;
	}

	public void setScaledImages(ArrayList<Image> scaledImages) {
		this.scaledImages = scaledImages;
	}

	public int getaWidth() {
		return aWidth;
	}

	public void setaWidth(int aWidth) {
		this.aWidth = aWidth;
	}

	public int getaHeight() {
		return aHeight;
	}

	public void setaHeight(int aHeight) {
		this.aHeight = aHeight;
	}

	public static int getTpr() {
		return tpr;
	}

	public static void setTpr(int tpr) {
		Panel.tpr = tpr;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {

	}

	private int isInImage(int x, int y) {
		
		int index = -1;
		
		for(int i = 0; i < imageBorders.size(); i++){
			
			if(imageBorders.get(i) != null){
			Rectangle r = imageBorders.get(i);
				if( (r.getMinX() < x) && (r.getMaxX() > x) && (r.getMinY() < y) && (r.getMaxY() > y)){
					index = i;		
					break;	
				}
			}
		}
			
		return index;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
		int i = isInImage(e.getX(),e.getY());
		
		if(-1 != i){
	
			
			JFrame f = new JFrame();
			BufferedImage img = messages.get(i).getBild();
			JLabel l = new JLabel(new ImageIcon(messages.get(i).getRgbData()));
			
			boolean big = false;
			
			if((img.getWidth() > (Toolkit.getDefaultToolkit().getScreenSize().getWidth() - 100 ))  ||    (img.getHeight() > (Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 100 ))){
				
				big = true;
				f.setSize(  (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() - 150    , (int)  Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 100  );
				f.setLocationRelativeTo(null);
			}else{
				f.setSize(img.getWidth(), img.getHeight());
				big = false;
				f.setLocationRelativeTo(this);
			}
			
			
			
			
//			f.setUndecorated(true);
			f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			
		
			MouseListener m = new MouseListener(){

				@Override
				public void mouseClicked(MouseEvent arg0) {
					
					f.dispose();
					
					
				}

				@Override
				public void mouseEntered(MouseEvent arg0) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mouseExited(MouseEvent arg0) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mousePressed(MouseEvent arg0) {
					// TODO Auto-generated method stub	
					
				}

				@Override
				public void mouseReleased(MouseEvent arg0) {
					// TODO Auto-generated method stub
					
				}
				
			};
			f.addMouseListener(m);
			
			KeyListener k = new KeyListener(){

				@Override
				public void keyPressed(KeyEvent arg0) {
					// TODO Auto-generated method stub
					int kc = arg0.getKeyCode();
					if(kc == arg0.VK_ESCAPE) f.dispose();
				}

				@Override
				public void keyReleased(KeyEvent arg0) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void keyTyped(KeyEvent arg0) {
					// TODO Auto-generated method stub
					
				}
				
				
				
			};
			f.addKeyListener(k);
			
			
			if(!big) {
				f.add(l);
		
			
			
			
			
			
			
			
			}else{

				JScrollPane jScrollPane = new JScrollPane(l);
				
				jScrollPane.setVerticalScrollBarPolicy(jScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
				jScrollPane.setHorizontalScrollBarPolicy(jScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				
				f.add(jScrollPane);
				
			}
			
			f.setVisible(true);
			
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
