package com.example.beevolut;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;

import java.sql.Connection;
import java.sql.Statement;

public class MainActivity extends AppCompatActivity {

    private EditText inputScanner;
    private TextView txtStatus;
    private Button btnCamera;

    private Button btnHistorico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputScanner = findViewById(R.id.inputScanner);
        txtStatus = findViewById(R.id.txtStatus);
        btnCamera = findViewById(R.id.btnCamera);
        btnHistorico = findViewById(R.id.btnHistorico);
        btnHistorico.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(MainActivity.this, HistoricoActivity.class);
            startActivity(intent);
        });

        inputScanner.setShowSoftInputOnFocus(false);
        inputScanner.requestFocus();

        inputScanner.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {

                String codigoLido = inputScanner.getText().toString().trim();

                if (!codigoLido.isEmpty()) {
                    txtStatus.setText("Status: A processar leitura...");
                    txtStatus.setTextColor(0xFFFFA500);
                    enviarParaBaseDeDados(codigoLido);
                    inputScanner.setText("");
                    inputScanner.requestFocus();
                }
                return true;
            }
            return false;
        });

        btnCamera.setOnClickListener(v -> {
            GmsBarcodeScannerOptions options = new GmsBarcodeScannerOptions.Builder()
                    .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                    .enableAutoZoom()
                    .build();

            GmsBarcodeScanner scanner = GmsBarcodeScanning.getClient(this, options);

            scanner.startScan()
                    .addOnSuccessListener(barcode -> {
                        String codigoLido = barcode.getRawValue();
                        txtStatus.setText("Status: A processar leitura da câmara...");
                        txtStatus.setTextColor(0xFFFFA500);
                        enviarParaBaseDeDados(codigoLido);
                    })
                    .addOnFailureListener(e -> {
                        txtStatus.setText("Status: Erro na leitura ou cancelada");
                        txtStatus.setTextColor(0xFFFF0000);
                    });
        });
    }

    private void enviarParaBaseDeDados(String idProduto) {
        new Thread(() -> {
            try {
                Connection conn = DatabaseHelper.getConnection();

                if (conn != null) {
                    String sql = "INSERT INTO movimentacao (id_produto, tipo_movimentacao, horario) VALUES (" + idProduto + ", 'Entrada', GETDATE())";

                    Statement stmt = conn.createStatement();
                    stmt.executeUpdate(sql);
                    conn.close();

                    runOnUiThread(() -> {
                        txtStatus.setText("Status: Registo guardado com sucesso!");
                        txtStatus.setTextColor(0xFF008000); // Verde
                        Toast.makeText(MainActivity.this, "Inserido no banco!", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    runOnUiThread(() -> {
                        txtStatus.setText("Status: Falha ao ligar ao SQL Server");
                        txtStatus.setTextColor(0xFFFF0000);
                    });
                }
            } catch (Exception e) {
                final String motivoErro = e.getMessage();
                e.printStackTrace();
                runOnUiThread(() -> {
                    txtStatus.setText("Erro: " + motivoErro);
                    txtStatus.setTextColor(0xFFFF0000);
                });
            }
        }).start();
    }
}