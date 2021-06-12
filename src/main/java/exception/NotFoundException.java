package exception;

public class NotFoundException extends ServiceRuntimeException {

  public NotFoundException(String message) {
    super(String.format("Not found. (%s)", message));
  }
}
