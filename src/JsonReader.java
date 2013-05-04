/*     */ import br.uff.midiacom.ana.util.exception.XMLException;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.FileReader;
/*     */ import java.io.IOException;
/*     */ import org.json.simple.JSONArray;
/*     */ import org.json.simple.JSONObject;
/*     */ import org.json.simple.parser.JSONParser;
/*     */ import org.json.simple.parser.ParseException;
/*     */ 
/*     */ public class JsonReader
/*     */ {
/*     */   public static JSONObject Read(String Path)
/*     */     throws XMLException
/*     */   {
/*  24 */     String sJson = ReadString(Path);
/*  25 */     sJson = markStrings(sJson.replaceAll("(?:/\\*(?:[^*]|(?:\\*+[^*/]))*\\*+/)|(?://.*)", ""));
/*  26 */     JSONParser parser = new JSONParser();
/*  27 */     JSONObject json = null;
/*     */     try {
/*  29 */       json = (JSONObject)parser.parse(sJson);
/*     */     }
/*     */     catch (ParseException ex) {
/*  32 */       int antes = ex.getPosition() - 5;
/*  33 */       int depois = ex.getPosition() + 5;
/*  34 */       if (antes < 0) {
/*  35 */         depois -= antes;
/*  36 */         antes = 0;
/*     */       }
/*  38 */       else if (depois > sJson.length()) {
/*  39 */         depois = sJson.length();
/*  40 */         antes = antes - depois - sJson.length();
/*     */       }
/*  42 */       throw new XMLException("Erro perto de " + sJson.substring(antes, depois) + " linha:" + getLine(sJson.substring(antes, depois), Path));
/*     */     }
/*  44 */     return json;
/*     */   }
/*     */ 
/*     */   private static String getLine(String substring, String path) {
/*  48 */     String sJson = "";
/*  49 */     int linha = 0;
/*     */     try {
/*  51 */       BufferedReader in = new BufferedReader(new FileReader(path));
/*     */ 
/*  53 */       while (in.ready()) {
/*  54 */         String str = in.readLine();
/*  55 */         sJson = sJson + str.replaceAll("//.*$", "");
/*  56 */         if (sJson.contains(substring))
/*  57 */           return Integer.toString(linha);
/*  58 */         linha++;
/*     */       }
/*  60 */       in.close();
/*     */     } catch (IOException e) {
/*  62 */       sJson = e.toString();
/*     */     }
/*  64 */     return "não identificada";
/*     */   }
/*     */ 
/*     */   public static JSONArray parse(String json) throws ParseException {
/*  68 */     json = markStrings(json);
/*  69 */     JSONParser parser = new JSONParser();
/*  70 */     JSONArray jsonR = (JSONArray)parser.parse(json);
/*  71 */     return jsonR;
/*     */   }
/*     */ 
/*     */   public static String ReadString(String Path) {
/*  75 */     String sJson = "";
/*     */     try {
/*  77 */       BufferedReader in = new BufferedReader(new FileReader(Path));
/*     */ 
/*  79 */       while (in.ready()) {
/*  80 */         String str = in.readLine();
/*  81 */         sJson = sJson + str.replaceAll("//.*$", "");
/*     */       }
/*  83 */       in.close();
/*     */     } catch (IOException e) {
/*  85 */       sJson = e.toString();
/*     */     }
/*  87 */     return sJson;
/*     */   }
/*     */ 
/*     */   private static String markStrings(String sJson) {
/*  91 */     String[] substitutos = { "subtype", "include", "ncl", "id", "head", "body", "region", "title", "parent", "descriptor", "descriptorSwitch", "link", 
/*  92 */       "rule", "transition", "connector", "meta", "metadata", "media", "property", "height", "instance", 
/*  93 */       "context", "switch", "port", "device", "left", "right", "top", "bottom", "jnsURI", "clip", 
/*  94 */       "width", "zIndex", "type", "dur", "startProgress", "borderColor", "switchPort", "ruleURI", 
/*  95 */       "endProgress", "direction", "fadeColor", "horzRepeat", "vertRepeat", "borderWidth", "connectorURI", 
/*  96 */       "player", "explicitDur", "freeze", "focusIndex", "moveLeft", "moveRight", "descriptorURI", 
/*  97 */       "moveUp", "moveDown", "transIn", "transOut", "focusBorderColor", "focusBorderWidth", "regionURI", 
/*  98 */       "focusBorderTransparency", "focusSrc", "focusSelSrc", "selBorderColor", "descriptorParams", 
/*  99 */       "src", "alias", "documentURI", "baseId", "refer", "anchors", "area", "coords", "begin", "end", "text", "position", "first", 
/* 100 */       "last", "label", "vars", "expression", "default", "params", "binds", "component" };
/* 101 */     int i = 0;
/* 102 */     sJson = sJson.replaceAll(" ", " ");
/* 103 */     for (i = 0; i < substitutos.length; i++) {
/* 104 */       sJson = sJson.replaceAll("'" + substitutos[i] + "':", substitutos[i] + ":");
/* 105 */       sJson = sJson.replaceAll(substitutos[i] + ":", "\"" + substitutos[i] + "\":");
/*     */     }
/* 107 */     return sJson;
/*     */   }
/*     */ }

/* Location:           C:\Users\Convidado\Desktop\pacoteCompleto\jnsTranslator.jar
 * Qualified Name:     JsonReader
 * JD-Core Version:    0.6.2
 */