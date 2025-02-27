package edu.prueba.reproductordelpinodepazvictoria;

import android.net.Uri;
import android.os.Bundle;
import android.content.pm.ActivityInfo;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerView;

public class VideoPlayerActivity extends AppCompatActivity {

    private PlayerView playerView;
    private ExoPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_video_player);

        // Obtener datos del intent
        int tipoVideo = getIntent().getIntExtra("tipo_video", -1);
        String videoUrl = getIntent().getStringExtra("video_url");

        // Mostrar Toast con la info recibida
        Toast.makeText(this, "Tipo de Video: " + tipoVideo + "\nURL: " + videoUrl, Toast.LENGTH_LONG).show();

        // Inicializar PlayerView
        playerView = findViewById(R.id.playerView);
        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

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

            MediaItem mediaItem = new MediaItem.Builder().setUri(videoUri).build();
            player.setMediaItem(mediaItem);
            player.prepare();
            player.play();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) {
            player.release();
        }
    }
}
