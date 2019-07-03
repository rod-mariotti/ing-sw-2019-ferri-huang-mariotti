package it.polimi.se2019.client.util;

import it.polimi.se2019.client.View;
import it.polimi.se2019.server.exceptions.PlayerNotFoundException;
import it.polimi.se2019.util.Response;
import javafx.application.Platform;

import java.util.logging.Logger;

public class ClientCommandHandler {
    private View view;
    
    public ClientCommandHandler(View view) {
        this.view = view;
    }

    public void handle(Response request) {
        if (request.getSuccess() && request.getMessage().equals(Constants.FINISHGAME)) {
            this.view.showMessage(request.getMessage());
        } else if (!request.getSuccess()) {
            this.view.reportError(request.getMessage());
        } else {
            if (this.view.isCliTrueGuiFalse()) new Thread(() -> internalHandle(request)).start();
            else Platform.runLater(() -> internalHandle(request));
        }
    }

    private synchronized void internalHandle(Response request) {
        System.out.println(request.serialize());
        if (request.getSuccess()) {
            // game initialization
            if (request.getGame() != null) {
                this.view.setGame(request.getGame());
                try {
                    this.view.setPlayerColor(request.getGame().getPlayerByNickname(this.view.getNickname()).getColor());
                } catch (PlayerNotFoundException e) {
                    Logger.getGlobal().warning(e.toString());
                }
                this.view.showGame();
            }
            if (request.getUpdateData() != null) {
                Logger.getGlobal().info("Update Data not null in command handler");
                this.view.update(request);
                request.getUpdateData().forEach(stateUpdate -> Logger.getGlobal().info("Received an update: "+stateUpdate.toString()));
                this.view.showGame();
            }
            this.view.showMessage(request.getMessage());
        } else {
            this.view.reportError(request.getMessage());
        }
    }
}
