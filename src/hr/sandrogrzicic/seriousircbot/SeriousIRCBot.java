package hr.sandrogrzicic.seriousircbot;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jibble.pircbot.DccChat;
import org.jibble.pircbot.DccFileTransfer;
import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.User;

/**
 * The Serious IRC Bot.
 * 
 * @author SeriousWorm
 */
public class SeriousIRCBot extends PircBot {

	/** The version of our bot. */
	public static final String BOT_VERSION = "b8";

	private static final Random randomNumberGenerator = new Random();
	private final String directory;

	private final UserGreets userGreets = new UserGreets();
	private final UserQuotes userQuotes = new UserQuotes();
	
	private static final Object QUERY_VERSION = "verzija";
	private static final String QUERY_HELP = "pomoć";

	private static final Object QUERY_GREET = "pozdrav";
	private static final Object QUERY_GREET_SET = "postavi";
	private static final Object QUERY_GREET_GET = "?";

	private static final String QUERY_QUOTE = "citat";
	private static final String QUERY_QUOTE_ADD = "dodaj";
	private static final String QUERY_QUOTE_DEL = "obriši";

	private static final String MESSAGE_FATAL_ERROR = "FATAL ERROR";
	private static final String MESSAGE_INVALID_COMMAND = "nije mi poznata ta naredba. Probaj ?" + QUERY_HELP + ".";
	private static final String MESSAGE_PARAMETER_INVALID = "Greška: nevaljani parametar:";
	private static final String MESSAGE_VERSION = "moja verzija je";
	private static final String MESSAGE_COMMAND_PARAMETERS_NUMBER = "ova naredba zahtijeva ";
	private static final String MESSAGE_PARAMETER = "parametar.";
	private static final String MESSAGE_PARAMETERS = "parametara.";
	private static final String MESSAGE_COMMAND_PARAMETERS = "valjani parametri su";
	private static final String MESSAGE_AND = "i";

	private static final String MESSAGE_GREET_GET = "pozdravna poruka za korisnika";
	private static final String MESSAGE_GREET_GET_SENDER = "tvoja pozdravna poruka je ";
	private static final String MESSAGE_GREET_NOT_SET = "nema postavljenu pozdravnu poruku";
	private static final String MESSAGE_GREET_NOT_SET_SENDER = "nisi postavio pozdravnu poruku";
	private static final String MESSAGE_GREET_IS = "je";
	private static final String MESSAGE_GREET_SET = "pozdravna poruka je uspješno postavljena";

	private static final String MESSAGE_QUOTE = "Citat";
	private static final String MESSAGE_QUOTE_ADDED = "citat je dodan. Redni broj: ";
	private static final String MESSAGE_QUOTE_DELETED = "citat je obrisan";
	private static final String MESSAGE_QUOTE_NOT_DELETED = "citat nije obrisan jer nisi dodao taj citat.";

	private static final String MESSAGE_QUOTE_DOES_NOT_EXIST = "ne postoji";
	

	/**
	 * Creates a new bot with default parameters.
	 */
	public SeriousIRCBot(final String name, final String directory) {
		this.setName(name);
		this.directory = directory;

		if (!(new File(directory).isDirectory())) {
			exitWithError("Invalid directory!", 1);
		}

		userGreets.load(directory);
		userQuotes.load(directory);
	}

	@Override
	protected void onMessage(final String channel, final String sender, final String login, final String hostname, final String message) {
		if (!message.startsWith("?")) {
			return;
		}

		String[] splitMessage = message.substring(1).split(" ", 2);
		String command = splitMessage[0];
		String parameters = (splitMessage.length == 2) ? splitMessage[1] : "";
		
		if (command.equals(QUERY_VERSION)) {
			handleVersion(channel, sender);
		} else if (command.equals(QUERY_HELP)) {
			handleHelp(sender, parameters);
		} else if (command.equals(QUERY_GREET)) {
			handleGreet(channel, sender, parameters);
		} else if (command.equals(QUERY_QUOTE)) {
			handleQuote(channel, sender, parameters);
		} else {
			if (command.length() > 0) {
				sendNotice(sender, sender + ", " + MESSAGE_INVALID_COMMAND);
			}
		}
	}

