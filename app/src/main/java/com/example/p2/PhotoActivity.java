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
import android.widget.TextView;

public class PhotoActivity extends AppCompatActivity {

    private ImageView myImage;
    private Button pictureButton, complexCameraButton;
    private TextView mainText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        // Binds myImage variable to the ImageView with the Id "MyImage"
        myImage = (ImageView)findViewById(R.id.MyImage);

        // Binds pictureButton variable to the Button with the Id "Button"
        pictureButton = (Button)findViewById(R.id.take_picture);

        // Binds complexCameraButton variable to the Button with the Id "Button"
        complexCameraButton = (Button)findViewById(R.id.video_camera);

        // Creates a on click listener that calls our take picture function when ever our button is clicked
        pictureButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                /* Checks to see if permission is granted to the application to use the camera. If not
                 * the user is prompted to allow permission
                 */
                if (ContextCompat.checkSelfPermission(PhotoActivity.this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted - Calls function to take a picture
                    takePicture();
                } else {
                    /* Permission not granted - Ask user for permission again. If they deny it
                     * indefinitely, nothing will appear
                     */
                    ActivityCompat.requestPermissions(PhotoActivity.this, new String[] {Manifest.permission.CAMERA}, 1);
                }
            }
        });

        // Creates a on click listener that goes to a new activity that shows our own personal camera
        complexCameraButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent nextAcitvity = new Intent(getBaseContext(), VideoActivity.class);
                startActivity(nextAcitvity);
            }
        });
    }

    // Function creates a intent(API call) that opens the camera and resolves when we take a picture
    private void takePicture(){
        Intent picture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(picture, 0);

    }

    /* When the intent is resolved it takes the data from the intent(in this case, the picture)
     * and sets it to our image view
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            Bitmap pic = (Bitmap)data.getExtras().get("data");
            myImage.setImageBitmap(pic);

            // Sets the text of the main text of the application to let the user know that a picture was taken
            mainText = findViewById(R.id.mainText);
            mainText.setText("Picture has been taken. View below!");
        }


    }
}
