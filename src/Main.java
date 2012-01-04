/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import br.uff.midiacom.xml.XMLException;

/**
 *
 * @author Esdras Caleb
 */
public class Main {

    /** 
     * Fun��o principal que ir� ler o documento JSN
     * @param args the command line arguments "args"s que dir�o os arquivos de entrada e saida
     * @throws ParseException 
     * @throws XMLException 
     * @throws IOException 
     * @throws Exception 
     */
    public static void main(String[] args) throws ParseException, XMLException, IOException {
    	String arquivoEntrada = "/home/caleb/exemplosJNS/exemploSimples.jns";
    	String arquivoSaida = "/home/caleb/exemplosJNS/exemploteste.ncl";
    	if(args.length>1)
    		arquivoEntrada = args[0];
    	if(args.length>2)
    		arquivoSaida = args[1];
    	JSONObject jns = JsonReader.Read(arquivoEntrada);
    	JNSInterpreter interpretador = new JNSInterpreter();
        String arquivoFinal = interpretador.InterpretsJNS(jns);
        FileWriter writer = new FileWriter(arquivoSaida);
        PrintWriter saida = new PrintWriter(writer);
        saida.print(arquivoFinal);
        saida.close();
        writer.close();
        /*
    	String expressao = "s and (b and c) and (v or z)";
    	String[] subExpressao = expressao.split("\\(.*?\\)");
    	int i = subExpressao.length;        */

    }

}
