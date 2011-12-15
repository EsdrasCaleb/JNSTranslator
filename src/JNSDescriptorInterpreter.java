import java.net.URISyntaxException;
import java.util.Vector;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import br.uff.midiacom.ana.NCLElement;
import br.uff.midiacom.ana.NCLElementImpl;
import br.uff.midiacom.ana.connector.NCLConnectorParam;
import br.uff.midiacom.ana.datatype.enums.NCLAttributes;
import br.uff.midiacom.ana.datatype.enums.NCLColor;
import br.uff.midiacom.ana.datatype.auxiliar.SrcType;
import br.uff.midiacom.ana.datatype.auxiliar.TimeType;
import br.uff.midiacom.ana.descriptor.NCLDescriptor;
import br.uff.midiacom.ana.descriptor.NCLDescriptorBase;
import br.uff.midiacom.ana.descriptor.NCLDescriptorBindRule;
import br.uff.midiacom.ana.descriptor.NCLDescriptorSwitch;
import br.uff.midiacom.ana.descriptor.param.*;
import br.uff.midiacom.ana.link.NCLParam;
import br.uff.midiacom.ana.rule.NCLTestRule;
import br.uff.midiacom.xml.XMLException;
import br.uff.midiacom.xml.datatype.number.PercentageType;


public class JNSDescriptorInterpreter {
	private NCLDescriptorBase Base;
	private Vector AuxiliarVector;
	private JNSHeadInterpreter Pai;
	
	NCLDescriptorBase getBase(){
		return Base;
	}
	
	JNSDescriptorInterpreter(JNSHeadInterpreter pai) throws XMLException{
		Base =  new NCLDescriptorBase();
		AuxiliarVector = new Vector();
		Pai = pai;
	}
	
	private NCLDescriptor Interprets(JSONObject jnsDescritor) throws XMLException {
    	NCLDescriptor descritor = new NCLDescriptor((String)jnsDescritor.get("id"));
    	Boolean navegacao = false;
        if(jnsDescritor.containsKey("player")){
            descritor.setPlayer((String)jnsDescritor.get("player"));
        }
        if(jnsDescritor.containsKey("explicitDur")){
            descritor.setExplicitDur(new TimeType(jnsDescritor.get("explicitDur").toString()));
        }
        if(jnsDescritor.containsKey("region")){
            descritor.setRegion(Pai.getInterpretadorRegiao().getBase().findRegion(jnsDescritor.get("region").toString()));
        }
        if(jnsDescritor.containsKey("freeze")){
            descritor.setFreeze((Boolean)jnsDescritor.get("freeze"));
        }
        if(jnsDescritor.containsKey("focusIndex")){
            descritor.setFocusIndex(Integer.parseInt(jnsDescritor.get("focusIndex").toString()));
            navegacao = true;
        }
        if(jnsDescritor.containsKey("moveLeft")){
            descritor.setMoveLeft((NCLDescriptor)Base.findDescriptor(Integer.parseInt(jnsDescritor.get("moveLeft").toString())));
            navegacao = true;
        }
        if(jnsDescritor.containsKey("moveRight")){
            descritor.setMoveRight((NCLDescriptor)Base.findDescriptor(Integer.parseInt(jnsDescritor.get("moveRight").toString())));
            navegacao = true;
        }
        if(jnsDescritor.containsKey("moveUp")){
            descritor.setMoveUp((NCLDescriptor)Base.findDescriptor(Integer.parseInt(jnsDescritor.get("moveUp").toString())));
            navegacao = true;
        }
        if(jnsDescritor.containsKey("moveDown")){
            descritor.setMoveDown((NCLDescriptor)Base.findDescriptor(Integer.parseInt(jnsDescritor.get("moveDown").toString())));
            navegacao = true;
        }
        if(jnsDescritor.containsKey("transIn")){
            descritor.setTransIn(JNSaNaComplements.FindTrasition((String)jnsDescritor.get("transIn"),Pai.getInterpretaTransicao().getBase()));
            navegacao = true;
        }
        if(jnsDescritor.containsKey("transOut")){
            descritor.setTransOut(JNSaNaComplements.FindTrasition((String)jnsDescritor.get("transOut"),Pai.getInterpretaTransicao().getBase()));
            navegacao = true;
        }
        if(jnsDescritor.containsKey("focusBorderColor")){
            descritor.setFocusBorderColor(NCLColor.valueOf(((String)jnsDescritor.get("focusBorderColor")).toUpperCase()));
        }
        if(jnsDescritor.containsKey("selBorderColor")){
            descritor.setSelBorderColor(NCLColor.valueOf(((String)jnsDescritor.get("selBorderColor")).toUpperCase()));
        }
        if(jnsDescritor.containsKey("focusBorderWidth")){
            descritor.setFocusBorderWidth(Integer.parseInt(jnsDescritor.get("focusBorderWidth").toString()));
        }
        if(jnsDescritor.containsKey("focusBorderTransparency")){
            descritor.setFocusBorderTransparency(new PercentageType(jnsDescritor.get("focusBorderTransparency").toString()));
        }
        if(jnsDescritor.containsKey("focusSelSrc")){
            descritor.setFocusSelSrc(new SrcType((String)jnsDescritor.get("focusSelSrc")));
        }
        if(jnsDescritor.containsKey("focusSrc")){
            descritor.setFocusSrc(new SrcType((String)jnsDescritor.get("focusSrc")));
        }
        if(jnsDescritor.containsKey("descriptorParams")){
            JSONArray params = (JSONArray)jnsDescritor.get("descriptorParams");
            int i = 0;
            for(i=0;i<params.size();i++){
                String nomeParametro = JNSjSONComplements.getKey(((JSONObject)params.get(i)).toString());
                String valor = (String) ((JSONObject)params.get(i)).get(nomeParametro);
                NCLStringDescriptorParam parametro = new NCLStringDescriptorParam();
                parametro.setName(NCLAttributes.getEnumType(nomeParametro));
                parametro.setValue(valor);
                descritor.addDescriptorParam(parametro);
            }
        }
        if(navegacao){
        	AuxiliarVector.add(jnsDescritor);
        }
		return descritor;
	}
	
