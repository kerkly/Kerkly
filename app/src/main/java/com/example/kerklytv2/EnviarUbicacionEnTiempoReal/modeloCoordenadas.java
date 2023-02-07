package com.example.kerklytv2.EnviarUbicacionEnTiempoReal;

public class modeloCoordenadas {
    Double Latitud;
    Double Longitud;

    public modeloCoordenadas() {
    }

    public modeloCoordenadas(Double Latitud, Double Longitud) {
        this.Latitud = Latitud;
        this.Longitud = Longitud;
    }

    public Double getLatitud() {
        return Latitud;
    }

    public void setLatitud(Double Latitud) {
        this.Latitud = Latitud;
    }

    public Double getLongitud() {
        return Longitud;
    }

    public void setLongitud(Double Longitud) {
        this.Longitud = Longitud;
    }
}
