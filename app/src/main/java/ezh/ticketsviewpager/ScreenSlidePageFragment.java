package ezh.ticketsviewpager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;


public class ScreenSlidePageFragment extends Fragment
{
    private static final String TICKET_ID = "ticket_id";
    private static final String DESCRIPCION = "descripcion";
    private static final String NOMBRE_USUARIO = "nombre_usuario";
    private static final String NOMBRE_DEPTO = "nombre_depto";
    private static final String NOMBRE_PROCESO = "nombre_proceso";
    private static final String NOMBRE_ROL = "nombre_rol";
    private static final String FECHA = "fecha";
    private static final String ESTATUS = "estatus";
    private static final String FECHA_RESOLUCION = "fecha_resolucion";
    private static final String NOMBRE_LINEA = "nombre_linea";
    private static final String NOMBRE_EQUIPO = "nombre_equipo";


    private String ticket_id;
    private String descripcion;
    private String nombre_usuario;
    private String nombre_depto;
    private String nombre_proceso;
    private String nombre_rol;
    private String fecha;
    private String estatus;
    private String fecha_resolucion;
    private String nombre_linea;
    private String nombre_equipo;



    public static ScreenSlidePageFragment newInstance
            (String idTicket, String descripcion,
             String nombre_usuario, String nombre_depto,
             String nombre_proceso, String nombre_rol,
             String fecha, String estatus, String fecha_resolucion,
             String nombre_linea, String nombre_equipo)
    {
        ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();

        Bundle bundle = new Bundle();
        bundle.putString(TICKET_ID,idTicket);
        bundle.putString(DESCRIPCION, descripcion);
        bundle.putString(NOMBRE_USUARIO, nombre_usuario);
        bundle.putString(NOMBRE_DEPTO, nombre_depto);
        bundle.putString(NOMBRE_PROCESO, nombre_proceso);
        bundle.putString(NOMBRE_ROL, nombre_rol);
        bundle.putString(FECHA, fecha);
        bundle.putString(ESTATUS, estatus);
        bundle.putString(FECHA_RESOLUCION, fecha_resolucion);
        bundle.putString(NOMBRE_LINEA, nombre_linea);
        bundle.putString(NOMBRE_EQUIPO, nombre_equipo);
        fragment.setArguments(bundle);
        fragment.setRetainInstance(true);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        // asignamos valores a los atributos
        this.ticket_id = getArguments().getString(TICKET_ID);
        this.descripcion = getArguments().getString(DESCRIPCION);
        this.nombre_usuario = getArguments().getString(NOMBRE_USUARIO);
        this.nombre_depto = getArguments().getString(NOMBRE_DEPTO);
        this.nombre_proceso = getArguments().getString(NOMBRE_PROCESO);
        this.nombre_rol = getArguments().getString(NOMBRE_ROL);
        this.fecha = getArguments().getString(FECHA);
        this.estatus = getArguments().getString(ESTATUS);
        this.fecha_resolucion = getArguments().getString(FECHA_RESOLUCION);
        this.nombre_linea = getArguments().getString(NOMBRE_LINEA);
        this.nombre_equipo = getArguments().getString(NOMBRE_EQUIPO);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle saveInstanceState)
    {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_screen_slide_page, container, false
        );

        TextView txtIdTicket = (TextView) rootView.findViewById(R.id.textId);
        txtIdTicket.setText("Ticket id: "+this.ticket_id);

        TextView txtDescripcion = (TextView) rootView.findViewById(R.id.textDescripcion);
        txtDescripcion.setText("Descripción: "+this.descripcion);


        TextView txtNombreUsuario = (TextView) rootView.findViewById(R.id.nombreUsuario);
        txtNombreUsuario.setText("Nombre usuario: "+this.nombre_usuario);

        TextView txtNombreDepto = (TextView) rootView.findViewById(R.id.nombreDepto);
        txtNombreDepto.setText("Nombre departamento: "+this.nombre_depto);

        TextView txtNombreProceso = (TextView) rootView.findViewById(R.id.nombreproceso);
        txtNombreProceso.setText("Nombre proceso: "+this.nombre_proceso);

        TextView txtNombreRol = (TextView) rootView.findViewById(R.id.nombreRol);
        txtNombreRol.setText("Nombre rol: "+this.nombre_rol);

        TextView fecha = (TextView) rootView.findViewById(R.id.fecha);
        fecha.setText("Fecha: "+this.fecha);

        TextView status = (TextView) rootView.findViewById(R.id.status);
        status.setText("Estatus: "+this.estatus);

        TextView txtFechaResolucion = (TextView) rootView.findViewById(R.id.fechaResolucion);
        txtFechaResolucion.setText("Fecha resolución: "+this.fecha_resolucion);

        TextView txtNombreLinea = (TextView) rootView.findViewById(R.id.nombreLinea);
        txtNombreLinea.setText("Nombre linea: "+nombre_linea);

        TextView txtNombreEuipo = (TextView) rootView.findViewById(R.id.nombreEquipo);
        txtNombreEuipo.setText("Nombre equipo: "+this.nombre_equipo);

        return rootView;

    }




}
