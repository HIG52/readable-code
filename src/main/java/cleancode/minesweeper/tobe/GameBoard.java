package cleancode.minesweeper.tobe;

import java.util.Arrays;
import java.util.Random;

public class GameBoard {

    //Cell 객체를 생성하여 리팩토링
    private final Cell[][] board;

    private static final int LAND_MINE_COUNT = 10;

    public GameBoard(int rowSize, int colSize){
        board = new Cell[rowSize][colSize];
    }

    public void flag(int selectedRowIndex, int selectedColIndex) {
        Cell cell = findCell(selectedRowIndex, selectedColIndex);
        cell.flag();
    }

    public void open(int selectedRowIndex, int selectedColIndex) {
        Cell cell = findCell(selectedRowIndex, selectedColIndex);
        cell.open();
    }

    public boolean isLandMineCell(int selectedRowIndex, int selectedColIndex) {
        Cell cell = findCell(selectedRowIndex, selectedColIndex);
        return cell.isLandMine();
    }

    public void openSurroundedCells(int row, int col) {
        //빠른 리턴으로 뇌가 좀더 효율적으로 읽을수 있게 해줌
        if (row < 0 || row >= getRowSize() || col < 0 || col >= getColSize()) {
            return;
        }
        //사고를 뒤집어야 하기 때문에 !대신 신규 메서드를 생성
        //변경전 : if (BOARD[row][col].doesNotEqualsSign(CLOSED_CELL_SIGN)) {
        //상수를 Cell내부로 이동후 변경
        //변경후
        //이미 열려있으면 넘어가라
        if (isOpenedCell(row, col)) {
            return;
        }
        if (isLandMineCell(row, col)) {
            return;
        }
        //이줄까지 온다면 아직 열리지 않은cell이니 open을 해주는것
        //아래 if-else문에서 꺼내올수 있게되었다
        //cell을 열고 값의 따라 return해줄지 재귀를 돌지 정해지기 때문
        open(row, col);

        //열고난 후 숫자cell이면 멈춘다
        if (doesCellHaveLandMineCount(row, col)) {
            //기존에 숫자가 있으면 숫자값을 꺼내서 BOARD에 할당해주었음
            //그 얘기는 열고 동시에 숫자를 표기해준것이다.
            //BOARD[row][col] = Cell.ofNearbyLandMineCount(NEARBY_LAND_MINE_COUNTS[row][col]);
            return;
        }

        openSurroundedCells(row - 1, col - 1);
        openSurroundedCells(row - 1, col);
        openSurroundedCells(row - 1, col + 1);
        openSurroundedCells(row, col - 1);
        openSurroundedCells(row, col + 1);
        openSurroundedCells(row + 1, col - 1);
        openSurroundedCells(row + 1, col);
        openSurroundedCells(row + 1, col + 1);
    }

    private boolean doesCellHaveLandMineCount(int row, int col) {
        return findCell(row, col).hasLandMineCount();
    }

    private boolean isOpenedCell(int row, int col) {
        return findCell(row, col).isOpened();
    }

    //보드판을 형성
    public void initializeGame() {
        int rowSize = board.length;
        int colSize = board[0].length;

        // i j를 의미에 맞게 row 와 col으로 변경
        //보드를 생성하는 과정
        for (int row = 0; row < rowSize; row++) {
            for (int col = 0; col < colSize; col++) {
                //BOARD 초기화
                board[row][col] = Cell.create();
            }
        }
        //의미를 갖지 않는 i값이기 때문에 그대로 냅둠
        //지뢰를 설치하는 과정
        for (int i = 0; i < LAND_MINE_COUNT; i++) {
            int landMineCol = new Random().nextInt(colSize);
            int landMineRow = new Random().nextInt(rowSize);
            Cell landMineCell = findCell(landMineRow, landMineCol);
            landMineCell.turnOnLandMine();
        }
        // i j를 의미에 맞게 row 와 col으로 변경
        for (int row = 0; row < rowSize; row++) {
            for (int col = 0; col < colSize; col++) {
                if (isLandMineCell(row, col)) { //지뢰Cell인경우
                    //NEARBY_LAND_MINE_COUNTS[row][col] = 0;
                    //BOARD[row][col] = Cell.create(); 를 해줄때 0으로 세팅하기에 필요없는 코드가 됨
                    continue;
                }
                //지뢰가 아닐때
                int count = countNearbyLandMines(row, col); //주변 지뢰의 개수를 가져옴
                Cell cell = findCell(row, col);
                cell.updateNearbyLandMineCount(count);
            }
        }
    }

