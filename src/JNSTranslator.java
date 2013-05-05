/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.io.File;
/*     */ import java.io.FileWriter;
/*     */ import java.io.PrintWriter;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JFileChooser;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JTextArea;
/*     */ import javax.swing.SwingUtilities;
/*     */ import javax.swing.UIManager;
/*     */ import javax.swing.filechooser.FileNameExtensionFilter;
/*     */ import javax.swing.text.Document;
/*     */ import org.json.simple.JSONObject;
/*     */ 
/*     */ public class JNSTranslator extends JPanel
/*     */   implements ActionListener
/*     */ {
/*     */   private static final String newline = "\n";
/*     */   JButton openButton;
/*     */   JTextArea log;
/*     */   JFileChooser fc;
/*     */ 
/*     */   public JNSTranslator()
/*     */   {
/*  55 */     super(new BorderLayout());
/*     */ 
/*  59 */     this.log = new JTextArea(5, 50);
/*  60 */     this.log.setEditable(false);
/*  61 */     JScrollPane logScrollPane = new JScrollPane(this.log);
/*     */ 
/*  64 */     this.fc = new JFileChooser();
/*  65 */     this.fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
/*  66 */     FileNameExtensionFilter filter = new FileNameExtensionFilter("JNS Code", new String[] { "jns" });
/*  67 */     this.fc.setFileFilter(filter);
/*  68 */     this.fc.setAcceptAllFileFilterUsed(false);
/*     */ 
/*  82 */     this.openButton = new JButton("Selecionar arquivo.");
/*  83 */     this.openButton.addActionListener(this);
/*     */ 
/*  86 */     JPanel buttonPanel = new JPanel();
/*  87 */     buttonPanel.add(this.openButton);
/*  88 */     this.log.append("Abra um arquivo jns para compilar. \nO resultado tera o mesmo nome com a teminação .ncl");
/*     */ 
/*  90 */     add(buttonPanel, "First");
/*  91 */     add(logScrollPane, "Center");
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent e)
/*     */   {
/*  97 */     if (e.getSource() == this.openButton) {
/*  98 */       this.log.setText("");
/*  99 */       int returnVal = this.fc.showOpenDialog(this);
/*     */ 
/* 101 */       if (returnVal == 0) {
/* 102 */         File file = this.fc.getSelectedFile();
/*     */ 
/* 104 */         this.log.append("Opening: " + file.getName() + "." + "\n");
/* 105 */         JSONObject jns = null;
/*     */         try {
/* 107 */           jns = JsonReader.Read(file.getAbsolutePath());
/*     */         }
/*     */         catch (Exception ex) {
/* 110 */           this.log.append("Erro na leitura:\n" + ex.getMessage());
/* 111 */           return;
/*     */         }
/* 113 */         JNSInterpreter interpretador = new JNSInterpreter();
/* 114 */         String arquivoFinal = null;
/*     */         try {
/* 116 */           arquivoFinal = interpretador.InterpretsJNS(jns);
/*     */         }
/*     */         catch (Exception ex) {
/* 119 */           this.log.append("Erro de interpretação verifique sua sintaxe e envie para caleb@midiacom.uff.br seu código e este log:\n" + ex.getMessage());
/* 120 */           return;
/*     */         }
/* 122 */         FileWriter writer = null;
/*     */         try {
/* 124 */           writer = new FileWriter(file.getPath().replace(".jns", ".ncl"));
/*     */         }
/*     */         catch (Exception ex) {
/* 127 */           this.log.append("Erro para excrever arquivo verifique permissões da pasta");
/* 128 */           return;
/*     */         }
/* 130 */         PrintWriter saida = new PrintWriter(writer);
/* 131 */         saida.print(arquivoFinal);
/* 132 */         saida.close();
/* 133 */         this.log.append("Arquivo " + file.getName().replace(".jns", ".ncl") + " em " + file.getPath().replace(file.getName(), ""));
/*     */         try {
/* 135 */           writer.close();
/* 136 */           return;
/*     */         }
/*     */         catch (Exception ex) {
/* 139 */           this.log.append("Erro para fechar arquivo verifique permissões da pasta");
/* 140 */           return;
/*     */         }
/*     */       }
/* 143 */       this.log.append("Abra um arquivo jns para compilar. \nO resultado tera o mesmo nome com a teminação .ncl");
/*     */ 
/* 145 */       this.log.setCaretPosition(this.log.getDocument().getLength());
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void createAndShowGUI()
/*     */   {
/* 157 */     JFrame frame = new JFrame("JNSWindowsCompiler");
/* 158 */     frame.setDefaultCloseOperation(3);
/*     */ 
/* 161 */     frame.add(new JNSTranslator());
/*     */ 
/* 164 */     frame.pack();
/* 165 */     frame.setVisible(true);
/*     */   }
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/* 171 */     SwingUtilities.invokeLater(new Runnable()
/*     */     {
/*     */       public void run() {
/* 174 */         UIManager.put("swing.boldMetal", Boolean.FALSE);
/* 175 */         JNSTranslator.createAndShowGUI();
/*     */       }
/*     */     });
/*     */   }
/*     */ }

/* Location:           C:\Users\Convidado\Desktop\pacoteCompleto\jnsTranslator.jar
 * Qualified Name:     JNSTranslator
 * JD-Core Version:    0.6.2
 */