/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package daogames;

import model.Jogo;
import util.ConnectionFactorry;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author JUANDIOVANISARASSAFI
 */
public class jogodao {
  public void inserir(Jogo j) {
        String sql = "INSERT INTO jogo(titulo, plataforma, preco, imagem_path) VALUES (?, ?, ?, ?)";

        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, j.getTitulo());
            ps.setString(2, j.getPlataforma());
            ps.setDouble(3, j.getPreco());
            ps.setString(4, j.getImagemPath());

            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao inserir: " + e.getMessage());
        }
    }

    public void excluir(int id) {
        String sql = "DELETE FROM jogo WHERE id = ?";

        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao excluir: " + e.getMessage());
        }
    }

    public List<Jogo> listar() {
        List<Jogo> lista = new ArrayList<>();
        String sql = "SELECT * FROM jogo ORDER BY titulo";

        try (Connection c = ConnectionFactory.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Jogo j = new Jogo();
                j.setId(rs.getInt("id"));
                j.setTitulo(rs.getString("titulo"));
                j.setPlataforma(rs.getString("plataforma"));
                j.setPreco(rs.getDouble("preco"));
                j.setImagemPath(rs.getString("imagem_path"));

                lista.add(j);
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar: " + e.getMessage());
        }

        return lista;
    }

    public Jogo buscaPorId(int id) {
        String sql = "SELECT * FROM jogo WHERE id = ?";

        try (Connection c = ConnectionFactory.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
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

        return null;
    }
}
