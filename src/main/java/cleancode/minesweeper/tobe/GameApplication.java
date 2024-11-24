package cleancode.minesweeper.tobe;

import cleancode.minesweeper.tobe.gamelevel.Beginer;
import cleancode.minesweeper.tobe.gamelevel.GameLevel;
import cleancode.minesweeper.tobe.io.ConsoleInputHandler;
import cleancode.minesweeper.tobe.io.ConsoleOutputHandler;
import cleancode.minesweeper.tobe.io.InputHandler;
import cleancode.minesweeper.tobe.io.OutputHandler;

public class GameApplication {

    //단일 책임 원칙에 따라 main과 Minesweeper 를 생성하여 책임을 분리하여 진입점을 생성하는 역할만 해주게됨
    public static void main(String[] args){
        GameLevel gameLevel = new Beginer();
        InputHandler inputHandler = new ConsoleInputHandler();
        OutputHandler outputHandler = new ConsoleOutputHandler();

        Minesweeper minesweeper = new Minesweeper(gameLevel, inputHandler, outputHandler);
        minesweeper.initialize();
        minesweeper.run();

    }

    /*
    * DIP (Dependency Inversion Principle)
    * 고수준 저수준 모델이 추상화에 의존
    *
    * DI (Dependency Injection) = "3" // 제3자가 두객체간 의존성을 주입시켜준다 런타임시점에
    * 의존성 주입(필요한 의존성을 직접생성이 아니라 주입받겠다)
    *
    * IoC (Inversion of Control)
    * 제어의 역전
    * 프로그램의 흐름을 개발자가 아닌 프레임워크에게 위임
    *
    * */

}
