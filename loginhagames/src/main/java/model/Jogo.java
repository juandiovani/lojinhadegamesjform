/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author JUANDIOVANISARASSAFI
 */
public class Jogo {
    // Atributos (características do jogo)
    // private = só a própria classe acessa diretamente 
    private int id;
    private String titulo;
    private String plataforma;
    private double preco;
    private String imagemPath;
    
// Constructor vazio
    // Necessário para frameworks, DAO, e criação sem dados 
    public Jogo() {}
    // Constructor com parâmetros 
    // Facilita criar um jogo já com dados
    public Jogo(String titulo, String plataforma, double preco, String imagemPath) {
    this.titulo = titulo;
    this.plataforma = plataforma;
    this.preco = preco;
    this.imagemPath = imagemPath;
    }
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
    public double getPreco(double preco) {
     this.preco = preco;
     
    }
    public String getImagemPath(String imagemPath) {
         this.imagemPath = imagemPath;
    }
    
}
    
    
    

