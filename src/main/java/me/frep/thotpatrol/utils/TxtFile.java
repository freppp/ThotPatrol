package me.frep.thotpatrol.utils;

import me.frep.thotpatrol.ThotPatrol;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TxtFile {
	
    private File File;
    private final String Name;
    private final List<String> Lines = new ArrayList<String>();

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
        } catch (Exception ignored) {
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

    public static int getLogged(String playerName) {
        int logged = 0;
        try {
            Scanner scanner = new Scanner(new File(ThotPatrol.Instance.getDataFolder() + "/violations.txt"));
            while (scanner.hasNextLine()) {
                if(scanner.nextLine().contains(playerName)){
                    logged++;
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
        return logged;
    }

    public static int getViolations(String playerName, String checkType) {
        int logged = 0;
        try {
            Scanner scanner = new Scanner(new File(ThotPatrol.Instance.getDataFolder() + "/violations.txt"));
            while (scanner.hasNextLine()) {
                if(scanner.nextLine().contains(playerName) && scanner.nextLine().contains(checkType)) {
                    logged++;
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
        return logged;
    }

    public static int averagePing(String playerName) {
        List<Integer> pings = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(new File(ThotPatrol.Instance.getDataFolder() + "/violations.txt"));
            while (scanner.hasNext()) {
                if(scanner.nextLine().contains(playerName)) {
                    String[] spl = scanner.nextLine().split("Ping: ");
                    pings.add(Integer.parseInt(spl[1]));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
        int averagePing = 0;
        for (int ping : pings) {
            averagePing += ping;
        }
        return averagePing / getLogged(playerName);
    }

    public static double averageTps(String playerName) {
        List<Double> tpss = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(new File(ThotPatrol.Instance.getDataFolder() + "/violations.txt"));
            while (scanner.hasNextLine()) {
                if(scanner.nextLine().contains(playerName)) {
                    String[] spl = scanner.nextLine().split("TPS: ");
                    String[] spl1 = spl[1].split(" | ");
                    tpss.add(Double.parseDouble(spl1[0]));
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
        double averageTps = 0;
        for (double tps : tpss) {
            averageTps += tps;
        }
        return (UtilMath.trim(2, averageTps / getLogged(playerName)));
    }
}