package it.polimi.se2019.util;

import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.games.Game;
import it.polimi.se2019.server.games.KillShotTrack;
import it.polimi.se2019.server.games.board.Board;
import it.polimi.se2019.server.games.player.CharacterState;

public interface LocalModel {
    void setCharacterState(CharacterState characterState);
    void setGame(Game game);
    void setKillShotTrack(KillShotTrack killShotTrack);
    void updatePlayerWeapon(Weapon weaponToUpdate);

    Board getBoard();
    Game getGame();
}