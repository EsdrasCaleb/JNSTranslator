/*     */ import br.uff.midiacom.ana.NCLDoc;
/*     */ import br.uff.midiacom.ana.NCLHead;
/*     */ import br.uff.midiacom.ana.util.enums.NCLNamespace;
/*     */ import br.uff.midiacom.ana.util.exception.XMLException;
/*     */ import br.uff.midiacom.ana.util.modification.NCLModificationNotifier;
/*     */ import java.util.Vector;
/*     */ import org.json.simple.JSONArray;
/*     */ import org.json.simple.JSONObject;
/*     */ import org.json.simple.parser.ParseException;
/*     */ 
/*     */ public class JNSInterpreter
/*     */ {
/*     */   private Vector VetorDeAuxiliarSwitRule;
/*     */   private JNSRuleInterpreter InterpretaRegra;
/*     */ 
/*     */   JNSInterpreter()
/*     */   {
/*  60 */     this.VetorDeAuxiliarSwitRule = new Vector();
/*     */   }
/*     */ 
/*     */   public String InterpretsJNS(JSONObject jsn)
/*     */     throws XMLException, ParseException, InterruptedException
/*     */   {
/*  74 */     NCLDoc docN = new NCLDoc();
/*     */ 
/*  77 */     JSONObject jsonNCL = (JSONObject)jsn.get("ncl");
/*  78 */     String valor = (String)jsonNCL.get("id");
/*  79 */     if (valor != null)
/*  80 */       docN.setId(valor);
/*     */     else
/*  82 */       docN.setId("jnsNCL");
/*  83 */     valor = (String)jsonNCL.get("title");
/*  84 */     if (valor != null)
/*  85 */       docN.setId(valor);
/*  86 */     docN.setXmlns(NCLNamespace.EDTV);
/*     */ 
/*  88 */     JSONArray jsonHead = (JSONArray)jsonNCL.get("head");
/*  89 */     fillVars(jsonNCL);
/*  90 */     JNSHeadInterpreter interpretadorHead = new JNSHeadInterpreter();
/*  91 */     this.InterpretaRegra = new JNSRuleInterpreter(this.VetorDeAuxiliarSwitRule);
/*  92 */     interpretadorHead.setInterpretaRegra(this.InterpretaRegra);
/*  93 */     NCLHead cabecalho = interpretadorHead.Interprets(jsonHead);
/*  94 */     docN.setHead(cabecalho);
/*  95 */     JSONArray jsonBody = (JSONArray)jsonNCL.get("body");
/*  96 */     JNSBodyInterpreter intepletaCorpo = new JNSBodyInterpreter(interpretadorHead);
/*  97 */     docN.setBody(intepletaCorpo.Interprets(jsonBody));
/*  98 */     interpretadorHead.InsertBases();
/*  99 */     docN.setHead(cabecalho);
/* 100 */     String stringRetorno = docN.parse(0);
/* 101 */     NCLModificationNotifier.getInstance().finish();
/* 102 */     return stringRetorno;
/*     */   }
/*     */ 
/*     */   void fillVars(JSONObject jsonNCL)
/*     */   {
/* 113 */     JSONArray head = (JSONArray)jsonNCL.get("head");
/* 114 */     if (head == null)
/* 115 */       return;
/* 116 */     int i = 0;
/* 117 */     for (i = 0; i < head.size(); i++) {
/* 118 */       JSONObject elemento = (JSONObject)head.get(i);
/* 119 */       if (elemento.containsKey("descriptorSwitch")) {
/* 120 */         elemento = (JSONObject)elemento.get("descriptorSwitch");
/* 121 */         fillAuxVector(elemento);
/*     */       }
/*     */     }
/* 124 */     JSONArray body = (JSONArray)jsonNCL.get("body");
/* 125 */     searchInContext(body);
/*     */   }
/*     */ 
/*     */   void searchInContext(JSONArray contexto) {
/* 129 */     int i = 0;
/* 130 */     for (i = 0; i < contexto.size(); i++) {
/* 131 */       JSONObject elemento = (JSONObject)contexto.get(i);
/* 132 */       if (elemento.containsKey("switch")) {
/* 133 */         elemento = (JSONObject)elemento.get("switch");
/* 134 */         fillAuxVector(elemento);
/*     */       }
/* 136 */       else if (elemento.containsKey("context")) {
/* 137 */         searchInContext((JSONArray)elemento.get("context"));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void fillAuxVector(JSONObject elemento) {
/* 143 */     if (elemento.containsKey("vars")) {
/* 144 */       JSONArray vars = (JSONArray)elemento.get("vars");
/* 145 */       int j = 0;
/* 146 */       Vector variaveisR = new Vector();
/* 147 */       Vector variaveisN = new Vector();
/* 148 */       for (j = 0; j < vars.size(); j++) {
/* 149 */         String key = JNSjSONComplements.getKey(((JSONObject)vars.get(j)).toString());
/* 150 */         variaveisR.add(key);
/* 151 */         variaveisN.add((String)((JSONObject)vars.get(j)).get(key));
/*     */       }
/* 153 */       String[] keys = JNSjSONComplements.getKeys(elemento);
/* 154 */       for (j = 0; j < keys.length; j++)
/* 155 */         if ((!keys[j].contains("=")) && (!keys[j].contentEquals("default")) && (!keys[j].contentEquals("refer")) && (!keys[j].contentEquals("id")) && (!keys[j].contentEquals("vars")) && (!keys[j].contains("<")) && (!keys[j].contains(">"))) {
/* 156 */           Vector vetor = new Vector();
/* 157 */           vetor.add(elemento.get("id"));
/* 158 */           vetor.add(keys[j]);
/* 159 */           vetor.add(variaveisR);
/* 160 */           vetor.add(variaveisN);
/* 161 */           this.VetorDeAuxiliarSwitRule.add(vetor);
/*     */         }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Convidado\Desktop\pacoteCompleto\jnsTranslator.jar
 * Qualified Name:     JNSInterpreter
 * JD-Core Version:    0.6.2
 */