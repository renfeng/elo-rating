package work.fair24.elo;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.tools.generic.EscapeTool;
import org.apache.velocity.tools.generic.NumberTool;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Cli {

	public static void main(String... args) throws IOException {

		if (args.length < 2) {
			throw new IllegalArgumentException();
		}

		String names = args[0];
		String matches = args[1];
		String output = args.length == 3 ? args[2] : null;

		Map<Integer, Player> players = new HashMap<>();
		Files.lines(Paths.get(names)).forEach(s -> {
			String[] split = StringUtils.split(s, ' ');
			int id = Integer.parseInt(split[0]);
			String name = split[1];

			Player player = new Player(1500, 32);
			player.setId(id);
			player.setName(name);

			players.put(id, player);
		});

		EloRating eloRating = new EloRating();
		Files.lines(Paths.get(matches)).forEach(s -> {
			String[] split = StringUtils.split(s, ' ');
			int playerA = Integer.parseInt(split[0]);
			int playerB = Integer.parseInt(split[1]);
			eloRating.match(players.get(playerA), 1, players.get(playerB), 0);
		});

//		System.out.println(System.getProperty("user.dir"));

		/*
		 * https://stackoverflow.com/questions/9051413/unable-to-find-velocity-template-resources
		 */
		Properties p = new Properties();
		p.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		p.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		Velocity.init(p);

		VelocityContext context = new VelocityContext();

		/*
		 * https://stackoverflow.com/questions/8820240/how-to-format-numbers-in-velocity-templates
		 */
		context.put("numberTool", new NumberTool());

		if (output == null) {
			/*
			 * a list of suggested next matches
			 */
			SortedMap<Player, List<Player>> next = new TreeMap<>(Comparator.comparingInt(Player::getId));
			for (Player player : players.values()) {
				List<Player> playerNext = new ArrayList<>(players.values());
				playerNext.removeAll(player.getWins());
				playerNext.removeAll(player.getLosses());
				playerNext.removeAll(player.getDraws());
				playerNext.remove(player);
				next.put(player, playerNext);
			}

			context.put("next", next);

			Writer writer = new StringWriter();
			Velocity.getTemplate("next.vm").merge(context, writer);
			System.out.println(writer.toString());

		} else if (output.equals("show_ranking")) {
			/*
			 * a list of players sorted by score, their ranking (position in the list) and their number of wins and
			 * losses
			 */
			List<Player> ranking = new ArrayList<>(players.values());
			Collections.sort(ranking, (o1, o2) -> -Double.compare(o1.getRating(), o2.getRating()));

			context.put("ranking", ranking);

			Writer writer = new StringWriter();
			Velocity.getTemplate("ranking.vm").merge(context, writer);
			System.out.println(writer.toString());

		} else {
			/*
			 * a report for each person, showing with whom they played and how they fared
			 *
			 * check every player in case of duplicate USER_NAME
			 */
			for (Player player : players.values()) {
				if (!player.getName().equals(output)) {
					continue;
				}

				context.put("player", player);

				Writer writer = new StringWriter();
				Velocity.getTemplate("player.vm").merge(context, writer);
				System.out.println(writer.toString());

			}
		}
	}
}
