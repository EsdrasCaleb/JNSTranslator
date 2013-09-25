 import br.uff.midiacom.ana.interfaces.NCLProperty;
 import br.uff.midiacom.ana.util.exception.XMLException;
 import org.json.simple.JSONObject;
 
 public class JNSPropertyInterpreter
 {
   static NCLProperty Interprets(JSONObject jnsProperty)
     throws XMLException
   {
     NCLProperty propriedade = new NCLProperty(JNSjSONComplements.getKey(jnsProperty.toString()));
     Object valor = jnsProperty.get(propriedade.getName().toString());
     if (valor != null)
       propriedade.setValue((String)valor);
     return propriedade;
   }
 }

/* Location:           C:\Users\Convidado\Desktop\pacoteCompleto\jnsTranslator.jar
 * Qualified Name:     JNSPropertyInterpreter
 * JD-Core Version:    0.6.2
 */