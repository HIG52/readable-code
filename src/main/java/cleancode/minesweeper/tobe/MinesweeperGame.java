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
            //sign을 갈아끼우는 형태에서 요청하는 형태로 변경
            BOARD[selectedRowIndex][selectedColIndex].flag();
            checkIfGameOver();
            return;

        }

        //오픈일때
        if (doesUserChooseToOpenCell(userActionInput)) {
            if (isLandMineCell(selectedRowIndex, selectedColIndex)) {
                //BOARD[selectedRowIndex][selectedColIndex] = Cell.ofLandMine(); 이미 지뢰를 켜둔 cell이라 없어도 된다
                BOARD[selectedRowIndex][selectedColIndex].open();
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
        return BOARD[selectedRowIndex][selectedColIndex].isLandMine();
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
        boolean isAllOpened = isAllCellChecked();
        if (isAllOpened) {
            changeGameStatusToWin();
        }
    }

    private static void changeGameStatusToWin() {
        gameStatus = 1;
    }

    //승리 조건은 사실 어떤 cell 이 열려있거나 닫여있지만 깃발로 확인했다 이기에 메서드명 자체가 부적절함
    //개인 의견으로는 깃발이 꽂혀있을때 지뢰가 있어야만 확인해야하는게 아닌가 싶음
    private static boolean isAllCellChecked() {
        //이중 for문 해소
        return Arrays.stream(BOARD)
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

    //보드판을 형성
    private static void initializeGame() {
        // i j를 의미에 맞게 row 와 col으로 변경
        //보드를 생성하는 과정
        for (int row = 0; row < BOARD_ROW_SIZE; row++) {
            for (int col = 0; col < BOARD_COL_SIZE; col++) {
                //BOARD 초기화
                BOARD[row][col] = Cell.create();
            }
        }
        //의미를 갖지 않는 i값이기 때문에 그대로 냅둠
        //지뢰를 설치하는 과정
        for (int i = 0; i < LAND_MINE_COUNT; i++) {
            int col = new Random().nextInt(BOARD_COL_SIZE);
            int row = new Random().nextInt(BOARD_ROW_SIZE);
            BOARD[row][col].turnOnLandMine();
        }
        // i j를 의미에 맞게 row 와 col으로 변경
        for (int row = 0; row < BOARD_ROW_SIZE; row++) {
            for (int col = 0; col < BOARD_COL_SIZE; col++) {
                if (isLandMineCell(row, col)) { //지뢰Cell인경우
                    //NEARBY_LAND_MINE_COUNTS[row][col] = 0;
                    //BOARD[row][col] = Cell.create(); 를 해줄때 0으로 세팅하기에 필요없는 코드가 됨
                    continue;
                }
                //지뢰가 아닐때
                int count = countNearbyLandMines(row, col); //주변 지뢰의 개수를 가져옴
                BOARD[row][col].updateNearbyLandMineCount(count);
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
        //이미 열려있으면 넘어가라
        if (BOARD[row][col].isOpened()) {
            return;
        }
        if (isLandMineCell(row, col)) {
            return;
        }
        //이줄까지 온다면 아직 열리지 않은cell이니 open을 해주는것
        //아래 if-else문에서 꺼내올수 있게되었다
        //cell을 열고 값의 따라 return해줄지 재귀를 돌지 정해지기 때문
        BOARD[row][col].open();

        //열고난 후 숫자cell이면 멈춘다
        if (BOARD[row][col].hasLandMineCount()) {
            //기존에 숫자가 있으면 숫자값을 꺼내서 BOARD에 할당해주었음
            //그 얘기는 열고 동시에 숫자를 표기해준것이다.
            //BOARD[row][col] = Cell.ofNearbyLandMineCount(NEARBY_LAND_MINE_COUNTS[row][col]);
            return;
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
