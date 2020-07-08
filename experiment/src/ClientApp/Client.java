package ClientApp;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Client {
    static ObjectInputStream in;
    static ObjectOutputStream out;
    static InetAddress ia;
    public static final int port = 1238;
    static SocketChannel channel;
    static Object command, answer;
    static String userCommand;
    static ByteBuffer buffer = ByteBuffer.allocate(2048);

    public static void main(String[] args) {

        try {
            ia = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Client app запущено \nСистема готова к работе. Для просмотра списка команд введите \"help\".");

        while (true) {
            try {
                userCommand = reader.readLine();
                ClientUserCommands.check(userCommand); // Проверка команды на валидность
                if (ClientUserCommands.getStatus()==1) {  // Если команда введена корректно
                    command = ClientUserCommands.getCommand(userCommand, reader);  // getCommand возвращает введенную пользователем команду в виде объекта
                    if (command != null) {
                        channel = SocketChannel.open(new InetSocketAddress(ia, port));
                        channel.configureBlocking(false);
                        sendCommand(command);
                        answer = getAnswer();
//                        while (answer == null) {
//                            answer = getAnswer();
//                        }

                        if ((command = ProcessingAnswer.print(answer, userCommand)) != null) {
                            in.close();
                            out.close();
                            channel.close();
                            channel = SocketChannel.open(new InetSocketAddress(ia, port));
                            channel.configureBlocking(false);
                            sendCommand(command);

                            ProcessingAnswer.print(answer, userCommand);   // Получает на вход ответ сервера (getAnswer()) и обрабатывает его
                        }
                        //in.close();
                        out.close();
                        channel.close();

//                        if ((command = ProcessingAnswer.print(getAnswer(), userCommand)) != null) {
//                            in.close();
//                            out.close();
//                            channel.close();
//                            channel = SocketChannel.open(new InetSocketAddress(ia, port));
//                            channel.configureBlocking(false);
//                            sendCommand(command);
//                            ProcessingAnswer.print(getAnswer(), userCommand);
//                        }

//                        Socket socket = new Socket(ia, port);
//                        sendCommand(command, socket);
//                        if ((command = ProcessingAnswer.print(getAnswer(socket), userCommand)) != null) {
//                            in.close();
//                            out.close();
//                            socket.close();
//                            socket = new Socket(ia, port);
//                            sendCommand(command, socket);
//                            ProcessingAnswer.print(getAnswer(socket), userCommand);
//                        }

//                        in.close();
//                        out.close();
//                        channel.close();
                    }
                }
            } catch (IOException e) {
                System.err.println("Сервер временно недоступен");
            }
            catch (ClassNotFoundException e) {
                System.err.println("Сервер временно недоступен");
            }
        }

    }

    /**
     * Отправлят команду на сервер
     * @param com отправляемая команда
     */

    public static void sendCommand (Object com) {
        try {
            if(channel == null){
                System.err.println("Соединение не создано");
                return;
            }
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            out = new ObjectOutputStream(byteStream);
            out.writeObject(com);
            channel.write(ByteBuffer.wrap(byteStream.toByteArray()));
//            out = new ObjectOutputStream(socket.getOutputStream());
//            out.writeObject(com);
//            out.flush();
            System.out.println("Отправлено на сервер");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Получает ответ от сервера
     * @return объект в виде обработанной команды
     * @throws IOException
     */
    public static Object getAnswer () throws IOException, ClassNotFoundException {
        if(channel == null){
            System.err.println("Соединение не создано");
            return null;
        }

        ByteBuffer buffer = ByteBuffer.allocate(2048);
        in = new ObjectInputStream(channel.socket().getInputStream());

        try {
            //ObjectInputStream objStream = new ObjectInputStream(new ByteArrayInputStream(buffer.array()));
            return in.readObject();
        }catch(StreamCorruptedException e){
            return null;
        }
//        try {
//            in = new ObjectInputStream(socket.getInputStream());
//            return in.readObject();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}

//class Waiter extends Thread {
//
//    @Override
//    public void run() {
//        try {
//            while (Client.getAnswer() == null) {
//                if (Client.getAnswer() != null) {
//                    if ((Client.command = ProcessingAnswer.print(Client.getAnswer(), Client.userCommand)) != null) {
//                        Client.in.close();
//                        Client.out.close();
//                        Client.channel.close();
//                        Client.channel = SocketChannel.open(new InetSocketAddress(Client.ia, Client.port));
//                        Client.channel.configureBlocking(false);
//                        Client.sendCommand(Client.command);
//
//                        Waiter waiter = new Waiter();
//                        waiter.start();
//
//                        ProcessingAnswer.print(Client.getAnswer(), Client.userCommand);
//                    }
//                    Client.in.close();
//                    Client.out.close();
//                    Client.channel.close();
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
