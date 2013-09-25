 import br.uff.midiacom.ana.NCLBody;
 import br.uff.midiacom.ana.link.NCLLink;
 import br.uff.midiacom.ana.meta.NCLMetadata;
 import br.uff.midiacom.ana.util.exception.XMLException;
 import java.util.Vector;
 import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
 
//classe para interpretar o corpo do documento
 public class JNSBodyInterpreter
 {
   private JNSHeadInterpreter interpretadorHead;
   private JNSMediaInterpreter interpretadorMedia;
   private JNSContextInterpreter interpretaContexto;
   private JNSLinkInterpreter interpretaElo;
   private JNSSwitchInterpreter interpretaSwitch;
private int i;
 
   JNSBodyInterpreter(JNSHeadInterpreter interpretadorHead)
   {
     this.interpretadorHead = interpretadorHead;
     this.interpretadorMedia = new JNSMediaInterpreter(interpretadorHead);
     this.interpretaElo = new JNSLinkInterpreter(interpretadorHead);
     this.interpretaContexto = new JNSContextInterpreter(interpretadorHead, this.interpretaElo);
     this.interpretaSwitch = new JNSSwitchInterpreter(interpretadorHead, this.interpretaContexto);
   }
   
   NCLBody Interprets(JSONArray jsonBody) throws XMLException {
     NCLBody corpo = new NCLBody();
     this.interpretadorHead.body = corpo;
 
     Vector portas = new Vector();
     for (int i = 0; i < jsonBody.size(); i++) {
       JSONObject elemento = (JSONObject)jsonBody.get(i);
       if (elemento.containsKey("id")) {
				try{
					corpo.setId((String)elemento.get("id"));
				}
				catch(XMLException e){
					throw new XMLException("Erro com o id "+e.getMessage());
				}
       } else if (elemento.containsKey("media")) {
		  try{
		   corpo.addNode(this.interpretadorMedia.Interprets((JSONObject)elemento.get("media")));
		  }
		  catch(XMLException e){
			  throw new XMLException("Erro com a midia:"+elemento.get("id")+"\n"+e.getMessage());
		  }
       }
       else if (elemento.containsKey("context")) {
			try{
			 this.interpretaContexto.Interprets((JSONArray)elemento.get("context"), corpo);
			}
			catch(XMLException e){
				throw new XMLException("Erro com o contexto:\n"+elemento.toString()+"\n"+e.getMessage());
			}
       }
       else if (elemento.containsKey("meta")) {
    	   try{
    		   corpo.addMeta(JNSMetaInterpreter.InterMeta((JSONObject)elemento.get("meta")));
    	   }
    	   catch(XMLException e){
				throw new XMLException("Erro com a meta:\n"+elemento.toString()+"\n"+e.getMessage());
			}
       }
       else if (elemento.containsKey("metadata")) {
         NCLMetadata meta = new NCLMetadata();
         try{
        	 meta.setRDFTree((String)elemento.get("metadata"));
        	 corpo.addMetadata(meta);
         }
         catch(XMLException e){
			throw new XMLException("Erro com a metadata:\n"+elemento.toString()+"\n"+e.getMessage());
         }
       }
       else if (elemento.containsKey("property")) {
    	   try{
    		   corpo.addProperty(JNSPropertyInterpreter.Interprets((JSONObject)elemento.get("property")));
    	   }
    	   catch(XMLException e){
   				throw new XMLException("Erro com a property:"+elemento.get("name").toString()+"\n"+e.getMessage());
    	   }
       }
       else if (elemento.containsKey("link")) {
    	 try{
    		 NCLLink link = this.interpretaElo.Interprets((JSONObject)elemento.get("link"), corpo);
    		 corpo.addLink(link);
    	 }
    	 catch(XMLException e){
    		 throw new XMLException("Erro com a link:\n"+elemento.toString()+"\n"+e.getMessage());
    	 }
       }
       else if (elemento.containsKey("switch")) {
    	   try{
    		   corpo.addNode(this.interpretaSwitch.Interprets((JSONObject)elemento.get("switch"), corpo));
    	   }
    	   catch(XMLException e){
   				throw new XMLException("Erro com o switch:"+elemento.get("id").toString()+"\n"+e.getMessage());
    	   }
       }
       else if (elemento.containsKey("port")) {
         portas.add(elemento);
       }
     }
     for (i = 0; i < portas.size(); i++) {
    	 try{
    		 JSONObject elemento = (JSONObject)portas.get(i);
    		 JNSPortInterpreter.Interprets((JSONObject)elemento.get("port"), corpo);
    	 }
    	 catch(XMLException e){
    		 throw new XMLException("Erro com a porta:\n"+portas.get(i).toString()+"\n"+e.getMessage());
    	 }
     }
 
     return corpo;
   }
 }
