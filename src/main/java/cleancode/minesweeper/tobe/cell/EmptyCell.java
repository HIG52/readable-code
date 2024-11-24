package cleancode.minesweeper.tobe.cell;

public class EmptyCell extends Cell {

    //체크를 하였지만 비어있는 sign
    private static final String EMPTY_SIGN = "■";


    @Override
    public String getSign() {
        if(isOpened){
            return EMPTY_SIGN;
        }
        if(isFlaged){
            return FLAG_SIGN;
        }
        return UNCHECKED_SIGN;
    }

    @Override
    public boolean isLandMine() {
        return false;
    }

    @Override
    public boolean hasLandMineCount() {
        return false;
    }
}
