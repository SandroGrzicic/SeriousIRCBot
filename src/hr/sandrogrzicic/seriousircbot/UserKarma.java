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
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;

/**
 * Handles user greets.
 * 
 * @author SeriousWorm
 */
public class UserKarma {
	private static final String KARMA_FILENAME = "karma.txt";
	private static final String KARMA_TIMES_FILENAME = "karmaTimes.txt";
	private final HashMap<String, Integer> karma;
	private final HashMap<String, Long> karmaTimes;

	/** Compares the two Entries by value. */
	private class KarmaComparator implements Comparator<Entry<String, Integer>> {
		@Override
		public int compare(final Entry<String, Integer> o1, final Entry<String, Integer> o2) {
			int val = -o1.getValue().compareTo(o2.getValue());
			if (val == 0) {
				return o1.getKey().compareTo(o2.getKey());
			}
			return val;
		}
	}

	/**
	 * Creates a new empty map with user karma.
	 */
	public UserKarma() {
		karma = new HashMap<String, Integer>();
		karmaTimes = new HashMap<String, Long>();
	}

	/**
	 * Loads the user karma from the specified directory.
	 * 
	 * @param directory
	 */
	public void load(final String directory) {
		final String filename = directory + File.separator + KARMA_FILENAME;
		final String filenameTimes = directory + File.separator + KARMA_TIMES_FILENAME;

		BufferedReader reader = null;
		BufferedReader readerTimes = null;

		try {
			new File(filename).createNewFile();
			new File(filenameTimes).createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"));
			readerTimes = new BufferedReader(new InputStreamReader(new FileInputStream(filenameTimes), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			String inputLine;
			// read karma
			while ((inputLine = reader.readLine()) != null) {
				String[] karmaData = inputLine.split("\t", 2);
				karma.put(karmaData[0], Integer.valueOf(karmaData[1]));
			}
			// read karma update times
			while ((inputLine = readerTimes.readLine()) != null) {
				String[] karmaTime = inputLine.split("\t", 2);
				karmaTimes.put(karmaTime[0], Long.valueOf(karmaTime[1]));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Saves the user karma to the specified directory.
	 * 
	 * @param directory
	 * @return whether the save has been successful.
	 */
	public boolean save(final String directory) {
		final String filename = directory + File.separator + KARMA_FILENAME;
		final String filenameTimes = directory + File.separator + KARMA_TIMES_FILENAME;

		BufferedWriter writer = null;
		BufferedWriter writerTimes = null;

		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "UTF-8"));
			writerTimes = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filenameTimes), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			Set<Entry<String, Integer>> entries = new TreeSet<Entry<String, Integer>>(new KarmaComparator());
			entries.addAll(karma.entrySet());

			for (final Entry<String, Integer> karmaData : entries) {
				writer.write(karmaData.getKey() + "\t" + karmaData.getValue());
				writer.newLine();
			}
			writer.close();

			for (final Entry<String, Long> karmaTime : karmaTimes.entrySet()) {
				writerTimes.write(karmaTime.getKey() + "\t" + karmaTime.getValue());
				writerTimes.newLine();
			}
			writerTimes.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * Returns the karma associated with the specified user.
	 * 
	 * @param nickname
	 * @return user's karma
	 */
	public int get(final String user) {
		String userLC = user.toLowerCase();
		if (karma.containsKey(userLC)) {
			return karma.get(userLC);
		} else {
			return 0;
		}
	}

	/** Increments a user's karma. */
	public void incrementKarma(final String nick) {
		changeKarma(nick.toLowerCase(), 1);
	}

	/** Decrements an user's karma. */
	public void decrementKarma(final String nick) {
		changeKarma(nick.toLowerCase(), -1);
	}

	private void changeKarma(final String nick, final int delta) {
		if (karma.containsKey(nick)) {
			karma.put(nick, karma.get(nick) + Integer.valueOf(delta));
		} else {
			karma.put(nick, Integer.valueOf(delta));
		}
	}

	/** Returns whether the specified user has any karma set. */
	public boolean contains(final String nick) {
		return karma.containsKey(nick.toLowerCase());
	}

	/** Returns the time in seconds when the specified user last changed the target user's karma. */
	public long getUpdateTime(final String nick, final String target) {
		String mix = mix(nick.toLowerCase(), target.toLowerCase());
		if (karmaTimes.containsKey(mix)) {
			return karmaTimes.get(mix);
		} else {
			return 0;
		}
	}

	private String mix(final String nick, final String target) {
		// byte[] mix = new byte[nick.length() + target.length()];
		// byte[] nickBytes = nick.getBytes();
		// byte[] targetBytes = target.getBytes();
		//
		// System.arraycopy(nickBytes, 0, mix, 0, nickBytes.length);
		// System.arraycopy(targetBytes, 0, mix, nickBytes.length, targetBytes.length);
		// return new String(mix);

		return nick.concat(target);
	}

	/** Updates the karma last update times map with the current time. */
	public void changed(final String nick, final String target) {
		karmaTimes.put(mix(nick.toLowerCase(), target.toLowerCase()), System.currentTimeMillis() / 1000);
	}

}
