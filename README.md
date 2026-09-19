# JTEKT — Integração Logística

[![Java](https://img.shields.io/badge/Java-Android-orange?logo=java)](https://www.java.com/)
[![Android](https://img.shields.io/badge/Android-SDK-green?logo=android)](https://developer.android.com/)
[![SQL Server](https://img.shields.io/badge/SQL%20Server-Database-red?logo=microsoftsqlserver)](https://www.microsoft.com/sql-server)
[![JDBC](https://img.shields.io/badge/JDBC-jTDS-blue)](https://sourceforge.net/projects/jtds/)

Prova de Conceito (PoC) de uma aplicação Android desenvolvida em Java para registar a entrada de paletes através da leitura de QR Codes. A aplicação comunica diretamente com uma base de dados SQL Server por meio do driver JDBC **jTDS**.

> **Contexto:** SENAI — Projeto JTEKT  
> **Arquitetura:** Android → SQL Server

## Índice

- [O que o projeto faz](#o-que-o-projeto-faz)
- [Por que o projeto é útil](#por-que-o-projeto-é-útil)
- [Arquitetura da solução](#arquitetura-da-solução)
- [Tecnologias utilizadas](#tecnologias-utilizadas)
- [Validação da Prova de Conceito](#validação-da-prova-de-conceito)
- [Limitações e segurança](#limitações-e-segurança)
- [Resolução de problemas](#resolução-de-problemas)
- [Manutenção e contribuições](#manutenção-e-contribuições)

## O que o projeto faz

O sistema permite registar a entrada de paletes no armazém por meio da leitura ótica de QR Codes usando a câmara de um telemóvel Android.

Após a leitura, a aplicação:

1. Identifica o código associado ao produto ou à palete.
2. Consulta os dados necessários na base de dados.
3. Regista a movimentação no SQL Server.
4. Permite verificar o registo diretamente no banco de dados.

A leitura dos códigos é realizada com a biblioteca **ZXing**, enquanto a comunicação com o SQL Server utiliza JDBC através do driver **jTDS**.

## Por que o projeto é útil

A solução centraliza o registo das movimentações de paletes e permite consultar os dados no SQL Server após a operação.

A PoC demonstra a integração direta entre um dispositivo Android e um banco de dados relacional, reduzindo a necessidade de componentes intermediários durante a validação do conceito.

## Arquitetura da solução

```text
+------------------------+
| Telemóvel Android      |
| Aplicação Java         |
| ZXing — leitura QR     |
+-----------+------------+
            |
            | JDBC / jTDS
            | Rede local
            v
+------------------------+
| SQL Server             |
| Base: Beevolut         |
|                        |
|  - produto             |
|  - movimentacao        |
+------------------------+
```

## Tecnologias utilizadas

| Tecnologia | Função |
|---|---|
| Java | Desenvolvimento da aplicação Android |
| Android | Plataforma de execução da aplicação |
| ZXing | Leitura e interpretação dos QR Codes |
| SQL Server | Armazenamento dos produtos e movimentações |
| JDBC / jTDS | Comunicação entre a aplicação e o SQL Server |

## Validação da Prova de Conceito

A validação da PoC deve seguir o fluxo funcional abaixo:

**Leitura do QR Code → Identificação do produto → Registo da movimentação → Confirmação no SQL Server**

| Teste | Resultado esperado | Evidência |
|---|---|---|
| Leitura de QR Code válido | O código é reconhecido pela aplicação | Leitura apresentada no telemóvel |
| Identificação do produto | O produto correspondente é localizado no banco de dados | Dados do produto apresentados ou utilizados no registo |
| Registo de entrada | Uma nova movimentação é criada | Novo registo na tabela `movimentacao` |
| Consulta da movimentação | Os dados permanecem armazenados no SQL Server | Consulta SQL com o registo criado |
| QR Code inválido ou inexistente | A aplicação impede ou informa a impossibilidade do registo | Mensagem de validação apresentada |
| Produto inexistente | A integridade referencial é preservada | Registo rejeitado ou erro tratado pela aplicação |

### Teste

QR Codes para teste da aplicação: 

| Produto 1 | Produto 2 | Produto 3 |
| :---: | :---: | :---: |
| <img width="200" height="200" alt="QR Code 1" src="https://github.com/user-attachments/assets/ab0b05a4-9819-47a1-b7dc-7df36e57a125" /> | <img width="200" height="200" alt="QR Code 2" src="https://github.com/user-attachments/assets/5e76e32e-7c28-4590-83a3-e33194d7f572" /> | <img width="200" height="200" alt="QR Code 3" src="https://github.com/user-attachments/assets/556e93c4-6644-458e-92d1-248261e6ee19" /> |


## Manutenção e contribuições

Este projeto foi desenvolvido no contexto académico do **SENAI — Projeto JTEKT**, com foco na validação de uma solução de integração logística entre Android e SQL Server.

### Autoria

- **Projeto:** Integração Logística JTEKT
- **Contexto:** Projeto académico do SENAI
- **Tecnologias:** Java, Android, ZXing, SQL Server e JDBC/jTDS

### Contribuições

Alterações futuras devem ser testadas na aplicação e verificadas no banco de dados antes de serem integradas ao projeto.

As alterações no schema SQL, nas consultas ou no fluxo de registo devem ser documentadas para manter a reprodutibilidade da PoC.