	/** Handles the Quote command. */
	private void handleQuote(final String channel, final String sender, final String parameters) {
		String[] params = parameters.split(" ", 2);
		String command = params[0];
		if (command.equals(QUERY_QUOTE_ADD)) {
			if (params.length == 1) {
				sendNotice(sender, sender + ", " + MESSAGE_COMMAND_PARAMETERS_NUMBER + " [2] " + MESSAGE_PARAMETERS);
			} else {
				sendNotice(sender, sender + ", " + MESSAGE_QUOTE_ADDED + "[" + userQuotes.add(sender, params[1]) + "].");
				userQuotes.save(directory);
			}
		} else if (command.equals(QUERY_QUOTE_DEL)) {
			if (params.length == 1) {
				sendNotice(sender, sender + ", " + MESSAGE_COMMAND_PARAMETERS_NUMBER + " [2] " + MESSAGE_PARAMETERS);
			} else {
				int ID;
				try {
					ID = Integer.parseInt(params[1]);
				} catch (NumberFormatException nfe) {
					sendNotice(sender, MESSAGE_PARAMETER_INVALID + " [" + params[1] + "]!");
					return;
				}
				if (sender.equals(userQuotes.get(ID).getUser())) {
					userQuotes.remove(ID);
					sendNotice(sender, sender + ", " + MESSAGE_QUOTE_DELETED + ".");
				} else {
					sendNotice(sender, sender + ", " + MESSAGE_QUOTE_NOT_DELETED + ".");
				}
			}
		} else if (command.length() > 0) {
			int ID;
			try {
				ID = Integer.parseInt(command);
			} catch (NumberFormatException nfe) {
				sendMessage(sender, MESSAGE_PARAMETER_INVALID + " [" + command + "]!");
				return;
			}
			UserQuote quote = userQuotes.get(ID - 1);
			if (quote != null) {
				sendMessage(channel, MESSAGE_QUOTE + " #[" + ID + "]: [" + quote.getUser() + "] " + quote.get());
			} else {
				sendNotice(sender, MESSAGE_QUOTE + " #[" + ID + "] " + MESSAGE_QUOTE_DOES_NOT_EXIST + ".");
			}
		} else {
			UserQuote quote = userQuotes.get();
			sendMessage(channel, MESSAGE_QUOTE + " #[" + quote.getID() + "]: [" + quote.getUser() + "] " + quote.get());
		}
	}

	/** Handles the Version command. */
	private void handleVersion(final String channel, final String sender) {
		sendNotice(sender, sender + ", " + MESSAGE_VERSION + " [" + BOT_VERSION + "].");
	}

	/** Handles the Help command. */
	private void handleHelp(final String sender, final String parameters) {
		String[] params = parameters.split(" ", 2);
		String topic = params[0];

		if (topic.length() == 0) {
			sendNotice(sender, "FRISC v" + BOT_VERSION + ". Naredbe počinju sa upitnikom (?). Popis naredbi: ");
			sendNotice(sender, "-> " + QUERY_HELP + ", " + QUERY_VERSION + ", " + QUERY_GREET + ", " + QUERY_QUOTE + ".");
		} else {
			if (topic.equals(QUERY_HELP)) {
				sendNotice(sender, "[" + topic +
						"]: Ispisuje osnovne informacije o korištenju ovog bota te popis naredbi koje se mogu koristiti.");
			} else if (topic.equals(QUERY_VERSION)) {
				sendNotice(sender, "[" + topic + "]: Ispisuje verziju ovog bota.");
			} else if (topic.equals(QUERY_GREET)) {
				sendNotice(sender, "[" + topic + "]: Služi kao sustav pozdrava. Naredbe: [" + QUERY_GREET_SET + "], [" + QUERY_GREET_GET + "].");
			} else if (topic.equals(QUERY_QUOTE)) {
				sendNotice(sender, "[" + topic + "]: Služi kao sustav citata. Naredbe: [" + QUERY_QUOTE_ADD + "], [" + QUERY_QUOTE_DEL + "].");
				sendNotice(
						sender,
						"[" +
								topic +
								"]: Ispišite citat s određenim ID brojem tako da broj upišete kao parametar naredbe. Ispišite slučajni citat tako da pozovete naredbu bez parametara.");
			// } else if (topic.equals(QUERY_)) {
			// sendMessage(channel, "[" + topic + "]:");
			}
		}
	}

