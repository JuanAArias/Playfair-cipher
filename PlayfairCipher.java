package Decryption;

import java.util.HashSet;
import java.util.Set;
import java.util.Random;

/*
 * Playfair encryption cipher algorithm
 */
public class PlayfairCipher extends Cipher {
	
	/* Matrix key */
	private PlayfairMatrix matrix;
	
	/*
	 * Start encryption
	 */
	public PlayfairCipher(String plaintxt, String key) {
		matrix = new PlayfairMatrix();
		encrypt(plaintxt, key);
	}
	
	/*
	 * Encrypt
	 */
	@Override
	public void encrypt(String plaintxt, String key) {
		matrix.build(key);
		print();
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < plaintxt.length() - 1; i += 2) {
			char plainChar1 = plaintxt.charAt(i);
			char plainChar2 = plaintxt.charAt(i + 1);
			System.out.printf("encrypting %c %c\n", plainChar1, plainChar2);
			encrypt(builder, plainChar1, plainChar2);
		}
		matrix.clear();
		ciphertxt = builder.toString();	
	}
	
	/*
	 * Decrypt
	 */
	@Override
	public void decrypt(String key) {
		matrix.build(key);
		print();
		StringBuilder builder = new StringBuilder();
		for (int i = 0 ; i < ciphertxt.length() - 1; i += 2) {
			char cipherChar1 = ciphertxt.charAt(i),
				 cipherChar2 = ciphertxt.charAt(i + 1);
			decrypt(builder, cipherChar1, cipherChar2);
		}
		System.out.println(builder);
		removeFillers(builder);
		System.out.println(builder);
	}
	
	/*
	 * Build ciphertext of the given plaintext character
	 */
	private void encrypt(StringBuilder builder, char plainChar1, char plainChar2) {
		if (plainChar1 == plainChar2) {
			encryptSeparate(builder, plainChar1, plainChar2);
		} else {
			char cipherChar1 = 0, cipherChar2 = 0;
			int row1 = matrix.getRow(plainChar1), col1 = matrix.getCol(plainChar1),
				row2 = matrix.getRow(plainChar2), col2 = matrix.getCol(plainChar2);
			if( row1 == row2) {
				cipherChar1 = matrix.nextRight(row1, col1);
				cipherChar2 = matrix.nextRight(row2, col2);
			} else if(col1 == col2) {
				cipherChar1 = matrix.nextBelow(row1, col1);
				cipherChar2 = matrix.nextBelow(row2, col2);
			} else {
				cipherChar1 = matrix.get(row1, col2);
				cipherChar2 = matrix.get(row2, col1);
			}
			builder.append(cipherChar1);
			builder.append(cipherChar2);
		}
	}
	
	/*
	 * Encrypt plainChar digram separately
	 */
	private void encryptSeparate(StringBuilder builder, char plainChar1, char plainChar2) {
		Random rand = new Random();
		int randRow1 = rand.nextInt(5), randCol1 = rand.nextInt(5),
			randRow2 = rand.nextInt(5), randCol2 = rand.nextInt(5);
		char randChar1 = matrix.get(randRow1, randCol1),
			 randChar2 = matrix.get(randRow2, randCol2);
		encrypt(builder, plainChar1, randChar1);
		encrypt(builder, randChar2, plainChar2);
	}
	
	/*
	 * Build plaintext of the given ciphertext character
	 */
	private void decrypt(StringBuilder builder, char cipherChar1, char cipherChar2) {
		char plainChar1 = 0, plainChar2 = 0;
		int row1 = matrix.getRow(cipherChar1), col1 = matrix.getCol(cipherChar1),
			row2 = matrix.getRow(cipherChar2), col2 = matrix.getCol(cipherChar2);
		if( row1 == row2) {
			plainChar1 = matrix.nextLeft(row1, col1);
			plainChar2 = matrix.nextLeft(row2, col2);
		} else if(col1 == col2) {
			plainChar1 = matrix.nextUp(row1, col1);
			plainChar2 = matrix.nextUp(row2, col2);
		} else {
			plainChar1 = matrix.get(row1, col2);
			plainChar2 = matrix.get(row2, col1);
		}
		builder.append(plainChar1);
		builder.append(plainChar2);
	}
	
	/*
	 * Remove filler letters between duplicate letters in plaintxt
	 */
	private void removeFillers(StringBuilder builder) {
		for (int i = 0; i < builder.length() - 3; ++i) {
			char plainChar1 = builder.charAt(i),
				 plainChar2 = builder.charAt(i + 3);
			if (plainChar1 == plainChar2) {
				builder.delete(i + 1, i + 3);
				++i;
			} else {
				i += 3;
			}
		}
	}
	
	/*
	 * Debugging
	 */
	private void print() {
		matrix.print();
	}
	
	
	
	/*
	 * Class for matrix that represents key
	 */
	private class PlayfairMatrix {
		
		/* Matrix of chars */
		private Index[][] matrix;
		
		/*
		 * Initialize
		 */
		protected PlayfairMatrix() {
			matrix = new Index[5][5];
		}
		
		/*
		 * Build matrix
		 */
		protected void build(String key) {
			Set<Character> set = new HashSet<Character>();
			int index = 0, row = 0, col = 0;
			boolean addingKey = true;
			for (int i = 0; i < 5 && addingKey; ++i) {
				for (int j = 0; j < 5 && addingKey;) {
					addingKey = addKeyChar(set, key, index++, i, j);
					j = isChar(i, j) ? j + 1 : j; 
					col = !addingKey? j : row;
				}
				row = !addingKey ? i : row;
			}
			index = 0;
			for (int j = col; j < 5;) {
				addAlphabetChar(set, index++, row, j);
				j = isChar(row, j) ? j + 1 : j;
			}
			for (int i = row + 1; i < 5; ++i) {
				for (int j = 0; j < 5;) {
					addAlphabetChar(set, index++, i, j);
					j = isChar(i, j) ? j + 1 : j;
				}
			}
		}
		
		
		/*
		 * Clear entries
		 */
		protected void clear() {
			for (int i = 0; i < 5; ++i) {
				for (int j = 0; j < 5; ++j) {
					matrix[i][j].clear();
				}
			}
		}
		
		/*
		 * Return char at position in matrix
		 * ADD: random choice i vs j
		 */
		protected char get(int row, int col) {
			return matrix[row][col].getChar();
		}
		
		/*
		 * Return the next char to the right in the matrix
		 * 
		 */
		protected char nextRight(int row, int col) {
			if (col == 4) {
				col = 0;
			} else {
				++col;
			}
			return matrix[row][col].getChar();
		}
		
		/*
		 * Return the next char to the left in the matrix
		 */
		protected char nextLeft(int row, int col) {
			if (col == 0) {
				col = 4;
			} else {
				--col;
			}
			return matrix[row][col].getChar();
		}
		
		/*
		 * Return the next char below in the matrix
		 */
		protected char nextBelow(int row, int col) {
			if (row == 4) {
				row = 0;
			} else {
				++row;
			}
			return matrix[row][col].getChar();
		}
		
		/*
		 * Return the next char below in the matrix
		 */
		protected char nextUp(int row, int col) {
			if (row == 0) {
				row = 4;
			} else {
				--row;
			}
			return matrix[row][col].getChar();
		}
		
		/*
		 * Return true if char at row, col
		 */
		protected boolean isChar(int row, int col) {
			return matrix[row][col] != null &&
				   matrix[row][col].getChar() != 0;
		}
		
		/*
		 * Return row of given plainChar in the matrix
		 */
		protected int getRow(char plainChar) {
			int row = 0;
			boolean found = false;
			for (int i = 0; i < 5 && !found; ++i) {
				for (int j = 0; j < 5 && !found; ++j) {
					if (matrix[i][j].equals(plainChar)) {
						row = i;
						found = true;
					}
				}
			}
			return row;
		}
		
		/*
		 * Return row of given plainChar in the matrix
		 */
		protected int getCol(char plainChar) {
			int col = 0;
			boolean found = false;
			for (int i = 0; i < 5 && !found; ++i) {
				for (int j = 0; j < 5 && !found; ++j) {
					if (matrix[j][i].equals(plainChar)) {
						col = i;
						found = true;
					}
				}
			}
			return col;
		}
		
		/*
		 * Print all chars in matrix
		 */
		protected void print() {
			for (int i = 0; i < 5; ++i) {
				System.out.print("[");
				matrix[i][0].print();
				for (int j = 1; j < 5; ++j) {
					System.out.print('\t');
					matrix[i][j].print();
				}
				System.out.println("]");
			}
			System.out.println();
		}
		
		/*
		 * Add next key char to matrix
		 */
		private boolean addKeyChar(Set<Character> set, String key, int index, int i, int j) {
			boolean addingKey = false;
			if (index < key.length()) {
				addingKey = true;
				char keyChar = key.charAt(index);
				if (!set.contains(keyChar)) {
					set.add(keyChar);
					if (keyChar == 'i') {
						set.add('j');
					} else if (keyChar == 'j') {
						set.add('i');
					}
					matrix[i][j] = new Index(keyChar);
				}
			}
			return addingKey;
		}
		
		/*
		 * Add next letter in alphabet to matrix
		 */
		private void addAlphabetChar(Set<Character> set, int index, int i, int j) {
			char alphaChar = ALPHABET.charAt(index);
			if (!set.contains(alphaChar)) {
				matrix[i][j] = new Index(alphaChar);
				if (alphaChar == 'i') {
					set.add('j');
				} else if (alphaChar == 'j') {
					set.add('i');
				}
			}
		}
		
		
		
		/*
		 * Index in PlayfairMatrix
		 */
		private class Index {
			
			/* Char(s) at index in matrix */
			private char char1, char2;
			
			/*
			 * Construct with given char
			 */
			protected Index(char keyChar) {
				char1 = keyChar;
				switch (keyChar) {
				case 'i':
					char2 = 'j';
					break;
				case 'j':
					char2 = 'i';
					break;
				default:
					char2 = 0;
				}
			}
			
			/*
			 * Set char at index
			 */
			protected void clear() {
				char1 = 0;
				char2 = 0;
			}
			
			/*
			 * Return char at index
			 */
			protected char getChar() {
				return char1;
			}
			
			/*
			 * Return true if equal to given char
			 */
			protected boolean equals(char plainChar) {
				return char1 == plainChar || char2 == plainChar;
			}
			
			/*
			 * Print the char(s) at the Index
			 */
			protected void print() {
				System.out.print(char1);
				if (char2 != 0) {
					System.out.print("/");
					System.out.print(char2);
				}
			}
		}
		
	}
}






