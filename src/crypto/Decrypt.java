package crypto;

import java.util.*;

import static crypto.Helper.*;

public class Decrypt {

	public static final int ALPHABETSIZE = Byte.MAX_VALUE - Byte.MIN_VALUE + 1; // 256
	public static final int APOSITION = 97 + ALPHABETSIZE / 2;

	// source : https://en.wikipedia.org/wiki/Letter_frequency
	public static final double[] ENGLISHFREQUENCIES = { 0.08497, 0.01492, 0.02202, 0.04253, 0.11162, 0.02228, 0.02015,
			0.06094, 0.07546, 0.00153, 0.01292, 0.04025, 0.02406, 0.06749, 0.07507, 0.01929, 0.00095, 0.07587, 0.06327,
			0.09356, 0.02758, 0.00978, 0.0256, 0.0015, 0.01994, 0.00077 };

	/**
	 * Method to break a string encoded with different types of cryptosystems
	 * 
	 * @param type the integer representing the method to break : 0 = Caesar, 1 =
	 *             Vigenere, 2 = XOR
	 * @return the decoded string or the original encoded message if type is not in
	 *         the list above.
	 */

	public static String breakCipher(String cipher, int type) {
		// TODO : COMPLETE THIS METHOD: done
		byte[] cipherBytes = stringToBytes(cipher).clone();
		List<Byte> cipherList = new ArrayList<>();
		String possString = "empty";

		for (int i = 0; i < cipherBytes.length; ++i) {
			cipherList.add(cipherBytes[i]);
		}
		if (type == 0) { // Caeser with freq
			byte decodingKey = caesarWithFrequencies(cipherBytes);
			possString = bytesToString(Encrypt.caesar(cipherBytes, (byte) -decodingKey));
		}
		if (type == 3) { // Caesar brute force
			possString = arrayToString(caesarBruteForce(cipherBytes));
		}
		if (type == 2) { // XOR brute force
			possString = arrayToString(xorBruteForce(cipherBytes));
		}
		if (type == 1) { // Vigenere with freq
			possString = bytesToString(vigenereWithFrequencies(cipherBytes));
		}
		System.out.println("Decrypted string: " + possString);
		return possString; // TODO: to be modified: done
	}

	/**
	 * Converts a 2D byte array to a String
	 * 
	 * @param bruteForceResult a 2D byte array containing the result of a brute
	 *                         force method
	 */

	public static String arrayToString(byte[][] bruteForceResult) {
		// TODO : COMPLETE THIS METHOD: done
		String finalString = "";
		for (int i = 0; i < bruteForceResult.length; ++i) {
			finalString += bytesToString(bruteForceResult[i]) + System.lineSeparator();
		}
		return finalString; // TODO: to be modified: done
	}

	// -----------------------Caesar-------------------------
	/**
	 * Method to decode a byte array encoded using the Caesar scheme This is done by
	 * the brute force generation of all the possible options
	 * 
	 * @param cipher the byte array representing the encoded text
	 * @return a 2D byte array containing all the possibilities
	 */

	public static byte[][] caesarBruteForce(byte[] cipher) {
		// TODO : COMPLETE THIS METHOD
		byte[][] possible = new byte[256][cipher.length];
		int k = 0;
		for (byte i = -126; i < 127; ++i) {
			k = (int) i + 126;
			possible[k] = Encrypt.caesar(cipher, i, true).clone();
		}
		return possible; // TODO: to be modified: done
	}

	/**
	 * Method that finds the key to decode a Caesar encoding by comparing
	 * frequencies
	 * 
	 * @param cipherText the byte array representing the encoded text
	 * @return the encoding key
	 */

	public static byte caesarWithFrequencies(byte[] cipherText) {
		// TODO : COMPLETE THIS METHOD
		byte key = caesarFindKey(computeFrequencies(cipherText));
		byte officialKey = (byte) (key + 159);
		return officialKey; // TODO: to be modified
	}

	public static byte caesarWithFrequencies(List<Byte> cipherText) {
		// TODO : COMPLETE THIS METHOD
		byte key = caesarFindKey(computeFrequencies(cipherText));
		byte officialKey = (byte) (key + 159);
		return officialKey; // TODO: to be modified
	}

	/**
	 * Method that computes the frequencies of letters inside a byte array
	 * corresponding to a String
	 * 
	 * @param cipherText the byte array
	 * @return the character frequencies as an array of float
	 */

	public static float[] computeFrequencies(byte[] cipherText) {
		// TODO : COMPLETE THIS METHOD
		float[] frequency = new float[256];
		for (int i = 0; i < cipherText.length; ++i) {
			byte temp = cipherText[i];
			frequency[temp + 127] += (float) (1.0 / cipherText.length);
		}
		return frequency; // TODO: to be modified
	}

	public static float[] computeFrequencies(List<Byte> cipherText) {
		// TODO : COMPLETE THIS METHOD
		float[] frequency = new float[256];
		for (int i = 0; i < cipherText.size(); ++i) {
			byte temp = cipherText.get(i);
			frequency[temp + 127] += (float) (1.0 / cipherText.size());
		}
		return frequency; // TODO: to be modified
	}

