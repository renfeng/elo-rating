package work.fair24.elo;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Cli {

	/**
	 * New players are assigned a rating of 1500
	 * https://en.wikipedia.org/wiki/Elo_rating_system
	 */
	private static final int RATING = 1500;

	/**
	 * Players below 2100: K-factor of 32 used
	 * https://en.wikipedia.org/wiki/Elo_rating_system
	 */
	private static final int K_FACTOR = 32;

	private final VelocityContext context;

	private final Map<Integer, Player> players;

	private Cli(Map<Integer, Player> players) {

		/*
		 * https://stackoverflow.com/questions/9051413/unable-to-find-velocity-template-resources
		 */
		Properties p = new Properties();
		p.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		p.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		Velocity.init(p);

		/*
		 * https://stackoverflow.com/questions/8820240/how-to-format-numbers-in-velocity-templates
		 */
		context = new VelocityContext();
//		context.put("numberTool", new NumberTool());
		context.put("numberTool", "");

		this.players = players;
	}

	public static void main(String... args) throws IOException {

		if (args.length < 2) {
			throw new IllegalArgumentException("Missing parameters.\n" +
					"First parameter (required): path to a file in which each line defines a player;\n" +
					"Second parameter (required): path to a file in which each line defines a match\n" +
					"Third parameter (optional): name of a player whose status will be printed");
		}

		String names = args[0];
		String matches = args[1];
		String output = args.length == 3 ? args[2] : null;

		//		System.out.println(System.getProperty("user.dir"));

		Map<Integer, Player> players = Files.lines(Paths.get(names))
				.map(Cli::readPlayer)
				.collect(Collectors.toMap(Player::getId, Function.identity()));

		Cli cli = new Cli(players);

		Files.lines(Paths.get(matches))
				.map(cli::loadMatch)
				.forEach(Match::applyEloRating);

		if (output == null) {
			cli.printNextMatches();
		} else if (output.equals("show_ranking")) {
			cli.printRankings();
		} else {
			cli.printPlayer(output);
		}
	}

	private Match loadMatch(String s) {
		/*
		 * first is winner, second is loser
		 * TODO new format for draw
		 */
		String[] split = StringUtils.split(s, ' ');
		int playerA = Integer.parseInt(split[0]);
		int playerB = Integer.parseInt(split[1]);
		return new Match(players.get(playerA), 1, players.get(playerB), 0);
	}

	private static Player readPlayer(String s) {
		String[] split = StringUtils.split(s, ' ');
		int id = Integer.parseInt(split[0]);
		String name = split[1];

		Player player = new Player(RATING, K_FACTOR);
		player.setId(id);
		player.setName(name);

		return player;
	}

	private void printPlayer(String name) {
		/*
		 * a report for each person, showing with whom they played and how they fared
		 *
		 * check every player in case of duplicate USER_NAME
		 */
		for (Player player : players.values()) {
			if (!player.getName().equals(name)) {
				continue;
			}

			context.put("player", player);

			print("player.vm");
		}
	}

	private void printRankings() {
		/*
		 * a list of players sorted by score, their ranking (position in the list) and their number of wins and
		 * losses
		 */
		List<Player> ranking = new ArrayList<>(players.values());
		ranking.sort((o1, o2) -> -Double.compare(o1.getRating(), o2.getRating()));

		context.put("ranking", ranking);

		print("ranking.vm");
	}

	private void printNextMatches() {
		/*
		 * a list of suggested next matches
		 */
		SortedMap<Player, List<Player>> nextPlayers = new TreeMap<>(Comparator.comparingInt(Player::getId));
		for (Player player : players.values()) {
			List<Player> nextPlayer = new ArrayList<>(players.values());
			nextPlayer.removeAll(player.getWins());
			nextPlayer.removeAll(player.getLosses());
			nextPlayer.removeAll(player.getDraws());
			nextPlayer.remove(player);
			nextPlayers.put(player, nextPlayer);
		}

		context.put("next", nextPlayers);

		print("next.vm");
	}

	private void print(String template) {
		Writer writer = new StringWriter();
		Velocity.getTemplate(template).merge(context, writer);
		System.out.println(writer.toString());
	}
}
