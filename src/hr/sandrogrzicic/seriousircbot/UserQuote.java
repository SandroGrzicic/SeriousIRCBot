package hr.sandrogrzicic.seriousircbot;

/**
 * Represents a single user quote.
 * 
 * @author SeriousWorm
 */
public class UserQuote {
	private final int id;
	private final String user;
	private final String quote;

	/**
	 * Create a new quote.
	 */
	public UserQuote(final int id, final String author, final String quote) {
		this.id = id;
		this.user = author;
		this.quote = quote;
	}

	/**
	 * @return quote ID.
	 */
	public final int getID() {
		return id;
	}

	/**
	 * @return the author of the quote.
	 */
	public final String getUser() {
		return user;
	}


	/**
	 * @return the quote.
	 */
	public final String get() {
		return quote;
	}

	@Override
	public String toString() {
		return quote;
	}

}
