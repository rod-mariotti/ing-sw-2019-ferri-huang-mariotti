package it.polimi.se2019.server.actions.effects;

import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.*;
import it.polimi.se2019.server.games.player.CharacterState;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.games.player.PlayerColor;
import it.polimi.se2019.server.users.UserData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class DamageTargetListTest {

    Tile tile;
    Tile[][] tileMap;
    Board board;
    Game game;
    Player p1, p2, p3, p4;
    Map<String, List<Targetable>> targets;
    List<Targetable> list;

    @Before
    public void setUp() {
        targets = new HashMap<>();
        game = new Game();
        tileMap = new Tile[2][3];
        LinkType[] links00 = {LinkType.WALL, LinkType.DOOR, LinkType.DOOR, LinkType.WALL};
        tileMap[0][0] = new NormalTile(RoomColor.RED, links00, null);
        LinkType[] links01 = {LinkType.DOOR, LinkType.DOOR, LinkType.OPEN, LinkType.WALL};
        tileMap[0][1] = new NormalTile(RoomColor.YELLOW, links01, null);
        LinkType[] links10 = {LinkType.WALL, LinkType.WALL, LinkType.OPEN, LinkType.DOOR};
        tileMap[1][0] = new NormalTile(RoomColor.BLUE, links10, null);
        LinkType[] links11 = {LinkType.OPEN, LinkType.WALL, LinkType.WALL, LinkType.DOOR};
        tileMap[1][1] = new NormalTile(RoomColor.BLUE, links11, null);
        LinkType[] links02 = {LinkType.OPEN, LinkType.DOOR, LinkType.WALL, LinkType.WALL};
        tileMap[0][2] = new NormalTile(RoomColor.YELLOW, links02, null);
        LinkType[] links12 = {LinkType.WALL, LinkType.WALL, LinkType.WALL, LinkType.DOOR};
        tileMap[1][2] = new NormalTile(RoomColor.WHITE, links12, null);
        board = new Board(tileMap);
        game.setBoard(board);

        p1 = new Player(true, new UserData("A"), new CharacterState(), PlayerColor.BLUE);
        p1.getCharacterState().setTile(tileMap[1][0]);
        p2 = new Player(true, new UserData("B"), new CharacterState(), PlayerColor.GREEN);
        p2.getCharacterState().setTile(tileMap[1][1]);
        p3 = new Player(true, new UserData("C"), new CharacterState(), PlayerColor.YELLOW);
        p3.getCharacterState().setTile(tileMap[1][2]);
        p4 = new Player(true, new UserData("D"), new CharacterState(), PlayerColor.GREY);
        p4.getCharacterState().setTile(tileMap[0][1]);
        game.setPlayerList(new ArrayList<>(Arrays.asList(p1,p2,p3,p4)));

    }

    @After
    public void tearDown() {
        tile = null;
        tileMap = null;
        board = null;
        game = null;
        p1 = null;
        p2 = null;
        p3 =null;
        p4 = null;
        targets = null;
        list = null;
    }

    @Test
    public void testDamageTargetList() {
        Effect effect = new DamageTargetList(2);
        List<Targetable> targetList = new ArrayList<>();
        targets.put("targetList", targetList);
        game.setCurrentPlayer(p1);

        targetList.addAll(Arrays.asList(p2, p3, p4));
        effect.run(game, targets);
        assertEquals(0, p1.getCharacterState().getDamageBar().size());
        assertEquals(2, p2.getCharacterState().getDamageBar().size());
        assertEquals(2, p3.getCharacterState().getDamageBar().size());
        assertEquals(2, p4.getCharacterState().getDamageBar().size());
        p2.getCharacterState().getDamageBar().stream()
                .forEach(pc -> assertEquals(PlayerColor.BLUE, pc));
        p3.getCharacterState().getDamageBar().stream()
                .forEach(pc -> assertEquals(PlayerColor.BLUE, pc));
        p4.getCharacterState().getDamageBar().stream()
                .forEach(pc -> assertEquals(PlayerColor.BLUE, pc));

    }
}