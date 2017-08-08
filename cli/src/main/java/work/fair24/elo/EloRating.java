package work.fair24.elo;

public class EloRating {

	/*
	 * for each 400 rating points of advantage over the opponent, the expected score is magnified ten times in
	 * comparison to the opponent's expected score
	 */
	public static final double RATING_POINTS = 400;
	public static final double MAGNIFIED = 10;

	public void match(Player playerA, double scoreA, Player playerB, double scoreB) {

		/*
		 * https://en.wikipedia.org/wiki/Elo_rating_system#Mathematical_details
		 *
		 * Suppose Player A has a rating of 1613, and plays in a five-round tournament. He or she loses to a player
		 * rated 1609, draws with a player rated 1477, defeats a player rated 1388, defeats a player rated 1586, and
		 * loses to a player rated 1720. The player's actual score is (0 + 0.5 + 1 + 1 + 0) = 2.5. The expected
		 * score, calculated according to the formula above, was (0.51 + 0.69 + 0.79 + 0.54 + 0.35) = 2.88.
		 * Therefore, the player's new rating is (1613 + 32(2.5 − 2.88)) = 1601, assuming that a K-factor of 32 is
		 * used.
		 *
		 * 1 - win
		 * 0 - loss
		 * .5 - a draw is considered half a win and half a loss
		 */

		double qA = Math.pow(MAGNIFIED, playerA.getRating() / RATING_POINTS);
		double qB = Math.pow(MAGNIFIED, playerB.getRating() / RATING_POINTS);

		playerA.setRating(playerA.getRating() + playerA.getkFactor() * (scoreA - qA / (qA + qB)));
		playerB.setRating(playerB.getRating() + playerB.getkFactor() * (scoreB - qB / (qA + qB)));

		if (scoreA > scoreB) {
			playerA.getWins().add(playerB);
			playerB.getLosses().add(playerA);
		} else if (scoreA < scoreB) {
			playerA.getLosses().add(playerB);
			playerB.getWins().add(playerA);
		} else {
			/*
			 * this is an enhancement
			 */
			playerA.getDraws().add(playerB);
			playerB.getDraws().add(playerA);
		}
	}
}
