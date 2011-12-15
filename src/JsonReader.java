/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Esdras Caleb
 */
public class JsonReader {
    public static JSONObject Read(String Path) throws ParseException{
        String sJson = new String();
        try {
            BufferedReader in = new BufferedReader(new FileReader(Path));
            String str;
            while (in.ready()) {
                str = in.readLine();
                sJson = sJson+ str;
            }
            in.close();
        } catch (IOException e) {
            sJson = e.toString();
        }
        sJson = markStrings(sJson);
        JSONParser parser = new JSONParser();
        JSONObject json =  (JSONObject) parser.parse(sJson);
        return json;
    }

	private static String markStrings(String sJson) {
		String[] substitutos = {"ncl","id","head","body","region","title","parent","descriptor","descriptorSwitch","link",
				"rule","transition","connector","meta","metadata","media","property","height",
				"context","switch","port","device","title","left","right","top","bottom",
				"width","zIndex","type","subtype","dur","startProgress","borderColor",
				"endProgress","direction","fadeColor","horRepeat","vertRepeat","borderWidth",
				"player","explicitDur","freeze","focusIndex","moveLeft","moveRight",
				"moveUp","moveDown","transIn","transOut","focusBorderColor","focusBorderWidth",
				"focusBorderTransparency","focusSrc","focusSelSrc","selBorderColor","descriptorParams","cBody",
				"src","refer","type","anchors","area","coords","begin","end","text","position","first",
				"last","label","vars","expression","default","params","binds","xconector","component"};
		int i = 0;
		for(i=0;i<substitutos.length;i++){
			sJson = sJson.replaceAll("'"+substitutos[i]+"':",substitutos[i]+":");
		}
		for(i=0;i<substitutos.length;i++){
			sJson = sJson.replaceAll(substitutos[i]+":","\""+substitutos[i]+"\":");
		}
		return sJson;
		
	}
}