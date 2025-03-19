package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;
import java.time.LocalTime;

public class Warning implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8224097662914849956L;
	
	private String message;
	private LocalTime time;
	char [][] array;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public char[][] getArray() {
		return array;
	}

	public void setArray(char[][] array) {
		this.array = array;
	}

	public Warning(String message, char[][] array) {
		this.message = message;
		this.time = LocalTime.now();
		this.array = array;
	}

	public LocalTime getTime() {
		return time;
	}
}
