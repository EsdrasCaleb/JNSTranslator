import br.uff.midiacom.ana.util.exception.XMLException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JsonReader
{
  public static JSONObject Read(String Path)
    throws XMLException
  {
/*  24 */     String sJson = ReadString(Path);
/*  25 */     sJson = markStrings(sJson.replaceAll("(?:/\\*(?:[^*]|(?:\\*+[^*/]))*\\*+/)|(?://.*)", ""));
/*  26 */     JSONParser parser = new JSONParser();
/*  27 */     JSONObject json = null;
    try {
/*  29 */       json = (JSONObject)parser.parse(sJson);
    }
    catch (ParseException ex) {
/*  32 */       int antes = ex.getPosition() - 5;
/*  33 */       int depois = ex.getPosition() + 5;
/*  34 */       if (antes < 0) {
/*  35 */         depois -= antes;
/*  36 */         antes = 0;
      }
/*  38 */       else if (depois > sJson.length()) {
/*  39 */         depois = sJson.length();
/*  40 */         antes = antes - depois - sJson.length();
      }
/*  42 */       throw new XMLException("Erro perto de " + sJson.substring(antes, depois) + " linha:" + getLine(sJson.substring(antes, depois), Path));
    }
/*  44 */     return json;
  }

  private static String getLine(String substring, String path) {
     String sJson = "";
     int linha = 0;
    try {
       BufferedReader in = new BufferedReader(new FileReader(path));

       while (in.ready()) {
         String str = in.readLine();
         sJson = sJson + str.replaceAll("//.*$", "");
         if (sJson.contains(substring))
           return Integer.toString(linha);
         linha++;
      }
       in.close();
    } catch (IOException e) {
       sJson = e.toString();
    }
     return "não identificada";
  }

  public static JSONArray parse(String json) throws ParseException {
     json = markStrings(json);
     JSONParser parser = new JSONParser();
     JSONArray jsonR = (JSONArray)parser.parse(json);
     return jsonR;
  }

  public static String ReadString(String Path) {
    String sJson = "";
    try {
    	BufferedReader in = new BufferedReader(new FileReader(Path));
    	while (in.ready()) {
    		String str = in.readLine();
    		sJson = sJson + str.replaceAll("//.*$", "");
    	}
    	in.close();
    } 
    catch (IOException e) {
       sJson = e.toString();
    }
    		  
    return sJson;
  }

  private static String markStrings(String sJson) {
     String[] substitutos = { "subtype", "include", "ncl", "id", "head", "body", "region", "title", "parent", "descriptor", "descriptorSwitch", "link", 
       "rule", "transition", "xconnector","connector", "meta", "metadata", "media", "property", "height", "instance", 
       "context", "switch", "port", "device", "left", "right", "top", "bottom", "jnsURI", "clip", 
       "width", "zIndex", "type", "dur", "startProgress", "borderColor", "switchPort", "ruleURI", 
       "endProgress", "direction", "fadeColor", "horzRepeat", "vertRepeat", "borderWidth", "connectorURI", 
       "player", "explicitDur", "freeze", "focusIndex", "moveLeft", "moveRight", "descriptorURI", 
      "moveUp", "moveDown", "transIn", "transOut", "focusBorderColor", "focusBorderWidth", "regionURI", 
       "focusBorderTransparency", "focusSrc", "focusSelSrc", "selBorderColor", "descriptorParams", 
       "src", "alias", "documentURI", "baseId", "refer", "anchors", "area", "coords", "begin", "end", "text", "position", "first", 
       "last", "label", "vars", "expression", "default", "params", "binds", "component" };
     int i = 0;
     sJson = sJson.replaceAll(" ", " ");
    for (i = 0; i < substitutos.length; i++) {
      sJson = sJson.replaceAll("'" + substitutos[i] + "':", substitutos[i] + ":");
      sJson = sJson.replaceAll(substitutos[i] + ":", "\"" + substitutos[i] + "\":");
    }
    return sJson;
  }
}
