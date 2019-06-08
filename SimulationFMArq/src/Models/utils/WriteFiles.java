package Models.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class WriteFiles {
	private String path = System.getProperty("user.dir");//"C:\\Users\\Fernando Junio\\git\\Simulacoes\\SimulationFMArq\\";
	
	public void writeInFile(String entrada){
		try{
			File file = new File(path);
			BufferedWriter writer = new BufferedWriter(new FileWriter(file,true));
			writer.write("Valor Recebido em: " + new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS").format(new Date()));
			writer.write("   " + entrada);
			writer.newLine();
			//Criando o conteúdo do arquivo
			writer.flush();
			//Fechando conexão e escrita do arquivo.
			writer.close();
			System.out.println("Arquivo gravado em: " + path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setFileName(String name){
		this.path = this.path + '\\' + name; 
	}
	
	public void setCompletPath(String completePath){
		this.path = completePath; 
	}
	
	public String getAtualPath(){
		return this.path;
	}
}