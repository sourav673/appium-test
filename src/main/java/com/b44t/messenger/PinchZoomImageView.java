package com.b44t.messenger;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import androidx.appcompat.widget.AppCompatImageView;

public class PinchZoomImageView extends AppCompatImageView {
  private Matrix matrix = new Matrix();
  private float[] matrixValues = new float[9];

  private ScaleGestureDetector scaleGestureDetector;
  private float scale = 1f, minScale = 1f, maxScale = 4f;

  private PointF lastTouch = new PointF();
  private int mode = 0; // 0 = none, 1 = drag, 2 = zoom

  private float viewWidth, viewHeight;
  private float imageWidth, imageHeight;

  public PinchZoomImageView(Context context) {
    super(context);
    init(context);
  }

  public PinchZoomImageView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context);
  }

  public PinchZoomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context);
  }

  private void init(Context context) {
    scaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
    setScaleType(ScaleType.MATRIX);
    setOnTouchListener(touchListener);
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    viewWidth = w;
    viewHeight = h;
    fitImageToWidth(); // Make sure the image fills the width initially
  }

  private void fitImageToWidth() {
    Drawable drawable = getDrawable();
    if (drawable == null) return;

    imageWidth = drawable.getIntrinsicWidth();
    imageHeight = drawable.getIntrinsicHeight();

    if (imageWidth <= 0 || imageHeight <= 0 || viewWidth <= 0) return;

    // Scale the image to match the full width of the screen
    scale = viewWidth / imageWidth;

    // Calculate the new height based on the aspect ratio
    float newHeight = imageHeight * scale;

    // Center the image vertically
    float offsetY = (viewHeight - newHeight) / 2;

    matrix.setScale(scale, scale);
    matrix.postTranslate(0, offsetY);
    setImageMatrix(matrix);
  }

  private View.OnTouchListener touchListener = new View.OnTouchListener() {
    @Override
    public boolean onTouch(View v, MotionEvent event) {
      scaleGestureDetector.onTouchEvent(event);

      switch (event.getActionMasked()) {
        case MotionEvent.ACTION_DOWN:
          lastTouch.set(event.getX(), event.getY());
          mode = 1; // Drag mode
          break;

        case MotionEvent.ACTION_MOVE:
          if (mode == 1) { // Dragging
            float dx = event.getX() - lastTouch.x;
            float dy = event.getY() - lastTouch.y;
            matrix.postTranslate(dx, dy);
            lastTouch.set(event.getX(), event.getY());
          }
          break;

        case MotionEvent.ACTION_POINTER_DOWN:
          mode = 2; // Zoom mode
          break;

        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_POINTER_UP:
          mode = 0;
          break;
      }

      setImageMatrix(matrix);
      fixTranslation();
      return true;
    }
  };

  private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
    @Override
    public boolean onScale(ScaleGestureDetector detector) {
      float scaleFactor = detector.getScaleFactor();
      float newScale = scale * scaleFactor;

      // Limit zooming
      if (newScale < minScale) {
        scaleFactor = minScale / scale;
      } else if (newScale > maxScale) {
        scaleFactor = maxScale / scale;
      }

      // Scale around fingers (focal point)
      matrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
      scale *= scaleFactor;

      fixTranslation();
      return true;
    }
  }

  private void fixTranslation() {
    matrix.getValues(matrixValues);
    float transX = matrixValues[Matrix.MTRANS_X];
    float transY = matrixValues[Matrix.MTRANS_Y];
    float currentScale = matrixValues[Matrix.MSCALE_X];

    float maxTransX = viewWidth * (currentScale - 1);
    float maxTransY = viewHeight * (currentScale - 1);

    transX = Math.min(0, Math.max(transX, -maxTransX));
    transY = Math.min(0, Math.max(transY, -maxTransY));

    matrixValues[Matrix.MTRANS_X] = transX;
    matrixValues[Matrix.MTRANS_Y] = transY;

    matrix.setValues(matrixValues);
    setImageMatrix(matrix);
  }
}
