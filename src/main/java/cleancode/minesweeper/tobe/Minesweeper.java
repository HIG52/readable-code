package cleancode.minesweeper.tobe;

import cleancode.minesweeper.tobe.game.GameInitializable;
import cleancode.minesweeper.tobe.game.GameRunnable;
import cleancode.minesweeper.tobe.gamelevel.GameLevel;
import cleancode.minesweeper.tobe.io.InputHandler;
import cleancode.minesweeper.tobe.io.OutputHandler;

public class Minesweeper implements GameInitializable, GameRunnable {

    //객체로 추상화 및 캡슐화
    private final GameBoard gameBoard;
    //인덱스 변환에 대한 책임분리로 신규 객체를 생성
    private final BoardIndexConverter boardIndexConverter = new BoardIndexConverter();
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
        outputHandler.showGameStartComment();

        gameBoard.initializeGame();

        while (true) {
            try {
                //게임판 그리기
                outputHandler.showBoard(gameBoard);

                //현재 게임의 상태를 확인 : gameStatus = 1
                if (doesUserWinTheGame()) {
                    outputHandler.showGameWinningComent();
                    break;
                }
                //현재 게임의 상태를 확인 : gameStatus = -1
                if (doesUserLoseTheGame()) {
                    outputHandler.showGameLosingConment();
                    break;
                }

                //좌표 입력
                String cellInput = getCellInputFromUser();
                //셀에 대한 행위 입력 : 오픈(1), 깃발 꽂기(2)
                String userActionInput = getUserActionInputFromUser();

                //좌표와 행위 입력시 해당 Cell에 대한 계산을 진행
                actOnCell(cellInput, userActionInput);
            }catch (GameException e){
                //의도한 예외처리
                outputHandler.showExceptionMessage(e);
            }catch (Exception e){
                //예상하지 못한 예외처리
                outputHandler.showSimpleMessage("프로그램에 문제가 생겼습니다.");
            }

        }
    }

    private void actOnCell(String cellInput, String userActionInput) {
        //좌표 입력
        //X좌표 a~j
        int selectedColIndex = boardIndexConverter.getSelectedColIndex(cellInput, gameBoard.getColSize());
        //Y좌표 1~10
        int selectedRowIndex = boardIndexConverter.getSelectedRowIndex(cellInput, gameBoard.getRowSize());

        //깃발 꽂기일때
        if (doesUserChooseToPlantFlag(userActionInput)) {
            //sign을 갈아끼우는 형태에서 요청하는 형태로 변경
            gameBoard.flag(selectedRowIndex, selectedColIndex);
            checkIfGameOver();
            return;

        }

        //오픈일때
        if (doesUserChooseToOpenCell(userActionInput)) {
            if (gameBoard.isLandMineCell(selectedRowIndex, selectedColIndex)) {
                //BOARD[selectedRowIndex][selectedColIndex] = Cell.ofLandMine(); 이미 지뢰를 켜둔 cell이라 없어도 된다
                gameBoard.open(selectedRowIndex, selectedColIndex);
                changeGameStatusToLose();
                return;
            }
            gameBoard.openSurroundedCells(selectedRowIndex, selectedColIndex);
            checkIfGameOver();
            return;
        }

        throw new GameException("잘못된 번호를 선택하셨습니다.");
    }

    private void changeGameStatusToLose() {
        gameStatus = -1;
    }

    private boolean doesUserChooseToOpenCell(String userActionInput) {
        return userActionInput.equals("1");
    }

    private boolean doesUserChooseToPlantFlag(String userActionInput) {
        return userActionInput.equals("2");
    }

    private String getUserActionInputFromUser() {
        outputHandler.showCommentForUserAction();
        return inputHandler.getUserInput();
    }

    private String getCellInputFromUser() {
        outputHandler.showCommentForSelectingCell();
        return inputHandler.getUserInput();
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
