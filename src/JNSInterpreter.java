/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import br.uff.midiacom.ana.NCLBody;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import br.uff.midiacom.ana.NCLDoc;
import br.uff.midiacom.ana.NCLElement;
import br.uff.midiacom.ana.NCLHead;
import br.uff.midiacom.ana.connector.NCLCausalConnector;
import br.uff.midiacom.ana.connector.NCLConnectorBase;
import br.uff.midiacom.ana.connector.NCLSimpleCondition;
import br.uff.midiacom.ana.descriptor.NCLDescriptor;
import br.uff.midiacom.ana.descriptor.NCLDescriptorBase;
import br.uff.midiacom.ana.descriptor.NCLDescriptorSwitch;
import br.uff.midiacom.ana.interfaces.NCLArea;
import br.uff.midiacom.ana.interfaces.NCLInterface;
import br.uff.midiacom.ana.interfaces.NCLMapping;
import br.uff.midiacom.ana.interfaces.NCLPort;
import br.uff.midiacom.ana.interfaces.NCLProperty;
import br.uff.midiacom.ana.interfaces.NCLSwitchPort;
import br.uff.midiacom.ana.link.NCLLink;
import br.uff.midiacom.ana.meta.NCLMeta;
import br.uff.midiacom.ana.meta.NCLMetadata;
import br.uff.midiacom.ana.node.NCLContext;
import br.uff.midiacom.ana.node.NCLMedia;
import br.uff.midiacom.ana.node.NCLNode;
import br.uff.midiacom.ana.node.NCLSwitch;
import br.uff.midiacom.ana.region.NCLRegion;
import br.uff.midiacom.ana.region.NCLRegionBase;
import br.uff.midiacom.ana.rule.NCLCompositeRule;
import br.uff.midiacom.ana.rule.NCLRule;
import br.uff.midiacom.ana.rule.NCLRuleBase;
import br.uff.midiacom.ana.rule.NCLTestRule;
import br.uff.midiacom.ana.transition.NCLTransition;
import br.uff.midiacom.ana.transition.NCLTransitionBase;
import br.uff.midiacom.xml.XMLException;

/**
 *
 * @author Esdras Caleb
 */
public class JNSInterpreter {
    
    private Vector VetorDeAuxiliarSwitRule;
    private Vector GlobalPropertyVector;
    private JNSRuleInterpreter InterpretaRegra;
    
    JNSInterpreter(){
    	VetorDeAuxiliarSwitRule = new Vector();
    	GlobalPropertyVector = new Vector();
    }
    

/**
 * Metodo pai que interpleta o JSN
 * @param jsn objeto JSON que descreve o documento 
 * @return
 * @throws XMLException 
 * @throws Exception Retorna exeção em caso de ids mal formatados
 */
    public String InterpretsJNS(JSONObject jsn) throws XMLException {
        NCLDoc docN = new NCLDoc();

        //retirando o objeto ncl do jsn
        JSONObject jsonNCL = (JSONObject)jsn.get("ncl");
        String valor = (String)jsonNCL.get("id");
        if(valor != null)
            docN.setId(valor);
        else
            docN.setId("jsnNCL");
        valor = (String)jsonNCL.get("title");
        if(valor != null)
            docN.setId(valor);
        docN.setXmlns(br.uff.midiacom.ana.datatype.enums.NCLNamespace.EDTV);
        //retirando cabecalho
        JSONArray jsonHead = (JSONArray)jsonNCL.get("head");
        fillGlobalProperty((JSONArray) jsonNCL.get("body"));
        fillVars(jsonNCL);
        JNSHeadInterpreter interpretadorHead = new JNSHeadInterpreter();
    	InterpretaRegra = new JNSRuleInterpreter(VetorDeAuxiliarSwitRule,GlobalPropertyVector);
    	interpretadorHead.setInterpretaRegra(InterpretaRegra);
        NCLHead cabecalho = interpretadorHead.Interprets(jsonHead);
        docN.setHead(cabecalho);
        JSONArray jsonBody = (JSONArray)jsonNCL.get("body");
        JNSBodyInterpreter intepletaCorpo = new JNSBodyInterpreter(interpretadorHead);
        docN.setBody(intepletaCorpo.Interprets(jsonBody));
        interpretadorHead.InsertBases();
        docN.setHead(cabecalho);
        return docN.parse(0);
    }
    
///Preneche o vetor de variaveis glovais nao faz busca dentro de switch
private void fillGlobalProperty(JSONArray contexto) throws XMLException {
	int i=0;
	for(i=0;i<contexto.size();i++){
        JSONObject elemento = (JSONObject)contexto.get(i);
        if(elemento.containsKey("media")){
        	JSONObject jsnMedia = (JSONObject) elemento.get("media");
        	if(jsnMedia.containsKey("anchors")){
                JSONArray jsnAnchors = (JSONArray)jsnMedia.get("anchors");
                int j;
                for(j=0;j<jsnAnchors.size();j++){
                     JSONObject anchors = (JSONObject)jsnAnchors.get(i);
                     if(anchors.containsKey("property")){
                    	NCLProperty propriedade = JNSPropertyInterpreter.Interprets((JSONObject)anchors.get("property"));
                    	GlobalPropertyVector.add(propriedade);
                     }
                }
        	}
        }
        else if(elemento.containsKey("context")){
        	 elemento = (JSONObject)elemento.get("context");
        	 fillGlobalProperty((JSONArray)elemento.get("cBody"));
        }
  	}
}


