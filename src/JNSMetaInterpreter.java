import org.json.simple.JSONObject;

import br.uff.midiacom.ana.meta.NCLMeta;
import br.uff.midiacom.ana.meta.NCLMetadata;
import br.uff.midiacom.xml.XMLException;


public class JNSMetaInterpreter {
	public static NCLMeta InterMeta(JSONObject jsnMeta) throws XMLException{
        NCLMeta meta = new NCLMeta();
        String nome = JNSjSONComplements.getKey(jsnMeta.toString());
        meta.setName(nome);
        meta.setContent((String)jsnMeta.get(nome));
        return meta;
    }
	
	public static NCLMetadata InterMetadata(String rdfTree) throws XMLException{
		 NCLMetadata  meta = new NCLMetadata();
         meta.setRDFTree(rdfTree);
         return meta;
	}

}
