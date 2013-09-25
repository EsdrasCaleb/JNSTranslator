import br.uff.midiacom.ana.NCLBody;
import br.uff.midiacom.ana.NCLHead;
import br.uff.midiacom.ana.connector.NCLConnectorBase;
import br.uff.midiacom.ana.descriptor.NCLDescriptorBase;
import br.uff.midiacom.ana.region.NCLRegion;
import br.uff.midiacom.ana.reuse.NCLImportBase;
import br.uff.midiacom.ana.reuse.NCLImportNCL;
import br.uff.midiacom.ana.reuse.NCLImportedDocumentBase;
import br.uff.midiacom.ana.rule.NCLRuleBase;
import br.uff.midiacom.ana.transition.NCLTransitionBase;
import br.uff.midiacom.ana.util.ElementList;
import br.uff.midiacom.ana.util.SrcType;
import br.uff.midiacom.ana.util.exception.XMLException;
import java.util.Vector;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class JNSHeadInterpreter
{
  private NCLHead Head;
  public NCLBody body;
  private JNSRegionInterpreter InterpretadorRegiao;
  private JNSDescriptorInterpreter InterpretadorDescritor;
  private JNSTransitionInterpreter InterpretaTransicao;
  private JNSRuleInterpreter InterpretaRegra;
  private JNSConnectorInterpreter InterpretaConnector;

  void setInterpretaRegra(JNSRuleInterpreter InterpretaRegra)
  {
     this.InterpretaRegra = InterpretaRegra;
  }

  JNSRuleInterpreter getInterpretaRegra() {
     return this.InterpretaRegra;
  }

  JNSRegionInterpreter getInterpretadorRegiao() {
     return this.InterpretadorRegiao;
  }

  JNSDescriptorInterpreter getInterpretadorDescritor() {
     return this.InterpretadorDescritor;
  }

  JNSConnectorInterpreter getInterpretaConnector() {
     return this.InterpretaConnector;
  }

  JNSTransitionInterpreter getInterpretaTransicao() {
     return this.InterpretaTransicao;
  }

  JNSHeadInterpreter() throws XMLException {
     this.Head = new NCLHead();
  }
  public NCLHead Interprets(JSONArray jsonHead) throws XMLException {
     return Interprets(jsonHead, Boolean.valueOf(false));
  }

  public NCLHead Interprets(JSONArray jsonHead, Boolean imported) throws XMLException{
     this.InterpretadorRegiao = new JNSRegionInterpreter(this.Head);
     this.InterpretadorDescritor = new JNSDescriptorInterpreter(this);
     this.InterpretaTransicao = new JNSTransitionInterpreter();
     this.InterpretaConnector = new JNSConnectorInterpreter();
     if (jsonHead == null)
       return this.Head;
     Vector descritores = new Vector();
     this.Head.setDescriptorBase(this.InterpretadorDescritor.getBase());
     Vector headers = new Vector();
     for (int i = 0; i < jsonHead.size(); i++) {
       JSONObject elemento = (JSONObject)jsonHead.get(i);

       if (elemento.containsKey("include")) {
         JSONObject include = (JSONObject)elemento.get("include");
         if (elemento.containsKey("jnsURI")) {
           try{
        		   String file = JsonReader.ReadString((String)include.get("jnsURI"));
        		   headers.add(JsonReader.parse(file));
           }
           catch(ParseException e){
        	   throw new XMLException("Erro na leitura e/ou interpretação do header jns incluido:\n"+elemento.toString()+"\n"+e.getMessage());
           }
        }
        else {
           if (include.containsKey("documentURI")) {
             NCLImportedDocumentBase baseImport = null;
             try{
	             if (this.Head.getImportedDocumentBase() != null) {
	               baseImport = this.Head.getImportedDocumentBase();
	             } else {
	               baseImport = new NCLImportedDocumentBase();
	               this.Head.setImportedDocumentBase(baseImport);
	             }
	             NCLImportNCL importado = new NCLImportNCL();
	             importado.setAlias((String)include.get("alias"));
	             importado.setDocumentURI(new SrcType((String)include.get("documentURI")));
	             baseImport.addImportNCL(importado);
             }
             catch(XMLException e){
          	   throw new XMLException("Problemas na leitura e/ou interpretação da base de docuemntos\n"+elemento.toString()+"\n"+e.toString());
             }
          }

           if (include.containsKey("descriptorURI")) {
        	try{
             NCLImportBase importado = new NCLImportBase();
             importado.setDocumentURI(new SrcType((String)include.get("descriptorURI")));
             importado.setAlias((String)include.get("alias"));
             this.InterpretadorDescritor.getBase().addImportBase(importado);
        	}
        	catch(XMLException e){
        		throw new XMLException("Problemas na leitura e/ou interpretação da base de descritores\n"+elemento.toString()+"\n"+e.toString());
        	}
          }
           if (include.containsKey("regionURI")) {
        	 try{
	             NCLImportBase importado = new NCLImportBase();
	             importado.setDocumentURI(new SrcType((String)include.get("regionURI")));
	             importado.setAlias((String)include.get("alias"));
	             if (include.containsKey("region")) {
	               String region = (String)include.get("region");
	               importado.setRegion((NCLRegion)this.InterpretadorRegiao.getBase().findRegion(region));
	             }
	             include.containsKey("baseId");
	             this.InterpretadorRegiao.addImportBase(importado);
        	 }
        	 catch(XMLException e){
         		throw new XMLException("Problemas na leitura e/ou interpretação da base de regiões\n"+elemento.toString()+"\n"+e.toString());
         	 }
          }
           if (include.containsKey("ruleURI")) {
        	try{
             NCLImportBase importado = new NCLImportBase();
             importado.setDocumentURI(new SrcType((String)include.get("ruleURI")));
             importado.setAlias((String)include.get("alias"));
             this.InterpretaRegra.getBase().addImportBase(importado);
        	}
        	catch(XMLException e){
         		throw new XMLException("Problemas na leitura e/ou interpretação da base de regras\n"+elemento.toString()+"\n"+e.toString());
         	}
          }
           if (include.containsKey("connectorURI")) {
        	try{
             NCLImportBase importado = new NCLImportBase();
             importado.setDocumentURI(new SrcType((String)include.get("connectorURI")));
             importado.setAlias((String)include.get("alias"));
             this.InterpretaConnector.getBase().addImportBase(importado);
        	}
        	catch(XMLException e){
         		throw new XMLException("Problemas na leitura e/ou interpretação da base de conectores\n"+elemento.toString()+"\n"+e.toString());
         	 }
          }
           if (include.containsKey("transitionURI")) {
        	try{
             NCLImportBase importado = new NCLImportBase();
             importado.setDocumentURI(new SrcType((String)include.get("transitionURI")));
             importado.setAlias((String)include.get("alias"));
             this.InterpretaTransicao.getBase().addImportBase(importado);
        	}
        	catch(XMLException e){
         		throw new XMLException("Problemas na leitura e/ou interpretação da base de transições:"+elemento.toString()+"\n"+e.toString());
         	}
          }
        }
      }
      if (elemento.containsKey("transtion")) {
	   try{
         if (imported.booleanValue()) {
           if (this.Head.getTransitionBase().getTransitions().get((String)((JSONObject)elemento.get("transtion")).get("id")) != null)
           {
             this.InterpretaTransicao.Add((JSONObject)elemento.get("transtion"));
           }
        }
        else
           this.InterpretaTransicao.Add((JSONObject)elemento.get("transtion"));
	   }
   	   catch(XMLException e){
    		throw new XMLException("Problemas na leitura e/ou interpretação da transição:"+((JSONObject)elemento.get("transition")).get("id")+"\n"+e.toString());
       }
      }
      else if ((elemento.containsKey("descriptor")) || (elemento.containsKey("descriptorSwitch"))) {
         if (imported.booleanValue()) {
           if (this.Head.getDescriptorBase().getDescriptor((String)((JSONObject)elemento.get("descriptor")).get("id")) != null)
             descritores.add(elemento);
         }
         else
           descritores.add(elemento);
      }
      else if (elemento.containsKey("region")) {
    	try{
         if (imported.booleanValue()) {
           if (this.InterpretadorRegiao.Bases.findRegion((String)((JSONObject)elemento.get("transtion")).get("id")) == null)
             this.InterpretadorRegiao.Add((JSONObject)elemento.get("region"));
         }
         else
           this.InterpretadorRegiao.Add((JSONObject)elemento.get("region"));
    	}
        catch(XMLException e){
     		throw new XMLException("Problemas na leitura e/ou interpretação da região:"+((JSONObject)elemento.get("region")).get("id")+"\n"+e.toString());
        }
      }
      else if (elemento.containsKey("meta")) {
    	try{
         this.Head.addMeta(JNSMetaInterpreter.InterMeta((JSONObject)elemento.get("meta")));
    	}
    	catch(XMLException e){
     		throw new XMLException("Problemas na leitura e/ou interpretação da meta\n"+elemento.toString()+"\n"+e.toString());
        }
      }
      else if (elemento.containsKey("metadata")) {
    	try{
         this.Head.addMetadata(JNSMetaInterpreter.InterMetadata((String)elemento.get("metadata")));
    	}
    	catch(XMLException e){
     		throw new XMLException("Problemas na leitura e/ou interpretação da metadata\n"+elemento.toString()+"\n"+e.toString());
        }
      }
      else if (elemento.containsKey("rule")) {
       try{
        if (imported.booleanValue()) {
           if (this.Head.getRuleBase().getRule((String)((JSONObject)elemento.get("transtion")).get("id")) == null)
             this.InterpretaRegra.Add((JSONObject)elemento.get("rule"));
        }
        else
           this.InterpretaRegra.Add((JSONObject)elemento.get("rule"));
       }
       catch(XMLException e){
    		throw new XMLException("Problemas na leitura e/ou interpretação da regra:"+((JSONObject)elemento.get("rule")).get("id")+"\n"+e.toString());
       }
      }
      else if (elemento.containsKey("connector")) {
       try{
        if (imported.booleanValue()) {
           if (this.Head.getConnectorBase().getCausalConnector((String)((JSONObject)elemento.get("transtion")).get("id")) == null)
             this.InterpretaConnector.Add((JSONObject)elemento.get("connector"));
        }
        else {
           this.InterpretaConnector.Add((JSONObject)elemento.get("connector"));
        }
      }
      catch(XMLException e){
    		throw new XMLException("Problemas na leitura e/ou interpretação do conector:"+((JSONObject)elemento.get("transition")).get("id")+"\n"+e.toString());
      }
     }
    }
    int i;
    for (i = 0; i < descritores.size(); i++) {
       JSONObject elemento = (JSONObject)descritores.get(i);
       if (elemento.containsKey("descriptor")) {
    	 try{
    		 this.InterpretadorDescritor.Add((JSONObject)elemento.get("descriptor"));
    	 }
    	 catch(XMLException e){
     		throw new XMLException("Problemas na leitura e/ou interpretação do desctitor:"+((JSONObject)elemento.get("descriptor")).get("id")+"\n"+e.toString());
    	 }
      }
       else if (elemento.containsKey("descriptorSwitch")) {
    	 try{
    		 this.InterpretadorDescritor.AddSwitc((JSONObject)elemento.get("descriptorSwitch"));
    	 }
    	 catch(XMLException e){
      		throw new XMLException("Problemas na leitura e/ou interpretação do descriptorSwitch:"+((JSONObject)elemento.get("descriptorSwitch")).get("id")+"\n"+e.toString());
     	 }
      }
    }

    for (i = 0; i < headers.size(); i++) {
      try{
       Interprets((JSONArray)headers.get(i), Boolean.valueOf(true));
      }
      catch(XMLException e){
    		throw new XMLException("Problemas na leitura e/ou interpretação do header incluido:"+((JSONArray)headers.get(i)).toString()+"\n"+e.toString());
   	 }
    }

     if (this.InterpretaTransicao.getBase().hasTransition())
       this.Head.setTransitionBase(this.InterpretaTransicao.getBase());
     this.InterpretadorRegiao.AdicionaRegioesFaltantes();
     return this.Head;
  }

  public void InsertBases() throws XMLException
  {
     this.InterpretadorDescritor.ReInterprets();
     if (this.InterpretadorDescritor.getBase().hasDescriptor())
       this.Head.setDescriptorBase(this.InterpretadorDescritor.getBase());
     if (this.InterpretaRegra.getBase().hasRule())
       this.Head.setRuleBase(this.InterpretaRegra.getBase());
     if (this.InterpretaConnector.getBase().hasCausalConnector())
       this.Head.setConnectorBase(this.InterpretaConnector.getBase());
  }
}