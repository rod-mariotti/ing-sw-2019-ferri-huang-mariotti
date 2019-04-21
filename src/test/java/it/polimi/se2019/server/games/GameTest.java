package it.polimi.se2019.server.games;

import it.polimi.se2019.server.cards.powerup.PowerUp;
import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.games.board.Board;
import it.polimi.se2019.server.games.board.WeaponCrate;
import it.polimi.se2019.server.games.player.CharacterState;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.users.UserData;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.*;

public class GameTest {

    Game game;

    @Before
    public void setUp() {
        game = new Game();
    }

    @After
    public void tearDown() {
        game = null;
    }

    @Test
    public void testGenerateGameData() {

        GameData gameData = game.generateGameData();

        Assert.assertEquals(game.getId(), gameData.getId());
        Assert.assertEquals(game.getStartDate(), gameData.getStartDate());
    }

    @Test
    public void testUpdateTurn() {
    }

    @Test
    public void testSetCurrentPlayer_NotActivePlayer() {

        Player nextPlayer = new Player(false, new UserData("Nick"), new CharacterState());
        Player currPlayer = game.getCurrentPlayer();

        game.setCurrentPlayer(nextPlayer);

        Assert.assertEquals(currPlayer, game.getCurrentPlayer());
    }

    @Test
    public void testSetCurrentPlayer_ActivePlayer() {

        Player nextPlayer = new Player(true, new UserData("Nick"), new CharacterState());

        game.setCurrentPlayer(nextPlayer);

        Assert.assertEquals(nextPlayer, game.getCurrentPlayer());
    }


    @Test
    public void testSetId() {

        game.setId("def");

        Assert.assertEquals("def", game.getId());
    }

    @Test
    public void testSetStartDate() {

        Date newDate = new Date();

        game.setStartDate(newDate);

        Assert.assertEquals(newDate, game.getStartDate());
    }

    @Test
    public void testSetPlayerList() {

        Player p1 = new Player(false, new UserData("Nick1"), new CharacterState());
        Player p2 = new Player(true, new UserData("Nick2"), new CharacterState());
        ArrayList<Player> playerList = new ArrayList<>(Arrays.asList(p1, p2));

        game.setPlayerList(playerList);

        Assert.assertEquals(playerList, game.getPlayerList());
    }

    @Test
    public void testSetBoard() {

        Board board = new Board();

        game.setBoard(board);

        Assert.assertEquals(board, game.getBoard());
    }

    @Test
    public void testSetKillshotTrack() {

        Integer killshots = 1;

        game.setKillshotTrack(killshots);

        Assert.assertEquals(killshots, game.getKillshotTrack());
    }

    @Test
    public void testSetDeaths() {

        Integer deaths = 3;

        game.setDeaths(deaths);

        Assert.assertEquals(deaths, game.getDeaths());
    }

    @Test
    public void testSetWeaponDeck() {

        ArrayList<Weapon> weaponDeck = new ArrayList<>();

        game.setWeaponDeck(weaponDeck);

        Assert.assertEquals(weaponDeck, game.getWeaponDeck());
    }

    @Test
    public void testSetPowerupDeck() {

        ArrayList<PowerUp> powerupDeck = new ArrayList<>();

        game.setPowerupDeck(powerupDeck);

        Assert.assertEquals(powerupDeck, game.getPowerupDeck());
    }
}