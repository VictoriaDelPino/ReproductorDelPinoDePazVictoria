package edu.prueba.reproductordelpinodepazvictoria;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ItemRecycleViewAdapter extends RecyclerView.Adapter<ItemRecycleViewAdapter.ViewHolder> {

    private final List<Recurso> recursos;
    private final Context context;
    private final OnItemClickListener listener;

    // Interfaz para manejar clics en el botón Play
    public interface OnItemClickListener {
        void onPlayClick(Recurso recurso);
    }

    public ItemRecycleViewAdapter(Context context, List<Recurso> recursos, OnItemClickListener listener) {
        this.context = context;
        this.recursos = recursos;
        this.listener = listener;
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

        holder.txtNombre.setText(recurso.getNombre() + "  ");
        holder.txtDescripcion.setText(recurso.getDescripcion());

        // Cargar imagen desde assets/images usando Bitmap
        Bitmap bitmap = getBitmapFromAssets(recurso.getImagen());
        if (bitmap != null) {
            holder.imgCaratula.setImageBitmap(bitmap);
        } else {
            holder.imgCaratula.setImageResource(R.drawable.ic_launcher_foreground); // Imagen por defecto si no se encuentra
        }

        // Seleccionar el icono según el tipo de recurso
        int iconResId = 0;
        switch (recurso.getTipo()) {
            case 0:
                iconResId = R.mipmap.audio;
                break;
            case 1:
                iconResId = R.mipmap.video;
                break;
            case 2:
                iconResId = R.mipmap.streaming;
                break;
        }

        // Configurar el icono al final del TextView
        if (iconResId != 0) {
            Drawable icon = ContextCompat.getDrawable(context, iconResId);
            if (icon != null) {
                int iconSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, context.getResources().getDisplayMetrics());
                icon.setBounds(0, 0, iconSize, iconSize);
                holder.txtNombre.setCompoundDrawables(null, null, icon, null);
            }
        }

        // Acción al hacer clic en el botón de play
        holder.imgBtnPlay.setOnClickListener(v -> listener.onPlayClick(recurso));
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
