package edu.prueba.reproductordelpinodepazvictoria;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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

import edu.prueba.reproductordelpinodepazvictoria.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ItemRecycleViewAdapter adapter;
    private List<Recurso> recursoList;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    public static int prueba;


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
        // Inicializa el RecyclerView
        setupRecyclerView();

    }

    private void setupRecyclerView() {
        // Vincula el RecyclerView con su ID en el layout
        recyclerView = findViewById(R.id.RecyclerView);

        // Usa un GridLayoutManager con 2 columnas
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));

        // Carga la lista de recursos
        recursoList = new ArrayList<>();
        recursoList.add(new Recurso("Lori Meyers", "El tiempo pasará", 0, "eltiempopasara", "eltiempo.jpg"));
        recursoList.add(new Recurso("Lori Meyers", "Emborracharme", 2, "https://riberadeltajo.es/PMDM_ut4/LME.mp4", "emborracharme.jpg"));
        recursoList.add(new Recurso("Manuel Martínez", "Truco de magia", 1, "magia", "magic.jpg"));
        recursoList.add(new Recurso("Metallica", "Enter Sandman", 0, "entersandman", "entersandman.jpg"));

        // Inicializa el adaptador y asigna la lista de recursos
        adapter = new ItemRecycleViewAdapter(this, recursoList, recurso -> {
            //Toast.makeText(this, "Reproduciendo: " + recurso.getURI(), Toast.LENGTH_SHORT).show();

            if (recurso.getTipo() == 1 ) {
                Toast.makeText(this, "Tipo 1", Toast.LENGTH_SHORT).show();

               /* Intent intent = new Intent(this, VideoPlayerActivity.class);
                intent.putExtra("video_url", recurso.getURI());
                startActivity(intent);*/
            }else if (recurso.getTipo() == 2){
                Toast.makeText(this, "Tipo 2", Toast.LENGTH_SHORT).show();

            }else if (recurso.getTipo() == 0){
                Toast.makeText(this, "Tipo 0", Toast.LENGTH_SHORT).show();
            }


        });
        recyclerView.setAdapter(adapter);


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

        if (id == R.id.itemFiltro) {
            // Acción al hacer clic en "Settings"
            Intent intent = new Intent(this, FiltrosActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
