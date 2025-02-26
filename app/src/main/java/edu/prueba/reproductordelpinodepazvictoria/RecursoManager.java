package edu.prueba.reproductordelpinodepazvictoria;

import android.content.Context;
import android.content.res.AssetManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RecursoManager {

    public static List<Recurso> loadRecursosFromJSON(Context context) {
        List<Recurso> recursos = new ArrayList<>();
        try {
            // Lee el JSON desde assets
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open("recursosList.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            String json = new String(buffer, "UTF-8");

            // Convierte el JSON a una lista de objetos Recurso usando Gson
            Gson gson = new Gson();
            Type listType = new TypeToken<RecursosWrapper>() {}.getType();
            RecursosWrapper wrapper = gson.fromJson(json, listType);

            if (wrapper != null && wrapper.recursos_list != null) {
                recursos = wrapper.recursos_list;
            }

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error al cargar el JSON", Toast.LENGTH_SHORT).show();
        }
        return recursos;
    }

    // Clase auxiliar para manejar el JSON correctamente
    private static class RecursosWrapper {
        List<Recurso> recursos_list;
    }
}
