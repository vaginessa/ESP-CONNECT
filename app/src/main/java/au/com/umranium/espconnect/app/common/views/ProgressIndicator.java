package au.com.umranium.espconnect.app.common.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import au.com.umranium.espconnect.R;

public class ProgressIndicator extends LinearLayout {

  private static final int[] DOTS = new int[]{
      R.id.progress_indicator_dot_1,
      R.id.progress_indicator_dot_2,
      R.id.progress_indicator_dot_3,
      R.id.progress_indicator_dot_4,
      R.id.progress_indicator_dot_5,
      R.id.progress_indicator_dot_6,
      R.id.progress_indicator_dot_7,
  };

  private static final int[] LINES = new int[]{
      R.id.progress_indicator_line_1,
      R.id.progress_indicator_line_2,
      R.id.progress_indicator_line_3,
      R.id.progress_indicator_line_4,
      R.id.progress_indicator_line_5,
      R.id.progress_indicator_line_6,
  };

  private int currentStep = 0;

  public ProgressIndicator(Context context) {
    super(context);
    init(context);
  }

  public ProgressIndicator(Context context, AttributeSet attrs) {
    super(context, attrs);
    extractProperties(context, attrs);
    init(context);
  }

  @TargetApi(11)
  public ProgressIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    extractProperties(context, attrs);
    init(context);
  }

  @TargetApi(21)
  public ProgressIndicator(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    extractProperties(context, attrs);
    init(context);
  }

  private void init(Context context) {
    inflate(context, R.layout.layout_progress_indicator, this);
    updateState();
  }

  private void updateState() {
    for (int i = 0; i < DOTS.length; i++) {
      ImageView view = (ImageView) findViewById(DOTS[i]);
      if (i == currentStep) {
        view.setImageResource(R.drawable.dot_current);
      } else if (i < currentStep ) {
        view.setImageResource(R.drawable.dot_filled);
      } else {
        view.setImageResource(R.drawable.dot_empty);
      }
    }
    for (int i = 0; i < LINES.length; i++) {
      ImageView view = (ImageView) findViewById(LINES[i]);
      if (i < currentStep) {
        view.setImageResource(R.drawable.progress_line_bright);
      } else {
        view.setImageResource(R.drawable.progress_line_dull);
      }
    }
  }

  private void extractProperties(Context context, AttributeSet attrs) {
    TypedArray a = context.getTheme().obtainStyledAttributes(
        attrs,
        R.styleable.ProgressIndicator,
        0, 0);

    try {
      currentStep = a.getInt(R.styleable.ProgressIndicator_currentStep, 0);
    } finally {
      a.recycle();
    }
  }

  public int getCurrentStep() {
    return currentStep;
  }

  public void setCurrentStep(int mCurrentStep) {
    this.currentStep = mCurrentStep;
    updateState();
  }
}
