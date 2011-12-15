import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import br.uff.midiacom.ana.NCLBody;
import br.uff.midiacom.ana.interfaces.NCLPort;
import br.uff.midiacom.ana.link.NCLLink;
import br.uff.midiacom.ana.meta.NCLMetadata;
import br.uff.midiacom.xml.XMLException;


public class JNSBodyInterpreter {
	JNSHeadInterpreter interpretadorHead;
	JNSMediaInterpreter interpretadorMedia;
	JNSContextInterpreter interpretaContexto;
	JNSLinkInterpreter interpretaElo;
	JNSSwitchInterpreter interpretaSwitch;
	
	JNSBodyInterpreter(JNSHeadInterpreter interpretadorHead){
		this.interpretadorHead = interpretadorHead; 
		interpretadorMedia = new JNSMediaInterpreter(interpretadorHead);
	}
	NCLBody Interprets(JSONArray jsonBody) throws XMLException{
		NCLBody body = new NCLBody();
        int i;
        
        for(i=0;i<jsonBody.size();i++){
            JSONObject elemento = (JSONObject)jsonBody.get(i);
            if(elemento.containsKey("media")){
                body.addNode(interpretadorMedia.Interprets((JSONObject)elemento.get("media"),body));
            }
            else if(elemento.containsKey("context")){
        		if(interpretaContexto == null)
        			interpretaContexto = new JNSContextInterpreter(interpretadorHead);
                body.addNode(interpretaContexto.Interprets((JSONObject)elemento.get("context"),body));
            }
            else if(elemento.containsKey("meta")){
               body.addMeta(JNSMetaInterpreter.InterMeta((JSONObject)elemento.get("meta")));
            }
            else if(elemento.containsKey("metadata")){
                NCLMetadata  meta = new NCLMetadata();
                meta.setRDFTree((String)elemento.get("metadata"));
                body.addMetadata(meta);
            }
            else if(elemento.containsKey("property")){
                body.addProperty(JNSPropertyInterpreter.Interprets((JSONObject)elemento.get("property")));
            }
            else if(elemento.containsKey("link")){
            	NCLLink link = interpretaElo.Interprets((JSONObject)elemento.get("link"),body);
                body.addLink(link);
            }
            else if(elemento.containsKey("switch")){
                body.addNode(interpretaSwitch.Interprets((JSONObject)elemento.get("switch"),body));
            }
            else if(elemento.containsKey("port")){
            	NCLPort[] portas = JNSPortInterpreter.Interprets(elemento.get("port"),body);
                int j;
                for(j=0;j<portas.length;j++)
                	body.addPort(portas[j]);
            }
        }

        return body;
    }
}
