package main.java.org.example.io;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.UUID;

/**
 * <p>
 * Clase estática para leer de teclado con comprobación de tipo de datos y
 * escribir en pantalla.
 * </p>
 * 
 * <p>
 * <b>USO EDUCATIVO</b>
 * </p>
 * 
 * <p>
 * Los tipos de dato que maneja son:
 * </p>
 * 
 * <ul>
 * <li>entero (int)
 * <li>decimal (double)
 * <li>caracter (char)
 * <li>byte
 * <li>short
 * <li>int
 * <li>long
 * <li>float
 * <li>double
 * <li>boolean (true, false)
 * <li>char
 * <li>LocarDate
 * <li>UUID
 * <li>String (admite tira vacía)
 * </ul>
 * 
 * @author Amadeo
 * @version 1.0
 * @since 2018-07-01
 */
public class IO {

	private static Scanner sc = new Scanner(System.in);

	/**
	 * Constructor
	 */
	IO() {
		sc.useDelimiter("\n");
	}

	/**
	 * Muestra un objeto
	 * 
	 * @param o
	 *            objeto
	 */
	static public void print(Object o) {
		System.out.print(o);
	}

	/**
	 * Muestra un objeto y salta de l�nea
	 * 
	 * @param o
	 *            objeto
	 */
	static public void println(Object o) {
		System.out.println(o);
	}

	static public void printf(String format, Object... objects) {
		System.out.printf(format, objects);
	}

	/**
	 * Lee un valor de tipo byte
	 * 
	 * @return
	 */
	static public byte readByte() {
		while (true) {
			try {
				return Byte.parseByte(sc.nextLine());
			} catch (Exception e) {
				System.err.print("ERROR: No es de tipo byte ? ");
			}
		}
	}

	/**
	 * Lee un valor de tipo short
	 * 
	 * @return
	 */
	static public short readShort() {
		while (true) {
			try {
				return Short.parseShort(sc.nextLine());
			} catch (Exception e) {
				System.err.print("ERROR: No es de tipo short ? ");
			}
		}
	}

	/**
	 * Lee un valor de tipo int
	 * 
	 * @return
	 */
	static public int readInt() {
		while (true) {
			try {
				return Integer.parseInt(sc.nextLine());
			} catch (Exception e) {
				System.err.print("ERROR: No es de tipo int ? ");
			}
		}
	}

	static public Integer readIntOrNull() {
		while (true) {
			try {
				String in = sc.nextLine();
				if (in.isBlank()) {
					return null;
				}
				return Integer.parseInt(in);
			} catch (Exception e) {
				System.err.print("ERROR: No es de tipo int ? ");
			}
		}
	}
	/**
	 * Lee un valor de tipo long
	 * 
	 * @return
	 */
	static public long readLong() {
		while (true) {
			try {
				return Long.parseLong(sc.nextLine());
			} catch (Exception e) {
				System.err.print("ERROR: No es de tipo long ? ");
			}
		}
	}

	/**
	 * Lee un valor de tipo float
	 * 
	 * @return
	 */
	static public float readFloat() {
		while (true) {
			try {
				return Float.parseFloat(sc.nextLine());
			} catch (Exception e) {
				System.err.print("ERROR: No es de tipo float ? ");
			}
		}
	}

	/**
	 * Lee un valor de tipo double
	 * 
	 * @return
	 */
	static public double readDouble() {
		while (true) {
			try {
				return Double.parseDouble(sc.nextLine());
			} catch (Exception e) {
				System.err.print("ERROR: No es de tipo double ? ");
			}
		}
	}

	/**
	 * Lee un valor de tipo boolean
	 * 
	 * @return
	 */
	static public boolean readBoolean() {
		while (true) {
			String s = sc.nextLine();
			if (s.equals("true")) return true;
			if (s.equals("false")) return false;
			System.err.print("ERROR: No es de tipo boolean (true o false) ? ");
		}
	}

	/**
	 * Lee un valor de tipo char
	 * 
	 * @return
	 */
	static public char readChar() {
		while (true) {
			String s = sc.nextLine();
			if (s.length() == 1) {
				return s.toCharArray()[0];
			}
			System.err.print("ERROR: No es de tipo char ? ");
		}
	}
	
	/**
	 * Lee un valor de tipo LocalDate
	 * 
	 * @return
	 */
	static public LocalDate readLocalDate() {
	    while (true) {
	        try {
	            return LocalDate.parse(sc.nextLine());
	        } catch (DateTimeParseException e) {
	            System.err.print("ERROR: No es de tipo LocalDate ? ");
	        }
	    }
	}
	
	/**
	 * Lee un valor de tipo LocalDate
	 * 
	 * @return
	 */

	static public UUID readUUID() {
	    while (true) {
	        try {
	            return UUID.fromString(sc.nextLine());
	        } catch (IllegalArgumentException e) {
	            System.err.print("ERROR: No es de tipo UUID ? ");
	        }
	    }
	}
	
	
	/**
	 * Lee un valor de tipo String
	 * 
	 * @return
	 */
	static public String readString() {
		return sc.nextLine();
	}

	public static Double readDoubleOrNull() {
		String input = sc.nextLine();
		if (input.isBlank()){
			return null;
		}
		try{
			return Double.parseDouble(input);
		} catch (NumberFormatException e){
			System.out.println("ERROR: No es de tipo double ?");
			return readDoubleOrNull();
		}
	}

	public static LocalDate readLocalDateOrNull() {
		String input = sc.nextLine();
		if (input.isBlank()){
			return  null;
		}
		try {
			return LocalDate.parse(input);
		} catch (DateTimeParseException e){
			System.err.print("ERROR: No es de tipo LocalDate ? ");
			return readLocalDateOrNull();
		}
	}


}
