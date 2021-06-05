package exception.http.method;

import exception.ServiceRuntimeException;

public class NotSupportHttpMethodException extends ServiceRuntimeException {

  public NotSupportHttpMethodException(String method) {
    super(String.format("Not support http method. (%s)", method));
  }
}
