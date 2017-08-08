package work.fair24.elo;

import java.util.ArrayList;
import java.util.List;

public class Player {

	private int id;
	private String name;
	private double rating;
	private double kFactor;

	private final List<Player> wins = new ArrayList<>();
	private final List<Player> losses = new ArrayList<>();
	private final List<Player> draws = new ArrayList<>();

	public Player(double rating, double kFactor) {
		this.rating = rating;
		this.kFactor = kFactor;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

	public double getkFactor() {
		return kFactor;
	}

	public void setkFactor(double kFactor) {
		this.kFactor = kFactor;
	}

	public List<Player> getWins() {
		return wins;
	}

	public List<Player> getLosses() {
		return losses;
	}

	public List<Player> getDraws() {
		return draws;
	}
}
