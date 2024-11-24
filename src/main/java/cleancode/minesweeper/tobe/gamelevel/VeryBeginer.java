package cleancode.minesweeper.tobe.gamelevel;

//게임레벨의 스펙을 만족시키는 구현체
public class VeryBeginer implements GameLevel{

    @Override
    public int getRowSize() {
        return 4;
    }

    @Override
    public int getColSize() {
        return 5;
    }

    @Override
    public int getLandMineCount() {
        return 2;
    }
}
