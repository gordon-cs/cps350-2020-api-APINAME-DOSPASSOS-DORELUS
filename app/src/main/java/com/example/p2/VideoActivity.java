package com.example.p2;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class VideoActivity extends AppCompatActivity {

    private Button buttonCapture, buttonBack, buttonPlay, buttonPause, buttonStop;
    private ProgressBar progressBar;
    private VideoView videoView;
    private TextView mainText, currentTime, durationTime;
    private static int VIDEO_REQUEST = 100, videoDuration = 0;

    // Gets all buttons and components from view and sets them for usage in other functions
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
         currentTime = findViewById(R.id.currentTime);
         durationTime = findViewById(R.id.durationTime);
         progressBar = findViewById(R.id.progressBar);

        buttonCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Starts video capture
                captureVideo();
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

    @Override
    protected void onStop() {
        super.onStop();
        // Releases video's resources
        videoView.stopPlayback();
    }

    @Override
    protected void onPause() {
        super.onPause();
        /* Prevents a glitch on devices lower than Android Nougat 7.0 where during the Activity lifecycle,
         * there may be a few seconds of the video's audio playback still playing even though the app is
         * not visible as onStop() catches up
         */
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            videoView.pause();
        }
    }

    // Starts the intent for capturing video
    public void captureVideo() {
        Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        if (videoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(videoIntent, VIDEO_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == VIDEO_REQUEST && resultCode == RESULT_OK) {
            // Sets the data of the video recorded into the VideoView component and saves the video URI
            videoView.setVideoURI(data.getData());
            // Sets the text of the main text of the application to let the user know that a video was taken
            mainText.setText("Video has been taken. View below!");

            // Once the video is loaded, we do what we want to the video
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(final MediaPlayer mp) {
                    // Saves the duration of the video
                    videoDuration = mp.getDuration();
                    // Starts playing the video
                    mp.start();
                    // Sets the maximum number of the progress bar to keep 0-100% ratio of the video's length
                    progressBar.setMax(videoDuration);
                    // Sets the duration time text of the video
                    final Date duration = new Date(videoDuration);
                    final DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                    formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                    final String timeFormatted = formatter.format(duration);
                    durationTime.setText(timeFormatted);

                    // A thread to monitor the change in the time of the video playing to update the
                    // progress bar and current time text
                    final Thread videoWatcher = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // This while loop allows for the progress bar and current time to continuously  update
                            while (mp.getCurrentPosition() < videoDuration) {
                                progressBar.setProgress(mp.getCurrentPosition());
                                final Date current = new Date(mp.getCurrentPosition());
                                final String timeFormatted = formatter.format(current);
                                /* This is called to prevent an error where only the original thread that created
                                 * a view hierarchy can touch its views. In other words, only the UI thread
                                 * can make changes to the UI
                                 */
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Sets the current time of the video
                                        currentTime.setText(timeFormatted);
                                    }
                                });
                            }
                        }
                    });

                    // Creates a universal thread exception catcher
                    videoWatcher.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                        public void uncaughtException(Thread th, Throwable ex) {
                            System.out.println("ERROR: " + ex);
                        }
                    });

                    // Starts running the thread
                    videoWatcher.start();

                    // Changes the status of the video control buttons to allow the user to click on
                    // them since the video has been loaded
                    buttonPlay.setBackgroundColor(Color.parseColor("#009688"));
                    buttonPlay.setClickable(true);
                    buttonPause.setBackgroundColor(Color.parseColor("#009688"));
                    buttonPause.setClickable(true);
                    buttonStop.setBackgroundColor(Color.parseColor("#009688"));
                    buttonStop.setClickable(true);

                    // When the play button is clicked, the video resumes its last position
                    buttonPlay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Continues playing the video
                            initializePlayer(mp);

                            // Enables the Pause and Stop buttons
                            buttonPause.setBackgroundColor(Color.parseColor("#009688"));
                            buttonPause.setClickable(true);
                            buttonStop.setBackgroundColor(Color.parseColor("#009688"));
                            buttonStop.setClickable(true);
                            // Disables the Play button
                            buttonPlay.setBackgroundColor(Color.parseColor("#80009688"));
                            buttonPlay.setClickable(false);
                        }
                    });

                    // When the pause button is clicked, the video pauses
                    buttonPause.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Pauses the video
                            mp.pause();

                            // Disables the Pause button
                            buttonPause.setBackgroundColor(Color.parseColor("#80009688"));
                            buttonPause.setClickable(false);
                            // Enables the Play button
                            buttonPlay.setBackgroundColor(Color.parseColor("#009688"));
                            buttonPlay.setClickable(true);
                        }
                    });

                    // When the stop button is clicked, the video is stopped (aka the current position is
                    // set back to 0:00 but the media player will still show the progress bar at the last played position
                    buttonStop.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Stops the video player and brings it back to the beginning
                            stopPlayer(mp);

                            // Disables the Pause and Stop buttons
                            buttonPause.setBackgroundColor(Color.parseColor("#80009688"));
                            buttonPause.setClickable(false);
                            buttonStop.setBackgroundColor(Color.parseColor("#80009688"));
                            buttonStop.setClickable(false);
                            // Enables the Play button
                            buttonPlay.setBackgroundColor(Color.parseColor("#009688"));
                            buttonPlay.setClickable(true);
                        }
                    });
                }
            });


            // Once the video finishes, we reset all necessary data
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    // Disables the Pause and Stop buttons
                    buttonPause.setBackgroundColor(Color.parseColor("#80009688"));
                    buttonPause.setClickable(false);
                    buttonStop.setBackgroundColor(Color.parseColor("#80009688"));
                    buttonStop.setClickable(false);
                    // Enables the Play button
                    buttonPlay.setBackgroundColor(Color.parseColor("#009688"));
                    buttonPlay.setClickable(true);
                }
            });
        }
    }

    // Initializes the media player to play the video from its last position
    private void initializePlayer(final MediaPlayer mp) {
        mp.start();
    }

    // Stops the media player and brings the video back to the beginning
    private void stopPlayer(MediaPlayer mp) {
        mp.pause();
        mp.seekTo(1);
    }
}