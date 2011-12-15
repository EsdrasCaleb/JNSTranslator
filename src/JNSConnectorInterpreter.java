import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import br.uff.midiacom.ana.NCLElement;
import br.uff.midiacom.ana.connector.NCLAction;
import br.uff.midiacom.ana.connector.NCLAssessmentStatement;
import br.uff.midiacom.ana.connector.NCLAttributeAssessment;
import br.uff.midiacom.ana.connector.NCLCausalConnector;
import br.uff.midiacom.ana.connector.NCLCompoundAction;
import br.uff.midiacom.ana.connector.NCLCondition;
import br.uff.midiacom.ana.connector.NCLCompoundCondition;
import br.uff.midiacom.ana.connector.NCLConnectorBase;
import br.uff.midiacom.ana.connector.NCLConnectorParam;
import br.uff.midiacom.ana.connector.NCLRole;
import br.uff.midiacom.ana.connector.NCLSimpleAction;
import br.uff.midiacom.ana.connector.NCLSimpleCondition;
import br.uff.midiacom.ana.connector.NCLValueAssessment;
import br.uff.midiacom.ana.datatype.auxiliar.AssValueParamType;
import br.uff.midiacom.ana.datatype.auxiliar.ByParamType;
import br.uff.midiacom.ana.datatype.auxiliar.DoubleParamType;
import br.uff.midiacom.ana.datatype.auxiliar.IntegerParamType;
import br.uff.midiacom.ana.datatype.auxiliar.KeyParamType;
import br.uff.midiacom.ana.datatype.auxiliar.StringParamType;
import br.uff.midiacom.ana.datatype.enums.NCLActionOperator;
import br.uff.midiacom.ana.datatype.enums.NCLComparator;
import br.uff.midiacom.ana.datatype.enums.NCLConditionOperator;
import br.uff.midiacom.ana.datatype.enums.NCLDefaultActionRole;
import br.uff.midiacom.ana.datatype.enums.NCLDefaultConditionRole;
import br.uff.midiacom.ana.datatype.enums.NCLEventAction;
import br.uff.midiacom.ana.datatype.enums.NCLEventType;
import br.uff.midiacom.ana.datatype.enums.NCLKey;
import br.uff.midiacom.ana.datatype.enums.NCLEventTransition;
import br.uff.midiacom.ana.datatype.enums.NCLAttributeType;
import br.uff.midiacom.ana.descriptor.NCLDescriptor;
import br.uff.midiacom.ana.rule.NCLCompositeRule;
import br.uff.midiacom.xml.XMLException;
import br.uff.midiacom.xml.datatype.number.MaxType;


public class JNSConnectorInterpreter {
	 NCLConnectorBase Base;
	 
	 JNSConnectorInterpreter() throws XMLException{
		 Base = new NCLConnectorBase(); 
	 }
	 
	 NCLConnectorBase getBase(){
		 return Base;
	 }
	 
	
	 void Add(JSONObject jnsConnector) throws XMLException{
		NCLCausalConnector connector = interConnector(jnsConnector);
        Base.addCausalConnector(connector);
	}
	
    private  NCLCausalConnector interConnector(JSONObject jConnector) throws XMLException{
        NCLCausalConnector nConnector = new NCLCausalConnector((String)jConnector.get("id"));
        String expressao = ((String)jConnector.get("expression")).replace('\'', '\"');
        if(jConnector.containsKey("params")){
        	JSONArray jsonParams = (JSONArray)jConnector.get("params");
        	int i = 0;
        	for(i=0;i<jsonParams.size();i++){
        		nConnector.addConnectorParam(new NCLConnectorParam(((String)jsonParams.get(i)).trim()));
        	}
        }
        String expresaoCondicao = expressao.substring(0,expressao.indexOf("then"));
        String expressaoAcao =  expressao.substring(expressao.indexOf("then")+4,expressao.length());
        nConnector.setCondition(interCondition(expresaoCondicao.trim(),nConnector));
        nConnector.setAction((NCLAction)interAction(expressaoAcao.trim(),nConnector));
        return nConnector;
    }

