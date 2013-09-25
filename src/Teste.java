/*    */ import br.uff.midiacom.ana.util.exception.XMLException;
/*    */ import java.io.FileWriter;
/*    */ import java.io.PrintStream;
/*    */ import java.io.PrintWriter;
/*    */ import org.json.simple.JSONObject;
/*    */ import org.json.simple.parser.ParseException;
/*    */ 
/*    */ public class Teste
/*    */ {
/*    */   public static void teste(String[] args)
/*    */     throws XMLException, ParseException, InterruptedException
/*    */   {
/* 32 */     String arquivoEntrada = "/home/caleb/tvd/jns/m/exemplo3.jns";
/* 33 */     String arquivoSaida = "out.ncl";
/*    */ 
/* 39 */     System.out.println("arquivo lido processando");
/* 40 */     if (args.length > 1)
/* 41 */       arquivoSaida = args[1];
/* 42 */     JSONObject jns = null;
/*    */     try {
/* 44 */       jns = JsonReader.Read(arquivoEntrada);
/*    */     }
/*    */     catch (Exception ex) {
/* 47 */       System.out.println("Erro na leitura:\n" + ex.getMessage());
/* 48 */       return;
/*    */     }
/* 50 */     JNSInterpreter interpretador = new JNSInterpreter();
/*    */ 
/* 53 */     String arquivoFinal = interpretador.InterpretsJNS(jns);
/*    */ 
/* 59 */     FileWriter writer = null;
/*    */     try {
/* 61 */       writer = new FileWriter(arquivoSaida);
/*    */     }
/*    */     catch (Exception ex) {
/* 64 */       System.out.println("Erro para excrever arquivo verifique permissões");
/* 65 */       return;
/*    */     }
/* 67 */     PrintWriter saida = new PrintWriter(writer);
/* 68 */     saida.print(arquivoFinal);
/* 69 */     saida.close();
/*    */     try {
/* 71 */       writer.close();
/*    */     }
/*    */     catch (Exception ex) {
/* 74 */       System.out.println("Erro para fechar arquivo verifique permissões");
/* 75 */       return;
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Convidado\Desktop\pacoteCompleto\jnsTranslator.jar
 * Qualified Name:     Teste
 * JD-Core Version:    0.6.2
 */