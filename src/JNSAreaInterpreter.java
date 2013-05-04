/*    */ import br.uff.midiacom.ana.interfaces.NCLArea;
/*    */ import br.uff.midiacom.ana.util.ArrayType;
/*    */ import br.uff.midiacom.ana.util.SampleType;
/*    */ import br.uff.midiacom.ana.util.TimeType;
/*    */ import br.uff.midiacom.ana.util.exception.XMLException;
/*    */ import org.json.simple.JSONObject;
/*    */ 
/*    */ public class JNSAreaInterpreter
/*    */ {
/*    */   static NCLArea Interprets(JSONObject jnsArea)
/*    */     throws XMLException
/*    */   {
/* 15 */     NCLArea area = new NCLArea((String)jnsArea.get("id"));
/* 16 */     if (jnsArea.containsKey("coords")) {
/* 17 */       String[] sCoords = ((String)jnsArea.get("coords")).split(",");
/* 18 */       double[] coords = new double[sCoords.length];
/*    */ 
/* 20 */       for (int i = 0; i < sCoords.length; i++)
/* 21 */         coords[i] = Integer.parseInt(sCoords[i]);
/* 22 */       area.setCoords(new ArrayType(coords));
/*    */     }
/* 24 */     if (jnsArea.containsKey("begin")) {
/* 25 */       area.setBegin(new TimeType((String)jnsArea.get("begin")));
/*    */     }
/* 27 */     if (jnsArea.containsKey("end")) {
/* 28 */       area.setEnd(new TimeType((String)jnsArea.get("end")));
/*    */     }
/* 30 */     if (jnsArea.containsKey("text")) {
/* 31 */       area.setText((String)jnsArea.get("text"));
/*    */     }
/* 33 */     if (jnsArea.containsKey("position")) {
/* 34 */       area.setPosition(Integer.valueOf(Integer.parseInt(jnsArea.get("position").toString())));
/*    */     }
/* 36 */     if (jnsArea.containsKey("first")) {
/* 37 */       area.setFirst(new SampleType((String)jnsArea.get("first")));
/*    */     }
/* 39 */     if (jnsArea.containsKey("last")) {
/* 40 */       area.setLast(new SampleType((String)jnsArea.get("last")));
/*    */     }
/* 42 */     if (jnsArea.containsKey("label")) {
/* 43 */       area.setLabel((String)jnsArea.get("label"));
/*    */     }
/* 45 */     jnsArea.containsKey("clip");
/*    */ 
/* 49 */     return area;
/*    */   }
/*    */ }

/* Location:           C:\Users\Convidado\Desktop\pacoteCompleto\jnsTranslator.jar
 * Qualified Name:     JNSAreaInterpreter
 * JD-Core Version:    0.6.2
 */