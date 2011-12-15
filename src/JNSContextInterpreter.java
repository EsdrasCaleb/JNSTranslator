import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import br.uff.midiacom.ana.interfaces.NCLPort;
import br.uff.midiacom.ana.meta.NCLMetadata;
import br.uff.midiacom.ana.node.NCLContext;
import br.uff.midiacom.xml.XMLException;


public class JNSContextInterpreter extends JNSBodyInterpreter{
	
	
	JNSContextInterpreter(JNSHeadInterpreter interpretadorHead) {
		super(interpretadorHead);
	}

	NCLContext Interprets(JSONObject jsnContext,Object corpo) throws XMLException {
        NCLContext contexto = new NCLContext((String)jsnContext.get("id"));
        JSONArray jsnCBody = (JSONArray)jsnContext.get("cBody");
        if(jsnContext.containsKey("refer")){
            contexto.setRefer((NCLContext) JNSaNaComplements.FindNode((String)jsnContext.get("refer"),corpo));
        }
        int i = 0;
        for(i=0;i<jsnCBody.size();i++){
            JSONObject elemento = (JSONObject)jsnCBody.get(i);

            if(elemento.containsKey("context")){
            	if(interpretaContexto == null)
        			interpretaContexto = new JNSContextInterpreter(interpretadorHead);
                contexto.addNode(Interprets((JSONObject)elemento.get("context"),contexto)); //a resolver
            }
            else if(elemento.containsKey("media")){
                contexto.addNode(interpretadorMedia.Interprets((JSONObject)elemento.get("media"),contexto));
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
                contexto.addProperty(JNSPropertyInterpreter.Interprets(((JSONObject)elemento.get("property"))));
            }
            else if(elemento.containsKey("link")){
                contexto.addLink(JNSLinkInterpreter.Interprets((JSONObject)elemento.get("link"),contexto));
            }
            else if(elemento.containsKey("switch")){
                contexto.addNode(JNSSwitchInterpreter.Interprets((JSONObject)elemento.get("switch"),contexto));
            }
            else if(elemento.containsKey("port")){
                NCLPort[] portas = JNSPortInterpreter.Interprets(elemento.get("port"),contexto);
                int j;
                for(j=0;j<portas.length;j++)
                	contexto.addPort(portas[j]);
            }
        }

        return contexto;
    }
}
