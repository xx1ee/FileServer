package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class Main {
    private static final int PORT = 23456;
    static String pathToFileServer = "./src/server/data/";
    static  String pathToFileClient = "./src/client/data/";
    static volatile HashMap<Integer, String> ID = new HashMap<>();
    static String fileName = "";
    static String fileContent = "";

    public static void main(String[] args) {

        try {
            FileInputStream fis = new FileInputStream("./src/server/ID");
            ObjectInputStream ois = new ObjectInputStream(fis);
            ID = (HashMap) ois.readObject();
            fis.close();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            // e.printStackTrace();
        }
        boolean stop = false;
        try(ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Server started!");
            while (!stop) {
                Socket socket = server.accept();
                DataInputStream input = new DataInputStream(socket.getInputStream());
                DataOutputStream output = new DataOutputStream(socket.getOutputStream());

                switch (input.readUTF()) {
                    case "1":
                        get(input, output);
                        break;
                    case "2":
                        put(input, output);
                        break;
                    case "3":
                        delete(input, output);
                        break;
                    case "exit":
                        stop = true;
                        socket.close();
                        server.close();
                        break;
                    default:
                        output.writeUTF("Unknown request");
                }
                try {
                    FileOutputStream fos = new FileOutputStream("./src/server/ID");
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    oos.writeObject(ID);
                    fos.close();
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.out.println("message " + e.getMessage());
        }
    }

    private static void get(DataInputStream input, DataOutputStream output) {
        try {
            output.writeUTF("Do you want to get the file by name or by id (1 - name, 2 - id):");
            String nameorid = input.readUTF();
            if (nameorid.equals("1")) {
                output.writeUTF("Enter filename:");
                fileName = input.readUTF();
            } else {
                output.writeUTF("Enter id:");
                int id = Integer.parseInt(input.readUTF());
                fileName = ID.get(id);
                System.out.println(fileName);
                System.out.println(ID);
            }
            String filesPath = pathToFileServer + fileName;
            File myFile = new File(filesPath);
            if (myFile.exists()) {
                output.writeInt(1);
                byte[] message = new byte[(int) myFile.length()];
                FileInputStream fis = new FileInputStream(myFile);
                fis.read(message);
                fis.close();
                output.writeInt(message.length);
                output.write(message);
            } else {
                output.writeInt(2);
                output.writeUTF("The response says that this file is not found!");
            }
        } catch (IOException e) {
            System.out.println("Cannot read the file " + e.getMessage());
        }
    }

    private static void put(DataInputStream input, DataOutputStream output) throws IOException {
        try {
            output.writeUTF("Enter name of the file to be saved on server:");
            fileName = input.readUTF();
            if (fileName.equals("")) {
                Random random = new Random();
                fileName = String.valueOf(random.nextInt(100));
            }
            String filesPath = pathToFileServer + fileName;
            File myFile = new File(filesPath);
            if (myFile.exists()) {
                output.writeUTF("The response says that creating the file was forbidden!");
            } else {
                int length = input.readInt();
                byte[] fileContent = new byte[length];
                input.readFully(fileContent, 0, length);

                FileOutputStream fos = new FileOutputStream(myFile);
                fos.write(fileContent);
                fos.close();
                int idname = (int) (Math.random() * 100);
                ID.put(idname, fileName);
                output.writeUTF("Response says that file is saved! ID = " + idname);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void delete(DataInputStream input, DataOutputStream output) {
        try {
            output.writeUTF("Do you want to delete the file by name or by id (1 - name, 2 - id):");
            String nameorid = input.readUTF();
            if (nameorid.equals("1")) {
                output.writeUTF("Enter filename:");
                fileName = input.readUTF();
            } else {
                output.writeUTF("Enter id:");
                int id = Integer.parseInt(input.readUTF());
                fileName = ID.get(id);
            }
            String filesPath = pathToFileServer + fileName;
            Path path = FileSystems.getDefault().getPath(filesPath);
            if (Files.exists(path)) {
                Files.deleteIfExists(path);
                output.writeUTF("The response says that this file was deleted successfully!");
            } else {
                output.writeUTF("The response says that the file was not found!");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}