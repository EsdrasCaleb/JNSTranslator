/*     */ import br.uff.midiacom.ana.NCLBody;
/*     */ import br.uff.midiacom.ana.interfaces.NCLMapping;
/*     */ import br.uff.midiacom.ana.interfaces.NCLSwitchPort;
/*     */ import br.uff.midiacom.ana.node.NCLNode;
/*     */ import br.uff.midiacom.ana.node.NCLSwitch;
/*     */ import br.uff.midiacom.ana.rule.NCLBindRule;
/*     */ import br.uff.midiacom.ana.rule.NCLRuleBase;
/*     */ import br.uff.midiacom.ana.rule.NCLTestRule;
/*     */ import br.uff.midiacom.ana.util.exception.XMLException;
/*     */ import java.util.Vector;
/*     */ import org.json.simple.JSONArray;
/*     */ import org.json.simple.JSONObject;
/*     */ 
/*     */ public class JNSSwitchInterpreter
/*     */ {
/*     */   private JNSHeadInterpreter interpretadorHead;
/*     */   private JNSMediaInterpreter interpretadorMedia;
/*     */   private JNSContextInterpreter interpretaContexto;
/*     */ 
/*     */   JNSSwitchInterpreter(JNSHeadInterpreter interpretadorHead, JNSContextInterpreter interpretaContexto)
/*     */   {
/*  25 */     this.interpretadorMedia = new JNSMediaInterpreter(interpretadorHead);
/*  26 */     this.interpretadorHead = interpretadorHead;
/*  27 */     this.interpretaContexto = interpretaContexto;
/*     */   }
/*     */ 
/*     */   NCLSwitch Interprets(JSONObject jnsSwtic, Object contexto) throws XMLException {
/*  31 */     NCLSwitch selecao = new NCLSwitch((String)jnsSwtic.get("id"));
/*  32 */     if (jnsSwtic.containsKey("refer")) {
/*  33 */       selecao.setRefer((NCLSwitch)this.interpretadorHead.body.findNode((String)jnsSwtic.get("reger")));
/*     */     }
/*  35 */     String[] chaves = JNSjSONComplements.getKeys(jnsSwtic);
/*  36 */     int i = 0;
/*  37 */     Vector regras = new Vector();
/*  38 */     Vector nodeID = new Vector();
/*  39 */     for (i = 0; i < chaves.length; i++) {
/*  40 */       NCLTestRule regra = null;
/*  41 */       String chave = chaves[i];
/*  42 */       if ((chave.contains("=")) || (chave.contains(">")) || (chave.contains("<")) || (chave.contains(" eq ")) || (chave.contains(" lt ")) || (chave.contains(" ne ")) || (chave.contains(" gte ")) || (chave.contains(" lte ")) || (chave.contains(" gt "))) {
/*  43 */         regra = this.interpretadorHead.getInterpretaRegra().InterRule(chave);
/*     */       }
/*  45 */       else if ((!chave.equals("default")) && (!chave.equals("refer")) && (!chave.equals("id")) && (!chave.equals("vars"))) {
/*  46 */         regra = (NCLTestRule)this.interpretadorHead.getInterpretaRegra().getBase().findRule(chave);
/*  47 */         if (regra == null) {
/*  48 */           regra = (NCLTestRule)this.interpretadorHead.getInterpretaRegra().getBase().findRule(chave + "_" + selecao.getId());
/*     */         }
/*     */       }
/*  51 */       if (regra != null) {
/*  52 */         NCLBindRule bind = new NCLBindRule();
/*  53 */         bind.setRule(regra);
/*  54 */         NCLNode no = null;
/*  55 */         if ((jnsSwtic.get(chave) instanceof JSONObject)) {
/*  56 */           JSONObject elemento = (JSONObject)jnsSwtic.get(chave);
/*  57 */           no = InterpretaNo(elemento, selecao);
/*     */         }
/*     */         else {
/*  60 */           no = selecao.findNode(chave);
/*     */         }
/*  62 */         if (no == null) {
/*  63 */           regras.add(bind);
/*  64 */           nodeID.add(chave);
/*     */         }
/*     */         else {
/*  67 */           bind.setConstituent(no);
/*  68 */           selecao.addBind(bind);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*  73 */     if (jnsSwtic.containsKey("default")) {
/*  74 */       NCLNode no = null;
/*  75 */       if ((jnsSwtic.get("default") instanceof JSONObject)) {
/*  76 */         JSONObject elemento = (JSONObject)jnsSwtic.get("default");
/*  77 */         InterpretaNo(elemento, selecao);
/*     */       }
/*     */       else {
/*  80 */         no = selecao.findNode((String)jnsSwtic.get("default"));
/*     */       }
/*  82 */       selecao.setDefaultComponent(no);
/*     */     }
/*  84 */     for (i = 0; i < regras.size(); i++) {
/*  85 */       NCLBindRule bind = (NCLBindRule)regras.get(i);
/*  86 */       bind.setConstituent(selecao.findNode((String)nodeID.get(i)));
/*  87 */       selecao.addBind(bind);
/*     */     }
/*  89 */     if (jnsSwtic.containsKey("switchPort")) {
/*  90 */       if ((jnsSwtic.get("switchPort") instanceof JSONObject)) {
/*  91 */         InterpretaSwitchPort((JSONObject)jnsSwtic.get("switchPort"), selecao);
/*     */       }
/*     */       else {
/*  94 */         JSONArray ports = (JSONArray)jnsSwtic.get("switchPort");
/*  95 */         for (i = 0; i < ports.size(); i++)
/*  96 */           InterpretaSwitchPort((JSONObject)ports.get(i), selecao);
/*     */       }
/*     */     }
/*  99 */     return selecao;
/*     */   }
/*     */ 
/*     */   private void InterpretaSwitchPort(JSONObject switchPort, NCLSwitch selecao) throws XMLException {
/* 103 */     String nome = JNSjSONComplements.getKey(switchPort.toString());
/* 104 */     JSONArray jnsPorts = (JSONArray)switchPort.get(nome);
/* 105 */     NCLSwitchPort portaSwtich = new NCLSwitchPort(nome);
/*     */ 
/* 107 */     for (int i = 0; i < jnsPorts.size(); i++)
/* 108 */       createSwitchPort((String)jnsPorts.get(i), selecao, portaSwtich);
/* 109 */     selecao.addPort(portaSwtich);
/*     */   }
/*     */ 
/*     */   private NCLNode InterpretaNo(JSONObject elemento, NCLSwitch selecao) throws XMLException {
/* 113 */     NCLNode no = null;
/* 114 */     if (elemento.containsKey("context")) {
/* 115 */       no = this.interpretaContexto.Interprets((JSONArray)elemento.get("context"), selecao);
/*     */     }
/*     */     else {
/* 118 */       if (elemento.containsKey("media")) {
/* 119 */         no = this.interpretadorMedia.Interprets((JSONObject)elemento.get("media"));
/*     */       }
/* 121 */       else if (elemento.containsKey("switch")) {
/* 122 */         no = Interprets((JSONObject)elemento.get("switch"), selecao);
/*     */       }
/* 124 */       selecao.addNode(no);
/*     */     }
/* 126 */     return no;
/*     */   }
/*     */ 
/*     */   private static void createSwitchPort(String nointerface, NCLSwitch selecao, NCLSwitchPort portaSwtich) throws XMLException {
/* 130 */     NCLMapping mapa = new NCLMapping();
/* 131 */     NCLNode no = selecao.getNode(nointerface.substring(0, nointerface.indexOf('.')));
/* 132 */     mapa.setComponent(no);
/* 133 */     mapa.setInterface(JNSaNaComplements.FindInterface(nointerface.substring(nointerface.indexOf('.') + 1), no));
/* 134 */     portaSwtich.addMapping(mapa);
/*     */   }
/*     */ }

/* Location:           C:\Users\Convidado\Desktop\pacoteCompleto\jnsTranslator.jar
 * Qualified Name:     JNSSwitchInterpreter
 * JD-Core Version:    0.6.2
 */