	public void ReInterprets() throws XMLException{
		if(AuxiliarVector.size()==0)
        	return;
    	int i =0;
    	for(i=0;i<AuxiliarVector.size();i++){
    		JSONObject jnsDescritor = (JSONObject)AuxiliarVector.get(i);
    		NCLDescriptor descritor = (NCLDescriptor) Base.findDescriptor((String)jnsDescritor.get("id"));
    		if(jnsDescritor.containsKey("moveLeft")){
                descritor.setMoveLeft((NCLDescriptor)Base.findDescriptor(Integer.parseInt(jnsDescritor.get("moveLeft").toString())));
            }
            if(jnsDescritor.containsKey("moveRight")){
                descritor.setMoveRight((NCLDescriptor)Base.findDescriptor(Integer.parseInt(jnsDescritor.get("moveRight").toString())));
            }
            if(jnsDescritor.containsKey("moveUp")){
                descritor.setMoveUp((NCLDescriptor)Base.findDescriptor(Integer.parseInt(jnsDescritor.get("moveUp").toString())));
            }
            if(jnsDescritor.containsKey("moveDown")){
                descritor.setMoveDown((NCLDescriptor)Base.findDescriptor(Integer.parseInt(jnsDescritor.get("moveDown").toString())));
            }
            if(jnsDescritor.containsKey("transIn")){
                descritor.setTransIn(JNSaNaComplements.FindTrasition((String)jnsDescritor.get("transIn"),Pai.getInterpretaTransicao().getBase()));
            }
            if(jnsDescritor.containsKey("transOut")){
                descritor.setTransOut(JNSaNaComplements.FindTrasition((String)jnsDescritor.get("transOut"),Pai.getInterpretaTransicao().getBase()));
            }
    	}
    }
	
	public void Add(JSONObject jnsDescritor) throws XMLException {
        NCLDescriptor descritor = Interprets(jnsDescritor);
        Base.addDescriptor(descritor);
    }
    

    public void AddSwitc(JSONObject jnsDescritorSwtich) throws XMLException {
    	NCLDescriptorSwitch descriptorSwitch = new NCLDescriptorSwitch((String)jnsDescritorSwtich.get("id"));
    	String[] chaves = JNSjSONComplements.GetKeys(jnsDescritorSwtich);
    	int i = 0;
    	Vector regras = new Vector();
    	Vector descritorID = new Vector();
    	for(i=0;i<chaves.length;i++){
    		NCLTestRule regra = null;
    		if(chaves[i].contains("=") ||chaves[i].contains(">") || chaves[i].contains("<")){
    			regra = Pai.getInterpretaRegra().InterRule(chaves[i]);
    		}
    		else if(chaves[i]!="default"&&chaves[i]!="id"&&chaves[i]!="vars"){
    			regra = Pai.getInterpretaRegra().getBase().findRule(chaves[i]);
    			if(regra==null){
    				regra = Pai.getInterpretaRegra().getBase().findRule(chaves[i]+"_"+descriptorSwitch.getId());
    			}
    		}
    		if(regra != null){
    			NCLDescriptorBindRule bind = new NCLDescriptorBindRule();
    			bind.setRule(regra);
    			NCLDescriptor descritor = null;
    			if(jnsDescritorSwtich.get(chaves[i]) instanceof JSONObject){
    				descritor = Interprets((JSONObject)((JSONObject)jnsDescritorSwtich.get(chaves[i])).get("descriptor"));
    				descriptorSwitch.addDescriptor(descritor);
    			}
    			else{
    				descritor = JNSaNaComplements.FindDescriptor((String)jnsDescritorSwtich.get(chaves[i]),descriptorSwitch);
    				if(descritor == null){
	    				regras.add(bind);
	    				descritorID.add((String)jnsDescritorSwtich.get(chaves[i]));
    				}
    			}
				bind.setConstituent(descritor);
				descriptorSwitch.addBind(bind);
    		}
    	}
    	if(jnsDescritorSwtich.containsKey("default")){
    		NCLDescriptor descritor = null;
    		if(jnsDescritorSwtich.get("default") instanceof JSONObject){
    			descritor = Interprets((JSONObject)((JSONObject)jnsDescritorSwtich.get("default")).get("descriptor"));
				descriptorSwitch.addDescriptor(descritor);
    		}
    		else{
    			descritor = JNSaNaComplements.FindDescriptor((String)jnsDescritorSwtich.get("default"),descriptorSwitch);
    		}
    		descriptorSwitch.setDefaultDescriptor(descritor);
    	}
    	for(i=0;i<regras.size();i++){
    		NCLDescriptorBindRule bind = (NCLDescriptorBindRule)regras.get(i);
    		bind.setConstituent(JNSaNaComplements.FindDescriptor((String)descritorID.get(i),descriptorSwitch));
			descriptorSwitch.addBind(bind);
    	}
		
	}
}
