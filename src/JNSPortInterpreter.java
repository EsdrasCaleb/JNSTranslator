 import br.uff.midiacom.ana.NCLBody;
 import br.uff.midiacom.ana.interfaces.NCLInterface;
 import br.uff.midiacom.ana.interfaces.NCLPort;
 import br.uff.midiacom.ana.node.NCLContext;
 import br.uff.midiacom.ana.node.NCLNode;
 import br.uff.midiacom.ana.node.NCLSwitch;
 import br.uff.midiacom.ana.util.exception.XMLException;
 import org.json.simple.JSONObject;
 
 public class JNSPortInterpreter
 {
   public static void Interprets(JSONObject port, Object contexto)
     throws XMLException
   {
     String[] nomes = JNSjSONComplements.getKeys(port);
     int i = 0;
     for (i = 0; i < nomes.length; i++) {
       String nome = nomes[i];
       String inter = null;
       NCLPort porta = new NCLPort(nome);
       nome = (String)port.get(nome);
       if (nome.contains(".")) {
         inter = nome.substring(nome.indexOf('.') + 1);
         nome = nome.substring(0, nome.indexOf('.'));
       }
       NCLNode componente = null;
       NCLInterface nclInterface = null;
       if ((contexto instanceof NCLContext))
         componente = ((NCLContext)contexto).findNode(nome);
       else if ((contexto instanceof NCLBody))
         componente = ((NCLBody)contexto).findNode(nome);
       else
         componente = ((NCLSwitch)contexto).findNode(nome);
       try {
         porta.setComponent(componente);
       }
       catch (XMLException ex) {
         throw new XMLException("Null component." + nome);
       }
       if (inter != null) {
         porta.setInterface(JNSaNaComplements.FindInterface(inter, componente));
       }
       if ((contexto instanceof NCLBody))
         ((NCLBody)contexto).addPort(porta);
       else
         ((NCLContext)contexto).addPort(porta);
     }
   }
 }

/* Location:           C:\Users\Convidado\Desktop\pacoteCompleto\jnsTranslator.jar
 * Qualified Name:     JNSPortInterpreter
 * JD-Core Version:    0.6.2
 */