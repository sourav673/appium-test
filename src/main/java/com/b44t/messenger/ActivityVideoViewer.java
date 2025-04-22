package com.b44t.messenger;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.thoughtcrime.securesms.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.util.Log;

public class ActivityVideoViewer extends AppCompatActivity
{
    private static final String TAG = ActivityVideoViewer.class.getSimpleName();
    public static String prfFilePath = "";
    public static boolean blFlagAllowDownload = false;
    private File file = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_video_viewer);
      getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
      VideoView videoView = findViewById(R.id.videoView);

      file = new File(prfFilePath);
      if (!file.exists()) {
        Toast.makeText(this, "Video file not found!", Toast.LENGTH_SHORT).show();
        return;
      }
      if(file.exists()) {
          videoView.setVideoURI(Uri.parse(file.getAbsolutePath()));
          videoView.setMediaController(new MediaController(this));
          videoView.start();
      };
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
  public boolean onCreateOptionsMenu(Menu menu)
  {
    if(blFlagAllowDownload)
    {
      getMenuInflater().inflate(R.menu.file_download_menu, menu);
    }
    return true;
  }
  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item)
  {
    int id = item.getItemId();
    if (id == R.id.menu_download)
    {
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
  public void saveFileToDownloads(Context context, File sourceFile, String outputFileName)
  {
    try
    {
      InputStream in = new FileInputStream(sourceFile);
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
      {
        // Android 10 and above
        ContentValues values = new ContentValues();
        values.put(MediaStore.Downloads.DISPLAY_NAME, outputFileName);
        values.put(MediaStore.Downloads.MIME_TYPE, "application/octet-stream");
        values.put(MediaStore.Downloads.IS_PENDING, 1);

        ContentResolver resolver = context.getContentResolver();
        Uri collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        Uri fileUri = resolver.insert(collection, values);

        try (OutputStream out = resolver.openOutputStream(fileUri))
        {
          copyStream(in, out);
        }

        values.clear();
        values.put(MediaStore.Downloads.IS_PENDING, 0);
        resolver.update(fileUri, values, null, null);

      }
      else
      {
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

