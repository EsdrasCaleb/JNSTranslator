import java.util.ArrayList;
import java.util.Vector;

import org.json.simple.JSONObject;


import br.uff.midiacom.ana.datatype.enums.NCLComparator;
import br.uff.midiacom.ana.datatype.enums.NCLOperator;
import br.uff.midiacom.ana.interfaces.NCLProperty;
import br.uff.midiacom.ana.rule.NCLCompositeRule;
import br.uff.midiacom.ana.rule.NCLRule;
import br.uff.midiacom.ana.rule.NCLRuleBase;
import br.uff.midiacom.ana.rule.NCLTestRule;
import br.uff.midiacom.xml.XMLException;

public class JNSRuleInterpreter {
	private NCLRuleBase Base;
	
    private int RuleIndex = 0;
    private Vector  GlobalPropertyVector;
    private Vector VectorDeAuxiliarSwitRule;
    
    JNSRuleInterpreter(Vector VectorDeAuxiliarSwitRule){
    	GlobalPropertyVector = new Vector();
    	this.VectorDeAuxiliarSwitRule = VectorDeAuxiliarSwitRule;
    }
    
	public NCLRuleBase getBase() {
		return Base;
	}
    
	NCLTestRule InterRule(String expressao) throws XMLException{
    	return InterRule(expressao,"RulePadrao_"+RuleIndex++);
    }

	NCLTestRule InterRule(String expressao,String id) throws XMLException {
		NCLTestRule regra;
		expressao = expressao.toLowerCase().trim();
		int indiceLocal = 0;
    	if(expressao.toLowerCase().contains("and")||expressao.toLowerCase().contains("or")){
    		regra = new NCLCompositeRule(id);
    		String[] subExpressao = expressao.split("\\(.*\\)");
    		String operador = "or";
    		((NCLCompositeRule)regra).setOperator(NCLOperator.OR);
			if(subExpressao[0].contains("and")){
				operador = "and";
	    		((NCLCompositeRule)regra).setOperator(NCLOperator.AND);
			}
    		for(int i=0;i<subExpressao.length;i++){
    			if(!expressao.startsWith(subExpressao[i])){
    				((NCLCompositeRule)regra).addRule(InterRule(expressao.trim().substring(1,expressao.indexOf(subExpressao[i])-1),"SubRule_"+id+"_"+indiceLocal++));
    			}
				String[] pequenasExpessoes = subExpressao[i].toLowerCase().split(operador);
				for(int j=0;j<pequenasExpessoes.length;j++)
				{
					if(pequenasExpessoes[j].trim()!="")
						((NCLCompositeRule)regra).addRule(InterRule(pequenasExpessoes[j],"SubRule_"+id+"_"+indiceLocal++));
				}
    			expressao = expressao.trim().substring(expressao.indexOf(subExpressao[i]), expressao.length()).replace(subExpressao[i], "");
    		}	
    	}
    	else{
    		expressao.replaceAll("[()]", "");
    		regra = new NCLRule("id");
    		regra = interExpressaoDeRule(expressao,(NCLRule)regra);
    	}
    	return regra;
    }
	
	private NCLRule interExpressaoDeRule(String ruleExpr,NCLRule regra) throws XMLException {
    	String[] variavalValor = null;
    	String comparador = null;
    	if(ruleExpr.contains("==")){
    		comparador = "==";
    		regra.setComparator(NCLComparator.EQ);
    	}
    	else if(ruleExpr.contains("!=")){
    		comparador = "!=";
    		regra.setComparator(NCLComparator.NE);
    	}
    	else if(ruleExpr.contains(">=")){
    		comparador = ">=";
    		regra.setComparator(NCLComparator.GTE);
    	}
    	else if(ruleExpr.contains("<=")){
    		comparador = "<=";
    		regra.setComparator(NCLComparator.LTE);
    	}
    	else if(ruleExpr.contains(">")){
    		comparador = ">";
    		regra.setComparator(NCLComparator.GT);
    	}
    	else if(ruleExpr.contains("<")){
    		comparador = "<";
    		regra.setComparator(NCLComparator.LT);
    	}
    	else if(ruleExpr.contains(" eq ")){
    		comparador = " eq ";
    		regra.setComparator(NCLComparator.EQ);
    	}
    	else if(ruleExpr.contains(" ne ")){
    		comparador = " ne ";
    		regra.setComparator(NCLComparator.NE);
    	}
    	else if(ruleExpr.contains(" gte ")){
    		comparador = " gte ";
    		regra.setComparator(NCLComparator.GTE);
    	}
    	else if(ruleExpr.contains(" lte ")){
    		comparador = " lte ";
    		regra.setComparator(NCLComparator.LTE);
    	}
    	else if(ruleExpr.contains(" gt ")){
    		comparador = " gt ";
    		regra.setComparator(NCLComparator.GT);
    	}
    	else if(ruleExpr.contains(" lt ")){
    		comparador = " lt ";
    		regra.setComparator(NCLComparator.LT);
    	}
    	variavalValor = ruleExpr.split(comparador);
    	regra.setValue( variavalValor[1]);
    	regra.setVar(getProperty(variavalValor[0]));
    	return regra;
    }
	
	private NCLProperty  getProperty(String id){
    	NCLProperty proriedade = null;
    	int i=0;
        for(i=0;i<GlobalPropertyVector.size();i++){
        	if(((NCLProperty)GlobalPropertyVector.get(i)).getId() == id)
        		return (NCLProperty)GlobalPropertyVector.get(i);
        }
    	return null;
    }
	
	void Add(JSONObject rule) throws XMLException {
    	String expressao = (String)((JSONObject)rule).get("expression");
    	String id = (String)((JSONObject)rule).get("id");
		if(!VariableReplaced((JSONObject)rule))
			Base.addRule(InterRule(expressao,id));
    }
	
	/**
     * Adiciona as regra caso ela tenha o campo vars, ou seja usada a mesma regra com mais de uma variavel(reuso)
     * @param regra jsonObject que contem a regra
     * @return
	 * @throws XMLException 
     */
    Boolean VariableReplaced(JSONObject regra) throws XMLException {
    	int i=0;
    	Boolean retorno = false;
    	String id = (String)regra.get("id");
    	String expressao = (String)regra.get("expression");
    	for(i=0;i<VectorDeAuxiliarSwitRule.size();i++){
    		if(((String)((Vector)VectorDeAuxiliarSwitRule.elementAt(i)).elementAt(1))==id){//esse vetor guarda id do switch que chamou seguido do id da rule que ele chama e das variaveis e sua correspondencia se e somente se ele tiver o campo var
    			Vector vetorDeTeste = ((Vector)((Vector)VectorDeAuxiliarSwitRule.elementAt(i)).elementAt(2));
    			int j = 0;
    			Boolean troca = false;
    			String novaExpressao = expressao;
    			for(j=0;j<vetorDeTeste.size();j++){
					if(expressao.contains((String)vetorDeTeste.elementAt(j))){
						retorno = true;
						troca = true;
						novaExpressao = expressao.replace((String)vetorDeTeste.elementAt(j), 
								(String)((Vector)((Vector)VectorDeAuxiliarSwitRule.elementAt(i)).elementAt(3)).elementAt(j));
								
					}
    			}
    			if(troca){
    				Base.addRule(InterRule(novaExpressao,id+"_"+(String)((Vector)VectorDeAuxiliarSwitRule.elementAt(i)).elementAt(0)));
    				expressao = (String)regra.get("expression");
    			}
    		}
    	}
		return retorno;
	}

	Vector getGlobalPropertyVector() {
		return this.GlobalPropertyVector;
	}
    
}
