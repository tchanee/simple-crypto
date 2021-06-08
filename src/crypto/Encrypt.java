package crypto;
import java.util.Random;
import static crypto.Helper.*;

public class Encrypt {
	
	public static final int CAESAR = 0;
	public static final int VIGENERE = 1;
	public static final int XOR = 2;
	public static final int ONETIME = 3;
	public static final int CBC = 4; 
	
	public static final byte SPACE = 32;
	
	final static Random rand = new Random();
	
	//-----------------------General-------------------------
	
	/**
	 * General method to encode a message using a key, you can choose the method you want to use to encode.
	 * @param message the message to encode already cleaned
	 * @param key the key used to encode
	 * @param type the method used to encode : 0 = Caesar, 1 = Vigenere, 2 = XOR, 3 = One time pad, 4 = CBC
	 * 
	 * @return an encoded String
	 * if the method is called with an unknown type of algorithm, it returns the original message
	 */
	public static String encrypt(String message, String key, int type) {
		// TODO: Done

		String encryptedString = "";

		int sizeMessage = message.length();
		int sizeKey = key.length();
		int keyInt = (int) key.charAt(0);


		byte[] encryptedMessage = new byte[message.length()];
		byte[] messageInit = stringToBytes(message);
		byte[] keyInit = stringToBytes(key);
		byte[] masque = generatePad(sizeMessage);
		byte[] cbcMasque = generatePad(sizeKey);
		byte[] messageVig = stringToBytes(message);

		if (type == CAESAR)
			encryptedMessage = caesar(messageInit,(byte)keyInt,false);

		if (type == VIGENERE)
			for(int i = 0; i < keyInit.length; ++i){
				keyInit[i] = (byte) (keyInit[i] - 48);
				messageVig[i] = (byte) (messageVig[i]);
			}
			encryptedMessage = vigenere(messageVig,keyInit,false);

		if (type == XOR)
			encryptedMessage = xor(messageInit,(byte)keyInt,false);

		if (type == ONETIME)
			encryptedMessage = oneTimePad(messageInit,masque);

		if (type == CBC)
			encryptedMessage = cbc(messageInit, cbcMasque);

		encryptedString = bytesToString(encryptedMessage);

		return encryptedString; // TODO: Done
	}



	//-----------------------Caesar-------------------------
	
	/**
	 * Method to encode a byte array message using a single character key
	 * the key is simply added to each byte of the original message
	 * @param plainText The byte array representing the string to encode
	 * @param key the byte corresponding to the char we use to shift
	 * @param spaceEncoding if false, then spaces are not encoded
	 * @return an encoded byte array
	 */
	public static byte[] caesar(byte[] plainText, byte key, boolean spaceEncoding) {
		// TODO: COMPLETE THIS METHOD: done
		assert (plainText!= null);
		int keyInt = key;
		for(int i = 0; i < plainText.length; ++i){
			int byteAtPosI = plainText[i];
			if (spaceEncoding || byteAtPosI != 32) {
				byteAtPosI += keyInt;
				byte temp = (byte) byteAtPosI;
				plainText[i] = temp;
			}
		}
		return plainText; // TODO: to be modified: done
	}
	
	/**
	 * Method to encode a byte array message  using a single character key
	 * the key is simply added  to each byte of the original message
	 * spaces are not encoded
	 * @param plainText The byte array representing the string to encode
	 * @param key the byte corresponding to the char we use to shift
	 * @return an encoded byte array
	 */
	public static byte[] caesar(byte[] plainText, byte key) {
		// TODO: COMPLETE THIS METHOD: done
		assert (plainText!= null);
		int keyInt = key;
		for(int i = 0; i < plainText.length; ++i){
			int byteAtPosI = plainText[i];
			if (byteAtPosI != 32) {
				byteAtPosI += keyInt;
				byte temp = (byte) byteAtPosI;
				plainText[i] = temp;
			}
		}
		return plainText; // TODO: to be modified: done
	}

	//-----------------------XOR-------------------------
	
