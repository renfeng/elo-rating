package work.fair24.elo;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import static org.junit.Assert.*;

public class CliTest {
	@Test
	public void testRanking() throws Exception {
		Cli.main(StringUtils.split("names matches show_ranking", ' '));
	}

	@Test
	public void testPlayer() throws Exception {
		Cli.main(StringUtils.split("names matches Brianna", ' '));
	}

	@Test
	public void testNext() throws Exception {
		Cli.main(StringUtils.split("names matches", ' '));
	}

}