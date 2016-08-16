package au.com.umranium.espconnect.app.taskscreens.scanning.loc;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog.Builder;

import au.com.umranium.espconnect.R;
import permissions.dispatcher.PermissionRequest;

/**
 * Dialog that shows the rationale behind asking for the course location permission.
 *
 * @author umran
 */
public class CourseLocationRationaleDialogFragment extends DialogFragment {

  private PermissionRequest mRequest;

  public CourseLocationRationaleDialogFragment() {
    setRetainInstance(true);
  }

  public void setRequest(PermissionRequest request) {
    mRequest = request;
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    return new Builder(getContext())
        .setTitle(R.string.dialog_course_location_rationale_title)
        .setMessage(R.string.dialog_course_location_rationale_message)
        .setPositiveButton(android.R.string.yes, new OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            mRequest.proceed();
          }
        })
        .setNegativeButton(android.R.string.no, new OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            mRequest.cancel();
          }
        })
        .create();
  }
}

