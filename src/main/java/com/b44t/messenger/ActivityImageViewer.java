package com.b44t.messenger;

import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import org.thoughtcrime.securesms.R;
import me.relex.photodraweeview.PhotoDraweeView;
import java.io.File;
import android.util.Log;

public class ActivityImageViewer extends AppCompatActivity
{
    private static final String TAG = ActivityImageViewer.class.getSimpleName();
    public static String prfFilePath = "";
    private File file = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_image_viewer);
      getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

      file = new File(prfFilePath);
      if (!file.exists()) {
          Toast.makeText(this, "Image file not found!", Toast.LENGTH_SHORT).show();
      }
      else {
        PhotoDraweeView mPhotoDraweeView = findViewById(R.id.photo_drawee_view);
        mPhotoDraweeView.setPhotoUri(Uri.fromFile(file));
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
}
