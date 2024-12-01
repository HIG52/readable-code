package cleancode.minesweeper.tobe;

import cleancode.minesweeper.tobe.game.GameInitializable;
import cleancode.minesweeper.tobe.game.GameRunnable;
import cleancode.minesweeper.tobe.gamelevel.GameLevel;
import cleancode.minesweeper.tobe.io.InputHandler;
import cleancode.minesweeper.tobe.io.OutputHandler;
import cleancode.minesweeper.tobe.position.CellPosition;
import cleancode.minesweeper.tobe.user.UserAction;

public class Minesweeper implements GameInitializable, GameRunnable {

    //객체로 추상화 및 캡슐화
    private final GameBoard gameBoard;
    //입력에 대한 책임, 출력에 대한 책임을 나누어준다.
    private final InputHandler inputHandler;
    private final OutputHandler outputHandler;
    private int gameStatus = 0; // 0: 게임 중, 1: 승리, -1: 패배

    public Minesweeper(GameLevel gameLevel, InputHandler inputHandler, OutputHandler outputHandler) {
        gameBoard = new GameBoard(gameLevel);
        this.inputHandler = inputHandler;
        this.outputHandler = outputHandler;
    }

    @Override
    public void initialize() {
        gameBoard.initializeGame();
    }

    public void run() {

        //추상화 레벨 맞춰주기 + 알기쉬운 이름짓기
        outputHandler.showGameStartComments();

        gameBoard.initializeGame();

        while (true) {
            try {
                //게임판 그리기
                outputHandler.showBoard(gameBoard);

                //현재 게임의 상태를 확인 : gameStatus = 1
                if (doesUserWinTheGame()) {
                    outputHandler.showGameWinningComment();
                    break;
                }
                //현재 게임의 상태를 확인 : gameStatus = -1
                if (doesUserLoseTheGame()) {
                    outputHandler.showGameLosingComment();
                    break;
                }

                //좌표 입력
                CellPosition cellPosition = getCellInputFromUser();
                //셀에 대한 행위 입력 : 오픈(1), 깃발 꽂기(2)
                UserAction userAction = getUserActionInputFromUser();

                //좌표와 행위 입력시 해당 Cell에 대한 계산을 진행
                actOnCell(cellPosition, userAction);
            }catch (GameException e){
                //의도한 예외처리
                outputHandler.showExceptionMessage(e);
            }catch (Exception e){
                //예상하지 못한 예외처리
                outputHandler.showSimpleMessage("프로그램에 문제가 생겼습니다.");
            }

        }
    }

    private void actOnCell(CellPosition cellPosition, UserAction userAction) {

        //깃발 꽂기일때
        if (doesUserChooseToPlantFlag(userAction)) {
            //sign을 갈아끼우는 형태에서 요청하는 형태로 변경
            gameBoard.flagAt(cellPosition);
            checkIfGameOver();
            return;

        }

        //오픈일때
        if (doesUserChooseToOpenCell(userAction)) {
            if (gameBoard.isLandMineCellAt(cellPosition)) {
                //BOARD[selectedRowIndex][selectedColIndex] = Cell.ofLandMine(); 이미 지뢰를 켜둔 cell이라 없어도 된다
                gameBoard.openAt(cellPosition);
                changeGameStatusToLose();
                return;
            }
            gameBoard.openSurroundedCells(cellPosition);
            checkIfGameOver();
            return;
        }

        throw new GameException("잘못된 번호를 선택하셨습니다.");
    }

    private void changeGameStatusToLose() {
        gameStatus = -1;
    }

    private boolean doesUserChooseToOpenCell(UserAction userAction) {
        return userAction == UserAction.OPEN;
    }

    private boolean doesUserChooseToPlantFlag(UserAction userAction) {
        return userAction == UserAction.FLAG;
    }

    private UserAction getUserActionInputFromUser() {
        outputHandler.showCommentForUserAction();
        return inputHandler.getUserActionFromUser();
    }

    private CellPosition getCellInputFromUser() {
        outputHandler.showCommentForSelectingCell();
        CellPosition cellPosition = inputHandler.getCellPositionFromUser();
        if(gameBoard.isInvalidCellPosition(cellPosition)){
            throw new GameException("잘못된 좌표를 선택하셨습니다.");
        }
        return cellPosition;
    }

    private boolean doesUserLoseTheGame() {
        return gameStatus == -1;
    }

    private boolean doesUserWinTheGame() {
        return gameStatus == 1;
    }

    private void checkIfGameOver() {
        if (gameBoard.isAllCellChecked()) {
            changeGameStatusToWin();
        }
    }

    private void changeGameStatusToWin() {
        gameStatus = 1;
    }


}
