package cleancode.minesweeper.tobe.io;

import java.util.Scanner;

public class ConsoleInputHandler {
    //스캐너는 입력부분에서만 쓰이기에 Minesweeper에서 input으로 옮겨옴
    public static final Scanner SCANNER = new Scanner(System.in);

    public String getUserInput() {
        return SCANNER.nextLine();
    }
}
