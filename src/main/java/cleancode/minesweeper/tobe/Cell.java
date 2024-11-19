package cleancode.minesweeper.tobe;

public class Cell {

    private static final String FLAG_SIGN = "⚑";
    private static final String LAND_MINE_SIGN = "☼";
    private static final String OPENED_CELL_SIGN = "■";
    private static final String CLOSED_CELL_SIGN = "□";

    //불변데이터를 명시해주기위해 final을 붙여줌
    private final String sign;
    //Cell자체가 주변 Cell의 지뢰수와 지뢰여부를 가지고 있게 변경
    private int nearbyLandMineCount;
    private boolean isLandMine;

    //외부에서 직접 new Cell(...) 로 객체를 생성할수 없으며 클래스 내부에서만 호출이 가능하다.
    private Cell(String sign, int nearbyLandMineCount, boolean isLandMine) {
        this.sign = sign;
        this.nearbyLandMineCount = nearbyLandMineCount;
        this.isLandMine = isLandMine;
    }

    //정적 팩토리 메서드
    //메서드명 of는 흔히 특정 값을 기반으로 객체를 생성할 때 사용되는 명명 규칙
    //생성자를 private 로 선언하여 외부에서 직접 호출할 수 없게 하고, 정적 팩토리 메서드만을 통해 객체를 생성하게 강제한다.
    //생성자는 클래스 이름과 동일해야 하지만, 정적 팩토리 메서드는 이름을 자유롭게 설정할 수 있다.
    //이름을 통행 생성 목적을 명확히 표현할수 있다.
    //상세사항은 티스토리 참고
    public static Cell of(String sign, int nearbyLandMineCount, boolean isLandMine) {
        return new Cell(sign, nearbyLandMineCount, isLandMine);
    }

    public static Cell ofFlag() {
        return of(FLAG_SIGN);
    }

    public static Cell ofLandMine() {
        return of(LAND_MINE_SIGN);
    }

    public static Cell ofClosed(){
        return of(CLOSED_CELL_SIGN);
    }

    public static Cell ofOpened() {
        return of(OPENED_CELL_SIGN);
    }

    public static Cell ofNearbyLandMineCount(int count){
        return of(String.valueOf(count));
    }

    public String getSign(){
        return sign;
    }

    public boolean isClosed() {
        return CLOSED_CELL_SIGN.equals(this.sign);
    }

    public boolean doesNotClosed() {
        return !isClosed();
    }

}
