package edu.prueba.reproductordelpinodepazvictoria;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.prueba.reproductordelpinodepazvictoria.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements MediaController.MediaPlayerControl {

    private RecyclerView recyclerView;
    private ItemRecycleViewAdapter adapter;
    private List<Recurso> recursoList;
    private ActivityMainBinding binding;
    private boolean filtroAudio, filtroVideo, filtroStreaming;
    private MediaPlayer mediaPlayer;
    private MediaController mediaController;
    private boolean shouldHideController = false; // Flag para controlar la visibilidad

    private final ActivityResultLauncher<Intent> filtroLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    filtroAudio = data.getBooleanExtra("audio", false);
                    filtroVideo = data.getBooleanExtra("video", false);
                    filtroStreaming = data.getBooleanExtra("streaming", false);

                    Toast.makeText(this, "Audio: " + filtroAudio +
                            ", Video: " + filtroVideo +
                            ", Streaming: " + filtroStreaming, Toast.LENGTH_LONG).show();

                    setupRecyclerView();
                }
            }
    );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            v.setPadding(insets.getSystemWindowInsetLeft(), insets.getSystemWindowInsetTop(),
                    insets.getSystemWindowInsetRight(), insets.getSystemWindowInsetBottom());
            return insets;
        });

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        SharedPreferences sharedPreferences = getSharedPreferences("FiltrosPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("primera_vez", true);
        editor.apply();

        filtroAudio = true;
        filtroVideo = true;
        filtroStreaming = true;

        setupRecyclerView();
        setupMediaController();
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.RecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));

        recursoList = new ArrayList<>();
        List<Recurso> allRecursos = RecursoManager.loadRecursosFromJSON(this);
        for (Recurso recurso : allRecursos) {
            if ((recurso.getTipo() == 0 && filtroAudio)) {
                recursoList.add(recurso);
            }
            if ((recurso.getTipo() == 1 && filtroVideo)) {
                recursoList.add(recurso);
            }
            if ((recurso.getTipo() == 2 && filtroStreaming)) {
                recursoList.add(recurso);
            }
        }

        adapter = new ItemRecycleViewAdapter(this, recursoList, recurso -> {
            if (recurso.getTipo() == 1 || recurso.getTipo() == 2) {
                detenerAudio();
                Intent intent = new Intent(this, VideoPlayerActivity.class);
                intent.putExtra("tipo_video", recurso.getTipo());
                intent.putExtra("video_url", recurso.getURI());
                startActivity(intent);
            } else if (recurso.getTipo() == 0) {
                reproducirAudioLocal(recurso.getURI());
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void setupMediaController() {
        mediaController = new MediaController(this) {
            @Override
            public void hide() {
                if (shouldHideController) {
                    super.hide();
                }
            }
        };
        mediaController.setAnchorView(findViewById(R.id.main));
        mediaController.setMediaPlayer(this);
    }

    private void reproducirAudioLocal(String nombreArchivo) {
        detenerAudio();

        int resId = getResources().getIdentifier(nombreArchivo, "raw", getPackageName());
        if (resId != 0) {
            mediaPlayer = MediaPlayer.create(this, resId);
            mediaPlayer.start();

            shouldHideController = false; // Evita que se oculte mientras se usa
            mediaController.show(0); // Mostrar siempre

            mediaPlayer.setOnCompletionListener(mp -> {
                shouldHideController = true; // Permite ocultar el controlador
                mediaController.hide(); // Se oculta solo al terminar el audio
            });

        } else {
            Toast.makeText(this, "Audio no encontrado en raw", Toast.LENGTH_SHORT).show();
        }
    }

    private void detenerAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        detenerAudio();
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        return (mediaPlayer != null) ? mediaPlayer.getCurrentPosition() : 0;
    }

    @Override
    public int getDuration() {
        return (mediaPlayer != null) ? mediaPlayer.getDuration() : 0;
    }

    @Override
    public boolean isPlaying() {
        return (mediaPlayer != null && mediaPlayer.isPlaying());
    }

    @Override
    public void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    public void seekTo(int pos) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(pos);
        }
    }

    @Override
    public void start() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    @Override
    public int getAudioSessionId() {
        return (mediaPlayer != null) ? mediaPlayer.getAudioSessionId() : 0;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (item.getItemId() == R.id.itemFiltro) {
            Intent intent = new Intent(this, FiltrosActivity.class);
            filtroLauncher.launch(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