	/**
	 * Method to encode a byte array using a XOR with a single byte long key
	 * @param plainText the byte array representing the string to encode
	 * @param key the byte we will use to XOR
	 * @param spaceEncoding if false, then spaces are not encoded
	 * @return an encoded byte array
	 */
	public static byte[] xor(byte[] plainText, byte key, boolean spaceEncoding) {
		// TODO: COMPLETE THIS METHOD
		assert (plainText!= null);
		for(int i = 0; i < plainText.length; ++i){
			if (spaceEncoding || plainText[i] != 32) {
				plainText[i] = (byte) (plainText[i] ^ key);
			}
		}
		return plainText; // TODO: to be modified
	}
	/**
	 * Method to encode a byte array using a XOR with a single byte long key
	 * spaces are not encoded
	 * @param key the byte we will use to XOR
	 * @return an encoded byte array
	 */
	public static byte[] xor(byte[] plainText, byte key) {
		// TODO: COMPLETE THIS METHOD
		assert (plainText!= null);
		for(int i = 0; i < plainText.length; ++i){
			if (plainText[i] != 32) {
				plainText[i] = (byte) (plainText[i] ^ key);
			}
		}
		return plainText; // TODO: to be modified
	}

	//-----------------------Vigenere-------------------------
	
	/**
	 * Method to encode a byte array using a byte array keyword
	 * The keyword is repeated along the message to encode
	 * The bytes of the keyword are added to those of the message to encode
	 * @param plainText the byte array representing the message to encode
	 * @param keyword the byte array representing the key used to perform the shift
	 * @param spaceEncoding if false, then spaces are not encoded
	 * @return an encoded byte array 
	 */
	public static byte[] vigenere(byte[] plainText, byte[] keyword, boolean spaceEncoding) {
		assert (plainText!= null && (keyword!= null));
		for(int i = 0; i < plainText.length; ++i){
			int byteAtPosI = plainText[i];
			int keyAtPosI = keyword[i % keyword.length];
			if (spaceEncoding || byteAtPosI != 32){
				byteAtPosI = byteAtPosI + keyAtPosI;
				plainText[i] = (byte) byteAtPosI;
			}
		}
		return plainText; // TODO: done
	}
	
	/**
	 * Method to encode a byte array using a byte array keyword
	 * The keyword is repeated along the message to encode
	 * spaces are not encoded
	 * The bytes of the keyword are added to those of the message to encode
	 * @param plainText the byte array representing the message to encode
	 * @param keyword the byte array representing the key used to perform the shift
	 * @return an encoded byte array 
	 */
	public static byte[] vigenere(byte[] plainText, byte[] keyword) {
		assert (plainText!= null && (keyword!= null));
		for(int i = 0; i < plainText.length; ++i){
			int byteAtPosI = plainText[i];
			int keyAtPosI = keyword[i % keyword.length];
			if (byteAtPosI != 32){
				byteAtPosI = byteAtPosI + keyAtPosI;
				plainText[i] = (byte) byteAtPosI;
			}

		}
		return plainText; // TODO: done
	}
	
	//-----------------------One Time Pad-------------------------
	
	/**
	 * Method to encode a byte array using a one time pad of the same length.
	 *  The method  XOR them together.
	 * @param plainText the byte array representing the string to encode
	 * @param pad the one time pad
	 * @return an encoded byte array
	 */
	public static byte[] oneTimePad(byte[] plainText, byte[] pad) {
		// TODO: COMPLETE THIS METHOD: done
		assert (plainText!= null && (pad!= null));
		if (pad.length != plainText.length)
			return null;
		for(int i = 0; i < plainText.length; ++i){
			plainText[i] = (byte) (plainText[i]^pad[i]);
		}
		return plainText; // TODO: to be modified: done
	}
	
	
	
	
	//-----------------------Basic CBC-------------------------
	
	/**
	 * Method applying a basic chain block counter of XOR without encryption method. Encodes spaces.
	 * @param plainText the byte array representing the string to encode
	 * @param iv the pad of size BLOCKSIZE we use to start the chain encoding
	 * @return an encoded byte array
	 */
	public static byte[] cbc(byte[] plainText, byte[] iv) {
		// TODO: COMPLETE THIS METHOD: done
		assert (plainText!= null && (iv!= null));
		int ivLength = iv.length;
		for(int i = 0; i < plainText.length; ++i){
			plainText[i] = (byte) (plainText[i]^iv[i % ivLength]);
		}
		return plainText; // TODO: to be modified: done
	}
	
	
	/**
	 * Generate a random pad/IV of bytes to be used for encoding
	 * @param size the size of the pad
	 * @return random bytes in an array
	 */
	public static byte[] generatePad(int size) {
		// TODO: COMPLETE THIS METHOD: done
		assert (size!= 0);
		byte[] generated = new byte[size];
		for(int i = 0; i < size; ++i){
			generated[i] = (byte) rand.nextInt(256);
		}
		return generated; // TODO: to be modified: done

	}
	
	
	
}
