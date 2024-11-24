package cleancode.minesweeper.tobe.cell;

//abstract를 넣어서 추상클래스로 변경
//자식클래스에서 구현해서 쓰겠다라는 의미
public abstract class Cell {
    //불변데이터를 명시해주기위해 final을 붙여줌
    //도메인의 근본 의미를 다시 생각하여 이름 변경
    protected static final String FLAG_SIGN = "⚑";
    //체크를 아직 하지 않은 sign
    protected static final String UNCHECKED_SIGN = "□";

    //Cell자체가 주변 Cell의 지뢰수와 지뢰여부를 가지고 있게 변경

    protected boolean isFlaged;
    protected boolean isOpened;

    //board 그릴때 사용
    //getSign을 할때 지금 현재 cell에 맞는 sign은 뭔지 그려줘야 하는 역할
    public abstract String getSign();

    public abstract boolean isLandMine();

    public abstract boolean hasLandMineCount();

    //공통기능
    
    public void flag() {
        this.isFlaged = true;
    }

    public void open() {
        this.isOpened = true;
    }

    public boolean isChecked() {
        return isFlaged || isOpened;
    }

    public boolean isOpened() {
        return isOpened;
    }
}
