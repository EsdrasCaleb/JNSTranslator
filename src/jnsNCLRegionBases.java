 import br.uff.midiacom.ana.NCLHead;
 import br.uff.midiacom.ana.region.NCLRegion;
 import br.uff.midiacom.ana.region.NCLRegionBase;
import br.uff.midiacom.ana.util.enums.NCLDevice;
 import br.uff.midiacom.ana.util.exception.XMLException;
import java.util.Vector;
 
 public class jnsNCLRegionBases
 {
   private Vector<NCLRegionBase> bases;
   private NCLHead head;
 
   jnsNCLRegionBases(NCLHead head)
   {
     this.head = head;
     this.bases = new Vector();
   }
   public NCLRegionBase[] getBases() {
     NCLRegionBase[] retorno = new NCLRegionBase[this.bases.size()];
     this.bases.toArray(retorno);
     return retorno;
   }
 
   public void addRegionBase(NCLRegionBase regionBase) throws XMLException {
     this.bases.add(regionBase);
     this.head.addRegionBase(regionBase);
   }
   public Object findRegion(String string) throws XMLException {
     return findRegion(string, null);
   }
 
   public Object findRegion(String string, String device) throws XMLException {
     NCLRegionBase[] basesNCL = getBases();
     Object retorno = null;
     for (int j = 0; j < basesNCL.length; j++) {
       if (((device != null) && (basesNCL[j].getDevice().equals(device))) || ((device == null) && (basesNCL[j].getDevice() == null)))
         retorno = basesNCL[j].getRegion(string);
       if (retorno != null)
         return retorno;
       if (string.contains("#")) {
         retorno = basesNCL[j].findRegion(string);
         if (retorno != null)
           return retorno;
       }
     }
     return null;
   }
   public void addRegion(NCLRegion regiao, String stringDevice) throws XMLException {
     NCLRegionBase regiaoDestino = null;
     NCLRegionBase[] basesNCL = getBases();
 
     for (int j = 0; j < basesNCL.length; j++) {
       if (stringDevice == null) {
         if (basesNCL[j].getDevice() == null) {
           regiaoDestino = basesNCL[j];
         }
 
       }
       else if ((basesNCL[j].getDevice() != null) && (basesNCL[j].getDevice().equals(stringDevice))) {
         regiaoDestino = basesNCL[j];
       }
     }
 
     if (regiaoDestino == null) {
       regiaoDestino = new NCLRegionBase();
       if (stringDevice != null)
         regiaoDestino.setDevice(new NCLDevice(stringDevice));
       this.bases.add(regiaoDestino);
       this.head.addRegionBase(regiaoDestino);
     }
     regiaoDestino.addRegion(regiao);
   }
 
   public void addRegion(NCLRegion regiao) throws XMLException
   {
     addRegion(regiao, null);
   }
 
   public NCLRegionBase findBaseWithRegion(String region) throws XMLException {
     NCLRegionBase[] basesNCL = getBases();
     NCLRegion retorno = null;
     for (int j = 0; j < basesNCL.length; j++) {
       retorno = basesNCL[j].getRegion(region);
       if (retorno != null)
         return basesNCL[j];
     }
     return null;
   }
 }

/* Location:           C:\Users\Convidado\Desktop\pacoteCompleto\jnsTranslator.jar
 * Qualified Name:     jnsNCLRegionBases
 * JD-Core Version:    0.6.2
 */