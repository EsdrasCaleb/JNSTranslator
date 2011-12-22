import java.util.Vector;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import br.uff.midiacom.ana.NCLBody;
import br.uff.midiacom.ana.NCLElement;
import br.uff.midiacom.ana.connector.NCLAction;
import br.uff.midiacom.ana.connector.NCLAssessmentStatement;
import br.uff.midiacom.ana.connector.NCLAttributeAssessment;
import br.uff.midiacom.ana.connector.NCLCausalConnector;
import br.uff.midiacom.ana.connector.NCLCompoundAction;
import br.uff.midiacom.ana.connector.NCLCompoundCondition;
import br.uff.midiacom.ana.connector.NCLCondition;
import br.uff.midiacom.ana.connector.NCLConnectorParam;
import br.uff.midiacom.ana.connector.NCLRole;
import br.uff.midiacom.ana.connector.NCLSimpleAction;
import br.uff.midiacom.ana.connector.NCLSimpleCondition;
import br.uff.midiacom.ana.connector.NCLValueAssessment;
import br.uff.midiacom.ana.datatype.auxiliar.AssValueParamType;
import br.uff.midiacom.ana.datatype.auxiliar.AssValueType;
import br.uff.midiacom.ana.datatype.auxiliar.ByParamType;
import br.uff.midiacom.ana.datatype.auxiliar.DoubleParamType;
import br.uff.midiacom.ana.datatype.auxiliar.IntegerParamType;
import br.uff.midiacom.ana.datatype.auxiliar.KeyParamType;
import br.uff.midiacom.ana.datatype.auxiliar.StringParamType;
import br.uff.midiacom.ana.datatype.enums.NCLActionOperator;
import br.uff.midiacom.ana.datatype.enums.NCLAttributeType;
import br.uff.midiacom.ana.datatype.enums.NCLComparator;
import br.uff.midiacom.ana.datatype.enums.NCLConditionOperator;
import br.uff.midiacom.ana.datatype.enums.NCLDefaultActionRole;
import br.uff.midiacom.ana.datatype.enums.NCLDefaultValueAssessment;
import br.uff.midiacom.ana.datatype.enums.NCLEventTransition;
import br.uff.midiacom.ana.datatype.enums.NCLEventType;
import br.uff.midiacom.ana.datatype.enums.NCLParamInstance;
import br.uff.midiacom.ana.datatype.enums.NCLDefaultConditionRole;
import br.uff.midiacom.ana.datatype.enums.NCLKey;
import br.uff.midiacom.ana.datatype.enums.NCLEventAction;
import br.uff.midiacom.ana.interfaces.NCLInterface;
import br.uff.midiacom.ana.interfaces.NCLProperty;
import br.uff.midiacom.ana.link.NCLBind;
import br.uff.midiacom.ana.link.NCLLink;
import br.uff.midiacom.ana.link.NCLParam;
import br.uff.midiacom.ana.node.NCLMedia;
import br.uff.midiacom.ana.node.NCLNode;
import br.uff.midiacom.xml.XMLException;
import br.uff.midiacom.xml.datatype.number.MaxType;


public class JNSLinkInterpreter {
	private int ConnectorIndex = 0;
	private int IndiceAssassement = 0;
	private JNSHeadInterpreter interpretadorHead;
	
	JNSLinkInterpreter(JNSHeadInterpreter interpretadorHead){
		this.interpretadorHead = interpretadorHead;
	}
	
