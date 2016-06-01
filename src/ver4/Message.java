package ver4;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import javax.imageio.ImageIO;

public class Message implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7955404936788278629L;

	// kan antingen innehalla en bild eller en textbit

	private String text; // om meddelandet ar i textform sparas det har
	public byte[] rgbData; // ska spaas som int[] da klassen BufferedImage inte
							// ar serialiserbar (nagon javla sopa pa oracle
							// behover lara sig programmera!)
	private String senderName; // har sparas avsandarens anvandarnamn
	private String kanal; // kanalen meddelandet skickades i
	private boolean me; //ar jag avsandare till detta meddelande
	
	public Message(String senderName, String kanal, String text) throws IllegalArgumentException { // konstruktor
																									// avsett
																									// far
																									// ett
																									// textformat
																									// meddelande

		if (text == null) {
			throw new IllegalArgumentException();
		} // meddelandet (i korrekt form) far aldrig vara null

		this.senderName = senderName; // spara data
		this.text = text;
		this.rgbData = null; // den typ meddelandet inte ar i blir till null
		this.kanal = kanal;
	}

	public Message(String senderName, String kanal, BufferedImage bufferedImage) throws IllegalArgumentException { // konstruktor
																													// avsett
																													// far
																													// ett
																													// bildformat
																													// meddelande

		if (bufferedImage == null) {
			throw new IllegalArgumentException();
		} // meddelandet (i korrekt form) far aldrig vara null
		this.senderName = senderName; // spara data
		this.rgbData = Message.imageToByteArray(bufferedImage); // spara bilden
																// so en
																// bytearray (da
																// Bufferedimage
																// inte ar
																// serialiserad)
		this.text = null; // den typ meddelandet inte ar i blir till null
		this.kanal = kanal;
	}

	// getters och setters

	public String getText() {
		if (this.text != null) { // om texten inte ar null ar meddelandets
									// huvudinnehall texten
			return text; // returnera da texten
		} else {
			return "an image"; // returnera annars att det ar en bild
		}
	}

	public BufferedImage getBild() {
		return Message.byteArrayToImage(rgbData);
	}

	public String getSenderName() {
		return senderName;
	}

	public boolean isImage() { // returnerar true ifall att det ar en bild,
								// false ifall att det ar en text, att den inte
								// har nagon annan form kontrolleras av
								// felhantering i konstruktorer

		if (text == null /*
							 * om meddelandets text ar null bor huvudinnehallet i
							 * meddelandet vara bilden
							 */) {
			return true;
		} else if (rgbData == null/*
									 * om bilden ar null bor huvudinnehallet i
									 * meddelandet vara text
									 */) {
			return false;
		}
		return false; // kommer inte hit tack vare kontroll i konstruktorerna

	}

	public static byte[] imageToByteArray(BufferedImage image) { // omvandla
																	// BufferedImage
																	// till
																	// byte[] av
																	// rgbdata
		try {

			ByteArrayOutputStream baos = new ByteArrayOutputStream(); // skapa
																		// en
																		// output
																		// stream
			ImageIO.write(image, "png", baos); // skriv bilden till en bytearray
												// via strommen
			return baos.toByteArray();

		} catch (IOException e) {

			e.printStackTrace();
			return null;

		}

	}

	public static BufferedImage byteArrayToImage(byte[] image) { // omvandla
																	// bytearray
																	// av
																	// rgbdata
																	// till
																	// bufferedimage

		try {

			InputStream in = new ByteArrayInputStream(image); // skapa en strom
			BufferedImage bImageFromConvert = ImageIO.read(in); // las bilden
																// fran en
																// bytearray
			return bImageFromConvert; // returnera bilden.

		} catch (IOException e) {

			// TODO Auto-generated catch block
			e.printStackTrace();

		}

		return null;

	}

	public byte[] getRgbData() {
		return rgbData;
	}

	public void setRgbData(byte[] rgbData) {
		this.rgbData = rgbData;
	}

	public String getKanal() {
		return kanal;
	}

	public void setKanal(String kanal) {
		this.kanal = kanal;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public boolean isMe() {
		return me;
	}

	public void setMe(boolean me) {
		this.me = me;
	}	

}
