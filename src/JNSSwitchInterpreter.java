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
	private JNSHeadInterpreter interpretadorHead;
	private JNSMediaInterpreter interpretadorMedia;
	private JNSContextInterpreter interpretaContexto;
	
	JNSSwitchInterpreter(JNSHeadInterpreter interpretadorHead){
		interpretadorMedia = new JNSMediaInterpreter(interpretadorHead);
		this.interpretadorHead = interpretadorHead;
	}
	
	NCLSwitch Interprets(JSONObject jnsSwtic,Object contexto) throws XMLException {
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
    		String chave = chaves[i];
    		if(chave.contains("=") ||chave.contains(">") || chave.contains("<")|| chave.contains(" eq ")|| chave.contains(" lt ")||chave.contains(" ne ")||chave.contains(" gte ")||chave.contains(" lte ")||chave.contains(" gt ")){
    			regra = interpretadorHead.getInterpretaRegra().InterRule(chave);
    			interpretadorHead.getInterpretaRegra().getBase().addRule(regra);
    		}
    		else if(!chave.equals("default")&&!chave.equals("refer")&&!chave.equals("id")&&!chave.equals("vars")){
    			regra = interpretadorHead.getInterpretaRegra().getBase().findRule(chave);
    			if(regra==null){
    				regra =interpretadorHead.getInterpretaRegra().getBase().findRule(chave+"_"+selecao.getId());
    			}
    		}
    		if(regra != null){
    			NCLSwitchBindRule bind = new NCLSwitchBindRule();
    			bind.setRule(regra);
    			NCLNode no = null;
    			if(jnsSwtic.get(chave) instanceof JSONObject){
    				JSONObject elemento = (JSONObject)((JSONObject)jnsSwtic.get(chave));
    				no = InterpretaNo(elemento,selecao);
    				selecao.addNode(no);
    			}
    			else{
    				no = GetConstituent(chave,selecao);    				
    			}
    			if(no == null){
    				regras.add(bind);
    				nodeID.add(chave);
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
    			JSONObject elemento = (JSONObject)((JSONObject)jnsSwtic.get("default"));			 	
    			no = InterpretaNo(elemento,selecao);
    			selecao.addNode(no);
    		}
    		else{
    			no = JNSaNaComplements.FindNode((String)jnsSwtic.get(chaves[i]),contexto); //a corrigir switchport
    		}
    		selecao.setDefaultComponent(no);
    	}
    	for(i=0;i<regras.size();i++){
    		NCLSwitchBindRule bind = (NCLSwitchBindRule) regras.get(i);
    		bind.setConstituent(GetConstituent((String)nodeID.get(i),selecao));
    		selecao.addBind(bind);
    	}
        return selecao;
    }
	
	private NCLNode InterpretaNo(JSONObject elemento,NCLSwitch selecao) throws XMLException {
	 	NCLNode no = null;
		if(elemento.containsKey("context")){
			if(interpretaContexto== null)
				interpretaContexto = new JNSContextInterpreter(interpretadorHead);
	 		no = interpretaContexto.Interprets((JSONObject)elemento.get("context"),selecao);
        }
        else if(elemento.containsKey("media")){
        	JSONObject midia = (JSONObject)elemento.get("media");
        	no = interpretadorMedia.Interprets(midia,selecao);
        } 
        else if(elemento.containsKey("switch")){
        	no = Interprets((JSONObject)elemento.get("switch"),selecao);
        }
		return no;
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
