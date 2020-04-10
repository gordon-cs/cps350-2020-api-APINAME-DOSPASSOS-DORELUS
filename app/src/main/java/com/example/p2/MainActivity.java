package com.example.p2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private ImageView myImage;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //binds myImage variable to the ImageView with the Id "MyImage"
        myImage = (ImageView)findViewById(R.id.MyImage);

        //binds button variable to the Button with the Id "Button"
        button = (Button)findViewById(R.id.Button);

        //creates a on click listener that calls our take picture function when ever our button is clicked
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                // Checks to see if permission is granted to the application to use the camera. If not
                // the user is prompted to allow permission
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_CALENDAR)
                        == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    takePicture();
                } else {
                    // Permission not granted
                    ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.CAMERA}, 1);
                }
            }
        });
    }
    //function creates a intent(API call) that opens the camera and resolves when we take a picture
    private void takePicture(){
        Intent picture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(picture, 0);
    }

    // when the intent is resolved it takes the data from the intent(in this case, the picture)
    //and sets it to our image view
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            Bitmap pic = (Bitmap)data.getExtras().get("data");
            myImage.setImageBitmap(pic);
        }
    }
}
