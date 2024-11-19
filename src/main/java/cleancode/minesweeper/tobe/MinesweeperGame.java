package cleancode.minesweeper.tobe;

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class MinesweeperGame {

    //매직넘버, 매직스트링 : 상수추출로 이름을 짓고 의미를 부여함
    public static final int BOARD_ROW_SIZE = 8;
    public static final int BOARD_COL_SIZE = 10;
    public static final Scanner SCANNER = new Scanner(System.in);
    //Cell 객체를 생성하여 리팩토링
    private static final Cell[][] BOARD = new Cell[BOARD_ROW_SIZE][BOARD_COL_SIZE];
    private static final Integer[][] NEARBY_LAND_MINE_COUNTS = new Integer[BOARD_ROW_SIZE][BOARD_COL_SIZE];
    private static final boolean[][] LAND_MINES = new boolean[BOARD_ROW_SIZE][BOARD_COL_SIZE];
    public static final int LAND_MINE_COUNT = 10;

    private static int gameStatus = 0; // 0: 게임 중, 1: 승리, -1: 패배

    public static void main(String[] args) {

        //추상화 레벨 맞춰주기 + 알기쉬운 이름짓기
        showGameStartComment();

        initializeGame();

        while (true) {
            try {
                //게임판 그리기
                showBoard();
                
                //현재 게임의 상태를 확인 : gameStatus = 1
                if (doesUserWinTheGame()) {
                    System.out.println("지뢰를 모두 찾았습니다. GAME CLEAR!");
                    break;
                }
                //현재 게임의 상태를 확인 : gameStatus = -1
                if (doesUserLoseTheGame()) {
                    System.out.println("지뢰를 밟았습니다. GAME OVER!");
                    break;
                }

                System.out.println();

                //좌표 입력
                String cellInput = getCellInputFromUser();
                //셀에 대한 행위 입력 : 오픈(1), 깃발 꽂기(2)
                String userActionInput = getUserActionInputFromUser();

                //좌표와 행위 입력시 해당 Cell에 대한 계산을 진행
                actOnCell(cellInput, userActionInput);
            }catch (AppException e){
                //의도한 예외처리
                System.out.println(e.getMessage());
            }catch (Exception e){
                //예상하지 못한 예외처리
                System.out.println("프로그램에 문제가 생겼습니다.");
                e.printStackTrace();
            }

        }
    }

    private static void actOnCell(String cellInput, String userActionInput) {
        //좌표 입력
        //X좌표 a~j
        int selectedColIndex = getSelectedColIndex(cellInput);
        //Y좌표 1~10
        int selectedRowIndex = getSelectedRowIndex(cellInput);

        //깃발 꽂기일때
        if (doesUserChooseToPlantFlag(userActionInput)) {
            BOARD[selectedRowIndex][selectedColIndex] = Cell.ofFlag();
            checkIfGameOver();
            return;

        }

        //오픈일때
        if (doesUserChooseToOpenCell(userActionInput)) {
            if (isLandMineCell(selectedRowIndex, selectedColIndex)) {
                BOARD[selectedRowIndex][selectedColIndex] = Cell.ofLandMine();
                changeGameStatusToLose();
                return;
            }
            open(selectedRowIndex, selectedColIndex);
            checkIfGameOver();
            return;
        }

        throw new AppException("잘못된 번호를 선택하셨습니다.");
    }

    private static void changeGameStatusToLose() {
        gameStatus = -1;
    }

    private static boolean isLandMineCell(int selectedRowIndex, int selectedColIndex) {
        return LAND_MINES[selectedRowIndex][selectedColIndex];
    }

    private static boolean doesUserChooseToOpenCell(String userActionInput) {
        return userActionInput.equals("1");
    }

    private static boolean doesUserChooseToPlantFlag(String userActionInput) {
        return userActionInput.equals("2");
    }

    private static int getSelectedRowIndex(String cellInput) {
        char cellInputRow = cellInput.charAt(1);
        return convertRowFrom(cellInputRow);
    }

    private static int getSelectedColIndex(String cellInput) {
        char cellInputCol = cellInput.charAt(0);
        return convertColFrom(cellInputCol);
    }

    private static String getUserActionInputFromUser() {
        System.out.println("선택한 셀에 대한 행위를 선택하세요. (1: 오픈, 2: 깃발 꽂기)");
        return SCANNER.nextLine();
    }

    private static String getCellInputFromUser() {
        System.out.println("선택할 좌표를 입력하세요. (예: a1)");
        return SCANNER.nextLine();
    }

    private static boolean doesUserLoseTheGame() {
        return gameStatus == -1;
    }

    private static boolean doesUserWinTheGame() {
        return gameStatus == 1;
    }

    private static void checkIfGameOver() {
        boolean isAllOpened = isAllCellOpened();
        if (isAllOpened) {
            changeGameStatusToWin();
        }
    }

    private static void changeGameStatusToWin() {
        gameStatus = 1;
    }

    private static boolean isAllCellOpened() {
        //이중 for문 해소
        return Arrays.stream(BOARD)
                .flatMap(Arrays::stream)
                //getter를 사용하는 대신 공개메서드를 사용
                .noneMatch(Cell::isClosed);
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

    private static int convertRowFrom(char cellInputRow) {
        int rowIndex = Character.getNumericValue(cellInputRow) - 1;
        if(rowIndex > BOARD_ROW_SIZE){
            throw new AppException("잘못된 입력입니다.");
        }
        return rowIndex;
    }

    private static int convertColFrom(char cellInputCol) {
        return switch (cellInputCol) {
            case 'a' -> 0;
            case 'b' -> 1;
            case 'c' -> 2;
            case 'd' -> 3;
            case 'e' -> 4;
            case 'f' -> 5;
            case 'g' -> 6;
            case 'h' -> 7;
            case 'i' -> 8;
            case 'j' -> 9;
            default -> throw new AppException("잘못된 입력입니다.");
        };
    }

    private static void showBoard() {
        System.out.println("   a b c d e f g h i j");
        for (int i = 0; i < BOARD_ROW_SIZE; i++) {
            System.out.printf("%d  ", i + 1);
            for (int j = 0; j < BOARD_COL_SIZE; j++) {
                //getter를 사용해야하는 부분
                //Cell 내부에서 그려달라고 하는게 더 이상한 부분
                System.out.print(BOARD[i][j].getSign() + " ");
            }
            System.out.println();
        }
    }

    private static void initializeGame() {
        // i j를 의미에 맞게 row 와 col으로 변경
        for (int row = 0; row < BOARD_ROW_SIZE; row++) {
            for (int col = 0; col < BOARD_COL_SIZE; col++) {
                BOARD[row][col] = Cell.ofClosed();
            }
        }
        //의미를 갖지 않는 i값이기 때문에 그대로 냅둠
        for (int i = 0; i < LAND_MINE_COUNT; i++) {
            int col = new Random().nextInt(BOARD_COL_SIZE);
            int row = new Random().nextInt(BOARD_ROW_SIZE);
            LAND_MINES[row][col] = true;
        }
        // i j를 의미에 맞게 row 와 col으로 변경
        for (int row = 0; row < BOARD_ROW_SIZE; row++) {
            for (int col = 0; col < BOARD_COL_SIZE; col++) {
                if (isLandMineCell(row, col)) { //지뢰가 아닌칸
                    NEARBY_LAND_MINE_COUNTS[row][col] = 0;
                    continue;
                }
                int count = countNearbyLandMines(row, col);
                NEARBY_LAND_MINE_COUNTS[row][col] = count;
            }
        }
    }

    private static int countNearbyLandMines(int row, int col) {
        int count = 0;
        if (row - 1 >= 0 && col - 1 >= 0 && isLandMineCell(row - 1, col - 1)) {
            count++;
        }
        if (row - 1 >= 0 && isLandMineCell(row - 1, col)) {
            count++;
        }
        if (row - 1 >= 0 && col + 1 < BOARD_COL_SIZE && isLandMineCell(row - 1, col + 1)) {
            count++;
        }
        if (col - 1 >= 0 && isLandMineCell(row, col - 1)) {
            count++;
        }
        if (col + 1 < BOARD_COL_SIZE && isLandMineCell(row, col + 1)) {
            count++;
        }
        if (row + 1 < BOARD_ROW_SIZE && col - 1 >= 0 && isLandMineCell(row + 1, col - 1)) {
            count++;
        }
        if (row + 1 < BOARD_ROW_SIZE && isLandMineCell(row + 1, col)) {
            count++;
        }
        if (row + 1 < BOARD_ROW_SIZE && col + 1 < BOARD_COL_SIZE && isLandMineCell(row + 1, col + 1)) {
            count++;
        }
        return count;
    }

    private static void showGameStartComment() {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println("지뢰찾기 게임 시작!");
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
    }

    private static void open(int row, int col) {
        //빠른 리턴으로 뇌가 좀더 효율적으로 읽을수 있게 해줌
        if (row < 0 || row >= BOARD_ROW_SIZE || col < 0 || col >= BOARD_COL_SIZE) {
            return;
        }
        //사고를 뒤집어야 하기 때문에 !대신 신규 메서드를 생성
        //변경전 : if (BOARD[row][col].doesNotEqualsSign(CLOSED_CELL_SIGN)) {
        //상수를 Cell내부로 이동후 변경
        //변경후
        if (BOARD[row][col].doesNotClosed()) {
            return;
        }
        if (isLandMineCell(row, col)) {
            return;
        }
        if (NEARBY_LAND_MINE_COUNTS[row][col] != 0) {
            //String.valueOf(NEARBY_LAND_MINE_COUNTS[row][col])
            //형태가 아닌 숫자만 받을수 있도록
            BOARD[row][col] = Cell.ofNearbyLandMineCount(NEARBY_LAND_MINE_COUNTS[row][col]);
            return;
        } else {
            BOARD[row][col] = Cell.ofOpened();
        }

        open(row - 1, col - 1);
        open(row - 1, col);
        open(row - 1, col + 1);
        open(row, col - 1);
        open(row, col + 1);
        open(row + 1, col - 1);
        open(row + 1, col);
        open(row + 1, col + 1);
    }

}
