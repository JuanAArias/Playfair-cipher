package Decryption;

public abstract class Cipher {
	
	/* Alphabet */
	protected static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
	
	/* Ciphertext*/
	protected String ciphertxt;
	
	/*
	 * Encrypt plaintxt with key
	 */
	public abstract void encrypt(String plaintxt, String key);
	
	/*
	 * Decrypt with key
	 */
	public abstract void decrypt(String key);
	
	/*
	 * Return ciphertxt
	 */
	@Override
	public String toString() {
		return ciphertxt;
	}

}
