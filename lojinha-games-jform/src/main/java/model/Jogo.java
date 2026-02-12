// Define o pacote "model".
// Aqui ficam as classes que representam dados do sistema.
package model;

// Classe Jogo representa um jogo da lojinha.
// Ela é o "modelo de dados" no padrão MVC.
public class Jogo {

    // Atributos (características do jogo).
    // private = só a própria classe acessa diretamente.
    private int id;
    private String titulo;
    private String plataforma;
    private double preco;
    private String imagemPath;

    // Construtor vazio.
    // Necessário para frameworks, DAO e criação sem dados iniciais.
    public Jogo() {}

    // Construtor com parâmetros.
    // Facilita criar um jogo já com dados.
    public Jogo(String titulo, String plataforma, double preco, String imagemPath) {
        this.titulo = titulo;
        this.plataforma = plataforma;
        this.preco = preco;
        this.imagemPath = imagemPath;
    }

    // ===== GETTERS E SETTERS =====
    // Servem para acessar e modificar atributos privados.

    public int getId() { 
        return id; 
    }

    public void setId(int id) { 
        this.id = id; 
    }

    public String getTitulo() { 
        return titulo; 
    }

    public void setTitulo(String titulo) { 
        this.titulo = titulo; 
    }

    public String getPlataforma() { 
        return plataforma; 
    }

    public void setPlataforma(String plataforma) { 
        this.plataforma = plataforma; 
    }

    public double getPreco() { 
        return preco; 
    }

    public void setPreco(double preco) { 
        this.preco = preco; 
    }

    public String getImagemPath() { 
        return imagemPath; 
    }

    public void setImagemPath(String imagemPath) { 
        this.imagemPath = imagemPath; 
    }
}


//📌 Explicação didática simples
//
//Você pode explicar assim para os alunos:
//
//"O Model é a representação de um objeto do mundo real dentro do sistema."
//
//Aqui:
//🎮 Um jogo da loja virou uma classe Java.
//
//🧠 O que é cada parte?
//✅ Atributos
//
//São as características do jogo:
//
//id → identificador no banco
//
//titulo → nome do jogo
//
//plataforma → PC, PS5, Xbox
//
//preco → valor
//
//imagemPath → caminho da imagem
//
//👉 É como uma ficha de cadastro.
//
//✅ Construtores
//Construtor vazio
//
//Permite criar objeto sem dados:
//
//Jogo j = new Jogo();
//
//
//Muito usado pelo DAO.
//
//Construtor com parâmetros
//
//Já cria com dados:
//
//Jogo j = new Jogo("FIFA", "PS5", 299.90, "img/fifa.jpg");
//
//
//Mais prático.
//
//✅ Getters e Setters
//
//Servem para:
//✔ Ler dados
//✔ Alterar dados
//✔ Proteger os atributos
//
//Isso é encapsulamento.
//
//🧩 Analogia fácil
//
//📄 Model = Ficha de cadastro
//
//Como ficha de biblioteca:
//
//Nome do livro
//
//Autor
//
//Preço
//
//O Model guarda dados, não regras de banco.
//
//🎯 Resumo para alunos
//
//O Model:
//✅ Representa dados
//✅ Não acessa banco
//✅ Não tem tela
//✅ Só guarda informações
//
//💡 Dica de professor (importante)
//
//Explique assim:
//
//"Model é o substantivo do sistema."
//
//Ex:
//
//Jogo
//
//Cliente
//
//Pedido
//
//Produto
//
//São coisas do mundo real virando classe.