	/** Handles the Greet command. */
	private void handleGreet(final String channel, final String sender, final String parameters) {
		String[] params = parameters.split(" ", 2);
		String command = params[0];
		if (command.equals(QUERY_GREET_SET)) {
			if (params.length == 1) {
				sendNotice(sender, sender + ", " + MESSAGE_COMMAND_PARAMETERS_NUMBER + " [2] " + MESSAGE_PARAMETERS);
			} else {
				userGreets.set(sender, params[1]);
				userGreets.save(directory);
				sendNotice(sender, sender + ", " + MESSAGE_GREET_SET + ".");
			}
		} else if (command.equals(QUERY_GREET_GET)) {
			if (params.length == 1) {
				sendNotice(sender, sender + ", " + MESSAGE_COMMAND_PARAMETERS_NUMBER + " [2] " + MESSAGE_PARAMETERS);
			} else {
				String greet = userGreets.get(params[1]);
				if (greet == null) {
					sendNotice(sender, sender + ", [" + params[1] + "] " + MESSAGE_GREET_NOT_SET + ".");
				} else {
					sendNotice(sender, sender + ", " + MESSAGE_GREET_GET + " [" + params[1] + "] " + MESSAGE_GREET_IS + " [" +
							userGreets.get(params[1]) + "].");
				}
			}
		} else {
			String greet = userGreets.get(sender);
			if (greet == null) {
				sendMessage(channel, sender + ", " + MESSAGE_GREET_NOT_SET_SENDER + ".");
			} else {
				sendMessage(channel, sender + ", " + MESSAGE_GREET_GET_SENDER + " [" + userGreets.get(sender) + "].");
			}
			// sendMessage(channel, sender + ", " + MESSAGE_COMMAND_PARAMETERS + " [" + QUERY_GREET_GET + "] " + MESSAGE_AND + " [" +
			// QUERY_GREET_SET + "].");
			// return;
		}
	}

	@Override
	protected void onPrivateMessage(final String sender, final String login, final String hostname, final String message) {
		if (message.equals("quit")) {
			List<String> quitMessages = new ArrayList<String>();
			quitMessages.add("Worm me zgasio!");
			quitMessages.add("Worm me ubio!");
			quitMessages.add("Worm me ugasio!");
			quitMessages.add("Worm me unistio!");
			quitMessages.add("FRISC by SeriousWorm");
			int randomIndex = randomNumberGenerator.nextInt(quitMessages.size());
			quitServer(quitMessages.get(randomIndex));
			System.exit(0);
		}
	}

	@Override
	protected void onJoin(final String channel, final String sender, final String login, final String hostname) {
		// greets
		if (userGreets.isSet(sender)) {
			// greet is never set for the bot so the check is not needed
			sendMessage(channel, "[" + sender + "] " + userGreets.get(sender));
		}
	}

	@Override
	protected void onAction(final String sender, final String login, final String hostname, final String target, final String action) {
		// TODO Auto-generated method stub
		super.onAction(sender, login, hostname, target, action);
	}

	@Override
	protected void onChannelInfo(final String channel, final int userCount, final String topic) {
		// TODO Auto-generated method stub
		super.onChannelInfo(channel, userCount, topic);
	}

	@Override
	protected void onConnect() {
		// TODO Auto-generated method stub
		super.onConnect();
	}


	@Override
	protected void onOp(final String channel, final String sourceNick, final String sourceLogin, final String sourceHostname, final String recipient) {
		if (recipient.equals(getNick())) {
			sendMessage(channel, "yay! Hvala " + sourceNick + "!");
		}
	}

	@Override
	protected void onDeop(final String channel, final String sourceNick, final String sourceLogin, final String sourceHostname, final String recipient) {
		if (recipient.equals(getNick())) {
			sendMessage(channel, "šmrc :(");
		}
	}

	@Override
	protected void onVoice(final String channel, final String sourceNick, final String sourceLogin, final String sourceHostname,
			final String recipient) {
		if (recipient.equals(getNick())) {
			sendMessage(channel, "yay! Thx " + sourceNick + "!");
		}
	}

	@Override
	protected void onDeVoice(final String channel, final String sourceNick, final String sourceLogin, final String sourceHostname,
			final String recipient) {
		if (recipient.equals(getNick())) {
			sendMessage(channel, "NEEEEEEEEEEEEEEEEEEEEeeeeeeeeeeeee..........          .");
		}
	}

	@Override
	protected void onDisconnect() {
		// TODO Auto-generated method stub
		super.onDisconnect();
	}

	@Override
	protected void onFileTransferFinished(final DccFileTransfer transfer, final Exception e) {
		// TODO Auto-generated method stub
		super.onFileTransferFinished(transfer, e);
	}

	@Override
	protected void onFinger(final String sourceNick, final String sourceLogin, final String sourceHostname, final String target) {
		// TODO Auto-generated method stub
		super.onFinger(sourceNick, sourceLogin, sourceHostname, target);
	}

	@Override
	protected void onIncomingChatRequest(final DccChat chat) {
		// TODO Auto-generated method stub
		super.onIncomingChatRequest(chat);
	}

