package com.b44t.messenger;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import org.thoughtcrime.securesms.R;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.util.Log;

public class ActivityPDFViewer extends AppCompatActivity {
  private static final String TAG = ActivityPDFViewer.class.getSimpleName();

  public static String prfFilePath = "";
  public static boolean blFlagAllowDownload = false;
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
  public void finish() {
    if(file != null) {
      file.delete();
      Log.d(TAG, "File destroyed after viewing");
    }
    super.finish();
  }
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    if (blFlagAllowDownload) {
      getMenuInflater().inflate(R.menu.file_download_menu, menu);
    }
    return true;
  }
  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item)
  {
    int id = item.getItemId();
    if (id == R.id.menu_download) {
      fileDownload(file);
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void fileDownload(File file)
  {
    saveFileToDownloads(this,file,file.getName());
  }

  private void copyStream(InputStream in, OutputStream out) throws IOException {
    byte[] buffer = new byte[8192];
    int length;
    while ((length = in.read(buffer)) != -1) {
      out.write(buffer, 0, length);
    }
    out.flush();
  }
  public void saveFileToDownloads(Context context, File sourceFile, String outputFileName) {
    try {
      InputStream in = new FileInputStream(sourceFile);
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        // Android 10 and above
        ContentValues values = new ContentValues();
        values.put(MediaStore.Downloads.DISPLAY_NAME, outputFileName);
        values.put(MediaStore.Downloads.MIME_TYPE, "application/octet-stream");
        values.put(MediaStore.Downloads.IS_PENDING, 1);

        ContentResolver resolver = context.getContentResolver();
        Uri collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        Uri fileUri = resolver.insert(collection, values);

        try (OutputStream out = resolver.openOutputStream(fileUri)) {
          copyStream(in, out);
        }

        values.clear();
        values.put(MediaStore.Downloads.IS_PENDING, 0);
        resolver.update(fileUri, values, null, null);
      } else {
        // Android 9 and below
        File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File outFile = new File(downloadDir, outputFileName);
        try (OutputStream out = new FileOutputStream(outFile)) {
          copyStream(in, out);
        }
      }
      Toast.makeText(this,file.getName()+" has been saved in Download folder",Toast.LENGTH_LONG).show();
      in.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
