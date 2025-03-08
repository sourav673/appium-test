package org.thoughtcrime.securesms;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.pdf.PdfRenderer;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PdfPageAdapter extends RecyclerView.Adapter<PdfPageAdapter.PdfPageViewHolder> {

  private final PdfRenderer pdfRenderer;

  public PdfPageAdapter(PdfRenderer pdfRenderer) {
    this.pdfRenderer = pdfRenderer;
  }

  @NonNull
  @Override
  public PdfPageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pdf_page_item, parent, false);
    return new PdfPageViewHolder(view, parent.getContext());
  }

  @Override
  public void onBindViewHolder(@NonNull PdfPageViewHolder holder, int position) {
    PdfRenderer.Page page = pdfRenderer.openPage(position);

    Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

    holder.bind(bitmap, position, pdfRenderer.getPageCount());
    page.close();
  }

  @Override
  public int getItemCount() {
    return pdfRenderer.getPageCount();
  }

  public static class PdfPageViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener {
    private final ImageView pageImageView;
    private final TextView pageNumberTextView;
    private final ScaleGestureDetector scaleGestureDetector;
    private final GestureDetector gestureDetector;

    private final Matrix matrix = new Matrix();
    private float scaleFactor = 1.0f;
    private float translateX = 0, translateY = 0;

    public PdfPageViewHolder(@NonNull View itemView, Context context) {
      super(itemView);
      pageImageView = itemView.findViewById(R.id.page_image);
      pageImageView.setScaleType(ImageView.ScaleType.MATRIX);
      pageNumberTextView = itemView.findViewById(R.id.page_number_text);

      scaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
          scaleFactor *= detector.getScaleFactor();
          scaleFactor = Math.max(1.0f, Math.min(scaleFactor, 5.0f)); // Limit zoom range
          updateMatrix();
          return true;
        }
      });

      gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
          if (scaleFactor > 1.0f) { // Only move if zoomed in
            translateX -= distanceX;
            translateY -= distanceY;
            updateMatrix();
          }
          return true;
        }
      });

      pageImageView.setOnTouchListener(this);
    }

    private void updateMatrix() {
      matrix.reset();
      matrix.setScale(scaleFactor, scaleFactor);
      matrix.postTranslate(translateX, translateY); // Apply translation
      pageImageView.setImageMatrix(matrix);
    }

    public void bind(Bitmap bitmap, int position, int totalPages) {
      pageImageView.setImageBitmap(bitmap);
      pageNumberTextView.setText("Page " + (position + 1) + "/" + totalPages);
      matrix.reset();

      // Calculate the scale factor based on the available space and the image's aspect ratio
      float scale = calculateFitScale(bitmap);
      matrix.setScale(scale, scale);
      pageImageView.setImageMatrix(matrix);
    }

    private float calculateFitScale(Bitmap bitmap) {
      float imageViewWidth = pageImageView.getWidth();
      float imageViewHeight = pageImageView.getHeight();
      float bitmapWidth = bitmap.getWidth();
      float bitmapHeight = bitmap.getHeight();

      float scaleX = imageViewWidth / bitmapWidth;
      float scaleY = imageViewHeight / bitmapHeight;

      return Math.max(scaleX, scaleY);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
      boolean pinchHandled = scaleGestureDetector.onTouchEvent(event);
      boolean dragHandled = gestureDetector.onTouchEvent(event);

      return pinchHandled || dragHandled;
    }
  }
}
