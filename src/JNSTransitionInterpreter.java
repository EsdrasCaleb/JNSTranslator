/*    */ import br.uff.midiacom.ana.transition.NCLTransition;
/*    */ import br.uff.midiacom.ana.transition.NCLTransitionBase;
/*    */ import br.uff.midiacom.ana.util.ElementList;
/*    */ import br.uff.midiacom.ana.util.TimeType;
/*    */ import br.uff.midiacom.ana.util.enums.NCLColor;
/*    */ import br.uff.midiacom.ana.util.enums.NCLTransitionDirection;
/*    */ import br.uff.midiacom.ana.util.enums.NCLTransitionSubtype;
/*    */ import br.uff.midiacom.ana.util.enums.NCLTransitionType;
/*    */ import br.uff.midiacom.ana.util.exception.XMLException;
/*    */ import java.util.Iterator;
/*    */ import org.json.simple.JSONObject;
/*    */ 
/*    */ public class JNSTransitionInterpreter
/*    */ {
/* 20 */   private NCLTransitionBase Base = null;
/*    */ 
/*    */   JNSTransitionInterpreter() throws XMLException {
/* 23 */     this.Base = new NCLTransitionBase();
/*    */   }
/*    */ 
/*    */   public NCLTransitionBase getBase() {
/* 27 */     return this.Base;
/*    */   }
/*    */ 
/*    */   public NCLTransition getTransition(String id) {
/* 31 */     Iterator transition = this.Base.getTransitions().iterator();
/* 32 */     while (transition.hasNext()) {
/* 33 */       NCLTransition aux = (NCLTransition)transition.next();
/* 34 */       if (aux.getId().equalsIgnoreCase(id)) {
/* 35 */         return aux;
/*    */       }
/*    */     }
/* 38 */     return null;
/*    */   }
/*    */ 
/*    */   public void Add(JSONObject jnsTrasiton) throws XMLException {
/* 42 */     NCLTransition transicao = new NCLTransition((String)jnsTrasiton.get("id"));
/*    */ 
/* 44 */     if (jnsTrasiton.containsKey("type")) {
/* 45 */       transicao.setType(NCLTransitionType.valueOf(((String)jnsTrasiton.get("type")).toUpperCase()));
/*    */     }
/* 47 */     if (jnsTrasiton.containsKey("subtype")) {
/* 48 */       transicao.setSubtype(NCLTransitionSubtype.valueOf(((String)jnsTrasiton.get("subtype")).toUpperCase()));
/*    */     }
/* 50 */     if (jnsTrasiton.containsKey("dur")) {
/* 51 */       transicao.setDur(new TimeType(Integer.valueOf(Integer.parseInt(jnsTrasiton.get("subtype").toString()))));
/*    */     }
/* 53 */     if (jnsTrasiton.containsKey("startProgress")) {
/* 54 */       transicao.setStartProgress((Double)jnsTrasiton.get("startProgress"));
/*    */     }
/* 56 */     if (jnsTrasiton.containsKey("endProgress")) {
/* 57 */       transicao.setEndProgress((Double)jnsTrasiton.get("endProgress"));
/*    */     }
/* 59 */     if (jnsTrasiton.containsKey("direction")) {
/* 60 */       transicao.setDirection(NCLTransitionDirection.valueOf((String)jnsTrasiton.get("direction")));
/*    */     }
/* 62 */     if (jnsTrasiton.containsKey("fadeColor")) {
/* 63 */       transicao.setFadeColor(NCLColor.valueOf((String)jnsTrasiton.get("fadeColor")));
/*    */     }
/* 65 */     if (jnsTrasiton.containsKey("borderColor")) {
/* 66 */       transicao.setBorderColor(NCLColor.valueOf((String)jnsTrasiton.get("borderColor")));
/*    */     }
/* 68 */     if (jnsTrasiton.containsKey("horRepeat")) {
/* 69 */       transicao.setHorRepeat(Integer.valueOf(Integer.parseInt(jnsTrasiton.get("borderColor").toString())));
/*    */     }
/* 71 */     if (jnsTrasiton.containsKey("vertRepeat")) {
/* 72 */       transicao.setVertRepeat(Integer.valueOf(Integer.parseInt(jnsTrasiton.get("vertRepeat").toString())));
/*    */     }
/* 74 */     if (jnsTrasiton.containsKey("borderWidth")) {
/* 75 */       transicao.setBorderWidth(Integer.valueOf(Integer.parseInt(jnsTrasiton.get("borderWidth").toString())));
/*    */     }
/* 77 */     this.Base.addTransition(transicao);
/*    */   }
/*    */ }

/* Location:           C:\Users\Convidado\Desktop\pacoteCompleto\jnsTranslator.jar
 * Qualified Name:     JNSTransitionInterpreter
 * JD-Core Version:    0.6.2
 */