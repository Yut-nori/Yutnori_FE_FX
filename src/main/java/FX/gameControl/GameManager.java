package FX.gameControl;

import FX.screen.gameBoard.centerPanel.MainBoard;
import FX.screen.gameBoard.leftPanel.LeftPanel;
import FX.screen.gameBoard.rightPanel.RightPanel;
import FX.screen.gameBoard.topPanel.TopPanel;
import javafx.scene.layout.Pane;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

public class GameManager {

    /** 멤버 변수 **/
    private Pane rootPane; // JavaFX에서는 Pane 사용
    private final GameState gameState;
    private ScreenManager screenManager;

    /** API 객체 변수 **/
    private final FX.api.OptionAPI optionAPI;
    private final FX.api.GameAPI gameAPI;

    public GameManager(GameState gameState) {
        this.gameState = gameState;
        this.optionAPI = new FX.api.OptionAPI();
        this.gameAPI = new FX.api.GameAPI();
    }

    public void setScreenManager(ScreenManager screenManager) {
        this.screenManager = screenManager;
    }

    // API 호출
    public void apiSetOption(int playerNum, int unitNum, int shape, boolean isTest) {
        optionAPI.setOption(playerNum, unitNum, shape, isTest);
        gameAPI.setPlayManager(optionAPI.getPlayManager());
    }

    public void apiThrowYut(int designatedYutResult) {
        if (designatedYutResult == 0) gameAPI.throwYut();
        else gameAPI.throwYut(designatedYutResult);
    }

    public void throwYut(int designatedYutResult) {
        if (gameState.isTest() || gameState.getYutResults().isEmpty())
            apiThrowYut(designatedYutResult);

        updateGameStateWhenThrowingYut();
        debug();

        leftRepaint();
        topRepaint();
    }

    public void clickUnit(int playerNum, int unitNum) {
        if (gameState.getCurrentPlayer() == playerNum &&
                gameState.getCurrentPhase().contains(Phase.UNIT_CLICK)) {

            int yutCount = gameState.getYutResults().size();
            gameAPI.moveUnit(gameState.getClickedYutResult(), unitNum);

            gameState.setCountClickedButton(gameState.getCountClickedButton() - 1);
            gameState.getCurrentPhase().remove(Phase.UNIT_CLICK);

            if (turnChanged()) {
                gameState.setCurrentPhase(EnumSet.of(Phase.BUTTON_CLICK));
                gameState.setCountClickedButton(0);
            }

            setGameStateByBackWhenMoveUnit();

            if (gameState.getClickedYutResult() == -1 && yutCount == gameState.getYutResults().size()) {
                gameState.setCountClickedButton(gameState.getCountClickedButton() + 1);
            }

            gameState.setClickedYutResult(0);
            checkAndActivateYutRecordClick();
            checkAndActivateButtonClick();
            debug();

            if (gameState.isGameEnd())
                screenManager.end();

            moveUnitRepaint();
        }
    }

    public boolean turnChanged() {
        return gameState.getCurrentPlayer() != gameAPI.getCurrentPlayer();
    }

    private void checkAndActivateButtonClick() {
        if (gameState.getCountClickedButton() != 0 &&
                gameState.getCountClickedButton() == gameState.getYutResults().size()) {
            gameState.getCurrentPhase().remove(Phase.BUTTON_CLICK);
        } else {
            gameState.getCurrentPhase().add(Phase.BUTTON_CLICK);
        }
    }

    private void checkAndActivateYutRecordClick() {
        if (!gameState.getYutResults().isEmpty()) {
            gameState.getCurrentPhase().add(Phase.YUT_RECORD_CLICK);
        } else {
            gameState.getCurrentPhase().remove(Phase.YUT_RECORD_CLICK);
        }
    }

    private void activateUnitClick() {
        gameState.getCurrentPhase().add(Phase.UNIT_CLICK);
    }

    private void setGameStateByBackWhenThrowYut() {
        gameState.setYutResults(gameAPI.getYutResult());
        gameState.setEvent(gameAPI.getEvent());
        gameState.setCurrentPlayer(gameAPI.getCurrentPlayer());
    }

    private void setGameStateByBackWhenMoveUnit() {
        gameState.setYutResults(gameAPI.getYutResult());
        gameState.setEvent(gameAPI.getEvent());
        gameState.setCurrentPlayer(gameAPI.getCurrentPlayer());
        gameState.setUnitPosition(gameAPI.getUnitPositions());
        gameState.setUnitNumberPerPosition(gameAPI.getUnitNumberPerPosition());
        gameState.setGameEnd(gameAPI.gameEnd());
    }

    private void updateGameStateWhenThrowingYut() {
        setGameStateByBackWhenThrowYut();

        gameState.setCountClickedButton(gameState.getCountClickedButton() + 1);
        List<Integer> yutResults = gameState.getYutResults();

        if (!gameState.isTest()) {
            if (!yutResults.isEmpty()) {
                gameState.setLastResult(yutResults.get(gameState.getCountClickedButton() - 1));
            }
        } else {
            gameState.setLastResult(yutResults.get(yutResults.size() - 1));
        }

        checkAndActivateButtonClick();
        checkAndActivateYutRecordClick();
    }

    public void clickYut(int yutResult) {
        if (gameState.getCurrentPhase().contains(Phase.YUT_RECORD_CLICK)) {
            gameState.setClickedYutResult(yutResult);
            activateUnitClick();
        }
    }

    public void leftRepaint() {
        rootPane.getChildren().removeIf(node -> node instanceof LeftPanel);
        LeftPanel leftPanel = new LeftPanel(this);
        rootPane.getChildren().add(leftPanel);
    }

    public void rightRepaint() {
        rootPane.getChildren().removeIf(node -> node instanceof RightPanel);
        RightPanel rightPanel = new RightPanel(this);
        rootPane.getChildren().add(rightPanel);
    }

    private void topRepaint() {
        rootPane.getChildren().removeIf(node -> node instanceof TopPanel);
        TopPanel topPanel = new TopPanel(this);
        rootPane.getChildren().add(topPanel);
    }

    private void centerRepaint() {
        rootPane.getChildren().removeIf(node -> node instanceof MainBoard);
        MainBoard mainBoard = new MainBoard(this);
        rootPane.getChildren().add(mainBoard);
    }

    public void moveUnitRepaint() {
        leftRepaint();
        rightRepaint();
        topRepaint();
        centerRepaint();
    }

    // ** Getters and Setters **


    public void setRootPane(Pane pane) {
        this.rootPane = pane;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void debug() {
        System.out.println("Player: " + gameState.getCurrentPlayer());
        System.out.println("Yut: " + gameState.getYutResults());
        System.out.println("Event: " + gameState.getEvent());
        System.out.println("Turn: " + gameState.getCurrentPhase());
        System.out.println("Unit: " + Arrays.deepToString(gameState.getUnitPosition()));
        System.out.println("UnitNumberPerPosition: " + Arrays.deepToString(gameState.getUnitNumberPerPosition()));
    }
}
