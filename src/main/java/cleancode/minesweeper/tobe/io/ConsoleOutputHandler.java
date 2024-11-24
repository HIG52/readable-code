package cleancode.minesweeper.tobe.io;

import cleancode.minesweeper.tobe.GameBoard;
import cleancode.minesweeper.tobe.GameException;

import java.util.List;
import java.util.stream.IntStream;

public class ConsoleOutputHandler implements OutputHandler {

    @Override
    public void showGameStartComment() {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println("지뢰찾기 게임 시작!");
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
    }

    @Override
    public void showBoard(GameBoard board) {
        String alphabats = generateColAlphabats(board);

        System.out.println("   " + alphabats);

        for (int row = 0; row < board.getRowSize(); row++) {
            System.out.printf("%2d  ", row + 1);
            for (int col = 0; col < board.getColSize(); col++) {
                //getter를 사용해야하는 부분
                //Cell 내부에서 그려달라고 하는게 더 이상한 부분
                System.out.print(board.getSign(row, col) + " ");
            }
            System.out.println();
        }

        System.out.println();

    }

    private String generateColAlphabats(GameBoard board) {
        List<String> alphabats = IntStream.range(0, board.getColSize())
                        .mapToObj(index -> (char) ('a'+index))
                                .map(Object::toString)
                                        .toList();
        return String.join(" ", alphabats);
    }

    @Override
    public void showGameWinningComent() {
        System.out.println("지뢰를 모두 찾았습니다. GAME CLEAR!");
    }

    @Override
    public void showGameLosingConment() {
        System.out.println("지뢰를 밟았습니다. GAME OVER!");
    }

    @Override
    public void showCommentForSelectingCell() {
        System.out.println("선택할 좌표를 입력하세요. (예: a1)");
    }

    @Override
    public void showCommentForUserAction() {
        System.out.println("선택한 셀에 대한 행위를 선택하세요. (1: 오픈, 2: 깃발 꽂기)");
    }

    @Override
    public void showExceptionMessage(GameException e) {
        System.out.println(e.getMessage());
    }

    @Override
    public void showSimpleMessage(String message) {
        System.out.println(message);
    }
}
