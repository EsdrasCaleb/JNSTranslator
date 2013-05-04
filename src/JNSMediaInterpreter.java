/*    */ import br.uff.midiacom.ana.NCLBody;
/*    */ import br.uff.midiacom.ana.descriptor.NCLDescriptor;
/*    */ import br.uff.midiacom.ana.descriptor.NCLDescriptorBase;
/*    */ import br.uff.midiacom.ana.node.NCLMedia;
/*    */ import br.uff.midiacom.ana.region.NCLRegion;
/*    */ import br.uff.midiacom.ana.util.ElementList;
/*    */ import br.uff.midiacom.ana.util.SrcType;
/*    */ import br.uff.midiacom.ana.util.enums.NCLInstanceType;
/*    */ import br.uff.midiacom.ana.util.enums.NCLMimeType;
/*    */ import br.uff.midiacom.ana.util.exception.XMLException;
/*    */ import org.json.simple.JSONArray;
/*    */ import org.json.simple.JSONObject;
/*    */ 
/*    */ public class JNSMediaInterpreter
/*    */ {
/*    */   private JNSHeadInterpreter interpretadorHead;
/*    */ 
/*    */   JNSMediaInterpreter(JNSHeadInterpreter interpretadorHead)
/*    */   {
/* 21 */     this.interpretadorHead = interpretadorHead;
/*    */   }
/*    */ 
/*    */   NCLMedia Interprets(JSONObject jsnMedia) throws XMLException {
/* 25 */     NCLMedia media = new NCLMedia((String)jsnMedia.get("id"));
/* 26 */     if (jsnMedia.containsKey("refer")) {
/* 27 */       media.setRefer((NCLMedia)this.interpretadorHead.body.findNode((String)jsnMedia.get("refer")));
/*    */     }
/* 29 */     if (jsnMedia.containsKey("instance")) {
/* 30 */       media.setInstance(NCLInstanceType.valueOf((String)jsnMedia.get("instance")));
/*    */     }
/* 32 */     if (jsnMedia.containsKey("src")) {
/* 33 */       media.setSrc(new SrcType((String)jsnMedia.get("src")));
/*    */     }
/*    */     else {
/* 36 */       media.setType(NCLMimeType.APPLICATION_X_GINGA_SETTINGS);
/*    */     }
/* 38 */     if (jsnMedia.containsKey("type")) {
/* 39 */       String midiaTipe = ((String)jsnMedia.get("type")).replace('/', '_').replace('-', '_');
/* 40 */       media.setType(NCLMimeType.valueOf(midiaTipe.toUpperCase()));
/*    */     }
/* 42 */     if (jsnMedia.containsKey("descriptor")) {
/* 43 */       Object descritor = null;
/* 44 */       if ((jsnMedia.get("descriptor") instanceof JSONObject)) {
/* 45 */         JSONObject descritorJNS = (JSONObject)jsnMedia.get("region");
/* 46 */         if (!descritorJNS.containsKey("id"))
/* 47 */           descritorJNS.put("id", "Regiao_" + media.getId());
/* 48 */         descritor = this.interpretadorHead.getInterpretadorDescritor().Add(descritorJNS);
/*    */       }
/* 50 */       else if ((jsnMedia.get("descriptor") instanceof String)) {
/* 51 */         descritor = this.interpretadorHead.getInterpretadorDescritor().getBase().findDescriptor((String)jsnMedia.get("descriptor"));
/*    */       }
/* 53 */       media.setDescriptor(descritor);
/*    */     }
/* 55 */     else if (jsnMedia.containsKey("region")) {
/* 56 */       NCLDescriptor descritorMedia = null;
/* 57 */       if ((jsnMedia.get("region") instanceof JSONObject)) {
/* 58 */         JSONObject regiao = (JSONObject)jsnMedia.get("region");
/* 59 */         if (!regiao.containsKey("id"))
/* 60 */           regiao.put("id", "Regiao_" + media.getId());
/* 61 */         NCLRegion nclRegiao = this.interpretadorHead.getInterpretadorRegiao().Add(regiao);
/* 62 */         descritorMedia = new NCLDescriptor(nclRegiao.getId() + "_Descriptor");
/* 63 */         descritorMedia.setRegion(nclRegiao);
/* 64 */         this.interpretadorHead.getInterpretadorDescritor().getBase().addDescriptor(descritorMedia);
/*    */       }
/*    */       else {
/* 67 */         descritorMedia = (NCLDescriptor)this.interpretadorHead.getInterpretadorDescritor().getBase().getDescriptors().get((String)jsnMedia.get("region") + "_Descriptor");
/* 68 */         if (descritorMedia == null) {
/* 69 */           descritorMedia = new NCLDescriptor(((String)jsnMedia.get("region")).replace('#', '_') + "_Descriptor");
/* 70 */           descritorMedia.setRegion(this.interpretadorHead.getInterpretadorRegiao().getBase().findRegion((String)jsnMedia.get("region")));
/* 71 */           if (descritorMedia.getRegion() != null)
/* 72 */             this.interpretadorHead.getInterpretadorDescritor().getBase().addDescriptor(descritorMedia);
/*    */         }
/*    */       }
/* 75 */       if (media.getDescriptor() == null)
/* 76 */         media.setDescriptor(descritorMedia);
/*    */     }
/* 78 */     if (jsnMedia.containsKey("anchors")) {
/* 79 */       JSONArray jsnAnchors = (JSONArray)jsnMedia.get("anchors");
/*    */ 
/* 81 */       for (int i = 0; i < jsnAnchors.size(); i++) {
/* 82 */         JSONObject elemento = (JSONObject)jsnAnchors.get(i);
/* 83 */         if (elemento.containsKey("property")) {
/* 84 */           media.addProperty(JNSPropertyInterpreter.Interprets((JSONObject)elemento.get("property")));
/*    */         }
/* 86 */         else if (elemento.containsKey("area")) {
/* 87 */           media.addArea(JNSAreaInterpreter.Interprets((JSONObject)elemento.get("area")));
/*    */         }
/*    */       }
/*    */     }
/*    */ 
/* 92 */     return media;
/*    */   }
/*    */ }

/* Location:           C:\Users\Convidado\Desktop\pacoteCompleto\jnsTranslator.jar
 * Qualified Name:     JNSMediaInterpreter
 * JD-Core Version:    0.6.2
 */