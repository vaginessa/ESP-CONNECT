package au.com.umranium.espconnect.analytics;

import com.google.android.gms.analytics.ExceptionParser;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Ensures that the full stacktrace is passed to Google Analytics as opposed to the default behaviour of just the first line.
 */
public class FullExceptionParser implements ExceptionParser {
  @Override
  public String getDescription(String s, Throwable throwable) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw, true);
    pw.print("Thread: " + s + ", Exception: ");
    throwable.printStackTrace(pw);
    return sw.getBuffer().toString();
  }
}
