/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

/**
 *
 * @author JUANDIOVANISARASSAFI
 */
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter.DateTimeFormatter;
public class ImagemStorage {
    private static final String PASTA_IMAGEM = "imagens";
    public static String salvarImagem( File arquivo) {
     try{
     if(arquivo == null) return null;
     File pasta = new File(PASTA_IMAGENS);
     if(!pasta.existe())pasta.mkdir();
     String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
     
     String nomeOriginl = arquivo.getName()
             .replace("\\s+", "_");
     String novoNome = timestamp + "_" + nomeOriginal;
     Path destino = Path.of(PASTA_IMAGEM, novoNome);
     Files.copy(arquivo, 
             destino,
             StandardCopyOption.REPLACE_EXISTING     
     );
     return PASTA_IMAGEM + "/" + novoNome;
     } catch (Exeption e) {
         throw new RuntimeException(
           "Erro ao salvar imagem: " + e.getMessage());
     }
    }
}
