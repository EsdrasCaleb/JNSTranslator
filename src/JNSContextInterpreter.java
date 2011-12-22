import java.util.Vector;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import br.uff.midiacom.ana.interfaces.NCLPort;
import br.uff.midiacom.ana.link.NCLLink;
import br.uff.midiacom.ana.meta.NCLMetadata;
import br.uff.midiacom.ana.node.NCLContext;
import br.uff.midiacom.xml.XMLException;


public class JNSContextInterpreter {
	private JNSHeadInterpreter interpretadorHead;
	private JNSMediaInterpreter interpretadorMedia;
	private JNSLinkInterpreter interpretaElo;
	private JNSSwitchInterpreter interpretaSwitch;
	
	JNSContextInterpreter(JNSHeadInterpreter interpretadorHead) {
		this.interpretadorHead = interpretadorHead;
		interpretadorMedia = new JNSMediaInterpreter(interpretadorHead);
		interpretaElo = new JNSLinkInterpreter(interpretadorHead);
		interpretaSwitch = new JNSSwitchInterpreter(interpretadorHead);
	}

	NCLContext Interprets(JSONObject jsnContext,Object corpo) throws XMLException {
        NCLContext contexto = new NCLContext((String)jsnContext.get("id"));
        JSONArray jsnCBody = (JSONArray)jsnContext.get("cBody");
        Vector portas = new Vector();
        if(jsnContext.containsKey("refer")){
            contexto.setRefer((NCLContext) JNSaNaComplements.FindNode((String)jsnContext.get("refer"),corpo));
        }
        int i = 0;
        for(i=0;i<jsnCBody.size();i++){
            JSONObject elemento = (JSONObject)jsnCBody.get(i);
            if(elemento.containsKey("media")){
            	contexto.addNode(interpretadorMedia.Interprets((JSONObject)elemento.get("media"),contexto));
            }
            else if(elemento.containsKey("context")){
        		contexto.addNode(Interprets((JSONObject)elemento.get("context"),contexto));
            }
            else if(elemento.containsKey("meta")){
            	contexto.addMeta(JNSMetaInterpreter.InterMeta((JSONObject)elemento.get("meta")));
            }
            else if(elemento.containsKey("metadata")){
                NCLMetadata  meta = new NCLMetadata();
                meta.setRDFTree((String)elemento.get("metadata"));
                contexto.addMetadata(meta);
            }
            else if(elemento.containsKey("property")){
            	contexto.addProperty(JNSPropertyInterpreter.Interprets((JSONObject)elemento.get("property")));
            }
            else if(elemento.containsKey("link")){
            	NCLLink link = interpretaElo.Interprets((JSONObject)elemento.get("link"),contexto);
            	contexto.addLink(link);
            }
            else if(elemento.containsKey("switch")){
            	contexto.addNode(interpretaSwitch.Interprets((JSONObject)elemento.get("switch"),contexto));
            }
            else if(elemento.containsKey("port")){
            	portas.add(elemento);
            }
        }
        for(i=0;i<portas.size();i++){ 
			JSONObject elemento = (JSONObject) portas.get(i);
			NCLPort[] portasLocais = JNSPortInterpreter.Interprets(elemento.get("port"),corpo);
	        int j;
	        for(j=0;j<portasLocais.length;j++)
	        	contexto.addPort(portasLocais[j]);
		}
        
        return contexto;
    }
}