	public static byte caesarFindKey(float[] charFrequencies) {
		// TODO : COMPLETE THIS METHOD
		/**
		 * IT is important to note that this method only decodes text which is literal
		 * meaning it will struggle to decrypt "CCC" but will be fine with "hi my name
		 * is Marvin"
		 */
		float maximumDotProduct = 0.0f;
		float tempDotProduct = 0.0f;
		int indexDesired = 0;
		for (int i = 0; i < 256; ++i) {
			for (int j = 0; j < ENGLISHFREQUENCIES.length; ++j) {
				float temp_charFrequencies = (float) charFrequencies[(j + i) % 256];
				tempDotProduct += (float) (ENGLISHFREQUENCIES[j] * temp_charFrequencies);
			}
			if (tempDotProduct > maximumDotProduct) {
				maximumDotProduct = tempDotProduct;
				indexDesired = i;
			}
			tempDotProduct = 0.0f;
		}
		indexDesired = indexDesired - 127;
		return (byte) indexDesired; // TODO: to be modified: done
	}

	/**
	 * Method that finds the key used by a Caesar encoding from an array of
	 * character frequencies
	 * 
	 * @param charFrequencies the array of character frequencies
	 * @return the key
	 */

	// -----------------------XOR-------------------------

	/**
	 * Method to decode a byte array encoded using a XOR This is done by the brute
	 * force generation of all the possible options
	 * 
	 * @param cipher the byte array representing the encoded text
	 * @return the array of possibilities for the clear text
	 */

	public static byte[][] xorBruteForce(byte[] cipher) {
		// TODO : COMPLETE THIS METHOD
		byte[][] possible = new byte[256][cipher.length];
		for (byte i = -126; i < 127; ++i) {
			possible[(int) (i + 126)] = Encrypt.xor(cipher, i, true).clone();
		}
		return possible; // TODO: to be modified: done
	}

	// -----------------------Vigenere-------------------------
	// Algorithm : see https://www.youtube.com/watch?v=LaWp_Kq0cKs

	/**
	 * Method to decode a byte array encoded following the Vigenere pattern, but in
	 * a clever way, saving up on large amounts of computations
	 * 
	 * @param cipher the byte array representing the encoded text
	 * @return the byte encoding of the clear text
	 */

	public static byte[] vigenereWithFrequencies(byte[] cipher) {
		// TODO : COMPLETE THIS METHOD: done
		List<Byte> removedSpaces = new ArrayList<Byte>(removeSpaces(cipher));
		byte[] removedSpacesBytes = new byte[removedSpaces.size()];
		for (int i = 0; i < removedSpaces.size(); ++i) {
			removedSpacesBytes[i] = removedSpaces.get(i);
		}
		int keyLength = vigenereFindKeyLength(removedSpaces);
		byte[] keyPos = vigenereFindKey(removedSpaces, keyLength).clone();
		byte[] test = Encrypt.vigenere(removedSpacesBytes, keyPos);
		return test; // TODO: to be modified: done
	}

	/**
	 * Helper Method used to remove the space character in a byte array for the
	 * clever Vigenere decoding
	 * 
	 * @param array the array to clean
	 * @return a List of bytes without spaces
	 */

	public static List<Byte> removeSpaces(byte[] array) {
		// TODO : COMPLETE THIS METHOD: done
		List<Byte> list = new ArrayList<>();
		for (int i = 0; i < array.length; ++i) {
			if (array[i] != 32) {
				list.add(array[i]);
			}
		}
		byte[] arr = new byte[list.size()];
		for (int i = 0; i < list.size(); ++i) {
			arr[i] = list.get(i);
		}
		return list;
	}

	/**
	 * Method that computes the key length for a Vigenere cipher text.
	 * 
	 * @param cipher the byte array representing the encoded text without space
	 * @return the length of the key
	 */

	// Assistant functions
	public static int vigenereFindKeyLength(List<Byte> cipher) {
		// TODO : COMPLETE THIS METHOD: done
		int[] number = vigenereKeyMaximaux(cipher);
		List<Integer> maxLocaux = new ArrayList<>(maxLocauxIndentify(number));
		Map<Integer, Integer> differences = new HashMap<Integer, Integer>(maxDifferences(maxLocaux));
		int[] keyLen = mostRepeatedDifference(differences);
		int keyZ = keyLen[0];
		if (keyLen[1] == 1) {
			keyZ = number.length - keyLen[0];
		}
		return keyZ; // TODO: to be modified: done
	}

	public static int[] vigenereKeyMaximaux(List<Byte> cipher) {
		// TODO : COMPLETE THIS METHOD: done
		int[] number = new int[cipher.size()];
		for (int i = 1; i < cipher.size(); ++i) {
			for (int j = 0; j + i < cipher.size(); ++j) {
				if (cipher.get(j).equals(cipher.get(j + i))) {
					number[i - 1] += 1;
				}
			}
		}
		/**
		 * System.out.println("done");
		 * 
		 * int k = 0; for(int i: number){ System.out.println("Index: " + k + " Number: "
		 * + i); ++k; } System.out.println("Done list of occurences");
		 */
		return number;
	}

