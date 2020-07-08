package ServerApp;

import Classes.Flat;
import Commands.Exit;
import Commands.Save;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;

public class Server {
    private static ObjectInputStream in;
    private static ObjectOutputStream out;
    public static final int port = 1238;
    private static HashSet<Flat> flats;
    private static File file;
    private static ArrayList<String> scripts, history;
    private static LocalDateTime today;

    public static void setHistory(String command) {
        if (history != null) {
            if (history.size() < 8) {
                String[] parts = command.split(" ");
                history.add(parts[0]);
            } else {
                history.remove(0);
                String[] parts = command.split(" ");
                history.add(parts[0]);
            }
        } else {
            history = new ArrayList<>();
            String[] parts = command.split(" ");
            history.add(parts[0]);
        }
    }

    public static HashSet<Flat> getFlats () {
        return flats;
    }

    public static ArrayList<String> getHistory () {
        return history;
    }

    public static ArrayList<String> getScripts () {
        return scripts;
    }

    public static File getFile () {
        return file;
    }

    public static void main(String[] args) {

        file = new File("C://Users/z/OneDrive/Рабочий стол/Lab6/Collection.txt");
        flats = null;
        try {
            flats = Instruments.Reader.readFile(file);   // Заполняет коллекцию из файла
        } catch (FileNotFoundException e) {
            System.err.println("Файл с коллекцией не найден");
        }
        today = LocalDateTime.now();
        history = new ArrayList<>();
        scripts = new ArrayList<>();

        try {
            ServerSocket socket = new ServerSocket (port);
            System.out.println("Server app запущено");
            User user = new User();
            user.start();              // Запускает отдельный поток для работы внутри сервера
            while (true) {
                Socket client = socket.accept();
                System.out.println("Установлено соединение с " + client.getInetAddress().getHostName());
                handleRequest(client);
            }
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Обрабатывает запрос клиента
     * ProcessingRequest.getResult(...) возвращает объект в виде обработанной команды
     * @param client
     */

    public static void handleRequest (Socket client) {
        try {
            in = new ObjectInputStream(client.getInputStream());
            out = new ObjectOutputStream(client.getOutputStream());

            out.writeObject(ProcessingRequest.getResult(in.readObject(), flats, today, client.getInetAddress(), scripts));

            System.out.println("Ответ отправлен");

            in.close();
            out.close();
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void handleUserCommand (String userCommand, HashSet<Flat> flats, File file, BufferedReader reader) {
        if (userCommand.equalsIgnoreCase("")) {}
        else if (userCommand.equalsIgnoreCase("exit")) {
            Save save = new Save(flats, file);
            Exit exit = new Exit(reader);
            save.execute();
            System.out.println("Работа Server app завешрена");
            exit.execute();
        }
        else if (userCommand.equalsIgnoreCase("save")) {
            Save save = new Save(flats, file);
            save.execute();
        }
        else if (ProcessingUserRequest.result(userCommand, flats, today) == 0) {
            System.out.println("Данная команда не поддерживается сервером");
        }
    }
}

class User extends Thread {

    @Override
    public void run() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String userCommand = null;
        while (true) {
            try {
                userCommand = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Server.handleUserCommand(userCommand, Server.getFlats(), Server.getFile(), reader);
        }
    }
}