	NCLLink Interprets(JSONObject jnsLink,Object contexto) throws XMLException{
        NCLLink elo = new NCLLink();
        if(jnsLink.containsKey("id")){
        	elo.setId((String) jnsLink.get("id"));
        }
        if(jnsLink.containsKey("expression")){
        	interpretsExpression(((String)jnsLink.get("expression")).replace('\'', '\"'),elo,contexto);
        }
        else{
        	if(jnsLink.containsKey("xconector")){
        		elo.setXconnector(JNSaNaComplements.FindConnector((String)jnsLink.get("xconector"),interpretadorHead.getInterpretaConnector().getBase()));
        	}
        	if(jnsLink.containsKey("params")){ // pegar parametros e por eles num array
        		JSONArray jsnParams = (JSONArray) jnsLink.get("params");
        		for(int i=0;i<jsnParams.size();i++){ // preenche vetor com as strings de parametro
    				elo.addLinkParam(interpretsParameter((JSONObject)jsnParams.get(i),NCLParamInstance.LINKPARAM,elo));
        		}
        		
        	} 
        	if(jnsLink.containsKey("binds")){//interpretar binds e setar coisas com o array
        		JSONArray jnsBinds =(JSONArray)jnsLink.get("binds");
        		for(int i=0;i<jnsBinds.size();i++){
        			String role = (String)JNSjSONComplements.getKey(jnsBinds.get(i).toString());
        			Object obBind = ((JSONObject)jnsBinds.get(i)).get(role);
        			if(obBind instanceof JSONArray){
        				for(int j=0;j<((JSONArray)obBind).size();j++)
        					elo.addBind(interpretsBind(((JSONArray)obBind).get(j),role,contexto,elo));
        			}
        			else{
        				elo.addBind(interpretsBind(obBind,role,contexto,elo));
        			}
        		}
        	}
        }
        return elo;
    }

	private void interpretsExpression(String expression, NCLLink elo,Object contexto) throws XMLException {
		String expresaoCondicao = expression.substring(0,expression.indexOf("then"));
        String expressaoAcao =  expression.substring(expression.indexOf("then")+4,expression.length());
        NCLCausalConnector nConnector = new NCLCausalConnector("Connector_Padrao_Interpreter"+ConnectorIndex++);
		nConnector.setCondition(interpretsCondExpression(expresaoCondicao.trim(),elo,contexto));
		nConnector.setAction(interpretsActExpression(expressaoAcao.trim(),elo,contexto));
		interpretadorHead.getInterpretaConnector().getBase().addCausalConnector(nConnector);
		elo.setXconnector(nConnector);
		IndiceAssassement = 0;
	}

