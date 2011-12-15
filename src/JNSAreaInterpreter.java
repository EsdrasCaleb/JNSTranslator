import org.json.simple.JSONObject;


import br.uff.midiacom.ana.datatype.auxiliar.SampleType;
import br.uff.midiacom.ana.datatype.auxiliar.TimeType;
import br.uff.midiacom.ana.interfaces.NCLArea;
import br.uff.midiacom.xml.XMLException;
import br.uff.midiacom.xml.datatype.array.ArrayType;


public class JNSAreaInterpreter {
	static NCLArea Interprets(JSONObject jnsArea) throws XMLException{
        NCLArea area = new NCLArea((String)jnsArea.get("id"));
         if(jnsArea.containsKey("coords")){
            String[] sCoords = ((String)jnsArea.get("coords")).split(",");
            double[] coords = new double[sCoords.length];
            int i;
            for(i=0;i<sCoords.length;i++)
                coords[i] = Integer.parseInt(sCoords[i]);
            area.setCoords(new ArrayType(coords));
        }
        if(jnsArea.containsKey("begin")){
            area.setBegin(new TimeType((String)jnsArea.get("begin")));
        }
        if(jnsArea.containsKey("end")){
            area.setEnd(new TimeType((String)jnsArea.get("end")));
        }
        if(jnsArea.containsKey("text")){
            area.setText((String)jnsArea.get("text"));
        }
        if(jnsArea.containsKey("position")){
            area.setPosition(Integer.parseInt(jnsArea.get("position").toString()));
        }
        if(jnsArea.containsKey("first")){
            area.setFirst(new SampleType((String)jnsArea.get("first")));
        }
        if(jnsArea.containsKey("last")){
            area.setLast(new SampleType((String)jnsArea.get("last")));
        }
        if(jnsArea.containsKey("label")){
            area.setLabel((String)jnsArea.get("label"));
        }

        return area;
    }
}
