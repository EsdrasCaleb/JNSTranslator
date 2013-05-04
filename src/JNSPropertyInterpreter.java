/*    */ import br.uff.midiacom.ana.interfaces.NCLProperty;
/*    */ import br.uff.midiacom.ana.util.exception.XMLException;
/*    */ import org.json.simple.JSONObject;
/*    */ 
/*    */ public class JNSPropertyInterpreter
/*    */ {
/*    */   static NCLProperty Interprets(JSONObject jnsProperty)
/*    */     throws XMLException
/*    */   {
/* 10 */     NCLProperty propriedade = new NCLProperty(JNSjSONComplements.getKey(jnsProperty.toString()));
/* 11 */     Object valor = jnsProperty.get(propriedade.getName().toString());
/* 12 */     if (valor != null)
/* 13 */       propriedade.setValue((String)valor);
/* 14 */     return propriedade;
/*    */   }
/*    */ }

/* Location:           C:\Users\Convidado\Desktop\pacoteCompleto\jnsTranslator.jar
 * Qualified Name:     JNSPropertyInterpreter
 * JD-Core Version:    0.6.2
 */