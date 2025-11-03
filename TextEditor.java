import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.text.*;

public class TextEditorPro extends JFrame {
    private JTextArea textArea;
    private JFileChooser fileChooser;
    private String currentFile = null;
    
    public TextEditorPro() {
        super("Text Editor Pro - .TE Files Only");
        setupUI();
        setupMenu();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        // Configure file filter for .TE files only
        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "TE Files (*.te)", "te"));
    }
    
    private void setupUI() {
        textArea = new JTextArea();
        textArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setRowHeaderView(new LineNumberView(textArea));
        
        JLabel statusBar = new JLabel("Ready | Lines: 1 | Length: 0 | Format: .TE Only");
        
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateStatus(); }
            public void removeUpdate(DocumentEvent e) { updateStatus(); }
            public void changedUpdate(DocumentEvent e) { updateStatus(); }
            
            private void updateStatus() {
                int lines = textArea.getLineCount();
                int length = textArea.getText().length();
                String fileInfo = currentFile != null ? " | File: " + new File(currentFile).getName() : "";
                statusBar.setText("Ready | Lines: " + lines + " | Length: " + length + fileInfo + " | Format: .TE Only");
            }
        });
        
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
    }
    
    private void setupMenu() {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu fileMenu = new JMenu("File");
        JMenuItem newItem = new JMenuItem("New .TE File");
        JMenuItem openItem = new JMenuItem("Open .TE File");
        JMenuItem saveItem = new JMenuItem("Save .TE File");
        JMenuItem saveAsItem = new JMenuItem("Save As .TE File");
        JMenuItem exitItem = new JMenuItem("Exit");
        
        newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        
        newItem.addActionListener(e -> newFile());
        openItem.addActionListener(e -> openFile());
        saveItem.addActionListener(e -> saveFile());
        saveAsItem.addActionListener(e -> saveAsFile());
        exitItem.addActionListener(e -> System.exit(0));
        
        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(saveAsItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        JMenu editMenu = new JMenu("Edit");
        JMenuItem cutItem = new JMenuItem("Cut");
        JMenuItem copyItem = new JMenuItem("Copy");
        JMenuItem pasteItem = new JMenuItem("Paste");
        JMenuItem selectAllItem = new JMenuItem("Select All");
        
        cutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
        copyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
        pasteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
        selectAllItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
        
        cutItem.addActionListener(e -> textArea.cut());
        copyItem.addActionListener(e -> textArea.copy());
        pasteItem.addActionListener(e -> textArea.paste());
        selectAllItem.addActionListener(e -> textArea.selectAll());
        
        editMenu.add(cutItem);
        editMenu.add(copyItem);
        editMenu.add(pasteItem);
        editMenu.addSeparator();
        editMenu.add(selectAllItem);
        
        JMenu viewMenu = new JMenu("View");
        JCheckBoxMenuItem wordWrapItem = new JCheckBoxMenuItem("Word Wrap");
        wordWrapItem.addActionListener(e -> textArea.setLineWrap(wordWrapItem.isSelected()));
        viewMenu.add(wordWrapItem);
        
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About .TE Format");
        aboutItem.addActionListener(e -> showAbout());
        helpMenu.add(aboutItem);
        
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void showAbout() {
        JOptionPane.showMessageDialog(this,
            "Text Editor Pro - .TE Format Only\nVersion 2.0\n\n" +
            "This editor works exclusively with .TE files!\n\n" +
            "Features:\n" +
            "• Line numbering\n" +
            "• Syntax highlighting ready\n" +
            "• Professional interface\n" +
            "• Exclusive .TE file format\n\n" +
            "Will NOT open: .txt, .doc, .pdf or other formats!",
            "About .TE Format",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void newFile() {
        if (confirmSave()) {
            textArea.setText("");
            currentFile = null;
            setTitle("Text Editor Pro - New .TE File");
        }
    }
    
    private void openFile() {
        if (confirmSave()) {
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                
                // FORMAT CHECK - ONLY .TE FILES!
                if (!selectedFile.getName().toLowerCase().endsWith(".te")) {
                    JOptionPane.showMessageDialog(this, 
                        "ERROR: This editor only supports .TE files!\n\n" +
                        "Selected file: " + selectedFile.getName() + "\n" +
                        "Please choose a file with .te extension.",
                        "Invalid File Format",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(selectedFile));
                    textArea.read(reader, null);
                    reader.close();
                    currentFile = selectedFile.getAbsolutePath();
                    setTitle("Text Editor Pro - " + selectedFile.getName());
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, "Error opening .TE file!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void saveFile() {
        if (currentFile == null) {
            saveAsFile();
        } else {
            try {
                FileWriter writer = new FileWriter(currentFile);
                textArea.write(writer);
                writer.close();
                JOptionPane.showMessageDialog(this, ".TE file saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error saving .TE file!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void saveAsFile() {
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            
            // AUTO-ADD .TE EXTENSION IF MISSING
            if (!file.getName().toLowerCase().endsWith(".te")) {
                file = new File(file.getAbsolutePath() + ".te");
            }
            
            currentFile = file.getAbsolutePath();
            setTitle("Text Editor Pro - " + file.getName());
            saveFile();
        }
    }
    
    private boolean confirmSave() {
        if (textArea.getDocument().getLength() > 0) {
            int result = JOptionPane.showConfirmDialog(this, 
                "Save changes to current .TE file?", "Confirm", 
                JOptionPane.YES_NO_CANCEL_OPTION);
            
            if (result == JOptionPane.YES_OPTION) {
                saveFile();
            } else if (result == JOptionPane.CANCEL_OPTION) {
                return false;
            }
        }
        return true;
    }
    
    class LineNumberView extends JComponent {
        private JTextArea textArea;
        
        public LineNumberView(JTextArea textArea) {
            this.textArea = textArea;
            setPreferredSize(new Dimension(40, 0));
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.BLACK);
            
            int lineHeight = textArea.getFontMetrics(textArea.getFont()).getHeight();
            int start = textArea.viewToModel(new Point(0, 0));
            int end = textArea.viewToModel(new Point(0, getHeight()));
            
            try {
                int startLine = textArea.getLineOfOffset(start);
                int endLine = textArea.getLineOfOffset(end);
                
                for (int line = startLine; line <= endLine; line++) {
                    try {
                        Rectangle rect = textArea.modelToView(textArea.getLineStartOffset(line));
                        g.drawString(String.valueOf(line + 1), 5, rect.y + rect.height - 5);
                    } catch (Exception e) {}
                }
            } catch (BadLocationException e) {
            }
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {}
            
            new TextEditorPro().setVisible(true);
        });
    }
}
