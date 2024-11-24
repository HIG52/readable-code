package cleancode.minesweeper.tobe.io;

import cleancode.minesweeper.tobe.GameBoard;
import cleancode.minesweeper.tobe.GameException;

public class ConsoleOutputHandler {

    public void showGameStartComment() {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println("지뢰찾기 게임 시작!");
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
    }

    public void showBoard(GameBoard board) {

        System.out.println("   a b c d e f g h i j");
        for (int row = 0; row < board.getRowSize(); row++) {
            System.out.printf("%d  ", row + 1);
            for (int col = 0; col < board.getColSize(); col++) {
                //getter를 사용해야하는 부분
                //Cell 내부에서 그려달라고 하는게 더 이상한 부분
                System.out.print(board.getSign(row, col) + " ");
            }
            System.out.println();
        }

        System.out.println();

    }

    public void printGameWinningComent() {
        System.out.println("지뢰를 모두 찾았습니다. GAME CLEAR!");
    }

    public void printGameLosingConment() {
        System.out.println("지뢰를 밟았습니다. GAME OVER!");
    }

    public void printCommentForSelectingCell() {
        System.out.println("선택할 좌표를 입력하세요. (예: a1)");
    }

    public void printCommentForUserAction() {
        System.out.println("선택한 셀에 대한 행위를 선택하세요. (1: 오픈, 2: 깃발 꽂기)");
    }

    public void printExceptionMessage(GameException e) {
        System.out.println(e.getMessage());
    }

    public void printSimpleMessage(String message) {
        System.out.println(message);
    }
}
