 import java.awt.BorderLayout;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.io.File;
 import java.io.FileWriter;
 import java.io.PrintWriter;
 import javax.swing.JButton;
 import javax.swing.JFileChooser;
 import javax.swing.JFrame;
 import javax.swing.JPanel;
 import javax.swing.JScrollPane;
 import javax.swing.JTextArea;
 import javax.swing.SwingUtilities;
 import javax.swing.UIManager;
 import javax.swing.filechooser.FileNameExtensionFilter;
 import javax.swing.text.Document;
 import org.json.simple.JSONObject;
 
 public class JNSTranslator extends JPanel
   implements ActionListener
 {
   private static final String newline = "\n";
   JButton openButton;
   JTextArea log;
   JFileChooser fc;
 
   public JNSTranslator()
   {
     super(new BorderLayout());
 
     this.log = new JTextArea(5, 50);
     this.log.setEditable(false);
     JScrollPane logScrollPane = new JScrollPane(this.log);
 
     this.fc = new JFileChooser();
     this.fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
     FileNameExtensionFilter filter = new FileNameExtensionFilter("JNS Code", new String[] { "jns" });
     this.fc.setFileFilter(filter);
     this.fc.setAcceptAllFileFilterUsed(false);
 
     this.openButton = new JButton("Selecionar arquivo.");
     this.openButton.addActionListener(this);
 
     JPanel buttonPanel = new JPanel();
     buttonPanel.add(this.openButton);
     this.log.append("Abra um arquivo jns para compilar. \nO resultado terá o mesmo nome com a teminação .ncl");
 
     add(buttonPanel, "First");
     add(logScrollPane, "Center");
   }
 
   public void actionPerformed(ActionEvent e)
   {
     if (e.getSource() == this.openButton) {
       this.log.setText("");
       int returnVal = this.fc.showOpenDialog(this);
 
       if (returnVal == 0) {
         File file = this.fc.getSelectedFile();
 
         this.log.append("Opening: " + file.getName() + "." + "\n");
         JSONObject jns = null;
         try {
           jns = JsonReader.Read(file.getAbsolutePath());
         }
         catch (Exception ex) {
           this.log.append("Erro na leitura:\n" + ex.getMessage());
           return;
         }
         JNSInterpreter interpretador = new JNSInterpreter();
         String arquivoFinal = null;
         try {
           arquivoFinal = interpretador.InterpretsJNS(jns);
         }
         catch (Exception ex) {
           this.log.append("Erro:\n" + ex.getMessage()+"\nCaso ainda tenha problemas e envie para caleb@midiacom.uff.br seu código e este log");
           return;
         }
         FileWriter writer = null;
         try {
           writer = new FileWriter(file.getPath().replace(".jns", ".ncl"));
         }
         catch (Exception ex) {
           this.log.append("Erro para excrever arquivo verifique permissões da pasta");
           return;
         }
         PrintWriter saida = new PrintWriter(writer);
         saida.print(arquivoFinal);
         saida.close();
         this.log.append("Arquivo " + file.getName().replace(".jns", ".ncl") + " em " + file.getPath().replace(file.getName(), ""));
         try {
           writer.close();
           return;
         }
         catch (Exception ex) {
           this.log.append("Erro para fechar arquivo verifique permissões da pasta");
           return;
         }
       }
       this.log.append("Abra um arquivo jns para compilar. \nO resultado tera o mesmo nome com a teminação .ncl");
 
       this.log.setCaretPosition(this.log.getDocument().getLength());
     }
   }
 
   private static void createAndShowGUI()
   {
     JFrame frame = new JFrame("JNSWindowsCompiler");
     frame.setDefaultCloseOperation(3);
 
     frame.add(new JNSTranslator());
 
     frame.pack();
     frame.setVisible(true);
   }
 
   public static void main(String[] args)
   {
     SwingUtilities.invokeLater(new Runnable()
     {
       public void run() {
         UIManager.put("swing.boldMetal", Boolean.FALSE);
         JNSTranslator.createAndShowGUI();
       }
     });
   }
 }

/* Location:           C:\Users\Convidado\Desktop\pacoteCompleto\jnsTranslator.jar
 * Qualified Name:     JNSTranslator
 * JD-Core Version:    0.6.2
 */