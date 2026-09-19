package com.example.beevolut;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import androidx.activity.result.ActivityResultLauncher;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.sql.Connection;
import java.sql.Statement;

public class MainActivity extends AppCompatActivity {

    private EditText inputScanner;
    private TextView txtStatus;
    private Button btnCamera;


    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if(result.getContents() != null) {
                    String codigoLido = result.getContents();

                    txtStatus.setText("Status: A processar leitura da câmera...");
                    txtStatus.setTextColor(0xFFFFA500);

                    enviarParaBaseDeDados(codigoLido);
                } else {
                    Toast.makeText(MainActivity.this, "Leitura cancelada", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputScanner = findViewById(R.id.inputScanner);
        txtStatus = findViewById(R.id.txtStatus);
        btnCamera = findViewById(R.id.btnCamera);

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
            ScanOptions options = new ScanOptions();
            options.setPrompt("Aponte para o QR Code do Palete");
            options.setBeepEnabled(true);
            options.setOrientationLocked(false);
            options.setCaptureActivity(com.journeyapps.barcodescanner.CaptureActivity.class);


            barcodeLauncher.launch(options);
        });
    }

    private void enviarParaBaseDeDados(String idProduto) {
        new Thread(() -> {
            try {
                Connection conn = DatabaseHelper.getConnection();

                if (conn != null) {
                    String sql = "INSERT INTO Beevolut.dbo.movimentacoes (id_produto, tipo_movimentacao, horario) VALUES (" + idProduto + ", 'Entrada', GETDATE())";

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