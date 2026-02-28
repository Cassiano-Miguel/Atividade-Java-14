import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

public class CrudCSVApp extends JFrame {
    private JTextField numeroField, matriculaField, nomeField;
    private JButton criarBtn, pesquisarBtn, editarBtn, salvarBtn, deletarBtn, limparBtn;
    private JLabel statusLabel;

    private ArrayList<String[]> registros = new ArrayList<>();
    private int registroAtual = -1;
    private final String caminhoCSV = "dados.csv";

    public CrudCSVApp() {
        super("CRUD CSV - Java Swing");
        setLayout(new BorderLayout(10, 10));

        // Painel de campos
        JPanel camposPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        camposPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        camposPanel.add(new JLabel("Número:"));
        numeroField = new JTextField();
        camposPanel.add(numeroField);

        camposPanel.add(new JLabel("Matrícula:"));
        matriculaField = new JTextField();
        camposPanel.add(matriculaField);

        camposPanel.add(new JLabel("Nome:"));
        nomeField = new JTextField();
        camposPanel.add(nomeField);

        add(camposPanel, BorderLayout.CENTER);

        // Painel de botões
        JPanel botoesPanel = new JPanel(new FlowLayout());
        criarBtn = new JButton("Criar");
        pesquisarBtn = new JButton("Pesquisar");
        editarBtn = new JButton("Editar");
        salvarBtn = new JButton("Salvar");
        deletarBtn = new JButton("Deletar");
        limparBtn = new JButton("Limpar");

        botoesPanel.add(criarBtn);
        botoesPanel.add(pesquisarBtn);
        botoesPanel.add(editarBtn);
        botoesPanel.add(salvarBtn);
        botoesPanel.add(deletarBtn);
        botoesPanel.add(limparBtn);

        add(botoesPanel, BorderLayout.SOUTH);

        // Painel de status
        statusLabel = new JLabel("Pronto.");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        add(statusLabel, BorderLayout.NORTH);

        // Estado inicial dos botões
        editarBtn.setEnabled(false);
        salvarBtn.setEnabled(false);
        deletarBtn.setEnabled(false);

        carregarRegistros();

        // Listeners
        criarBtn.addActionListener(e -> criarRegistro());
        pesquisarBtn.addActionListener(e -> pesquisarRegistro());
        editarBtn.addActionListener(e -> habilitarEdicao(true));
        salvarBtn.addActionListener(e -> salvarEdicao());
        deletarBtn.addActionListener(e -> deletarRegistro());
        limparBtn.addActionListener(e -> limparCampos());

        setCamposHabilitados(true);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 250);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void carregarRegistros() {
        registros.clear();
        File arquivo = new File(caminhoCSV);
        if (!arquivo.exists()) {
            statusLabel.setText("Arquivo CSV não encontrado. Será criado ao salvar.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(caminhoCSV))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                if (!linha.trim().isEmpty()) {
                    registros.add(linha.split(",", -1));
                }
            }
            statusLabel.setText("Carregados " + registros.size() + " registros.");
        } catch (IOException e) {
            statusLabel.setText("Erro ao ler arquivo: " + e.getMessage());
        }
    }

    private void salvarRegistros() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(caminhoCSV))) {
            for (String[] r : registros) {
                writer.write(String.join(",", r));
                writer.newLine();
            }
            statusLabel.setText("Registros salvos com sucesso!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar arquivo: " + e.getMessage());
            statusLabel.setText("Erro ao salvar.");
        }
    }

    private void criarRegistro() {
        String num = numeroField.getText().trim();
        String mat = matriculaField.getText().trim();
        String nome = nomeField.getText().trim();

        if (num.isEmpty() || mat.isEmpty() || nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos.");
            return;
        }

        String[] novo = {num, mat, nome};
        registros.add(novo);
        salvarRegistros();
        limparCampos();
        JOptionPane.showMessageDialog(this, "Registro criado com sucesso!");
    }

    private void pesquisarRegistro() {
        String nome = nomeField.getText().trim();
        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite um nome para pesquisar.");
            return;
        }

        for (int i = 0; i < registros.size(); i++) {
            String[] reg = registros.get(i);
            if (reg.length > 2 && reg[2].equalsIgnoreCase(nome)) {
                numeroField.setText(reg[0]);
                matriculaField.setText(reg[1]);
                nomeField.setText(reg[2]);
                registroAtual = i;
                setCamposHabilitados(false);
                editarBtn.setEnabled(true);
                deletarBtn.setEnabled(true);
                salvarBtn.setEnabled(false);
                statusLabel.setText("Registro encontrado. Nº: " + reg[0]);
                return;
            }
        }
        JOptionPane.showMessageDialog(this, "Registro não encontrado.");
        statusLabel.setText("Registro não encontrado.");
    }

    private void habilitarEdicao(boolean habilitar) {
        setCamposHabilitados(habilitar);
        salvarBtn.setEnabled(habilitar);
        editarBtn.setEnabled(!habilitar);
        statusLabel.setText(habilitar ? "Modo edição." : "Visualização.");
    }

    private void salvarEdicao() {
        if (registroAtual >= 0) {
            String num = numeroField.getText().trim();
            String mat = matriculaField.getText().trim();
            String nome = nomeField.getText().trim();

            if (num.isEmpty() || mat.isEmpty() || nome.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos.");
                return;
            }

            registros.set(registroAtual, new String[]{num, mat, nome});
            salvarRegistros();
            setCamposHabilitados(false);
            salvarBtn.setEnabled(false);
            editarBtn.setEnabled(true);
            JOptionPane.showMessageDialog(this, "Registro atualizado com sucesso!");
            statusLabel.setText("Registro atualizado.");
        }
    }

    private void deletarRegistro() {
        if (registroAtual >= 0) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Tem certeza que deseja deletar este registro?", 
                "Confirmar deleção", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                registros.remove(registroAtual);
                salvarRegistros();
                limparCampos();
                registroAtual = -1;
                editarBtn.setEnabled(false);
                deletarBtn.setEnabled(false);
                salvarBtn.setEnabled(false);
                JOptionPane.showMessageDialog(this, "Registro deletado com sucesso!");
                statusLabel.setText("Registro deletado.");
            }
        }
    }

    private void limparCampos() {
        numeroField.setText("");
        matriculaField.setText("");
        nomeField.setText("");
        setCamposHabilitados(true);
        editarBtn.setEnabled(false);
        deletarBtn.setEnabled(false);
        salvarBtn.setEnabled(false);
        registroAtual = -1;
        statusLabel.setText("Campos limpos. Pronto para novo registro.");
    }

    private void setCamposHabilitados(boolean habilitado) {
        numeroField.setEditable(habilitado);
        matriculaField.setEditable(habilitado);
        nomeField.setEditable(habilitado);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CrudCSVApp::new);
    }
}