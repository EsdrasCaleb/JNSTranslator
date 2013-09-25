 import java.util.Iterator;
 import java.util.Set;
 import java.util.Vector;
 import org.json.simple.JSONObject;
 
 public class JNSjSONComplements
 {
   static String[] getKeys(JSONObject jsonObject)
   {
     Vector retorno = new Vector();
     Iterator auxiliar = jsonObject.keySet().iterator();
     while (auxiliar.hasNext())
       retorno.add(auxiliar.next().toString());
     String[] arrayRetorno = new String[retorno.size()];
     for (int i = 0; i < retorno.size(); i++) {
       arrayRetorno[i] = ((String)retorno.get(i));
     }
     return arrayRetorno;
   }
 
   static String getKey(String jsonObject) {
     String nomeParametro = jsonObject;
     int inicio = nomeParametro.indexOf("\"");
     int fim = nomeParametro.indexOf("\":", inicio + 1);
     if (fim - inicio > 0) {
       while (nomeParametro.substring(inicio + 1, fim).indexOf('{') > 0) {
         inicio = nomeParametro.substring(inicio + 1, fim).indexOf('"');
       }
       nomeParametro = nomeParametro.substring(inicio + 1, fim);
     }
     else {
       nomeParametro = "";
     }return nomeParametro;
   }
 }

/* Location:           C:\Users\Convidado\Desktop\pacoteCompleto\jnsTranslator.jar
 * Qualified Name:     JNSjSONComplements
 * JD-Core Version:    0.6.2
 */