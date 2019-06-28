package it.polimi.se2019.server.deserialize;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.polimi.se2019.server.cards.ammocrate.AmmoCrate;
import it.polimi.se2019.server.cards.powerup.PowerUp;
import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.games.Deck;
import it.polimi.se2019.server.games.board.Board;
import it.polimi.se2019.util.DeserializerConstants;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;

public class DirectDeserializers {

    private static final String MAP = "maps/map";
    private static final String JSON_PATH = "src/main/resources/json/";
    private static final String WEAPON = "weapons/weapons.json";
    private static final String POWERUP = "powerups/powerups.json";
    private static final String AMMOCRATE = "ammocrates/ammocrates.json";
    private static final String JSON = ".json";

    static DynamicDeserializerFactory factory = new DynamicDeserializerFactory();


    public DirectDeserializers() {
        factory.registerDeserializer(DeserializerConstants.TILE, new TileDeserializerSupplier());
        factory.registerDeserializer(DeserializerConstants.AMMOCRATEDECK, new AmmoCrateDeserializerSupplier());
        factory.registerDeserializer(DeserializerConstants.POWERUPDECK, new PowerUpDeserializerSupplier());
        factory.registerDeserializer(DeserializerConstants.WEAPONDECK, new WeaponDeckDeserializerSuppier());
        factory.registerDeserializer(DeserializerConstants.WEAPON, new WeaponDeserializerSupplier());
        factory.registerDeserializer(DeserializerConstants.ACTIONS, new ActionsDeserializerSupplier());
        factory.registerDeserializer(DeserializerConstants.OPTIONALEFFECTS, new OptionalEffectDeserializerSupplier());
        factory.registerDeserializer(DeserializerConstants.ACTIONUNIT, new ActionUnitDeserializerSupplier());
        factory.registerDeserializer(DeserializerConstants.EFFECTS, new EffectDeserializerSupplier());
        factory.registerDeserializer(DeserializerConstants.CONDITIONS, new ConditionDeserializerSupplier());
    }

    public static Board deserializeBoard(String mapIndex) {

        BoardDeserializer boardDeserializer = new BoardDeserializer();
        String path = JSON_PATH+MAP+mapIndex+JSON;
        Board board = null;

        board = (Board) deserialize(board, boardDeserializer, path);

        return board;
    }

    public static Deck<Weapon> deserialzerWeaponDeck() {
        WeaponDeckDeserializer weaponDeckDeserializer = (WeaponDeckDeserializer) factory.getDeserializer(DeserializerConstants.WEAPONDECK);

        String path = JSON_PATH+WEAPON;

        Deck<Weapon> weaponDeck = null;

        weaponDeck = (Deck<Weapon>) deserialize(weaponDeck, weaponDeckDeserializer, path);

        return weaponDeck;
    }

    public static Deck<PowerUp> deserialzerPowerUpDeck() {
        PowerUpDeserializer powerUpDeserializer = (PowerUpDeserializer) factory.getDeserializer(DeserializerConstants.POWERUPDECK);

        String path = JSON_PATH+POWERUP;

        Deck<PowerUp> powerUpDeck = null;

        powerUpDeck = (Deck<PowerUp>) deserialize(powerUpDeck, powerUpDeserializer, path);

        return powerUpDeck;
    }

    public static Deck<AmmoCrate> deserializeAmmoCrate() {
        AmmoCrateDeserializer ammoCrateDeserializer = (AmmoCrateDeserializer) factory.getDeserializer(DeserializerConstants.AMMOCRATEDECK);

        String path = JSON_PATH+AMMOCRATE;

        Deck<AmmoCrate> ammoCrateDeck = null;

        ammoCrateDeck = (Deck<AmmoCrate>) deserialize(ammoCrateDeck, ammoCrateDeserializer, path);

        return ammoCrateDeck;
    }

    private static Object deserialize(Object object, RandomDeserializer deserializer, String path) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(path))) {

            JsonParser parser = new JsonParser();
            JsonObject json = parser.parse(bufferedReader).getAsJsonObject();
            object = deserializer.deserialize(json, factory);
        } catch (IOException | ClassNotFoundException e) {
            Logger.getGlobal().warning(e.toString());
        }
        return object;
    }
}

