package view;

// Importa o DAO (quem conversa com o MySQL)
import dao.JogoDAO;

// Importa o Model (objeto de dados)
import model.Jogo;

// Utilitário que copia a imagem para a pasta ./imagens e devolve o caminho
import util.ImageStorage;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.Image;
import java.io.File;
import java.util.List;

// Essa é a tela principal (View) da aplicação.
// Ela herda de JFrame: uma janela do Swing.
public class TelaProdutosForm extends javax.swing.JFrame {

    // Cria o DAO uma única vez para usar em toda a tela.
    // Ele executa inserir/atualizar/excluir/listar no banco.
    private final JogoDAO dao = new JogoDAO();

    // Guarda o ID selecionado na tabela:
    // null => modo "novo cadastro"
    // número => modo "edição" de um item existente
    private Integer idSelecionado = null;

    // Guarda o arquivo de imagem que o usuário escolheu no computador.
    // (ainda não foi salvo na pasta ./imagens)
    private File imagemEscolhida = null;

    // Construtor da tela (quando a janela é criada)
    public TelaProdutosForm() {
        initComponents();     // Monta os componentes do formulário (gerado pelo NetBeans)
        configurarTabela();   // Configura clique/seleção da tabela
        recarregarTabela();   // Carrega dados do banco para a JTable
        novo();               // Deixa tudo limpo para cadastrar um produto novo
    }

    // ===================== LÓGICA (CRUD + IMAGEM) =====================

    private void configurarTabela() {
        // Garante que só um item por vez pode ser selecionado.
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Listener: dispara quando o usuário seleciona uma linha na tabela.
        tabela.getSelectionModel().addListSelectionListener(e -> {

            // Evita disparar duas vezes (um evento "ajustando" e outro final).
            if (!e.getValueIsAdjusting()) {

                // Pega a linha selecionada
                int row = tabela.getSelectedRow();

                // Se realmente existe uma linha selecionada...
                if (row >= 0) {

                    // Pega o ID (coluna 0) da linha
                    int id = (int) tabela.getModel().getValueAt(row, 0);

                    // Carrega esse jogo nos campos para editar
                    carregarParaEdicao(id);
                }
            }
        });
    }

    private void carregarParaEdicao(int id) {
        // Busca o jogo no banco pelo ID
        Jogo j = dao.buscarPorId(id);

        // Se não encontrou, não faz nada
        if (j == null) return;

        // Marca que agora estamos editando (não é mais "novo")
        idSelecionado = j.getId();

        // Preenche os campos do formulário
        txtTitulo.setText(j.getTitulo());
        txtPlataforma.setText(j.getPlataforma());
        txtPreco.setText(String.valueOf(j.getPreco()));

        // Mostra o caminho da imagem (ou mensagem se não tiver)
        txtImagem.setText(j.getImagemPath() == null ? "Nenhuma imagem" : j.getImagemPath());

        // Muito importante:
        // Ao carregar para edição, não obrigamos escolher imagem de novo.
        imagemEscolhida = null;

        // Mostra a imagem do produto no preview
        mostrarImagem(j.getImagemPath(), false);
    }

    private void novo() {
        // Volta ao modo "novo cadastro"
        idSelecionado = null;

        // Esquece a imagem escolhida no PC
        imagemEscolhida = null;

        // Limpa campos
        txtTitulo.setText("");
        txtPlataforma.setText("");
        txtPreco.setText("");
        txtImagem.setText("Nenhuma imagem");

        // Limpa preview
        lblCapa.setIcon(null);
        lblCapa.setText("Selecione um item");

        // Remove seleção da tabela
        tabela.clearSelection();
    }

    private void escolherImagem() {
        // Abre janela do Windows para escolher arquivo
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Escolha a imagem do produto");

        // Mostra a janela
        int result = chooser.showOpenDialog(this);

        // Se o usuário clicou em "Abrir"
        if (result == JFileChooser.APPROVE_OPTION) {

            // Salva o arquivo escolhido
            imagemEscolhida = chooser.getSelectedFile();

            // Mostra preview imediatamente usando o caminho absoluto do Windows
            mostrarImagem(imagemEscolhida.getAbsolutePath(), true);

            // Informa no campo que a imagem ainda será copiada quando salvar
            txtImagem.setText("Será copiada ao salvar (./imagens/)");
        }
    }