    public int getRowSize() {
        return board.length;
    }

    public int getColSize() {
        return board[0].length;
    }

    //승리 조건은 사실 어떤 cell 이 열려있거나 닫여있지만 깃발로 확인했다 이기에 메서드명 자체가 부적절함
    //개인 의견으로는 깃발이 꽂혀있을때 지뢰가 있어야만 확인해야하는게 아닌가 싶음
    public boolean isAllCellChecked() {
        //이중 for문 해소
        return Arrays.stream(board)
                .flatMap(Arrays::stream)
                //getter를 사용하는 대신 공개메서드를 사용
                //.noneMatch(Cell::isClosed);
                .allMatch(Cell::isChecked);
        /*
            1. Arrays.stream(BOARD)
                BOARD라는 2차원 배열을 Arrays.stream() 메서드를 사용하여 배열의 각 행(1차원 배열)을 스트림으로 변환

            2. flatMap(Arrays::stream)
                a. flatMap()은 각 요소를 처리하면서 내부의 스트림을 평탄화 한다
                예) String[][] BOARD = {
                        {"A", "B", "C"},
                        {"D", "E", "F"},
                        {"G", "H", "I"}
                    };
                    배열이 있을때 Arrays.stream(BOARD)를 실행하면

                    Stream<String[]> // 각 행 {"A", "B", "C"}, {"D", "E", "F"}, {"G", "H", "I"}

                    이런식으로 만들어지게 되고 이걸 한번더 flatMap을 하게 되면

                    Stream<String> // "A", "B", "C", "D", "E", "F", "G", "H", "I"

                    1차원 스트림으로 변환하고 하나의 스트림으로 병합한다.

                    물건이 여러 칸에 나누어 담겨 있는 서랍을 하나로 합치는 것으로 비유할 수 있다.
                b. Arrays::stream은 각 1차원 배열(행)을 다시 스트림으로 변환한다.
                c. flatMap의 결과로 전체 2차원 배열이 1차원 스트림으로 변환하게 된다.
            3. noneMatch(CLOSED_CELL_SIGN::equals)
                a. noneMatch는 스트림의 모든 요소에 대해 조건을 검사하여, *조건을 만족하는 요소가 하나도 없을경우* true를 반환한다.
                b. CLOSED_CELL_SIGN::equals 조건을 사용하고 있으며,
                    각 셀이 CLOSED_CELL_SIGN과 같은지 확인 하고 같은 값이 하나도 없으면 true, 그렇지 않으면 false를 반환한다
         */
    }

    public String getSign(int rowIndex, int colIndex){
        Cell cell = findCell(rowIndex, colIndex);
        return cell.getSign();
    }

    private Cell findCell(int rowIndex, int colIndex) {
        return board[rowIndex][colIndex];
    }

    private int countNearbyLandMines(int row, int col) {
        int rowSize = getRowSize();
        int colSize = getColSize();
        int count = 0;

        if (row - 1 >= 0 && col - 1 >= 0 && isLandMineCell(row - 1, col - 1)) {
            count++;
        }
        if (row - 1 >= 0 && isLandMineCell(row - 1, col)) {
            count++;
        }
        if (row - 1 >= 0 && col + 1 < colSize && isLandMineCell(row - 1, col + 1)) {
            count++;
        }
        if (col - 1 >= 0 && isLandMineCell(row, col - 1)) {
            count++;
        }
        if (col + 1 < colSize && isLandMineCell(row, col + 1)) {
            count++;
        }
        if (row + 1 < rowSize && col - 1 >= 0 && isLandMineCell(row + 1, col - 1)) {
            count++;
        }
        if (row + 1 < rowSize && isLandMineCell(row + 1, col)) {
            count++;
        }
        if (row + 1 < rowSize && col + 1 < colSize && isLandMineCell(row + 1, col + 1)) {
            count++;
        }
        return count;
    }
}
