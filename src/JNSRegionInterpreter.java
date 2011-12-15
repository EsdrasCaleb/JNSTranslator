import org.json.simple.JSONObject;

import br.uff.midiacom.ana.region.NCLRegion;
import br.uff.midiacom.ana.region.NCLRegionBase;
import br.uff.midiacom.xml.XMLException;
import br.uff.midiacom.xml.datatype.number.RelativeType;


public class JNSRegionInterpreter {
	private NCLRegionBase Base;
	
	
	JNSRegionInterpreter() throws XMLException{
		Base = new NCLRegionBase();
	}
	NCLRegionBase getBase() throws XMLException{
		return Base;
	}
		
	void Add(JSONObject elemento) throws XMLException {
        NCLRegion regiao = new NCLRegion((String)elemento.get("id"));
        boolean relative = false;
        
        
        if(elemento.containsKey("device")){
            if(Base.getDevice() == null){
            	Base.setDevice((String)elemento.get("device"));
            }
            else if(!Base.getDevice().equalsIgnoreCase((String)elemento.get("device"))){
                throw new XMLException("Duas devices em um so documentoNCL");
            }
        }
        if(elemento.containsKey("parent")){
            String idPai = (String)elemento.get("parent");
            NCLRegion aux = Base.findRegion(idPai);
            if(aux!=null){
                aux.addRegion(regiao);
            }
            else
                throw new XMLException("Região declarada antes de seu pai");
        }
        else{
            Base.addRegion(regiao);
        }
        String valor = "";
        if(elemento.containsKey("title")){
            regiao.setTitle((String)elemento.get("title"));
        }
        if(elemento.containsKey("left")){
            regiao.setLeft(new RelativeType(elemento.get("left").toString()));
        }
        if(elemento.containsKey("right")){
            regiao.setRight(new RelativeType(elemento.get("right").toString()));
        }
        if(elemento.containsKey("top")){
            regiao.setTop(new RelativeType(elemento.get("top").toString()));
        }
        if(elemento.containsKey("bottom")){
            regiao.setBottom(new RelativeType(elemento.get("bottom").toString()));
        }
        if(elemento.containsKey("width")){
            regiao.setWidth(new RelativeType(elemento.get("width").toString()));
        }
        if(elemento.containsKey("height")){
            regiao.setHeight(new RelativeType(elemento.get("height").toString()));
        }
        if(elemento.containsKey("zIndex")){
            regiao.setzIndex(Integer.parseInt(elemento.get("zIndex").toString()));
        }
    }
}