	private NCLCondition interpretsCondExpression(String expresaoCondicao,NCLLink elo,Object contexto) throws XMLException {
		NCLCondition codicaoRetorno = null;
		if(expresaoCondicao.contains("and")||expresaoCondicao.contains("or")){
			codicaoRetorno = new NCLCompoundCondition();
			String operador = null;
			String[] subExpressao = null;
			while(operador == null){
				subExpressao = expresaoCondicao.split("\\(.*\\)");
				if(subExpressao[0].contains("and")){
					operador = "and";
					((NCLCompoundCondition)codicaoRetorno).setOperator(NCLConditionOperator.AND);
				}
				else if(subExpressao[0].contains("or")){
					operador = "or";
					((NCLCompoundCondition)codicaoRetorno).setOperator(NCLConditionOperator.OR);
				}
				else{
					subExpressao = expresaoCondicao.trim().substring(1,expresaoCondicao.indexOf(subExpressao[0])-1).split("\\(.*\\)");
					if(subExpressao[0].contains("delay")){
						subExpressao[0] = subExpressao[0].trim();
	    				String delay = subExpressao[0].substring(subExpressao[0].indexOf('=')+1,subExpressao[0].length()).trim();
	    				codicaoRetorno.setDelay(new DoubleParamType(Double.parseDouble(delay)));
	    			}
				}
			}
			for(int i=0;i<subExpressao.length;i++){
				if(!expresaoCondicao.startsWith(subExpressao[i])){
					((NCLCompoundCondition)codicaoRetorno).addCondition(interpretsCondExpression(expresaoCondicao.trim().substring(1,expresaoCondicao.trim().indexOf(subExpressao[i])-1).trim(),elo,contexto));
				}
				String[] pequenasExpessoes = subExpressao[i].toLowerCase().split(operador);
				for(int j=0;j<pequenasExpessoes.length;j++)
				{
					if(pequenasExpessoes[j].trim()!="")
						interpretsCondExpression(pequenasExpessoes[j],elo,contexto);
				}
				expresaoCondicao = expresaoCondicao.substring(expresaoCondicao.indexOf(subExpressao[i]), expresaoCondicao.length()).replace(subExpressao[i], "").trim();
			}
		}
		else{
	    	String roleString = "";
	    	if(expresaoCondicao.toLowerCase().startsWith("onbegin")){
	    		if(expresaoCondicao.toLowerCase().startsWith("onbeginatribuition")){    			
	    			roleString = "ONBEGINATRIBUITION";
		    	}
	    		else{
	    			roleString = "OnBegin";
	    		}
	    	}
	    	else if(expresaoCondicao.toLowerCase().startsWith("onend")){
	    		if(expresaoCondicao.toLowerCase().startsWith("onendatribuition")){
	    			roleString = "ONENDATTRIBUTION";
		    	}
	    		else{
	    			roleString = "OnEnd";
	    		}
	    	}
	    	else if(expresaoCondicao.toLowerCase().startsWith("onabort")){
	    		roleString = "OnAbort";
	    	}
	    	else if(expresaoCondicao.toLowerCase().startsWith("onresume")){
	    		roleString = "OnResume";	    		
	    	}
	    	else if(expresaoCondicao.toLowerCase().startsWith("onselection")){
	    		roleString = "OnSelection";	 
	    	}
	    	if (roleString== ""){
	    		codicaoRetorno = new NCLCompoundCondition();
	    		NCLAssessmentStatement assessement = new NCLAssessmentStatement();
	        	String[] valValor = null;
	        	String comparador = null;
	        	if(expresaoCondicao.contains("==")){
	        		comparador = "==";
	        		assessement.setComparator(NCLComparator.EQ);
	        	}
	        	else if(expresaoCondicao.contains("!=")){
	        		comparador = "!=";
	        		assessement.setComparator(NCLComparator.NE);
	        	}
	        	else if(expresaoCondicao.contains(">=")){
	        		comparador = ">=";
	        		assessement.setComparator(NCLComparator.GTE);
	        	}
	        	else if(expresaoCondicao.contains("<=")){
	        		comparador = "<=";
	        		assessement.setComparator(NCLComparator.LTE);
	        	}
	        	else if(expresaoCondicao.contains(">")){
	        		comparador = ">";
	        		assessement.setComparator(NCLComparator.GT);
	        	}
	        	else if(expresaoCondicao.contains("<")){
	        		comparador = "<";
	        		assessement.setComparator(NCLComparator.LT);
	        	}
	        	else if(expresaoCondicao.contains(" eq ")){
	        		comparador = " eq ";
	        		assessement.setComparator(NCLComparator.EQ);
	        	}
	        	else if(expresaoCondicao.contains(" ne ")){
	        		comparador = " ne ";
	        		assessement.setComparator(NCLComparator.NE);
	        	}
	        	else if(expresaoCondicao.contains(" gte ")){
	        		comparador = " gte ";
	        		assessement.setComparator(NCLComparator.GTE);
	        	}
	        	else if(expresaoCondicao.contains(" lte ")){
	        		comparador = " lte ";
	        		assessement.setComparator(NCLComparator.LTE);
	        	}
	        	else if(expresaoCondicao.contains(" gt ")){
	        		comparador = " gt ";
	        		assessement.setComparator(NCLComparator.GT);
	        	}
	        	else if(expresaoCondicao.contains(" lt ")){
	        		comparador = " lt ";
	        		assessement.setComparator(NCLComparator.LT);
	        	}
	        	valValor = expresaoCondicao.split(comparador);
	        	for(int indice=0;indice<valValor.length;indice++){
	    			String role = valValor[indice].substring(0,valValor[indice].indexOf(" "));
	    			NCLInterface inter = null;
	    			if(role.contains(".")){
	    				String[] idInterfaceT = role.split("\\.");
	    				while(!(contexto instanceof NCLBody)){
	    					contexto = ((NCLElement)contexto).getParent();
	    				}
	    				NCLNode noT = JNSaNaComplements.FindNode(idInterfaceT[0],contexto);
	    				inter = JNSaNaComplements.FindInterface(idInterfaceT[1], noT);
	    			}
	    			NCLAttributeAssessment atributeAss = new NCLAttributeAssessment();
	    			atributeAss.setRole(new NCLRole("defaultRole"+IndiceAssassement));
	    			if(inter instanceof NCLProperty){
	    				atributeAss.setEventType(NCLEventType.ATTRIBUTION);
	    				atributeAss.setAttributeType(NCLAttributeType.NODE_PROPERTY);
	    	    		try {
	    					assessement.addAttributeAssessment(atributeAss);
	    				} catch (Exception e) {
	    					e.printStackTrace();
	    				}
	    	    		interpretsBind(role,"defaultRole"+IndiceAssassement++,contexto,elo);
	    			}
	    			else if(valValor[indice].contains(" with ")){
	    				atributeAss.setRole(new NCLRole(role.toUpperCase()));
	    	    		String[] parametrosExtras = valValor[1].substring(valValor[1].indexOf(" with ")+4, valValor[1].length()).split(",");
	    	    		for(int i=0;i<parametrosExtras.length;i++){
	    	    			if(parametrosExtras[i].contains("eventtype")){
	    	    				String tipo = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"")+1,parametrosExtras[i].length());
	    	    	    		tipo = tipo.substring(0,tipo.indexOf('"'));
	    	    	    		atributeAss.setEventType(NCLEventType.valueOf(tipo.toUpperCase()));
	    	    			}
	    	    			else if(parametrosExtras[i].contains("attributetype")){
	    	    	    		String tipo = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"")+1,parametrosExtras[i].length());
	    	    	    		tipo = tipo.substring(0,tipo.indexOf('"'));
	    	    	    		atributeAss.setAttributeType(NCLAttributeType.valueOf(tipo.toUpperCase()));
	    	    			}
	    	    			else if(parametrosExtras[i].contains("key")){
	    	    	    		String key = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"")+1,parametrosExtras[i].length());
	    	    	    		key = key.substring(0,key.indexOf('"'));
    	    	    			atributeAss.setKey(new KeyParamType(key.toUpperCase()));
	    	    			}
	    	    			else if(parametrosExtras[i].contains("offset")){
	    	    	    		String offset = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"")+1,parametrosExtras[i].length());
	    	    	    		offset = offset.substring(0,offset.indexOf('"'));
	    	    	    		atributeAss.setOffset(new IntegerParamType(Integer.parseInt(offset)));
	    	    			}
	    	    		}
	    	    		try {
	    					assessement.addAttributeAssessment(atributeAss);
	    				} catch (Exception e) {
	    					// TODO Auto-generated catch block
	    					e.printStackTrace();
	    				}
	    	    		interpretsBind(role,"defaultRole"+IndiceAssassement++,contexto,elo);
	    	    	}
	    			else{
	    				NCLValueAssessment value = new NCLValueAssessment();
	    	    		if(role.toUpperCase() == "OCCURRING"){
	    	    			value.setValue(new AssValueParamType(new AssValueType(NCLDefaultValueAssessment.OCCURRING)));
	    	    		}
	    	    		else if(role.toUpperCase() == "PAUSED"){
	    	    			value.setValue(new AssValueParamType(new AssValueType(NCLDefaultValueAssessment.PAUSED)));
	    	    		}
	    	    		else if(role.toUpperCase() == "SLEEPING"){
	    	    			value.setValue(new AssValueParamType(new AssValueType(NCLDefaultValueAssessment.SLEEPING)));
	    	    		}
	    	    		else{
	    	    			value.setValue(new AssValueParamType(role));
	    	    		}
	    	    		assessement.setValueAssessment(value);
	    			}
	        	}
	    		((NCLCompoundCondition)codicaoRetorno).addStatement(assessement);
	    	}
	    	else{
	    		codicaoRetorno = new NCLSimpleCondition();
	    		((NCLSimpleCondition)codicaoRetorno).setRole(new NCLRole(NCLDefaultConditionRole.valueOf(roleString.toUpperCase())));
	    		String targets = expresaoCondicao.substring(roleString.length()+1, expresaoCondicao.length());
	    		int ind = targets.indexOf("with");
	    		if(ind > 0){
	    			targets = targets.substring(0,ind).toLowerCase();
		    		String[] parametrosExtras = expresaoCondicao.toLowerCase().substring(expresaoCondicao.indexOf(" with ")+4, expresaoCondicao.length()).split(",");
		    		for(int i=0;i<parametrosExtras.length;i++){
		    			parametrosExtras[i] = parametrosExtras[i].trim();
		    			if(parametrosExtras[i].contains("delay")){
		    				parametrosExtras[i] = parametrosExtras[i].trim();
		    				String delay = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"")+1,parametrosExtras[i].length()).trim();
		    				delay = delay.substring(0,delay.indexOf('"'));
		    				codicaoRetorno.setDelay(new DoubleParamType(Double.parseDouble(delay)));
		    			}
		    			else if(parametrosExtras[i].contains("eventtype")){
		    				String tipo = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"")+1,parametrosExtras[i].length()).trim();
		    	    		tipo = tipo.substring(0,tipo.indexOf('"'));
		    	    		((NCLSimpleCondition)codicaoRetorno).setEventType(NCLEventType.valueOf(tipo.toUpperCase()));
		    			}
		    			else if(parametrosExtras[i].contains("key")){
		    	    		String key = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"")+1,parametrosExtras[i].length()).trim();
		    	    		key = key.substring(0,key.indexOf('"'));
		    	    		((NCLSimpleCondition)codicaoRetorno).setKey(new KeyParamType(NCLKey.valueOf(key.toUpperCase())));
		    			}
		    			else if(parametrosExtras[i].contains("transition")){
		    				String transition = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"")+1,parametrosExtras[i].length()).trim();
		    				transition = transition.substring(0,transition.indexOf('"'));
		    				((NCLSimpleCondition)codicaoRetorno).setTransition(NCLEventTransition.getEnumType(transition.toUpperCase()));
		    			}
		    			else if(parametrosExtras[i].contains("min")){
		    				String min = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"")+1,parametrosExtras[i].length()).trim();
		    				min = min.substring(0,min.indexOf('"'));
		    				((NCLSimpleCondition)codicaoRetorno).setMin(Integer.parseInt(min));
		    			}	
		    			else if(parametrosExtras[i].contains("max")){
		    				String max = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"")+1,parametrosExtras[i].length()).trim();
		    				max = max.substring(0,max.indexOf('"'));
		    				((NCLSimpleCondition)codicaoRetorno).setMax(new MaxType(max));
		    			}	
		    			else if(parametrosExtras[i].contains("qualifier")){
		    				String qual = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"")+1,parametrosExtras[i].length()).trim();
		    				qual = qual.substring(0,qual.indexOf('"'));
		    				((NCLSimpleCondition)codicaoRetorno).setQualifier(NCLConditionOperator.valueOf(qual.toUpperCase()));
		    			}	
		    		}
		    	}
	    		String[] idInterface = targets.split(",");
	    		for(int i = 0;i<idInterface.length;i++){
	    			elo.addBind(interpretsBind(idInterface[i],roleString,contexto,elo));
	    		}
	    	}
		}
		return codicaoRetorno;
	}
	
	private NCLAction interpretsActExpression(String expresaoAcao,NCLLink elo,Object contexto) throws XMLException {
		NCLAction acao = null;
    	if(expresaoAcao.contains(";")||expresaoAcao.contains("||")){
    		acao = new NCLCompoundAction();
    		String operador = null;
    		String[] subExpressao = null;
    		while(operador == null){
    			subExpressao = expresaoAcao.split("\\(.*\\)");
				if(subExpressao[0].contains(";")){
					operador = ";";
		    		((NCLCompoundAction)acao).setOperator(NCLActionOperator.SEQ);
				}
				else if(subExpressao[0].contains("||")){
					operador = "||";
		    		((NCLCompoundAction)acao).setOperator(NCLActionOperator.PAR);
				}
				else{
					subExpressao = expresaoAcao.trim().substring(1,expresaoAcao.indexOf(subExpressao[0])-1).split("\\(.*\\)");
					if(subExpressao[0].contains("delay")){
						subExpressao[0] = subExpressao[0].trim();
	    				String delay = subExpressao[0].substring(subExpressao[0].indexOf('=')+1,subExpressao[0].length()).trim();
	    				acao.setDelay(new DoubleParamType(Double.parseDouble(delay.replace("s", ""))));
	    			}
				}
    		}
    		for(int i=0;i<subExpressao.length;i++){
    			if(!expresaoAcao.startsWith(subExpressao[i])){
    				((NCLCompoundAction)acao).addAction(interpretsActExpression(expresaoAcao.trim().substring(1,expresaoAcao.indexOf(subExpressao[i])-1),elo,contexto));
    			}
    			String[] pequenasExpessoes = subExpressao[i].toLowerCase().split(operador);
				for(int j=0;j<pequenasExpessoes.length;j++)
				{
					if(pequenasExpessoes[j].trim()!=""){
						((NCLCompoundAction)acao).addAction(interpretsActExpression(expresaoAcao.trim().substring(1,expresaoAcao.indexOf(subExpressao[i])-1),elo,contexto));
					}
				}
				expresaoAcao = expresaoAcao.trim().substring(expresaoAcao.indexOf(subExpressao[i]), expresaoAcao.length()).replace(subExpressao[i], "");
    		}
    	}
    	else{
    		acao = new NCLSimpleAction();
        	
        	if(expresaoAcao.toLowerCase().startsWith("start")){
        		((NCLSimpleAction)acao).setRole(new NCLRole(NCLDefaultActionRole.START));
        	}
        	else if(expresaoAcao.toLowerCase().startsWith("stop")){
        		((NCLSimpleAction)acao).setRole(new NCLRole(NCLDefaultActionRole.STOP));
        	}
        	else if(expresaoAcao.toLowerCase().startsWith("abort")){
        		((NCLSimpleAction)acao).setRole(new NCLRole(NCLDefaultActionRole.ABORT));
        	}
        	else if(expresaoAcao.toLowerCase().startsWith("pause")){
        		((NCLSimpleAction)acao).setRole(new NCLRole(NCLDefaultActionRole.PAUSE));
        	}
        	else if(expresaoAcao.toLowerCase().startsWith("resume")){
        		((NCLSimpleAction)acao).setRole(new NCLRole(NCLDefaultActionRole.RESUME));
        	}
        	else if(expresaoAcao.toLowerCase().startsWith("set")){
        		((NCLSimpleAction)acao).setRole(new NCLRole(NCLDefaultActionRole.SET));
        		int indiceSet = expresaoAcao.toLowerCase().indexOf("set")+4;
        		String valor = expresaoAcao.substring(indiceSet, expresaoAcao.indexOf(' ',indiceSet));
        		((NCLSimpleAction)acao).setValue(new StringParamType(valor));
        	}
        	else{
        		((NCLSimpleAction)acao).setRole(new NCLRole(expresaoAcao.trim().substring(0,expresaoAcao.trim().indexOf(" "))));
        	}
        	String targets = expresaoAcao.substring(((NCLSimpleAction)acao).getRole().getName().length()+1, expresaoAcao.length());
        	int ind = targets.indexOf("with");
    		if(ind > 0){
    			targets = targets.substring(0,ind).toLowerCase();
        		String[] parametrosExtras = expresaoAcao.substring(expresaoAcao.indexOf(" with ")+4, expresaoAcao.length()).split(",");
        		for(int i=0;i<parametrosExtras.length;i++){
        			parametrosExtras[i] = parametrosExtras[i].trim();
        			if(parametrosExtras[i].contains("delay")){
        				parametrosExtras[i] = parametrosExtras[i].trim();
        				String delay = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"")+1,parametrosExtras[i].length()).trim();
        				delay = delay.substring(0,delay.indexOf('"'));
        				acao.setDelay(new DoubleParamType(Double.parseDouble(delay.replace("s", ""))));
        			}
        			else if(parametrosExtras[i].contains("eventtype")){
        				String tipo = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"")+1,parametrosExtras[i].length()).trim();
        	    		tipo = tipo.substring(0,tipo.indexOf('"'));
        	    		((NCLSimpleAction)acao).setEventType(NCLEventType.valueOf(tipo.toUpperCase()));
        			}
        			else if(parametrosExtras[i].contains("actiontype")){
        	    		String tipo = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"")+1,parametrosExtras[i].length()).trim();
        	    		tipo = tipo.substring(0,tipo.indexOf('"'));
        	    		((NCLSimpleAction)acao).setActionType(NCLEventAction.getEnumType(tipo.toUpperCase()));
        			}
        			else if(parametrosExtras[i].contains("value")){
        				String value = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"")+1,parametrosExtras[i].length()).trim();
        				value = value.substring(0,value.indexOf('"'));
        				((NCLSimpleAction)acao).setValue(new StringParamType(value));
        			}
        			else if(parametrosExtras[i].contains("min")){
        				String min = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"")+1,parametrosExtras[i].length()).trim();
        				min = min.substring(0,min.indexOf('"'));
        				((NCLSimpleAction)acao).setMin(Integer.parseInt(min));
        			}	
        			else if(parametrosExtras[i].contains("max")){
        				String max = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"")+1,parametrosExtras[i].length()).trim();
        				max = max.substring(0,max.indexOf('"'));
	    				((NCLSimpleAction)acao).setMax(new MaxType(max));
        				
        			}	
        			else if(parametrosExtras[i].contains("repeat")){
        				String max = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"")+1,parametrosExtras[i].length()).trim();
        				max = max.substring(0,max.indexOf('"'));
        				((NCLSimpleAction)acao).setMax(new MaxType(max));
        			}	
        			else if(parametrosExtras[i].contains("qualifier")){
        				String qual = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"")+1,parametrosExtras[i].length()).trim();
        				qual = qual.substring(0,qual.indexOf('"'));
        				((NCLSimpleAction)acao).setQualifier(NCLActionOperator.valueOf(qual.toUpperCase()));
        			}
        			else if(parametrosExtras[i].contains("repeat")){
        				String repeat = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"")+1,parametrosExtras[i].length()).trim();
        				repeat = repeat.substring(0,repeat.indexOf('"'));
        				((NCLSimpleAction)acao).setRepeat(new IntegerParamType(Integer.parseInt(repeat)));
        			}	
        			else if(parametrosExtras[i].contains("repeatdelay")){
        				String delay = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"")+1,parametrosExtras[i].length()).trim();
        				delay = delay.substring(0,delay.indexOf('"'));
        				((NCLSimpleAction)acao).setRepeatDelay(new DoubleParamType(Double.parseDouble(delay.replace("s", ""))));
        			}	
        			else if(parametrosExtras[i].contains("duration")){
        				String duration = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"")+1,parametrosExtras[i].length()).trim();
        				duration = duration.substring(0,duration.indexOf('"'));
        				((NCLSimpleAction)acao).setRepeatDelay(new DoubleParamType(Double.parseDouble(duration)));
        			}	
        			else if(parametrosExtras[i].contains("by")){
        				String by = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"")+1,parametrosExtras[i].length()).trim();
        				by = by.substring(0,by.indexOf('"'));
        				((NCLSimpleAction)acao).setBy(new ByParamType(by));
        			}	
        		}
        	}
    		String[] idInterface = targets.split(",");
    		for(int i = 0;i<idInterface.length;i++){
    			elo.addBind(interpretsBind(idInterface[i],((NCLSimpleAction)acao).getRole().getName(),contexto,elo));
    		}
    	}
    		
    	return acao;
	}

	private NCLParam interpretsParameter(JSONObject parameter,NCLParamInstance instancia,NCLLink elo) throws XMLException {
		String obParametro = (String)JNSjSONComplements.getKey(parameter.toString());
		NCLParam parametro = new NCLParam(instancia);
		parametro.setName((NCLConnectorParam)elo.getXconnector().getConnectorParams().get(obParametro));
		parametro.setValue((String)parameter.get(obParametro));
		return parametro;
	}

	private NCLBind interpretsBind(Object obBind,String role,Object contexto,NCLLink elo) throws XMLException {
		NCLBind bind = new NCLBind();
		NCLRole nclRole = new NCLRole(role);
		bind.setRole(nclRole);
		String componente = null;
		if(obBind instanceof String){
			componente = (String)obBind;
		}
		else{
			JSONObject objeto = (JSONObject)obBind;
			componente = (String)objeto.get("component");
			if(objeto.containsKey("params")){
				if(objeto.get("params") instanceof JSONObject){
					bind.addBindParam(interpretsParameter((JSONObject)objeto.get("params"),NCLParamInstance.BINDPARAM,elo));
				}
				else{
					JSONArray objetos = (JSONArray)objeto.get("params");
					for(int i = 0;i<objetos.size();i++){
						bind.addBindParam(interpretsParameter((JSONObject)objetos.get(i),NCLParamInstance.BINDPARAM,elo));
					}
				}
			}
			if(objeto.containsKey("descriptor")){
				bind.setDescriptor(interpretadorHead.getInterpretadorDescritor().getBase().findDescriptor((String)objeto.get("descriptor")));
			}
		}
		if(componente.contains(".")){
			String[] componentes = componente.split("\\.");
			while(!(contexto instanceof NCLBody)){
				contexto = ((NCLElement)contexto).getParent();
			}
			bind.setComponent(JNSaNaComplements.FindNode(componentes[0].trim(), contexto));
			bind.setInterface(JNSaNaComplements.FindInterface(componentes[1].trim(),bind.getComponent()));
		}
		else{
			bind.setComponent(JNSaNaComplements.FindNode(componente.trim(), contexto));
		}
		
		return bind;
	}
}
