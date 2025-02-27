package edu.prueba.reproductordelpinodepazvictoria;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class FiltrosActivity extends AppCompatActivity {

    private CheckBox checkBoxAudio;
    private CheckBox checkBoxVideo;
    private CheckBox checkBoxStreaming;
    private Button confirmar;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_filtros);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        checkBoxAudio = findViewById(R.id.checkBoxAudio);
        checkBoxVideo = findViewById(R.id.checkBoxVideo);
        checkBoxStreaming = findViewById(R.id.checkBoxStreaming);
        confirmar = findViewById(R.id.button);

        // Obtener SharedPreferences
        sharedPreferences = getSharedPreferences("FiltrosPrefs", Context.MODE_PRIVATE);

        // Verificar si es la primera vez que se abre la app después de cerrarla
        boolean primeraVez = sharedPreferences.getBoolean("primera_vez", true);

        if (primeraVez) {
            // Si es la primera vez después de cerrar la app, restablecer valores a true
            savePreferences(true, true, true);
            sharedPreferences.edit().putBoolean("primera_vez", false).apply();
        }

        // Cargar valores guardados
        boolean audio = sharedPreferences.getBoolean("audio", true);
        boolean video = sharedPreferences.getBoolean("video", true);
        boolean streaming = sharedPreferences.getBoolean("streaming", true);

        // Aplicar los valores a los CheckBox
        checkBoxAudio.setChecked(audio);
        checkBoxVideo.setChecked(video);
        checkBoxStreaming.setChecked(streaming);

        confirmar.setOnClickListener(v -> {
            // Guardar valores en SharedPreferences
            savePreferences(checkBoxAudio.isChecked(), checkBoxVideo.isChecked(), checkBoxStreaming.isChecked());

            // Enviar los valores de vuelta a MainActivity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("audio", checkBoxAudio.isChecked());
            resultIntent.putExtra("video", checkBoxVideo.isChecked());
            resultIntent.putExtra("streaming", checkBoxStreaming.isChecked());
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    private void savePreferences(boolean audio, boolean video, boolean streaming) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("audio", audio);
        editor.putBoolean("video", video);
        editor.putBoolean("streaming", streaming);
        editor.apply();
    }
}
