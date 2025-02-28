package edu.prueba.reproductordelpinodepazvictoria;

import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;
import android.content.pm.ActivityInfo;

import androidx.appcompat.app.AppCompatActivity;

//Actividad encargada de la reproducción de videos, ya sean locales o en streaming
public class VideoPlayerActivity extends AppCompatActivity {

    private VideoView videoView;
    private MediaController mediaController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Fuerza la orientación de la pantalla a horizontal
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_video_player);

        // Obtiene los datos del intent
        int tipoVideo = getIntent().getIntExtra("tipo_video", -1);
        String videoUrl = getIntent().getStringExtra("video_url");

        // Inicializa el VideoView y el MediaController
        videoView = findViewById(R.id.videoView);
        mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        if (videoUrl != null) {
            Uri videoUri;
            if (tipoVideo == 1) {
                // Si el video es local obtiene su URI
                int resId = getResources().getIdentifier(videoUrl, "raw", getPackageName());
                videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + resId);
            } else {
                // Si el video es streaming convierte la URL en un URI
                videoUri = Uri.parse(videoUrl);
            }

            // Asigna la URI al VideoView y comenzar la reproducción
            videoView.setVideoURI(videoUri);
            videoView.start();
        }

        // Cierra la actividad automáticamente cuando el video termina
        videoView.setOnCompletionListener(mp -> finish());


    }
}
