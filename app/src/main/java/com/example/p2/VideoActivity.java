package com.example.p2;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class VideoActivity extends AppCompatActivity {

    private Button buttonCapture, buttonBack, buttonPlay, buttonPause, buttonStop;
    private VideoView videoView;
    private TextView mainText;
    private static int VIDEO_REQUEST = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

         buttonCapture = findViewById(R.id.capture);
         buttonBack = findViewById(R.id.back);
         buttonPlay = findViewById(R.id.playButton);
         buttonPause = findViewById(R.id.pauseButton);
         buttonStop = findViewById(R.id.stopButton);
         videoView = findViewById(R.id.videoView);
         mainText = findViewById(R.id.mainText);

        buttonCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Starts video capture
                captureVideo(v);
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Exits the activity and heads back to the last activity opened
                finish();
            }
        });
    }

    // Starts the intent for capturing video
    public void captureVideo(View view) {
        Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        if (videoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(videoIntent, VIDEO_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == VIDEO_REQUEST && resultCode == RESULT_OK) {
            // A Media controller to give the user control over the video
            final MediaController mediaController = new MediaController(VideoActivity.this);
            mediaController.setAnchorView(videoView);
            videoView.setMediaController(mediaController);
            videoView.requestFocus();
            // Sets the data of the video recorded into the VideoView component
            videoView.setVideoURI(data.getData());
            // Sets the text of the main text of the application to let the user know that a video was taken
            mainText.setText("Video has been taken. View below!");

            // Once the video is loaded, we do what we want to the video
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    // Starts playing the video
                    videoView.start();

                    // Changes the status of the video control buttons to allow the user to click on
                    // them since the video has been loaded
                    buttonPlay.setBackgroundColor(Color.parseColor("#009688"));
                    buttonPlay.setClickable(true);
                    buttonPause.setBackgroundColor(Color.parseColor("#009688"));
                    buttonPause.setClickable(true);
                    buttonStop.setBackgroundColor(Color.parseColor("#009688"));
                    buttonStop.setClickable(true);

                    // When the play button is clicked, the video restarts from the beginning
                    // Very confusing on why they give the method name "resume"
                    buttonPlay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            videoView.resume();

                        }
                    });
                    // When the pause button is clicked, the video pauses
                    buttonPause.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            videoView.pause();
                        }
                    });
                    // When the stop button is clicked, the video is stopped (aka the current position is
                    // set back to 0:00 but the media player will still show the progress bar at the last played position
                    buttonStop.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            videoView.stopPlayback();
                        }
                    });
                }
            });
        }
    }
}
