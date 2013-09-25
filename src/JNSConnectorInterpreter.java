 import br.uff.midiacom.ana.connector.NCLAction;
 import br.uff.midiacom.ana.connector.NCLAssessmentStatement;
 import br.uff.midiacom.ana.connector.NCLAttributeAssessment;
 import br.uff.midiacom.ana.connector.NCLCausalConnector;
 import br.uff.midiacom.ana.connector.NCLCompoundAction;
 import br.uff.midiacom.ana.connector.NCLCompoundCondition;
 import br.uff.midiacom.ana.connector.NCLCondition;
 import br.uff.midiacom.ana.connector.NCLConnectorBase;
 import br.uff.midiacom.ana.connector.NCLConnectorParam;
 import br.uff.midiacom.ana.connector.NCLSimpleAction;
 import br.uff.midiacom.ana.connector.NCLSimpleCondition;
 import br.uff.midiacom.ana.util.enums.NCLActionOperator;
 import br.uff.midiacom.ana.util.enums.NCLAttributeType;
 import br.uff.midiacom.ana.util.enums.NCLConditionOperator;
 import br.uff.midiacom.ana.util.enums.NCLDefaultActionRole;
 import br.uff.midiacom.ana.util.enums.NCLDefaultConditionRole;
 import br.uff.midiacom.ana.util.enums.NCLEventAction;
 import br.uff.midiacom.ana.util.enums.NCLEventType;
 import br.uff.midiacom.ana.util.exception.XMLException;
 import java.util.Vector;
 import org.json.simple.JSONArray;
 import org.json.simple.JSONObject;
 
 public class JNSConnectorInterpreter
 {
   private NCLConnectorBase Base;
 
   JNSConnectorInterpreter()
     throws XMLException
   {
     this.Base = new NCLConnectorBase();
   }
 
   NCLConnectorBase getBase() {
     return this.Base;
   }
 
   void Add(JSONObject jnsConnector) throws XMLException {
     NCLCausalConnector connector = interConnector(jnsConnector);
     this.Base.addCausalConnector(connector);
   }
 
   private NCLCausalConnector interConnector(JSONObject jConnector) throws XMLException {
     NCLCausalConnector nConnector = new NCLCausalConnector((String)jConnector.get("id"));
     String expressao = ((String)jConnector.get("expression")).replace('\'', '"');
     if (jConnector.containsKey("params")) {
       JSONArray jsonParams = (JSONArray)jConnector.get("params");
       int i = 0;
       for (i = 0; i < jsonParams.size(); i++) {
         nConnector.addConnectorParam(new NCLConnectorParam(((String)jsonParams.get(i)).trim()));
       }
     }
     String expresaoCondicao = expressao.substring(0, expressao.indexOf("then"));
     String expressaoAcao = expressao.substring(expressao.indexOf("then") + 4, expressao.length());
     interCondition(expresaoCondicao.trim(), nConnector, null);
     interAction(expressaoAcao.trim(), nConnector);
     return nConnector;
   }
 
   private NCLCondition interCondition(String simpleCondExpression, NCLCausalConnector conectorPai, NCLCompoundCondition condicaoPai) throws XMLException {
     NCLCondition condicao = null;
     if ((simpleCondExpression.toLowerCase().contains("and")) || (simpleCondExpression.toLowerCase().contains("or"))) {
       condicao = new NCLCompoundCondition();
       if (condicaoPai != null)
         condicaoPai.addCondition(condicao);
       else
         conectorPai.setCondition(condicao);
       String operador = null;
       String[] subExpressao = (String[])null;
       if (simpleCondExpression.contains("(")) {
         subExpressao = getInnerParentesis(simpleCondExpression);
         while (subExpressao.length == 1)
           subExpressao = getInnerParentesis(subExpressao[0]);
         int i = subExpressao.length - 1;
         if (subExpressao[i].startsWith("and")) {
           operador = "and";
           ((NCLCompoundCondition)condicao).setOperator(NCLConditionOperator.AND);
         }
         else if (subExpressao[i].startsWith("or")) {
           operador = "or";
           ((NCLCompoundCondition)condicao).setOperator(NCLConditionOperator.OR);
         }
         if (subExpressao[i].contains("delay")) {
           String delay = subExpressao[i].substring(subExpressao[i].indexOf('=') + 1, subExpressao[i].length()).trim();
           ((NCLCompoundCondition)condicao).setDelay(delay.replaceAll("'", ""));
         }
         i = 0;
         while (operador == null) {
           if (subExpressao[i].startsWith("and")) {
             operador = "and";
             ((NCLCompoundCondition)condicao).setOperator(NCLConditionOperator.AND);
           }
           else if (subExpressao[i].startsWith("or")) {
             operador = "or";
             ((NCLCompoundCondition)condicao).setOperator(NCLConditionOperator.OR);
           }
           i++;
         }
       }
       else {
         if (simpleCondExpression.contains("and")) {
           operador = "and";
           ((NCLCompoundCondition)condicao).setOperator(NCLConditionOperator.AND);
         }
         else if (simpleCondExpression.contains("or")) {
           operador = "or";
           ((NCLCompoundCondition)condicao).setOperator(NCLConditionOperator.OR);
         }
         subExpressao = simpleCondExpression.split(operador);
       }
       for (int i = 0; i < subExpressao.length; i++) {
         if (subExpressao[i].startsWith(operador)) {
           if (!subExpressao[i].equals(operador)) {
             String[] sub = subExpressao[i].split(operador);
             for (int j = 0; j < sub.length; j++) {
               if (sub[j].trim().length() > 0) {
                 NCLAssessmentStatement assessement = new NCLAssessmentStatement();
                 String comparador = JNSaNaComplements.InterpretaComparador(sub[j], assessement);
                 if (comparador != null)
                   interAssessmentStatement(sub[j], (NCLCompoundCondition)condicao);
                 else
                   interSimpleCondition(sub[j], conectorPai, condicao);
               }
             }
           }
         }
         else {
           interCondition(subExpressao[i], conectorPai, (NCLCompoundCondition)condicao);
         }
       }
     }
     else
     {
       NCLAssessmentStatement assessement = new NCLAssessmentStatement();
       String comparador = JNSaNaComplements.InterpretaComparador(simpleCondExpression, assessement);
       if (comparador != null) {
         interAssessmentStatement(simpleCondExpression, condicaoPai);
         return null;
       }
 
       condicao = interSimpleCondition(simpleCondExpression.trim(), conectorPai, condicaoPai);
     }
 
     return condicao;
   }
 
   private NCLCondition interSimpleCondition(String simpleCondExpression, NCLCausalConnector connectorPai, NCLCondition condicaoPai) throws XMLException
   {
     NCLSimpleCondition condicao = new NCLSimpleCondition();
     if (condicaoPai != null)
       ((NCLCompoundCondition)condicaoPai).addCondition(condicao);
     else
       connectorPai.setCondition(condicao);
     if (simpleCondExpression.toLowerCase().contains(" with ")) {
       String[] parametrosExtras = simpleCondExpression.substring(simpleCondExpression.indexOf("with ") + 4, simpleCondExpression.length()).split(",");
       condicao = (NCLSimpleCondition)JNSaNaComplements.adicionaParametrosExtas(condicao, parametrosExtras);
     }
 
     if (simpleCondExpression.toLowerCase().startsWith("onbegin"))
     {
       if (simpleCondExpression.toLowerCase().startsWith("onbeginatribuition")) {
         condicao.setRole(NCLDefaultConditionRole.ONBEGINATTRIBUTION);
       }
       else {
         condicao.setRole(NCLDefaultConditionRole.ONBEGIN);
       }
     }
     else if (simpleCondExpression.toLowerCase().startsWith("onend")) {
       if (simpleCondExpression.toLowerCase().startsWith("onendattribution")) {
         condicao.setRole(NCLDefaultConditionRole.ONENDATTRIBUTION);
       }
       else {
         condicao.setRole(NCLDefaultConditionRole.ONEND);
       }
     }
     else if (simpleCondExpression.toLowerCase().startsWith("onabort")) {
       condicao.setRole(NCLDefaultConditionRole.ONABORT);
     }
     else if (simpleCondExpression.toLowerCase().startsWith("onresume")) {
       condicao.setRole(NCLDefaultConditionRole.ONRESUME);
     }
     else if (simpleCondExpression.toLowerCase().startsWith("onselection")) {
       condicao.setRole(NCLDefaultConditionRole.ONSELECTION);
     }
     else {
       condicao.setRole(simpleCondExpression.substring(0, simpleCondExpression.indexOf("with")).trim());
     }
 
     return condicao;
   }
 
   private void interAssessmentStatement(String asessemtExpr, NCLCompoundCondition condicaoPai) throws XMLException {
     NCLAssessmentStatement assessement = new NCLAssessmentStatement();
     condicaoPai.addStatement(assessement);
     String[] valValor = (String[])null;
     String comparador = null;
     comparador = JNSaNaComplements.InterpretaComparador(asessemtExpr, assessement);
     valValor = asessemtExpr.split(comparador);
     for (int indice = 0; indice < valValor.length; indice++) {
       int end = valValor[indice].trim().indexOf(" ");
       if (indice > 0)
         end = valValor[indice].trim().length();
       String role = valValor[indice].trim().substring(0, end);
       if (valValor[indice].toLowerCase().contains(" with ")) {
         NCLAttributeAssessment atributeAss = new NCLAttributeAssessment();
         atributeAss.setRole(role);
         String[] parametrosExtras = valValor[indice].substring(valValor[indice].indexOf(" with ") + 5, valValor[indice].length()).split(",");
         for (int i = 0; i < parametrosExtras.length; i++) {
           if (parametrosExtras[i].toLowerCase().contains("eventtype")) {
             String tipo = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"") + 1, parametrosExtras[i].length());
             tipo = tipo.substring(0, tipo.indexOf('"'));
             atributeAss.setEventType(NCLEventType.valueOf(tipo.toUpperCase()));
           }
           else if (parametrosExtras[i].toLowerCase().contains("attributetype")) {
             String tipo = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"") + 1, parametrosExtras[i].length());
             tipo = tipo.substring(0, tipo.indexOf('"'));
             atributeAss.setAttributeType(NCLAttributeType.getEnumType(tipo));
           }
           else if (parametrosExtras[i].toLowerCase().contains("key")) {
             String key = parametrosExtras[i].substring(parametrosExtras[i].indexOf("=") + 1, parametrosExtras[i].length());
             key = key.replace("\"", "");
             atributeAss.setKey(key);
           }
           else if (parametrosExtras[i].toLowerCase().contains("offset")) {
             String offset = parametrosExtras[i].substring(parametrosExtras[i].indexOf("=") + 1, parametrosExtras[i].length());
             offset = offset.replace("\"", "");
             atributeAss.setOffset(offset);
           }
         }
         assessement.addAttributeAssessment(atributeAss);
       }
       else {
         assessement.setValueAssessment(role.replace("\"", ""));
       }
     }
   }
 
   private NCLAction interAction(String simpleActExpression, NCLCausalConnector connectorPai) throws XMLException { return interAction(simpleActExpression, connectorPai, null); }
 
   private NCLAction interAction(String simpleActExpression, NCLCausalConnector connectorPai, NCLCompoundAction acaoPai) throws XMLException {
     NCLAction acao = null;
     if ((simpleActExpression.contains(";")) || (simpleActExpression.contains("||"))) {
       acao = new NCLCompoundAction();
       if (acaoPai == null)
         connectorPai.setAction(acao);
       else
         acaoPai.addAction(acao);
       String operador = null;
       String[] subExpressao = (String[])null;
       if (simpleActExpression.contains("(")) {
         subExpressao = getInnerParentesis(simpleActExpression);
         while (subExpressao.length == 1)
           subExpressao = getInnerParentesis(subExpressao[0]);
         int i = subExpressao.length - 1;
         if (subExpressao[i].startsWith(";")) {
           operador = ";";
           ((NCLCompoundAction)acao).setOperator(NCLActionOperator.SEQ);
         }
         else if (subExpressao[i].startsWith("||")) {
           operador = "\\|\\|";
           ((NCLCompoundAction)acao).setOperator(NCLActionOperator.PAR);
         }
         if (subExpressao[i].contains("delay")) {
           String delay = subExpressao[i].substring(subExpressao[i].indexOf('=') + 1, subExpressao[i].length()).trim();
           ((NCLCompoundAction)acao).setDelay(delay.replaceAll("'", ""));
         }
         i = 0;
         while (operador == null) {
           if (subExpressao[i].startsWith(";")) {
             operador = ";";
             ((NCLCompoundAction)acao).setOperator(NCLActionOperator.SEQ);
           }
           else if (subExpressao[i].startsWith("||")) {
             operador = "\\|\\|";
             ((NCLCompoundAction)acao).setOperator(NCLActionOperator.PAR);
           }
           i++;
         }
       }
       else {
         if (simpleActExpression.contains(";")) {
           operador = ";";
           ((NCLCompoundAction)acao).setOperator(NCLActionOperator.SEQ);
         }
         else if (simpleActExpression.contains("||")) {
           operador = "\\|\\|";
           ((NCLCompoundAction)acao).setOperator(NCLActionOperator.PAR);
         }
         subExpressao = simpleActExpression.split(operador);
       }
       String operator = operador;
       if (operador.equals("\\|\\|"))
         operator = "||";
       for (int i = 0; i < subExpressao.length; i++) {
         if (subExpressao[i].startsWith(operator)) {
           if (!subExpressao[i].equals(operator)) {
             String[] sub = subExpressao[i].split(operador);
             for (int j = 0; j < sub.length; j++)
               if (sub[j].trim().length() > 0)
                 interSimpleAction(sub[j].trim(), connectorPai, (NCLCompoundAction)acao);
           }
         }
         else {
           interAction(subExpressao[i], connectorPai, (NCLCompoundAction)acao);
         }
       }
     }
     else
     {
       acao = interSimpleAction(simpleActExpression.trim(), connectorPai, acaoPai);
     }
 
     return acao;
   }
   public static String[] getInnerParentesis(String expression) {
     Vector auxiliar = new Vector();
     char[] stringExp = expression.toCharArray();
     int parem_num = 0;
     int primeiroInd = 0;
     int ultimoInd = 0;
     Boolean adiciona = Boolean.valueOf(false);
     for (int i = 0; i < stringExp.length; i++) {
       if (stringExp[i] == '(') {
         parem_num++;
         if (!adiciona.booleanValue()) {
           if ((i > ultimoInd) && (ultimoInd != 0)) {
             auxiliar.add(expression.substring(ultimoInd + 1, i).trim());
           }
           primeiroInd = i + 1;
           adiciona = Boolean.valueOf(true);
         }
       }
       else if (stringExp[i] == ')') {
         parem_num--;
         ultimoInd = i;
       }
       if ((parem_num == 0) && (adiciona.booleanValue())) {
         auxiliar.add(expression.substring(primeiroInd, ultimoInd).trim());
         adiciona = Boolean.valueOf(false);
       }
     }
     if (ultimoInd != expression.length() - 1)
       auxiliar.add(expression.substring(ultimoInd + 1, expression.length()).trim());
     String[] retorno = new String[auxiliar.size()];
     auxiliar.toArray(retorno);
     return retorno;
   }
 
   private NCLAction interSimpleAction(String simpleActExpression, NCLCausalConnector connectorPai) throws XMLException {
     return interSimpleAction(simpleActExpression, connectorPai, null);
   }
 
   private NCLAction interSimpleAction(String simpleActExpression, NCLCausalConnector connectorPai, NCLCompoundAction acaoPai) throws XMLException {
     NCLSimpleAction acao = new NCLSimpleAction();
     if (acaoPai == null)
       connectorPai.setAction(acao);
     else {
       acaoPai.addAction(acao);
     }
     if (simpleActExpression.trim().toLowerCase().startsWith("start")) {
       acao.setRole(NCLDefaultActionRole.START);
     }
     else if (simpleActExpression.trim().toLowerCase().startsWith("stop")) {
       acao.setRole(NCLDefaultActionRole.STOP);
     }
     else if (simpleActExpression.trim().toLowerCase().startsWith("abort")) {
       acao.setRole(NCLDefaultActionRole.ABORT);
     }
     else if (simpleActExpression.trim().toLowerCase().startsWith("pause")) {
       acao.setRole(NCLDefaultActionRole.PAUSE);
     }
     else if (simpleActExpression.trim().toLowerCase().startsWith("resume")) {
       acao.setRole(NCLDefaultActionRole.RESUME);
     }
     else if (simpleActExpression.trim().toLowerCase().startsWith("set")) {
       acao.setRole(NCLDefaultActionRole.SET);
     }
     else {
       acao.setRole(simpleActExpression.trim().substring(0, simpleActExpression.trim().indexOf(" ")));
     }
 
     if (simpleActExpression.toLowerCase().contains(" with ")) {
       String[] parametrosExtras = simpleActExpression.substring(simpleActExpression.indexOf("with ") + 4, simpleActExpression.length()).split(",");
       for (int i = 0; i < parametrosExtras.length; i++) {
         parametrosExtras[i] = parametrosExtras[i].trim();
         if (parametrosExtras[i].toLowerCase().contains("delay")) {
           parametrosExtras[i] = parametrosExtras[i].trim();
           String delay = parametrosExtras[i].substring(parametrosExtras[i].indexOf("=") + 1, parametrosExtras[i].length()).trim();
           delay = delay.replace("\"", "").trim();
           acao.setDelay(delay);
         }
         else if (parametrosExtras[i].toLowerCase().contains("eventtype")) {
           String tipo = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"") + 1, parametrosExtras[i].length()).trim();
           tipo = tipo.substring(0, tipo.indexOf('"'));
           acao.setEventType(NCLEventType.valueOf(tipo.toUpperCase()));
         }
         else if (parametrosExtras[i].toLowerCase().contains("actiontype")) {
           String tipo = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"") + 1, parametrosExtras[i].length()).trim();
           tipo = tipo.substring(0, tipo.indexOf('"'));
           acao.setActionType(NCLEventAction.getEnumType(tipo.toUpperCase()));
         }
         else if (parametrosExtras[i].toLowerCase().contains("value")) {
           String value = parametrosExtras[i].substring(parametrosExtras[i].indexOf("=") + 1, parametrosExtras[i].length()).trim();
           value = value.replace("\"", "");
           acao.setValue(value);
         }
         else if (parametrosExtras[i].toLowerCase().contains("min")) {
           String min = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"") + 1, parametrosExtras[i].length()).trim();
           min = min.substring(0, min.indexOf('"'));
           acao.setMin(Integer.valueOf(Integer.parseInt(min)));
         }
         else if (parametrosExtras[i].toLowerCase().contains("max")) {
           String max = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"") + 1, parametrosExtras[i].length()).trim();
           max = max.substring(0, max.indexOf('"'));
           acao.setMax(max);
         }
         else if (parametrosExtras[i].toLowerCase().contains("qualifier")) {
           String qual = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"") + 1, parametrosExtras[i].length()).trim();
           qual = qual.substring(0, qual.indexOf('"'));
           acao.setQualifier(NCLActionOperator.valueOf(qual.toUpperCase()));
         }
         else if (parametrosExtras[i].toLowerCase().contains("repeat")) {
           String repeat = parametrosExtras[i].substring(parametrosExtras[i].indexOf("=") + 1, parametrosExtras[i].length()).trim();
           repeat = repeat.replace("\"", "");
           acao.setRepeat(repeat);
         }
         else if (parametrosExtras[i].toLowerCase().contains("repeatdelay")) {
           String delay = parametrosExtras[i].substring(parametrosExtras[i].indexOf("=") + 1, parametrosExtras[i].length()).trim();
           delay = delay.replace("\"", "");
           acao.setRepeatDelay(delay);
         }
         else if (parametrosExtras[i].toLowerCase().contains("duration")) {
           String duration = parametrosExtras[i].substring(parametrosExtras[i].indexOf("=") + 1, parametrosExtras[i].length()).trim();
           duration = duration.replace("\"", "");
           acao.setDuration(duration);
         }
         else if (parametrosExtras[i].toLowerCase().contains("by")) {
           String by = parametrosExtras[i].substring(parametrosExtras[i].indexOf("=") + 1, parametrosExtras[i].length()).trim();
           by = by.replace("\"", "");
           acao.setBy(by);
         }
       }
     }
     return acao;
   }
 }

/* Location:           C:\Users\Convidado\Desktop\pacoteCompleto\jnsTranslator.jar
 * Qualified Name:     JNSConnectorInterpreter
 * JD-Core Version:    0.6.2
 */