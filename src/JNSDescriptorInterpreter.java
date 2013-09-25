 import br.uff.midiacom.ana.descriptor.NCLDescriptor;
 import br.uff.midiacom.ana.descriptor.NCLDescriptorBase;
 import br.uff.midiacom.ana.descriptor.NCLDescriptorParam;
 import br.uff.midiacom.ana.descriptor.NCLDescriptorSwitch;
 import br.uff.midiacom.ana.region.NCLRegion;
 import br.uff.midiacom.ana.rule.NCLBindRule;
 import br.uff.midiacom.ana.rule.NCLRuleBase;
 import br.uff.midiacom.ana.rule.NCLTestRule;
 import br.uff.midiacom.ana.util.PercentageType;
 import br.uff.midiacom.ana.util.SrcType;
 import br.uff.midiacom.ana.util.TimeType;
 import br.uff.midiacom.ana.util.enums.NCLAttributes;
 import br.uff.midiacom.ana.util.enums.NCLColor;
 import br.uff.midiacom.ana.util.exception.XMLException;
 import java.util.Vector;
 import org.json.simple.JSONArray;
 import org.json.simple.JSONObject;
 
 public class JNSDescriptorInterpreter
 {
   private NCLDescriptorBase Base;
   private Vector AuxiliarVector;
   private JNSHeadInterpreter Pai;
 
   NCLDescriptorBase getBase()
   {
     return this.Base;
   }
 
   JNSDescriptorInterpreter(JNSHeadInterpreter pai) throws XMLException {
     this.Base = new NCLDescriptorBase();
     this.AuxiliarVector = new Vector();
     this.Pai = pai;
   }
   private NCLDescriptor Interprets(JSONObject jnsDescritor) throws XMLException {
     return Interprets(jnsDescritor, null);
   }
   private NCLDescriptor Interprets(JSONObject jnsDescritor, NCLDescriptorSwitch pai) throws XMLException {
     NCLDescriptor descritor = new NCLDescriptor((String)jnsDescritor.get("id"));
     if (pai == null)
       this.Base.addDescriptor(descritor);
     else
       pai.addDescriptor(descritor);
     Boolean navegacao = Boolean.valueOf(false);
     if (jnsDescritor.containsKey("player")) {
       descritor.setPlayer((String)jnsDescritor.get("player"));
     }
     if (jnsDescritor.containsKey("explicitDur")) {
       descritor.setExplicitDur(new TimeType(jnsDescritor.get("explicitDur").toString()));
     }
     if (jnsDescritor.containsKey("region"))
     {
       NCLRegion nclRegiao;
       if ((jnsDescritor.get("region") instanceof JSONObject)) {
         JSONObject regiao = (JSONObject)jnsDescritor.get("region");
         if (!regiao.containsKey("id"))
           regiao.put("id", "Regiao_" + descritor.getId());
         nclRegiao = this.Pai.getInterpretadorRegiao().Add(regiao);
       }
       else {
         descritor.setRegion(this.Pai.getInterpretadorRegiao().getBase().findRegion(jnsDescritor.get("region").toString()));
       }
     }
     if (jnsDescritor.containsKey("freeze")) {
       descritor.setFreeze((Boolean)jnsDescritor.get("freeze"));
     }
     if (jnsDescritor.containsKey("focusIndex")) {
       descritor.setFocusIndex(Integer.valueOf(Integer.parseInt(jnsDescritor.get("focusIndex").toString())));
       navegacao = Boolean.valueOf(true);
     }
     if (jnsDescritor.containsKey("moveLeft")) {
       descritor.setMoveLeft((NCLDescriptor)this.Base.findDescriptor(Integer.valueOf(Integer.parseInt(jnsDescritor.get("moveLeft").toString()))));
       navegacao = Boolean.valueOf(true);
     }
     if (jnsDescritor.containsKey("moveRight")) {
       descritor.setMoveRight((NCLDescriptor)this.Base.findDescriptor(Integer.valueOf(Integer.parseInt(jnsDescritor.get("moveRight").toString()))));
       navegacao = Boolean.valueOf(true);
     }
     if (jnsDescritor.containsKey("moveUp")) {
       descritor.setMoveUp((NCLDescriptor)this.Base.findDescriptor(Integer.valueOf(Integer.parseInt(jnsDescritor.get("moveUp").toString()))));
       navegacao = Boolean.valueOf(true);
     }
     if (jnsDescritor.containsKey("moveDown")) {
       descritor.setMoveDown((NCLDescriptor)this.Base.findDescriptor(Integer.valueOf(Integer.parseInt(jnsDescritor.get("moveDown").toString()))));
       navegacao = Boolean.valueOf(true);
     }
     if (jnsDescritor.containsKey("transIn")) {
       descritor.setTransIn(JNSaNaComplements.FindTrasition((String)jnsDescritor.get("transIn"), this.Pai.getInterpretaTransicao().getBase()));
       navegacao = Boolean.valueOf(true);
     }
     if (jnsDescritor.containsKey("transOut")) {
       descritor.setTransOut(JNSaNaComplements.FindTrasition((String)jnsDescritor.get("transOut"), this.Pai.getInterpretaTransicao().getBase()));
       navegacao = Boolean.valueOf(true);
     }
     if (jnsDescritor.containsKey("focusBorderColor")) {
       descritor.setFocusBorderColor(NCLColor.valueOf(((String)jnsDescritor.get("focusBorderColor")).toUpperCase()));
     }
     if (jnsDescritor.containsKey("selBorderColor")) {
       descritor.setSelBorderColor(NCLColor.valueOf(((String)jnsDescritor.get("selBorderColor")).toUpperCase()));
     }
     if (jnsDescritor.containsKey("focusBorderWidth")) {
       descritor.setFocusBorderWidth(Integer.valueOf(Integer.parseInt(jnsDescritor.get("focusBorderWidth").toString())));
     }
     if (jnsDescritor.containsKey("focusBorderTransparency")) {
       descritor.setFocusBorderTransparency(new PercentageType(jnsDescritor.get("focusBorderTransparency").toString()));
     }
     if (jnsDescritor.containsKey("focusSelSrc")) {
       descritor.setFocusSelSrc(new SrcType((String)jnsDescritor.get("focusSelSrc")));
     }
     if (jnsDescritor.containsKey("focusSrc")) {
       descritor.setFocusSrc(new SrcType((String)jnsDescritor.get("focusSrc")));
     }
     if (jnsDescritor.containsKey("descriptorParams")) {
       JSONArray params = (JSONArray)jnsDescritor.get("descriptorParams");
       int i = 0;
       for (i = 0; i < params.size(); i++) {
         String nomeParametro = JNSjSONComplements.getKey(((JSONObject)params.get(i)).toString());
         String valor = (String)((JSONObject)params.get(i)).get(nomeParametro);
         NCLDescriptorParam parametro = new NCLDescriptorParam();
         parametro.setName(NCLAttributes.getEnumType(nomeParametro));
         parametro.setValue(valor);
         descritor.addDescriptorParam(parametro);
       }
     }
     if (navegacao.booleanValue()) {
       this.AuxiliarVector.add(jnsDescritor);
     }
     return descritor;
   }
 
   public void ReInterprets() throws XMLException {
     if (this.AuxiliarVector.size() == 0)
       return;
     int i = 0;
     for (i = 0; i < this.AuxiliarVector.size(); i++) {
       JSONObject jnsDescritor = (JSONObject)this.AuxiliarVector.get(i);
       NCLDescriptor descritor = (NCLDescriptor)this.Base.findDescriptor((String)jnsDescritor.get("id"));
       if (jnsDescritor.containsKey("moveLeft")) {
         descritor.setMoveLeft((NCLDescriptor)this.Base.findDescriptor(Integer.valueOf(Integer.parseInt(jnsDescritor.get("moveLeft").toString()))));
       }
       if (jnsDescritor.containsKey("moveRight")) {
         descritor.setMoveRight((NCLDescriptor)this.Base.findDescriptor(Integer.valueOf(Integer.parseInt(jnsDescritor.get("moveRight").toString()))));
       }
       if (jnsDescritor.containsKey("moveUp")) {
         descritor.setMoveUp((NCLDescriptor)this.Base.findDescriptor(Integer.valueOf(Integer.parseInt(jnsDescritor.get("moveUp").toString()))));
       }
       if (jnsDescritor.containsKey("moveDown")) {
         descritor.setMoveDown((NCLDescriptor)this.Base.findDescriptor(Integer.valueOf(Integer.parseInt(jnsDescritor.get("moveDown").toString()))));
       }
       if (jnsDescritor.containsKey("transIn")) {
         descritor.setTransIn(JNSaNaComplements.FindTrasition((String)jnsDescritor.get("transIn"), this.Pai.getInterpretaTransicao().getBase()));
       }
       if (jnsDescritor.containsKey("transOut"))
         descritor.setTransOut(JNSaNaComplements.FindTrasition((String)jnsDescritor.get("transOut"), this.Pai.getInterpretaTransicao().getBase()));
     }
   }
 
   public NCLDescriptor Add(JSONObject jnsDescritor) throws XMLException
   {
     NCLDescriptor descritor = Interprets(jnsDescritor);
     return descritor;
   }
 
   public void AddSwitc(JSONObject jnsDescritorSwtich) throws XMLException
   {
     NCLDescriptorSwitch descriptorSwitch = new NCLDescriptorSwitch((String)jnsDescritorSwtich.get("id"));
     this.Base.addDescriptor(descriptorSwitch);
     String[] chaves = JNSjSONComplements.getKeys(jnsDescritorSwtich);
     int i = 0;
     Vector regras = new Vector();
     Vector descritorID = new Vector();
     for (i = 0; i < chaves.length; i++) {
       NCLTestRule regra = null;
       if ((chaves[i].contains("=")) || (chaves[i].contains(">")) || (chaves[i].contains("<"))) {
         regra = this.Pai.getInterpretaRegra().InterRule(chaves[i]);
       }
       else if ((chaves[i] != "default") && (chaves[i] != "id") && (chaves[i] != "vars")) {
         regra = (NCLTestRule)this.Pai.getInterpretaRegra().getBase().findRule(chaves[i]);
         if (regra == null) {
           regra = (NCLTestRule)this.Pai.getInterpretaRegra().getBase().findRule(chaves[i] + "_" + descriptorSwitch.getId());
         }
       }
       if (regra != null) {
         NCLBindRule bind = new NCLBindRule();
         bind.setRule(regra);
         NCLDescriptor descritor = null;
         if ((jnsDescritorSwtich.get(chaves[i]) instanceof JSONObject)) {
           descritor = Interprets((JSONObject)((JSONObject)jnsDescritorSwtich.get(chaves[i])).get("descriptor"), descriptorSwitch);
         }
         else {
           descritor = JNSaNaComplements.FindDescriptor((String)jnsDescritorSwtich.get(chaves[i]), descriptorSwitch);
           if (descritor == null) {
             regras.add(bind);
             descritorID.add((String)jnsDescritorSwtich.get(chaves[i]));
           }
         }
         bind.setConstituent(descritor);
         descriptorSwitch.addBind(bind);
       }
     }
     if (jnsDescritorSwtich.containsKey("default")) {
       NCLDescriptor descritor = null;
       if ((jnsDescritorSwtich.get("default") instanceof JSONObject)) {
         descritor = Interprets((JSONObject)((JSONObject)jnsDescritorSwtich.get("default")).get("descriptor"), descriptorSwitch);
       }
       else {
         descritor = JNSaNaComplements.FindDescriptor((String)jnsDescritorSwtich.get("default"), descriptorSwitch);
       }
       descriptorSwitch.setDefaultDescriptor(descritor);
     }
     for (i = 0; i < regras.size(); i++) {
       NCLBindRule bind = (NCLBindRule)regras.get(i);
       bind.setConstituent(JNSaNaComplements.FindDescriptor((String)descritorID.get(i), descriptorSwitch));
       descriptorSwitch.addBind(bind);
     }
   }
 }

/* Location:           C:\Users\Convidado\Desktop\pacoteCompleto\jnsTranslator.jar
 * Qualified Name:     JNSDescriptorInterpreter
 * JD-Core Version:    0.6.2
 */