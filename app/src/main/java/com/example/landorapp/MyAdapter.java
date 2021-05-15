package com.example.landorapp;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ParkingViewHolder> {

    private Context mCtx;
    private List<Parking> parkingList;

    public MyAdapter(Context mCtx, List<Parking> parkingList){
        this.mCtx = mCtx;
        this.parkingList=parkingList;
    }

    @Override
    public ParkingViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.list_parkings, null);


        return new ParkingViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ParkingViewHolder holder, int position){
        Parking parking = parkingList.get(position);
        holder.textViewNombre.setText(parking.getNombreParking());
        holder.textViewEmpresa.setText(parking.getNombreEmpresa());
        holder.textViewTarifa.setText(parking.getTarifa());
        holder.textViewDireccion.setText(parking.getDireccion());


    }

    @Override
    public int getItemCount() {return parkingList.size();}

    class ParkingViewHolder extends RecyclerView.ViewHolder{
        TextView textViewNombre, textViewEmpresa, textViewTarifa, textViewDireccion;

        public ParkingViewHolder(View itemView){
            super(itemView);
            //enlazamos los widgets
            textViewNombre= itemView.findViewById(R.id.nombreParking_label);
            textViewEmpresa= itemView.findViewById(R.id.nombreEmpresa_label);
            textViewTarifa= itemView.findViewById(R.id.tarifa_label);
            textViewDireccion= itemView.findViewById(R.id.direccion_label);
            //onclick
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), InfoParking.class);
                    intent.putExtra("MENSAJE_NOMBRE", textViewNombre.getText().toString());
                    intent.putExtra("MENSAJE_EMPRESA", textViewEmpresa.getText().toString());
                    intent.putExtra("MENSAJE_TARIFA", textViewTarifa.getText().toString());
                    intent.putExtra("MENSAJE_DIRECCION", textViewDireccion.getText().toString());
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }



}
