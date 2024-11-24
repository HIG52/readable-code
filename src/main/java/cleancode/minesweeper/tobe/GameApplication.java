package cleancode.minesweeper.tobe;

import cleancode.minesweeper.tobe.gamelevel.Advenced;
import cleancode.minesweeper.tobe.gamelevel.GameLevel;
import cleancode.minesweeper.tobe.gamelevel.Middle;

public class GameApplication {

    //단일 책임 원칙에 따라 main과 Minesweeper 를 생성하여 책임을 분리하여 진입점을 생성하는 역할만 해주게됨
    public static void main(String[] args){
        GameLevel gameLevel = new Advenced();

        Minesweeper minesweeper = new Minesweeper(gameLevel);
        minesweeper.run();

    }

}
