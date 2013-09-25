 import br.uff.midiacom.ana.NCLBody;
 import br.uff.midiacom.ana.interfaces.NCLMapping;
 import br.uff.midiacom.ana.interfaces.NCLSwitchPort;
 import br.uff.midiacom.ana.node.NCLNode;
 import br.uff.midiacom.ana.node.NCLSwitch;
 import br.uff.midiacom.ana.rule.NCLBindRule;
 import br.uff.midiacom.ana.rule.NCLRuleBase;
 import br.uff.midiacom.ana.rule.NCLTestRule;
 import br.uff.midiacom.ana.util.exception.XMLException;
 import java.util.Vector;
 import org.json.simple.JSONArray;
 import org.json.simple.JSONObject;
 
 public class JNSSwitchInterpreter
 {
   private JNSHeadInterpreter interpretadorHead;
   private JNSMediaInterpreter interpretadorMedia;
   private JNSContextInterpreter interpretaContexto;
 
   JNSSwitchInterpreter(JNSHeadInterpreter interpretadorHead, JNSContextInterpreter interpretaContexto)
   {
     this.interpretadorMedia = new JNSMediaInterpreter(interpretadorHead);
     this.interpretadorHead = interpretadorHead;
     this.interpretaContexto = interpretaContexto;
   }
 
   NCLSwitch Interprets(JSONObject jnsSwtic, Object contexto) throws XMLException {
     NCLSwitch selecao = new NCLSwitch((String)jnsSwtic.get("id"));
     if (jnsSwtic.containsKey("refer")) {
       selecao.setRefer((NCLSwitch)this.interpretadorHead.body.findNode((String)jnsSwtic.get("reger")));
     }
     String[] chaves = JNSjSONComplements.getKeys(jnsSwtic);
     int i = 0;
     Vector regras = new Vector();
     Vector nodeID = new Vector();
     for (i = 0; i < chaves.length; i++) {
       NCLTestRule regra = null;
       String chave = chaves[i];
       if ((chave.contains("=")) || (chave.contains(">")) || (chave.contains("<")) || (chave.contains(" eq ")) || (chave.contains(" lt ")) || (chave.contains(" ne ")) || (chave.contains(" gte ")) || (chave.contains(" lte ")) || (chave.contains(" gt "))) {
         regra = this.interpretadorHead.getInterpretaRegra().InterRule(chave);
       }
       else if ((!chave.equals("default")) && (!chave.equals("refer")) && (!chave.equals("id")) && (!chave.equals("vars"))) {
         regra = (NCLTestRule)this.interpretadorHead.getInterpretaRegra().getBase().findRule(chave);
         if (regra == null) {
           regra = (NCLTestRule)this.interpretadorHead.getInterpretaRegra().getBase().findRule(chave + "_" + selecao.getId());
         }
       }
       if (regra != null) {
         NCLBindRule bind = new NCLBindRule();
         bind.setRule(regra);
         NCLNode no = null;
         if ((jnsSwtic.get(chave) instanceof JSONObject)) {
           JSONObject elemento = (JSONObject)jnsSwtic.get(chave);
           no = InterpretaNo(elemento, selecao);
         }
         else {
           no = selecao.findNode(chave);
         }
         if (no == null) {
           regras.add(bind);
           nodeID.add(chave);
         }
         else {
           bind.setConstituent(no);
           selecao.addBind(bind);
         }
       }
     }
 
     if (jnsSwtic.containsKey("default")) {
       NCLNode no = null;
       if ((jnsSwtic.get("default") instanceof JSONObject)) {
         JSONObject elemento = (JSONObject)jnsSwtic.get("default");
         InterpretaNo(elemento, selecao);
       }
       else {
         no = selecao.findNode((String)jnsSwtic.get("default"));
       }
       selecao.setDefaultComponent(no);
     }
     for (i = 0; i < regras.size(); i++) {
       NCLBindRule bind = (NCLBindRule)regras.get(i);
       bind.setConstituent(selecao.findNode((String)nodeID.get(i)));
       selecao.addBind(bind);
     }
     if (jnsSwtic.containsKey("switchPort")) {
       if ((jnsSwtic.get("switchPort") instanceof JSONObject)) {
         InterpretaSwitchPort((JSONObject)jnsSwtic.get("switchPort"), selecao);
       }
       else {
         JSONArray ports = (JSONArray)jnsSwtic.get("switchPort");
         for (i = 0; i < ports.size(); i++)
           InterpretaSwitchPort((JSONObject)ports.get(i), selecao);
       }
     }
     return selecao;
   }
 
   private void InterpretaSwitchPort(JSONObject switchPort, NCLSwitch selecao) throws XMLException {
     String nome = JNSjSONComplements.getKey(switchPort.toString());
     JSONArray jnsPorts = (JSONArray)switchPort.get(nome);
     NCLSwitchPort portaSwtich = new NCLSwitchPort(nome);
 
     for (int i = 0; i < jnsPorts.size(); i++)
       createSwitchPort((String)jnsPorts.get(i), selecao, portaSwtich);
     selecao.addPort(portaSwtich);
   }
 
   private NCLNode InterpretaNo(JSONObject elemento, NCLSwitch selecao) throws XMLException {
     NCLNode no = null;
     if (elemento.containsKey("context")) {
       no = this.interpretaContexto.Interprets((JSONArray)elemento.get("context"), selecao);
     }
     else {
       if (elemento.containsKey("media")) {
         no = this.interpretadorMedia.Interprets((JSONObject)elemento.get("media"));
       }
       else if (elemento.containsKey("switch")) {
         no = Interprets((JSONObject)elemento.get("switch"), selecao);
       }
       selecao.addNode(no);
     }
     return no;
   }
 
   private static void createSwitchPort(String nointerface, NCLSwitch selecao, NCLSwitchPort portaSwtich) throws XMLException {
     NCLMapping mapa = new NCLMapping();
     NCLNode no = selecao.getNode(nointerface.substring(0, nointerface.indexOf('.')));
     mapa.setComponent(no);
     mapa.setInterface(JNSaNaComplements.FindInterface(nointerface.substring(nointerface.indexOf('.') + 1), no));
     portaSwtich.addMapping(mapa);
   }
 }

/* Location:           C:\Users\Convidado\Desktop\pacoteCompleto\jnsTranslator.jar
 * Qualified Name:     JNSSwitchInterpreter
 * JD-Core Version:    0.6.2
 */