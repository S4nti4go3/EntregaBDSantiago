package com.estudio.entregabdsantiago;


import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import androidx.appcompat.app.AppCompatActivity;

public class ConsultaActivity extends AppCompatActivity {
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta);

        dbHelper = new DBHelper(this);

        // Consultar todos los datos de la base de datos
        Cursor cursor = dbHelper.consultarTodosLosUsuarios();

        // Configurar el adaptador para mostrar los datos en un ListView
        String[] fromColumns = {DBHelper.COL_CEDULA, DBHelper.COL_NOMBRE, DBHelper.COL_TELEFONO, DBHelper.COL_SOLICITUD};
        int[] toViews = {R.id.cedulaTextView, R.id.nombreTextView, R.id.telefonoTextView, R.id.solicitudTextView};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.item_usuario, cursor, fromColumns, toViews, 0);

        // Mostrar los datos en el ListView
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);
    }
}