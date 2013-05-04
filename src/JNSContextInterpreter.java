/*    */ import br.uff.midiacom.ana.NCLBody;
/*    */ import br.uff.midiacom.ana.link.NCLLink;
/*    */ import br.uff.midiacom.ana.meta.NCLMetadata;
/*    */ import br.uff.midiacom.ana.node.NCLContext;
/*    */ import br.uff.midiacom.ana.node.NCLSwitch;
/*    */ import br.uff.midiacom.ana.util.exception.XMLException;
/*    */ import java.util.Vector;
/*    */ import org.json.simple.JSONArray;
/*    */ import org.json.simple.JSONObject;
/*    */ 
/*    */ public class JNSContextInterpreter
/*    */ {
/*    */   private JNSHeadInterpreter interpretadorHead;
/*    */   private JNSMediaInterpreter interpretadorMedia;
/*    */   private JNSLinkInterpreter interpretaElo;
/*    */   private JNSSwitchInterpreter interpretaSwitch;
/*    */ 
/*    */   JNSContextInterpreter(JNSHeadInterpreter interpretadorHead, JNSLinkInterpreter interpretaElo)
/*    */   {
/* 22 */     this.interpretadorHead = interpretadorHead;
/* 23 */     this.interpretadorMedia = new JNSMediaInterpreter(interpretadorHead);
/* 24 */     this.interpretaElo = interpretaElo;
/* 25 */     this.interpretaSwitch = new JNSSwitchInterpreter(interpretadorHead, this);
/*    */   }
/*    */ 
/*    */   NCLContext Interprets(JSONArray jsnContext, Object corpo) throws XMLException {
/* 29 */     Vector portas = new Vector();
/* 30 */     NCLContext contexto = new NCLContext();
/* 31 */     if ((corpo instanceof NCLBody)) {
/* 32 */       ((NCLBody)corpo).addNode(contexto);
/*    */     }
/* 34 */     else if ((corpo instanceof NCLContext)) {
/* 35 */       ((NCLContext)corpo).addNode(contexto);
/*    */     }
/*    */     else {
/* 38 */       ((NCLSwitch)corpo).addNode(contexto);
/*    */     }
/* 40 */     int i = 0;
/* 41 */     for (i = 0; i < jsnContext.size(); i++) {
/* 42 */       JSONObject elemento = (JSONObject)jsnContext.get(i);
/* 43 */       if (elemento.containsKey("id"))
/* 44 */         contexto.setId((String)elemento.get("id"));
/* 45 */       if (elemento.containsKey("refer"))
/* 46 */         contexto.setRefer((NCLContext)this.interpretadorHead.body.findNode((String)elemento.get("refer")));
/* 47 */       if (elemento.containsKey("media")) {
/* 48 */         contexto.addNode(this.interpretadorMedia.Interprets((JSONObject)elemento.get("media")));
/*    */       }
/* 50 */       if (elemento.containsKey("context")) {
/* 51 */         Interprets((JSONArray)elemento.get("context"), contexto);
/*    */       }
/* 53 */       if (elemento.containsKey("meta")) {
/* 54 */         contexto.addMeta(JNSMetaInterpreter.InterMeta((JSONObject)elemento.get("meta")));
/*    */       }
/* 56 */       else if (elemento.containsKey("metadata")) {
/* 57 */         NCLMetadata meta = new NCLMetadata();
/* 58 */         meta.setRDFTree((String)elemento.get("metadata"));
/* 59 */         contexto.addMetadata(meta);
/*    */       }
/* 61 */       else if (elemento.containsKey("property")) {
/* 62 */         contexto.addProperty(JNSPropertyInterpreter.Interprets((JSONObject)elemento.get("property")));
/*    */       }
/* 64 */       else if (elemento.containsKey("link")) {
/* 65 */         NCLLink link = this.interpretaElo.Interprets((JSONObject)elemento.get("link"), contexto);
/* 66 */         contexto.addLink(link);
/*    */       }
/* 68 */       else if (elemento.containsKey("switch")) {
/* 69 */         contexto.addNode(this.interpretaSwitch.Interprets((JSONObject)elemento.get("switch"), contexto));
/*    */       }
/* 71 */       else if (elemento.containsKey("port")) {
/* 72 */         portas.add(elemento);
/*    */       }
/*    */     }
/* 75 */     for (i = 0; i < portas.size(); i++) {
/* 76 */       JSONObject elemento = (JSONObject)portas.get(i);
/* 77 */       JNSPortInterpreter.Interprets((JSONObject)elemento.get("port"), contexto);
/*    */     }
/* 79 */     return contexto;
/*    */   }
/*    */ }

/* Location:           C:\Users\Convidado\Desktop\pacoteCompleto\jnsTranslator.jar
 * Qualified Name:     JNSContextInterpreter
 * JD-Core Version:    0.6.2
 */