	private  NCLCondition interCondition(String simpleCondExpression, NCLCausalConnector conectorPai) throws XMLException{
    	NCLCondition condicao = null;
    	if(simpleCondExpression.toLowerCase().contains("and")||simpleCondExpression.toLowerCase().contains("or")){
    		condicao = new NCLCompoundCondition();
			String operador = null;
    		String[] subExpressao = null;
    		while(operador == null){
    			subExpressao = simpleCondExpression.split("\\(.*\\)");
				if(subExpressao[0].toLowerCase().contains("and")){
					operador = "and";
					((NCLCompoundCondition)condicao).setOperator(NCLConditionOperator.AND);
				}
				else if(subExpressao[0].toLowerCase().contains("or")){
					operador = "or";
					((NCLCompoundCondition)condicao).setOperator(NCLConditionOperator.OR);
				}
				else{
					subExpressao = simpleCondExpression.trim().substring(1,simpleCondExpression.indexOf(subExpressao[0])-1).split("\\(.*\\)");
					if(subExpressao[0].toLowerCase().contains("delay")){
						subExpressao[0] = subExpressao[0].trim();
	    				String delay = subExpressao[0].substring(subExpressao[0].indexOf('=')+1,subExpressao[0].length()).trim();
	    				condicao.setDelay(InterpretaDelay(delay,conectorPai));
	    			}
				}
    		}
    		for(int i=0;i<subExpressao.length;i++){
    			if(!simpleCondExpression.toLowerCase().startsWith(subExpressao[i])){
    				((NCLCompoundCondition)condicao).addCondition(interCondition(simpleCondExpression.trim().substring(1,simpleCondExpression.trim().indexOf(subExpressao[i])-1).trim(),conectorPai));
    			}
    			String[] pequenasExpessoes = subExpressao[i].toLowerCase().split(operador);
				for(int j=0;j<pequenasExpessoes.length;j++)
				{
					if(pequenasExpessoes[j].trim()!="")
						interSimpleCondition(pequenasExpessoes[j],conectorPai,condicao);
				}
    			simpleCondExpression = simpleCondExpression.substring(simpleCondExpression.indexOf(subExpressao[i]), simpleCondExpression.length()).replace(subExpressao[i], "").trim();
    		}
    	}
    	else{
    		condicao= interSimpleCondition(simpleCondExpression,conectorPai,condicao);
    	}
    		
    	return condicao;
    	
    }

