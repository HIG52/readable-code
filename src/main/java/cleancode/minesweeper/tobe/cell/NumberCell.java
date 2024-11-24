package cleancode.minesweeper.tobe.cell;

public class NumberCell extends Cell {

    private final int nearbyLandMineCount; //근처 지뢰 숫자

    public NumberCell(int nearbyLandMineCount){
        this.nearbyLandMineCount = nearbyLandMineCount;
    }

    @Override
    public String getSign() {
        if(isOpened){
            return String.valueOf(nearbyLandMineCount);
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
        return true;
    }
}