	@Override
	protected void onIncomingFileTransfer(final DccFileTransfer transfer) {
		// TODO Auto-generated method stub
		super.onIncomingFileTransfer(transfer);
	}

	@Override
	protected void onInvite(final String targetNick, final String sourceNick, final String sourceLogin, final String sourceHostname,
			final String channel) {
		if (sourceNick.equals("SeriousWorm")) {
			joinChannel(channel);
		}
	}

	@Override
	protected void onKick(final String channel, final String kickerNick, final String kickerLogin, final String kickerHostname,
			final String recipientNick, final String reason) {
		if (recipientNick.equals(getNick())) {
			joinChannel(channel);
		}
	}

	@Override
	protected void onMode(final String channel, final String sourceNick, final String sourceLogin, final String sourceHostname, final String mode) {
		// TODO Auto-generated method stub
		super.onMode(channel, sourceNick, sourceLogin, sourceHostname, mode);
	}

	@Override
	protected void onNickChange(final String oldNick, final String login, final String hostname, final String newNick) {
		String oldNickLC = oldNick.toLowerCase();
		String newNickLC = newNick.toLowerCase();
		if ((oldNickLC.endsWith("afk") && !newNickLC.endsWith("afk")) || (oldNickLC.endsWith("bnc") && !newNickLC.endsWith("bnc"))) {
			onJoin("#fer2", newNick, login, hostname);
		}
	}

	@Override
	protected void onNotice(final String sourceNick, final String sourceLogin, final String sourceHostname, final String target, final String notice) {
		// TODO Auto-generated method stub
		super.onNotice(sourceNick, sourceLogin, sourceHostname, target, notice);
	}

	@Override
	protected void onPart(final String channel, final String sender, final String login, final String hostname) {
		// TODO Auto-generated method stub
		super.onPart(channel, sender, login, hostname);
	}

	@Override
	protected void onPing(final String sourceNick, final String sourceLogin, final String sourceHostname, final String target, final String pingValue) {
		// TODO Auto-generated method stub
		super.onPing(sourceNick, sourceLogin, sourceHostname, target, pingValue);
	}


	@Override
	protected void onQuit(final String sourceNick, final String sourceLogin, final String sourceHostname, final String reason) {
		// if (!sourceNick.equals(getName())) {
		// sendMessage("#fer2", "ode nam " + sourceNick + "...");
		// }
	}

	@Override
	protected void onRemoveChannelBan(final String channel, final String sourceNick, final String sourceLogin, final String sourceHostname,
			final String hostmask) {
		// TODO Auto-generated method stub
		super.onRemoveChannelBan(channel, sourceNick, sourceLogin, sourceHostname, hostmask);
	}

	@Override
	protected void onRemoveChannelKey(final String channel, final String sourceNick, final String sourceLogin, final String sourceHostname,
			final String key) {
		// TODO Auto-generated method stub
		super.onRemoveChannelKey(channel, sourceNick, sourceLogin, sourceHostname, key);
	}

	@Override
	protected void onRemoveChannelLimit(final String channel, final String sourceNick, final String sourceLogin, final String sourceHostname) {
		// TODO Auto-generated method stub
		super.onRemoveChannelLimit(channel, sourceNick, sourceLogin, sourceHostname);
	}

	@Override
	protected void onRemoveInviteOnly(final String channel, final String sourceNick, final String sourceLogin, final String sourceHostname) {
		// TODO Auto-generated method stub
		super.onRemoveInviteOnly(channel, sourceNick, sourceLogin, sourceHostname);
	}

	@Override
	protected void onRemoveModerated(final String channel, final String sourceNick, final String sourceLogin, final String sourceHostname) {
		// TODO Auto-generated method stub
		super.onRemoveModerated(channel, sourceNick, sourceLogin, sourceHostname);
	}

	@Override
	protected void onRemoveNoExternalMessages(final String channel, final String sourceNick, final String sourceLogin, final String sourceHostname) {
		// TODO Auto-generated method stub
		super.onRemoveNoExternalMessages(channel, sourceNick, sourceLogin, sourceHostname);
	}

	@Override
	protected void onRemovePrivate(final String channel, final String sourceNick, final String sourceLogin, final String sourceHostname) {
		// TODO Auto-generated method stub
		super.onRemovePrivate(channel, sourceNick, sourceLogin, sourceHostname);
	}

	@Override
	protected void onRemoveSecret(final String channel, final String sourceNick, final String sourceLogin, final String sourceHostname) {
		// TODO Auto-generated method stub
		super.onRemoveSecret(channel, sourceNick, sourceLogin, sourceHostname);
	}