	private  NCLCondition interSimpleCondition(String simpleCondExpression,NCLCausalConnector connectorPai,NCLCondition condicaoPai) throws XMLException{//envie a string com trim e sem ()
    	NCLSimpleCondition condicao = new NCLSimpleCondition();
    	if(simpleCondExpression.toLowerCase().contains(" with ")){
    		String[] parametrosExtras = simpleCondExpression.substring(simpleCondExpression.indexOf(" with ")+4, simpleCondExpression.length()).split(",");
    		for(int i=0;i<parametrosExtras.length;i++){
    			parametrosExtras[i] = parametrosExtras[i].trim();
    			if(parametrosExtras[i].toLowerCase().contains("delay")){
    				parametrosExtras[i] = parametrosExtras[i].trim();
    				String delay = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"")+1,parametrosExtras[i].length()).trim();
    				delay = delay.substring(0,delay.indexOf('"'));
					condicao.setDelay(InterpretaDelay(delay,connectorPai));
    			}
    			else if(parametrosExtras[i].toLowerCase().contains("eventtype")){
    				String tipo = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"")+1,parametrosExtras[i].length()).trim();
    	    		tipo = tipo.substring(0,tipo.indexOf('"'));
    	    		condicao.setEventType(NCLEventType.getEnumType(tipo.toUpperCase()));
    			}
    			else if(parametrosExtras[i].toLowerCase().contains("key")){
    	    		String key = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"")+1,parametrosExtras[i].length()).trim();
    	    		key = key.substring(0,key.indexOf('"'));
    	    		condicao.setKey(InterpretaKey(key,connectorPai));
    			}
    			else if(parametrosExtras[i].toLowerCase().contains("transition")){
    				String transition = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"")+1,parametrosExtras[i].length()).trim();
    				transition = transition.substring(0,transition.indexOf('"'));
    	    		condicao.setTransition(NCLEventTransition.valueOf(transition.toUpperCase()));
    			}
    			else if(parametrosExtras[i].toLowerCase().contains("min")){
    				String min = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"")+1,parametrosExtras[i].length()).trim();
    				min = min.substring(0,min.indexOf('"'));
    				condicao.setMin(Integer.parseInt(min));
    			}	
    			else if(parametrosExtras[i].toLowerCase().contains("max")){
    				String max = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"")+1,parametrosExtras[i].length()).trim();
    				max = max.substring(0,max.indexOf('"'));
    				condicao.setMax(new MaxType(max));
    			}	
    			else if(parametrosExtras[i].toLowerCase().contains("qualifier")){
    				String qual = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"")+1,parametrosExtras[i].length()).trim();
    				qual = qual.substring(0,qual.indexOf('"'));
    				condicao.setQualifier(NCLConditionOperator.valueOf(qual.toUpperCase()));
    			}	
    		}
    	}
    	
    	if(simpleCondExpression.toLowerCase().startsWith("onbegin")){
    		
    		
    		if(simpleCondExpression.toLowerCase().startsWith("onbeginatribuition")){
    			condicao.setRole(new NCLRole(NCLDefaultConditionRole.ONBEGINATTRIBUTION));
	    	}
    		else{
	    		condicao.setRole(new NCLRole(NCLDefaultConditionRole.ONBEGIN));
    		}
    	}
    	else if(simpleCondExpression.toLowerCase().startsWith("onend")){
    		if(simpleCondExpression.toLowerCase().startsWith("onendatribuition")){
    			condicao.setRole(new NCLRole(NCLDefaultConditionRole.ONENDATTRIBUTION));
	    	}
    		else{
    			condicao.setRole(new NCLRole(NCLDefaultConditionRole.ONEND));
    		}
    	}
    	else if(simpleCondExpression.toLowerCase().startsWith("onabort")){
    		condicao.setRole(new NCLRole(NCLDefaultConditionRole.ONABORT));
    	}
    	else if(simpleCondExpression.toLowerCase().startsWith("onresume")){
    		condicao.setRole(new NCLRole(NCLDefaultConditionRole.ONRESUME));	    		
    	}
    	else if(simpleCondExpression.toLowerCase().startsWith("onselection")){
    		condicao.setRole(new NCLRole(NCLDefaultConditionRole.ONRESUME));	 
    	}
    	else{
    		NCLAssessmentStatement assessement = interAssessmentStatement(simpleCondExpression,connectorPai);
    		((NCLCompoundCondition)condicaoPai).addStatement(assessement);
    		return condicaoPai;
    	}
    	
    	if(condicaoPai!= null)
    		((NCLCompoundCondition)condicaoPai).addCondition(condicao);
    	else
    		condicaoPai = condicao;
    	return condicaoPai;
    }

	private  NCLAssessmentStatement interAssessmentStatement(String asessemtExpr,NCLCausalConnector connectorPai) throws XMLException {
    	NCLAssessmentStatement assessement = new NCLAssessmentStatement();
    	String[] valValor = null;
    	String comparador = null;
    	if(asessemtExpr.toLowerCase().contains("==")){
    		comparador = "==";
    		assessement.setComparator(NCLComparator.EQ);
    	}
    	else if(asessemtExpr.toLowerCase().contains("!=")){
    		comparador = "!=";
    		assessement.setComparator(NCLComparator.NE);
    	}
    	else if(asessemtExpr.toLowerCase().contains(">=")){
    		comparador = ">=";
    		assessement.setComparator(NCLComparator.GTE);
    	}
    	else if(asessemtExpr.toLowerCase().contains("<=")){
    		comparador = "<=";
    		assessement.setComparator(NCLComparator.LTE);
    	}
    	else if(asessemtExpr.toLowerCase().contains(">")){
    		comparador = ">";
    		assessement.setComparator(NCLComparator.GT);
    	}
    	else if(asessemtExpr.toLowerCase().contains("<")){
    		comparador = "<";
    		assessement.setComparator(NCLComparator.LT);
    	}
    	else if(asessemtExpr.toLowerCase().contains(" eq ")){
    		comparador = " eq ";
    		assessement.setComparator(NCLComparator.EQ);
    	}
    	else if(asessemtExpr.toLowerCase().contains(" ne ")){
    		comparador = " ne ";
    		assessement.setComparator(NCLComparator.NE);
    	}
    	else if(asessemtExpr.toLowerCase().contains(" gte ")){
    		comparador = " gte ";
    		assessement.setComparator(NCLComparator.GTE);
    	}
    	else if(asessemtExpr.toLowerCase().contains(" lte ")){
    		comparador = " lte ";
    		assessement.setComparator(NCLComparator.LTE);
    	}
    	else if(asessemtExpr.toLowerCase().contains(" gt ")){
    		comparador = " gt ";
    		assessement.setComparator(NCLComparator.GT);
    	}
    	else if(asessemtExpr.toLowerCase().contains(" lt ")){
    		comparador = " lt ";
    		assessement.setComparator(NCLComparator.LT);
    	}
    	valValor = asessemtExpr.split(comparador);
    	for(int indice=0;indice<valValor.length;indice++){
			String role = valValor[indice].substring(0,valValor[indice].indexOf(" "));
			if(valValor[indice].toLowerCase().contains(" with ")){
				NCLAttributeAssessment atributeAss = new NCLAttributeAssessment();
				atributeAss.setRole(new NCLRole(role.toUpperCase()));
	    		String[] parametrosExtras = valValor[indice].substring(valValor[indice].indexOf(" with ")+4, valValor[indice].length()).split(",");
	    		for(int i=0;i<parametrosExtras.length;i++){
	    			if(parametrosExtras[i].toLowerCase().contains("eventtype")){
	    				String tipo = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"")+1,parametrosExtras[i].length());
	    	    		tipo = tipo.substring(0,tipo.indexOf('"'));
	    	    		atributeAss.setEventType(NCLEventType.valueOf(tipo.toUpperCase()));
	    			}
	    			else if(parametrosExtras[i].toLowerCase().contains("attributetype")){
	    	    		String tipo = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"")+1,parametrosExtras[i].length());
	    	    		tipo = tipo.substring(0,tipo.indexOf('"'));
	    	    		atributeAss.setAttributeType(NCLAttributeType.valueOf(tipo.toUpperCase()));
	    			}
	    			else if(parametrosExtras[i].toLowerCase().contains("key")){
	    	    		String key = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"")+1,parametrosExtras[i].length());
	    	    		key = key.substring(0,key.indexOf('"'));
    	    			atributeAss.setKey(InterpretaKey(key,connectorPai));
	    			}
	    			else if(parametrosExtras[i].toLowerCase().contains("offset")){
	    	    		String offset = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"")+1,parametrosExtras[i].length());
	    	    		offset = offset.substring(0,offset.indexOf('"'));
	    	    		atributeAss.setOffset(InterpretaInteger(offset,connectorPai));
	    			}
	    		}
	    		try {
					assessement.addAttributeAssessment(atributeAss);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}
			else{
				NCLValueAssessment value = new NCLValueAssessment();
	    		value.setValue(InterpretaAssValueParam(role,connectorPai));
	    		assessement.setValueAssessment(value);
			}
    	}
    	return assessement;
	}

	private  NCLAction interAction(String simpleActExpression, NCLCausalConnector connectorPai) throws XMLException {
    	NCLAction acao = null;
    	if(simpleActExpression.toLowerCase().contains(";")||simpleActExpression.toLowerCase().contains("||")){
    		acao = new NCLCompoundAction();
    		String operador = null;
    		String[] subExpressao = null;
    		while(operador == null){
    			subExpressao = simpleActExpression.split("\\(.*\\)");
				if(subExpressao[0].toLowerCase().contains(";")){
					operador = ";";
		    		((NCLCompoundAction)acao).setOperator(NCLActionOperator.SEQ);
				}
				else if(subExpressao[0].toLowerCase().contains("||")){
					operador = "||";
		    		((NCLCompoundAction)acao).setOperator(NCLActionOperator.PAR);
				}
				else{
					subExpressao = simpleActExpression.trim().substring(1,simpleActExpression.indexOf(subExpressao[0])-1).split("\\(.*\\)");
					if(subExpressao[0].toLowerCase().contains("delay")){
						subExpressao[0] = subExpressao[0].trim();
	    				String delay = subExpressao[0].substring(subExpressao[0].indexOf('=')+1,subExpressao[0].length()).trim();
	    				acao.setDelay(InterpretaDelay(delay,connectorPai));
	    			}
				}
    		}
    		for(int i=0;i<subExpressao.length;i++){
    			if(!simpleActExpression.startsWith(subExpressao[i])){
    				((NCLCompoundAction)acao).addAction(interAction(simpleActExpression.trim().substring(1,simpleActExpression.indexOf(subExpressao[i])-1),connectorPai));
    			}
    			String[] pequenasExpessoes = subExpressao[i].split(operador);
				for(int j=0;j<pequenasExpessoes.length;j++)
				{
					if(pequenasExpessoes[j].trim()!=""){
						((NCLCompoundAction)acao).addAction(interSimpleAction(pequenasExpessoes[j],connectorPai));
					}
				}
    			simpleActExpression = simpleActExpression.trim().substring(simpleActExpression.indexOf(subExpressao[i]), simpleActExpression.length()).replace(subExpressao[i], "");
    		}
    	}
    	else{
    		acao = interSimpleAction(simpleActExpression,connectorPai);
    	}
    		
    	return acao;
	}
    
    private  NCLAction interSimpleAction(String simpleActExpression,NCLCausalConnector connectorPai) throws XMLException{//envie a string com trim e sem ()
    	NCLSimpleAction acao = new NCLSimpleAction();
    	
    	if(simpleActExpression.toLowerCase().startsWith("start")){
    		acao.setRole(new NCLRole(NCLDefaultActionRole.START));
    	}
    	else if(simpleActExpression.toLowerCase().startsWith("stop")){
    		acao.setRole(new NCLRole(NCLDefaultActionRole.STOP));
    	}
    	else if(simpleActExpression.toLowerCase().startsWith("abort")){
    		acao.setRole(new NCLRole(NCLDefaultActionRole.ABORT));
    	}
    	else if(simpleActExpression.toLowerCase().startsWith("pause")){
    		acao.setRole(new NCLRole(NCLDefaultActionRole.PAUSE));
    	}
    	else if(simpleActExpression.toLowerCase().startsWith("resume")){
    		acao.setRole(new NCLRole(NCLDefaultActionRole.RESUME));
    	}
    	else if(simpleActExpression.toLowerCase().startsWith("set")){
    		acao.setRole(new NCLRole(NCLDefaultActionRole.SET));
    		int indiceSet = simpleActExpression.toLowerCase().indexOf("set")+4;
    		String valor = simpleActExpression.substring(indiceSet, simpleActExpression.indexOf(' ',indiceSet));
    		acao.setValue(InterpretaValor(valor,connectorPai));
    	}
    	else{
    		acao.setRole(new NCLRole(simpleActExpression.trim().substring(0,simpleActExpression.trim().indexOf(" "))));
    	}
    		

    	if(simpleActExpression.toLowerCase().contains(" with ")){
    		String[] parametrosExtras = simpleActExpression.substring(simpleActExpression.indexOf(" with ")+4, simpleActExpression.length()).split(",");
    		for(int i=0;i<parametrosExtras.length;i++){
    			parametrosExtras[i] = parametrosExtras[i].trim();
    			if(parametrosExtras[i].toLowerCase().contains("delay")){
    				parametrosExtras[i] = parametrosExtras[i].trim();
    				String delay = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"")+1,parametrosExtras[i].length()).trim();
    				delay = delay.substring(0,delay.indexOf('"'));
    				acao.setDelay(InterpretaDelay(delay,connectorPai));
    			}
    			else if(parametrosExtras[i].toLowerCase().contains("eventtype")){
    				String tipo = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"")+1,parametrosExtras[i].length()).trim();
    	    		tipo = tipo.substring(0,tipo.indexOf('"'));
    	    		acao.setEventType(NCLEventType.valueOf(tipo.toUpperCase()));
    			}
    			else if(parametrosExtras[i].toLowerCase().contains("actiontype")){
    	    		String tipo = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"")+1,parametrosExtras[i].length()).trim();
    	    		tipo = tipo.substring(0,tipo.indexOf('"'));
    	    		acao.setActionType(NCLEventAction.getEnumType(tipo.toUpperCase()));
    			}
    			else if(parametrosExtras[i].toLowerCase().contains("value")){
    				String value = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"")+1,parametrosExtras[i].length()).trim();
    				value = value.substring(0,value.indexOf('"'));
    	    		acao.setValue(InterpretaValor(value,connectorPai));
    			}
    			else if(parametrosExtras[i].toLowerCase().contains("min")){
    				String min = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"")+1,parametrosExtras[i].length()).trim();
    				min = min.substring(0,min.indexOf('"'));
    	    		acao.setMin(Integer.parseInt(min));
    			}	
    			else if(parametrosExtras[i].toLowerCase().contains("max")){
    				String max = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"")+1,parametrosExtras[i].length()).trim();
    				max = max.substring(0,max.indexOf('"'));
    				acao.setMax(new MaxType(max));
    			}	
    			else if(parametrosExtras[i].toLowerCase().contains("qualifier")){
    				String qual = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"")+1,parametrosExtras[i].length()).trim();
    				qual = qual.substring(0,qual.indexOf('"'));
    	    		acao.setQualifier(NCLActionOperator.valueOf(qual.toUpperCase()));
    			}
    			else if(parametrosExtras[i].toLowerCase().contains("repeat")){
    				String repeat = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"")+1,parametrosExtras[i].length()).trim();
    				repeat = repeat.substring(0,repeat.indexOf('"'));
    				acao.setRepeat(InterpretaInteger(repeat,connectorPai));
    			}	
    			else if(parametrosExtras[i].toLowerCase().contains("repeatdelay")){
    				String delay = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"")+1,parametrosExtras[i].length()).trim();
    				delay = delay.substring(0,delay.indexOf('"'));
					acao.setRepeatDelay(InterpretaDelay(delay,connectorPai));
    			}	
    			else if(parametrosExtras[i].toLowerCase().contains("duration")){
    				String duration = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"")+1,parametrosExtras[i].length()).trim();
    				duration = duration.substring(0,duration.indexOf('"'));
					acao.setRepeatDelay(InterpretaDelay(duration,connectorPai));
    			}	
    			else if(parametrosExtras[i].toLowerCase().contains("by")){
    				String by = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"")+1,parametrosExtras[i].length()).trim();
    				by = by.substring(0,by.indexOf('"'));
					acao.setBy(InterpretaBy(by,connectorPai));
    			}	
    		}
    	}
		return acao;
    }
    
    //auxiliares
	
    private ByParamType InterpretaBy(String by, NCLCausalConnector connectorPai) throws XMLException {
    	if(by.toLowerCase().contains("$"))
			return new ByParamType(by, JNSaNaComplements.FindParameter(by.replace("$",""),connectorPai));
		else
			return new ByParamType(by);
	}

	private StringParamType InterpretaValor(String valor,NCLCausalConnector connectorPai)throws XMLException {
    	if(valor.toLowerCase().contains("$"))
			return new StringParamType(valor, JNSaNaComplements.FindParameter(valor.replace("$",""),connectorPai));
		else
			return new StringParamType(valor);
	}

	private DoubleParamType InterpretaDelay(String delay,NCLCausalConnector conectorPai) throws XMLException {
    	if(delay.toLowerCase().contains("$"))
			return new DoubleParamType(delay, JNSaNaComplements.FindParameter(delay.replace("$",""),conectorPai));
		else
			return new DoubleParamType(delay);
	}
    

    private KeyParamType InterpretaKey(String key,NCLCausalConnector conectorPai) throws XMLException {
    		if(key.toLowerCase().contains("$"))
    			return new KeyParamType(key,JNSaNaComplements.FindParameter(key.replace("$",""),conectorPai));
    		else
    			return new KeyParamType(key.toUpperCase());
	}
    
    private IntegerParamType InterpretaInteger(String offset,NCLCausalConnector conectorPai) throws XMLException {
    		if(offset.toLowerCase().contains("$"))
    			return new IntegerParamType(offset,JNSaNaComplements.FindParameter(offset.replace("$",""),conectorPai));
    		else
    			return new IntegerParamType(offset);
	}
    
	private AssValueParamType InterpretaAssValueParam(String role,NCLCausalConnector conectorPai) throws XMLException {
		if(role.toLowerCase().contains("$"))
			return new AssValueParamType(role,JNSaNaComplements.FindParameter(role.replace("$",""),conectorPai));
		else
			return new AssValueParamType(role);
	}
}
