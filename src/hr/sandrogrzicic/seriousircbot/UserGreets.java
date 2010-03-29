package hr.sandrogrzicic.seriousircbot;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * Handles user greets.
 * 
 * @author SeriousWorm
 */
public class UserGreets {
	private static final String GREETS_FILENAME = "greets.txt";
	private final HashMap<String, String> greets;

	/**
	 * Creates a new empty map with user greets.
	 */
	public UserGreets() {
		greets = new HashMap<String, String>();
	}

	/**
	 * Loads the user greets from the specified directory.
	 * 
	 * @param directory
	 */
	public void load(final String directory) {
		final String filename = directory + File.separator + GREETS_FILENAME;
		BufferedReader reader = null;
		
		try {
			new File(filename).createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			String inputLine;
			while ((inputLine = reader.readLine()) != null) {
				String[] greet = inputLine.split("\t", 2);
				greets.put(greet[0], greet[1]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Saves the user greets to the specified directory.
	 * 
	 * @param directory
	 * @return whether the save has been successful.
	 */
	public boolean save(final String directory) {
		final String filename = directory + File.separator + GREETS_FILENAME;
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			for (final Entry<String, String> greet : greets.entrySet()) {
				writer.write(greet.getKey() + "\t" + greet.getValue());
				writer.newLine();
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * Associates the new greet message with the specified user.
	 */
	public void set(final String user, final String greet) {
		greets.put(user.toLowerCase(), greet);
	}

	/**
	 * Returns the greet message associated with the specified user.
	 * 
	 * @param nickname
	 * @return the greet message.
	 */
	public String get(final String user) {
		String userLC = user.toLowerCase();
		if (greets.containsKey(userLC)) {
			return greets.get(userLC);
		} else {
			return "";
		}
	}

	/**
	 * Checks whether the user has a greet set.
	 */
	public boolean isSet(final String user) {
		return greets.containsKey(user.toLowerCase());
	}

}