    private void salvarOuAtualizar() {

        // Validação: impede salvar campos vazios
        if (txtTitulo.getText().isBlank() || txtPlataforma.getText().isBlank() || txtPreco.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Preencha título, plataforma e preço.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Converte o preço do texto para double
        double preco;
        try {
            // Aceita "199,90" e transforma em "199.90"
            preco = Double.parseDouble(txtPreco.getText().replace(",", "."));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Preço inválido! Ex: 199.90", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Caminho final que será gravado no banco
        String caminhoImagem = null;

        // Se o usuário escolheu uma imagem nova agora...
        if (imagemEscolhida != null) {
            // Copia para ./imagens e devolve o caminho relativo
            caminhoImagem = ImageStorage.salvarImagem(imagemEscolhida);

        // Senão, se estamos editando um item existente...
        } else if (idSelecionado != null) {

            // Mantém a imagem atual do banco (não perde ao editar)
            Jogo atual = dao.buscarPorId(idSelecionado);
            if (atual != null) caminhoImagem = atual.getImagemPath();
        }

        // Monta o objeto Jogo com os dados do form
        Jogo j = new Jogo(
                txtTitulo.getText().trim(),
                txtPlataforma.getText().trim(),
                preco,
                caminhoImagem
        );

        // Se não tem ID selecionado => é cadastro (INSERT)
        if (idSelecionado == null) {
            dao.inserir(j);
            JOptionPane.showMessageDialog(this, "Produto cadastrado!", "OK", JOptionPane.INFORMATION_MESSAGE);

        // Se tem ID => é edição (UPDATE)
        } else {
            j.setId(idSelecionado);
            dao.atualizar(j);
            JOptionPane.showMessageDialog(this, "Produto atualizado!", "OK", JOptionPane.INFORMATION_MESSAGE);
        }

        // Atualiza a tabela e limpa o form
        recarregarTabela();
        novo();
    }

    private void excluir() {
        // Se não selecionou nada, não dá pra excluir
        if (idSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um item para excluir.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Confirmação para evitar exclusões acidentais
        int confirm = JOptionPane.showConfirmDialog(this, "Excluir este produto?", "Confirmar", JOptionPane.YES_NO_OPTION);

        // Se confirmou...
        if (confirm == JOptionPane.YES_OPTION) {
            dao.excluir(idSelecionado);
            JOptionPane.showMessageDialog(this, "Produto excluído!", "OK", JOptionPane.INFORMATION_MESSAGE);
            recarregarTabela();
            novo();
        }
    }

    private void recarregarTabela() {
        // Pega o model da JTable (onde ficam as linhas)
        DefaultTableModel m = (DefaultTableModel) tabela.getModel();

        // Limpa todas as linhas antes de recarregar
        m.setRowCount(0);

        // Busca lista do banco
        List<Jogo> lista = dao.listar();

        // Adiciona cada jogo como uma nova linha da tabela
        for (Jogo j : lista) {
            m.addRow(new Object[]{ j.getId(), j.getTitulo(), j.getPlataforma(), j.getPreco(), j.getImagemPath() });
        }
    }

    private void mostrarImagem(String caminho, boolean caminhoAbsoluto) {
        try {
            // Se não tem caminho, mostra texto
            if (caminho == null || caminho.isBlank()) {
                lblCapa.setIcon(null);
                lblCapa.setText("Sem imagem");
                return;
            }

            // Cria um ícone usando o caminho recebido
            ImageIcon icon = new ImageIcon(caminho);

            // Redimensiona a imagem para caber no preview
            Image img = icon.getImage().getScaledInstance(280, 280, Image.SCALE_SMOOTH);

            // Troca o texto pelo ícone
            lblCapa.setText("");
            lblCapa.setIcon(new ImageIcon(img));

        } catch (Exception e) {
            // Se deu erro (arquivo não encontrado, etc.)
            lblCapa.setIcon(null);
            lblCapa.setText("Erro ao carregar imagem");
        }
    }

    // ===================== EVENTOS DOS BOTÕES =====================

    // Botão "Novo" limpa o formulário
    private void btnNovoActionPerformed(java.awt.event.ActionEvent evt) {
        novo();
    }

    // Botão "Escolher imagem" abre o explorador de arquivos
    private void btnEscolherImagemActionPerformed(java.awt.event.ActionEvent evt) {
        escolherImagem();
    }

    // Botão "Salvar" decide entre inserir ou atualizar
    private void btnSalvarActionPerformed(java.awt.event.ActionEvent evt) {
        salvarOuAtualizar();
    }

    // Botão "Excluir" remove o item selecionado
    private void btnExcluirActionPerformed(java.awt.event.ActionEvent evt) {
        excluir();
    }

    // ===================== GUI BUILDER (NETBEANS) =====================
    // Essa parte é gerada automaticamente pelo NetBeans (JForm).
    // Normalmente não editamos manualmente para não quebrar o Designer.


    // ===================== GUI BUILDER (NETBEANS) =====================
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        painelTopo = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        lblSubtitulo = new javax.swing.JLabel();
        painelForm = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtTitulo = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtPlataforma = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtPreco = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        btnEscolherImagem = new javax.swing.JButton();
        txtImagem = new javax.swing.JTextField();
        painelBotoes = new javax.swing.JPanel();
        btnNovo = new javax.swing.JButton();
        btnSalvar = new javax.swing.JButton();
        btnExcluir = new javax.swing.JButton();
        painelTabela = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabela = new javax.swing.JTable();
        painelPreview = new javax.swing.JPanel();
        lblCapa = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Lojinha de Games — JForm");
        setMinimumSize(new java.awt.Dimension(980, 600));

        painelTopo.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 8, 8, 8));

        lblTitulo.setFont(lblTitulo.getFont().deriveFont(lblTitulo.getFont().getStyle() | java.awt.Font.BOLD, 22));
        lblTitulo.setText("Lojinha de Games");

        lblSubtitulo.setForeground(new java.awt.Color(90, 90, 90));
        lblSubtitulo.setText("CRUD + Upload de imagem + MySQL (JDBC + DAO)");

        javax.swing.GroupLayout painelTopoLayout = new javax.swing.GroupLayout(painelTopo);
        painelTopo.setLayout(painelTopoLayout);
        painelTopoLayout.setHorizontalGroup(
            painelTopoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(painelTopoLayout.createSequentialGroup()
                .addGroup(painelTopoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTitulo)
                    .addComponent(lblSubtitulo))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        painelTopoLayout.setVerticalGroup(
            painelTopoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(painelTopoLayout.createSequentialGroup()
                .addComponent(lblTitulo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblSubtitulo)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        painelForm.setBorder(javax.swing.BorderFactory.createTitledBorder("Cadastro / Edição"));

        jLabel1.setText("Título:");

        jLabel2.setText("Plataforma:");

        jLabel3.setText("Preço (ex: 199.90):");

        jLabel4.setText("Imagem (upload):");

        btnEscolherImagem.setText("Escolher imagem...");
        btnEscolherImagem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEscolherImagemActionPerformed(evt);
            }
        });

        txtImagem.setEditable(false);
        txtImagem.setText("Nenhuma imagem");

        javax.swing.GroupLayout painelFormLayout = new javax.swing.GroupLayout(painelForm);
        painelForm.setLayout(painelFormLayout);
        painelFormLayout.setHorizontalGroup(
            painelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(painelFormLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(painelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(btnEscolherImagem, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                    .addComponent(txtTitulo)
                    .addComponent(txtPlataforma)
                    .addComponent(txtPreco)
                    .addComponent(txtImagem))
                .addContainerGap())
        );
        painelFormLayout.setVerticalGroup(
            painelFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(painelFormLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPlataforma, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPreco, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEscolherImagem, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtImagem, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        painelBotoes.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        btnNovo.setText("Novo");
        btnNovo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNovoActionPerformed(evt);
            }
        });

        btnSalvar.setText("Salvar (Cadastrar/Editar)");
        btnSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarActionPerformed(evt);
            }
        });

        btnExcluir.setText("Excluir");
        btnExcluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcluirActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout painelBotoesLayout = new javax.swing.GroupLayout(painelBotoes);
        painelBotoes.setLayout(painelBotoesLayout);
        painelBotoesLayout.setHorizontalGroup(
            painelBotoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(painelBotoesLayout.createSequentialGroup()
                .addComponent(btnNovo, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnSalvar, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnExcluir, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        painelBotoesLayout.setVerticalGroup(
            painelBotoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(painelBotoesLayout.createSequentialGroup()
                .addGroup(painelBotoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNovo, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnExcluir, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        painelTabela.setBorder(javax.swing.BorderFactory.createTitledBorder("Produtos cadastrados"));

        tabela.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Título", "Plataforma", "Preço", "Imagem"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tabela);

        javax.swing.GroupLayout painelTabelaLayout = new javax.swing.GroupLayout(painelTabela);
        painelTabela.setLayout(painelTabelaLayout);
        painelTabelaLayout.setHorizontalGroup(
            painelTabelaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(painelTabelaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 520, Short.MAX_VALUE)
                .addContainerGap())
        );
        painelTabelaLayout.setVerticalGroup(
            painelTabelaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(painelTabelaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)
                .addContainerGap())
        );

        painelPreview.setBorder(javax.swing.BorderFactory.createTitledBorder("Preview da imagem"));

        lblCapa.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblCapa.setText("Selecione um item");
        lblCapa.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        lblCapa.setPreferredSize(new java.awt.Dimension(280, 280));

        javax.swing.GroupLayout painelPreviewLayout = new javax.swing.GroupLayout(painelPreview);
        painelPreview.setLayout(painelPreviewLayout);
        painelPreviewLayout.setHorizontalGroup(
            painelPreviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(painelPreviewLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblCapa, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                .addContainerGap())
        );
        painelPreviewLayout.setVerticalGroup(
            painelPreviewLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(painelPreviewLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblCapa, javax.swing.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(painelTopo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(painelForm, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(painelBotoes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(painelTabela, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(painelPreview, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(painelTopo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(painelForm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(painelBotoes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(painelTabela, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(painelPreview, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>

    // Variables declaration - do not modify
    private javax.swing.JButton btnEscolherImagem;
    private javax.swing.JButton btnExcluir;
    private javax.swing.JButton btnNovo;
    private javax.swing.JButton btnSalvar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblCapa;
    private javax.swing.JLabel lblSubtitulo;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JPanel painelBotoes;
    private javax.swing.JPanel painelForm;
    private javax.swing.JPanel painelPreview;
    private javax.swing.JPanel painelTabela;
    private javax.swing.JPanel painelTopo;
    private javax.swing.JTable tabela;
    private javax.swing.JTextField txtImagem;
    private javax.swing.JTextField txtPlataforma;
    private javax.swing.JTextField txtPreco;
    private javax.swing.JTextField txtTitulo;
    // End of variables declaration
}
