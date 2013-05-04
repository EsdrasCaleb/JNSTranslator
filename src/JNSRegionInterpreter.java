/*    */ import br.uff.midiacom.ana.NCLHead;
/*    */ import br.uff.midiacom.ana.region.NCLRegion;
/*    */ import br.uff.midiacom.ana.region.NCLRegionBase;
/*    */ import br.uff.midiacom.ana.reuse.NCLImportBase;
/*    */ import br.uff.midiacom.ana.util.exception.XMLException;
/*    */ import java.util.Vector;
/*    */ import org.json.simple.JSONObject;
/*    */ 
/*    */ public class JNSRegionInterpreter
/*    */ {
/*    */   protected jnsNCLRegionBases Bases;
/*    */   private Vector regionsToAdd;
/*    */ 
/*    */   JNSRegionInterpreter(NCLHead head)
/*    */     throws XMLException
/*    */   {
/* 17 */     this.Bases = new jnsNCLRegionBases(head);
/* 18 */     this.regionsToAdd = new Vector();
/*    */   }
/*    */   jnsNCLRegionBases getBase() {
/* 21 */     return this.Bases;
/*    */   }
/*    */   NCLRegionBase[] getBases() throws XMLException {
/* 24 */     return this.Bases.getBases();
/*    */   }
/*    */ 
/*    */   NCLRegion Add(JSONObject elemento) throws XMLException {
/* 28 */     NCLRegion regiao = new NCLRegion((String)elemento.get("id"));
/*    */ 
/* 31 */     String valor = "";
/* 32 */     if (elemento.containsKey("title")) {
/* 33 */       regiao.setTitle((String)elemento.get("title"));
/*    */     }
/* 35 */     if (elemento.containsKey("left")) {
/* 36 */       regiao.setLeft(elemento.get("left").toString());
/*    */     }
/* 38 */     if (elemento.containsKey("right")) {
/* 39 */       regiao.setRight(elemento.get("right").toString());
/*    */     }
/* 41 */     if (elemento.containsKey("top")) {
/* 42 */       regiao.setTop(elemento.get("top").toString());
/*    */     }
/* 44 */     if (elemento.containsKey("bottom")) {
/* 45 */       regiao.setBottom(elemento.get("bottom").toString());
/*    */     }
/* 47 */     if (elemento.containsKey("width")) {
/* 48 */       regiao.setWidth(elemento.get("width").toString());
/*    */     }
/* 50 */     if (elemento.containsKey("height")) {
/* 51 */       regiao.setHeight(elemento.get("height").toString());
/*    */     }
/* 53 */     if (elemento.containsKey("zIndex")) {
/* 54 */       regiao.setzIndex(Integer.valueOf(Integer.parseInt(elemento.get("zIndex").toString())));
/*    */     }
/* 56 */     if (elemento.containsKey("parent")) {
/* 57 */       String idPai = (String)elemento.get("parent");
/* 58 */       String device = null;
/* 59 */       if (elemento.containsKey("device")) {
/* 60 */         device = (String)elemento.get("device");
/*    */       }
/* 62 */       NCLRegion aux = (NCLRegion)this.Bases.findRegion(idPai, device);
/* 63 */       if (aux != null) {
/* 64 */         aux.addRegion(regiao);
/*    */       }
/*    */       else {
/* 67 */         this.regionsToAdd.add(elemento);
/* 68 */         return null;
/*    */       }
/*    */ 
/*    */     }
/* 73 */     else if (elemento.containsKey("device")) {
/* 74 */       this.Bases.addRegion(regiao, (String)elemento.get("device"));
/*    */     }
/*    */     else {
/* 77 */       this.Bases.addRegion(regiao);
/*    */     }
/*    */ 
/* 80 */     return regiao;
/*    */   }
/*    */ 
/*    */   public void AdicionaRegioesFaltantes() throws XMLException {
/* 84 */     while (this.regionsToAdd.size() > 0)
/* 85 */       Add((JSONObject)this.regionsToAdd.remove(0));
/*    */   }
/*    */ 
/*    */   public void addImportBase(NCLImportBase importado) throws XMLException
/*    */   {
/* 90 */     NCLRegionBase baseNova = new NCLRegionBase();
/* 91 */     baseNova.addImportBase(importado);
/* 92 */     this.Bases.addRegionBase(baseNova);
/*    */   }
/*    */ }

/* Location:           C:\Users\Convidado\Desktop\pacoteCompleto\jnsTranslator.jar
 * Qualified Name:     JNSRegionInterpreter
 * JD-Core Version:    0.6.2
 */