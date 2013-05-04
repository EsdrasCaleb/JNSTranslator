/*     */ import br.uff.midiacom.ana.NCLBody;
/*     */ import br.uff.midiacom.ana.connector.NCLAction;
/*     */ import br.uff.midiacom.ana.connector.NCLAssessmentStatement;
/*     */ import br.uff.midiacom.ana.connector.NCLCausalConnector;
/*     */ import br.uff.midiacom.ana.connector.NCLCondition;
/*     */ import br.uff.midiacom.ana.connector.NCLConnectorBase;
/*     */ import br.uff.midiacom.ana.connector.NCLConnectorParam;
/*     */ import br.uff.midiacom.ana.connector.NCLRoleElement;
/*     */ import br.uff.midiacom.ana.connector.NCLSimpleCondition;
/*     */ import br.uff.midiacom.ana.descriptor.NCLDescriptor;
/*     */ import br.uff.midiacom.ana.descriptor.NCLDescriptorBase;
/*     */ import br.uff.midiacom.ana.descriptor.NCLDescriptorSwitch;
/*     */ import br.uff.midiacom.ana.interfaces.NCLInterface;
/*     */ import br.uff.midiacom.ana.link.NCLBind;
/*     */ import br.uff.midiacom.ana.link.NCLLink;
/*     */ import br.uff.midiacom.ana.link.NCLParam;
/*     */ import br.uff.midiacom.ana.node.NCLContext;
/*     */ import br.uff.midiacom.ana.node.NCLMedia;
/*     */ import br.uff.midiacom.ana.node.NCLNode;
/*     */ import br.uff.midiacom.ana.node.NCLSwitch;
/*     */ import br.uff.midiacom.ana.region.NCLRegion;
/*     */ import br.uff.midiacom.ana.region.NCLRegionBase;
/*     */ import br.uff.midiacom.ana.rule.NCLRule;
/*     */ import br.uff.midiacom.ana.transition.NCLTransition;
/*     */ import br.uff.midiacom.ana.transition.NCLTransitionBase;
/*     */ import br.uff.midiacom.ana.util.ElementList;
/*     */ import br.uff.midiacom.ana.util.enums.NCLComparator;
/*     */ import br.uff.midiacom.ana.util.enums.NCLConditionOperator;
/*     */ import br.uff.midiacom.ana.util.enums.NCLEventTransition;
/*     */ import br.uff.midiacom.ana.util.enums.NCLEventType;
/*     */ import br.uff.midiacom.ana.util.exception.NCLParsingException;
/*     */ import br.uff.midiacom.ana.util.exception.XMLException;
/*     */ import java.io.PrintStream;
/*     */ import java.util.Iterator;
/*     */ 
/*     */ public class JNSaNaComplements
/*     */ {
/*     */   static NCLInterface FindInterface(String id, Object no)
/*     */     throws XMLException
/*     */   {
/*  47 */     NCLInterface interfaces = null;
/*  48 */     if ((no instanceof NCLMedia)) {
/*  49 */       interfaces = ((NCLMedia)no).getArea(id);
/*  50 */       if (interfaces == null)
/*  51 */         interfaces = ((NCLMedia)no).getProperty(id);
/*     */     }
/*  53 */     else if ((no instanceof NCLContext)) {
/*  54 */       interfaces = ((NCLContext)no).getPort(id);
/*  55 */       if (interfaces == null)
/*  56 */         interfaces = ((NCLContext)no).getProperty(id);
/*     */     }
/*  58 */     return interfaces;
/*     */   }
/*     */ 
/*     */   static NCLNode FindNode(String id, Object corpo) throws XMLException {
/*     */     try {
/*  63 */       if ((corpo instanceof NCLBody))
/*  64 */         return ((NCLBody)corpo).findNode(id);
/*  65 */       if ((corpo instanceof NCLContext))
/*  66 */         return ((NCLContext)corpo).findNode(id);
/*  67 */       if ((corpo instanceof NCLSwitch))
/*  68 */         return ((NCLSwitch)corpo).findNode(id);
/*  69 */       return null;
/*     */     } catch (NCLParsingException ex) {
/*     */     }
/*  72 */     throw new NCLParsingException("Could not find document root element of " + id);
/*     */   }
/*     */ 
/*     */   static NCLTransition FindTrasition(String id, NCLTransitionBase bTransition)
/*     */   {
/*  77 */     Iterator transition = bTransition.getTransitions().iterator();
/*  78 */     while (transition.hasNext()) {
/*  79 */       NCLTransition aux = (NCLTransition)transition.next();
/*  80 */       if (aux.getId().equalsIgnoreCase(id)) {
/*  81 */         return aux;
/*     */       }
/*     */     }
/*  84 */     return null;
/*     */   }
/*     */ 
/*     */   static NCLDescriptor FindDescriptor(int focusIndex, NCLDescriptorBase bDescritores) {
/*  88 */     Iterator descritores = bDescritores.getDescriptors().iterator();
/*  89 */     while (descritores.hasNext()) {
/*  90 */       NCLDescriptor aux = (NCLDescriptor)descritores.next();
/*  91 */       if (aux.getFocusIndex().toString().compareTo(Integer.toString(focusIndex)) == 0) {
/*  92 */         return aux;
/*     */       }
/*     */     }
/*  95 */     return null;
/*     */   }
/*     */ 
/*     */   static NCLDescriptor FindDescriptor(String id, Object bDescritores) {
/*  99 */     Iterator descritores = null;
/* 100 */     if ((bDescritores instanceof NCLDescriptorBase))
/* 101 */       descritores = ((NCLDescriptorBase)bDescritores).getDescriptors().iterator();
/*     */     else
/* 103 */       descritores = ((NCLDescriptorSwitch)bDescritores).getDescriptors().iterator();
/* 104 */     while (descritores.hasNext()) {
/* 105 */       NCLDescriptor aux = (NCLDescriptor)descritores.next();
/* 106 */       if (aux.getId().equalsIgnoreCase(id)) {
/* 107 */         return aux;
/*     */       }
/*     */     }
/* 110 */     return null;
/*     */   }
/*     */ 
/*     */   static NCLRegion FindRegion(String id, Object bRegioes) {
/* 114 */     Iterator regioes = null;
/* 115 */     if ((bRegioes instanceof NCLRegionBase))
/* 116 */       regioes = ((NCLRegionBase)bRegioes).getRegions().iterator();
/*     */     else
/* 118 */       regioes = ((NCLRegion)bRegioes).getRegions().iterator();
/* 119 */     while (regioes.hasNext()) {
/* 120 */       NCLRegion aux = (NCLRegion)regioes.next();
/* 121 */       if (aux.getId().equalsIgnoreCase(id)) {
/* 122 */         return aux;
/*     */       }
/*     */ 
/* 125 */       aux = FindRegion(id, aux);
/* 126 */       if (aux != null) {
/* 127 */         return aux;
/*     */       }
/*     */     }
/* 130 */     return null;
/*     */   }
/*     */ 
/*     */   static NCLRoleElement FindRole(String role, NCLCausalConnector connector) {
/* 134 */     NCLRoleElement returnRole = connector.getCondition().findRole(role);
/* 135 */     if (returnRole == null)
/* 136 */       returnRole = connector.getAction().findRole(role);
/* 137 */     if (returnRole == null)
/* 138 */       System.out.println("Erro role " + role + " no lugar errado");
/* 139 */     return returnRole;
/*     */   }
/*     */ 
/*     */   static NCLConnectorParam FindParameter(String nome, NCLCausalConnector connectorPai) {
/* 143 */     Iterator parametros = null;
/* 144 */     parametros = connectorPai.getConnectorParams().iterator();
/* 145 */     while (parametros.hasNext()) {
/* 146 */       NCLConnectorParam aux = (NCLConnectorParam)parametros.next();
/* 147 */       if (aux.getName().equalsIgnoreCase(nome)) {
/* 148 */         return aux;
/*     */       }
/*     */     }
/* 151 */     return null;
/*     */   }
/*     */ 
/*     */   static NCLParam FindParameter(String nome, NCLLink eloPai) {
/* 155 */     Iterator parametros = null;
/* 156 */     parametros = eloPai.getLinkParams().iterator();
/* 157 */     while (parametros.hasNext()) {
/* 158 */       NCLParam aux = (NCLParam)parametros.next();
/* 159 */       if (aux.getName().getName().equalsIgnoreCase(nome)) {
/* 160 */         return aux;
/*     */       }
/*     */     }
/* 163 */     return null;
/*     */   }
/*     */ 
/*     */   static NCLBind FindBind(String nome, NCLLink eloPai) {
/* 167 */     Iterator binds = null;
/* 168 */     binds = eloPai.getBinds().iterator();
/* 169 */     while (binds.hasNext()) {
/* 170 */       NCLBind aux = (NCLBind)binds.next();
/* 171 */       if (((NCLConnectorParam)aux.getRole()).getName().equalsIgnoreCase(nome)) {
/* 172 */         return aux;
/*     */       }
/*     */     }
/* 175 */     return null;
/*     */   }
/*     */ 
/*     */   static NCLCausalConnector FindConnector(String id, NCLConnectorBase bConectores) {
/* 179 */     Iterator conectors = null;
/* 180 */     conectors = bConectores.getCausalConnectors().iterator();
/* 181 */     while (conectors.hasNext()) {
/* 182 */       NCLCausalConnector aux = (NCLCausalConnector)conectors.next();
/* 183 */       if (aux.getId().equalsIgnoreCase(id)) {
/* 184 */         return aux;
/*     */       }
/*     */     }
/* 187 */     return null;
/*     */   }
/*     */ 
/*     */   public static NCLCondition adicionaParametrosExtas(NCLCondition codicaoRetorno, String[] parametrosExtras) throws XMLException
/*     */   {
/* 192 */     for (int i = 0; i < parametrosExtras.length; i++) {
/* 193 */       parametrosExtras[i] = parametrosExtras[i].trim();
/* 194 */       if (parametrosExtras[i].contains("delay")) {
/* 195 */         parametrosExtras[i] = parametrosExtras[i].trim();
/* 196 */         String delay = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"") + 1, parametrosExtras[i].length()).trim();
/* 197 */         delay = delay.substring(0, delay.indexOf('"'));
/* 198 */         codicaoRetorno.setDelay(delay);
/*     */       }
/* 200 */       else if (parametrosExtras[i].contains("eventtype")) {
/* 201 */         String tipo = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"") + 1, parametrosExtras[i].length()).trim();
/* 202 */         tipo = tipo.substring(0, tipo.indexOf('"'));
/* 203 */         ((NCLSimpleCondition)codicaoRetorno).setEventType(NCLEventType.valueOf(tipo.toUpperCase()));
/*     */       }
/* 205 */       else if (parametrosExtras[i].contains("key")) {
/* 206 */         String key = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"") + 1, parametrosExtras[i].length()).trim();
/* 207 */         key = key.substring(0, key.indexOf('"'));
/* 208 */         ((NCLSimpleCondition)codicaoRetorno).setKey(key);
/*     */       }
/* 210 */       else if (parametrosExtras[i].contains("transition")) {
/* 211 */         String transition = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"") + 1, parametrosExtras[i].length()).trim();
/* 212 */         transition = transition.substring(0, transition.indexOf('"'));
/* 213 */         ((NCLSimpleCondition)codicaoRetorno).setTransition(NCLEventTransition.getEnumType(transition.toUpperCase()));
/*     */       }
/* 215 */       else if (parametrosExtras[i].contains("min")) {
/* 216 */         String min = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"") + 1, parametrosExtras[i].length()).trim();
/* 217 */         min = min.substring(0, min.indexOf('"'));
/* 218 */         ((NCLSimpleCondition)codicaoRetorno).setMin(Integer.valueOf(Integer.parseInt(min)));
/*     */       }
/* 220 */       else if (parametrosExtras[i].contains("max")) {
/* 221 */         String max = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"") + 1, parametrosExtras[i].length()).trim();
/* 222 */         max = max.substring(0, max.indexOf('"'));
/* 223 */         ((NCLSimpleCondition)codicaoRetorno).setMax(max);
/*     */       }
/* 225 */       else if (parametrosExtras[i].contains("qualifier")) {
/* 226 */         String qual = parametrosExtras[i].substring(parametrosExtras[i].indexOf("\"") + 1, parametrosExtras[i].length()).trim();
/* 227 */         qual = qual.substring(0, qual.indexOf('"'));
/* 228 */         ((NCLSimpleCondition)codicaoRetorno).setQualifier(NCLConditionOperator.valueOf(qual.toUpperCase()));
/*     */       }
/*     */     }
/* 231 */     return codicaoRetorno;
/*     */   }
/*     */ 
/*     */   public static String InterpretaComparador(String Expr, Object pai) throws XMLException {
/* 235 */     NCLComparator oComparador = null;
/* 236 */     String comparador = null;
/* 237 */     if (Expr.toLowerCase().contains("==")) {
/* 238 */       comparador = "==";
/* 239 */       oComparador = NCLComparator.EQ;
/*     */     }
/* 241 */     else if (Expr.toLowerCase().contains("!=")) {
/* 242 */       comparador = "!=";
/* 243 */       oComparador = NCLComparator.NE;
/*     */     }
/* 245 */     else if (Expr.toLowerCase().contains(">=")) {
/* 246 */       comparador = ">=";
/* 247 */       oComparador = NCLComparator.GTE;
/*     */     }
/* 249 */     else if (Expr.toLowerCase().contains("<=")) {
/* 250 */       comparador = "<=";
/* 251 */       oComparador = NCLComparator.LTE;
/*     */     }
/* 253 */     else if (Expr.toLowerCase().contains(">")) {
/* 254 */       comparador = ">";
/* 255 */       oComparador = NCLComparator.GT;
/*     */     }
/* 257 */     else if (Expr.toLowerCase().contains("<")) {
/* 258 */       comparador = "<";
/* 259 */       oComparador = NCLComparator.LT;
/*     */     }
/* 261 */     else if (Expr.toLowerCase().contains(" eq ")) {
/* 262 */       comparador = " eq ";
/* 263 */       oComparador = NCLComparator.EQ;
/*     */     }
/* 265 */     else if (Expr.toLowerCase().contains(" ne ")) {
/* 266 */       comparador = " ne ";
/* 267 */       oComparador = NCLComparator.NE;
/*     */     }
/* 269 */     else if (Expr.toLowerCase().contains(" gte ")) {
/* 270 */       comparador = " gte ";
/* 271 */       oComparador = NCLComparator.GTE;
/*     */     }
/* 273 */     else if (Expr.toLowerCase().contains(" lte ")) {
/* 274 */       comparador = " lte ";
/* 275 */       oComparador = NCLComparator.LTE;
/*     */     }
/* 277 */     else if (Expr.toLowerCase().contains(" gt ")) {
/* 278 */       comparador = " gt ";
/* 279 */       oComparador = NCLComparator.GT;
/*     */     }
/* 281 */     else if (Expr.toLowerCase().contains(" lt ")) {
/* 282 */       comparador = " lt ";
/* 283 */       oComparador = NCLComparator.LT;
/*     */     }
/* 285 */     if (comparador != null)
/* 286 */       if ((pai instanceof NCLRule))
/* 287 */         ((NCLRule)pai).setComparator(oComparador);
/* 288 */       else if ((pai instanceof NCLAssessmentStatement))
/* 289 */         ((NCLAssessmentStatement)pai).setComparator(oComparador);
/* 290 */     return comparador;
/*     */   }
/*     */ }

/* Location:           C:\Users\Convidado\Desktop\pacoteCompleto\jnsTranslator.jar
 * Qualified Name:     JNSaNaComplements
 * JD-Core Version:    0.6.2
 */