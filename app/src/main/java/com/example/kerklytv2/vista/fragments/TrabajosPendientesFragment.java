package com.example.kerklytv2.vista.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kerklytv2.R;
import com.google.android.material.button.MaterialButton;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TrabajosPendientesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TrabajosPendientesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private MaterialButton btn_urgencia;
    private MaterialButton btn_servicio;
    private Bundle b;

    public TrabajosPendientesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TrabajosPendientesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TrabajosPendientesFragment newInstance(String param1, String param2) {
        TrabajosPendientesFragment fragment = new TrabajosPendientesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_trabajos_pendientes, container, false);

        btn_urgencia = v.findViewById(R.id.btn_urgencia);
        btn_servicio = v.findViewById(R.id.btn_servicioNormal);
        b = getArguments();

        btn_urgencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTrabajos();
            }
        });

        btn_servicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setServicio();
            }
        });

        return v;
    }

    private void setTrabajos() {
        TrabajosUrgenciaFragment f = new TrabajosUrgenciaFragment();
        f.setArguments(b);
        int fm = requireActivity().getSupportFragmentManager().beginTransaction().
                replace(R.id.nav_host_fragment_content_interfaz_kerkly,f).commit();
    }

    private void setServicio() {
        ServicioNormalFragment f = new ServicioNormalFragment();
        f.setArguments(b);
        int fm = requireActivity().getSupportFragmentManager().beginTransaction().
                replace(R.id.nav_host_fragment_content_interfaz_kerkly,f).commit();
    }
}
