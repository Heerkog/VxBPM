package nl.rug.ds.bpm.editor.core.configloader;

import nl.rug.ds.bpm.editor.models.ModelChecker;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created by Heerko Groefsema on 30-Jan-17.
 */
public class Configsaver {
	private String home;
	
	public Configsaver()
	{
		home = System.getProperty("user.home");
	}
	
	public void saveModelCheckers(List<ModelChecker> modelCheckers)
	{
		File file = new File(home + "/VxBPM/model-checkers.xml");
		StringBuilder sb = new StringBuilder();
		
		sb.append("<?xml version=\"1.0\"?>\n" +
				 "<modelCheckers>\n");
		for(ModelChecker m: modelCheckers)
			sb.append(m.toXML());
		sb.append("</modelCheckers>");
		
		try {
			if(!file.exists())
			{
				file.getParentFile().mkdirs();
				file.createNewFile();
			}

			PrintWriter writer = new PrintWriter(file, "UTF-8");
			writer.println(sb.toString());
			writer.close();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
