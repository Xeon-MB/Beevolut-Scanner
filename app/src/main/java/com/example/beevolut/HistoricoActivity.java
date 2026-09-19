package com.example.beevolut;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;

public class HistoricoActivity extends AppCompatActivity {

    private TextView txtListaHistorico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico);

        txtListaHistorico = findViewById(R.id.txtListaHistorico);
        carregarHistorico();
    }

    private void carregarHistorico() {
        new Thread(() -> {
            try {
                Connection conn = DatabaseHelper.getConnection();
                if (conn != null) {
                    String sql = "SELECT TOP 10 p.nome_produto, m.horario, m.tipo_movimentacao " +
                            "FROM movimentacao m " +
                            "INNER JOIN produto p ON m.id_produto = p.id_produto " +
                            "ORDER BY m.id_movimentacao DESC";

                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(sql);

                    StringBuilder construtorTexto = new StringBuilder();
                    SimpleDateFormat formatoHora = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

                    while (rs.next()) {
                        String nome = rs.getString("nome_produto");
                        String tipo = rs.getString("tipo_movimentacao");
                        String horario = formatoHora.format(rs.getTimestamp("horario"));

                        construtorTexto.append("📦 ").append(nome).append("\n");
                        construtorTexto.append("Tipo: ").append(tipo).append(" | Hora: ").append(horario).append("\n\n");
                    }

                    conn.close();

                    runOnUiThread(() -> {
                        if (construtorTexto.length() == 0) {
                            txtListaHistorico.setText("Nenhuma movimentação registada ainda.");
                        } else {
                            txtListaHistorico.setText(construtorTexto.toString());
                        }
                    });
                } else {
                    runOnUiThread(() -> txtListaHistorico.setText("Erro: Não foi possível ligar ao servidor."));
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> txtListaHistorico.setText("Erro ao consultar a base de dados:\n" + e.getMessage()));
            }
        }).start();
    }
}