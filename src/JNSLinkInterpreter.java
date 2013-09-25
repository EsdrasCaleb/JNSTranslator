 import br.uff.midiacom.ana.NCLBody;
 import br.uff.midiacom.ana.NCLElement;
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
 import br.uff.midiacom.ana.connector.NCLStatement;
 import br.uff.midiacom.ana.descriptor.NCLDescriptorBase;
 import br.uff.midiacom.ana.interfaces.NCLInterface;
 import br.uff.midiacom.ana.interfaces.NCLProperty;
 import br.uff.midiacom.ana.link.NCLBind;
 import br.uff.midiacom.ana.link.NCLBindParam;
 import br.uff.midiacom.ana.link.NCLLink;
 import br.uff.midiacom.ana.link.NCLLinkParam;
 import br.uff.midiacom.ana.node.NCLNode;
 import br.uff.midiacom.ana.util.ElementList;
 import br.uff.midiacom.ana.util.enums.NCLActionOperator;
 import br.uff.midiacom.ana.util.enums.NCLAttributeType;
 import br.uff.midiacom.ana.util.enums.NCLConditionOperator;
 import br.uff.midiacom.ana.util.enums.NCLDefaultActionRole;
 import br.uff.midiacom.ana.util.enums.NCLEventAction;
 import br.uff.midiacom.ana.util.enums.NCLEventType;
 import br.uff.midiacom.ana.util.exception.XMLException;
 import org.json.simple.JSONArray;
 import org.json.simple.JSONObject;
 
 public class JNSLinkInterpreter
 {
   private int ConnectorIndex;
   private int IndiceAssassement;
   private JNSHeadInterpreter interpretadorHead;
 
   JNSLinkInterpreter(JNSHeadInterpreter interpretadorHead)
   {
     this.interpretadorHead = interpretadorHead;
     this.ConnectorIndex = 0;
     this.IndiceAssassement = 0;
   }
 
   NCLLink Interprets(JSONObject jnsLink, Object contexto) throws XMLException {
     NCLLink elo = new NCLLink();
     if (jnsLink.containsKey("id")) {
       elo.setId((String)jnsLink.get("id"));
     }
     if (jnsLink.containsKey("expression")) {
       interpretsExpression(((String)jnsLink.get("expression")).replace('\'', '"'), elo, contexto);
     }
     else {
       if (jnsLink.containsKey("xconnector")) {
         elo.setXconnector(JNSaNaComplements.FindConnector((String)jnsLink.get("xconnector"), this.interpretadorHead.getInterpretaConnector().getBase()));
       }
       if (jnsLink.containsKey("params")) {
         if ((jnsLink.get("params") instanceof JSONArray)) {
           JSONArray jnsParams = (JSONArray)jnsLink.get("params");
           for (int i = 0; i < jnsParams.size(); i++) {
             String[] parametros = JNSjSONComplements.getKeys((JSONObject)jnsParams.get(i));
             for (int j = 0; j < parametros.length; j++)
               elo.addLinkParam(interpretsParameter((JSONObject)jnsParams.get(i), elo, parametros[j]));
           }
         }
         else
         {
           String[] parametros = JNSjSONComplements.getKeys((JSONObject)jnsLink.get("params"));
           for (int j = 0; j < parametros.length; j++) {
             elo.addLinkParam(interpretsParameter((JSONObject)jnsLink.get("params"), elo, parametros[j]));
           }
         }
       }
       if (jnsLink.containsKey("binds")) {
         JSONArray jnsBinds = (JSONArray)jnsLink.get("binds");
         for (int i = 0; i < jnsBinds.size(); i++) {
           String[] roles = JNSjSONComplements.getKeys((JSONObject)jnsBinds.get(i));
           String role = null;
           for (int j = 0; j < roles.length; j++) {
             if ((!roles[j].equals("params")) && (!roles[j].equals("descriptor")))
               role = roles[j];
           }
           String obBind = (String)((JSONObject)jnsBinds.get(i)).get(role);
           Object jnsParams = null;
           String descriptor = null;
           if (((JSONObject)jnsBinds.get(i)).containsKey("descriptor")) {
             descriptor = (String)((JSONObject)jnsBinds.get(i)).get("descriptor");
           }
           if (((JSONObject)jnsBinds.get(i)).containsKey("params")) {
             jnsParams = ((JSONObject)jnsBinds.get(i)).get("params");
           }
           if (obBind.contains(",")) {
             String[] obBinds = obBind.split(",");
             for (int j = 0; j < obBinds.length; j++)
               interpretsBind(obBinds[j], role, contexto, (NCLCausalConnector)elo.getXconnector(), elo, jnsParams, descriptor);
           }
           else {
             interpretsBind(obBind, role, contexto, (NCLCausalConnector)elo.getXconnector(), elo, jnsParams, descriptor);
           }
         }
       }
     }
     return elo;
   }
 
   private void interpretsExpression(String expression, NCLLink elo, Object contexto) throws XMLException {
     String expresaoCondicao = expression.substring(0, expression.indexOf("then"));
     String expressaoAcao = expression.substring(expression.indexOf("then") + 4, expression.length());
     NCLCausalConnector nConnector = new NCLCausalConnector("Connector_Padrao" + this.ConnectorIndex++);
     elo.setXconnector(nConnector);
     interpretsCondExpression(expresaoCondicao.trim(), nConnector, contexto, elo);
     interpretsActExpression(expressaoAcao.trim(), nConnector, contexto, elo);
     this.interpretadorHead.getInterpretaConnector().getBase().addCausalConnector(nConnector);
     this.IndiceAssassement = 0;
   }
 
   private void interpretsCondExpression(String expresaoCondicao, Object pai, Object contexto, NCLLink elo) throws XMLException {
     NCLCondition codicaoRetorno = null;
     NCLCausalConnector conector = null;
     if ((expresaoCondicao.contains(" and ")) || (expresaoCondicao.contains(" or "))) {
       codicaoRetorno = new NCLCompoundCondition();
       String operador = null;
       conector = pegaConector(pai, codicaoRetorno);
       String[] subExpressao = (String[])null;
       if (expresaoCondicao.contains("(")) {
         subExpressao = JNSConnectorInterpreter.getInnerParentesis(expresaoCondicao);
         while ((subExpressao.length == 1) && (subExpressao[0].contains("(")))
           subExpressao = JNSConnectorInterpreter.getInnerParentesis(subExpressao[0]);
         int i = 0;
         if (subExpressao.length > 1)
           i = subExpressao.length - 1;
         if (subExpressao[i].startsWith("and")) {
           operador = "and";
           ((NCLCompoundCondition)codicaoRetorno).setOperator(NCLConditionOperator.AND);
         }
         else if (subExpressao[i].startsWith("or")) {
           operador = "or";
           ((NCLCompoundCondition)codicaoRetorno).setOperator(NCLConditionOperator.OR);
         }
         if (subExpressao[i].contains("delay")) {
           String delay = subExpressao[i].substring(subExpressao[i].indexOf('=') + 1, subExpressao[i].length()).trim();
           ((NCLCompoundCondition)codicaoRetorno).setDelay(delay.replaceAll("'", ""));
         }
         i = 0;
         while (operador == null) {
           if (subExpressao[i].startsWith("and")) {
             operador = "and";
             ((NCLCompoundCondition)codicaoRetorno).setOperator(NCLConditionOperator.AND);
           }
           else if (subExpressao[i].startsWith("or")) {
             operador = "or";
             ((NCLCompoundCondition)codicaoRetorno).setOperator(NCLConditionOperator.OR);
           }
           i++;
         }
       }
       else {
         if (expresaoCondicao.contains("and")) {
           operador = "and";
           ((NCLCompoundCondition)codicaoRetorno).setOperator(NCLConditionOperator.AND);
         }
         else if (expresaoCondicao.contains("or")) {
           operador = "or";
           ((NCLCompoundCondition)codicaoRetorno).setOperator(NCLConditionOperator.OR);
         }
         subExpressao = expresaoCondicao.split(operador);
       }
       for (int i = 0; i < subExpressao.length; i++) {
         if (subExpressao[i].startsWith(operador)) {
           if (!subExpressao[i].equals(operador)) {
             String[] sub = subExpressao[i].split(operador);
             for (int j = 0; j < sub.length; j++) {
               if (sub[j].trim().length() > 0)
                 interpretsCondExpression(sub[j].trim(), codicaoRetorno, contexto, elo);
             }
           }
         }
         else
           interpretsCondExpression(subExpressao[i].trim(), codicaoRetorno, contexto, elo);
       }
     }
     else
     {
       String roleString = "";
       String expressaoLimpa = expresaoCondicao.trim().replace('\t', ' ');
       roleString = expressaoLimpa.trim().substring(0, expressaoLimpa.indexOf(' '));
       NCLAssessmentStatement assessement = new NCLAssessmentStatement();
       String comparador = JNSaNaComplements.InterpretaComparador(expresaoCondicao, assessement);
       if (comparador != null) {
         conector = pegaConector(pai, assessement);
         codicaoRetorno = (NCLCompoundCondition)pai;
         String[] valValor = (String[])null;
         valValor = expresaoCondicao.split(comparador);
         for (int indice = 0; indice < valValor.length; indice++) {
           int end = valValor[indice].trim().indexOf(" ");
           if (end < 0)
             end = valValor[indice].trim().length();
           String role = valValor[indice].trim().substring(0, end);
           NCLInterface inter = null;
           if (role.contains(".")) {
             String[] idInterfaceT = new String[2];
             int ini = role.indexOf('.');
             idInterfaceT[0] = role.substring(0, ini);
             idInterfaceT[1] = role.substring(ini + 1);
             while (!(contexto instanceof NCLBody)) {
               contexto = ((NCLElement)contexto).getParent();
             }
             NCLNode noT = JNSaNaComplements.FindNode(idInterfaceT[0], contexto);
             inter = JNSaNaComplements.FindInterface(idInterfaceT[1], noT);
           }
           NCLAttributeAssessment atributeAss = new NCLAttributeAssessment();
           atributeAss.setRole("defaultRole" + this.IndiceAssassement);
           if ((inter instanceof NCLProperty)) {
             atributeAss.setEventType(NCLEventType.ATTRIBUTION);
             atributeAss.setAttributeType(NCLAttributeType.NODE_PROPERTY);
             assessement.addAttributeAssessment(atributeAss);
             interpretsBind(role, "defaultRole" + this.IndiceAssassement++, contexto, conector, elo, null, null);
           }
           else if (valValor[indice].contains(" with ")) {
             atributeAss.setRole(role);
             String[] parametrosExtras = valValor[indice].substring(valValor[indice].indexOf(" with ") + 5, valValor[indice].length()).split(",");
             for (int i = 0; i < parametrosExtras.length; i++) {
               if (parametrosExtras[i].contains("eventtype")) {
                 String tipo = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"") + 1, parametrosExtras[i].length());
                 tipo = tipo.substring(0, tipo.indexOf('"'));
                 atributeAss.setEventType(NCLEventType.getEnumType(tipo));
               }
               else if (parametrosExtras[i].contains("attributetype")) {
                 String tipo = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"") + 1, parametrosExtras[i].length());
                 tipo = tipo.substring(0, tipo.indexOf('"'));
                 atributeAss.setAttributeType(NCLAttributeType.getEnumType(tipo));
               }
               else if (parametrosExtras[i].contains("key")) {
                 String key = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"") + 1, parametrosExtras[i].length());
                 key = key.substring(0, key.indexOf('"'));
                 atributeAss.setKey(key);
               }
               else if (parametrosExtras[i].contains("offset")) {
                 String offset = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"") + 1, parametrosExtras[i].length());
                 offset = offset.substring(0, offset.indexOf('"'));
                 atributeAss.setOffset(offset);
               }
             }
             assessement.addAttributeAssessment(atributeAss);
             interpretsBind(role, "defaultRole" + this.IndiceAssassement++, contexto, conector, elo, null, null);
           }
           else {
             assessement.setValueAssessment(role.replace("\"", ""));
           }
         }
       }
       else {
         codicaoRetorno = new NCLSimpleCondition();
         conector = pegaConector(pai, codicaoRetorno);
         ((NCLSimpleCondition)codicaoRetorno).setRole(roleString);
         String targets = expresaoCondicao.substring(roleString.length() + 1, expresaoCondicao.length());
         int ind = targets.indexOf("with");
         String[] idInterface = targets.split(",");
         ((NCLSimpleCondition)codicaoRetorno).setMax(Integer.valueOf(idInterface.length));
         ((NCLSimpleCondition)codicaoRetorno).setQualifier(NCLConditionOperator.AND);
         if (ind > 0) {
           targets = targets.substring(0, ind);
           idInterface = targets.split(",");
           ((NCLSimpleCondition)codicaoRetorno).setMax(Integer.valueOf(idInterface.length));
           String[] parametrosExtras = expresaoCondicao.substring(expresaoCondicao.indexOf(" with ") + 5, expresaoCondicao.length()).split(",");
           codicaoRetorno = JNSaNaComplements.adicionaParametrosExtas(codicaoRetorno, parametrosExtras);
         }
         for (int i = 0; i < idInterface.length; i++)
           interpretsBind(idInterface[i], roleString, contexto, conector, elo, null, null);
       }
     }
   }
 
   private NCLCausalConnector pegaConector(Object pai, Object chamador) throws XMLException
   {
     NCLCausalConnector conector = null;
     if ((pai instanceof NCLCausalConnector)) {
       conector = (NCLCausalConnector)pai;
       if ((chamador instanceof NCLCondition))
         conector.setCondition((NCLCondition)chamador);
       else
         conector.setAction((NCLAction)chamador);
     }
     else if ((pai instanceof NCLCompoundCondition)) {
       NCLCompoundCondition condicaoAuxiliar = (NCLCompoundCondition)pai;
       if ((chamador instanceof NCLCondition))
         condicaoAuxiliar.addCondition((NCLCondition)chamador);
       else
         condicaoAuxiliar.addStatement((NCLStatement)chamador);
       while (!(condicaoAuxiliar.getParent() instanceof NCLCausalConnector)) {
         condicaoAuxiliar = (NCLCompoundCondition)condicaoAuxiliar.getParent();
       }
       conector = (NCLCausalConnector)condicaoAuxiliar.getParent();
     }
     else if ((pai instanceof NCLCompoundAction)) {
       NCLCompoundAction acaoAuxiliar = (NCLCompoundAction)pai;
       acaoAuxiliar.addAction((NCLAction)chamador);
       while (!(acaoAuxiliar.getParent() instanceof NCLCausalConnector)) {
         acaoAuxiliar = (NCLCompoundAction)acaoAuxiliar.getParent();
       }
       conector = (NCLCausalConnector)acaoAuxiliar.getParent();
     }
     return conector;
   }
 
   private NCLAction interpretsActExpression(String expresaoAcao, Object pai, Object contexto, NCLLink elo) throws XMLException {
     NCLAction acao = null;
     NCLCausalConnector connector = null;
     if ((expresaoAcao.contains(";")) || (expresaoAcao.contains("||"))) {
       acao = new NCLCompoundAction();
       connector = pegaConector(pai, acao);
       String operador = null;
       String[] subExpressao = (String[])null;
       if (expresaoAcao.contains("(")) {
         subExpressao = JNSConnectorInterpreter.getInnerParentesis(expresaoAcao);
         while (subExpressao.length == 1)
           subExpressao = JNSConnectorInterpreter.getInnerParentesis(subExpressao[0]);
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
         if (expresaoAcao.contains(";")) {
           operador = ";";
           ((NCLCompoundAction)acao).setOperator(NCLActionOperator.SEQ);
         }
         else if (expresaoAcao.contains("||")) {
           operador = "\\|\\|";
           ((NCLCompoundAction)acao).setOperator(NCLActionOperator.PAR);
         }
         subExpressao = expresaoAcao.split(operador);
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
                 interpretsActExpression(sub[j].trim(), acao, contexto, elo);
           }
         }
         else
           interpretsActExpression(subExpressao[i].trim(), acao, contexto, elo);
       }
     }
     else
     {
       acao = new NCLSimpleAction();
       connector = pegaConector(pai, acao);
       if (expresaoAcao.toLowerCase().startsWith("start")) {
         ((NCLSimpleAction)acao).setRole(NCLDefaultActionRole.START);
       }
       else if (expresaoAcao.toLowerCase().startsWith("stop")) {
         ((NCLSimpleAction)acao).setRole(NCLDefaultActionRole.STOP);
       }
       else if (expresaoAcao.toLowerCase().startsWith("abort")) {
         ((NCLSimpleAction)acao).setRole(NCLDefaultActionRole.ABORT);
       }
       else if (expresaoAcao.toLowerCase().startsWith("pause")) {
         ((NCLSimpleAction)acao).setRole(NCLDefaultActionRole.PAUSE);
       }
       else if (expresaoAcao.toLowerCase().startsWith("resume")) {
         ((NCLSimpleAction)acao).setRole(NCLDefaultActionRole.RESUME);
       }
       else if (expresaoAcao.toLowerCase().startsWith("set")) {
         ((NCLSimpleAction)acao).setRole(NCLDefaultActionRole.SET);
         int indiceSet = expresaoAcao.indexOf("set") + 4;
         String valor = expresaoAcao.substring(indiceSet, expresaoAcao.indexOf(' ', indiceSet));
         ((NCLSimpleAction)acao).setValue(valor);
       }
       else {
         ((NCLSimpleAction)acao).setRole(expresaoAcao.trim().substring(0, expresaoAcao.trim().indexOf(" ")));
       }
       String targets = expresaoAcao.substring(expresaoAcao.indexOf(((NCLSimpleAction)acao).getRole().toString()) + ((NCLSimpleAction)acao).getRole().toString().length(), expresaoAcao.length());
       int ind = targets.indexOf("with");
       String[] idInterface = targets.split(",");
       if (idInterface.length > 1) {
         ((NCLSimpleAction)acao).setMax(Integer.valueOf(idInterface.length));
         ((NCLSimpleAction)acao).setQualifier(NCLActionOperator.PAR);
       }
       if (ind > 0) {
         targets = targets.substring(0, ind);
         idInterface = targets.split(",");
         ((NCLSimpleAction)acao).setMax(Integer.valueOf(idInterface.length));
         String[] parametrosExtras = expresaoAcao.substring(expresaoAcao.indexOf(" with ") + 5, expresaoAcao.length()).split(" , ");
         for (int i = 0; i < parametrosExtras.length; i++) {
           parametrosExtras[i] = parametrosExtras[i].trim();
           if (parametrosExtras[i].contains("delay")) {
             parametrosExtras[i] = parametrosExtras[i].trim();
             String delay = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"") + 1, parametrosExtras[i].length()).trim();
             delay = delay.substring(0, delay.indexOf('"'));
             acao.setDelay(delay.replace("s", ""));
           }
           else if (parametrosExtras[i].contains("eventtype")) {
             String tipo = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"") + 1, parametrosExtras[i].length()).trim();
             tipo = tipo.substring(0, tipo.indexOf('"'));
             ((NCLSimpleAction)acao).setEventType(NCLEventType.valueOf(tipo.toUpperCase()));
           }
           else if (parametrosExtras[i].contains("actiontype")) {
             String tipo = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"") + 1, parametrosExtras[i].length()).trim();
             tipo = tipo.substring(0, tipo.indexOf('"'));
             ((NCLSimpleAction)acao).setActionType(NCLEventAction.getEnumType(tipo.toUpperCase()));
           }
           else if (parametrosExtras[i].contains("value")) {
             String value = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"") + 1, parametrosExtras[i].length()).trim();
             value = value.substring(0, value.indexOf('"'));
             ((NCLSimpleAction)acao).setValue(value);
           }
           else if (parametrosExtras[i].contains("min")) {
             String min = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"") + 1, parametrosExtras[i].length()).trim();
             min = min.substring(0, min.indexOf('"'));
             ((NCLSimpleAction)acao).setMin(Integer.valueOf(Integer.parseInt(min)));
           }
           else if (parametrosExtras[i].contains("max")) {
             String max = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"") + 1, parametrosExtras[i].length()).trim();
             max = max.substring(0, max.indexOf('"'));
             ((NCLSimpleAction)acao).setMax(max);
           }
           else if (parametrosExtras[i].contains("qualifier")) {
             String qual = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"") + 1, parametrosExtras[i].length()).trim();
             qual = qual.substring(0, qual.indexOf('"'));
             ((NCLSimpleAction)acao).setQualifier(NCLActionOperator.valueOf(qual.toUpperCase()));
           }
           else if (parametrosExtras[i].contains("repeat")) {
             String repeat = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"") + 1, parametrosExtras[i].length()).trim();
             repeat = repeat.substring(0, repeat.indexOf('"'));
             ((NCLSimpleAction)acao).setRepeat(repeat);
           }
           else if (parametrosExtras[i].contains("repeatdelay")) {
             String delay = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"") + 1, parametrosExtras[i].length()).trim();
             delay = delay.substring(0, delay.indexOf('"'));
             ((NCLSimpleAction)acao).setRepeatDelay(delay.replace("s", ""));
           }
           else if (parametrosExtras[i].contains("duration")) {
             String duration = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"") + 1, parametrosExtras[i].length()).trim();
             duration = duration.substring(0, duration.indexOf('"'));
             ((NCLSimpleAction)acao).setDuration(Double.valueOf(Double.parseDouble(duration)));
           }
           else if (parametrosExtras[i].contains("by")) {
             String by = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"") + 1, parametrosExtras[i].length()).trim();
             by = by.substring(0, by.indexOf('"'));
             ((NCLSimpleAction)acao).setBy(by);
           }
         }
       }
       for (int i = 0; i < idInterface.length; i++) {
         interpretsBind(idInterface[i], ((NCLSimpleAction)acao).getRole().toString(), contexto, connector, elo, null, null);
       }
     }
     return acao;
   }
 
   private NCLLinkParam interpretsParameter(JSONObject parameter, NCLLink elo, String nome) throws XMLException {
     NCLLinkParam parametro = new NCLLinkParam();
     parametro.setName((NCLConnectorParam)((NCLCausalConnector)elo.getXconnector()).getConnectorParams().get(nome));
     parametro.setValue((String)parameter.get(nome));
     return parametro;
   }
 
   private NCLBind interpretsBind(String componente, String role, Object contexto, NCLCausalConnector conector, NCLLink elo, Object jnsParams, String descriptor) throws XMLException {
     NCLBind bind = new NCLBind();
     elo.addBind(bind);
     if (jnsParams != null) {
       if ((jnsParams instanceof JSONArray)) {
         JSONArray jnsParamsA = (JSONArray)jnsParams;
         for (int i = 0; i < jnsParamsA.size(); i++) {
           String[] parametros = JNSjSONComplements.getKeys((JSONObject)jnsParamsA.get(i));
           for (int j = 0; j < parametros.length; j++)
             bind.addBindParam(interpretsBindParameter((JSONObject)jnsParamsA.get(i), conector, parametros[j]));
         }
       }
       else
       {
         String[] paramaetros = JNSjSONComplements.getKeys((JSONObject)jnsParams);
         for (int i = 0; i < paramaetros.length; i++) {
           bind.addBindParam(interpretsBindParameter((JSONObject)jnsParams, conector, paramaetros[i]));
         }
       }
     }
     if (descriptor != null) {
       bind.setDescriptor(this.interpretadorHead.getInterpretadorDescritor().getBase().findDescriptor(descriptor));
     }
     if (componente.contains(".")) {
       String[] componentes = new String[2];
       int ini = componente.indexOf('.');
       componentes[0] = componente.substring(0, ini);
       componentes[1] = componente.substring(ini + 1);
       while (!(contexto instanceof NCLBody)) {
         contexto = ((NCLElement)contexto).getParent();
       }
       bind.setComponent(JNSaNaComplements.FindNode(componentes[0].trim(), contexto));
       bind.setInterface(JNSaNaComplements.FindInterface(componentes[1].trim(), bind.getComponent()));
     }
     else {
       bind.setComponent(JNSaNaComplements.FindNode(componente.trim(), contexto));
     }
     bind.setRole(JNSaNaComplements.FindRole(role, conector));
     return bind;
   }
 
   private NCLBindParam interpretsBindParameter(JSONObject parameter, NCLCausalConnector conector, String nome) throws XMLException {
     NCLBindParam parametro = new NCLBindParam();
     parametro.setName((NCLConnectorParam)conector.getConnectorParams().get(nome));
     parametro.setValue((String)parameter.get(nome));
     return parametro;
   }
 }

/* Location:           C:\Users\Convidado\Desktop\pacoteCompleto\jnsTranslator.jar
 * Qualified Name:     JNSLinkInterpreter
 * JD-Core Version:    0.6.2
 */
