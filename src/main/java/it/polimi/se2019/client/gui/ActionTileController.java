package it.polimi.se2019.client.gui;

import it.polimi.se2019.client.util.Constants;
import it.polimi.se2019.client.util.Util;
import it.polimi.se2019.server.cards.weapons.Weapon;
import it.polimi.se2019.server.exceptions.TileNotFoundException;
import it.polimi.se2019.server.games.board.Tile;
import it.polimi.se2019.server.games.player.CharacterState;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ActionTileController {

    private static final Logger logger = Logger.getLogger(ActionTileController.class.getName());

    private MainApp mainApp;
    private GridPane tileGrid;
    private GridPane ammoGrid;
    private GridPane playerGrid;
    private Label infoText;
    private GridPane progressBar;
    private Button cancelButton;
    private Button confirmButton;
    private List<GridPane> weaponCrateList;
    private GridPane myWeapons;
    private GridPane myPowerups;

    @FXML
    private Button mmm;
    @FXML
    private Button mg;
    @FXML
    private Button s;
    @FXML
    private Button r;
    @FXML
    private Button mrs;
    @FXML
    private Button mmmm;
    @FXML
    private Button mmg;
    @FXML
    private Button mmrs;
    @FXML
    private Button mmmg;

    public void initialize() {
        // do nothing
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void init() {
        mainApp.getGameBoardController().setActionTileController(this);
        initGrids();
        initInfo();
    }

    /**
     * Gets the references of grids for maps and cards.
     */
    public void initGrids() {
        BorderPane root = (BorderPane) mainApp.getPrimaryStage().getScene().getRoot();
        VBox vBox = (VBox) (root.getCenter()).lookup("#leftVBox");
        AnchorPane map = (AnchorPane) vBox.getChildren().get(0);

        tileGrid = (GridPane) map.lookup("#tileGrid");
        ammoGrid = (GridPane) map.lookup("#ammoGrid");
        playerGrid = (GridPane) map.lookup("#playerGrid");

        weaponCrateList = new ArrayList<>();
        weaponCrateList.add((GridPane) map.lookup("#blueWeapons"));
        weaponCrateList.add((GridPane) map.lookup("#redWeapons"));
        weaponCrateList.add((GridPane) map.lookup("#yellowWeapons"));

        myWeapons = (GridPane) vBox.lookup("#myWeapons");
        myPowerups = (GridPane) vBox.lookup("#myPowerups");

    }

    /**
     * Gets the references for the info objects.
     *
     */
    public void initInfo() {
        BorderPane root = (BorderPane) mainApp.getPrimaryStage().getScene().getRoot();

        infoText = (Label) root.getCenter().lookup("#infoText");
        infoText.setText("Select an action(1)");

        progressBar = (GridPane) root.getCenter().lookup(Constants.PROGRESS_BAR);

        confirmButton = (Button) root.getCenter().lookup("#confirmButton");
        cancelButton = (Button) root.getCenter().lookup("#cancelButton");


    }

    @FXML
    public void handleM() {
        mainApp.getInputRequested().add(this::getTile);
        mainApp.getInput();
    }

    @FXML
    public void handleMG() {
        mainApp.getInputRequested().add(this::getTile);
        mainApp.getInputRequested().add(this::getCard);
        mainApp.getInput();
    }

    @FXML
    public void handleS() {
        mainApp.getInputRequested().add(this::getShoot);
        mainApp.getInput();
    }

    @FXML
    public void handleR() {
        mainApp.getInputRequested().add(this::getReload);
        mainApp.getInput();
    }

    @FXML
    public void handleMRS() {
        mainApp.getInputRequested().add(this::getTile);
        mainApp.getInputRequested().add(this::getReload);
        mainApp.getInputRequested().add(this::getShoot);
        mainApp.getInput();
    }

    public void getTile(){

        disableActionButtons();
        tileGrid.toFront();
        tileGrid.setDisable(false);
        tileGrid.setVisible(true);

        infoText.setText("Select 1 tile ");
        cancelButton.setDisable(false);

        setUpProgressBar(1);
    }

    public void getCard() {

        disableActionButtons();

        infoText.setText("Select 1 card ");
        cancelButton.setDisable(false);
        showGrabbableCards();

        setUpProgressBar(1);
    }

    public void getShoot() {

        disableActionButtons();
        infoText.setText("Select 1 card ");
        cancelButton.setDisable(false);

        myWeapons.setDisable(false);
        myWeapons.getStyleClass().add("my-node");
        showMyLoadedWeapons();

        setUpProgressBar(1);
    }

    public void getActionUnit() {

        disableActionButtons();


    }

    public void getReload() {

        disableActionButtons();
        infoText.setText("Select 1 card ");
        cancelButton.setDisable(false);
        confirmButton.setDisable(false);

        myWeapons.setDisable(false);
        myWeapons.getStyleClass().add("my-node");
        showMyUnloadedWeapons();
        mainApp.getGameBoardController().getIntermediateInput().putIfAbsent(Constants.RELOAD, new ArrayList<>());

        setUpProgressBar(3);

    }

    public void getTarget() {
        System.out.println("target");
        disableActionButtons();
        playerGrid.toFront();
        playerGrid.setDisable(false);
        playerGrid.setVisible(true);

        infoText.setText("Select 3 players ");
        cancelButton.setDisable(false);

        //TODO do not let player choose himself
        playerGrid.getChildren().stream()
                .map(n -> (VBox) n)
                .forEach(vBox -> vBox.getChildren().stream()
                        .map(n -> (HBox) n)
                        .filter(hbox -> !hbox.getChildren().isEmpty())
                        .forEach(hBox -> hBox.getChildren().stream()
                                .map(n -> (Circle) n)
                                .filter(Node::isVisible)
                                .forEach(c -> {
                                    if (c.getFill() != Paint.valueOf(mainApp.getPlayerColor().getColor())){
                                        c.setDisable(false);
                                        c.getStyleClass().add("my-shape");
                                    }
                                    else {
                                        c.setDisable(true);
                                    }
                                })
                        )
                );

        setUpProgressBar(3);
    }

    /**
     * Deactivates the action buttons.
     *
     */
    public void disableActionButtons() {
        if (mainApp.getGame().isFrenzy()) {
            mrs.setDisable(true);
            mmmm.setDisable(true);
            mmg.setDisable(true);
            mmrs.setDisable(true);
            mmmg.setDisable(true);
        }
        else {
            mmm.setDisable(true);
            mg.setDisable(true);
            s.setDisable(true);
            r.setDisable(true);
        }
    }

    /**
     * Prepares the number of circles indicating the max number of selections needed.
     * @param numOfTargets is the number of selections.
     */
    public void setUpProgressBar(int numOfTargets) {

        IntStream.range(0, numOfTargets)
                .forEach(i -> progressBar.getChildren().get(i).setVisible(true));
    }

    /**
     * Shows the objects that are grabbable from the player's position in the map.
     *
     */
    public void showGrabbableCards() {
        Tile t = null;
        if (mainApp.getPlayerInput().isEmpty()){
            t  = mainApp.getGame().getCurrentPlayer().getCharacterState().getTile();
        }
        else {
            int[] coords = Util.convertToCoords(Integer.parseInt(mainApp.getPlayerInput().get(Constants.TILE).get(0)));
            t = mainApp.getGame().getBoard().getTile(coords[0], coords[1]);
        }

        System.out.println("Grab: "+t);
        try {
            int[] coords = mainApp.getGame().getBoard().getTilePosition(t);
            if (t.isSpawnTile()) {
                System.out.println("spawn tile");
                String roomColor = t.getRoomColor().getColor();
                Optional<GridPane> optGrid = weaponCrateList.stream()
                        .filter(wc -> wc.getId().split("Weapons")[0].equalsIgnoreCase(roomColor))
                        .findFirst();
                if (optGrid.isPresent()){
                    optGrid.get().setDisable(false);
                    optGrid.get().getStyleClass().add("my-node");
                }
            }
            else {
                System.out.println("normal tile");
                ammoGrid.toFront();
                ammoGrid.setDisable(false);
                ammoGrid.setVisible(true);
                HBox hBox = (HBox) ammoGrid.getChildren().get(Util.convertToIndex(coords[0], coords[1]));
                Node n = hBox.getChildren().get(0);
                n.getStyleClass().add("my-node");
            }
        } catch (TileNotFoundException e) {
            logger.warning(e.toString());
        }
    }

    /**
     * Shows the unloaded weapons for selection, and hides the loaded ones.
     *
     */
    public void showMyUnloadedWeapons() {
        CharacterState myCharacterState =  mainApp.getGame().getPlayerList().stream()
                .filter(p -> p.getColor() == mainApp.getPlayerColor())
                .collect(Collectors.toList()).get(0).getCharacterState();
        List<Weapon> myWeaponsModel = myCharacterState.getWeapoonBag();

        IntStream.range(0, myWeaponsModel.size())
                .forEach(i -> {
                    ImageView iv = null;
                    iv = (ImageView) myWeapons.getChildren().get(i);
                    if (!myWeaponsModel.get(i).isLoaded()) {
                        iv.setOpacity(1.0);
                        iv.setDisable(false);
                        iv.setVisible(true);
                    }
                    else {
                        iv.setVisible(false);
                    }

                    mainApp.getGameBoardController().setCardSelectionBehavior(iv, myWeapons, Constants.RELOAD);
                });
    }

    /**
     * Shows the loaded weapons for selection, and hides the unloaded ones.
     *
     */
    public void showMyLoadedWeapons() {
        CharacterState myCharacterState =  mainApp.getGame().getPlayerList().stream()
                .filter(p -> p.getColor() == mainApp.getPlayerColor())
                .collect(Collectors.toList()).get(0).getCharacterState();
        List<Weapon> myWeaponsModel = myCharacterState.getWeapoonBag();


        IntStream.range(0, myWeaponsModel.size())
                .forEach(i -> {
                    ImageView iv = null;
                    iv = (ImageView) myWeapons.getChildren().get(i);
                    if (!myWeaponsModel.get(i).isLoaded()) {
                        iv.setVisible(false);
                    }
                    else {
                        iv.setVisible(true);
                        iv.setOpacity(1.0);
                        iv.setDisable(false);
                    }

                    mainApp.getGameBoardController().setCardSelectionBehavior(iv, myWeapons, Constants.SHOOT);
                });
    }




}
