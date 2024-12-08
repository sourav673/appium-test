package com.b44t.messenger;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.Settings;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import org.thoughtcrime.securesms.BaseActionBarActivity;
import org.thoughtcrime.securesms.R;

import java.io.File;
import java.io.IOException;

public class ActivityPDFViewer extends BaseActionBarActivity
{
  private ImageView image_view = null;
  public static String prfFilePath = "";

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_pdf_viewer);
    image_view  = findViewById(R.id.image_view);
    renderPdfFromDownloads(prfFilePath);
  }

  public void renderPdfFromDownloads(String sFilePath)
  {
    try
    {
      //File file = new File(getFilesDir(), "abc.pdf");
      File file = new File(sFilePath);
      System.out.println("-------------File absoulute path = "+file.getAbsolutePath());
      System.out.println("-------------File Size = "+file.length());
      ParcelFileDescriptor fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
      System.out.println("-------------File descriptor = "+fileDescriptor.getStatSize());
      PdfRenderer pdfRenderer = new PdfRenderer(fileDescriptor);
      System.out.println("-------------File pdfrender = "+pdfRenderer.getPageCount());
      PdfRenderer.Page page = pdfRenderer.openPage(0);
      Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
      page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
      image_view.setImageBitmap(bitmap);
    }catch (Exception e)
    {
      System.out.println("-------------Error = "+e);
    }

  }
}
