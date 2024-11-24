package cleancode.minesweeper.tobe;

//커스텀 예외처리
public class GameException extends RuntimeException{

    public GameException(String message){
        super(message);
    }

}
