package exception.http;

public class InvalidRequestUriFormatException extends RuntimeException {

  public InvalidRequestUriFormatException(String requestUri) {
    super(String.format("Invalid Request-URI. [%s]", requestUri));
  }
}
