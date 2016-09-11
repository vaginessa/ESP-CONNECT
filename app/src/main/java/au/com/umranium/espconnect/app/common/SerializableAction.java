package au.com.umranium.espconnect.app.common;

import java.io.Serializable;

import au.com.umranium.espconnect.app.BaseActivity;
import au.com.umranium.espconnect.app.BaseController;

/**
 * An action that can be serialized and executed at some point in the future against a controller.
 */
public abstract class SerializableAction<Controller extends BaseController> implements Serializable {

  public void invoke(BaseActivity<Controller> activity) {
    Controller controller = activity.getController();
    run(controller);
  }

  public abstract void run(Controller controller);

}
