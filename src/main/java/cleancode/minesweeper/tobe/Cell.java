package cleancode.minesweeper.tobe;

public class Cell {
    //불변데이터를 명시해주기위해 final을 붙여줌
    //도메인의 근본 의미를 다시 생각하여 이름 변경
    private static final String FLAG_SIGN = "⚑";
    private static final String LAND_MINE_SIGN = "☼";
    //체크를 아직 하지 않은 sign
    private static final String UNCHECKED_SIGN = "■";
    //체크를 하였지만 비어있는 sign
    private static final String EMPTY_SIGN = "□";

    //Cell자체가 주변 Cell의 지뢰수와 지뢰여부를 가지고 있게 변경
    private int nearbyLandMineCount; //근처 지뢰 숫자
    private boolean isLandMine; //지뢰 여부
    private boolean isFlaged;
    private boolean isOpened;

    //Cell이 가진 속성 : 근처 지뢰 숫자, 지뢰여부
    //Cell의 상태 : 깃발 유무, 열렸다/닫혔다, 사용자가 확인함

    //외부에서 직접 new Cell(...) 로 객체를 생성할수 없으며 클래스 내부에서만 호출이 가능하다.
    private Cell(int nearbyLandMineCount, boolean isLandMine, boolean isFlaged, boolean isOpened) {
        this.nearbyLandMineCount = nearbyLandMineCount;
        this.isLandMine = isLandMine;
        this.isFlaged = isFlaged;
        this.isOpened = isOpened;
    }

    //정적 팩토리 메서드
    //메서드명 of는 흔히 특정 값을 기반으로 객체를 생성할 때 사용되는 명명 규칙
    //생성자를 private 로 선언하여 외부에서 직접 호출할 수 없게 하고, 정적 팩토리 메서드만을 통해 객체를 생성하게 강제한다.
    //생성자는 클래스 이름과 동일해야 하지만, 정적 팩토리 메서드는 이름을 자유롭게 설정할 수 있다.
    //이름을 통행 생성 목적을 명확히 표현할수 있다.
    //상세사항은 티스토리 참고
    public static Cell of(int nearbyLandMineCount, boolean isLandMine, boolean isFlaged, boolean isOpened) {
        return new Cell(nearbyLandMineCount, isLandMine, isFlaged, isOpened);
    }
    
    //맨 처음 빈cell을 만들어 코드에 할당
    public static Cell create(){
        return of(0, false, false, false);
    }

    public void turnOnLandMine() {
        this.isLandMine = true;
    }

    public void updateNearbyLandMineCount(int count) {
        this.nearbyLandMineCount = count;
    }

    public void flag() {
        this.isFlaged = true;
    }

    public void open() {
        this.isOpened = true;
    }

    public boolean isChecked() {
        return isFlaged || isOpened;
    }

    public boolean isLandMine() {
        return isLandMine;
    }

    public boolean isOpened() {
        return isOpened;
    }

    public boolean hasLandMineCount() {
        return this.nearbyLandMineCount != 0;
    }

    //board 그릴때 사용
    //getSign을 할때 지금 현재 cell에 맞는 sign은 뭔지 그려줘야 하는 역할
    public String getSign(){
        //열린Cell이라면
        if(isOpened){
            //지뢰라면
            if(isLandMine){
                //지뢰 텍스트 반환
                return LAND_MINE_SIGN;
            }
            //1이상의 주변 지뢰 숫자가 있다면
            if(hasLandMineCount()){
                //주변 지뢰 숫자 String으로 변환하여 반환
                return String.valueOf(nearbyLandMineCount);
            }
            //둘다 아닐경우
            return EMPTY_SIGN;
        }

        //깃발 꽂혀있는지
        if(isFlaged){
            return FLAG_SIGN;
        }

        //둘다 아닐때 비로소 체크되지 않은 Cell
        return UNCHECKED_SIGN;
    }
}
