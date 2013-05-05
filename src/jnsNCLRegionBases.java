/*    */ import br.uff.midiacom.ana.NCLHead;
/*    */ import br.uff.midiacom.ana.region.NCLRegion;
/*    */ import br.uff.midiacom.ana.region.NCLRegionBase;
import br.uff.midiacom.ana.util.enums.NCLDevice;
/*    */ import br.uff.midiacom.ana.util.exception.XMLException;
import java.util.Vector;
/*    */ 
/*    */ public class jnsNCLRegionBases
/*    */ {
/*    */   private Vector<NCLRegionBase> bases;
/*    */   private NCLHead head;
/*    */ 
/*    */   jnsNCLRegionBases(NCLHead head)
/*    */   {
/* 15 */     this.head = head;
/* 16 */     this.bases = new Vector();
/*    */   }
/*    */   public NCLRegionBase[] getBases() {
/* 19 */     NCLRegionBase[] retorno = new NCLRegionBase[this.bases.size()];
/* 20 */     this.bases.toArray(retorno);
/* 21 */     return retorno;
/*    */   }
/*    */ 
/*    */   public void addRegionBase(NCLRegionBase regionBase) throws XMLException {
/* 25 */     this.bases.add(regionBase);
/* 26 */     this.head.addRegionBase(regionBase);
/*    */   }
/*    */   public Object findRegion(String string) throws XMLException {
/* 29 */     return findRegion(string, null);
/*    */   }
/*    */ 
/*    */   public Object findRegion(String string, String device) throws XMLException {
/* 33 */     NCLRegionBase[] basesNCL = getBases();
/* 34 */     Object retorno = null;
/* 35 */     for (int j = 0; j < basesNCL.length; j++) {
/* 36 */       if (((device != null) && (basesNCL[j].getDevice().equals(device))) || ((device == null) && (basesNCL[j].getDevice() == null)))
/* 37 */         retorno = basesNCL[j].getRegion(string);
/* 38 */       if (retorno != null)
/* 39 */         return retorno;
/* 40 */       if (string.contains("#")) {
/* 41 */         retorno = basesNCL[j].findRegion(string);
/* 42 */         if (retorno != null)
/* 43 */           return retorno;
/*    */       }
/*    */     }
/* 46 */     return null;
/*    */   }
/*    */   public void addRegion(NCLRegion regiao, String stringDevice) throws XMLException {
/* 49 */     NCLRegionBase regiaoDestino = null;
/* 50 */     NCLRegionBase[] basesNCL = getBases();
/*    */ 
/* 52 */     for (int j = 0; j < basesNCL.length; j++) {
/* 53 */       if (stringDevice == null) {
/* 54 */         if (basesNCL[j].getDevice() == null) {
/* 55 */           regiaoDestino = basesNCL[j];
/*    */         }
/*    */ 
/*    */       }
/* 59 */       else if ((basesNCL[j].getDevice() != null) && (basesNCL[j].getDevice().equals(stringDevice))) {
/* 60 */         regiaoDestino = basesNCL[j];
/*    */       }
/*    */     }
/*    */ 
/* 64 */     if (regiaoDestino == null) {
/* 65 */       regiaoDestino = new NCLRegionBase();
/* 66 */       if (stringDevice != null)
/* 67 */         regiaoDestino.setDevice(new NCLDevice(stringDevice));
/* 68 */       this.bases.add(regiaoDestino);
/* 69 */       this.head.addRegionBase(regiaoDestino);
/*    */     }
/* 71 */     regiaoDestino.addRegion(regiao);
/*    */   }
/*    */ 
/*    */   public void addRegion(NCLRegion regiao) throws XMLException
/*    */   {
/* 76 */     addRegion(regiao, null);
/*    */   }
/*    */ 
/*    */   public NCLRegionBase findBaseWithRegion(String region) throws XMLException {
/* 80 */     NCLRegionBase[] basesNCL = getBases();
/* 81 */     NCLRegion retorno = null;
/* 82 */     for (int j = 0; j < basesNCL.length; j++) {
/* 83 */       retorno = basesNCL[j].getRegion(region);
/* 84 */       if (retorno != null)
/* 85 */         return basesNCL[j];
/*    */     }
/* 87 */     return null;
/*    */   }
/*    */ }

/* Location:           C:\Users\Convidado\Desktop\pacoteCompleto\jnsTranslator.jar
 * Qualified Name:     jnsNCLRegionBases
 * JD-Core Version:    0.6.2
 */