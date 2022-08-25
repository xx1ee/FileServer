package client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private static final String address = "127.0.0.1";
    private static final int PORT = 23456;
    static  String pathToFileClient = "./src/client/data/";
    static String fileName = "";
    public static void main(String[] args) {

        try {
            Thread.sleep(1000);
            Socket socket = new Socket(address, PORT);

            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            System.out.print("Enter action (1 - get a file, 2 - create a file, 3 - delete a file): ");
            Scanner scanner = new Scanner(System.in);
            String command = scanner.nextLine();

            switch (command) {
                case "1":
                    output.writeUTF(command);
                    System.out.println(input.readUTF());
                    output.writeUTF(scanner.nextLine());
                    System.out.println(input.readUTF());
                    output.writeUTF(scanner.nextLine());
                    System.out.println("The request was sent.");
                    int num = input.readInt();
                    if (num == 1) {
                        System.out.print("The file was downloaded! Specify a name for it: ");
                        String specialName1 = scanner.nextLine();
                        File file1 = new File(pathToFileClient + specialName1);
                        int length = input.readInt();
                        byte[] fileContent = new byte[length];
                        input.readFully(fileContent, 0, length);
                        FileOutputStream fos = new FileOutputStream(file1);
                        fos.write(fileContent);
                        fos.close();
                        System.out.println("File saved on the hard drive!");
                    } else if (num == 2) {
                        System.out.println(input.readUTF());
                    }
                    break;
                case "3":
                    output.writeUTF(command);
                    System.out.println(input.readUTF());
                    output.writeUTF(scanner.nextLine());
                    System.out.println(input.readUTF());
                    output.writeUTF(scanner.nextLine());
                    System.out.println("The request was sent.");
                    System.out.println(input.readUTF());
                    break;
                case "2":
                    output.writeUTF(command);
                    System.out.println("Enter name of the file:");
                    fileName = scanner.nextLine();
                    File file = new File(pathToFileClient + fileName);
                    if (!file.exists()) { System.out.println("File not exists"); break; }
                    else {
                        System.out.println(input.readUTF());
                        output.writeUTF(scanner.nextLine());
                        FileInputStream ios = new FileInputStream(file);
                        byte[] message = new byte[(int) file.length()];

                        ios.read(message);
                        ios.close();

                        output.writeInt(message.length);
                        output.write(message);

                    }
                    System.out.println("The request was sent.");
                    System.out.println(input.readUTF());
                    break;
                case "exit":
                    output.writeUTF("exit");
                    System.out.println("The request was sent.");
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + command);
            }
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
}