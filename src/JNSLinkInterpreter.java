/*     */ import br.uff.midiacom.ana.NCLBody;
/*     */ import br.uff.midiacom.ana.NCLElement;
/*     */ import br.uff.midiacom.ana.connector.NCLAction;
/*     */ import br.uff.midiacom.ana.connector.NCLAssessmentStatement;
/*     */ import br.uff.midiacom.ana.connector.NCLAttributeAssessment;
/*     */ import br.uff.midiacom.ana.connector.NCLCausalConnector;
/*     */ import br.uff.midiacom.ana.connector.NCLCompoundAction;
/*     */ import br.uff.midiacom.ana.connector.NCLCompoundCondition;
/*     */ import br.uff.midiacom.ana.connector.NCLCondition;
/*     */ import br.uff.midiacom.ana.connector.NCLConnectorBase;
/*     */ import br.uff.midiacom.ana.connector.NCLConnectorParam;
/*     */ import br.uff.midiacom.ana.connector.NCLSimpleAction;
/*     */ import br.uff.midiacom.ana.connector.NCLSimpleCondition;
/*     */ import br.uff.midiacom.ana.connector.NCLStatement;
/*     */ import br.uff.midiacom.ana.descriptor.NCLDescriptorBase;
/*     */ import br.uff.midiacom.ana.interfaces.NCLInterface;
/*     */ import br.uff.midiacom.ana.interfaces.NCLProperty;
/*     */ import br.uff.midiacom.ana.link.NCLBind;
/*     */ import br.uff.midiacom.ana.link.NCLBindParam;
/*     */ import br.uff.midiacom.ana.link.NCLLink;
/*     */ import br.uff.midiacom.ana.link.NCLLinkParam;
/*     */ import br.uff.midiacom.ana.node.NCLNode;
/*     */ import br.uff.midiacom.ana.util.ElementList;
/*     */ import br.uff.midiacom.ana.util.enums.NCLActionOperator;
/*     */ import br.uff.midiacom.ana.util.enums.NCLAttributeType;
/*     */ import br.uff.midiacom.ana.util.enums.NCLConditionOperator;
/*     */ import br.uff.midiacom.ana.util.enums.NCLDefaultActionRole;
/*     */ import br.uff.midiacom.ana.util.enums.NCLEventAction;
/*     */ import br.uff.midiacom.ana.util.enums.NCLEventType;
/*     */ import br.uff.midiacom.ana.util.exception.XMLException;
/*     */ import org.json.simple.JSONArray;
/*     */ import org.json.simple.JSONObject;
/*     */ 
/*     */ public class JNSLinkInterpreter
/*     */ {
/*     */   private int ConnectorIndex;
/*     */   private int IndiceAssassement;
/*     */   private JNSHeadInterpreter interpretadorHead;
/*     */ 
/*     */   JNSLinkInterpreter(JNSHeadInterpreter interpretadorHead)
/*     */   {
/*  46 */     this.interpretadorHead = interpretadorHead;
/*  47 */     this.ConnectorIndex = 0;
/*  48 */     this.IndiceAssassement = 0;
/*     */   }
/*     */ 
/*     */   NCLLink Interprets(JSONObject jnsLink, Object contexto) throws XMLException {
/*  52 */     NCLLink elo = new NCLLink();
/*  53 */     if (jnsLink.containsKey("id")) {
/*  54 */       elo.setId((String)jnsLink.get("id"));
/*     */     }
/*  56 */     if (jnsLink.containsKey("expression")) {
/*  57 */       interpretsExpression(((String)jnsLink.get("expression")).replace('\'', '"'), elo, contexto);
/*     */     }
/*     */     else {
/*  60 */       if (jnsLink.containsKey("connector")) {
/*  61 */         elo.setXconnector(JNSaNaComplements.FindConnector((String)jnsLink.get("connector"), this.interpretadorHead.getInterpretaConnector().getBase()));
/*     */       }
/*  63 */       if (jnsLink.containsKey("params")) {
/*  64 */         if ((jnsLink.get("params") instanceof JSONArray)) {
/*  65 */           JSONArray jnsParams = (JSONArray)jnsLink.get("params");
/*  66 */           for (int i = 0; i < jnsParams.size(); i++) {
/*  67 */             String[] parametros = JNSjSONComplements.getKeys((JSONObject)jnsParams.get(i));
/*  68 */             for (int j = 0; j < parametros.length; j++)
/*  69 */               elo.addLinkParam(interpretsParameter((JSONObject)jnsParams.get(i), elo, parametros[j]));
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/*  74 */           String[] parametros = JNSjSONComplements.getKeys((JSONObject)jnsLink.get("params"));
/*  75 */           for (int j = 0; j < parametros.length; j++) {
/*  76 */             elo.addLinkParam(interpretsParameter((JSONObject)jnsLink.get("params"), elo, parametros[j]));
/*     */           }
/*     */         }
/*     */       }
/*  80 */       if (jnsLink.containsKey("binds")) {
/*  81 */         JSONArray jnsBinds = (JSONArray)jnsLink.get("binds");
/*  82 */         for (int i = 0; i < jnsBinds.size(); i++) {
/*  83 */           String[] roles = JNSjSONComplements.getKeys((JSONObject)jnsBinds.get(i));
/*  84 */           String role = null;
/*  85 */           for (int j = 0; j < roles.length; j++) {
/*  86 */             if ((!roles[j].equals("params")) && (!roles[j].equals("descriptor")))
/*  87 */               role = roles[j];
/*     */           }
/*  89 */           String obBind = (String)((JSONObject)jnsBinds.get(i)).get(role);
/*  90 */           Object jnsParams = null;
/*  91 */           String descriptor = null;
/*  92 */           if (((JSONObject)jnsBinds.get(i)).containsKey("descriptor")) {
/*  93 */             descriptor = (String)((JSONObject)jnsBinds.get(i)).get("descriptor");
/*     */           }
/*  95 */           if (((JSONObject)jnsBinds.get(i)).containsKey("params")) {
/*  96 */             jnsParams = ((JSONObject)jnsBinds.get(i)).get("params");
/*     */           }
/*  98 */           if (obBind.contains(",")) {
/*  99 */             String[] obBinds = obBind.split(",");
/* 100 */             for (int j = 0; j < obBinds.length; j++)
/* 101 */               interpretsBind(obBinds[j], role, contexto, (NCLCausalConnector)elo.getXconnector(), elo, jnsParams, descriptor);
/*     */           }
/*     */           else {
/* 104 */             interpretsBind(obBind, role, contexto, (NCLCausalConnector)elo.getXconnector(), elo, jnsParams, descriptor);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 109 */     return elo;
/*     */   }
/*     */ 
/*     */   private void interpretsExpression(String expression, NCLLink elo, Object contexto) throws XMLException {
/* 113 */     String expresaoCondicao = expression.substring(0, expression.indexOf("then"));
/* 114 */     String expressaoAcao = expression.substring(expression.indexOf("then") + 4, expression.length());
/* 115 */     NCLCausalConnector nConnector = new NCLCausalConnector("Connector_Padrao" + this.ConnectorIndex++);
/* 116 */     elo.setXconnector(nConnector);
/* 117 */     interpretsCondExpression(expresaoCondicao.trim(), nConnector, contexto, elo);
/* 118 */     interpretsActExpression(expressaoAcao.trim(), nConnector, contexto, elo);
/* 119 */     this.interpretadorHead.getInterpretaConnector().getBase().addCausalConnector(nConnector);
/* 120 */     this.IndiceAssassement = 0;
/*     */   }
/*     */ 
/*     */   private void interpretsCondExpression(String expresaoCondicao, Object pai, Object contexto, NCLLink elo) throws XMLException {
/* 124 */     NCLCondition codicaoRetorno = null;
/* 125 */     NCLCausalConnector conector = null;
/* 126 */     if ((expresaoCondicao.contains(" and ")) || (expresaoCondicao.contains(" or "))) {
/* 127 */       codicaoRetorno = new NCLCompoundCondition();
/* 128 */       String operador = null;
/* 129 */       conector = pegaConector(pai, codicaoRetorno);
/* 130 */       String[] subExpressao = (String[])null;
/* 131 */       if (expresaoCondicao.contains("(")) {
/* 132 */         subExpressao = JNSConnectorInterpreter.getInnerParentesis(expresaoCondicao);
/* 133 */         while ((subExpressao.length == 1) && (subExpressao[0].contains("(")))
/* 134 */           subExpressao = JNSConnectorInterpreter.getInnerParentesis(subExpressao[0]);
/* 135 */         int i = 0;
/* 136 */         if (subExpressao.length > 1)
/* 137 */           i = subExpressao.length - 1;
/* 138 */         if (subExpressao[i].startsWith("and")) {
/* 139 */           operador = "and";
/* 140 */           ((NCLCompoundCondition)codicaoRetorno).setOperator(NCLConditionOperator.AND);
/*     */         }
/* 142 */         else if (subExpressao[i].startsWith("or")) {
/* 143 */           operador = "or";
/* 144 */           ((NCLCompoundCondition)codicaoRetorno).setOperator(NCLConditionOperator.OR);
/*     */         }
/* 146 */         if (subExpressao[i].contains("delay")) {
/* 147 */           String delay = subExpressao[i].substring(subExpressao[i].indexOf('=') + 1, subExpressao[i].length()).trim();
/* 148 */           ((NCLCompoundCondition)codicaoRetorno).setDelay(delay.replaceAll("'", ""));
/*     */         }
/* 150 */         i = 0;
/* 151 */         while (operador == null) {
/* 152 */           if (subExpressao[i].startsWith("and")) {
/* 153 */             operador = "and";
/* 154 */             ((NCLCompoundCondition)codicaoRetorno).setOperator(NCLConditionOperator.AND);
/*     */           }
/* 156 */           else if (subExpressao[i].startsWith("or")) {
/* 157 */             operador = "or";
/* 158 */             ((NCLCompoundCondition)codicaoRetorno).setOperator(NCLConditionOperator.OR);
/*     */           }
/* 160 */           i++;
/*     */         }
/*     */       }
/*     */       else {
/* 164 */         if (expresaoCondicao.contains("and")) {
/* 165 */           operador = "and";
/* 166 */           ((NCLCompoundCondition)codicaoRetorno).setOperator(NCLConditionOperator.AND);
/*     */         }
/* 168 */         else if (expresaoCondicao.contains("or")) {
/* 169 */           operador = "or";
/* 170 */           ((NCLCompoundCondition)codicaoRetorno).setOperator(NCLConditionOperator.OR);
/*     */         }
/* 172 */         subExpressao = expresaoCondicao.split(operador);
/*     */       }
/* 174 */       for (int i = 0; i < subExpressao.length; i++) {
/* 175 */         if (subExpressao[i].startsWith(operador)) {
/* 176 */           if (!subExpressao[i].equals(operador)) {
/* 177 */             String[] sub = subExpressao[i].split(operador);
/* 178 */             for (int j = 0; j < sub.length; j++) {
/* 179 */               if (sub[j].trim().length() > 0)
/* 180 */                 interpretsCondExpression(sub[j].trim(), codicaoRetorno, contexto, elo);
/*     */             }
/*     */           }
/*     */         }
/*     */         else
/* 185 */           interpretsCondExpression(subExpressao[i].trim(), codicaoRetorno, contexto, elo);
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 190 */       String roleString = "";
/* 191 */       String expressaoLimpa = expresaoCondicao.trim().replace('\t', ' ');
/* 192 */       roleString = expressaoLimpa.trim().substring(0, expressaoLimpa.indexOf(' '));
/* 193 */       NCLAssessmentStatement assessement = new NCLAssessmentStatement();
/* 194 */       String comparador = JNSaNaComplements.InterpretaComparador(expresaoCondicao, assessement);
/* 195 */       if (comparador != null) {
/* 196 */         conector = pegaConector(pai, assessement);
/* 197 */         codicaoRetorno = (NCLCompoundCondition)pai;
/* 198 */         String[] valValor = (String[])null;
/* 199 */         valValor = expresaoCondicao.split(comparador);
/* 200 */         for (int indice = 0; indice < valValor.length; indice++) {
/* 201 */           int end = valValor[indice].trim().indexOf(" ");
/* 202 */           if (end < 0)
/* 203 */             end = valValor[indice].trim().length();
/* 204 */           String role = valValor[indice].trim().substring(0, end);
/* 205 */           NCLInterface inter = null;
/* 206 */           if (role.contains(".")) {
/* 207 */             String[] idInterfaceT = new String[2];
/* 208 */             int ini = role.indexOf('.');
/* 209 */             idInterfaceT[0] = role.substring(0, ini);
/* 210 */             idInterfaceT[1] = role.substring(ini + 1);
/* 211 */             while (!(contexto instanceof NCLBody)) {
/* 212 */               contexto = ((NCLElement)contexto).getParent();
/*     */             }
/* 214 */             NCLNode noT = JNSaNaComplements.FindNode(idInterfaceT[0], contexto);
/* 215 */             inter = JNSaNaComplements.FindInterface(idInterfaceT[1], noT);
/*     */           }
/* 217 */           NCLAttributeAssessment atributeAss = new NCLAttributeAssessment();
/* 218 */           atributeAss.setRole("defaultRole" + this.IndiceAssassement);
/* 219 */           if ((inter instanceof NCLProperty)) {
/* 220 */             atributeAss.setEventType(NCLEventType.ATTRIBUTION);
/* 221 */             atributeAss.setAttributeType(NCLAttributeType.NODE_PROPERTY);
/* 222 */             assessement.addAttributeAssessment(atributeAss);
/* 223 */             interpretsBind(role, "defaultRole" + this.IndiceAssassement++, contexto, conector, elo, null, null);
/*     */           }
/* 225 */           else if (valValor[indice].contains(" with ")) {
/* 226 */             atributeAss.setRole(role);
/* 227 */             String[] parametrosExtras = valValor[indice].substring(valValor[indice].indexOf(" with ") + 5, valValor[indice].length()).split(",");
/* 228 */             for (int i = 0; i < parametrosExtras.length; i++) {
/* 229 */               if (parametrosExtras[i].contains("eventtype")) {
/* 230 */                 String tipo = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"") + 1, parametrosExtras[i].length());
/* 231 */                 tipo = tipo.substring(0, tipo.indexOf('"'));
/* 232 */                 atributeAss.setEventType(NCLEventType.getEnumType(tipo));
/*     */               }
/* 234 */               else if (parametrosExtras[i].contains("attributetype")) {
/* 235 */                 String tipo = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"") + 1, parametrosExtras[i].length());
/* 236 */                 tipo = tipo.substring(0, tipo.indexOf('"'));
/* 237 */                 atributeAss.setAttributeType(NCLAttributeType.getEnumType(tipo));
/*     */               }
/* 239 */               else if (parametrosExtras[i].contains("key")) {
/* 240 */                 String key = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"") + 1, parametrosExtras[i].length());
/* 241 */                 key = key.substring(0, key.indexOf('"'));
/* 242 */                 atributeAss.setKey(key);
/*     */               }
/* 244 */               else if (parametrosExtras[i].contains("offset")) {
/* 245 */                 String offset = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"") + 1, parametrosExtras[i].length());
/* 246 */                 offset = offset.substring(0, offset.indexOf('"'));
/* 247 */                 atributeAss.setOffset(offset);
/*     */               }
/*     */             }
/* 250 */             assessement.addAttributeAssessment(atributeAss);
/* 251 */             interpretsBind(role, "defaultRole" + this.IndiceAssassement++, contexto, conector, elo, null, null);
/*     */           }
/*     */           else {
/* 254 */             assessement.setValueAssessment(role.replace("\"", ""));
/*     */           }
/*     */         }
/*     */       }
/*     */       else {
/* 259 */         codicaoRetorno = new NCLSimpleCondition();
/* 260 */         conector = pegaConector(pai, codicaoRetorno);
/* 261 */         ((NCLSimpleCondition)codicaoRetorno).setRole(roleString);
/* 262 */         String targets = expresaoCondicao.substring(roleString.length() + 1, expresaoCondicao.length());
/* 263 */         int ind = targets.indexOf("with");
/* 264 */         String[] idInterface = targets.split(",");
/* 265 */         ((NCLSimpleCondition)codicaoRetorno).setMax(Integer.valueOf(idInterface.length));
/* 266 */         ((NCLSimpleCondition)codicaoRetorno).setQualifier(NCLConditionOperator.AND);
/* 267 */         if (ind > 0) {
/* 268 */           targets = targets.substring(0, ind);
/* 269 */           idInterface = targets.split(",");
/* 270 */           ((NCLSimpleCondition)codicaoRetorno).setMax(Integer.valueOf(idInterface.length));
/* 271 */           String[] parametrosExtras = expresaoCondicao.substring(expresaoCondicao.indexOf(" with ") + 5, expresaoCondicao.length()).split(",");
/* 272 */           codicaoRetorno = JNSaNaComplements.adicionaParametrosExtas(codicaoRetorno, parametrosExtras);
/*     */         }
/* 274 */         for (int i = 0; i < idInterface.length; i++)
/* 275 */           interpretsBind(idInterface[i], roleString, contexto, conector, elo, null, null);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private NCLCausalConnector pegaConector(Object pai, Object chamador) throws XMLException
/*     */   {
/* 282 */     NCLCausalConnector conector = null;
/* 283 */     if ((pai instanceof NCLCausalConnector)) {
/* 284 */       conector = (NCLCausalConnector)pai;
/* 285 */       if ((chamador instanceof NCLCondition))
/* 286 */         conector.setCondition((NCLCondition)chamador);
/*     */       else
/* 288 */         conector.setAction((NCLAction)chamador);
/*     */     }
/* 290 */     else if ((pai instanceof NCLCompoundCondition)) {
/* 291 */       NCLCompoundCondition condicaoAuxiliar = (NCLCompoundCondition)pai;
/* 292 */       if ((chamador instanceof NCLCondition))
/* 293 */         condicaoAuxiliar.addCondition((NCLCondition)chamador);
/*     */       else
/* 295 */         condicaoAuxiliar.addStatement((NCLStatement)chamador);
/* 296 */       while (!(condicaoAuxiliar.getParent() instanceof NCLCausalConnector)) {
/* 297 */         condicaoAuxiliar = (NCLCompoundCondition)condicaoAuxiliar.getParent();
/*     */       }
/* 299 */       conector = (NCLCausalConnector)condicaoAuxiliar.getParent();
/*     */     }
/* 301 */     else if ((pai instanceof NCLCompoundAction)) {
/* 302 */       NCLCompoundAction acaoAuxiliar = (NCLCompoundAction)pai;
/* 303 */       acaoAuxiliar.addAction((NCLAction)chamador);
/* 304 */       while (!(acaoAuxiliar.getParent() instanceof NCLCausalConnector)) {
/* 305 */         acaoAuxiliar = (NCLCompoundAction)acaoAuxiliar.getParent();
/*     */       }
/* 307 */       conector = (NCLCausalConnector)acaoAuxiliar.getParent();
/*     */     }
/* 309 */     return conector;
/*     */   }
/*     */ 
/*     */   private NCLAction interpretsActExpression(String expresaoAcao, Object pai, Object contexto, NCLLink elo) throws XMLException {
/* 313 */     NCLAction acao = null;
/* 314 */     NCLCausalConnector connector = null;
/* 315 */     if ((expresaoAcao.contains(";")) || (expresaoAcao.contains("||"))) {
/* 316 */       acao = new NCLCompoundAction();
/* 317 */       connector = pegaConector(pai, acao);
/* 318 */       String operador = null;
/* 319 */       String[] subExpressao = (String[])null;
/* 320 */       if (expresaoAcao.contains("(")) {
/* 321 */         subExpressao = JNSConnectorInterpreter.getInnerParentesis(expresaoAcao);
/* 322 */         while (subExpressao.length == 1)
/* 323 */           subExpressao = JNSConnectorInterpreter.getInnerParentesis(subExpressao[0]);
/* 324 */         int i = subExpressao.length - 1;
/* 325 */         if (subExpressao[i].startsWith(";")) {
/* 326 */           operador = ";";
/* 327 */           ((NCLCompoundAction)acao).setOperator(NCLActionOperator.SEQ);
/*     */         }
/* 329 */         else if (subExpressao[i].startsWith("||")) {
/* 330 */           operador = "\\|\\|";
/* 331 */           ((NCLCompoundAction)acao).setOperator(NCLActionOperator.PAR);
/*     */         }
/* 333 */         if (subExpressao[i].contains("delay")) {
/* 334 */           String delay = subExpressao[i].substring(subExpressao[i].indexOf('=') + 1, subExpressao[i].length()).trim();
/* 335 */           ((NCLCompoundAction)acao).setDelay(delay.replaceAll("'", ""));
/*     */         }
/* 337 */         i = 0;
/* 338 */         while (operador == null) {
/* 339 */           if (subExpressao[i].startsWith(";")) {
/* 340 */             operador = ";";
/* 341 */             ((NCLCompoundAction)acao).setOperator(NCLActionOperator.SEQ);
/*     */           }
/* 343 */           else if (subExpressao[i].startsWith("||")) {
/* 344 */             operador = "\\|\\|";
/* 345 */             ((NCLCompoundAction)acao).setOperator(NCLActionOperator.PAR);
/*     */           }
/* 347 */           i++;
/*     */         }
/*     */       }
/*     */       else {
/* 351 */         if (expresaoAcao.contains(";")) {
/* 352 */           operador = ";";
/* 353 */           ((NCLCompoundAction)acao).setOperator(NCLActionOperator.SEQ);
/*     */         }
/* 355 */         else if (expresaoAcao.contains("||")) {
/* 356 */           operador = "\\|\\|";
/* 357 */           ((NCLCompoundAction)acao).setOperator(NCLActionOperator.PAR);
/*     */         }
/* 359 */         subExpressao = expresaoAcao.split(operador);
/*     */       }
/* 361 */       String operator = operador;
/* 362 */       if (operador.equals("\\|\\|"))
/* 363 */         operator = "||";
/* 364 */       for (int i = 0; i < subExpressao.length; i++) {
/* 365 */         if (subExpressao[i].startsWith(operator)) {
/* 366 */           if (!subExpressao[i].equals(operator)) {
/* 367 */             String[] sub = subExpressao[i].split(operador);
/* 368 */             for (int j = 0; j < sub.length; j++)
/* 369 */               if (sub[j].trim().length() > 0)
/* 370 */                 interpretsActExpression(sub[j].trim(), acao, contexto, elo);
/*     */           }
/*     */         }
/*     */         else
/* 374 */           interpretsActExpression(subExpressao[i].trim(), acao, contexto, elo);
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 379 */       acao = new NCLSimpleAction();
/* 380 */       connector = pegaConector(pai, acao);
/* 381 */       if (expresaoAcao.toLowerCase().startsWith("start")) {
/* 382 */         ((NCLSimpleAction)acao).setRole(NCLDefaultActionRole.START);
/*     */       }
/* 384 */       else if (expresaoAcao.toLowerCase().startsWith("stop")) {
/* 385 */         ((NCLSimpleAction)acao).setRole(NCLDefaultActionRole.STOP);
/*     */       }
/* 387 */       else if (expresaoAcao.toLowerCase().startsWith("abort")) {
/* 388 */         ((NCLSimpleAction)acao).setRole(NCLDefaultActionRole.ABORT);
/*     */       }
/* 390 */       else if (expresaoAcao.toLowerCase().startsWith("pause")) {
/* 391 */         ((NCLSimpleAction)acao).setRole(NCLDefaultActionRole.PAUSE);
/*     */       }
/* 393 */       else if (expresaoAcao.toLowerCase().startsWith("resume")) {
/* 394 */         ((NCLSimpleAction)acao).setRole(NCLDefaultActionRole.RESUME);
/*     */       }
/* 396 */       else if (expresaoAcao.toLowerCase().startsWith("set")) {
/* 397 */         ((NCLSimpleAction)acao).setRole(NCLDefaultActionRole.SET);
/* 398 */         int indiceSet = expresaoAcao.indexOf("set") + 4;
/* 399 */         String valor = expresaoAcao.substring(indiceSet, expresaoAcao.indexOf(' ', indiceSet));
/* 400 */         ((NCLSimpleAction)acao).setValue(valor);
/*     */       }
/*     */       else {
/* 403 */         ((NCLSimpleAction)acao).setRole(expresaoAcao.trim().substring(0, expresaoAcao.trim().indexOf(" ")));
/*     */       }
/* 405 */       String targets = expresaoAcao.substring(expresaoAcao.indexOf(((NCLSimpleAction)acao).getRole().toString()) + ((NCLSimpleAction)acao).getRole().toString().length(), expresaoAcao.length());
/* 406 */       int ind = targets.indexOf("with");
/* 407 */       String[] idInterface = targets.split(",");
/* 408 */       if (idInterface.length > 1) {
/* 409 */         ((NCLSimpleAction)acao).setMax(Integer.valueOf(idInterface.length));
/* 410 */         ((NCLSimpleAction)acao).setQualifier(NCLActionOperator.PAR);
/*     */       }
/* 412 */       if (ind > 0) {
/* 413 */         targets = targets.substring(0, ind);
/* 414 */         idInterface = targets.split(",");
/* 415 */         ((NCLSimpleAction)acao).setMax(Integer.valueOf(idInterface.length));
/* 416 */         String[] parametrosExtras = expresaoAcao.substring(expresaoAcao.indexOf(" with ") + 5, expresaoAcao.length()).split(" , ");
/* 417 */         for (int i = 0; i < parametrosExtras.length; i++) {
/* 418 */           parametrosExtras[i] = parametrosExtras[i].trim();
/* 419 */           if (parametrosExtras[i].contains("delay")) {
/* 420 */             parametrosExtras[i] = parametrosExtras[i].trim();
/* 421 */             String delay = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"") + 1, parametrosExtras[i].length()).trim();
/* 422 */             delay = delay.substring(0, delay.indexOf('"'));
/* 423 */             acao.setDelay(delay.replace("s", ""));
/*     */           }
/* 425 */           else if (parametrosExtras[i].contains("eventtype")) {
/* 426 */             String tipo = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"") + 1, parametrosExtras[i].length()).trim();
/* 427 */             tipo = tipo.substring(0, tipo.indexOf('"'));
/* 428 */             ((NCLSimpleAction)acao).setEventType(NCLEventType.valueOf(tipo.toUpperCase()));
/*     */           }
/* 430 */           else if (parametrosExtras[i].contains("actiontype")) {
/* 431 */             String tipo = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"") + 1, parametrosExtras[i].length()).trim();
/* 432 */             tipo = tipo.substring(0, tipo.indexOf('"'));
/* 433 */             ((NCLSimpleAction)acao).setActionType(NCLEventAction.getEnumType(tipo.toUpperCase()));
/*     */           }
/* 435 */           else if (parametrosExtras[i].contains("value")) {
/* 436 */             String value = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"") + 1, parametrosExtras[i].length()).trim();
/* 437 */             value = value.substring(0, value.indexOf('"'));
/* 438 */             ((NCLSimpleAction)acao).setValue(value);
/*     */           }
/* 440 */           else if (parametrosExtras[i].contains("min")) {
/* 441 */             String min = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"") + 1, parametrosExtras[i].length()).trim();
/* 442 */             min = min.substring(0, min.indexOf('"'));
/* 443 */             ((NCLSimpleAction)acao).setMin(Integer.valueOf(Integer.parseInt(min)));
/*     */           }
/* 445 */           else if (parametrosExtras[i].contains("max")) {
/* 446 */             String max = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"") + 1, parametrosExtras[i].length()).trim();
/* 447 */             max = max.substring(0, max.indexOf('"'));
/* 448 */             ((NCLSimpleAction)acao).setMax(max);
/*     */           }
/* 450 */           else if (parametrosExtras[i].contains("qualifier")) {
/* 451 */             String qual = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"") + 1, parametrosExtras[i].length()).trim();
/* 452 */             qual = qual.substring(0, qual.indexOf('"'));
/* 453 */             ((NCLSimpleAction)acao).setQualifier(NCLActionOperator.valueOf(qual.toUpperCase()));
/*     */           }
/* 455 */           else if (parametrosExtras[i].contains("repeat")) {
/* 456 */             String repeat = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"") + 1, parametrosExtras[i].length()).trim();
/* 457 */             repeat = repeat.substring(0, repeat.indexOf('"'));
/* 458 */             ((NCLSimpleAction)acao).setRepeat(repeat);
/*     */           }
/* 460 */           else if (parametrosExtras[i].contains("repeatdelay")) {
/* 461 */             String delay = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"") + 1, parametrosExtras[i].length()).trim();
/* 462 */             delay = delay.substring(0, delay.indexOf('"'));
/* 463 */             ((NCLSimpleAction)acao).setRepeatDelay(delay.replace("s", ""));
/*     */           }
/* 465 */           else if (parametrosExtras[i].contains("duration")) {
/* 466 */             String duration = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"") + 1, parametrosExtras[i].length()).trim();
/* 467 */             duration = duration.substring(0, duration.indexOf('"'));
/* 468 */             ((NCLSimpleAction)acao).setDuration(Double.valueOf(Double.parseDouble(duration)));
/*     */           }
/* 470 */           else if (parametrosExtras[i].contains("by")) {
/* 471 */             String by = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"") + 1, parametrosExtras[i].length()).trim();
/* 472 */             by = by.substring(0, by.indexOf('"'));
/* 473 */             ((NCLSimpleAction)acao).setBy(by);
/*     */           }
/*     */         }
/*     */       }
/* 477 */       for (int i = 0; i < idInterface.length; i++) {
/* 478 */         interpretsBind(idInterface[i], ((NCLSimpleAction)acao).getRole().toString(), contexto, connector, elo, null, null);
/*     */       }
/*     */     }
/* 481 */     return acao;
/*     */   }
/*     */ 
/*     */   private NCLLinkParam interpretsParameter(JSONObject parameter, NCLLink elo, String nome) throws XMLException {
/* 485 */     NCLLinkParam parametro = new NCLLinkParam();
/* 486 */     parametro.setName((NCLConnectorParam)((NCLCausalConnector)elo.getXconnector()).getConnectorParams().get(nome));
/* 487 */     parametro.setValue((String)parameter.get(nome));
/* 488 */     return parametro;
/*     */   }
/*     */ 
/*     */   private NCLBind interpretsBind(String componente, String role, Object contexto, NCLCausalConnector conector, NCLLink elo, Object jnsParams, String descriptor) throws XMLException {
/* 492 */     NCLBind bind = new NCLBind();
/* 493 */     elo.addBind(bind);
/* 494 */     if (jnsParams != null) {
/* 495 */       if ((jnsParams instanceof JSONArray)) {
/* 496 */         JSONArray jnsParamsA = (JSONArray)jnsParams;
/* 497 */         for (int i = 0; i < jnsParamsA.size(); i++) {
/* 498 */           String[] parametros = JNSjSONComplements.getKeys((JSONObject)jnsParamsA.get(i));
/* 499 */           for (int j = 0; j < parametros.length; j++)
/* 500 */             bind.addBindParam(interpretsBindParameter((JSONObject)jnsParamsA.get(i), conector, parametros[j]));
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 505 */         String[] paramaetros = JNSjSONComplements.getKeys((JSONObject)jnsParams);
/* 506 */         for (int i = 0; i < paramaetros.length; i++) {
/* 507 */           bind.addBindParam(interpretsBindParameter((JSONObject)jnsParams, conector, paramaetros[i]));
/*     */         }
/*     */       }
/*     */     }
/* 511 */     if (descriptor != null) {
/* 512 */       bind.setDescriptor(this.interpretadorHead.getInterpretadorDescritor().getBase().findDescriptor(descriptor));
/*     */     }
/* 514 */     if (componente.contains(".")) {
/* 515 */       String[] componentes = new String[2];
/* 516 */       int ini = componente.indexOf('.');
/* 517 */       componentes[0] = componente.substring(0, ini);
/* 518 */       componentes[1] = componente.substring(ini + 1);
/* 519 */       while (!(contexto instanceof NCLBody)) {
/* 520 */         contexto = ((NCLElement)contexto).getParent();
/*     */       }
/* 522 */       bind.setComponent(JNSaNaComplements.FindNode(componentes[0].trim(), contexto));
/* 523 */       bind.setInterface(JNSaNaComplements.FindInterface(componentes[1].trim(), bind.getComponent()));
/*     */     }
/*     */     else {
/* 526 */       bind.setComponent(JNSaNaComplements.FindNode(componente.trim(), contexto));
/*     */     }
/* 528 */     bind.setRole(JNSaNaComplements.FindRole(role, conector));
/* 529 */     return bind;
/*     */   }
/*     */ 
/*     */   private NCLBindParam interpretsBindParameter(JSONObject parameter, NCLCausalConnector conector, String nome) throws XMLException {
/* 533 */     NCLBindParam parametro = new NCLBindParam();
/* 534 */     parametro.setName((NCLConnectorParam)conector.getConnectorParams().get(nome));
/* 535 */     parametro.setValue((String)parameter.get(nome));
/* 536 */     return parametro;
/*     */   }
/*     */ }

/* Location:           C:\Users\Convidado\Desktop\pacoteCompleto\jnsTranslator.jar
 * Qualified Name:     JNSLinkInterpreter
 * JD-Core Version:    0.6.2
 */