	public static List<Integer> maxLocauxIndentify(int[] list) {
		int[] number = list.clone();
		ArrayList<Integer> maxLocaux = new ArrayList<>();

		if (number.length < 3) {
			System.out.println("Too short");
			assert (false);
		}

		if (number.length >= 3) {
			if (number[0] >= number[1] && number[0] >= number[2] && number[0] != 0) {
				maxLocaux.add(0);
			}
		}
		if (number.length >= 4) {
			if (number[1] >= number[2] && number[1] > number[3]) {
				if (number[1] >= number[0] && number[1] != 0) {
					maxLocaux.add(1);
				}
			}
		}
		if (number.length >= 5) {
			for (int i = 0; i < (Math.ceil(number.length / 2.0) - 4); ++i) {
				if (number[i + 2] >= number[i + 1] && number[i + 2] > number[i]) {
					if (number[i + 2] >= number[i + 3] && number[i + 2] > number[i + 4]) {
						if (number[i + 2] != 0) {
							maxLocaux.add(i + 2);
						}
					}
				}
			}
		}
		return maxLocaux;
	}

	public static Map<Integer, Integer> maxDifferences(List<Integer> listOfMaxIndices) {
		Map<Integer, Integer> differences = new HashMap<Integer, Integer>();
		if (listOfMaxIndices.size() == 1) {
			differences.put(listOfMaxIndices.get(0) + 1, 1);
		}
		for (int i = 0; i < listOfMaxIndices.size() - 1; ++i) {
			int diff = Math.abs(listOfMaxIndices.get(i + 1) - listOfMaxIndices.get(i));
			if (differences.get(diff) != null) {
				int init = differences.get(diff);
				differences.put(diff, init + 1);
			} else {
				differences.put(diff, 1);
			}
		}
		/**
		 * for (Map.Entry<Integer,Integer> entry: differences.entrySet()) {
		 * System.out.println("Key: " + entry.getKey() + " Value " + entry.getValue());
		 * }
		 */
		return differences;
	}

	public static int[] mostRepeatedDifference(Map<Integer, Integer> mapDifference) {
		int[] bool = new int[2];
		int mostRepeatedDiff = 0;
		int numbOfMostRepeatedDiff = 0;
		for (Map.Entry<Integer, Integer> entry : mapDifference.entrySet()) {
			if (entry.getValue() > mostRepeatedDiff) {
				mostRepeatedDiff = entry.getValue();
				numbOfMostRepeatedDiff = entry.getKey();
			}
			if (entry.getValue() == mostRepeatedDiff && entry.getKey() < numbOfMostRepeatedDiff) {
				mostRepeatedDiff = entry.getValue();
				numbOfMostRepeatedDiff = entry.getKey();
			}
		}

		if (numbOfMostRepeatedDiff == 0) {
			numbOfMostRepeatedDiff = 1;
			bool[0] = numbOfMostRepeatedDiff;
			bool[1] = 1;
			return bool;
		}

		bool[0] = numbOfMostRepeatedDiff;
		return bool;
	}

	public static byte[] vigenereFindKey(List<Byte> cipher, int keyLength) {
		// TODO : COMPLETE THIS METHOD
		byte[] key = new byte[keyLength];
		for (int j = 0; j < keyLength; ++j) {
			List<Byte> temp = new ArrayList<Byte>();
			for (int i = 0; i + j < cipher.size(); i = i + keyLength) {
				temp.add(cipher.get(i + j));
			}
			byte decodingKey = Decrypt.caesarWithFrequencies(temp);
			key[j] = (byte) -decodingKey;

		}
		return key; // TODO: to be modified
	}
	/**
	 * Takes the cipher without space, and the key length, and uses the dot product
	 * with the English language frequencies to compute the shifting for each letter
	 * of the key
	 * 
	 * @param cipher    the byte array representing the encoded text without space
	 * @param keyLength the length of the key we want to find
	 * @return the inverse key to decode the Vigenere cipher text
	 */

	// -----------------------Basic CBC-------------------------

	/**
	 * Method used to decode a String encoded following the CBC pattern
	 * 
	 * @param cipher the byte array representing the encoded text
	 * @param iv     the pad of size BLOCKSIZE we use to start the chain encoding
	 * @return the clear text
	 */

	public static byte[] decryptCBC(byte[] cipher, byte[] iv) {
		// TODO : COMPLETE THIS METHOD
		assert (cipher != null && (iv != null));
		int ivLength = iv.length;
		for (int i = 0; i < cipher.length; ++i) {
			cipher[i] = (byte) (cipher[i] ^ iv[i % ivLength]);
		}
		return cipher;// TODO: to be modified
	}

}
