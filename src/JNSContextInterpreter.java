 import br.uff.midiacom.ana.NCLBody;
 import br.uff.midiacom.ana.link.NCLLink;
 import br.uff.midiacom.ana.meta.NCLMetadata;
 import br.uff.midiacom.ana.node.NCLContext;
 import br.uff.midiacom.ana.node.NCLSwitch;
 import br.uff.midiacom.ana.util.exception.XMLException;
 import java.util.Vector;
 import org.json.simple.JSONArray;
 import org.json.simple.JSONObject;
 
 public class JNSContextInterpreter
 {
   private JNSHeadInterpreter interpretadorHead;
   private JNSMediaInterpreter interpretadorMedia;
   private JNSLinkInterpreter interpretaElo;
   private JNSSwitchInterpreter interpretaSwitch;
 
   JNSContextInterpreter(JNSHeadInterpreter interpretadorHead, JNSLinkInterpreter interpretaElo)
   {
     this.interpretadorHead = interpretadorHead;
     this.interpretadorMedia = new JNSMediaInterpreter(interpretadorHead);
     this.interpretaElo = interpretaElo;
     this.interpretaSwitch = new JNSSwitchInterpreter(interpretadorHead, this);
   }
 
   NCLContext Interprets(JSONArray jsnContext, Object corpo) throws XMLException {
     Vector portas = new Vector();
     NCLContext contexto = new NCLContext();
     if ((corpo instanceof NCLBody)) {
       ((NCLBody)corpo).addNode(contexto);
     }
     else if ((corpo instanceof NCLContext)) {
       ((NCLContext)corpo).addNode(contexto);
     }
     else {
       ((NCLSwitch)corpo).addNode(contexto);
     }
     int i = 0;
     for (i = 0; i < jsnContext.size(); i++) {
       JSONObject elemento = (JSONObject)jsnContext.get(i);
       if (elemento.containsKey("id"))
         contexto.setId((String)elemento.get("id"));
       if (elemento.containsKey("refer"))
         contexto.setRefer((NCLContext)this.interpretadorHead.body.findNode((String)elemento.get("refer")));
       if (elemento.containsKey("media")) {
         contexto.addNode(this.interpretadorMedia.Interprets((JSONObject)elemento.get("media")));
       }
       if (elemento.containsKey("context")) {
         Interprets((JSONArray)elemento.get("context"), contexto);
       }
       if (elemento.containsKey("meta")) {
         contexto.addMeta(JNSMetaInterpreter.InterMeta((JSONObject)elemento.get("meta")));
       }
       else if (elemento.containsKey("metadata")) {
         NCLMetadata meta = new NCLMetadata();
         meta.setRDFTree((String)elemento.get("metadata"));
         contexto.addMetadata(meta);
       }
       else if (elemento.containsKey("property")) {
         contexto.addProperty(JNSPropertyInterpreter.Interprets((JSONObject)elemento.get("property")));
       }
       else if (elemento.containsKey("link")) {
         NCLLink link = this.interpretaElo.Interprets((JSONObject)elemento.get("link"), contexto);
         contexto.addLink(link);
       }
       else if (elemento.containsKey("switch")) {
         contexto.addNode(this.interpretaSwitch.Interprets((JSONObject)elemento.get("switch"), contexto));
       }
       else if (elemento.containsKey("port")) {
         portas.add(elemento);
       }
     }
     for (i = 0; i < portas.size(); i++) {
       JSONObject elemento = (JSONObject)portas.get(i);
       JNSPortInterpreter.Interprets((JSONObject)elemento.get("port"), contexto);
     }
     return contexto;
   }
}