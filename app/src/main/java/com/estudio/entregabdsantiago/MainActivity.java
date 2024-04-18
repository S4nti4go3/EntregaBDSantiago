package com.estudio.entregabdsantiago;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private EditText cedulaEditText, nombreEditText, telefonoEditText, solicitudEditText;
    private TextView resultTextView;
    private Button registrarButton, consultarButton, verTodosButton, btnEliminarTodos, botonBorrarPorCedula;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar vistas
        cedulaEditText = findViewById(R.id.cedulaEditText);
        nombreEditText = findViewById(R.id.nombreEditText);
        telefonoEditText = findViewById(R.id.telefonoEditText);
        solicitudEditText = findViewById(R.id.solicitudEditText);
        registrarButton = findViewById(R.id.registrarButton);
        consultarButton = findViewById(R.id.consultarButton);
        verTodosButton = findViewById(R.id.verTodosButton);
        btnEliminarTodos = findViewById(R.id.btnEliminarTodos);
        resultTextView = findViewById(R.id.resultTextView);
        botonBorrarPorCedula = findViewById(R.id.botonBorrarPorCedula);

        // Inicializar DBHelper
        dbHelper = new DBHelper(this);

        // Configurar botón para registrar
        registrarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrarUsuario();
            }
        });

        // Configurar botón para consultar
        consultarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (usuarioRegistrado()) {
                    consultarUsuario(view);
                } else {
                    Toast.makeText(MainActivity.this, "Debe registrar un usuario primero", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Configurar botón para ver todos los datos
        verTodosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verTodosLosDatos();
            }
        });

        btnEliminarTodos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.eliminarTodosLosDatos(MainActivity.this);
                limpiarCampos();
                Toast.makeText(MainActivity.this, "Todos los datos han sido eliminados", Toast.LENGTH_SHORT).show();
            }
        });

        botonBorrarPorCedula.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener la cédula ingresada por el usuario desde un EditText
                String cedulaABorrar = cedulaEditText.getText().toString();
                // Verificar que la cédula no esté vacía antes de intentar borrar
                if (!cedulaABorrar.isEmpty()) {
                    // Verificar si la cédula está registrada en la base de datos
                    if (dbHelper.existeCedula(cedulaABorrar)) {
                        // Llamar al método eliminarPorCedula del DBHelper para borrar los datos
                        dbHelper.eliminarPorCedula(cedulaABorrar);
                        limpiarCampos();
                        Toast.makeText(MainActivity.this, "Datos eliminados", Toast.LENGTH_SHORT).show();
                    } else {
                        // Mostrar un mensaje al usuario indicando que la cédula no está registrada
                        Toast.makeText(MainActivity.this, "La cédula no está registrada", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Mostrar un mensaje al usuario indicando que la cédula está vacía
                    Toast.makeText(MainActivity.this, "Por favor ingresa una cédula", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void registrarUsuario() {
        // Obtener los datos ingresados por el usuario
        String cedula = cedulaEditText.getText().toString();
        String nombre = nombreEditText.getText().toString();
        String telefono = telefonoEditText.getText().toString();
        String solicitud = solicitudEditText.getText().toString();

        // Verificar si algún campo está vacío
        if (!TextUtils.isEmpty(cedula) && !TextUtils.isEmpty(nombre) && !TextUtils.isEmpty(telefono) && !TextUtils.isEmpty(solicitud)) {
            // Insertar los datos en la base de datos
            long resultado = dbHelper.insertarUsuario(cedula, nombre, telefono, solicitud);

            // Verificar si se insertaron correctamente los datos
            if (resultado != -1) {
                Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();
                // Limpiar campos después de registrar
                limpiarCampos();

                // Abrir la actividad para ver todos los datos
                Intent intent = new Intent(MainActivity.this, ConsultaActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Error al registrar usuario", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Mostrar mensaje de error si algún campo está vacío
            Toast.makeText(this, "Por favor ingrese todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    private void consultarUsuario(View view){
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        String document = cedulaEditText.getText().toString();

        if(!document.isEmpty()){
            Cursor cursor = database.rawQuery("SELECT " + DBHelper.COL_NOMBRE + ", " + DBHelper.COL_TELEFONO + ", " + DBHelper.COL_SOLICITUD + " FROM " + DBHelper.TABLE_USUARIO + " WHERE " + DBHelper.COL_CEDULA + " = ?", new String[]{document});
            if (cursor.moveToFirst()){
                // Asignar los datos de consulta al TextView
                String resultado = "Nombre: " + cursor.getString(0) + "\nTeléfono: " + cursor.getString(1) + "\nSolicitud: " + cursor.getString(2);
                resultTextView.setText(resultado);
                resultTextView.setVisibility(View.VISIBLE); // Mostrar el TextView
            } else {
                resultTextView.setVisibility(View.GONE); // Ocultar el TextView si no se encontró el usuario
                Toast.makeText(this, "No se encontró el usuario", Toast.LENGTH_SHORT).show();
            }
        } else {
            resultTextView.setVisibility(View.GONE); // Ocultar el TextView si no se ingresó ningún documento de consulta
            Toast.makeText(this, "Ingrese un documento de consulta", Toast.LENGTH_SHORT).show();
        }
    }

    private void verTodosLosDatos() {
        // Obtener el número total de usuarios registrados
        int totalUsuarios = dbHelper.obtenerTotalUsuarios();

        // Mostrar el número total de usuarios registrados en un Toast
        Toast.makeText(MainActivity.this, "Número total de usuarios registrados: " + totalUsuarios, Toast.LENGTH_SHORT).show();

        // Abrir la actividad para ver todos los datos
        Intent intent = new Intent(MainActivity.this, ConsultaActivity.class);
        startActivity(intent);
    }

    private boolean usuarioRegistrado() {
        // Verificar si el usuario ha sido registrado previamente
        String cedula = cedulaEditText.getText().toString();
        Cursor cursor = dbHelper.consultarUsuario(cedula);
        return cursor != null && cursor.moveToFirst();
    }

    private void limpiarCampos() {
        cedulaEditText.setText("");
        nombreEditText.setText("");
        telefonoEditText.setText("");
        solicitudEditText.setText("");
        resultTextView.setText("");
    }
}