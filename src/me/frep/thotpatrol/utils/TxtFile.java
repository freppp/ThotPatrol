package me.frep.thotpatrol.utils;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TxtFile {
	
    private File File;
    private String Name;
    private List<String> Lines = new ArrayList<String>();

    public TxtFile(JavaPlugin Plugin, String Path, String Name) {
        File = new File(Plugin.getDataFolder() + Path);
        File.mkdirs();
        File = new File(Plugin.getDataFolder() + Path, Name + ".txt");
        try {
            File.createNewFile();
        } catch (IOException localIOException) {
        }
        this.Name = Name;

        readTxtFile();
    }

    public void clear() {
        Lines.clear();
    }

    public void addLine(String line) {
        Lines.add(line);
    }

    public void write() {
        try {
            FileWriter fw = new FileWriter(File, false);
            BufferedWriter bw = new BufferedWriter(fw);
            for (String Line : Lines) {
                bw.write(Line);
                bw.newLine();
            }
            bw.close();
            fw.close();
        } catch (Exception localException) {
        }
    }

    public void readTxtFile() {
        Lines.clear();
        try {
            FileReader fr = new FileReader(File);
            BufferedReader br = new BufferedReader(fr);
            String Line;
            while ((Line = br.readLine()) != null) {
                Lines.add(Line);
            }
            br.close();
            fr.close();
        } catch (Exception exx) {
            exx.printStackTrace();
        }
    }

    public String getName() {
        return Name;
    }

    public String getText() {
        String text = "";
        for (int i = 0; i < Lines.size(); i++) {
            String line = Lines.get(i);

            text = text + line + (Lines.size() - 1 == i ? "" : "\n");
        }
        return text;
    }

    public List<String> getLines() {
        return Lines;
    }
}
