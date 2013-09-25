 import br.uff.midiacom.ana.NCLHead;
 import br.uff.midiacom.ana.region.NCLRegion;
 import br.uff.midiacom.ana.region.NCLRegionBase;
 import br.uff.midiacom.ana.reuse.NCLImportBase;
 import br.uff.midiacom.ana.util.exception.XMLException;
 import java.util.Vector;
 import org.json.simple.JSONObject;
 
 public class JNSRegionInterpreter
 {
   protected jnsNCLRegionBases Bases;
   private Vector regionsToAdd;
 
   JNSRegionInterpreter(NCLHead head)
     throws XMLException
   {
     this.Bases = new jnsNCLRegionBases(head);
     this.regionsToAdd = new Vector();
   }
   jnsNCLRegionBases getBase() {
     return this.Bases;
   }
   NCLRegionBase[] getBases() throws XMLException {
     return this.Bases.getBases();
   }
 
   NCLRegion Add(JSONObject elemento) throws XMLException {
     NCLRegion regiao = new NCLRegion((String)elemento.get("id"));
 
     String valor = "";
     if (elemento.containsKey("title")) {
       regiao.setTitle((String)elemento.get("title"));
     }
     if (elemento.containsKey("left")) {
       regiao.setLeft(elemento.get("left").toString());
     }
     if (elemento.containsKey("right")) {
       regiao.setRight(elemento.get("right").toString());
     }
     if (elemento.containsKey("top")) {
       regiao.setTop(elemento.get("top").toString());
     }
     if (elemento.containsKey("bottom")) {
       regiao.setBottom(elemento.get("bottom").toString());
     }
     if (elemento.containsKey("width")) {
       regiao.setWidth(elemento.get("width").toString());
     }
     if (elemento.containsKey("height")) {
       regiao.setHeight(elemento.get("height").toString());
     }
     if (elemento.containsKey("zIndex")) {
       regiao.setzIndex(Integer.valueOf(Integer.parseInt(elemento.get("zIndex").toString())));
     }
     if (elemento.containsKey("parent")) {
       String idPai = (String)elemento.get("parent");
       String device = null;
       if (elemento.containsKey("device")) {
         device = (String)elemento.get("device");
       }
       NCLRegion aux = (NCLRegion)this.Bases.findRegion(idPai, device);
       if (aux != null) {
         aux.addRegion(regiao);
       }
       else {
         this.regionsToAdd.add(elemento);
         return null;
       }
 
     }
     else if (elemento.containsKey("device")) {
       this.Bases.addRegion(regiao, (String)elemento.get("device"));
     }
     else {
       this.Bases.addRegion(regiao);
     }
 
     return regiao;
   }
 
   public void AdicionaRegioesFaltantes() throws XMLException {
     while (this.regionsToAdd.size() > 0)
       Add((JSONObject)this.regionsToAdd.remove(0));
   }
 
   public void addImportBase(NCLImportBase importado) throws XMLException
   {
     NCLRegionBase baseNova = new NCLRegionBase();
     baseNova.addImportBase(importado);
     this.Bases.addRegionBase(baseNova);
   }
 }

/* Location:           C:\Users\Convidado\Desktop\pacoteCompleto\jnsTranslator.jar
 * Qualified Name:     JNSRegionInterpreter
 * JD-Core Version:    0.6.2
 */