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
- [Pré-requisitos](#pré-requisitos)
- [Como começar](#como-começar)
  - [1. Preparar o SQL Server](#1-preparar-o-sql-server)
  - [2. Configurar a rede e o IP](#2-configurar-a-rede-e-o-ip)
  - [3. Compilar a aplicação](#3-compilar-a-aplicação)
  - [4. Testar a leitura do QR Code](#4-testar-a-leitura-do-qr-code)
- [Resolução de problemas](#resolução-de-problemas)
- [Manutenção e contribuições](#manutenção-e-contribuições)

## O que o projeto faz

O sistema permite registar a entrada de paletes no armazém por meio da leitura ótica de QR Codes usando a câmara de um telemóvel Android.

Após a leitura, a aplicação:

1. Identifica o produto ou código da palete.
2. Consulta os dados necessários na base de dados.
3. Regista a movimentação no SQL Server.
4. Permite validar o registo diretamente no banco de dados.

A leitura é realizada com a biblioteca **ZXing**, enquanto a comunicação com o SQL Server utiliza JDBC através do driver **jTDS**.

## Por que o projeto é útil

A solução permite acompanhar e auditar movimentações de paletes em tempo real, mantendo os registos centralizados no SQL Server.

A PoC não depende de uma API intermédia ou de uma infraestrutura local complexa: o dispositivo Android comunica diretamente com o servidor de base de dados, desde que esteja na mesma rede e que o SQL Server aceite conexões remotas.

> **Atenção:** numa aplicação de produção, recomenda-se utilizar uma API segura entre o Android e o banco de dados, evitando expor diretamente as credenciais do SQL Server no dispositivo.

## Arquitetura da solução

```text
+------------------------+
| Telemóvel Android      |
| Aplicação Java        |
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

## Pré-requisitos

- Android Studio configurado para desenvolvimento Android em Java.
- Telemóvel Android ou emulador com acesso à rede.
- SQL Server instalado e em execução.
- Base de dados `Beevolut`.
- Utilizador SQL Server com permissões de leitura e escrita.
- SQL Server configurado para aceitar conexões TCP/IP.
- Computador e telemóvel ligados à mesma rede, quando aplicável.
- Dependências Android do ZXing e do driver JDBC jTDS configuradas no projeto.

## Como começar

O fluxo recomendado para validar a PoC é:

**Preparar SQL → Alterar IP → Compilar → Ler QR Code → Confirmar registo**

### 1. Preparar o SQL Server

#### 1.1 Criar a base de dados

Execute no SQL Server Management Studio (SSMS):

```sql
CREATE DATABASE Beevolut;
GO

USE Beevolut;
GO
```

#### 1.2 Criar a tabela `produto`

O exemplo abaixo representa uma estrutura mínima. Ajuste os tipos e os nomes das colunas conforme o código atual da aplicação.

```sql
CREATE TABLE produto (
    id_produto INT IDENTITY(1,1) PRIMARY KEY,
    codigo VARCHAR(100) NOT NULL UNIQUE,
    descricao VARCHAR(255) NULL
);
GO
```

#### 1.3 Criar a tabela `movimentacao`

A tabela de movimentações deve manter a relação com o produto por meio de uma chave estrangeira.

```sql
CREATE TABLE movimentacao (
    id_movimentacao INT IDENTITY(1,1) PRIMARY KEY,
    id_produto INT NOT NULL,
    tipo VARCHAR(30) NOT NULL DEFAULT 'ENTRADA',
    data_movimentacao DATETIME NOT NULL DEFAULT GETDATE(),

    CONSTRAINT FK_movimentacao_produto
        FOREIGN KEY (id_produto)
        REFERENCES produto(id_produto)
);
GO
```

#### 1.4 Inserir um produto para teste

```sql
INSERT INTO produto (codigo, descricao)
VALUES ('PAL001', 'Palete de teste');
GO

SELECT * FROM produto;
SELECT * FROM movimentacao;
```

O valor `PAL001` deve ser utilizado no QR Code de teste, caso a aplicação procure o produto pelo campo `codigo`.

#### 1.5 Ativar o utilizador `sa`

No SQL Server Management Studio:

1. Aceda a **Security > Logins**.
2. Abra as propriedades do utilizador `sa`.
3. No separador **Status**, ative o login.
4. Defina uma palavra-passe segura.
5. Confirme que o servidor permite autenticação do SQL Server e do Windows, quando necessário.
6. Reinicie o serviço do SQL Server se a alteração exigir.

> **Segurança:** a utilização de `sa` serve apenas para a validação da PoC. Para um ambiente real, crie um utilizador exclusivo com permissões mínimas, evitando privilégios administrativos.

#### 1.6 Ativar conexões TCP/IP

No **SQL Server Configuration Manager**:

1. Abra `SQL Server Network Configuration`.
2. Entre em `Protocols for <instância>`.
3. Ative **TCP/IP**.
4. Em **IP Addresses**, configure uma porta TCP conhecida, normalmente `1433` para uma instância padrão.
5. Reinicie o serviço do SQL Server.

Se existir firewall no computador servidor, autorize a porta TCP configurada para conexões da rede de teste.

### 2. Configurar a rede e o IP

Descubra o endereço IPv4 do computador que executa o SQL Server:

```powershell
ipconfig
```

Procure o endereço IPv4 da interface ligada à mesma rede do telemóvel. Exemplo ilustrativo:

```text
IPv4: 192.168.1.100
Porta: 1433
```

Abra o ficheiro:

```text
app/src/main/java/<pacote>/DatabaseHelper.java
```

Localize a configuração da conexão e substitua o IP de exemplo pelo IP real do servidor:

```java
private static final String IP_SERVIDOR = "192.168.1.100";
private static final String PORTA = "1433";
private static final String BANCO = "Beevolut";
private static final String USUARIO = "sa";
private static final String SENHA = "SUA_SENHA";
```

A URL JDBC poderá seguir este formato, dependendo da implementação existente:

```java
String url = "jdbc:jtds:sqlserver://"
        + IP_SERVIDOR + ":" + PORTA
        + "/" + BANCO;
```

**Não utilize `localhost` ou `127.0.0.1` no telemóvel** para apontar para o computador. Esses endereços referem-se ao próprio dispositivo onde a aplicação está a ser executada.

### 3. Compilar a aplicação

1. Abra o projeto no Android Studio.
2. Confirme que o Gradle concluiu a sincronização sem erros.
3. Verifique se as dependências do ZXing e do jTDS estão disponíveis.
4. Confirme que o `DatabaseHelper.java` contém o IP, a porta, a base de dados e as credenciais corretas.
5. Ligue o telemóvel por USB com a depuração USB ativa ou utilize um emulador.
6. Execute **Build > Make Project**.
7. Instale e execute a aplicação no dispositivo.

Antes de testar, confirme que:

- O SQL Server está em execução.
- A base `Beevolut` existe.
- O login configurado está ativo.
- O computador e o telemóvel estão na mesma rede, quando necessário.
- A firewall permite a conexão à porta do SQL Server.

### 4. Testar a leitura do QR Code

1. Crie ou utilize um QR Code contendo um código de produto existente, por exemplo `PAL001`.
2. Abra a aplicação no Android.
3. Aceda à funcionalidade de leitura.
4. Autorize o acesso à câmara, se solicitado.
5. Aponte a câmara para o QR Code.
6. Confirme o produto identificado.
7. Efetue o registo da entrada da palete.
8. No SSMS, valide o resultado:

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

O teste é considerado concluído quando a leitura é reconhecida pela aplicação e uma nova movimentação aparece na tabela `movimentacao`.

## Resolução de problemas

### Erro 233 ou falha de autenticação

Possíveis causas:

- O utilizador `sa` está desativado.
- A palavra-passe está incorreta.
- O modo de autenticação do SQL Server não permite autenticação SQL.
- O serviço do SQL Server não foi reiniciado após a alteração.

Verificações:

1. Confirme se o login `sa` está ativo.
2. Redefina a palavra-passe, se necessário.
3. Ative o modo de autenticação adequado nas propriedades do servidor.
4. Reinicie o serviço do SQL Server.
5. Teste o login diretamente no SSMS antes de testar pelo Android.

### Erro de Foreign Key

A movimentação pode falhar quando o `id_produto` informado não existe na tabela `produto`.

Verifique:

```sql
SELECT *
FROM produto
WHERE id_produto = 1;
```

Se a aplicação utiliza um código textual:

```sql
SELECT *
FROM produto
WHERE codigo = 'PAL001';
```

Insira primeiro o produto e só depois registe a movimentação. Não elimine a restrição de Foreign Key apenas para contornar o erro, pois ela garante a integridade dos dados.

### Falha de conexão ou timeout

Verifique:

- IP correto no `DatabaseHelper.java`.
- Porta TCP configurada no SQL Server.
- Serviço do SQL Server em execução.
- TCP/IP ativo no SQL Server Configuration Manager.
- Firewall do servidor.
- Telemóvel e computador na mesma rede.
- Permissões do utilizador de banco de dados.

O IP deve ser o endereço do computador servidor, e não o IP do telemóvel.

### QR Code não é reconhecido

Verifique:

- Permissão de acesso à câmara.
- Iluminação e foco da câmara.
- Qualidade e contraste do QR Code.
- Formato do conteúdo lido.
- Correspondência entre o código do QR Code e o campo `codigo` da tabela `produto`.
- Configuração da biblioteca ZXing.

## Manutenção e contribuições

Este projeto foi desenvolvido no contexto académico do **SENAI — Projeto JTEKT**, com foco na validação de uma solução de integração logística entre Android e SQL Server.

### Autoria

- **Projeto:** Integração Logística JTEKT
- **Contexto:** Projeto académico do SENAI
- **Tecnologias:** Java, Android, ZXing, SQL Server e JDBC/jTDS

### Contribuições

Para propor alterações:

1. Crie uma branch para a alteração.
2. Faça a implementação e os testes locais.
3. Verifique se a aplicação continua a comunicar com a base `Beevolut`.
4. Registe as alterações no commit.
5. Abra um Pull Request ou apresente a alteração à equipa responsável pelo projeto.

As alterações ao schema SQL devem ser documentadas neste README para que a banca avaliadora consiga reproduzir o ambiente de teste.
