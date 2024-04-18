package com.estudio.entregabdsantiago;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "usuarios.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_USUARIO = "usuario";
    public static final String COL_ID = "_id";
    public static final String COL_CEDULA = "cedula";
    public static final String COL_NOMBRE = "nombre";
    public static final String COL_TELEFONO = "telefono";
    public static final String COL_SOLICITUD = "solicitud";

    private static final String CREATE_TABLE_USUARIO = "CREATE TABLE " + TABLE_USUARIO + " (" +
            COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COL_CEDULA + " TEXT," +
            COL_NOMBRE + " TEXT," +
            COL_TELEFONO + " TEXT," +
            COL_SOLICITUD + " TEXT)";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USUARIO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIO);
        onCreate(db);
    }

    public long insertarUsuario(String cedula, String nombre, String telefono, String solicitud) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_CEDULA, cedula);
        values.put(COL_NOMBRE, nombre);
        values.put(COL_TELEFONO, telefono);
        values.put(COL_SOLICITUD, solicitud);
        long resultado = db.insert(TABLE_USUARIO, null, values);
        db.close();
        return resultado;
    }

    public Cursor consultarUsuario(String cedula) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {COL_NOMBRE, COL_TELEFONO, COL_SOLICITUD};
        String selection = COL_CEDULA + " = ?";
        String[] selectionArgs = {cedula};
        return db.query(TABLE_USUARIO, projection, selection, selectionArgs, null, null, null);
    }

    public Cursor consultarTodosLosUsuarios() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {COL_ID, COL_CEDULA, COL_NOMBRE, COL_TELEFONO, COL_SOLICITUD};
        return db.query(TABLE_USUARIO, projection, null, null, null, null, null);
    }

    public void eliminarTodosLosDatos(MainActivity mainActivity) {
        SQLiteDatabase database = this.getWritableDatabase();

        // Ejecutar la consulta SQL para eliminar todos los datos de la tabla
        database.execSQL("DELETE FROM " + TABLE_USUARIO);

        // Cerrar la conexión con la base de datos después de realizar la operación
        database.close();
    }

    public void eliminarPorCedula(String cedula) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("usuario", "cedula=?", new String[]{cedula});
        db.close();
    }
    public boolean existeCedula(String cedula) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] parametros = {cedula};
        Cursor cursor = db.rawQuery("SELECT * FROM usuario WHERE cedula=?", parametros);
        boolean existe = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return existe;
    }

    public int obtenerTotalUsuarios() {
        SQLiteDatabase db = this.getReadableDatabase();
        int count = 0;
        if (db != null) {
            Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_USUARIO, null);
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        return count;
    }
}