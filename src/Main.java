
/**
 * Serious IRC Bot entrypoint.
 * 
 * @author SeriousWorm
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(final String[] args) throws Exception {
		String dir;
		if (args.length == 0) {
			dir = "C:/Documents/_Projekti/SeriousIRCBot/podaci";
		} else {
			dir = args[0];
		}
		// SeriousIRCBot bot = new SeriousIRCBot("FRISCtest", dir);
		SeriousIRCBot bot = new SeriousIRCBot("FRISC", dir);

		// bot.setVerbose(true);
		bot.connect("irc.freenode.net");
		// bot.joinChannel("#fer2test");
		bot.joinChannel("#fer2");
	}
}
