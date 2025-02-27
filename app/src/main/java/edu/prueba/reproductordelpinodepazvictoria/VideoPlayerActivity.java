package edu.prueba.reproductordelpinodepazvictoria;

import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.pm.ActivityInfo;
import android.widget.VideoView;


public class VideoPlayerActivity extends AppCompatActivity {

    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_video_player);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Obtener el Intent y los datos pasados
        int tipoVideo = getIntent().getIntExtra("tipo_video", -1); // Valor por defecto -1 si no existe
        String videoUrl = getIntent().getStringExtra("video_url"); // Devuelve null si no existe

        // Mostrar los valores recibidos en un Toast (para depuración)
        Toast.makeText(this, "Tipo de Video: " + tipoVideo + "\nURL: " + videoUrl, Toast.LENGTH_LONG).show();

        videoView=findViewById(R.id.videoView);
        if (tipoVideo == 1 && videoUrl != null) {
            // Obtener la URI del recurso en raw
            int resId = getResources().getIdentifier(videoUrl, "raw", getPackageName());

            if (resId != 0) {
                Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + resId);
                videoView.setVideoURI(videoUri);

                // Agregar controles de reproducción
                MediaController mediaController = new MediaController(this);
                mediaController.setAnchorView(videoView);
                videoView.setMediaController(mediaController);

                videoView.start();
            } else if (tipoVideo == 2 && videoUrl != null) {
                // Reproducir video desde URL online
                Uri videoUri = Uri.parse(videoUrl);
                videoView.setVideoURI(videoUri);
                videoView.start();
            }else {
                Toast.makeText(this, "Video no encontrado en raw", Toast.LENGTH_SHORT).show();
            }
        }


    }

}