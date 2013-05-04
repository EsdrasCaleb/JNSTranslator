/*    */ import br.uff.midiacom.ana.NCLBody;
/*    */ import br.uff.midiacom.ana.link.NCLLink;
/*    */ import br.uff.midiacom.ana.meta.NCLMetadata;
/*    */ import br.uff.midiacom.ana.util.exception.XMLException;
/*    */ import java.util.Vector;
/*    */ import org.json.simple.JSONArray;
/*    */ import org.json.simple.JSONObject;
/*    */ 
/*    */ public class JNSBodyInterpreter
/*    */ {
/*    */   private JNSHeadInterpreter interpretadorHead;
/*    */   private JNSMediaInterpreter interpretadorMedia;
/*    */   private JNSContextInterpreter interpretaContexto;
/*    */   private JNSLinkInterpreter interpretaElo;
/*    */   private JNSSwitchInterpreter interpretaSwitch;
private int i;
/*    */ 
/*    */   JNSBodyInterpreter(JNSHeadInterpreter interpretadorHead)
/*    */   {
/* 22 */     this.interpretadorHead = interpretadorHead;
/* 23 */     this.interpretadorMedia = new JNSMediaInterpreter(interpretadorHead);
/* 24 */     this.interpretaElo = new JNSLinkInterpreter(interpretadorHead);
/* 25 */     this.interpretaContexto = new JNSContextInterpreter(interpretadorHead, this.interpretaElo);
/* 26 */     this.interpretaSwitch = new JNSSwitchInterpreter(interpretadorHead, this.interpretaContexto);
/*    */   }
/*    */   NCLBody Interprets(JSONArray jsonBody) throws XMLException {
/* 29 */     NCLBody corpo = new NCLBody();
/* 30 */     this.interpretadorHead.body = corpo;
/*    */ 
/* 32 */     Vector portas = new Vector();
/* 33 */     for (int i = 0; i < jsonBody.size(); i++) {
/* 34 */       JSONObject elemento = (JSONObject)jsonBody.get(i);
/* 35 */       if (elemento.containsKey("id")) {
/* 36 */         corpo.setId((String)elemento.get("id"));
/* 37 */       } else if (elemento.containsKey("media")) {
/* 38 */         corpo.addNode(this.interpretadorMedia.Interprets((JSONObject)elemento.get("media")));
/*    */       }
/* 40 */       else if (elemento.containsKey("context")) {
/* 41 */         this.interpretaContexto.Interprets((JSONArray)elemento.get("context"), corpo);
/*    */       }
/* 43 */       else if (elemento.containsKey("meta")) {
/* 44 */         corpo.addMeta(JNSMetaInterpreter.InterMeta((JSONObject)elemento.get("meta")));
/*    */       }
/* 46 */       else if (elemento.containsKey("metadata")) {
/* 47 */         NCLMetadata meta = new NCLMetadata();
/* 48 */         meta.setRDFTree((String)elemento.get("metadata"));
/* 49 */         corpo.addMetadata(meta);
/*    */       }
/* 51 */       else if (elemento.containsKey("property")) {
/* 52 */         corpo.addProperty(JNSPropertyInterpreter.Interprets((JSONObject)elemento.get("property")));
/*    */       }
/* 54 */       else if (elemento.containsKey("link")) {
/* 55 */         NCLLink link = this.interpretaElo.Interprets((JSONObject)elemento.get("link"), corpo);
/* 56 */         corpo.addLink(link);
/*    */       }
/* 58 */       else if (elemento.containsKey("switch")) {
/* 59 */         corpo.addNode(this.interpretaSwitch.Interprets((JSONObject)elemento.get("switch"), corpo));
/*    */       }
/* 61 */       else if (elemento.containsKey("port")) {
/* 62 */         portas.add(elemento);
/*    */       }
/*    */     }
/* 65 */     for (i = 0; i < portas.size(); i++) {
/* 66 */       JSONObject elemento = (JSONObject)portas.get(i);
/* 67 */       JNSPortInterpreter.Interprets((JSONObject)elemento.get("port"), corpo);
/*    */     }
/*    */ 
/* 70 */     return corpo;
/*    */   }
/*    */ }

/* Location:           C:\Users\Convidado\Desktop\pacoteCompleto\jnsTranslator.jar
 * Qualified Name:     JNSBodyInterpreter
 * JD-Core Version:    0.6.2
 */