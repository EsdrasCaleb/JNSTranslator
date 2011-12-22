import java.util.Vector;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import br.uff.midiacom.ana.NCLBody;
import br.uff.midiacom.ana.interfaces.NCLPort;
import br.uff.midiacom.ana.link.NCLLink;
import br.uff.midiacom.ana.meta.NCLMetadata;
import br.uff.midiacom.ana.node.NCLContext;
import br.uff.midiacom.xml.XMLException;


public class JNSBodyInterpreter {
	private JNSHeadInterpreter interpretadorHead;
	private JNSMediaInterpreter interpretadorMedia;
	private JNSContextInterpreter interpretaContexto;
	private JNSLinkInterpreter interpretaElo;
	private JNSSwitchInterpreter interpretaSwitch;
	
	JNSBodyInterpreter(JNSHeadInterpreter interpretadorHead){
		this.interpretadorHead = interpretadorHead; 
		interpretadorMedia = new JNSMediaInterpreter(interpretadorHead);
		interpretaContexto = new JNSContextInterpreter(interpretadorHead);
		interpretaElo = new JNSLinkInterpreter(interpretadorHead);
		interpretaSwitch = new JNSSwitchInterpreter(interpretadorHead);
	}
	NCLBody Interprets(JSONArray jsonBody) throws XMLException{
		NCLBody corpo = new NCLBody();
        int i;
        Vector portas = new Vector();
        for(i=0;i<jsonBody.size();i++){
            JSONObject elemento = (JSONObject)jsonBody.get(i);
            if(elemento.containsKey("media")){
            	corpo.addNode(interpretadorMedia.Interprets((JSONObject)elemento.get("media"),corpo));
            }
            else if(elemento.containsKey("context")){
        		if(interpretaContexto == null)
        			interpretaContexto = new JNSContextInterpreter(interpretadorHead);
        		NCLContext contexto = interpretaContexto.Interprets((JSONObject)elemento.get("context"),corpo);
        		corpo.addNode(contexto);
                
            }
            else if(elemento.containsKey("meta")){
            	corpo.addMeta(JNSMetaInterpreter.InterMeta((JSONObject)elemento.get("meta")));
            }
            else if(elemento.containsKey("metadata")){
                NCLMetadata  meta = new NCLMetadata();
                meta.setRDFTree((String)elemento.get("metadata"));
                corpo.addMetadata(meta);
            }
            else if(elemento.containsKey("property")){
            	corpo.addProperty(JNSPropertyInterpreter.Interprets((JSONObject)elemento.get("property")));
            }
            else if(elemento.containsKey("link")){
            	NCLLink link = interpretaElo.Interprets((JSONObject)elemento.get("link"),corpo);
            	corpo.addLink(link);
            }
            else if(elemento.containsKey("switch")){
            	corpo.addNode(interpretaSwitch.Interprets((JSONObject)elemento.get("switch"),corpo));
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
	        	corpo.addPort(portasLocais[j]);
		}
        
        return corpo;
    }
}
