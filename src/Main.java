/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import br.uff.midiacom.ana.NCLInvalidIdentifierException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;

import javax.xml.parsers.ParserConfigurationException;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Esdras Caleb
 */
public class Main {

    /** 
     * Função principal que irá ler o documento JSN
     * @param args the command line arguments "args"s que dirão os arquivos de entrada e saida
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
    	String arquivoEntrada = "C:\\NCL\\teste.jns";
    	String arquivoSaida = "C:\\NCL\\teste.ncl";
    	if(args.length>1)
    		arquivoEntrada = args[0];
    	if(args.length>2)
    		arquivoSaida = args[1];
    	JSONObject jns = JsonReader.Read(arquivoEntrada);
        String arquivoFinal = JNSInterpreter.InterpretsJNS(jns);
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
