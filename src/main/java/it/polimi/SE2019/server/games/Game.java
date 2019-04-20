package it.polimi.SE2019.server.games;

import it.polimi.SE2019.server.cards.powerup.PowerUp;
import it.polimi.SE2019.server.cards.weapons.Weapon;
import it.polimi.SE2019.server.games.board.Board;
import it.polimi.SE2019.server.games.player.Player;

import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;

/**
 * 
 */
public class Game extends Observable {

	private String id;
	private Date startDate;
	private ArrayList<Player> playerList;
	private Player currentPlayer;
	private Board board;
	private Integer killshotTrack;
	private Integer deaths;
	private ArrayList<Weapon> weaponDeck;
	private ArrayList<PowerUp> powerupDeck;


	public Game(String id, Date startDate, ArrayList<Player> playerList, Player currentPlayer, Board board, Integer killshotTrack, Integer deaths, ArrayList<Weapon> weaponDeck, ArrayList<PowerUp> powerupDeck) {
		this.id = id;
		this.startDate = startDate;
		this.playerList = playerList;
		this.currentPlayer = currentPlayer;
		this.board = board;
		this.killshotTrack = killshotTrack;
		this.deaths = deaths;
		this.weaponDeck = weaponDeck;
		this.powerupDeck = powerupDeck;
	}

	public GameData generateGameData() {
		return null;
	}

	public void updateTurn() {

	}

	public Player getCurrentPlayer() {
		return currentPlayer;
	}

	public void setCurrentPlayer(Player currentPlayer) {
		this.currentPlayer = currentPlayer;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public ArrayList<Player> getPlayerList() {
		return playerList;
	}

	public void setPlayerList(ArrayList<Player> playerList) {
		this.playerList = playerList;
	}

	public Board getBoard() {
		return board;
	}

	public void setBoard(Board board) {
		this.board = board;
	}

	public Integer getKillshotTrack() {
		return killshotTrack;
	}

	public void setKillshotTrack(Integer killshotTrack) {
		this.killshotTrack = killshotTrack;
	}

	public Integer getDeaths() {
		return deaths;
	}

	public void setDeaths(Integer deaths) {
		this.deaths = deaths;
	}

	public ArrayList<Weapon> getWeaponDeck() {
		return weaponDeck;
	}

	public void setWeaponDeck(ArrayList<Weapon> weaponDeck) {
		this.weaponDeck = weaponDeck;
	}

	public ArrayList<PowerUp> getPowerupDeck() {
		return powerupDeck;
	}

	public void setPowerupDeck(ArrayList<PowerUp> powerupDeck) {
		this.powerupDeck = powerupDeck;
	}

}