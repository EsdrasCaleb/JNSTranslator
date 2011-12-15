import java.util.Vector;

import org.json.simple.JSONObject;

import br.uff.midiacom.ana.interfaces.NCLInterface;
import br.uff.midiacom.ana.interfaces.NCLMapping;
import br.uff.midiacom.ana.interfaces.NCLSwitchPort;
import br.uff.midiacom.ana.node.NCLNode;
import br.uff.midiacom.ana.node.NCLSwitch;
import br.uff.midiacom.ana.node.NCLSwitchBindRule;
import br.uff.midiacom.ana.rule.NCLTestRule;
import br.uff.midiacom.xml.XMLException;


public class JNSSwitchInterpreter {
	static NCLSwitch Interprets(JSONObject jnsSwtic,Object contexto) throws XMLException {
        NCLSwitch selecao = new NCLSwitch((String)jnsSwtic.get("id"));
        if(jnsSwtic.containsKey("refer")){
        	selecao.setRefer((NCLSwitch)JNSaNaComplements.FindNode((String)jnsSwtic.get("reger"),contexto));
        }
        String[] chaves = JNSjSONComplements.GetKeys(jnsSwtic);
    	int i = 0;
    	Vector regras = new Vector();
    	Vector nodeID = new Vector();
    	for(i=0;i<chaves.length;i++){
    		NCLTestRule regra = null;
    		if(chaves[i].contains("=") ||chaves[i].contains(">") || chaves[i].contains("<")){
    			regra = JNSRuleInterpreter.InterRule(chaves[i]);
    		}
    		else if(chaves[i]!="default"&&chaves[i]!="refer"&&chaves[i]!="id"&&chaves[i]!="vars"){
    			regra = JNSRuleInterpreter.Base.findRule(chaves[i]);
    			if(regra==null){
    				regra =JNSRuleInterpreter.Base.findRule(chaves[i]+"_"+selecao.getId());
    			}
    		}
    		if(regra != null){
    			NCLSwitchBindRule bind = new NCLSwitchBindRule();
    			bind.setRule(regra);
    			NCLNode no = null;
    			if(jnsSwtic.get(chaves[i]) instanceof JSONObject){
    				JSONObject elemento = (JSONObject)((JSONObject)jnsSwtic.get(chaves[i]));
				 	if(elemento.containsKey("context")){
				 		no = JNSContextInterpreter.Interprets((JSONObject)elemento.get("context"),selecao);
		            }
		            else if(elemento.containsKey("media")){
		            	no = JNSMediaInterpreter.Interprets((JSONObject)elemento.get("media"),selecao);
		            } 
		            else if(elemento.containsKey("switch")){
		            	no = Interprets((JSONObject)elemento.get("switch"),selecao);
		            }
    				selecao.addNode(no);
    			}
    			else{
    				no = GetConstituent(chaves[i],selecao);    				
    			}
    			if(no == null){
    				regras.add(bind);
    				nodeID.add((String)jnsSwtic.get(chaves[i]));
				}
    			else{
    				bind.setConstituent(no);
    				selecao.addBind(bind);
    			}
    		}
    	}
    	if(jnsSwtic.containsKey("default")){
    		NCLNode no = null;
    		if(jnsSwtic.get("default") instanceof JSONObject){
    			JSONObject elemento = (JSONObject)((JSONObject)jnsSwtic.get(chaves[i]));
			 	if(elemento.containsKey("context")){
			 		no = JNSContextInterpreter.Interprets((JSONObject)elemento.get("context"),selecao);
	            }
	            else if(elemento.containsKey("media")){
	            	no = JNSMediaInterpreter.Interprets((JSONObject)elemento.get("media"),selecao);
	            } 
	            else if(elemento.containsKey("switch")){
	            	no = Interprets((JSONObject)elemento.get("switch"),selecao);
	            }
    			selecao.addNode(no);
    		}
    		else{
    			no = JNSaNaComplements.FindNode((String)jnsSwtic.get(chaves[i]),contexto); //a corrigir switchport
    		}
    		selecao.setDefaultComponent(no);
    	}
    	for(i=0;i<regras.size();i++){
    		NCLSwitchBindRule bind = new NCLSwitchBindRule();
    		bind.setConstituent(GetConstituent((String)nodeID.get(i),selecao));
    		selecao.addBind(bind);
    	}
        return selecao;
    }
	
	private static NCLNode GetConstituent(String nome,NCLSwitch selecao) throws XMLException {
		NCLNode no = null;
		String inter = null;
		if(nome.contains(".")){
			inter = nome.substring(nome.indexOf('.')+1,nome.length());
        	nome = nome.substring(0,nome.indexOf('.'));
        	no = selecao.findNode(nome);
        	if(no!=null){
        		no = (NCLNode) createSwitchPort(no,JNSaNaComplements.FindInterface(inter,no));
        		selecao.addPort((NCLSwitchPort)no);
        	}
		}
		else
			no = selecao.findNode(nome);
		return no;
	}

	private static NCLSwitchPort createSwitchPort(NCLNode no,NCLInterface inter) throws XMLException {
		NCLSwitchPort portaSwtich = new NCLSwitchPort(no.getId()+"_"+inter.getId());
		NCLMapping mapa = new NCLMapping();
		mapa.setComponent(no);
		mapa.setInterface(inter);
		portaSwtich.addMapping(mapa);
		return portaSwtich;
	}
}
