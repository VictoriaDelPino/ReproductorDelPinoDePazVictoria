package edu.prueba.reproductordelpinodepazvictoria;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;
import android.content.pm.ActivityInfo;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class VideoPlayerActivity extends AppCompatActivity {

    private VideoView videoView;
    private MediaController mediaController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_video_player);

        // Obtener datos del intent
        int tipoVideo = getIntent().getIntExtra("tipo_video", -1);
        String videoUrl = getIntent().getStringExtra("video_url");

        // Inicializar VideoView y MediaController
        videoView = findViewById(R.id.videoView);
        mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        if (videoUrl != null) {
            Uri videoUri;
            if (tipoVideo == 1) {
                // Video en res/raw
                int resId = getResources().getIdentifier(videoUrl, "raw", getPackageName());
                videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + resId);
            } else {
                // Video online (tipo 2)
                videoUri = Uri.parse(videoUrl);
            }

            videoView.setVideoURI(videoUri);
            videoView.start();
        }

        // Cerrar la actividad cuando el video termine
        videoView.setOnCompletionListener(mp -> finish());


    }
}
