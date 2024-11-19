package cleancode.minesweeper.tobe;

//커스텀 예외처리
public class AppException extends RuntimeException{

    public AppException(String message){
        super(message);
    }

}
