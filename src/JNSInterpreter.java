 import br.uff.midiacom.ana.NCLDoc;
 import br.uff.midiacom.ana.NCLHead;
 import br.uff.midiacom.ana.util.enums.NCLNamespace;
 import br.uff.midiacom.ana.util.exception.XMLException;
 import br.uff.midiacom.ana.util.modification.NCLModificationNotifier;
 import java.util.Vector;
 import org.json.simple.JSONArray;
 import org.json.simple.JSONObject;
 import org.json.simple.parser.ParseException;
 
 public class JNSInterpreter
 {
   private Vector VetorDeAuxiliarSwitRule;
   private JNSRuleInterpreter InterpretaRegra;
 
   JNSInterpreter()
   {
     this.VetorDeAuxiliarSwitRule = new Vector();
   }
 
   public String InterpretsJNS(JSONObject jsn)
     throws XMLException, ParseException, InterruptedException
   {
     NCLDoc docN = new NCLDoc();
 
     JSONObject jsonNCL = (JSONObject)jsn.get("ncl");
     String valor = (String)jsonNCL.get("id");
     if (valor != null)
       docN.setId(valor);
     else
       docN.setId("jnsNCL");
     valor = (String)jsonNCL.get("title");
     if (valor != null)
       docN.setId(valor);
     docN.setXmlns(NCLNamespace.EDTV);
 
     JSONArray jsonHead = (JSONArray)jsonNCL.get("head");
     fillVars(jsonNCL);
     JNSHeadInterpreter interpretadorHead = new JNSHeadInterpreter();
     this.InterpretaRegra = new JNSRuleInterpreter(this.VetorDeAuxiliarSwitRule);
     interpretadorHead.setInterpretaRegra(this.InterpretaRegra);
     NCLHead cabecalho = interpretadorHead.Interprets(jsonHead);
     docN.setHead(cabecalho);
     JSONArray jsonBody = (JSONArray)jsonNCL.get("body");
     JNSBodyInterpreter intepletaCorpo = new JNSBodyInterpreter(interpretadorHead);
     docN.setBody(intepletaCorpo.Interprets(jsonBody));
     interpretadorHead.InsertBases();
     docN.setHead(cabecalho);
     String stringRetorno = docN.parse(0);
     NCLModificationNotifier.getInstance().finish();
     return stringRetorno;
   }
 
   void fillVars(JSONObject jsonNCL)
   {
     JSONArray head = (JSONArray)jsonNCL.get("head");
     if (head == null)
       return;
     int i = 0;
     for (i = 0; i < head.size(); i++) {
       JSONObject elemento = (JSONObject)head.get(i);
       if (elemento.containsKey("descriptorSwitch")) {
         elemento = (JSONObject)elemento.get("descriptorSwitch");
         fillAuxVector(elemento);
       }
     }
     JSONArray body = (JSONArray)jsonNCL.get("body");
     searchInContext(body);
   }
 
   void searchInContext(JSONArray contexto) {
     int i = 0;
     for (i = 0; i < contexto.size(); i++) {
       JSONObject elemento = (JSONObject)contexto.get(i);
       if (elemento.containsKey("switch")) {
         elemento = (JSONObject)elemento.get("switch");
         fillAuxVector(elemento);
       }
       else if (elemento.containsKey("context")) {
         searchInContext((JSONArray)elemento.get("context"));
       }
     }
   }
 
   private void fillAuxVector(JSONObject elemento) {
     if (elemento.containsKey("vars")) {
       JSONArray vars = (JSONArray)elemento.get("vars");
       int j = 0;
       Vector variaveisR = new Vector();
       Vector variaveisN = new Vector();
       for (j = 0; j < vars.size(); j++) {
         String key = JNSjSONComplements.getKey(((JSONObject)vars.get(j)).toString());
         variaveisR.add(key);
         variaveisN.add((String)((JSONObject)vars.get(j)).get(key));
       }
       String[] keys = JNSjSONComplements.getKeys(elemento);
       for (j = 0; j < keys.length; j++)
         if ((!keys[j].contains("=")) && (!keys[j].contentEquals("default")) && (!keys[j].contentEquals("refer")) && (!keys[j].contentEquals("id")) && (!keys[j].contentEquals("vars")) && (!keys[j].contains("<")) && (!keys[j].contains(">"))) {
           Vector vetor = new Vector();
           vetor.add(elemento.get("id"));
           vetor.add(keys[j]);
           vetor.add(variaveisR);
           vetor.add(variaveisN);
           this.VetorDeAuxiliarSwitRule.add(vetor);
         }
     }
   }
 }

/* Location:           C:\Users\Convidado\Desktop\pacoteCompleto\jnsTranslator.jar
 * Qualified Name:     JNSInterpreter
 * JD-Core Version:    0.6.2
 */