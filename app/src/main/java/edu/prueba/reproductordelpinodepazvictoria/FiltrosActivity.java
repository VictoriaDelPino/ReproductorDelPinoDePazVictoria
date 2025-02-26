package edu.prueba.reproductordelpinodepazvictoria;

import android.content.Intent;
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
    private boolean audio;
    private boolean video;
    private boolean streaming;
    private Button confirmar;
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

        checkBoxAudio=findViewById(R.id.checkBoxAudio);
        checkBoxVideo= findViewById(R.id.checkBoxVideo);
        checkBoxStreaming=findViewById(R.id.checkBoxStreaming);

        checkBoxAudio.setOnCheckedChangeListener((buttonView, isChecked) -> {
            audio = isChecked;
        });
        checkBoxVideo.setOnCheckedChangeListener((buttonView, isChecked) -> {
            video = isChecked;
        });
        checkBoxStreaming.setOnCheckedChangeListener((buttonView, isChecked) -> {
            streaming = isChecked;
        });


        confirmar = findViewById(R.id.button);
        confirmar.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("audio", audio);
            resultIntent.putExtra("video", video);
            resultIntent.putExtra("streaming", streaming);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
}