 import br.uff.midiacom.ana.rule.NCLCompositeRule;
 import br.uff.midiacom.ana.rule.NCLRule;
 import br.uff.midiacom.ana.rule.NCLRuleBase;
 import br.uff.midiacom.ana.rule.NCLTestRule;
 import br.uff.midiacom.ana.util.enums.NCLOperator;
 import br.uff.midiacom.ana.util.exception.XMLException;
 import br.uff.midiacom.ana.util.ncl.NCLVariable;
 import java.util.Vector;
 import org.json.simple.JSONObject;
 
 public class JNSRuleInterpreter
 {
   private NCLRuleBase Base;
   private int RuleIndex = 0;
   private Vector VectorDeAuxiliarSwitRule;
 
   JNSRuleInterpreter(Vector VectorDeAuxiliarSwitRule)
     throws XMLException
   {
     this.VectorDeAuxiliarSwitRule = VectorDeAuxiliarSwitRule;
     this.Base = new NCLRuleBase();
   }
 
   public NCLRuleBase getBase() {
     return this.Base;
   }
 
   NCLTestRule InterRule(String expressao) throws XMLException {
     return InterRule(expressao, "RulePadrao_" + this.RuleIndex++);
   }
 
   NCLTestRule InterRule(String expressao, String id) throws XMLException
   {
     expressao = expressao.trim();
     int indiceLocal = 0;
     NCLTestRule regra;
     if ((expressao.toLowerCase().contains(" and ")) || (expressao.toLowerCase().contains(" or "))) {
       regra = new NCLCompositeRule(id);
       String operador = null;
       String[] subExpressao = (String[])null;
       if (expressao.contains("(")) {
         subExpressao = expressao.split("\\(.*\\)");
         subExpressao = JNSConnectorInterpreter.getInnerParentesis(expressao);
         while (subExpressao.length == 1)
           subExpressao = JNSConnectorInterpreter.getInnerParentesis(subExpressao[0]);
         int i = subExpressao.length - 1;
         if (subExpressao[i].toLowerCase().startsWith("and")) {
           operador = "and";
           ((NCLCompositeRule)regra).setOperator(NCLOperator.AND);
         }
         else if (subExpressao[i].toLowerCase().startsWith("or")) {
           operador = "or";
           ((NCLCompositeRule)regra).setOperator(NCLOperator.OR);
         }
       }
       if (operador == null) {
         if (expressao.toLowerCase().contains("and")) {
           operador = "and";
           ((NCLCompositeRule)regra).setOperator(NCLOperator.AND);
         }
         else if (expressao.toLowerCase().contains("or")) {
           operador = "or";
           ((NCLCompositeRule)regra).setOperator(NCLOperator.OR);
         }
         subExpressao = new String[1];
         subExpressao[0] = expressao;
       }
       for (int i = 0; i < subExpressao.length; i++) {
         String[] pequenasExpessoes = subExpressao[i].toLowerCase().split(operador);
         for (int j = 0; j < pequenasExpessoes.length; j++)
         {
           if (pequenasExpessoes[j].trim() != "")
             ((NCLCompositeRule)regra).addRule(InterRule(pequenasExpessoes[j], "SubRule_" + id + "_" + indiceLocal++));
         }
         expressao = expressao.trim().substring(expressao.indexOf(subExpressao[i]), expressao.length()).replace(subExpressao[i], "");
       }
     }
     else {
       expressao.replaceAll("[()]", "");
       regra = new NCLRule(id);
       regra = interExpressaoDeRule(expressao, (NCLRule)regra);
     }
     this.Base.addRule(regra);
     return regra;
   }
 
   private NCLRule interExpressaoDeRule(String ruleExpr, NCLRule regra) throws XMLException {
     String[] variavalValor = (String[])null;
     String comparador = null;
     comparador = JNSaNaComplements.InterpretaComparador(ruleExpr, regra);
     variavalValor = ruleExpr.split(comparador);
     regra.setValue(variavalValor[1].trim().replaceAll("'", ""));
     regra.setVar(new NCLVariable(variavalValor[0].trim()));
     return regra;
   }
 
   void Add(JSONObject rule) throws XMLException
   {
     String expressao = (String)rule.get("expression");
     String id = (String)rule.get("id");
     if (!VariableReplaced(rule).booleanValue())
       this.Base.addRule(InterRule(expressao, id));
   }
 
   Boolean VariableReplaced(JSONObject regra)
     throws XMLException
   {
     int i = 0;
     Boolean retorno = Boolean.valueOf(false);
     String id = (String)regra.get("id");
     String expressao = (String)regra.get("expression");
     for (i = 0; i < this.VectorDeAuxiliarSwitRule.size(); i++) {
       if (((String)((Vector)this.VectorDeAuxiliarSwitRule.elementAt(i)).elementAt(1)).contains(id)) {
         Vector vetorDeTeste = (Vector)((Vector)this.VectorDeAuxiliarSwitRule.elementAt(i)).elementAt(2);
         int j = 0;
         Boolean troca = Boolean.valueOf(false);
         String novaExpressao = expressao;
         for (j = 0; j < vetorDeTeste.size(); j++) {
           if (expressao.contains((String)vetorDeTeste.elementAt(j))) {
             retorno = Boolean.valueOf(true);
             troca = Boolean.valueOf(true);
             novaExpressao = expressao.replace((String)vetorDeTeste.elementAt(j), 
               (String)((Vector)((Vector)this.VectorDeAuxiliarSwitRule.elementAt(i)).elementAt(3)).elementAt(j));
           }
         }
 
         if (troca.booleanValue()) {
           this.Base.addRule(InterRule(novaExpressao, id + "_" + (String)((Vector)this.VectorDeAuxiliarSwitRule.elementAt(i)).elementAt(0)));
           expressao = (String)regra.get("expression");
         }
       }
     }
     return retorno;
   }
 }

/* Location:           C:\Users\Convidado\Desktop\pacoteCompleto\jnsTranslator.jar
 * Qualified Name:     JNSRuleInterpreter
 * JD-Core Version:    0.6.2
 */