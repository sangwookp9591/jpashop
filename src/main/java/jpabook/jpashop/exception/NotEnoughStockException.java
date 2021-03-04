package jpabook.jpashop.exception;

public class NotEnoughStockException extends RuntimeException{
    //여기서 overide를 했는데 왜해야 하냐면 메세지 같은것을 다넘겨 주고
    // 메세지와 예외가 발생한 근원적 이셉션을 넣어 처리가 되게 하기 위해서
    public NotEnoughStockException() {
        super();
    }

    public NotEnoughStockException(String message) {
        super(message);
    }

    public NotEnoughStockException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotEnoughStockException(Throwable cause) {
        super(cause);
    }

}
