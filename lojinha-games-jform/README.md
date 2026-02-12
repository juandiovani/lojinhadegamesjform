# Lojinha de Games — JForm (NetBeans GUI Builder) + CRUD + Upload

## Como abrir e ver o Design
1. NetBeans: File > Open Project...
2. Abra a pasta do projeto (onde existe `pom.xml`)
3. Vá em: `Source Packages > view > TelaProdutosForm.java`
4. Você verá as abas: **Source | Design** (porque existe o arquivo `.form`)

## Banco (MySQL)
```sql
CREATE DATABASE lojinha_games;
USE lojinha_games;

CREATE TABLE jogo (
  id INT AUTO_INCREMENT PRIMARY KEY,
  titulo VARCHAR(120) NOT NULL,
  plataforma VARCHAR(50) NOT NULL,
  preco DECIMAL(10,2) NOT NULL,
  imagem_path VARCHAR(255)
);
```

## Upload da imagem
- Botão "Escolher imagem..." abre o JFileChooser
- Ao salvar, copia para `./imagens/`
- Salva no banco o caminho `imagens/xxxx_nome.jpg`
