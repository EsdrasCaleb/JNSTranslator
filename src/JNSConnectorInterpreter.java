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
/*     */ import br.uff.midiacom.ana.util.enums.NCLActionOperator;
/*     */ import br.uff.midiacom.ana.util.enums.NCLAttributeType;
/*     */ import br.uff.midiacom.ana.util.enums.NCLConditionOperator;
/*     */ import br.uff.midiacom.ana.util.enums.NCLDefaultActionRole;
/*     */ import br.uff.midiacom.ana.util.enums.NCLDefaultConditionRole;
/*     */ import br.uff.midiacom.ana.util.enums.NCLEventAction;
/*     */ import br.uff.midiacom.ana.util.enums.NCLEventType;
/*     */ import br.uff.midiacom.ana.util.exception.XMLException;
/*     */ import java.util.Vector;
/*     */ import org.json.simple.JSONArray;
/*     */ import org.json.simple.JSONObject;
/*     */ 
/*     */ public class JNSConnectorInterpreter
/*     */ {
/*     */   private NCLConnectorBase Base;
/*     */ 
/*     */   JNSConnectorInterpreter()
/*     */     throws XMLException
/*     */   {
/*  35 */     this.Base = new NCLConnectorBase();
/*     */   }
/*     */ 
/*     */   NCLConnectorBase getBase() {
/*  39 */     return this.Base;
/*     */   }
/*     */ 
/*     */   void Add(JSONObject jnsConnector) throws XMLException {
/*  43 */     NCLCausalConnector connector = interConnector(jnsConnector);
/*  44 */     this.Base.addCausalConnector(connector);
/*     */   }
/*     */ 
/*     */   private NCLCausalConnector interConnector(JSONObject jConnector) throws XMLException {
/*  48 */     NCLCausalConnector nConnector = new NCLCausalConnector((String)jConnector.get("id"));
/*  49 */     String expressao = ((String)jConnector.get("expression")).replace('\'', '"');
/*  50 */     if (jConnector.containsKey("params")) {
/*  51 */       JSONArray jsonParams = (JSONArray)jConnector.get("params");
/*  52 */       int i = 0;
/*  53 */       for (i = 0; i < jsonParams.size(); i++) {
/*  54 */         nConnector.addConnectorParam(new NCLConnectorParam(((String)jsonParams.get(i)).trim()));
/*     */       }
/*     */     }
/*  57 */     String expresaoCondicao = expressao.substring(0, expressao.indexOf("then"));
/*  58 */     String expressaoAcao = expressao.substring(expressao.indexOf("then") + 4, expressao.length());
/*  59 */     interCondition(expresaoCondicao.trim(), nConnector, null);
/*  60 */     interAction(expressaoAcao.trim(), nConnector);
/*  61 */     return nConnector;
/*     */   }
/*     */ 
/*     */   private NCLCondition interCondition(String simpleCondExpression, NCLCausalConnector conectorPai, NCLCompoundCondition condicaoPai) throws XMLException {
/*  65 */     NCLCondition condicao = null;
/*  66 */     if ((simpleCondExpression.toLowerCase().contains("and")) || (simpleCondExpression.toLowerCase().contains("or"))) {
/*  67 */       condicao = new NCLCompoundCondition();
/*  68 */       if (condicaoPai != null)
/*  69 */         condicaoPai.addCondition(condicao);
/*     */       else
/*  71 */         conectorPai.setCondition(condicao);
/*  72 */       String operador = null;
/*  73 */       String[] subExpressao = (String[])null;
/*  74 */       if (simpleCondExpression.contains("(")) {
/*  75 */         subExpressao = getInnerParentesis(simpleCondExpression);
/*  76 */         while (subExpressao.length == 1)
/*  77 */           subExpressao = getInnerParentesis(subExpressao[0]);
/*  78 */         int i = subExpressao.length - 1;
/*  79 */         if (subExpressao[i].startsWith("and")) {
/*  80 */           operador = "and";
/*  81 */           ((NCLCompoundCondition)condicao).setOperator(NCLConditionOperator.AND);
/*     */         }
/*  83 */         else if (subExpressao[i].startsWith("or")) {
/*  84 */           operador = "or";
/*  85 */           ((NCLCompoundCondition)condicao).setOperator(NCLConditionOperator.OR);
/*     */         }
/*  87 */         if (subExpressao[i].contains("delay")) {
/*  88 */           String delay = subExpressao[i].substring(subExpressao[i].indexOf('=') + 1, subExpressao[i].length()).trim();
/*  89 */           ((NCLCompoundCondition)condicao).setDelay(delay.replaceAll("'", ""));
/*     */         }
/*  91 */         i = 0;
/*  92 */         while (operador == null) {
/*  93 */           if (subExpressao[i].startsWith("and")) {
/*  94 */             operador = "and";
/*  95 */             ((NCLCompoundCondition)condicao).setOperator(NCLConditionOperator.AND);
/*     */           }
/*  97 */           else if (subExpressao[i].startsWith("or")) {
/*  98 */             operador = "or";
/*  99 */             ((NCLCompoundCondition)condicao).setOperator(NCLConditionOperator.OR);
/*     */           }
/* 101 */           i++;
/*     */         }
/*     */       }
/*     */       else {
/* 105 */         if (simpleCondExpression.contains("and")) {
/* 106 */           operador = "and";
/* 107 */           ((NCLCompoundCondition)condicao).setOperator(NCLConditionOperator.AND);
/*     */         }
/* 109 */         else if (simpleCondExpression.contains("or")) {
/* 110 */           operador = "or";
/* 111 */           ((NCLCompoundCondition)condicao).setOperator(NCLConditionOperator.OR);
/*     */         }
/* 113 */         subExpressao = simpleCondExpression.split(operador);
/*     */       }
/* 115 */       for (int i = 0; i < subExpressao.length; i++) {
/* 116 */         if (subExpressao[i].startsWith(operador)) {
/* 117 */           if (!subExpressao[i].equals(operador)) {
/* 118 */             String[] sub = subExpressao[i].split(operador);
/* 119 */             for (int j = 0; j < sub.length; j++) {
/* 120 */               if (sub[j].trim().length() > 0) {
/* 121 */                 NCLAssessmentStatement assessement = new NCLAssessmentStatement();
/* 122 */                 String comparador = JNSaNaComplements.InterpretaComparador(sub[j], assessement);
/* 123 */                 if (comparador != null)
/* 124 */                   interAssessmentStatement(sub[j], (NCLCompoundCondition)condicao);
/*     */                 else
/* 126 */                   interSimpleCondition(sub[j], conectorPai, condicao);
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */         else {
/* 132 */           interCondition(subExpressao[i], conectorPai, (NCLCompoundCondition)condicao);
/*     */         }
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 138 */       NCLAssessmentStatement assessement = new NCLAssessmentStatement();
/* 139 */       String comparador = JNSaNaComplements.InterpretaComparador(simpleCondExpression, assessement);
/* 140 */       if (comparador != null) {
/* 141 */         interAssessmentStatement(simpleCondExpression, condicaoPai);
/* 142 */         return null;
/*     */       }
/*     */ 
/* 145 */       condicao = interSimpleCondition(simpleCondExpression.trim(), conectorPai, condicaoPai);
/*     */     }
/*     */ 
/* 149 */     return condicao;
/*     */   }
/*     */ 
/*     */   private NCLCondition interSimpleCondition(String simpleCondExpression, NCLCausalConnector connectorPai, NCLCondition condicaoPai) throws XMLException
/*     */   {
/* 154 */     NCLSimpleCondition condicao = new NCLSimpleCondition();
/* 155 */     if (condicaoPai != null)
/* 156 */       ((NCLCompoundCondition)condicaoPai).addCondition(condicao);
/*     */     else
/* 158 */       connectorPai.setCondition(condicao);
/* 159 */     if (simpleCondExpression.toLowerCase().contains(" with ")) {
/* 160 */       String[] parametrosExtras = simpleCondExpression.substring(simpleCondExpression.indexOf("with ") + 4, simpleCondExpression.length()).split(",");
/* 161 */       condicao = (NCLSimpleCondition)JNSaNaComplements.adicionaParametrosExtas(condicao, parametrosExtras);
/*     */     }
/*     */ 
/* 164 */     if (simpleCondExpression.toLowerCase().startsWith("onbegin"))
/*     */     {
/* 167 */       if (simpleCondExpression.toLowerCase().startsWith("onbeginatribuition")) {
/* 168 */         condicao.setRole(NCLDefaultConditionRole.ONBEGINATTRIBUTION);
/*     */       }
/*     */       else {
/* 171 */         condicao.setRole(NCLDefaultConditionRole.ONBEGIN);
/*     */       }
/*     */     }
/* 174 */     else if (simpleCondExpression.toLowerCase().startsWith("onend")) {
/* 175 */       if (simpleCondExpression.toLowerCase().startsWith("onendattribution")) {
/* 176 */         condicao.setRole(NCLDefaultConditionRole.ONENDATTRIBUTION);
/*     */       }
/*     */       else {
/* 179 */         condicao.setRole(NCLDefaultConditionRole.ONEND);
/*     */       }
/*     */     }
/* 182 */     else if (simpleCondExpression.toLowerCase().startsWith("onabort")) {
/* 183 */       condicao.setRole(NCLDefaultConditionRole.ONABORT);
/*     */     }
/* 185 */     else if (simpleCondExpression.toLowerCase().startsWith("onresume")) {
/* 186 */       condicao.setRole(NCLDefaultConditionRole.ONRESUME);
/*     */     }
/* 188 */     else if (simpleCondExpression.toLowerCase().startsWith("onselection")) {
/* 189 */       condicao.setRole(NCLDefaultConditionRole.ONSELECTION);
/*     */     }
/*     */     else {
/* 192 */       condicao.setRole(simpleCondExpression.substring(0, simpleCondExpression.indexOf("with")).trim());
/*     */     }
/*     */ 
/* 195 */     return condicao;
/*     */   }
/*     */ 
/*     */   private void interAssessmentStatement(String asessemtExpr, NCLCompoundCondition condicaoPai) throws XMLException {
/* 199 */     NCLAssessmentStatement assessement = new NCLAssessmentStatement();
/* 200 */     condicaoPai.addStatement(assessement);
/* 201 */     String[] valValor = (String[])null;
/* 202 */     String comparador = null;
/* 203 */     comparador = JNSaNaComplements.InterpretaComparador(asessemtExpr, assessement);
/* 204 */     valValor = asessemtExpr.split(comparador);
/* 205 */     for (int indice = 0; indice < valValor.length; indice++) {
/* 206 */       int end = valValor[indice].trim().indexOf(" ");
/* 207 */       if (indice > 0)
/* 208 */         end = valValor[indice].trim().length();
/* 209 */       String role = valValor[indice].trim().substring(0, end);
/* 210 */       if (valValor[indice].toLowerCase().contains(" with ")) {
/* 211 */         NCLAttributeAssessment atributeAss = new NCLAttributeAssessment();
/* 212 */         atributeAss.setRole(role);
/* 213 */         String[] parametrosExtras = valValor[indice].substring(valValor[indice].indexOf(" with ") + 5, valValor[indice].length()).split(",");
/* 214 */         for (int i = 0; i < parametrosExtras.length; i++) {
/* 215 */           if (parametrosExtras[i].toLowerCase().contains("eventtype")) {
/* 216 */             String tipo = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"") + 1, parametrosExtras[i].length());
/* 217 */             tipo = tipo.substring(0, tipo.indexOf('"'));
/* 218 */             atributeAss.setEventType(NCLEventType.valueOf(tipo.toUpperCase()));
/*     */           }
/* 220 */           else if (parametrosExtras[i].toLowerCase().contains("attributetype")) {
/* 221 */             String tipo = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"") + 1, parametrosExtras[i].length());
/* 222 */             tipo = tipo.substring(0, tipo.indexOf('"'));
/* 223 */             atributeAss.setAttributeType(NCLAttributeType.getEnumType(tipo));
/*     */           }
/* 225 */           else if (parametrosExtras[i].toLowerCase().contains("key")) {
/* 226 */             String key = parametrosExtras[i].substring(parametrosExtras[i].indexOf("=") + 1, parametrosExtras[i].length());
/* 227 */             key = key.replace("\"", "");
/* 228 */             atributeAss.setKey(key);
/*     */           }
/* 230 */           else if (parametrosExtras[i].toLowerCase().contains("offset")) {
/* 231 */             String offset = parametrosExtras[i].substring(parametrosExtras[i].indexOf("=") + 1, parametrosExtras[i].length());
/* 232 */             offset = offset.replace("\"", "");
/* 233 */             atributeAss.setOffset(offset);
/*     */           }
/*     */         }
/* 236 */         assessement.addAttributeAssessment(atributeAss);
/*     */       }
/*     */       else {
/* 239 */         assessement.setValueAssessment(role.replace("\"", ""));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/* 244 */   private NCLAction interAction(String simpleActExpression, NCLCausalConnector connectorPai) throws XMLException { return interAction(simpleActExpression, connectorPai, null); }
/*     */ 
/*     */   private NCLAction interAction(String simpleActExpression, NCLCausalConnector connectorPai, NCLCompoundAction acaoPai) throws XMLException {
/* 247 */     NCLAction acao = null;
/* 248 */     if ((simpleActExpression.contains(";")) || (simpleActExpression.contains("||"))) {
/* 249 */       acao = new NCLCompoundAction();
/* 250 */       if (acaoPai == null)
/* 251 */         connectorPai.setAction(acao);
/*     */       else
/* 253 */         acaoPai.addAction(acao);
/* 254 */       String operador = null;
/* 255 */       String[] subExpressao = (String[])null;
/* 256 */       if (simpleActExpression.contains("(")) {
/* 257 */         subExpressao = getInnerParentesis(simpleActExpression);
/* 258 */         while (subExpressao.length == 1)
/* 259 */           subExpressao = getInnerParentesis(subExpressao[0]);
/* 260 */         int i = subExpressao.length - 1;
/* 261 */         if (subExpressao[i].startsWith(";")) {
/* 262 */           operador = ";";
/* 263 */           ((NCLCompoundAction)acao).setOperator(NCLActionOperator.SEQ);
/*     */         }
/* 265 */         else if (subExpressao[i].startsWith("||")) {
/* 266 */           operador = "\\|\\|";
/* 267 */           ((NCLCompoundAction)acao).setOperator(NCLActionOperator.PAR);
/*     */         }
/* 269 */         if (subExpressao[i].contains("delay")) {
/* 270 */           String delay = subExpressao[i].substring(subExpressao[i].indexOf('=') + 1, subExpressao[i].length()).trim();
/* 271 */           ((NCLCompoundAction)acao).setDelay(delay.replaceAll("'", ""));
/*     */         }
/* 273 */         i = 0;
/* 274 */         while (operador == null) {
/* 275 */           if (subExpressao[i].startsWith(";")) {
/* 276 */             operador = ";";
/* 277 */             ((NCLCompoundAction)acao).setOperator(NCLActionOperator.SEQ);
/*     */           }
/* 279 */           else if (subExpressao[i].startsWith("||")) {
/* 280 */             operador = "\\|\\|";
/* 281 */             ((NCLCompoundAction)acao).setOperator(NCLActionOperator.PAR);
/*     */           }
/* 283 */           i++;
/*     */         }
/*     */       }
/*     */       else {
/* 287 */         if (simpleActExpression.contains(";")) {
/* 288 */           operador = ";";
/* 289 */           ((NCLCompoundAction)acao).setOperator(NCLActionOperator.SEQ);
/*     */         }
/* 291 */         else if (simpleActExpression.contains("||")) {
/* 292 */           operador = "\\|\\|";
/* 293 */           ((NCLCompoundAction)acao).setOperator(NCLActionOperator.PAR);
/*     */         }
/* 295 */         subExpressao = simpleActExpression.split(operador);
/*     */       }
/* 297 */       String operator = operador;
/* 298 */       if (operador.equals("\\|\\|"))
/* 299 */         operator = "||";
/* 300 */       for (int i = 0; i < subExpressao.length; i++) {
/* 301 */         if (subExpressao[i].startsWith(operator)) {
/* 302 */           if (!subExpressao[i].equals(operator)) {
/* 303 */             String[] sub = subExpressao[i].split(operador);
/* 304 */             for (int j = 0; j < sub.length; j++)
/* 305 */               if (sub[j].trim().length() > 0)
/* 306 */                 interSimpleAction(sub[j].trim(), connectorPai, (NCLCompoundAction)acao);
/*     */           }
/*     */         }
/*     */         else {
/* 310 */           interAction(subExpressao[i], connectorPai, (NCLCompoundAction)acao);
/*     */         }
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 316 */       acao = interSimpleAction(simpleActExpression.trim(), connectorPai, acaoPai);
/*     */     }
/*     */ 
/* 319 */     return acao;
/*     */   }
/*     */   public static String[] getInnerParentesis(String expression) {
/* 322 */     Vector auxiliar = new Vector();
/* 323 */     char[] stringExp = expression.toCharArray();
/* 324 */     int parem_num = 0;
/* 325 */     int primeiroInd = 0;
/* 326 */     int ultimoInd = 0;
/* 327 */     Boolean adiciona = Boolean.valueOf(false);
/* 328 */     for (int i = 0; i < stringExp.length; i++) {
/* 329 */       if (stringExp[i] == '(') {
/* 330 */         parem_num++;
/* 331 */         if (!adiciona.booleanValue()) {
/* 332 */           if ((i > ultimoInd) && (ultimoInd != 0)) {
/* 333 */             auxiliar.add(expression.substring(ultimoInd + 1, i).trim());
/*     */           }
/* 335 */           primeiroInd = i + 1;
/* 336 */           adiciona = Boolean.valueOf(true);
/*     */         }
/*     */       }
/* 339 */       else if (stringExp[i] == ')') {
/* 340 */         parem_num--;
/* 341 */         ultimoInd = i;
/*     */       }
/* 343 */       if ((parem_num == 0) && (adiciona.booleanValue())) {
/* 344 */         auxiliar.add(expression.substring(primeiroInd, ultimoInd).trim());
/* 345 */         adiciona = Boolean.valueOf(false);
/*     */       }
/*     */     }
/* 348 */     if (ultimoInd != expression.length() - 1)
/* 349 */       auxiliar.add(expression.substring(ultimoInd + 1, expression.length()).trim());
/* 350 */     String[] retorno = new String[auxiliar.size()];
/* 351 */     auxiliar.toArray(retorno);
/* 352 */     return retorno;
/*     */   }
/*     */ 
/*     */   private NCLAction interSimpleAction(String simpleActExpression, NCLCausalConnector connectorPai) throws XMLException {
/* 356 */     return interSimpleAction(simpleActExpression, connectorPai, null);
/*     */   }
/*     */ 
/*     */   private NCLAction interSimpleAction(String simpleActExpression, NCLCausalConnector connectorPai, NCLCompoundAction acaoPai) throws XMLException {
/* 360 */     NCLSimpleAction acao = new NCLSimpleAction();
/* 361 */     if (acaoPai == null)
/* 362 */       connectorPai.setAction(acao);
/*     */     else {
/* 364 */       acaoPai.addAction(acao);
/*     */     }
/* 366 */     if (simpleActExpression.trim().toLowerCase().startsWith("start")) {
/* 367 */       acao.setRole(NCLDefaultActionRole.START);
/*     */     }
/* 369 */     else if (simpleActExpression.trim().toLowerCase().startsWith("stop")) {
/* 370 */       acao.setRole(NCLDefaultActionRole.STOP);
/*     */     }
/* 372 */     else if (simpleActExpression.trim().toLowerCase().startsWith("abort")) {
/* 373 */       acao.setRole(NCLDefaultActionRole.ABORT);
/*     */     }
/* 375 */     else if (simpleActExpression.trim().toLowerCase().startsWith("pause")) {
/* 376 */       acao.setRole(NCLDefaultActionRole.PAUSE);
/*     */     }
/* 378 */     else if (simpleActExpression.trim().toLowerCase().startsWith("resume")) {
/* 379 */       acao.setRole(NCLDefaultActionRole.RESUME);
/*     */     }
/* 381 */     else if (simpleActExpression.trim().toLowerCase().startsWith("set")) {
/* 382 */       acao.setRole(NCLDefaultActionRole.SET);
/*     */     }
/*     */     else {
/* 385 */       acao.setRole(simpleActExpression.trim().substring(0, simpleActExpression.trim().indexOf(" ")));
/*     */     }
/*     */ 
/* 389 */     if (simpleActExpression.toLowerCase().contains(" with ")) {
/* 390 */       String[] parametrosExtras = simpleActExpression.substring(simpleActExpression.indexOf("with ") + 4, simpleActExpression.length()).split(",");
/* 391 */       for (int i = 0; i < parametrosExtras.length; i++) {
/* 392 */         parametrosExtras[i] = parametrosExtras[i].trim();
/* 393 */         if (parametrosExtras[i].toLowerCase().contains("delay")) {
/* 394 */           parametrosExtras[i] = parametrosExtras[i].trim();
/* 395 */           String delay = parametrosExtras[i].substring(parametrosExtras[i].indexOf("=") + 1, parametrosExtras[i].length()).trim();
/* 396 */           delay = delay.replace("\"", "").trim();
/* 397 */           acao.setDelay(delay);
/*     */         }
/* 399 */         else if (parametrosExtras[i].toLowerCase().contains("eventtype")) {
/* 400 */           String tipo = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"") + 1, parametrosExtras[i].length()).trim();
/* 401 */           tipo = tipo.substring(0, tipo.indexOf('"'));
/* 402 */           acao.setEventType(NCLEventType.valueOf(tipo.toUpperCase()));
/*     */         }
/* 404 */         else if (parametrosExtras[i].toLowerCase().contains("actiontype")) {
/* 405 */           String tipo = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"") + 1, parametrosExtras[i].length()).trim();
/* 406 */           tipo = tipo.substring(0, tipo.indexOf('"'));
/* 407 */           acao.setActionType(NCLEventAction.getEnumType(tipo.toUpperCase()));
/*     */         }
/* 409 */         else if (parametrosExtras[i].toLowerCase().contains("value")) {
/* 410 */           String value = parametrosExtras[i].substring(parametrosExtras[i].indexOf("=") + 1, parametrosExtras[i].length()).trim();
/* 411 */           value = value.replace("\"", "");
/* 412 */           acao.setValue(value);
/*     */         }
/* 414 */         else if (parametrosExtras[i].toLowerCase().contains("min")) {
/* 415 */           String min = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"") + 1, parametrosExtras[i].length()).trim();
/* 416 */           min = min.substring(0, min.indexOf('"'));
/* 417 */           acao.setMin(Integer.valueOf(Integer.parseInt(min)));
/*     */         }
/* 419 */         else if (parametrosExtras[i].toLowerCase().contains("max")) {
/* 420 */           String max = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"") + 1, parametrosExtras[i].length()).trim();
/* 421 */           max = max.substring(0, max.indexOf('"'));
/* 422 */           acao.setMax(max);
/*     */         }
/* 424 */         else if (parametrosExtras[i].toLowerCase().contains("qualifier")) {
/* 425 */           String qual = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"") + 1, parametrosExtras[i].length()).trim();
/* 426 */           qual = qual.substring(0, qual.indexOf('"'));
/* 427 */           acao.setQualifier(NCLActionOperator.valueOf(qual.toUpperCase()));
/*     */         }
/* 429 */         else if (parametrosExtras[i].toLowerCase().contains("repeat")) {
/* 430 */           String repeat = parametrosExtras[i].substring(parametrosExtras[i].indexOf("=") + 1, parametrosExtras[i].length()).trim();
/* 431 */           repeat = repeat.replace("\"", "");
/* 432 */           acao.setRepeat(repeat);
/*     */         }
/* 434 */         else if (parametrosExtras[i].toLowerCase().contains("repeatdelay")) {
/* 435 */           String delay = parametrosExtras[i].substring(parametrosExtras[i].indexOf("=") + 1, parametrosExtras[i].length()).trim();
/* 436 */           delay = delay.replace("\"", "");
/* 437 */           acao.setRepeatDelay(delay);
/*     */         }
/* 439 */         else if (parametrosExtras[i].toLowerCase().contains("duration")) {
/* 440 */           String duration = parametrosExtras[i].substring(parametrosExtras[i].indexOf("=") + 1, parametrosExtras[i].length()).trim();
/* 441 */           duration = duration.replace("\"", "");
/* 442 */           acao.setDuration(duration);
/*     */         }
/* 444 */         else if (parametrosExtras[i].toLowerCase().contains("by")) {
/* 445 */           String by = parametrosExtras[i].substring(parametrosExtras[i].indexOf("=") + 1, parametrosExtras[i].length()).trim();
/* 446 */           by = by.replace("\"", "");
/* 447 */           acao.setBy(by);
/*     */         }
/*     */       }
/*     */     }
/* 451 */     return acao;
/*     */   }
/*     */ }

/* Location:           C:\Users\Convidado\Desktop\pacoteCompleto\jnsTranslator.jar
 * Qualified Name:     JNSConnectorInterpreter
 * JD-Core Version:    0.6.2
 */