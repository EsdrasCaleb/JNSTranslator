import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import br.uff.midiacom.ana.NCLHead;
import br.uff.midiacom.ana.meta.NCLMetadata;
import br.uff.midiacom.xml.XMLException;


public class JNSHeadInterpreter {
	private NCLHead Head;
	private JNSRegionInterpreter InterpretadorRegiao;
	private JNSDescriptorInterpreter InterpretadorDescritor;
	private JNSTransitionInterpreter InterpretaTransicao;
	private JNSRuleInterpreter InterpretaRegra;
	private JNSConnectorInterpreter InterpretaConnector;
	
	void setInterpretaRegra(JNSRuleInterpreter InterpretaRegra){
		this.InterpretaRegra = InterpretaRegra;
	}
	
	JNSRuleInterpreter getInterpretaRegra(){
		return InterpretaRegra;
	}
	
	JNSRegionInterpreter getInterpretadorRegiao(){
		return InterpretadorRegiao;
	}
	
	JNSDescriptorInterpreter getInterpretadorDescritor(){
		return InterpretadorDescritor;
	}
	
	JNSConnectorInterpreter getInterpretaConnector(){
		return InterpretaConnector;
	}
	
	JNSTransitionInterpreter getInterpretaTransicao(){
		return InterpretaTransicao;
	}
	
	JNSHeadInterpreter() throws XMLException{
		Head = new NCLHead();
	}
	
	public NCLHead Interprets(JSONArray jsonHead) throws XMLException{
        int i;
        InterpretadorRegiao = new JNSRegionInterpreter();
        InterpretadorDescritor = new JNSDescriptorInterpreter(this);
        InterpretaTransicao = new JNSTransitionInterpreter();
        InterpretaConnector = new JNSConnectorInterpreter();
        for(i=0;i<jsonHead.size();i++){
            JSONObject elemento = (JSONObject)jsonHead.get(i);

            if(elemento.containsKey("transtion")){
            	InterpretaTransicao.Add((JSONObject)elemento.get("transtion"));
            }
            else if(elemento.containsKey("descriptor")){
            	InterpretadorDescritor.Add((JSONObject)elemento.get("descriptor"));
            }
            else if(elemento.containsKey("descriptorSwitch")){
            	InterpretadorDescritor.AddSwitc((JSONObject)elemento.get("descriptorSwitch"));
            }
            else if(elemento.containsKey("region")){
            	InterpretadorRegiao.Add((JSONObject)elemento.get("region"));
            }
            else if(elemento.containsKey("meta")){
                Head.addMeta(JNSMetaInterpreter.InterMeta((JSONObject)elemento.get("meta")));
            }
            else if(elemento.containsKey("metadata")){
                Head.addMetadata(JNSMetaInterpreter.InterMetadata((String)elemento.get("metadata")));
            }
            else if(elemento.containsKey("rule")){
            	InterpretaRegra.Add((JSONObject)elemento.get("rule"));
            }
            else if(elemento.containsKey("connector")){
            	InterpretaConnector.Add((JSONObject)elemento.get("connector"));
            }
        }
        
        if(InterpretaTransicao.getBase().hasTransition())
        	Head.setTransitionBase(InterpretaTransicao.getBase());
        Head.addRegionBase(InterpretadorRegiao.getBase());
        InsertBases();
        return Head;
    }
	
	/*
	 * Classe que insere as bases opcionais caso elas tenham algum conteudo
	 */
	public void InsertBases() throws XMLException{
		InterpretadorDescritor.ReInterprets();
		if(InterpretaRegra.getBase().hasRule())
        	Head.setRuleBase(InterpretaRegra.getBase());
        if(InterpretadorDescritor.getBase().hasDescriptor())
        	Head.setDescriptorBase(InterpretadorDescritor.getBase());
        if(InterpretaConnector.getBase().hasCausalConnector())
        	Head.setConnectorBase(InterpretaConnector.getBase());
	}
}
