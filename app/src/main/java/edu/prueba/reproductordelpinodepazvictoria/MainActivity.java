package edu.prueba.reproductordelpinodepazvictoria;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
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
    private boolean shouldHideController = false; // para controlar la visibilidad del mediacontroller

    // Lanzador de actividad para recibir resultados de los filtros
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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            v.setPadding(insets.getSystemWindowInsetLeft(), insets.getSystemWindowInsetTop(),
                    insets.getSystemWindowInsetRight(), insets.getSystemWindowInsetBottom());
            return insets;
        });

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        // Guarda preferencias iniciales
        SharedPreferences sharedPreferences = getSharedPreferences("FiltrosPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("primera_vez", true);
        editor.apply();

        // Habilita todos los filtros por defecto
        filtroAudio = true;
        filtroVideo = true;
        filtroStreaming = true;

        setupRecyclerView();
        setupMediaController();
    }

    // Configura el RecyclerView con los datos filtrados
    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.RecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        recursoList = new ArrayList<>();
        List<Recurso> allRecursos = RecursoManager.loadRecursosFromJSON(this);

        // Filtra los recursos según el tipo seleccionado por el usuario
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

        // Maneja la selección de un recurso en el RecyclerView
        adapter = new ItemRecycleViewAdapter(this, recursoList, recurso -> {
            if (recurso.getTipo() == 1 || recurso.getTipo() == 2) {
                detenerAudio();
                // Oculta y deshabilita temporalmente el MediaController
                if (mediaController.isShowing()) {
                    mediaController.hide();
                }
                shouldHideController = true;

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

    // Configura el MediaController para la reproducción multimedia
    private void setupMediaController() {
        mediaController = new MediaController(this) {
            @Override
            public void hide() {
                if (shouldHideController) {
                    super.hide();
                }
            }
        };
        // Ancla el controlador a la vista principal
        mediaController.setAnchorView(findViewById(R.id.main));
        // Asocia el controlador con el MediaPlayer
        mediaController.setMediaPlayer(this);
    }

    // Reproduce un archivo de audio local
    private void reproducirAudioLocal(String nombreArchivo) {
        // Detiene la reproducción anterior si existe
        detenerAudio();

        int resId = getResources().getIdentifier(nombreArchivo, "raw", getPackageName());
        if (resId != 0) {
            mediaPlayer = MediaPlayer.create(this, resId);
            mediaPlayer.start();

            // Evita que se oculte mientras se usa
            shouldHideController = false;
            mediaController.show(0);

            mediaPlayer.setOnCompletionListener(mp -> {
                // Permite ocultar el controlador una vez a terminado el audio
                shouldHideController = true;
                mediaController.hide();
            });

        } else {
            Toast.makeText(this, "Audio no encontrado en raw", Toast.LENGTH_SHORT).show();
        }
    }

    //Detiene la reproducción del audio si está en curso y libera los recursos del MediaPlayer
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

    //Indica que el reproductor puede pausar la reproducción
    @Override
    public boolean canPause() {
        return true;
    }

    //Indica que se puede retroceder en la reproducción
    @Override
    public boolean canSeekBackward() {
        return true;
    }

    //Indica que se puede avanzar en la reproducción.
    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    //Obtiene la posición actual de la reproducción
    @Override
    public int getCurrentPosition() {
        return (mediaPlayer != null) ? mediaPlayer.getCurrentPosition() : 0;
    }

    //Obtiene la duración total del audio en reproducción
    @Override
    public int getDuration() {
        return (mediaPlayer != null) ? mediaPlayer.getDuration() : 0;
    }

    //Verifica si el audio está en reproducción
    @Override
    public boolean isPlaying() {
        return (mediaPlayer != null && mediaPlayer.isPlaying());
    }

    //Pausa la reproducción del audio si está en curso
    @Override
    public void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    //Busca en la reproducción hasta la posición especificada
    @Override
    public void seekTo(int pos) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(pos);
        }
    }

    //Inicia o reanuda la reproducción del audio
    @Override
    public void start() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    // Obtiene el ID de sesión de audio
    @Override
    public int getAudioSessionId() {
        return (mediaPlayer != null) ? mediaPlayer.getAudioSessionId() : 0;
    }



    @Override
    protected void onResume() {
        super.onResume();
        //Si al volver a la actividad hay un audio reproduciendose vuelve a mostrar el mediacontroller

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            if (mediaController != null) {
                shouldHideController = false;
                mediaController.show(0); // Se muestra de forma indefinida
            }
        }
    }




    @Override
    protected void onPause() {
        super.onPause();
        // Asegurar que el MediaController se oculta cuando la actividad se pone en pausa
        if (mediaController != null) {
            mediaController.hide();
            shouldHideController = true; // Mantenerlo oculto hasta que sea necesario
        }
    }

    //Infla el menú en la barra de herramientas cuando la actividad se crea
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //Maneja la selección de opciones en el menú de la barra de herramientas
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
