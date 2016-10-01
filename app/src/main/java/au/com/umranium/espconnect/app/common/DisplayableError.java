package au.com.umranium.espconnect.app.common;

public class DisplayableError extends RuntimeException {

  public DisplayableError(String detailMessage) {
    super(detailMessage);
  }

  public DisplayableError(String detailMessage, Throwable throwable) {
    super(detailMessage, throwable);
  }

}
