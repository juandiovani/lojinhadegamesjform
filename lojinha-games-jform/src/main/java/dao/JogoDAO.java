// Define o pacote DAO (Data Access Object).
// Aqui ficam as classes que conversam com o banco de dados.
package dao;

// Importa a classe Jogo (Model).
// Representa o objeto que será salvo no banco.
import model.Jogo;

// Classe responsável por criar conexões com o banco.
import util.ConnectionFactory;

// Importações do JDBC (Java Database Connectivity).
import java.sql.*;

// Importações para trabalhar com listas.
import java.util.ArrayList;
import java.util.List;

// Classe DAO do Jogo.
// Ela faz o CRUD (Create, Read, Update, Delete) no banco.
public class JogoDAO {

    // ==============================
    // INSERIR (CREATE)
    // ==============================
    public void inserir(Jogo j) {

        // Comando SQL para inserir dados no banco.
        // ? são parâmetros que serão preenchidos depois.
        String sql = "INSERT INTO jogo (titulo, plataforma, preco, imagem_path) VALUES (?, ?, ?, ?)";

        // try-with-resources:
        // Abre conexão e fecha automaticamente depois.
        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            // Define os valores nos ? da query.
            ps.setString(1, j.getTitulo());
            ps.setString(2, j.getPlataforma());
            ps.setDouble(3, j.getPreco());
            ps.setString(4, j.getImagemPath());

            // Executa o INSERT no banco.
            ps.executeUpdate();

        } catch (Exception e) {
            // Caso dê erro, mostra mensagem.
            throw new RuntimeException("Erro ao inserir: " + e.getMessage());
        }
    }

    // ==============================
    // ATUALIZAR (UPDATE)
    // ==============================
    public void atualizar(Jogo j) {

        // SQL para atualizar um registro.
        String sql = "UPDATE jogo SET titulo = ?, plataforma = ?, preco = ?, imagem_path = ? WHERE id = ?";

        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            // Passa novos valores.
            ps.setString(1, j.getTitulo());
            ps.setString(2, j.getPlataforma());
            ps.setDouble(3, j.getPreco());
            ps.setString(4, j.getImagemPath());

            // Define qual ID será atualizado.
            ps.setInt(5, j.getId());

            // Executa o UPDATE.
            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar: " + e.getMessage());
        }
    }

    // ==============================
    // EXCLUIR (DELETE)
    // ==============================
    public void excluir(int id) {

        // SQL para deletar pelo ID.
        String sql = "DELETE FROM jogo WHERE id = ?";

        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            // Define qual ID será excluído.
            ps.setInt(1, id);

            // Executa o DELETE.
            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao excluir: " + e.getMessage());
        }
    }

    // ==============================
    // LISTAR (READ)
    // ==============================
    public List<Jogo> listar() {

        // Lista para guardar os jogos.
        List<Jogo> lista = new ArrayList<>();

        // SQL para buscar todos.
        String sql = "SELECT * FROM jogo ORDER BY titulo";

        try (Connection c = ConnectionFactory.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            // Enquanto houver resultados...
            while (rs.next()) {

                // Cria um objeto Jogo.
                Jogo j = new Jogo();

                // Pega dados do banco e coloca no objeto.
                j.setId(rs.getInt("id"));
                j.setTitulo(rs.getString("titulo"));
                j.setPlataforma(rs.getString("plataforma"));
                j.setPreco(rs.getDouble("preco"));
                j.setImagemPath(rs.getString("imagem_path"));

                // Adiciona na lista.
                lista.add(j);
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar: " + e.getMessage());
        }

        // Retorna a lista completa.
        return lista;
    }

    // ==============================
    // BUSCAR POR ID
    // ==============================
    public Jogo buscarPorId(int id) {

        String sql = "SELECT * FROM jogo WHERE id = ?";

        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            // Define ID a buscar.
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                // Se encontrou...
                if (rs.next()) {

                    Jogo j = new Jogo();

                    j.setId(rs.getInt("id"));
                    j.setTitulo(rs.getString("titulo"));
                    j.setPlataforma(rs.getString("plataforma"));
                    j.setPreco(rs.getDouble("preco"));
                    j.setImagemPath(rs.getString("imagem_path"));

                    return j;
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar: " + e.getMessage());
        }

        // Se não encontrar, retorna null.
        return null;
    }
}


//📌 Explicação didática simples para alunos
//
//Você pode explicar assim:
//
//"O DAO é o mensageiro entre o Java e o banco de dados."
//
//Ele:
//
//Leva dados para o banco
//
//Busca dados do banco
//
//Atualiza dados
//
//Remove dados
//
//🧠 Analogia fácil
//
//📦 DAO = Entregador dos Correios
//
//Model (Jogo) = o pacote
//
//Banco de dados = a casa do cliente
//
//DAO = quem leva e traz o pacote
//
//O Model nunca fala direto com o banco.
//Quem conversa é o DAO.
//
//🎯 Resumo para prova ou revisão
//
//DAO serve para:
//✅ Inserir
//✅ Atualizar
//✅ Excluir
//✅ Buscar dados no banco
//
//Sem DAO:
//❌ Código SQL espalhado pelo sistema
//❌ Bagunça
//❌ Difícil manutenção