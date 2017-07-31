package cybermorphyts3;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventType;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

import java.util.List;
import java.util.logging.Level;

import org.json.JSONObject;

/**
 * A simple chat bot that responds to any channel messages starting with "hello"
 * by greeting the client who sent the message.<br>
 * This bot also includes a simple ping testing feature.
 */
public class CybermorphyTs3 {

	public static void main(String[] args) {

		Database.initialize();

		final TS3Config config = new TS3Config();
		config.setHost(Config.ip);
		config.setDebugLevel(Level.ALL);

		final TS3Query query = new TS3Query(config);
		query.connect();

		final TS3Api api = query.getApi();
		api.login(Config.username, Config.password);
		api.selectVirtualServerById(2);
		api.setNickname("CyberMorphy");

		// Get our own client ID by running the "whoami" command
		final int clientId = api.whoAmI().getId();
		api.moveClient(clientId, api.getChannelByNameExact("andere spiele", true).getId());
		api.sendChannelMessage("CyberMorphy is online!");

		// Listen to chat in the channel the query is currently in
		// As we never changed the channel, this will be the default channel of
		// the server
		api.registerEvent(TS3EventType.TEXT_CHANNEL, 0);
		api.registerEvent(TS3EventType.TEXT_PRIVATE, 0);
		api.registerEvent(TS3EventType.SERVER, 0);
		// Register the event listener
		api.addTS3Listeners(new TS3EventAdapter() {

			Bet bet = new Bet();

			@Override
			public void onClientJoin(ClientJoinEvent e) {
				api.sendPrivateMessage(e.getClientId(), Config.WELCOMEMESSAGE);
			}

			@Override
			public void onTextMessage(TextMessageEvent e) {
				String message = e.getMessage();
				if (e.getTargetMode() == TextMessageTargetMode.CLIENT && e.getInvokerId() != clientId) {

					if (message.equalsIgnoreCase("!doorbell")) {
						klingeln(e);
					} else if (message.equalsIgnoreCase("!help")) {
						api.sendPrivateMessage(api.getClientByUId(e.getInvokerUniqueId()).getId(), Config.HELPTEXT);
					} else if (message.matches("[1-9]\\d*w[1-9]\\d*")) {
						würfeln(message, e);
					} else if (message.startsWith("!bier")) {
						bierMessage(message, e);
					} else if (message.startsWith("!hearthpwn")) {
						hearthpwn(e);
					} else if (message.startsWith("!hearthmeta")) {
						hearthmeta(e);
					} else if (message.startsWith("!hearthtier")) {
						hearthtier(e);
					} else if (message.startsWith("!sr")) {
						getPlayerSR(message, e);
					} // else if (message.startsWith("!bet")) {
						// betMessage(message, e);
						// }
				}

				// react to channel messages not sent by the query itself
				else if (e.getTargetMode() == TextMessageTargetMode.CHANNEL && e.getInvokerId() != clientId) {
					if ((message.startsWith("[URL]https://www.youtube.com/watch?v=")) || (message.startsWith("[URL]https://www.youtube.com/watch?v="))) {
						youtube(message);
					}
				}
			}

			private void hearthmeta(TextMessageEvent e) {
				api.moveClient(clientId, api.getClientByUId(e.getInvokerUniqueId()).getChannelId());
				api.sendChannelMessage("Hier findest du das aktuelle Meta: [URL]https://tempostorm.com/hearthstone/meta-snapshot/[/URL]");

			}

			private void hearthpwn(TextMessageEvent e) {
				api.moveClient(clientId, api.getClientByUId(e.getInvokerUniqueId()).getChannelId());
				api.sendChannelMessage(Config.HEARTHPWNT1);
				api.sendChannelMessage(Config.HEARTHPWNT2);
				api.sendChannelMessage(Config.HEARTHPWNT3);
				api.sendChannelMessage(Config.HEARTHPWNT4);
				api.sendChannelMessage(Config.HEARTHPWNT5);
			}

			private void hearthtier(TextMessageEvent e) {
				api.moveClient(clientId, api.getClientByUId(e.getInvokerUniqueId()).getChannelId());
				api.sendChannelMessage(Config.HEARTHTIERLIST);
			}

			// private void betMessage(String message, TextMessageEvent e) {
			// message = message.replace("!bet ", "");
			// if (message.startsWith("start")) {
			// if (!bet.isBetstarted()) {
			// message = message.replace("start ", "");
			// bet = new Bet();
			// bet.setBetstarted(true);
			// bet.setBetoptions(message.split(" "));
			// api.moveClient(clientId,
			// api.getClientByUId(e.getInvokerUniqueId()).getChannelId());
			// api.sendChannelMessage(api.getClientByUId(e.getInvokerUniqueId()).getNickname()
			// + " hat eine Wette gestartet\n Bitte schreibe mir privat welche
			// option du wählst:");
			// for (int i = 0; i < bet.getBetoptions().length; i++) {
			// api.sendChannelMessage("!bet " + i + " [dein Einsatz] | " +
			// bet.getBetoptions()[i]);
			// }
			// } else {
			// api.moveClient(clientId,
			// api.getClientByUId(e.getInvokerUniqueId()).getChannelId());
			// api.sendChannelMessage("Es läuft bereits eine Wette\n Bitte
			// schreibe mir privat welche option du wählst:");
			// for (int i = 0; i < bet.getBetoptions().length; i++) {
			// api.sendChannelMessage("!bet " + i + "[dein Einsatz] | " +
			// bet.getBetoptions()[i]);
			// }
			// }
			// }
			// else if (message.startsWith("end")) {
			//
			// } else {
			// String[] userBet = message.split(" ");
			// bet.getUserchoices().put(e.getInvokerUniqueId(),
			// Integer.parseInt(userBet[0]));
			// }
			//
			//
			// }

			private void getPlayerSR(String message, TextMessageEvent e) {
				String[] texte = message.split(" ");
				String urlToRead = "https://owapi.net/api/v3/u/" + texte[1] + "/stats";
				String json = Util.getHTML(urlToRead);

				JSONObject jobject = new JSONObject(json);
				jobject = jobject.getJSONObject("eu");
				jobject = jobject.getJSONObject("stats");
				jobject = jobject.getJSONObject("competitive");
				jobject = jobject.getJSONObject("overall_stats");
				int comprank = jobject.getInt("comprank");

				api.moveClient(clientId, api.getClientByUId(e.getInvokerUniqueId()).getChannelId());
				api.sendChannelMessage(texte[1] + " hat ein SR von " + comprank);

			}

			private void youtube(String message) {
				String[] texte = message.split(" ");
				for (int i = 0; i < texte.length; i++) {
					if (texte[i].toLowerCase().contains("http://www.youtube.com/watch?v=")
							|| texte[i].toLowerCase().contains("https://www.youtube.com/watch?v=")) {
						String link = texte[i].replace("[URL]", "").replace("[/URL]", "");
						api.sendChannelMessage(Util.getYoutube(link));
					}
				}

			}

			private void klingeln(TextMessageEvent e) {
				List<Client> clients = api.getClients();
				for (Client c : clients) {
					if (c.isInServerGroup(Config.SERVERGROUP_ADMIN)) {
						api.sendPrivateMessage(c.getId(), api.getClientByUId(e.getInvokerUniqueId()).getNickname() + " wartet in der lobby");
					}
				}
			}

			private void würfeln(String message, TextMessageEvent e) {
				String[] messagepart = message.split("w");
				// TODO: might want to catch to high numbers to prevent
				// a memory leak
				api.moveClient(clientId, api.getClientByUId(e.getInvokerUniqueId()).getChannelId());
				api.sendChannelMessage(api.getClientByUId(e.getInvokerUniqueId()).getNickname() + " hat eine "
						+ Integer.toString(Util.diceRoll(Integer.parseInt(messagepart[0]), Integer.parseInt(messagepart[1]))) + " mit " + e.getMessage()
						+ " gewürfelt. ");

			}

			private void bierMessage(String message, TextMessageEvent e) {
				message = message.replace("!bier ", "");
				api.moveClient(clientId, api.getClientByUId(e.getInvokerUniqueId()).getChannelId());
				if (message.equalsIgnoreCase("+")) {
					int beercount = Database.raiseBeerCount(e.getInvokerUniqueId());
					api.sendChannelMessage(
							api.getClientByUId(e.getInvokerUniqueId()).getNickname() + " trinkt Bier Nummer " + beercount + " seit dem letzten reset. ");
				} else if (message.equalsIgnoreCase("-")) {
					int beercount = Database.lowerBeerCount(e.getInvokerUniqueId());
					api.sendChannelMessage(
							api.getClientByUId(e.getInvokerUniqueId()).getNickname() + " trinkt Bier Nummer " + beercount + " seit dem letzten reset. ");
				} else if (message.equalsIgnoreCase("reset")) {
					Database.resetBeerCount(e.getInvokerUniqueId());
					api.sendChannelMessage(api.getClientByUId(e.getInvokerUniqueId()).getNickname() + " 's Bierzähler wurde zurückgesetzt. ");
				} else {
					Client client = api.getClientByNameExact(message, true);
					if (client != null) {
						int beercount = Database.userBeerCount(client.getUniqueIdentifier());
						api.sendChannelMessage(message + " trinkt Bier Nummer " + beercount + " seit dem letzten reset. ");
					} else {
						api.sendPrivateMessage(api.getClientByUId(e.getInvokerUniqueId()).getId(), message + " ist kein valider Nutzer. ");
					}
				}

			}
		});

	}

}
