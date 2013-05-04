/*     */ import br.uff.midiacom.ana.descriptor.NCLDescriptor;
/*     */ import br.uff.midiacom.ana.descriptor.NCLDescriptorBase;
/*     */ import br.uff.midiacom.ana.descriptor.NCLDescriptorParam;
/*     */ import br.uff.midiacom.ana.descriptor.NCLDescriptorSwitch;
/*     */ import br.uff.midiacom.ana.region.NCLRegion;
/*     */ import br.uff.midiacom.ana.rule.NCLBindRule;
/*     */ import br.uff.midiacom.ana.rule.NCLRuleBase;
/*     */ import br.uff.midiacom.ana.rule.NCLTestRule;
/*     */ import br.uff.midiacom.ana.util.PercentageType;
/*     */ import br.uff.midiacom.ana.util.SrcType;
/*     */ import br.uff.midiacom.ana.util.TimeType;
/*     */ import br.uff.midiacom.ana.util.enums.NCLAttributes;
/*     */ import br.uff.midiacom.ana.util.enums.NCLColor;
/*     */ import br.uff.midiacom.ana.util.exception.XMLException;
/*     */ import java.util.Vector;
/*     */ import org.json.simple.JSONArray;
/*     */ import org.json.simple.JSONObject;
/*     */ 
/*     */ public class JNSDescriptorInterpreter
/*     */ {
/*     */   private NCLDescriptorBase Base;
/*     */   private Vector AuxiliarVector;
/*     */   private JNSHeadInterpreter Pai;
/*     */ 
/*     */   NCLDescriptorBase getBase()
/*     */   {
/*  32 */     return this.Base;
/*     */   }
/*     */ 
/*     */   JNSDescriptorInterpreter(JNSHeadInterpreter pai) throws XMLException {
/*  36 */     this.Base = new NCLDescriptorBase();
/*  37 */     this.AuxiliarVector = new Vector();
/*  38 */     this.Pai = pai;
/*     */   }
/*     */   private NCLDescriptor Interprets(JSONObject jnsDescritor) throws XMLException {
/*  41 */     return Interprets(jnsDescritor, null);
/*     */   }
/*     */   private NCLDescriptor Interprets(JSONObject jnsDescritor, NCLDescriptorSwitch pai) throws XMLException {
/*  44 */     NCLDescriptor descritor = new NCLDescriptor((String)jnsDescritor.get("id"));
/*  45 */     if (pai == null)
/*  46 */       this.Base.addDescriptor(descritor);
/*     */     else
/*  48 */       pai.addDescriptor(descritor);
/*  49 */     Boolean navegacao = Boolean.valueOf(false);
/*  50 */     if (jnsDescritor.containsKey("player")) {
/*  51 */       descritor.setPlayer((String)jnsDescritor.get("player"));
/*     */     }
/*  53 */     if (jnsDescritor.containsKey("explicitDur")) {
/*  54 */       descritor.setExplicitDur(new TimeType(jnsDescritor.get("explicitDur").toString()));
/*     */     }
/*  56 */     if (jnsDescritor.containsKey("region"))
/*     */     {
/*     */       NCLRegion nclRegiao;
/*  57 */       if ((jnsDescritor.get("region") instanceof JSONObject)) {
/*  58 */         JSONObject regiao = (JSONObject)jnsDescritor.get("region");
/*  59 */         if (!regiao.containsKey("id"))
/*  60 */           regiao.put("id", "Regiao_" + descritor.getId());
/*  61 */         nclRegiao = this.Pai.getInterpretadorRegiao().Add(regiao);
/*     */       }
/*     */       else {
/*  64 */         descritor.setRegion(this.Pai.getInterpretadorRegiao().getBase().findRegion(jnsDescritor.get("region").toString()));
/*     */       }
/*     */     }
/*  66 */     if (jnsDescritor.containsKey("freeze")) {
/*  67 */       descritor.setFreeze((Boolean)jnsDescritor.get("freeze"));
/*     */     }
/*  69 */     if (jnsDescritor.containsKey("focusIndex")) {
/*  70 */       descritor.setFocusIndex(Integer.valueOf(Integer.parseInt(jnsDescritor.get("focusIndex").toString())));
/*  71 */       navegacao = Boolean.valueOf(true);
/*     */     }
/*  73 */     if (jnsDescritor.containsKey("moveLeft")) {
/*  74 */       descritor.setMoveLeft((NCLDescriptor)this.Base.findDescriptor(Integer.valueOf(Integer.parseInt(jnsDescritor.get("moveLeft").toString()))));
/*  75 */       navegacao = Boolean.valueOf(true);
/*     */     }
/*  77 */     if (jnsDescritor.containsKey("moveRight")) {
/*  78 */       descritor.setMoveRight((NCLDescriptor)this.Base.findDescriptor(Integer.valueOf(Integer.parseInt(jnsDescritor.get("moveRight").toString()))));
/*  79 */       navegacao = Boolean.valueOf(true);
/*     */     }
/*  81 */     if (jnsDescritor.containsKey("moveUp")) {
/*  82 */       descritor.setMoveUp((NCLDescriptor)this.Base.findDescriptor(Integer.valueOf(Integer.parseInt(jnsDescritor.get("moveUp").toString()))));
/*  83 */       navegacao = Boolean.valueOf(true);
/*     */     }
/*  85 */     if (jnsDescritor.containsKey("moveDown")) {
/*  86 */       descritor.setMoveDown((NCLDescriptor)this.Base.findDescriptor(Integer.valueOf(Integer.parseInt(jnsDescritor.get("moveDown").toString()))));
/*  87 */       navegacao = Boolean.valueOf(true);
/*     */     }
/*  89 */     if (jnsDescritor.containsKey("transIn")) {
/*  90 */       descritor.setTransIn(JNSaNaComplements.FindTrasition((String)jnsDescritor.get("transIn"), this.Pai.getInterpretaTransicao().getBase()));
/*  91 */       navegacao = Boolean.valueOf(true);
/*     */     }
/*  93 */     if (jnsDescritor.containsKey("transOut")) {
/*  94 */       descritor.setTransOut(JNSaNaComplements.FindTrasition((String)jnsDescritor.get("transOut"), this.Pai.getInterpretaTransicao().getBase()));
/*  95 */       navegacao = Boolean.valueOf(true);
/*     */     }
/*  97 */     if (jnsDescritor.containsKey("focusBorderColor")) {
/*  98 */       descritor.setFocusBorderColor(NCLColor.valueOf(((String)jnsDescritor.get("focusBorderColor")).toUpperCase()));
/*     */     }
/* 100 */     if (jnsDescritor.containsKey("selBorderColor")) {
/* 101 */       descritor.setSelBorderColor(NCLColor.valueOf(((String)jnsDescritor.get("selBorderColor")).toUpperCase()));
/*     */     }
/* 103 */     if (jnsDescritor.containsKey("focusBorderWidth")) {
/* 104 */       descritor.setFocusBorderWidth(Integer.valueOf(Integer.parseInt(jnsDescritor.get("focusBorderWidth").toString())));
/*     */     }
/* 106 */     if (jnsDescritor.containsKey("focusBorderTransparency")) {
/* 107 */       descritor.setFocusBorderTransparency(new PercentageType(jnsDescritor.get("focusBorderTransparency").toString()));
/*     */     }
/* 109 */     if (jnsDescritor.containsKey("focusSelSrc")) {
/* 110 */       descritor.setFocusSelSrc(new SrcType((String)jnsDescritor.get("focusSelSrc")));
/*     */     }
/* 112 */     if (jnsDescritor.containsKey("focusSrc")) {
/* 113 */       descritor.setFocusSrc(new SrcType((String)jnsDescritor.get("focusSrc")));
/*     */     }
/* 115 */     if (jnsDescritor.containsKey("descriptorParams")) {
/* 116 */       JSONArray params = (JSONArray)jnsDescritor.get("descriptorParams");
/* 117 */       int i = 0;
/* 118 */       for (i = 0; i < params.size(); i++) {
/* 119 */         String nomeParametro = JNSjSONComplements.getKey(((JSONObject)params.get(i)).toString());
/* 120 */         String valor = (String)((JSONObject)params.get(i)).get(nomeParametro);
/* 121 */         NCLDescriptorParam parametro = new NCLDescriptorParam();
/* 122 */         parametro.setName(NCLAttributes.getEnumType(nomeParametro));
/* 123 */         parametro.setValue(valor);
/* 124 */         descritor.addDescriptorParam(parametro);
/*     */       }
/*     */     }
/* 127 */     if (navegacao.booleanValue()) {
/* 128 */       this.AuxiliarVector.add(jnsDescritor);
/*     */     }
/* 130 */     return descritor;
/*     */   }
/*     */ 
/*     */   public void ReInterprets() throws XMLException {
/* 134 */     if (this.AuxiliarVector.size() == 0)
/* 135 */       return;
/* 136 */     int i = 0;
/* 137 */     for (i = 0; i < this.AuxiliarVector.size(); i++) {
/* 138 */       JSONObject jnsDescritor = (JSONObject)this.AuxiliarVector.get(i);
/* 139 */       NCLDescriptor descritor = (NCLDescriptor)this.Base.findDescriptor((String)jnsDescritor.get("id"));
/* 140 */       if (jnsDescritor.containsKey("moveLeft")) {
/* 141 */         descritor.setMoveLeft((NCLDescriptor)this.Base.findDescriptor(Integer.valueOf(Integer.parseInt(jnsDescritor.get("moveLeft").toString()))));
/*     */       }
/* 143 */       if (jnsDescritor.containsKey("moveRight")) {
/* 144 */         descritor.setMoveRight((NCLDescriptor)this.Base.findDescriptor(Integer.valueOf(Integer.parseInt(jnsDescritor.get("moveRight").toString()))));
/*     */       }
/* 146 */       if (jnsDescritor.containsKey("moveUp")) {
/* 147 */         descritor.setMoveUp((NCLDescriptor)this.Base.findDescriptor(Integer.valueOf(Integer.parseInt(jnsDescritor.get("moveUp").toString()))));
/*     */       }
/* 149 */       if (jnsDescritor.containsKey("moveDown")) {
/* 150 */         descritor.setMoveDown((NCLDescriptor)this.Base.findDescriptor(Integer.valueOf(Integer.parseInt(jnsDescritor.get("moveDown").toString()))));
/*     */       }
/* 152 */       if (jnsDescritor.containsKey("transIn")) {
/* 153 */         descritor.setTransIn(JNSaNaComplements.FindTrasition((String)jnsDescritor.get("transIn"), this.Pai.getInterpretaTransicao().getBase()));
/*     */       }
/* 155 */       if (jnsDescritor.containsKey("transOut"))
/* 156 */         descritor.setTransOut(JNSaNaComplements.FindTrasition((String)jnsDescritor.get("transOut"), this.Pai.getInterpretaTransicao().getBase()));
/*     */     }
/*     */   }
/*     */ 
/*     */   public NCLDescriptor Add(JSONObject jnsDescritor) throws XMLException
/*     */   {
/* 162 */     NCLDescriptor descritor = Interprets(jnsDescritor);
/* 163 */     return descritor;
/*     */   }
/*     */ 
/*     */   public void AddSwitc(JSONObject jnsDescritorSwtich) throws XMLException
/*     */   {
/* 168 */     NCLDescriptorSwitch descriptorSwitch = new NCLDescriptorSwitch((String)jnsDescritorSwtich.get("id"));
/* 169 */     this.Base.addDescriptor(descriptorSwitch);
/* 170 */     String[] chaves = JNSjSONComplements.getKeys(jnsDescritorSwtich);
/* 171 */     int i = 0;
/* 172 */     Vector regras = new Vector();
/* 173 */     Vector descritorID = new Vector();
/* 174 */     for (i = 0; i < chaves.length; i++) {
/* 175 */       NCLTestRule regra = null;
/* 176 */       if ((chaves[i].contains("=")) || (chaves[i].contains(">")) || (chaves[i].contains("<"))) {
/* 177 */         regra = this.Pai.getInterpretaRegra().InterRule(chaves[i]);
/*     */       }
/* 179 */       else if ((chaves[i] != "default") && (chaves[i] != "id") && (chaves[i] != "vars")) {
/* 180 */         regra = (NCLTestRule)this.Pai.getInterpretaRegra().getBase().findRule(chaves[i]);
/* 181 */         if (regra == null) {
/* 182 */           regra = (NCLTestRule)this.Pai.getInterpretaRegra().getBase().findRule(chaves[i] + "_" + descriptorSwitch.getId());
/*     */         }
/*     */       }
/* 185 */       if (regra != null) {
/* 186 */         NCLBindRule bind = new NCLBindRule();
/* 187 */         bind.setRule(regra);
/* 188 */         NCLDescriptor descritor = null;
/* 189 */         if ((jnsDescritorSwtich.get(chaves[i]) instanceof JSONObject)) {
/* 190 */           descritor = Interprets((JSONObject)((JSONObject)jnsDescritorSwtich.get(chaves[i])).get("descriptor"), descriptorSwitch);
/*     */         }
/*     */         else {
/* 193 */           descritor = JNSaNaComplements.FindDescriptor((String)jnsDescritorSwtich.get(chaves[i]), descriptorSwitch);
/* 194 */           if (descritor == null) {
/* 195 */             regras.add(bind);
/* 196 */             descritorID.add((String)jnsDescritorSwtich.get(chaves[i]));
/*     */           }
/*     */         }
/* 199 */         bind.setConstituent(descritor);
/* 200 */         descriptorSwitch.addBind(bind);
/*     */       }
/*     */     }
/* 203 */     if (jnsDescritorSwtich.containsKey("default")) {
/* 204 */       NCLDescriptor descritor = null;
/* 205 */       if ((jnsDescritorSwtich.get("default") instanceof JSONObject)) {
/* 206 */         descritor = Interprets((JSONObject)((JSONObject)jnsDescritorSwtich.get("default")).get("descriptor"), descriptorSwitch);
/*     */       }
/*     */       else {
/* 209 */         descritor = JNSaNaComplements.FindDescriptor((String)jnsDescritorSwtich.get("default"), descriptorSwitch);
/*     */       }
/* 211 */       descriptorSwitch.setDefaultDescriptor(descritor);
/*     */     }
/* 213 */     for (i = 0; i < regras.size(); i++) {
/* 214 */       NCLBindRule bind = (NCLBindRule)regras.get(i);
/* 215 */       bind.setConstituent(JNSaNaComplements.FindDescriptor((String)descritorID.get(i), descriptorSwitch));
/* 216 */       descriptorSwitch.addBind(bind);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Convidado\Desktop\pacoteCompleto\jnsTranslator.jar
 * Qualified Name:     JNSDescriptorInterpreter
 * JD-Core Version:    0.6.2
 */