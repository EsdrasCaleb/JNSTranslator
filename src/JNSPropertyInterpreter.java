import org.json.simple.JSONObject;

import br.uff.midiacom.ana.interfaces.NCLProperty;
import br.uff.midiacom.xml.XMLException;


public class JNSPropertyInterpreter {

    static NCLProperty Interprets(JSONObject jnsProperty) throws XMLException{
        NCLProperty propriedade = new NCLProperty(JNSjSONComplements.getKey(jnsProperty.toString()));
        propriedade.setValue((String)jnsProperty.get(propriedade.getName()));
        return propriedade;
    }
}
