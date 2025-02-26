package edu.prueba.reproductordelpinodepazvictoria;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ItemRecycleViewAdapter extends RecyclerView.Adapter<ItemRecycleViewAdapter.ViewHolder> {

    private final List<Recurso> recursos;
    private final Context context;

    public ItemRecycleViewAdapter(Context context, List<Recurso> recursos) {
        this.context = context;
        this.recursos = recursos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_item_recurso, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recurso recurso = recursos.get(position);

        holder.txtNombre.setText(recurso.getNombre());
        holder.txtDescripcion.setText(recurso.getDescripcion());

        // Cargar imagen desde assets/images usando Bitmap
        Bitmap bitmap = getBitmapFromAssets(recurso.getImagen());
        if (bitmap != null) {
            holder.imgCaratula.setImageBitmap(bitmap);
        } else {
            holder.imgCaratula.setImageResource(R.drawable.ic_launcher_foreground); // Imagen por defecto si no se encuentra
        }

        // Acción al hacer clic en el botón de play
        holder.imgBtnPlay.setOnClickListener(v -> {
            Toast.makeText(context, "Reproduciendo: " + recurso.getURI(), Toast.LENGTH_SHORT).show();
            // Aquí puedes agregar la lógica para reproducir audio/video
        });
    }

    @Override
    public int getItemCount() {
        return recursos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCaratula;
        TextView txtNombre, txtDescripcion;
        ImageButton imgBtnPlay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCaratula = itemView.findViewById(R.id.imgCaratula);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtDescripcion = itemView.findViewById(R.id.txtDescripcion);
            imgBtnPlay = itemView.findViewById(R.id.imgBtnPlay);
        }
    }

    /**
     * Método para cargar una imagen desde assets/images usando Bitmap
     */
    private Bitmap getBitmapFromAssets(String fileName) {
        try {
            InputStream inputStream = context.getAssets().open("images/" + fileName);
            return BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
