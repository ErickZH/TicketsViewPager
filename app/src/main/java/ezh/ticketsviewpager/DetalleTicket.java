package ezh.ticketsviewpager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class DetalleTicket extends AppCompatActivity
{
    ViewPager pager = null;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    MyFragmentPagerAdapter pagerAdapter;

    SQLHelper sqlHelper;
    SQLiteDatabase db;

    ArrayList<String> ids;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_ticket);

        // Instantiate a ViewPager
        pager = (ViewPager) this.findViewById(R.id.pager);

        // Create an adapter with the fragments we show on the ViewPager

        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(
                getSupportFragmentManager());

        cargar_fragments(adapter);

        this.pager.setAdapter(adapter);

        pager.setCurrentItem(getIndex());

    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }

    public int getIndex()
    {
        // Obtenemos el id del ticket seleccionado
        String idTicket = (String)getIntent().getExtras().getSerializable("ticket");
        int i = 0;
        for (String s : ids)
        {
            if (s.equals(idTicket))
            {
                break;
            }

            i++;
        }
        return i;
    }

    public void cargar_fragments(MyFragmentPagerAdapter adapter)
    {
        ids = new ArrayList<String>();
        // Consultamos la base de datos
        sqlHelper = new SQLHelper(getApplicationContext());
        db = sqlHelper.getWritableDatabase();

        String consulta = "SELECT IdTicket, Descripcion, NombreCompleto, NombreDepartamento, " +
                "NombreProceso, NombreRol,  Fecha, Estatus, FechaResolucion,  LineaNombre, EquipoNombre" +
                " FROM ticket";

        Cursor cursor = db.rawQuery(consulta, null);

        if (cursor.moveToFirst())
        {
            do
            {
                String id = cursor.getString(0);
                String desc = cursor.getString(1);
                String nomUser = cursor.getString(2);
                String nomDepto = cursor.getString(3);
                String nomProceso = cursor.getString(4);
                String nomRol = cursor.getString(5);
                String fecha = cursor.getString(6);
                String estatus = cursor.getString(7);
                String fecha_resolucion = cursor.getString(8);
                String linea_nombre = cursor.getString(9);
                String nombre_equipo = cursor.getString(10);

                ids.add(id);

                adapter.addFragment(ScreenSlidePageFragment.newInstance(id, desc, nomUser, nomDepto, nomProceso, nomRol,
                        fecha, estatus, fecha_resolucion, linea_nombre, nombre_equipo));

            }
            while(cursor.moveToNext());
        }

    }
}
