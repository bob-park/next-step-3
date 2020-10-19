package exception.http;

public class InvalidRequestLineException extends RuntimeException {

    public InvalidRequestLineException(String requestLine) {
        super(String.format("Invalid Request-Line. [%s]", requestLine));
    }
}