	@Override
	protected void onRemoveTopicProtection(final String channel, final String sourceNick, final String sourceLogin, final String sourceHostname) {
		// TODO Auto-generated method stub
		super.onRemoveTopicProtection(channel, sourceNick, sourceLogin, sourceHostname);
	}

	@Override
	protected void onServerPing(final String response) {
		// TODO Auto-generated method stub
		super.onServerPing(response);
	}

	@Override
	protected void onServerResponse(final int code, final String response) {
		// TODO Auto-generated method stub
		super.onServerResponse(code, response);
	}

	@Override
	protected void onSetChannelBan(final String channel, final String sourceNick, final String sourceLogin, final String sourceHostname,
			final String hostmask) {
		// TODO Auto-generated method stub
		super.onSetChannelBan(channel, sourceNick, sourceLogin, sourceHostname, hostmask);

	}

	@Override
	protected void onSetChannelKey(final String channel, final String sourceNick, final String sourceLogin, final String sourceHostname,
			final String key) {
		// TODO Auto-generated method stub
		super.onSetChannelKey(channel, sourceNick, sourceLogin, sourceHostname, key);
	}

	@Override
	protected void onSetChannelLimit(final String channel, final String sourceNick, final String sourceLogin, final String sourceHostname,
			final int limit) {
		// TODO Auto-generated method stub
		super.onSetChannelLimit(channel, sourceNick, sourceLogin, sourceHostname, limit);
	}

	@Override
	protected void onSetInviteOnly(final String channel, final String sourceNick, final String sourceLogin, final String sourceHostname) {
		// TODO Auto-generated method stub
		super.onSetInviteOnly(channel, sourceNick, sourceLogin, sourceHostname);
	}

	@Override
	protected void onSetModerated(final String channel, final String sourceNick, final String sourceLogin, final String sourceHostname) {
		// TODO Auto-generated method stub
		super.onSetModerated(channel, sourceNick, sourceLogin, sourceHostname);
	}

	@Override
	protected void onSetNoExternalMessages(final String channel, final String sourceNick, final String sourceLogin, final String sourceHostname) {
		// TODO Auto-generated method stub
		super.onSetNoExternalMessages(channel, sourceNick, sourceLogin, sourceHostname);
	}

	@Override
	protected void onSetPrivate(final String channel, final String sourceNick, final String sourceLogin, final String sourceHostname) {
		// TODO Auto-generated method stub
		super.onSetPrivate(channel, sourceNick, sourceLogin, sourceHostname);
	}

	@Override
	protected void onSetSecret(final String channel, final String sourceNick, final String sourceLogin, final String sourceHostname) {
		// TODO Auto-generated method stub
		super.onSetSecret(channel, sourceNick, sourceLogin, sourceHostname);
	}

	@Override
	protected void onSetTopicProtection(final String channel, final String sourceNick, final String sourceLogin, final String sourceHostname) {
		// TODO Auto-generated method stub
		super.onSetTopicProtection(channel, sourceNick, sourceLogin, sourceHostname);
	}

	@Override
	protected void onTime(final String sourceNick, final String sourceLogin, final String sourceHostname, final String target) {
		// TODO Auto-generated method stub
		super.onTime(sourceNick, sourceLogin, sourceHostname, target);
	}

	@Override
	protected void onTopic(final String channel, final String topic, final String setBy, final long date, final boolean changed) {
		// TODO Auto-generated method stub
		super.onTopic(channel, topic, setBy, date, changed);
	}

	@Override
	protected void onUnknown(final String line) {
		// TODO Auto-generated method stub
		super.onUnknown(line);
	}

	@Override
	protected void onUserList(final String channel, final User[] users) {
		// TODO Auto-generated method stub
		super.onUserList(channel, users);
	}

	@Override
	protected void onUserMode(final String targetNick, final String sourceNick, final String sourceLogin, final String sourceHostname,
			final String mode) {
		// TODO Auto-generated method stub
		super.onUserMode(targetNick, sourceNick, sourceLogin, sourceHostname, mode);
	}

	@Override
	protected void onVersion(final String sourceNick, final String sourceLogin, final String sourceHostname, final String target) {
		// TODO Auto-generated method stub
		super.onVersion(sourceNick, sourceLogin, sourceHostname, target);
	}


	/**
	 * Disconnects from the server with the specified message, prints the specified message to error console and exits with the specified exit status.
	 */
	private void exitWithError(final String string, final int exitStatus) {
		quitServer(MESSAGE_FATAL_ERROR + ": " + string);
		System.err.println(string);
		System.exit(exitStatus);
	}

}
