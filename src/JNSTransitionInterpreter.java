 import br.uff.midiacom.ana.transition.NCLTransition;
 import br.uff.midiacom.ana.transition.NCLTransitionBase;
 import br.uff.midiacom.ana.util.ElementList;
 import br.uff.midiacom.ana.util.TimeType;
 import br.uff.midiacom.ana.util.enums.NCLColor;
 import br.uff.midiacom.ana.util.enums.NCLTransitionDirection;
 import br.uff.midiacom.ana.util.enums.NCLTransitionSubtype;
 import br.uff.midiacom.ana.util.enums.NCLTransitionType;
 import br.uff.midiacom.ana.util.exception.XMLException;
 import java.util.Iterator;
 import org.json.simple.JSONObject;
 
 public class JNSTransitionInterpreter
 {
   private NCLTransitionBase Base = null;
 
   JNSTransitionInterpreter() throws XMLException {
     this.Base = new NCLTransitionBase();
   }
 
   public NCLTransitionBase getBase() {
     return this.Base;
   }
 
   public NCLTransition getTransition(String id) {
     Iterator transition = this.Base.getTransitions().iterator();
     while (transition.hasNext()) {
       NCLTransition aux = (NCLTransition)transition.next();
       if (aux.getId().equalsIgnoreCase(id)) {
         return aux;
       }
     }
     return null;
   }
 
   public void Add(JSONObject jnsTrasiton) throws XMLException {
     NCLTransition transicao = new NCLTransition((String)jnsTrasiton.get("id"));
 
     if (jnsTrasiton.containsKey("type")) {
       transicao.setType(NCLTransitionType.valueOf(((String)jnsTrasiton.get("type")).toUpperCase()));
     }
     if (jnsTrasiton.containsKey("subtype")) {
       transicao.setSubtype(NCLTransitionSubtype.valueOf(((String)jnsTrasiton.get("subtype")).toUpperCase()));
     }
     if (jnsTrasiton.containsKey("dur")) {
       transicao.setDur(new TimeType(Integer.valueOf(Integer.parseInt(jnsTrasiton.get("subtype").toString()))));
     }
     if (jnsTrasiton.containsKey("startProgress")) {
       transicao.setStartProgress((Double)jnsTrasiton.get("startProgress"));
     }
     if (jnsTrasiton.containsKey("endProgress")) {
       transicao.setEndProgress((Double)jnsTrasiton.get("endProgress"));
     }
     if (jnsTrasiton.containsKey("direction")) {
       transicao.setDirection(NCLTransitionDirection.valueOf((String)jnsTrasiton.get("direction")));
     }
     if (jnsTrasiton.containsKey("fadeColor")) {
       transicao.setFadeColor(NCLColor.valueOf((String)jnsTrasiton.get("fadeColor")));
     }
     if (jnsTrasiton.containsKey("borderColor")) {
       transicao.setBorderColor(NCLColor.valueOf((String)jnsTrasiton.get("borderColor")));
     }
     if (jnsTrasiton.containsKey("horRepeat")) {
       transicao.setHorRepeat(Integer.valueOf(Integer.parseInt(jnsTrasiton.get("borderColor").toString())));
     }
     if (jnsTrasiton.containsKey("vertRepeat")) {
       transicao.setVertRepeat(Integer.valueOf(Integer.parseInt(jnsTrasiton.get("vertRepeat").toString())));
     }
     if (jnsTrasiton.containsKey("borderWidth")) {
       transicao.setBorderWidth(Integer.valueOf(Integer.parseInt(jnsTrasiton.get("borderWidth").toString())));
     }
     this.Base.addTransition(transicao);
   }
 }

/* Location:           C:\Users\Convidado\Desktop\pacoteCompleto\jnsTranslator.jar
 * Qualified Name:     JNSTransitionInterpreter
 * JD-Core Version:    0.6.2
 */