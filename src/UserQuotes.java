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
import java.util.ArrayList;
import java.util.Random;


/**
 * Handles user quotes.
 * 
 * @author SeriousWorm
 */
public class UserQuotes {
	private static final String QUOTES_FILENAME = "quotes.txt";
	private static final Random randomNumberGenerator = new Random();
	private final ArrayList<UserQuote> quotes;

	/**
	 * Creates a new empty list with user quotes.
	 */
	public UserQuotes() {
		quotes = new ArrayList<UserQuote>();
	}

	/**
	 * Loads the user quotes from the specified directory.
	 * 
	 * @param directory
	 */
	public void load(final String directory) {
		final String filename = directory + File.separator + QUOTES_FILENAME;
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
				String[] quote = inputLine.split("\t", 3);
				quotes.add(new UserQuote(Integer.parseInt(quote[0]), quote[1], quote[2]));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Saves the user quotes to the specified directory.
	 * 
	 * @param directory
	 * @return whether the save has been successful.
	 */
	public boolean save(final String directory) {
		final String filename = directory + File.separator + QUOTES_FILENAME;
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			for (final UserQuote quote : quotes) {
				writer.write(quote.getID() + "\t" + quote.getUser() + "\t" + quote.get());
				writer.newLine();
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * Adds a new quote. Returns the updated number of quotes.
	 */
	public int add(final String user, final String quote) {
		quotes.add(new UserQuote(quotes.size(), user, quote));
		return quotes.size();
	}

	/**
	 * Removes the specified quote.
	 */
	public boolean remove(final int ID) {
		quotes.remove(ID);
		return false;
	}

	/**
	 * Returns a random quote.
	 */
	public UserQuote get() {
		return get(randomNumberGenerator.nextInt(quotes.size()));
	}

	/**
	 * Returns the quote with the specific ID.
	 */
	public UserQuote get(final int ID) {
		if (ID >= 0 && ID < quotes.size()) {
			return quotes.get(ID);
		} else {
			return null;
		}
	}


}
