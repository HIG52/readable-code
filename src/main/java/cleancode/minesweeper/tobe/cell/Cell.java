package cleancode.minesweeper.tobe.cell;

//abstract를 넣어서 추상클래스로 변경
//자식클래스에서 구현해서 쓰겠다라는 의미
public interface Cell {
    //불변데이터를 명시해주기위해 final을 붙여줌
    //도메인의 근본 의미를 다시 생각하여 이름 변경
    static final String FLAG_SIGN = "⚑";
    //체크를 아직 하지 않은 sign
    static final String UNCHECKED_SIGN = "□";


    //board 그릴때 사용
    //getSign을 할때 지금 현재 cell에 맞는 sign은 뭔지 그려줘야 하는 역할
    String getSign();

    boolean isLandMine();

    boolean hasLandMineCount();

    //공통기능
    
    void flag();

    void open();

    boolean isChecked();

    boolean isOpened();
}
