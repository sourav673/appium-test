package com.b44t.messenger;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import org.thoughtcrime.securesms.R;
import java.io.File;
import java.io.IOException;
import android.util.Log;

public class ActivityPDFViewer extends AppCompatActivity {
  private static final String TAG = ActivityPDFViewer.class.getSimpleName();

  public static String prfFilePath = "";
  private PdfRenderer pdfRenderer;
  private ParcelFileDescriptor fileDescriptor;
  private PinchZoomImageView ivPDF;
  private Button btNext, btPrev;
  private TextView tvPageNumber;
  private int currentPageIndex = 0;
  private int totalPages = 0;
  private File file = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_pdf_viewer);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

    ivPDF = findViewById(R.id.ivPDF);
    btNext = findViewById(R.id.btNext);
    btPrev = findViewById(R.id.btPrev);
    tvPageNumber = findViewById(R.id.tvPageNumber);

    if (!openPdfFile()) {
      Toast.makeText(this, "Failed to open PDF", Toast.LENGTH_SHORT).show();
      finish();
      return;
    }

    totalPages = pdfRenderer.getPageCount();
    showPage(currentPageIndex);

    btNext.setOnClickListener(v -> nextPage());
    btPrev.setOnClickListener(v -> prevPage());
  }

  private boolean openPdfFile() {
    try {
      file = new File(prfFilePath);
      if (!file.exists()) {
        return false;
      }
      fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
      pdfRenderer = new PdfRenderer(fileDescriptor);
      return true;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }

  private void showPage(int index) {
    if (pdfRenderer == null || index < 0 || index >= totalPages) return;

    PdfRenderer.Page page = pdfRenderer.openPage(index);
    Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
    ivPDF.setImageBitmap(bitmap);
    page.close();

    tvPageNumber.setText(String.format("Page %d / %d", index + 1, totalPages));
  }

  private void nextPage() {
    if (currentPageIndex < totalPages - 1) {
      currentPageIndex++;
      showPage(currentPageIndex);
    }
  }

  private void prevPage() {
    if (currentPageIndex > 0) {
      currentPageIndex--;
      showPage(currentPageIndex);
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    try {
      if (pdfRenderer != null) pdfRenderer.close();
      if (fileDescriptor != null) fileDescriptor.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void finish()
  {
    if(file != null) {
      file.delete();
      Log.d(TAG, "File destroyed after viewing");
    }
    super.finish();
  }
}
