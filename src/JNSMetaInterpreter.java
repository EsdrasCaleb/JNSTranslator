/*    */ import br.uff.midiacom.ana.meta.NCLMeta;
/*    */ import br.uff.midiacom.ana.meta.NCLMetadata;
/*    */ import br.uff.midiacom.ana.util.exception.XMLException;
/*    */ import org.json.simple.JSONObject;
/*    */ 
/*    */ public class JNSMetaInterpreter
/*    */ {
/*    */   public static NCLMeta InterMeta(JSONObject jsnMeta)
/*    */     throws XMLException
/*    */   {
/* 10 */     NCLMeta meta = new NCLMeta();
/* 11 */     String nome = JNSjSONComplements.getKey(jsnMeta.toString());
/* 12 */     meta.setName(nome);
/* 13 */     meta.setContent((String)jsnMeta.get(nome));
/* 14 */     return meta;
/*    */   }
/*    */ 
/*    */   public static NCLMetadata InterMetadata(String rdfTree) throws XMLException {
/* 18 */     NCLMetadata meta = new NCLMetadata();
/* 19 */     meta.setRDFTree(rdfTree);
/* 20 */     return meta;
/*    */   }
/*    */ }

/* Location:           C:\Users\Convidado\Desktop\pacoteCompleto\jnsTranslator.jar
 * Qualified Name:     JNSMetaInterpreter
 * JD-Core Version:    0.6.2
 */