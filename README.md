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

### Consulta de validação

Após executar um teste, a movimentação pode ser verificada com uma consulta semelhante à seguinte:

```sql
USE Beevolut;
GO

SELECT
    m.id_movimentacao,
    p.codigo,
    p.descricao,
    m.tipo,
    m.data_movimentacao
FROM movimentacao AS m
INNER JOIN produto AS p
    ON p.id_produto = m.id_produto
ORDER BY m.id_movimentacao DESC;
```

> **Nota:** os nomes das colunas devem ser ajustados caso o schema utilizado no projeto seja diferente do exemplo acima.

## Limitações e segurança

A comunicação direta entre o Android e o SQL Server foi utilizada como estratégia de validação da PoC.

Essa abordagem simplifica a demonstração da integração, mas apresenta limitações para um ambiente de produção:

- As credenciais do banco de dados não devem ficar expostas na aplicação.
- O utilizador `sa` deve ser utilizado apenas em testes controlados, quando necessário.
- O acesso ao SQL Server deve ser restringido por rede e por permissões.
- Uma API intermediária com autenticação e autorização seria mais adequada para uma implantação real.
- A comunicação deve utilizar mecanismos de proteção, como TLS, quando suportados pela arquitetura adotada.

## Resolução de problemas

### Erro 233 ou falha de autenticação

As causas mais comuns são:

- Login `sa` desativado.
- Palavra-passe incorreta.
- Modo de autenticação do SQL Server incompatível com a configuração utilizada.
- Serviço do SQL Server não reiniciado após alterações.

A validação deve ser feita primeiro no SQL Server Management Studio e, posteriormente, na aplicação Android.

### Erro de Foreign Key

Uma movimentação pode falhar quando o produto associado não existe na tabela `produto`.

A relação entre `movimentacao` e `produto` deve ser preservada para evitar registos sem correspondência. O produto deve ser validado antes da criação da movimentação.

### Falha de conexão ou timeout

Verifique os seguintes pontos:

- Endereço IP e porta configurados no `DatabaseHelper.java`.
- Serviço do SQL Server em execução.
- TCP/IP habilitado no servidor.
- Firewall permitindo a conexão.
- Dispositivo Android com acesso à rede do servidor.
- Permissões do utilizador utilizado na conexão.

### QR Code não reconhecido

Verifique:

- Permissão de acesso à câmara.
- Qualidade, iluminação e foco do código.
- Formato do conteúdo lido.
- Correspondência entre o código do QR Code e os dados existentes no banco de dados.
- Configuração da biblioteca ZXing.

## Manutenção e contribuições

Este projeto foi desenvolvido no contexto académico do **SENAI — Projeto JTEKT**, com foco na validação de uma solução de integração logística entre Android e SQL Server.

### Autoria

- **Projeto:** Integração Logística JTEKT
- **Contexto:** Projeto académico do SENAI
- **Tecnologias:** Java, Android, ZXing, SQL Server e JDBC/jTDS

### Contribuições

Alterações futuras devem ser testadas na aplicação e verificadas no banco de dados antes de serem integradas ao projeto.

As alterações no schema SQL, nas consultas ou no fluxo de registo devem ser documentadas para manter a reprodutibilidade da PoC.
