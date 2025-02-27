package edu.prueba.reproductordelpinodepazvictoria;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import edu.prueba.reproductordelpinodepazvictoria.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ItemRecycleViewAdapter adapter;
    private List<Recurso> recursoList;
    private ActivityMainBinding binding;
    private boolean filtroAudio, filtroVideo, filtroStreaming;
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();

    private SeekBar seekBar;
    private ImageButton btnPausaPlay, btnAdelante, btnAtras;
    private TextView txtTiempoActual;
    private boolean isPlaying = false;

    private final ActivityResultLauncher<Intent> filtroLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    filtroAudio = data.getBooleanExtra("audio", false);
                    filtroVideo = data.getBooleanExtra("video", false);
                    filtroStreaming = data.getBooleanExtra("streaming", false);

                    // Mostrar los valores recibidos
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
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        // Reseteamos "primera_vez" para que al cerrar la app de verdad se reinicien los filtros
        SharedPreferences sharedPreferences = getSharedPreferences("FiltrosPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("primera_vez", true);
        editor.apply();


        filtroAudio=true;
        filtroVideo= true;
        filtroStreaming=true;

        // Inicializa el RecyclerView
        setupRecyclerView();

        // Inicializa controles de audio
        setupAudioControls();
    }

    private void setupRecyclerView() {
        // Vincula el RecyclerView con su ID en el layout
        recyclerView = findViewById(R.id.RecyclerView);

        // Usa un GridLayoutManager con 2 columnas
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));

        // Carga la lista de recursos
        recursoList = new ArrayList<>();
        List<Recurso> allRecursos = RecursoManager.loadRecursosFromJSON(this);
        for (Recurso recurso : allRecursos) {
            if ((recurso.getTipo() == 0 && filtroAudio) ) {
                recursoList.add(recurso);
            }
            if ((recurso.getTipo() == 1 && filtroVideo) ) {
                recursoList.add(recurso);
            }
            if ((recurso.getTipo() == 2 && filtroStreaming) ) {
                recursoList.add(recurso);
            }

        }
        // Inicializa el adaptador y asigna la lista de recursos
        adapter = new ItemRecycleViewAdapter(this, recursoList, recurso -> {
            //Toast.makeText(this, "Reproduciendo: " + recurso.getURI(), Toast.LENGTH_SHORT).show();

            if (recurso.getTipo() == 1 || recurso.getTipo() == 2) {
                detenerAudio(); // Si se abre un video, detener audio
                Intent intent = new Intent(this, VideoPlayerActivity.class);
                intent.putExtra("tipo_video", recurso.getTipo());
                intent.putExtra("video_url", recurso.getURI());
                startActivity(intent);
            }else if (recurso.getTipo() == 0){
                Toast.makeText(this, "Tipo 0", Toast.LENGTH_SHORT).show();
                reproducirAudioLocal(recurso.getURI());
            }


        });
        recyclerView.setAdapter(adapter);


    }


    private void setupAudioControls() {
        seekBar = findViewById(R.id.seekBar);
        btnPausaPlay = findViewById(R.id.btnPausaPlay);
        btnAdelante = findViewById(R.id.btnAdelante);
        btnAtras = findViewById(R.id.btnAtras);
        txtTiempoActual = findViewById(R.id.txtTiempoActual);

        // Inicialmente ocultos
        ocultarControlesAudio();

        btnPausaPlay.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                if (isPlaying) {
                    mediaPlayer.pause();
                    btnPausaPlay.setImageResource(android.R.drawable.ic_media_play);
                } else {
                    mediaPlayer.start();
                    btnPausaPlay.setImageResource(android.R.drawable.ic_media_pause);
                    actualizarSeekBar();
                }
                isPlaying = !isPlaying;
            }
        });
    }

    private void reproducirAudioLocal(String nombreArchivo) {
        detenerAudio(); // Asegurar que no haya otro audio sonando

        int resId = getResources().getIdentifier(nombreArchivo, "raw", getPackageName());
        if (resId != 0) {
            mediaPlayer = MediaPlayer.create(this, resId);
            mediaPlayer.start();
            isPlaying = true;

            // Mostrar controles de audio
            mostrarControlesAudio();

            // Cambiar icono a "Pause"
            btnPausaPlay.setImageResource(android.R.drawable.ic_media_pause);

            seekBar.setMax(mediaPlayer.getDuration());
            actualizarSeekBar();

            mediaPlayer.setOnCompletionListener(mp -> {
                btnPausaPlay.setImageResource(android.R.drawable.ic_media_play);
                isPlaying = false;
                ocultarControlesAudio();
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
            isPlaying = false;
            ocultarControlesAudio();
        }
    }

    private void actualizarSeekBar() {
        handler.postDelayed(() -> {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                txtTiempoActual.setText(formatoTiempo(mediaPlayer.getCurrentPosition()));
                actualizarSeekBar();
            }
        }, 1000);
    }

    private void ocultarControlesAudio() {
        seekBar.setVisibility(SeekBar.INVISIBLE);
        btnPausaPlay.setVisibility(ImageButton.INVISIBLE);
        btnAdelante.setVisibility(ImageButton.INVISIBLE);
        btnAtras.setVisibility(ImageButton.INVISIBLE);
        txtTiempoActual.setVisibility(TextView.INVISIBLE);
    }

    private void mostrarControlesAudio() {
        seekBar.setVisibility(SeekBar.VISIBLE);
        btnPausaPlay.setVisibility(ImageButton.VISIBLE);
        btnAdelante.setVisibility(ImageButton.VISIBLE);
        btnAtras.setVisibility(ImageButton.VISIBLE);
        txtTiempoActual.setVisibility(TextView.VISIBLE);
    }

    private String formatoTiempo(int millis) {
        int minutos = (int) TimeUnit.MILLISECONDS.toMinutes(millis);
        int segundos = (int) (TimeUnit.MILLISECONDS.toSeconds(millis) % 60);
        return String.format("%02d:%02d", minutos, segundos);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        detenerAudio();
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
