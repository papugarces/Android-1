package com.jose.proyectos_institucionales;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jose.proyectos_institucionales.controlador.CtlCargo;
import com.jose.proyectos_institucionales.controlador.CtlIntegrante;
import com.jose.proyectos_institucionales.controlador.CtlUsuario;
import com.jose.proyectos_institucionales.modelo.Cargo;
import com.jose.proyectos_institucionales.modelo.Integrante;
import com.jose.proyectos_institucionales.modelo.Proyecto;
import com.jose.proyectos_institucionales.modelo.Usuario;

import java.util.ArrayList;


public class GestionIntegrantesProyecto extends AppCompatActivity  {

    AlertDialog.Builder ad;
    EditText txtCedula;
    CtlUsuario controladorUsuario;
    CtlIntegrante controladorIntegrantes;
    CtlCargo controladorCargo;
    ListView listVieww;
    Proyecto proyecto;
    ArrayList<Integrante> listaDeIntegrantes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_integrantes_proyecto);

        txtCedula = findViewById(R.id.txtCedulaUsuario);
        listVieww = findViewById(R.id.listVieww);

        Bundle bundle = getIntent().getExtras();
        proyecto = (Proyecto) bundle.getSerializable("objProyecto");

        controladorUsuario = new CtlUsuario(this);
        controladorIntegrantes = new CtlIntegrante(this);
        controladorCargo = new CtlCargo(this);

        listaDeIntegrantes = (ArrayList<Integrante>) controladorIntegrantes.listarIntegrantesProyecto(proyecto.getId());


        if (listaDeIntegrantes.size()!=0){
            cargarLista(listaDeIntegrantes);
        }


    }

    public void seleccionarCargo(Integrante integranteMed){
        Intent intent = new Intent(this , EleccionCargoUsuario.class);
        intent.putExtra("objIntegrante",integranteMed);
        intent.putExtra("objProyecto",proyecto);
        startActivity(intent);
    }

    public void regresar(View view){
        Intent intent = new Intent(this , DetalleProyectoPropio.class);
        intent.putExtra("objProyecto",proyecto);
        startActivity(intent);
    }

    public void cargarLista(ArrayList<Integrante> lista) {

        ArrayList<String> nombreIntegrantes = new ArrayList<>();
        String entrada;
        for (Integrante integrante : lista){

            Usuario usuario = controladorUsuario.buscarUsuarioPorID(integrante.getIdUsuario());

            Cargo cargo = controladorCargo.buscarCargo(integrante.getIdCargo());

            entrada = usuario.getNombres() + " "+ usuario.getApellidos() + " " + cargo.getNombre();
            nombreIntegrantes.add(entrada);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, nombreIntegrantes);
        listVieww.setAdapter(adapter);
        listVieww.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int posicion, long id) {
                abrirInformacion(posicion);
            }
        });

    }

    public void buscarUsuarioAgregar(View view) {
        if (!txtCedula.getText().toString().equals("")) {
            final Usuario usuario = controladorUsuario.buscarUsuarioCedula(txtCedula.getText().toString());
            if (usuario != null) {
                final Context context = this;
                ad = new AlertDialog.Builder(context);
                ad.setTitle("Integrantes");
                ad.setMessage("Desea Vincular a :\n" + usuario.getNombres() + " " + usuario.getApellidos());
                ad.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Integrante integranteMed = new Integrante(proyecto.getId(),usuario.getId(),null);
                        seleccionarCargo(integranteMed);
                    }
                });
                ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        txtCedula.setText("");
                        dialog.cancel();
                    }
                });
                ad.show();
            } else {
                Toast.makeText(getApplicationContext(), "No hay Ninguna persona con este # De Documento", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Debe de Llenar El Campo", Toast.LENGTH_SHORT).show();
        }

        cargarLista(listaDeIntegrantes);
    }

    public void abrirInformacion(Integer pos){
        final Context context = this;
        ad = new AlertDialog.Builder(context);
        ad.setTitle("Integrantes");

        final Usuario us = controladorUsuario.buscarUsuarioPorID(listaDeIntegrantes.get(pos).getIdUsuario());
        Cargo car = controladorCargo.buscarCargo(listaDeIntegrantes.get(pos).getIdCargo());

        ad.setMessage("Nombre: "+us.getNombres() +"\n"
                +"Apellido: "+us.getApellidos()+"\n"
                +"Cargo:"+car.getNombre());
        ad.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                eliminarUsuario(us.getId(),us.getNombres()+" "+us.getApellidos(),us.getNumeroDocumento());
             }
        });
        ad.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

                dialog.cancel();
            }
        });
        ad.show();
    }


    public void eliminarUsuario(final Integer idusuario , String NombreCompleto , String cedula){
        final Context context = this;
        ad = new AlertDialog.Builder(context);
        ad.setTitle("Warning");
        ad.setMessage("Desea Eliminar a "+"\n"
                +NombreCompleto+"\n"
                +"Cedula :" + cedula);
        ad.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                controladorIntegrantes.eliminarIntegrante(idusuario);
                listaDeIntegrantes = (ArrayList<Integrante>) controladorIntegrantes.listarIntegrantesProyecto(proyecto.getId());
                cargarLista(listaDeIntegrantes);
                Toast.makeText(getApplicationContext(), "Se Elimino Correctamente", Toast.LENGTH_SHORT).show();

            }
        });
        ad.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.cancel();
            }
        });
        ad.show();
    }

}
