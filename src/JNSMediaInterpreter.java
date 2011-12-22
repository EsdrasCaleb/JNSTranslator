import java.net.URISyntaxException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import br.uff.midiacom.ana.datatype.enums.NCLMimeType;
import br.uff.midiacom.ana.datatype.auxiliar.SrcType;
import br.uff.midiacom.ana.descriptor.NCLDescriptor;
import br.uff.midiacom.ana.interfaces.NCLProperty;
import br.uff.midiacom.ana.node.NCLMedia;
import br.uff.midiacom.xml.XMLException;


public class JNSMediaInterpreter {
	private JNSHeadInterpreter interpretadorHead;
	
	JNSMediaInterpreter(JNSHeadInterpreter interpretadorHead){
		this.interpretadorHead = interpretadorHead;
	}
	
	NCLMedia Interprets(JSONObject jsnMedia,Object corpo) throws XMLException{
    	NCLMedia media = new NCLMedia((String)jsnMedia.get("id"));
    	if(jsnMedia.containsKey("refer")){
    		media.setRefer((NCLMedia) JNSaNaComplements.FindNode((String)jsnMedia.get("refer"),corpo));
        }
        if(jsnMedia.containsKey("src")){
            media.setSrc(new SrcType((String)jsnMedia.get("src")));
            if((!jsnMedia.containsKey("region")||!jsnMedia.containsKey("descriptor"))&& (((String)jsnMedia.get("src")).contains(".mp")||((String)jsnMedia.get("src")).contains(".wav"))){
            	NCLDescriptor descritorMeda = new NCLDescriptor(media.getId()+"_Descriptor");
            	interpretadorHead.getInterpretadorDescritor().getBase().addDescriptor(descritorMeda);
            	media.setDescriptor(descritorMeda);
            }
        }
        if(jsnMedia.containsKey("type")){
        	String midiaTipe = ((String)jsnMedia.get("type")).replace('/', '_').replace('-', '_');
            media.setType(NCLMimeType.valueOf(midiaTipe.toUpperCase()));
        }
        if(jsnMedia.containsKey("descriptor")){
            media.setDescriptor((NCLDescriptor)interpretadorHead.getInterpretadorDescritor().getBase().getDescriptors().get((String)jsnMedia.get("descriptor")));
        }
        else if(jsnMedia.containsKey("region")){
            NCLDescriptor descritorMedia = new NCLDescriptor(media.getId()+"_Descriptor");
            descritorMedia.setRegion(interpretadorHead.getInterpretadorRegiao().getBase().findRegion((String)jsnMedia.get("region")));
            interpretadorHead.getInterpretadorDescritor().getBase().addDescriptor(descritorMedia);
            media.setDescriptor(descritorMedia);
        }
        else if(media.getMediaType().name().toUpperCase().contains("AUDIO")){
        	NCLDescriptor descritorMedia = new NCLDescriptor(media.getId()+"_Descriptor");
        	interpretadorHead.getInterpretadorDescritor().getBase().addDescriptor(descritorMedia);
            media.setDescriptor(descritorMedia);
        }
        if(jsnMedia.containsKey("anchors")){
            JSONArray jsnAnchors = (JSONArray)jsnMedia.get("anchors");
            int i;
            for(i=0;i<jsnAnchors.size();i++){
                 JSONObject elemento = (JSONObject)jsnAnchors.get(i);
                 if(elemento.containsKey("property")){
                	NCLProperty propriedade = interpretadorHead.getInterpretaRegra().getProperty(JNSjSONComplements.getKey(((JSONObject)elemento.get("property")).toString()));
                    media.addProperty(propriedade);
                 }
                 else if(elemento.containsKey("area")){
                     media.addArea(JNSAreaInterpreter.Interprets((JSONObject)elemento.get("area")));
                 }
            }

        }
        return media;
    }
}
