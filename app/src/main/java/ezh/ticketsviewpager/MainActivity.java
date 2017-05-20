package ezh.ticketsviewpager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity
{
    ListView listView;
    SQLHelper sqlHelper;
    SQLiteDatabase db;
    ArrayList<String> items_list;

    RequestQueue requestQueue;
    ProgressDialog pDialog;
    MiTareaAsincronaDialog tarea2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e("EZH","onCreate 1");
        listView = (ListView) findViewById(R.id.list);

        // Validamos que haya internet para poder hacer la petición al json
        if (accesoARed())
        {
            Log.e("EZH","onCreate 2");
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setMessage("Procesando...");
            pDialog.setCancelable(true);
            pDialog.setMax(100);
            Log.e("EZH","onCreate 3");

            // creamos una instancia de la clase Asincrona
            tarea2 = new MiTareaAsincronaDialog();
            tarea2.execute();

            Log.e("EZH","onCreate 4");
        }
        else
        {
            Log.e("EZH","onCreate 5");
            // Verificar si hay registros en la base de datos para mostrarlos
            if (getNumberRows() > 0)
            {
                Log.e("EZH","onCreate 6");

                cargar_list_view();

                Log.e("EZH","onCreate 7");
            }
            else
            {
                Log.e("EZH","onCreate 8");
                Toast.makeText(getApplicationContext(),"Sin acceso a la red!!", Toast.LENGTH_LONG).show();
            }


        }

        Log.e("EZH","onCreate 2");

        // Agregamos evento a los items del listView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                // Obtenemos elemento del list view de acuerdo a la posición
                String item = (String) listView.getItemAtPosition(position);
                // Creamos un nuevo StringTokenizer para separar el item por -
                StringTokenizer st = new StringTokenizer(item,"-");

                String idTicket = st.nextToken(); // Ticket: numero
                String ticketId = idTicket.substring(8,idTicket.length());// Obtenemos el id del ticket

                // Nos movemos a la actividad de detalles
                Intent intent = new Intent(getApplicationContext(),DetalleTicket.class);
                // Pasamos como parametro el id del ticket
                intent.putExtra("ticket",ticketId);
                // Asignamos el tipo de lanzamiento de la activity
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                // Iniciamos la activity
                startActivity(intent);


            }
        });
    }
    private class MiTareaAsincronaDialog extends AsyncTask<String, Integer, Boolean>
    {

        @Override
        protected Boolean doInBackground(String... params)
        {
            Log.e("EZH","doInBackground 1");
            // llamada al metodo petición server
            peticion_server();
            Log.e("EZH","doInBackground 2");

            for (int i = 1; i <= 10; i++)
            {
                try
                {
                    Thread.sleep(1000);

                    publishProgress(i*10);
                }
                catch (InterruptedException e)
                {
                    Log.e("EZH","Error: "+e.getMessage());
                }

            }

            return true;
        }
        @Override
        protected void onPreExecute()
        {
            pDialog.setProgress(0);
            pDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... values)
        {
            int progreso = values[0].intValue();

            pDialog.setProgress(progreso);
        }

        @Override
        protected void onPostExecute(Boolean result)
        {
            Log.e("EZH","onPostExecute 1");
            if (result)
            {
                Log.e("EZH","onPostExecute 2");

                cargar_list_view();

                Toast.makeText(getApplicationContext(), "Petición finalizada", Toast.LENGTH_SHORT).show();

                pDialog.dismiss();
                Log.e("EZH","onPostExecute 3");
            }

        }

    }

    public void peticion_server()
    {
        // Url del json
        String url = "http://chatmano3.azurewebsites.net/api/ticketslistadopendientesporusuario/1";
        // creamos objeto request de Volley

        Log.e("EZH","peticion_server 0");

        // Preparamos request
        requestQueue = Volley.newRequestQueue(this);

        Log.e("EZH","peticion_server 1");

        // Creamos un nuevo StringRequest
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        Log.e("EZH","peticion_server 2");
                        // Verificar si el numero de objetos json es mayor al numero de registros en la base de datos
                        try
                        {
                            Log.e("EZH","peticion_server 3");
                            // Creamos un nuevo JSONArray apartir de la respuesta
                            JSONArray jsonArray = new JSONArray(response);
                            // Llamamos al metodo getNumberRows que devuelve el total de registros en la base de datos
                            int rows = getNumberRows();

                            Log.e("EZH","peticion_server 4");
                            Log.e("EZH","peticion_server length jsonArray: "+jsonArray.length());

                            if (jsonArray.length() >= rows)
                            {
                                Log.e("EZH","peticion_server 5");
                                // Registrar apartir del numero de rows hasra el maximo tamaño del jsonArray
                                for (int i = rows; i < jsonArray.length(); i++)
                                {
                                    // Agregamos los nuevos elementos a la base de datos
                                    // Obtenemos un JSONObject apartir del JSONArray
                                    JSONObject item = jsonArray.getJSONObject(i);
                                    // llamamos al metodo insert_bd
                                    insert_bd(item);
                                }
                            }
                            else
                            {
                                Log.e("EZH","peticion_server 6");
                                // eleminar los registros de la base de datos y descargar los que tenga el jsonArray
                                // llamamos al metodo delete_table

                                delete_table();
                                Log.e("EZH","peticion_server 7");
                                for (int i = 0; i < jsonArray.length(); i++)
                                {
                                    // Obtenemos un JSONObject apartir del JSONArray
                                    JSONObject item = jsonArray.getJSONObject(i);

                                    insert_bd(item);

                                }
                                Log.e("EZH","peticion_server 8");
                            }

                        }
                        catch (JSONException e)
                        {
                            Log.e("EZH","ERROR JSON: "+e.getMessage());
                        }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError e)
                    {
                        Log.e("EZH","ERROR Volley: "+e.getMessage());
                    }
                }
        );
        // Agregamos al request la petición StringRequest
        requestQueue.add(stringRequest);


    }

    public void insert_bd(JSONObject item)
    {
        try
        {
            Log.e("EZH","insert_bd 1");
            // Creamos una nueva instancia a la base de datos
            sqlHelper = new SQLHelper(getApplicationContext());
            // Obtenemos la escritura de la base de datos
            db = sqlHelper.getWritableDatabase();

            Log.e("EZH","insert_bd 2");

            String ticketId = item.getString("IdTicket");
            String descripcion = item.getString("Descripcion");
            String usuarioId = item.getString("IdUsuario");
            String nombreUsuario = item.getString("NombreCompleto");
            String departamentoId = item.getString("IdDepartamento");
            String nombreDepartamento = item.getString("NombreDepartamento");
            String accionDepartamento = item.getString("IdAccionDepartamento");
            String nombreAccionDepto = item.getString("NombreAccionDepartamento");
            String nombreProceso = item.getString("NombreProceso");
            String nombreRol = item.getString("NombreRol");
            String turnoId = item.getString("IdTurno");
            String fecha = item.getString("Fecha");
            String status = item.getString("Estatus");
            String fechaResolucion = item.getString("FechaResolucion");
            String lineaId = item.getString("IdLinea");
            String nombreLinea = item.getString("LineaNombre");
            String equipoId = item.getString("IdEquipo");
            String nombreEquipo = item.getString("EquipoNombre");
            String fechaMesAnio = item.getString("StringFechaMesAnio");
            String seccion = item.getString("Seccion");
            String numeroMes = item.getString("NumeroMes");

            Log.e("EZH","insert_bd 3");

            // Insertamos en la tabla ticket
            db.execSQL("insert into ticket values('"+ticketId+"', '"+descripcion+"', '"+usuarioId+"', " +
                    "'"+nombreUsuario+"', '"+departamentoId+"', '"+nombreDepartamento+"', '"+accionDepartamento+"'," +
                    "'"+nombreAccionDepto+"', '"+nombreProceso+"', '"+nombreRol+"', '"+turnoId+"', '"+fecha+"'," +
                    "'"+status+"', '"+fechaResolucion+"', '"+lineaId+"', '"+nombreLinea+"', '"+equipoId+"', '"+nombreEquipo+"'," +
                    "'"+fechaMesAnio+"', '"+seccion+"', '"+numeroMes+"')");
            Log.e("EZH","insert_bd 4");

            db.close();

            Log.e("EZH","insert_bd 5");
        }
        catch (JSONException e)
        {
            Log.e("EZH","Error JSON: "+e.getMessage());
        }
    }

    public int getNumberRows()
    {
        Log.e("EZH","getNumberRows 1");
        // Creamos una nueva instancia a la base de datos
        sqlHelper = new SQLHelper(getApplicationContext());
        // Obtenemos la escritura de la base de datos
        db = sqlHelper.getWritableDatabase();
        Log.e("EZH","getNumberRows 2");
        int rows = 0;

        // Consulta
        String consulta = "SELECT * FROM ticket";

        // Creamos un cursor
        Cursor cursor = db.rawQuery(consulta, null);
        Log.e("EZH","getNumberRows 3");

        if (cursor.moveToFirst())
        {
            Log.e("EZH","getNumberRows 4");

            do
            {
                rows++;
            }
            while (cursor.moveToNext());

        }
        Log.e("EZH","getNumberRows 5");
        // Cerramos conexion a la base de datos
        db.close();

        Log.e("EZH","getNumberRows 6");
        Log.e("EZH","getNumberRows rows: "+rows);

        return rows;
    }
    public void delete_table()
    {
        // eliminamos los datos de la tabla ticket
        sqlHelper = new SQLHelper(getApplicationContext());

        Log.e("EZH","delete_table 1");

        db = sqlHelper.getWritableDatabase();

        Log.e("EZH","delete_table 2");
        db.execSQL("delete from ticket");

        Log.e("EZH","delete_table 3");

        db.close();

        Log.e("EZH","delete_table 4");
    }

    public void cargar_list_view()
    {
        items_list = new ArrayList<String>();
        Log.e("EZH","cargar_list_view 1");
        // Consultamos la base de datos
        sqlHelper = new SQLHelper(getApplicationContext());
        Log.e("EZH","cargar_list_view 2");
        db = sqlHelper.getWritableDatabase();
        Log.e("EZH","cargar_list_view 3");
        String consulta = "select IdTicket, Descripcion from ticket";

        try
        {
            Cursor c = db.rawQuery(consulta,null);
            Log.e("EZH","cargar_list_view 4");
            if (c.moveToFirst())
            {
                Log.e("EZH","cargar_list_view 5");
                do
                {
                    String tickte = c.getString(0);
                    String desc = c.getString(1);

                    items_list.add("Ticket: " + tickte + "-" + "Descripción: " + desc);

                    Log.e("EZH","Ticket: " + tickte + "-" + "Descripción: " + desc);
                }
                while (c.moveToNext());
                Log.e("EZH","cargar_list_view 6");
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_list_item_1,
                        items_list
                );
                Log.e("EZH","cargar_list_view 7");
                listView.setAdapter(adapter);
                Log.e("EZH","cargar_list_view 8");
            }
            else
            {
                Log.e("EZH","cargar_list_view 9");
                Toast.makeText(getApplicationContext(),"Sin registros", Toast.LENGTH_LONG).show();
            }

            db.close();
        }
        catch (Exception e)
        {
            Log.e("EZH","Error cargar listView: "+e.getMessage());
        }
    }

    public boolean accesoARed()
    {
        Log.e("EZH","accesoARed 1");
        ConnectivityManager cm;
        NetworkInfo ni;
        cm = (ConnectivityManager) this.getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        ni = cm.getActiveNetworkInfo();
        boolean tipoConexion1 = false;
        boolean tipoConexion2 = false;
        boolean respuesta = false;
        Log.e("EZH","accesoARed 2");
        if (ni != null)
        {
            Log.e("EZH","accesoARed 3");
            ConnectivityManager connManager1 = (ConnectivityManager) this.getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
            NetworkInfo mWifi = connManager1.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            ConnectivityManager connManager2 = (ConnectivityManager) this.getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
            NetworkInfo mMobile = connManager2.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            Log.e("EZH","accesoARed 4");
            if (mWifi.isConnected())
            {
                tipoConexion1 = true;
                Log.e("EZH","accesoARed 5");
            }
            if (mMobile.isConnected())
            {
                tipoConexion2 = true;
                Log.e("EZH","accesoARed 6");
            }

            if (tipoConexion1 == true || tipoConexion2 == true)
            {
               /* Estas conectado a internet usando wifi o redes moviles, puedes enviar tus datos */
                respuesta = true;
                Log.e("EZH","accesoARed 7");
            }
        }
        else
        {
       /* No estas conectado a internet */

            respuesta =  false;
            Log.e("EZH","accesoARed 8");
        }
        Log.e("EZH","accesoARed 9 respuesta "+respuesta);
        return respuesta;
    }


}
