/*    */ import java.util.Iterator;
/*    */ import java.util.Set;
/*    */ import java.util.Vector;
/*    */ import org.json.simple.JSONObject;
/*    */ 
/*    */ public class JNSjSONComplements
/*    */ {
/*    */   static String[] getKeys(JSONObject jsonObject)
/*    */   {
/*  9 */     Vector retorno = new Vector();
/* 10 */     Iterator auxiliar = jsonObject.keySet().iterator();
/* 11 */     while (auxiliar.hasNext())
/* 12 */       retorno.add(auxiliar.next().toString());
/* 13 */     String[] arrayRetorno = new String[retorno.size()];
/* 14 */     for (int i = 0; i < retorno.size(); i++) {
/* 15 */       arrayRetorno[i] = ((String)retorno.get(i));
/*    */     }
/* 17 */     return arrayRetorno;
/*    */   }
/*    */ 
/*    */   static String getKey(String jsonObject) {
/* 21 */     String nomeParametro = jsonObject;
/* 22 */     int inicio = nomeParametro.indexOf("\"");
/* 23 */     int fim = nomeParametro.indexOf("\":", inicio + 1);
/* 24 */     if (fim - inicio > 0) {
/* 25 */       while (nomeParametro.substring(inicio + 1, fim).indexOf('{') > 0) {
/* 26 */         inicio = nomeParametro.substring(inicio + 1, fim).indexOf('"');
/*    */       }
/* 28 */       nomeParametro = nomeParametro.substring(inicio + 1, fim);
/*    */     }
/*    */     else {
/* 31 */       nomeParametro = "";
/* 32 */     }return nomeParametro;
/*    */   }
/*    */ }

/* Location:           C:\Users\Convidado\Desktop\pacoteCompleto\jnsTranslator.jar
 * Qualified Name:     JNSjSONComplements
 * JD-Core Version:    0.6.2
 */