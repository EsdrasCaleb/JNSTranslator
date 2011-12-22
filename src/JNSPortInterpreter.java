import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import br.uff.midiacom.ana.NCLBody;
import br.uff.midiacom.ana.interfaces.NCLInterface;
import br.uff.midiacom.ana.interfaces.NCLPort;
import br.uff.midiacom.ana.node.NCLContext;
import br.uff.midiacom.ana.node.NCLMedia;
import br.uff.midiacom.ana.node.NCLNode;
import br.uff.midiacom.ana.node.NCLSwitch;
import br.uff.midiacom.xml.XMLException;


public class JNSPortInterpreter {

	public static NCLPort[] Interprets(Object port,Object contexto) throws XMLException {
		NCLPort[] porta = null;
		if(port instanceof JSONObject){
			porta = new NCLPort[1];
			porta[0] = Interpreta((JSONObject)port,contexto);
		}
		else{
			porta = new NCLPort[((JSONArray)port).size()];
			for(int i=0;i<((JSONArray)port).size();i++){
				porta[i] = Interpreta((JSONObject)((JSONArray)port).get(i),contexto);
			}
		}
		return porta;
	}

	private static NCLPort Interpreta(JSONObject port,Object contexto) throws XMLException {
		String nome = JNSjSONComplements.getKey(port.toString());
        String inter = null;
        if(nome.contains(".")){
        	inter = nome.substring(nome.indexOf('.')+1,nome.length());
        	nome = nome.substring(0,nome.indexOf('.'));
        }
        NCLPort porta = new NCLPort(nome);
        nome = (String) port.get(nome);
        NCLNode componente = null;
        NCLInterface nclInterface = null;
        if(contexto instanceof NCLContext)
        	componente = ((NCLContext)contexto).findNode(nome);
        else if(contexto instanceof NCLBody)
        	componente = ((NCLBody)contexto).findNode(nome);
        else 
        	componente = ((NCLSwitch)contexto).findNode(nome);
        porta.setComponent(componente);
        if(inter!=null){
        	if(componente instanceof NCLMedia)
        		nclInterface = ((NCLMedia)componente).findInterface(inter);
        	else if(componente instanceof NCLContext)
        		nclInterface = ((NCLContext)componente).findInterface(inter);
        	else if(componente instanceof NCLSwitch)
        		nclInterface = ((NCLSwitch)componente).findInterface(inter);
        	porta.setInterface(nclInterface);
        }
		return porta;
	}

}
