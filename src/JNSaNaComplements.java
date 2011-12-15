import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import br.uff.midiacom.ana.NCLBody;
import br.uff.midiacom.ana.connector.NCLCausalConnector;
import br.uff.midiacom.ana.connector.NCLCompoundCondition;
import br.uff.midiacom.ana.connector.NCLConnectorBase;
import br.uff.midiacom.ana.connector.NCLConnectorParam;
import br.uff.midiacom.ana.descriptor.NCLDescriptor;
import br.uff.midiacom.ana.descriptor.NCLDescriptorBase;
import br.uff.midiacom.ana.descriptor.NCLDescriptorSwitch;
import br.uff.midiacom.ana.descriptor.param.NCLDescriptorParam;
import br.uff.midiacom.ana.interfaces.NCLInterface;
import br.uff.midiacom.ana.interfaces.NCLPort;
import br.uff.midiacom.ana.link.NCLBind;
import br.uff.midiacom.ana.link.NCLLink;
import br.uff.midiacom.ana.link.NCLParam;
import br.uff.midiacom.ana.node.NCLContext;
import br.uff.midiacom.ana.node.NCLMedia;
import br.uff.midiacom.ana.node.NCLNode;
import br.uff.midiacom.ana.node.NCLSwitch;
import br.uff.midiacom.ana.region.NCLRegion;
import br.uff.midiacom.ana.region.NCLRegionBase;
import br.uff.midiacom.ana.rule.NCLRuleBase;
import br.uff.midiacom.ana.rule.NCLTestRule;
import br.uff.midiacom.ana.transition.NCLTransition;
import br.uff.midiacom.ana.transition.NCLTransitionBase;
import br.uff.midiacom.xml.XMLException;


public class JNSaNaComplements {

	
    static NCLInterface FindInterface(String id, Object no){
    	Iterator interfaces = null;
    	if(no instanceof NCLMedia)
    		interfaces = ((NCLMedia)no).getAreas().iterator();
    	else if(no instanceof NCLContext)
    		interfaces = ((NCLContext)no).getPorts().iterator();
    	else if(no instanceof NCLSwitch)
    		interfaces = ((NCLSwitch)no).getPorts().iterator();
    	 while(interfaces.hasNext()){
    		 NCLInterface aux = (NCLInterface) interfaces.next();
             if(aux.getId().equalsIgnoreCase(id)){
                 return  aux;
             }
    	 }
    	 if(no instanceof NCLMedia)
     		interfaces = ((NCLMedia)no).getProperties().iterator();
    	 else if(no instanceof NCLContext)
     		interfaces = ((NCLContext)no).getProperties().iterator();
    	 while(interfaces.hasNext()){
    		 NCLInterface aux = (NCLInterface) interfaces.next();
             if(aux.getId().equalsIgnoreCase(id)){
                 return  aux;
             }
    	 }
    	 return null;
    }
    
    static NCLNode FindNode(String id,Object corpo) throws XMLException{
    	if(corpo instanceof NCLBody)
    		return ((NCLBody)corpo).findNode(id); 
    	else if(corpo instanceof NCLContext)
    		return ((NCLContext)corpo).findNode(id);
    	else if(corpo instanceof NCLSwitch)
    		return ((NCLSwitch)corpo).findNode(id);
    	return null;
    }

    static NCLTransition FindTrasition(String id,NCLTransitionBase bTransition){
        Iterator transition = bTransition.getTransitions().iterator();
        while(transition.hasNext()){
            NCLTransition aux = (NCLTransition)transition.next();
            if(aux.getId().equalsIgnoreCase(id)){
                return  aux;
            }
        }
        return null;
    }
    
    static NCLDescriptor FindDescriptor(int focusIndex, NCLDescriptorBase bDescritores){
    	Iterator descritores = bDescritores.getDescriptors().iterator();
        while(descritores.hasNext()){
            NCLDescriptor aux = (NCLDescriptor)descritores.next();
            if(aux.getFocusIndex() == focusIndex){
                return  aux;
            }
        }
        return null;
    }

     static NCLDescriptor FindDescriptor(String id, Object bDescritores){
    	 Iterator descritores = null;
    	 if(bDescritores instanceof NCLDescriptorBase)
    		 descritores = ((NCLDescriptorBase)bDescritores).getDescriptors().iterator();
    	 else
    		 descritores = ((NCLDescriptorSwitch)bDescritores).getDescriptors().iterator();
         while(descritores.hasNext()){
            NCLDescriptor aux = (NCLDescriptor)descritores.next();
            if(aux.getId().equalsIgnoreCase(id)){
                return  aux;
            }
        }
        return null;
    }
     
    static NCLRegion FindRegion(String id, Object bRegioes) {
    	Iterator regioes = null;
    	if(bRegioes instanceof NCLRegionBase)
    		regioes =  ((NCLRegionBase)bRegioes).getRegions().iterator();
    	else
    		regioes =  ((NCLRegion)bRegioes).getRegions().iterator();
        while(regioes.hasNext()){
            NCLRegion aux = (NCLRegion) regioes.next();
            if(aux.getId().equalsIgnoreCase(id)){
                return  aux;
            }
            else{
            	aux = FindRegion(id,aux);
            	if(aux != null)
            		return aux;
            }
        }
        return null;
    }

	static NCLConnectorParam FindParameter(String nome,NCLCausalConnector connectorPai) {
		Iterator parametros = null;
		parametros =  ((NCLCausalConnector)connectorPai).getConnectorParams().iterator();
        while(parametros.hasNext()){
            NCLConnectorParam aux = (NCLConnectorParam) parametros.next();
            if(aux.getName().equalsIgnoreCase(nome)){
                return  aux;
            }
        }
		return null;
	}
	
	static NCLParam FindParameter(String nome,NCLLink eloPai){
		Iterator parametros = null;
		parametros =  ((NCLLink)eloPai).getLinkParams().iterator();
        while(parametros.hasNext()){
        	NCLParam aux = (NCLParam) parametros.next();
            if(aux.getName().getName().equalsIgnoreCase(nome)){
                return  aux;
            }
        }
		return null;
	}
	
	static NCLBind FindBind(String nome,NCLLink eloPai){
		Iterator binds = null;
		binds =  ((NCLLink)eloPai).getBinds().iterator();
        while(binds.hasNext()){
        	NCLBind aux = (NCLBind) binds.next();
            if(aux.getRole().getName().equalsIgnoreCase(nome)){
                return  aux;
            }
        }
		return null;
	}

	static NCLCausalConnector FindConnector(String id, NCLConnectorBase bConectores) {
		Iterator conectors = null;
		conectors =  bConectores.getCausalConnectors().iterator();
        while(conectors.hasNext()){
        	NCLCausalConnector aux = (NCLCausalConnector) conectors.next();
            if(aux.getId().equalsIgnoreCase(id)){
                return  aux;
            }
        }
		return null;
	}
}
