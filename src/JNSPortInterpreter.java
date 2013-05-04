/*    */ import br.uff.midiacom.ana.NCLBody;
/*    */ import br.uff.midiacom.ana.interfaces.NCLInterface;
/*    */ import br.uff.midiacom.ana.interfaces.NCLPort;
/*    */ import br.uff.midiacom.ana.node.NCLContext;
/*    */ import br.uff.midiacom.ana.node.NCLNode;
/*    */ import br.uff.midiacom.ana.node.NCLSwitch;
/*    */ import br.uff.midiacom.ana.util.exception.XMLException;
/*    */ import org.json.simple.JSONObject;
/*    */ 
/*    */ public class JNSPortInterpreter
/*    */ {
/*    */   public static void Interprets(JSONObject port, Object contexto)
/*    */     throws XMLException
/*    */   {
/* 17 */     String[] nomes = JNSjSONComplements.getKeys(port);
/* 18 */     int i = 0;
/* 19 */     for (i = 0; i < nomes.length; i++) {
/* 20 */       String nome = nomes[i];
/* 21 */       String inter = null;
/* 22 */       NCLPort porta = new NCLPort(nome);
/* 23 */       nome = (String)port.get(nome);
/* 24 */       if (nome.contains(".")) {
/* 25 */         inter = nome.substring(nome.indexOf('.') + 1);
/* 26 */         nome = nome.substring(0, nome.indexOf('.'));
/*    */       }
/* 28 */       NCLNode componente = null;
/* 29 */       NCLInterface nclInterface = null;
/* 30 */       if ((contexto instanceof NCLContext))
/* 31 */         componente = ((NCLContext)contexto).findNode(nome);
/* 32 */       else if ((contexto instanceof NCLBody))
/* 33 */         componente = ((NCLBody)contexto).findNode(nome);
/*    */       else
/* 35 */         componente = ((NCLSwitch)contexto).findNode(nome);
/*    */       try {
/* 37 */         porta.setComponent(componente);
/*    */       }
/*    */       catch (XMLException ex) {
/* 40 */         throw new XMLException("Null component." + nome);
/*    */       }
/* 42 */       if (inter != null) {
/* 43 */         porta.setInterface(JNSaNaComplements.FindInterface(inter, componente));
/*    */       }
/* 45 */       if ((contexto instanceof NCLBody))
/* 46 */         ((NCLBody)contexto).addPort(porta);
/*    */       else
/* 48 */         ((NCLContext)contexto).addPort(porta);
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Convidado\Desktop\pacoteCompleto\jnsTranslator.jar
 * Qualified Name:     JNSPortInterpreter
 * JD-Core Version:    0.6.2
 */