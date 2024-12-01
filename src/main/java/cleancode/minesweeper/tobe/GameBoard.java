package cleancode.minesweeper.tobe;

import cleancode.minesweeper.tobe.cell.*;
import cleancode.minesweeper.tobe.gamelevel.GameLevel;
import cleancode.minesweeper.tobe.position.CellPosition;
import cleancode.minesweeper.tobe.position.CellPositions;
import cleancode.minesweeper.tobe.position.RelativePosition;

import java.util.List;

public class GameBoard {

    //Cell 객체를 생성하여 리팩토링
    private final Cell[][] board;
    private final int landMineCount;

    public GameBoard(GameLevel gameLevel){
        int colSize = gameLevel.getColSize();
        int rowSize = gameLevel.getRowSize();

        board = new Cell[rowSize][colSize];

        landMineCount = gameLevel.getLandMineCount();
    }

    public void flagAt(CellPosition cellPosition) {
        Cell cell = findCell(cellPosition);
        cell.flag();
    }

    public void openAt(CellPosition cellPosition) {
        Cell cell = findCell(cellPosition);
        cell.open();
    }

    public boolean isLandMineCellAt(CellPosition cellPosition) {
        Cell cell = findCell(cellPosition);
        return cell.isLandMine();
    }

    public void openSurroundedCells(CellPosition cellPosition) {

        /*if (cellPosition.isRowIndexMoreThanOrEqual(getRowSize())
                || cellPosition.isColIndexMoreThanOrEqual(getColSize())) {
            return;
        }*/
        //사고를 뒤집어야 하기 때문에 !대신 신규 메서드를 생성
        //변경전 : if (BOARD[row][col].doesNotEqualsSign(CLOSED_CELL_SIGN)) {
        //상수를 Cell내부로 이동후 변경
        //변경후
        //이미 열려있으면 넘어가라
        if (isOpenedCell(cellPosition)) {
            return;
        }
        if (isLandMineCellAt(cellPosition)) {
            return;
        }
        //이줄까지 온다면 아직 열리지 않은cell이니 open을 해주는것
        //아래 if-else문에서 꺼내올수 있게되었다
        //cell을 열고 값의 따라 return해줄지 재귀를 돌지 정해지기 때문
        openAt(cellPosition);

        //열고난 후 숫자cell이면 멈춘다
        if (doesCellHaveLandMineCount(cellPosition)) {
            //기존에 숫자가 있으면 숫자값을 꺼내서 BOARD에 할당해주었음
            //그 얘기는 열고 동시에 숫자를 표기해준것이다.
            //BOARD[row][col] = Cell.ofNearbyLandMineCount(NEARBY_LAND_MINE_COUNTS[row][col]);
            return;
        }

        List<CellPosition> surroundedPositions = calculateSurroundedPositions(cellPosition, getRowSize(), getColSize());
        surroundedPositions.forEach(this::openSurroundedCells);

        /*for(RelativePosition relativePosition : RelativePosition.SURROUNDED_POSITIONS) {
            if(canMovePosition(cellPosition, relativePosition)) {
                CellPosition nextCellPosition = cellPosition.calculatePositionBy(relativePosition);
                openSurroundedCells(nextCellPosition);
            }
        }*/

    }

    private static boolean canMovePosition(CellPosition cellPosition, RelativePosition relativePosition) {
        return cellPosition.canCalculatePositionBy(relativePosition);
    }

    private boolean doesCellHaveLandMineCount(CellPosition cellPosition) {
        Cell cell = findCell(cellPosition);
        return cell.hasLandMineCount();
    }

    private boolean isOpenedCell(CellPosition cellPosition) {
        Cell cell = findCell(cellPosition);
        return cell.isOpened();
    }

    public boolean isInvalidCellPosition(CellPosition cellPosition) {
        int rowSize = getRowSize();
        int colSize = getColSize();

        return cellPosition.isRowIndexMoreThanOrEqual(rowSize) || cellPosition.isColIndexMoreThanOrEqual(colSize);
    }

    //보드판을 형성
    public void initializeGame() {
        CellPositions cellPositions = CellPositions.from(board);

        // i j를 의미에 맞게 row 와 col으로 변경
        //보드를 생성하는 과정
        initializeEmptyCells(cellPositions);

        //의미를 갖지 않는 i값이기 때문에 그대로 냅둠
        //지뢰를 설치하는 과정
        List<CellPosition> landMinePositions = cellPositions.extractRandomPositions(landMineCount);
        initializeLandMineCells(landMinePositions);

        // i j를 의미에 맞게 row 와 col으로 변경
        List<CellPosition> numberPositionCandidates = cellPositions.subtract(landMinePositions);
        initializeNumberCells(numberPositionCandidates);

    }

    private void initializeEmptyCells(CellPositions cellPositions) {
        List<CellPosition> allPositions = cellPositions.getPositions();
        for (CellPosition position : allPositions) {
            updateCellAt(position, new EmptyCell());
        }
    }

    private void initializeLandMineCells(List<CellPosition> landMinePositions) {
        for (CellPosition position : landMinePositions) {
            updateCellAt(position, new LandMineCell());
        }
    }

    private void initializeNumberCells(List<CellPosition> numberPositionCandidates) {
        for (CellPosition candidatePosition : numberPositionCandidates) {
            int count = countNearbyLandMines(candidatePosition);
            if (count != 0) {
                updateCellAt(candidatePosition, new NumberCell(count));
            }
        }
    }

    private void updateCellAt(CellPosition position, Cell cell) {
        board[position.getRowIndex()][position.getColIndex()] = cell;
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
        Cells cells = Cells.from(board);
        return cells.isAllChecked();
    }

    public String getSign(CellPosition cellPosition){
        Cell cell = findCell(cellPosition);
        return cell.getSign();
    }

    private Cell findCell(CellPosition cellPosition) {
        return board[cellPosition.getRowIndex()][cellPosition.getColIndex()];
    }

    private int countNearbyLandMines(CellPosition cellPosition) {
        int rowSize = getRowSize();
        int colSize = getColSize();

        long count = calculateSurroundedPositions(cellPosition, rowSize, colSize).stream()
                .filter(this::isLandMineCellAt)
                .count();


        return (int) count;
    }

    private List<CellPosition> calculateSurroundedPositions(CellPosition cellPosition, int rowSize, int colSize) {
        return RelativePosition.SURROUNDED_POSITIONS.stream()
                .filter(cellPosition::canCalculatePositionBy)
                .map(cellPosition::calculatePositionBy)
                .filter(position -> position.isRowIndexLessThan(rowSize))
                .filter(position -> position.isColIndexLessThan(colSize))
                .toList();
    }
}
