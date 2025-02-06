package com.b44t.messenger;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import org.thoughtcrime.securesms.R;
import org.thoughtcrime.securesms.customui.ZoomableImageView;
import me.relex.photodraweeview.PhotoDraweeView;

import java.io.File;

public class ActivityImageViewer extends AppCompatActivity
{
    public static String prfFilePath = "";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

      System.out.println("===path=>>"+prfFilePath);
//      File imgFile = new  File(this.getBaseContext().getFilesDir(), "abc.png");
      File file = new File(prfFilePath);
      if (!file.exists())
      {
        Toast.makeText(this, "PDF file not found!", Toast.LENGTH_SHORT).show();
        return;
      }
      if(file.exists())
      {
        PhotoDraweeView mPhotoDraweeView = findViewById(R.id.photo_drawee_view);
        mPhotoDraweeView.setPhotoUri(Uri.fromFile(file));
      };
    }
}
