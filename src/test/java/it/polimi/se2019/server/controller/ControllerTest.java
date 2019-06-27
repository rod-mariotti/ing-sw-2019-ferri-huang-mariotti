package it.polimi.se2019.server.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.polimi.se2019.client.util.Constants;
import it.polimi.se2019.server.ServerApp;
import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.deserialize.*;
import it.polimi.se2019.server.exceptions.IllegalPlayerActionException;
import it.polimi.se2019.server.exceptions.PlayerNotFoundException;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.GameManager;
import it.polimi.se2019.server.games.Targetable;
import it.polimi.se2019.server.games.board.*;
import it.polimi.se2019.server.games.player.AmmoColor;
import it.polimi.se2019.server.games.player.Player;
import it.polimi.se2019.server.games.player.PlayerColor;
import it.polimi.se2019.server.net.CommandHandler;
import it.polimi.se2019.server.playerActions.*;
import it.polimi.se2019.server.users.UserData;
import it.polimi.se2019.util.InternalMessage;
import it.polimi.se2019.util.Request;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ControllerTest {

    private static final String TESTNICK0 = "testNick0";
    private static final String TESTNICK1 = "testNick1";
    private static final String MOVEPLAYERACTION = "it.polimi.se2019.server.playerActions.MovePlayerAction";
    private static final String SHOOTPLAYERACTION = "it.polimi.se2019.server.playerActions.ShootPlayerAction";
    private static final String GRABPLAYERACTION = "it.polimi.se2019.server.playerActions.GrabPlayerAction";
    private static final String RELOADPLAYERACTION = "it.polimi.se2019.server.playerActions.ReloadPlayerAction";
    private static final String SHOOTWEAPONSELECTION = "it.polimi.se2019.server.playerAction.ShootWeaponSelection";

    private GameManager gameManager = new GameManager();
    private Game game;
    private Controller controller;
    private Tile[][] tileMap;
    private Board board;
    private CommandHandler actualPlayerCommandHandler = new CommandHandler();
    private Weapon weapon;

    private DynamicDeserializerFactory factory = new DynamicDeserializerFactory();
    private WeaponDeserializer weaponDeserializer = new WeaponDeserializer();

    @Before
    @SuppressWarnings("Duplicates")
    public void setUp() throws GameManager.AlreadyPlayingException, GameManager.GameNotFoundException, PlayerNotFoundException {
        // GameManager and Game init
        ServerApp serverApp = new ServerApp();
        gameManager.init("src/test/java/it/polimi/se2019/server/games/data/games_dump.json");
        gameManager.getMapPreference().add("0");
        int waitingListMaxSize = 5;
        for (int i = 0; i <= waitingListMaxSize; i++) {
            UserData user = new UserData("testNick" + i);
            if (i == 0) {
                gameManager.addUserToWaitingList(user, actualPlayerCommandHandler);
            }
            else {
                gameManager.addUserToWaitingList(user, new CommandHandler());
            }
        }

        game = gameManager.retrieveGame(TESTNICK0);

        // Board init
        tileMap = new Tile[2][3];
        LinkType[] links00 = {LinkType.WALL, LinkType.DOOR, LinkType.DOOR, LinkType.WALL};
        tileMap[0][0] = new SpawnTile(RoomColor.RED, links00, null);
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
        board = new Board("lol", tileMap);

        game.setBoard(board);

        // Spawn players
        Player player0 = game.getPlayerByNickname(TESTNICK0);
        player0.getCharacterState().setTile(tileMap[0][0]);

        Player player1 = game.getPlayerByNickname(TESTNICK1);
        player1.getCharacterState().setTile(tileMap[0][1]);

        // initialize Weapons
        factory.registerDeserializer("actions", new ActionsDeserializerSupplier());
        factory.registerDeserializer("optionaleffects", new OptionalEffectDeserializerSupplier());
        factory.registerDeserializer("actionunit", new ActionUnitDeserializerSupplier());
        factory.registerDeserializer("effects", new EffectDeserializerSupplier());
        factory.registerDeserializer("conditions", new ConditionDeserializerSupplier());

        String path = "src/test/java/it/polimi/se2019/server/deserialize/data/cards.json";
        BufferedReader bufferedReader;
        try {
            bufferedReader = new BufferedReader(new FileReader(path));
            JsonParser parser = new JsonParser();
            JsonObject json = parser.parse(bufferedReader).getAsJsonObject();

            weapon = weaponDeserializer.deserialize(json, factory);

            try {
                bufferedReader.close();
            } catch (IOException e) {
                Assert.fail("Buffered reader could not close correctly.");
            }
        } catch (ClassNotFoundException | FileNotFoundException e) {
            Assert.fail("Unable to deserialize weapon.");
        }

        // add weapon to testNick0
        player0.getCharacterState().addWeapon(weapon);
        player0.getCharacterState().addAmmo(getAmmoBag(3));

        // Set testNick0 as current player
        game.setCurrentPlayer(player0);

        // Controller init
        controller = new Controller(gameManager);

        actualPlayerCommandHandler.register(controller);
    }

    @Test
    public void testMovePlayerAction() throws GameManager.GameNotFoundException, PlayerNotFoundException {
        controller.setControllerStateForGame(game, new WaitingForMainActions());

        Player player = gameManager.retrieveGame(TESTNICK0).getPlayerByNickname(TESTNICK0);

        List<Targetable> targetableList = new ArrayList<>();
        targetableList.add(tileMap[0][1]);

        Map<String, List<Targetable>> command = new HashMap<>();
        command.put(Constants.KEY_ORDER, Arrays.asList(new MovePlayerAction(0)));
        command.put(MOVEPLAYERACTION, targetableList);

        InternalMessage message =  new InternalMessage(command);
        Request request = new Request(message, TESTNICK0);

        actualPlayerCommandHandler.handleLocalRequest(request);

        Assert.assertSame(player.getCharacterState().getTile(), tileMap[0][1]);
    }

    @Test
    public void testShootWeaponSelection() throws GameManager.GameNotFoundException, PlayerNotFoundException {
        final int ACTIONUNITPOSITION = 0;

        ControllerState waitingForMainActions = new WaitingForMainActions();
        controller.setControllerStateForGame(game, waitingForMainActions);

        Player player1 = gameManager.retrieveGame(TESTNICK1).getPlayerByNickname(TESTNICK1);

        List<Targetable> targetableList = new ArrayList<>();
        targetableList.add(weapon);

        Map<String, List<Targetable>> command = new HashMap<>();
        command.put(Constants.KEY_ORDER, Arrays.asList(new ShootWeaponSelection(0)));
        command.put(SHOOTWEAPONSELECTION, targetableList);

        InternalMessage message = new InternalMessage(command);
        Request request = new Request(message, TESTNICK0);

        actualPlayerCommandHandler.handleLocalRequest(request);

        ControllerState waitingForEffects = new WaitingForEffects(weapon, waitingForMainActions);
        controller.setControllerStateForGame(game, waitingForEffects);

        targetableList = new ArrayList<>();

        targetableList.add(player1);
        targetableList.add(weapon);
        targetableList.add(weapon.getActionUnitList().get(ACTIONUNITPOSITION));
        targetableList.add(null);
        targetableList.add(null);

        command = new HashMap<>();
        command.put(Constants.KEY_ORDER, Arrays.asList(new ShootPlayerAction(0)));
        command.put(SHOOTPLAYERACTION, targetableList);

        message = new InternalMessage(command);
        request = new Request(message, TESTNICK0);

        actualPlayerCommandHandler.handleLocalRequest(request);

        List<PlayerColor> damageBar = new ArrayList<>();
        damageBar.add(PlayerColor.BLUE);
        damageBar.add(PlayerColor.BLUE);
        Assert.assertEquals(damageBar, player1.getCharacterState().getDamageBar());
    }

    @Test
    public void testGrabPlayerAction() throws GameManager.GameNotFoundException, PlayerNotFoundException {
        ControllerState waitingForMainActions = new WaitingForMainActions();
        controller.setControllerStateForGame(game, waitingForMainActions);

        // add weapon to tileMap[0][0] SpawnTile
        List<Weapon> weaponList = new ArrayList<>();
        weaponList.add(weapon);

        tileMap[0][0].setWeaponCrate(weaponList);

        Player player0 = gameManager.retrieveGame(TESTNICK0).getPlayerByNickname(TESTNICK0);
        player0.getCharacterState().addAmmo(getAmmoBag(3));

        List<Targetable> targetableList = new ArrayList<>();
        targetableList.add(weapon);

        Map<String, List<Targetable>> command = new HashMap<>();
        command.put(Constants.KEY_ORDER, Arrays.asList(new GrabPlayerAction(0)));
        command.put(GRABPLAYERACTION, targetableList);

        InternalMessage message = new InternalMessage(command);
        Request request = new Request(message, TESTNICK0);

        player0.getCharacterState().addAmmo(getAmmoBag(3));
        actualPlayerCommandHandler.handleLocalRequest(request);

        // assert player have two weapons
        Assert.assertEquals(2, player0.getCharacterState().getWeaponBag().size());

        player0.getCharacterState().setAmmoBag(getAmmoBag(0));
        actualPlayerCommandHandler.handleLocalRequest(request);

        // assert player still have two weapons (unable to pay pickup cost)
        Assert.assertEquals(2, player0.getCharacterState().getWeaponBag().size());

        player0.getCharacterState().addAmmo(getAmmoBag(3));
        actualPlayerCommandHandler.handleLocalRequest(request);

        // assert player have tree weapons
        Assert.assertEquals(3, player0.getCharacterState().getWeaponBag().size());

        actualPlayerCommandHandler.handleLocalRequest(request);

        // assert player still have tree weapons (bag full)
        Assert.assertEquals(3, player0.getCharacterState().getWeaponBag().size());
        Assert.assertEquals(2, (int) player0.getCharacterState().getAmmoBag().get(AmmoColor.BLUE));
        Assert.assertEquals(2, (int) player0.getCharacterState().getAmmoBag().get(AmmoColor.YELLOW));
    }

    @Test
    public void testReloadPlayerAction() throws GameManager.GameNotFoundException, PlayerNotFoundException {
        Player player0 = gameManager.retrieveGame(TESTNICK0).getPlayerByNickname(TESTNICK0);
        player0.getCharacterState().addAmmo(getAmmoBag(3));

        List<Targetable> targetableList = new ArrayList<>();
        targetableList.add(weapon);

        Map<String, List<Targetable>> command = new HashMap<>();
        command.put(Constants.KEY_ORDER, Arrays.asList(new ReloadPlayerAction(0)));
        command.putIfAbsent(RELOADPLAYERACTION, targetableList);

        weapon.setLoaded(false);

        Assert.assertFalse(player0.getCharacterState().getWeaponBag().get(0).isLoaded());

        Map<AmmoColor, Integer> ammoBag = player0.getCharacterState().getAmmoBag();

        Assert.assertTrue(ammoBag.get(AmmoColor.BLUE) >= weapon.getReloadCostAsMap().get(AmmoColor.BLUE));
        Assert.assertTrue(ammoBag.get(AmmoColor.YELLOW) >= weapon.getReloadCostAsMap().get(AmmoColor.YELLOW));

        InternalMessage message = new InternalMessage(command);
        Request request = new Request(message, TESTNICK0);

        actualPlayerCommandHandler.handleLocalRequest(request);

        Assert.assertTrue(player0.getCharacterState().getWeaponBag().get(0).isLoaded());
    }

    @Test
    public void testActionAvailability() throws GameManager.GameNotFoundException, PlayerNotFoundException, IllegalPlayerActionException {
        Player player0 = gameManager.retrieveGame(TESTNICK0).getPlayerByNickname(TESTNICK0);
        player0.getCharacterState().getDamageBar().clear();

        List<Targetable> targetableList = new ArrayList<>();
        targetableList.add(tileMap[1][1]);

        Map<String, List<Targetable>> command = new HashMap<>();
        command.put(Constants.KEY_ORDER, Arrays.asList(new MovePlayerAction(0), new GrabPlayerAction(0)));
        command.put(MOVEPLAYERACTION, targetableList);
        targetableList = new ArrayList<>();
        targetableList.add(weapon);
        command.put(GRABPLAYERACTION, targetableList);

        InternalMessage message =  new InternalMessage(command);
        Request request = new Request(message, TESTNICK0);

        actualPlayerCommandHandler.handleLocalRequest(request);
        // not available action, so no move performed
        Assert.assertSame(player0.getCharacterState().getTile(), tileMap[0][0]);

    }

    private Map<AmmoColor, Integer> getAmmoBag(int amountOfAmmoPerColor) {
        Map<AmmoColor, Integer> ammo = new HashMap<>();
        ammo.put(AmmoColor.BLUE, amountOfAmmoPerColor);
        ammo.put(AmmoColor.RED, amountOfAmmoPerColor);
        ammo.put(AmmoColor.YELLOW, amountOfAmmoPerColor);

        return ammo;
    }
}
