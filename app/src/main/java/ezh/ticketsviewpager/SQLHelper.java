package ezh.ticketsviewpager;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLHelper extends SQLiteOpenHelper
{
    public static final String DATABASE_NAME = "BD_Tickets.db";

    public SQLHelper(Context context)
    {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        // Definimos estructura de la tabla ticket
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS ticket (" +
                        "IdTicket TEXT PRIMARY KEY," +
                        "Descripcion TEXT," +
                        "IdUsuario TEXT," +
                        "NombreCompleto TEXT," +
                        "IdDepartamento TEXT," +
                        "NombreDepartamento TEXT," +
                        "IdAccionDepartamento TEXT," +
                        "NombreAccionDepartamento TEXT," +
                        "NombreProceso TEXT," +
                        "NombreRol TEXT," +
                        "IdTurno TEXT," +
                        "Fecha TEXT," +
                        "Estatus TEXT," +
                        "FechaResolucion TEXT," +
                        "IdLinea TEXT," +
                        "LineaNombre TEXT," +
                        "IdEquipo TEXT," +
                        "EquipoNombre TEXT," +
                        "StringFechaMesAnio TEXT," +
                        "Seccion TEXT," +
                        "NumeroMes TEXT)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }
}
