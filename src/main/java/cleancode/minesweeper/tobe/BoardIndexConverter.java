package cleancode.minesweeper.tobe;

public class BoardIndexConverter {

    private static final char BASE_CHAR_FOR_COL = 'a';

    public int getSelectedColIndex(String cellInput, int colSize) {
        char cellInputCol = cellInput.charAt(0);
        return convertColFrom(cellInputCol, colSize);
    }

    //두자리수에대한 연산을 가능하게 확장
    public int getSelectedRowIndex(String cellInput, int rowSize) {
        String cellInputRow = cellInput.substring(1);
        return convertRowFrom(cellInputRow, rowSize);
    }

    private int convertRowFrom(String cellInputRow, int rowSize) { // "10"
        int rowIndex = Integer.parseInt(cellInputRow) - 1;
        if(rowIndex <0 || rowIndex >= rowSize){
            throw new GameException("잘못된 입력입니다.");
        }
        return rowIndex;
    }

    //아스키 코드에대한 연산으로 모든 알파벳에 대응 가능하게 수정
    private int convertColFrom(char cellInputCol, int colSize) { // 'a'
        int colIndex = cellInputCol - BASE_CHAR_FOR_COL;

        if(colIndex < 0 || colIndex >= colSize){
            throw new GameException("잘못된 입력입니다.");
        }

        return colIndex;
    }
}
