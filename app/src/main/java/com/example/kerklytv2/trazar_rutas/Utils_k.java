package com.example.kerklytv2.trazar_rutas;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Utils_k {

    public static Coordenadas coordenadas = new Coordenadas();
    public static List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();

    public static void Marcador(GoogleMap nMap, Context context, Double latitud, Double longitud , String problema, String nombreCliente, String folio){
        new Marcador(nMap, context, latitud, longitud, problema, nombreCliente, folio).ObetenerCoordenadasBaseDeDatos();
    }
}
