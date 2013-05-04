/*     */ import br.uff.midiacom.ana.rule.NCLCompositeRule;
/*     */ import br.uff.midiacom.ana.rule.NCLRule;
/*     */ import br.uff.midiacom.ana.rule.NCLRuleBase;
/*     */ import br.uff.midiacom.ana.rule.NCLTestRule;
/*     */ import br.uff.midiacom.ana.util.enums.NCLOperator;
/*     */ import br.uff.midiacom.ana.util.exception.XMLException;
/*     */ import br.uff.midiacom.ana.util.ncl.NCLVariable;
/*     */ import java.util.Vector;
/*     */ import org.json.simple.JSONObject;
/*     */ 
/*     */ public class JNSRuleInterpreter
/*     */ {
/*     */   private NCLRuleBase Base;
/*  24 */   private int RuleIndex = 0;
/*     */   private Vector VectorDeAuxiliarSwitRule;
/*     */ 
/*     */   JNSRuleInterpreter(Vector VectorDeAuxiliarSwitRule)
/*     */     throws XMLException
/*     */   {
/*  29 */     this.VectorDeAuxiliarSwitRule = VectorDeAuxiliarSwitRule;
/*  30 */     this.Base = new NCLRuleBase();
/*     */   }
/*     */ 
/*     */   public NCLRuleBase getBase() {
/*  34 */     return this.Base;
/*     */   }
/*     */ 
/*     */   NCLTestRule InterRule(String expressao) throws XMLException {
/*  38 */     return InterRule(expressao, "RulePadrao_" + this.RuleIndex++);
/*     */   }
/*     */ 
/*     */   NCLTestRule InterRule(String expressao, String id) throws XMLException
/*     */   {
/*  43 */     expressao = expressao.trim();
/*  44 */     int indiceLocal = 0;
/*     */     NCLTestRule regra;
/*  45 */     if ((expressao.toLowerCase().contains(" and ")) || (expressao.toLowerCase().contains(" or "))) {
/*  46 */       regra = new NCLCompositeRule(id);
/*  47 */       String operador = null;
/*  48 */       String[] subExpressao = (String[])null;
/*  49 */       if (expressao.contains("(")) {
/*  50 */         subExpressao = expressao.split("\\(.*\\)");
/*  51 */         subExpressao = JNSConnectorInterpreter.getInnerParentesis(expressao);
/*  52 */         while (subExpressao.length == 1)
/*  53 */           subExpressao = JNSConnectorInterpreter.getInnerParentesis(subExpressao[0]);
/*  54 */         int i = subExpressao.length - 1;
/*  55 */         if (subExpressao[i].toLowerCase().startsWith("and")) {
/*  56 */           operador = "and";
/*  57 */           ((NCLCompositeRule)regra).setOperator(NCLOperator.AND);
/*     */         }
/*  59 */         else if (subExpressao[i].toLowerCase().startsWith("or")) {
/*  60 */           operador = "or";
/*  61 */           ((NCLCompositeRule)regra).setOperator(NCLOperator.OR);
/*     */         }
/*     */       }
/*  64 */       if (operador == null) {
/*  65 */         if (expressao.toLowerCase().contains("and")) {
/*  66 */           operador = "and";
/*  67 */           ((NCLCompositeRule)regra).setOperator(NCLOperator.AND);
/*     */         }
/*  69 */         else if (expressao.toLowerCase().contains("or")) {
/*  70 */           operador = "or";
/*  71 */           ((NCLCompositeRule)regra).setOperator(NCLOperator.OR);
/*     */         }
/*  73 */         subExpressao = new String[1];
/*  74 */         subExpressao[0] = expressao;
/*     */       }
/*  76 */       for (int i = 0; i < subExpressao.length; i++) {
/*  77 */         String[] pequenasExpessoes = subExpressao[i].toLowerCase().split(operador);
/*  78 */         for (int j = 0; j < pequenasExpessoes.length; j++)
/*     */         {
/*  80 */           if (pequenasExpessoes[j].trim() != "")
/*  81 */             ((NCLCompositeRule)regra).addRule(InterRule(pequenasExpessoes[j], "SubRule_" + id + "_" + indiceLocal++));
/*     */         }
/*  83 */         expressao = expressao.trim().substring(expressao.indexOf(subExpressao[i]), expressao.length()).replace(subExpressao[i], "");
/*     */       }
/*     */     }
/*     */     else {
/*  87 */       expressao.replaceAll("[()]", "");
/*  88 */       regra = new NCLRule(id);
/*  89 */       regra = interExpressaoDeRule(expressao, (NCLRule)regra);
/*     */     }
/*  91 */     this.Base.addRule(regra);
/*  92 */     return regra;
/*     */   }
/*     */ 
/*     */   private NCLRule interExpressaoDeRule(String ruleExpr, NCLRule regra) throws XMLException {
/*  96 */     String[] variavalValor = (String[])null;
/*  97 */     String comparador = null;
/*  98 */     comparador = JNSaNaComplements.InterpretaComparador(ruleExpr, regra);
/*  99 */     variavalValor = ruleExpr.split(comparador);
/* 100 */     regra.setValue(variavalValor[1].trim().replaceAll("'", ""));
/* 101 */     regra.setVar(new NCLVariable(variavalValor[0].trim()));
/* 102 */     return regra;
/*     */   }
/*     */ 
/*     */   void Add(JSONObject rule) throws XMLException
/*     */   {
/* 107 */     String expressao = (String)rule.get("expression");
/* 108 */     String id = (String)rule.get("id");
/* 109 */     if (!VariableReplaced(rule).booleanValue())
/* 110 */       this.Base.addRule(InterRule(expressao, id));
/*     */   }
/*     */ 
/*     */   Boolean VariableReplaced(JSONObject regra)
/*     */     throws XMLException
/*     */   {
/* 120 */     int i = 0;
/* 121 */     Boolean retorno = Boolean.valueOf(false);
/* 122 */     String id = (String)regra.get("id");
/* 123 */     String expressao = (String)regra.get("expression");
/* 124 */     for (i = 0; i < this.VectorDeAuxiliarSwitRule.size(); i++) {
/* 125 */       if (((String)((Vector)this.VectorDeAuxiliarSwitRule.elementAt(i)).elementAt(1)).contains(id)) {
/* 126 */         Vector vetorDeTeste = (Vector)((Vector)this.VectorDeAuxiliarSwitRule.elementAt(i)).elementAt(2);
/* 127 */         int j = 0;
/* 128 */         Boolean troca = Boolean.valueOf(false);
/* 129 */         String novaExpressao = expressao;
/* 130 */         for (j = 0; j < vetorDeTeste.size(); j++) {
/* 131 */           if (expressao.contains((String)vetorDeTeste.elementAt(j))) {
/* 132 */             retorno = Boolean.valueOf(true);
/* 133 */             troca = Boolean.valueOf(true);
/* 134 */             novaExpressao = expressao.replace((String)vetorDeTeste.elementAt(j), 
/* 135 */               (String)((Vector)((Vector)this.VectorDeAuxiliarSwitRule.elementAt(i)).elementAt(3)).elementAt(j));
/*     */           }
/*     */         }
/*     */ 
/* 139 */         if (troca.booleanValue()) {
/* 140 */           this.Base.addRule(InterRule(novaExpressao, id + "_" + (String)((Vector)this.VectorDeAuxiliarSwitRule.elementAt(i)).elementAt(0)));
/* 141 */           expressao = (String)regra.get("expression");
/*     */         }
/*     */       }
/*     */     }
/* 145 */     return retorno;
/*     */   }
/*     */ }

/* Location:           C:\Users\Convidado\Desktop\pacoteCompleto\jnsTranslator.jar
 * Qualified Name:     JNSRuleInterpreter
 * JD-Core Version:    0.6.2
 */