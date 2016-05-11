package au.com.umranium.nodemcuwifi.api;

import java.io.IOException;

/**
 * A required field in the data was not provided.
 *
 * @author umran
 */
public class RequiredFieldNotProvided extends IOException {

  private final String mFieldName;
  private final String mMessageName;

  public RequiredFieldNotProvided(String fieldName, String messageName) {
    mFieldName = fieldName;
    mMessageName = messageName;
  }

  public RequiredFieldNotProvided(Throwable cause, String fieldName, String messageName) {
    super(cause);
    mFieldName = fieldName;
    mMessageName = messageName;
  }

  public String getFieldName() {
    return mFieldName;
  }

  public String getMessageName() {
    return mMessageName;
  }
}