	/**
     * 
     * @param jsonNCL
     */
    void fillVars(JSONObject jsonNCL) {
    	JSONArray head = (JSONArray)jsonNCL.get("head");
    	int i = 0;
    	for(i=0;i<head.size();i++){
          JSONObject elemento = (JSONObject)head.get(i);
          if(elemento.containsKey("descriptorSwitch")){
        	  elemento = (JSONObject)elemento.get("descriptorSwitch");
        	  fillAuxVector(elemento);
          }
    	}
    	JSONArray body = (JSONArray)jsonNCL.get("body");
    	searchInContext(body);
	}
    
    void searchInContext(JSONArray contexto){
    	int i=0;
    	for(i=0;i<contexto.size();i++){
            JSONObject elemento = (JSONObject)contexto.get(i);
            if(elemento.containsKey("switch")){
          	  elemento = (JSONObject)elemento.get("switch");
          	  fillAuxVector(elemento);
            }
            else if(elemento.containsKey("context")){
            	 elemento = (JSONObject)elemento.get("context");
            	 searchInContext((JSONArray)elemento.get("cBody"));
            }
      	}
    }
    
    private void fillAuxVector(JSONObject elemento){
    	if(elemento.containsKey("vars")){
  		  JSONArray vars = (JSONArray)elemento.get("vars");
  		  int j=0;
  		  Vector variaveisR = new Vector();
  		  Vector variaveisN = new Vector();
  		  for(j=0;j<vars.size();j++){
  			  String key = JNSjSONComplements.getKey(((JSONObject)vars.get(j)).toString());
  			  variaveisR.add(key);
  			  variaveisN.add((String)((JSONObject)vars.get(j)).get(key));
  		  }
  		  String[] keys = JNSjSONComplements.GetKeys(elemento);
  		  for(j=0;j<keys.length;j++){
  			  if(!keys[j].contains("=")&&keys[j]!="default" &&keys[j]!="refer" && keys[j]!="id"&&keys[j]!="vars"&&!keys[j].contains("<")&&!keys[j].contains(">")){
  				  Vector vetor = new Vector();
  				  vetor.add(elemento.get("id"));
  				  vetor.add(keys[j]);
  				  vetor.add(variaveisR);
  				  vetor.add(variaveisN);
  				  VetorDeAuxiliarSwitRule.add(vetor);
  			  }
  		  }
  	  }
    }

}
