 import br.uff.midiacom.ana.NCLBody;
 import br.uff.midiacom.ana.descriptor.NCLDescriptor;
 import br.uff.midiacom.ana.descriptor.NCLDescriptorBase;
 import br.uff.midiacom.ana.node.NCLMedia;
 import br.uff.midiacom.ana.region.NCLRegion;
 import br.uff.midiacom.ana.util.ElementList;
 import br.uff.midiacom.ana.util.SrcType;
 import br.uff.midiacom.ana.util.enums.NCLInstanceType;
 import br.uff.midiacom.ana.util.enums.NCLMimeType;
 import br.uff.midiacom.ana.util.exception.XMLException;
 import org.json.simple.JSONArray;
 import org.json.simple.JSONObject;
 
 public class JNSMediaInterpreter
 {
   private JNSHeadInterpreter interpretadorHead;
 
   JNSMediaInterpreter(JNSHeadInterpreter interpretadorHead)
   {
     this.interpretadorHead = interpretadorHead;
   }
 
   NCLMedia Interprets(JSONObject jsnMedia) throws XMLException {
     NCLMedia media = new NCLMedia((String)jsnMedia.get("id"));
     if (jsnMedia.containsKey("refer")) {
       media.setRefer((NCLMedia)this.interpretadorHead.body.findNode((String)jsnMedia.get("refer")));
     }
     if (jsnMedia.containsKey("instance")) {
       media.setInstance(NCLInstanceType.valueOf((String)jsnMedia.get("instance")));
     }
     if (jsnMedia.containsKey("src")) {
       media.setSrc(new SrcType((String)jsnMedia.get("src")));
     }
     else {
       media.setType(NCLMimeType.APPLICATION_X_GINGA_SETTINGS);
     }
     if (jsnMedia.containsKey("type")) {
       String midiaTipe = ((String)jsnMedia.get("type")).replace('/', '_').replace('-', '_');
       media.setType(NCLMimeType.valueOf(midiaTipe.toUpperCase()));
     }
     if (jsnMedia.containsKey("descriptor")) {
       Object descritor = null;
       if ((jsnMedia.get("descriptor") instanceof JSONObject)) {
         JSONObject descritorJNS = (JSONObject)jsnMedia.get("region");
         if (!descritorJNS.containsKey("id"))
           descritorJNS.put("id", "Regiao_" + media.getId());
         descritor = this.interpretadorHead.getInterpretadorDescritor().Add(descritorJNS);
       }
       else if ((jsnMedia.get("descriptor") instanceof String)) {
         descritor = this.interpretadorHead.getInterpretadorDescritor().getBase().findDescriptor((String)jsnMedia.get("descriptor"));
       }
       media.setDescriptor(descritor);
     }
     else if (jsnMedia.containsKey("region")) {
       NCLDescriptor descritorMedia = null;
       if ((jsnMedia.get("region") instanceof JSONObject)) {
         JSONObject regiao = (JSONObject)jsnMedia.get("region");
         if (!regiao.containsKey("id"))
           regiao.put("id", "Regiao_" + media.getId());
         NCLRegion nclRegiao = this.interpretadorHead.getInterpretadorRegiao().Add(regiao);
         descritorMedia = new NCLDescriptor(nclRegiao.getId() + "_Descriptor");
         descritorMedia.setRegion(nclRegiao);
         this.interpretadorHead.getInterpretadorDescritor().getBase().addDescriptor(descritorMedia);  
       }
       else {
         descritorMedia = (NCLDescriptor)this.interpretadorHead.getInterpretadorDescritor().getBase().getDescriptors().get((String)jsnMedia.get("region") + "_Descriptor");
         if (descritorMedia == null) {
           descritorMedia = new NCLDescriptor(((String)jsnMedia.get("region")).replace('#', '_') + "_Descriptor");
           descritorMedia.setRegion(this.interpretadorHead.getInterpretadorRegiao().getBase().findRegion((String)jsnMedia.get("region")));
           if (descritorMedia.getRegion() != null)
             this.interpretadorHead.getInterpretadorDescritor().getBase().addDescriptor(descritorMedia);
         }
       }
       if (media.getDescriptor() == null)
         media.setDescriptor(descritorMedia);
       if(jsnMedia.containsKey("keys")){
    	   //adiciona chaves a vetor marcando id da midia e direções
    	   //cocloca no descritor da midia um focusindex(caso não o tenha), usando uma constante
       }
     }
     if (jsnMedia.containsKey("anchors")) {
       JSONArray jsnAnchors = (JSONArray)jsnMedia.get("anchors");
 
       for (int i = 0; i < jsnAnchors.size(); i++) {
         JSONObject elemento = (JSONObject)jsnAnchors.get(i);
         if (elemento.containsKey("property")) {
           media.addProperty(JNSPropertyInterpreter.Interprets((JSONObject)elemento.get("property")));
         }
         else if (elemento.containsKey("area")) {
           media.addArea(JNSAreaInterpreter.Interprets((JSONObject)elemento.get("area")));
         }
       }
     }
 
     return media;
   }
 }