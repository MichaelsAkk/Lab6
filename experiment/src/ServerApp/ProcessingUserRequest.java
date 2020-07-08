package ServerApp;

import Classes.Flat;
import ClientApp.ProcessingAnswer;
import Commands.*;
import Instruments.ScriptInfo;
import com.sun.org.apache.regexp.internal.RE;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;

public class ProcessingUserRequest {

    public static int result(String userCommand, HashSet<Flat> flats, LocalDateTime today) {

        if (userCommand.equalsIgnoreCase("help")) {
            Help help = new Help();
            help.execute();
            System.out.println(help.getInfo());
            Server.setHistory(help.toString());
            return 1;
        }

        if (userCommand.equalsIgnoreCase("clear")) {
            Clear clear = new Clear(flats);
            clear.execute();
            System.out.println(clear.getInfo());
            Server.setHistory(clear.toString());
            return 1;
        }

        if (userCommand.equalsIgnoreCase("show")) {
            Show show = new Show(flats);
            show.execute();
            System.out.println(show.getInfo());
            Server.setHistory(show.toString());
            return 1;
        }

        if (userCommand.equalsIgnoreCase("info")) {
            Info info = new Info(flats, today);
            info.execute();
            System.out.println(info.getInfo());
            Server.setHistory(info.toString());
            return 1;
        }

        if (userCommand.equalsIgnoreCase("add")) {
            Add add = new Add(flats);
            try {
                add.fields();
            } catch (IOException e) {
                e.printStackTrace();
            }
            add.execute();
            System.out.println(add.getInfo());
            Server.setHistory(add.toStrings());
            return 1;
        }

        String[] parts = userCommand.split(" ",2);
        if (parts[0].equalsIgnoreCase("remove_by_id")) {
            try {
                int id = Integer.parseInt(parts[1]);
                if (id<=0) {
                    System.out.println("Поле id не является положительным числом");
                    return 1;
                } else {
                    Remove_By_Id rm = new Remove_By_Id(flats, id);
                    rm.execute();
                    System.out.println(rm.getInfo());
                    Server.setHistory(rm.toStrings());
                    return 1;
                }
            } catch (Exception e) {
                System.out.println("Поле id не является целым числом");
                return 1;
            }
        }

        if (parts[0].equalsIgnoreCase("update")) {
            try {
                int id = Integer.parseInt(parts[1]);
                if (id<=0) {
                    System.out.println("Поле id не является положительным числом");
                    return 1;
                } else {
                    Update update = new Update(flats, id);
                    update.check();
                    if (update.getInfo().equalsIgnoreCase("+")) {
                        update.fieldsUpdate();
                        update.setFlats(flats);
                        update.execute();
                        System.out.println(update.getInfo());
                        Server.setHistory(update.toStrings());
                    } else {
                        System.out.println(update.getInfo());
                    }
                    return 1;
                }
            } catch (Exception e) {
                System.out.println("Поле id не является целым числом");
                return 1;
            }
        }


        if (parts[0].equalsIgnoreCase("remove_greater")) {
            try {
                int numberOfRooms = Integer.parseInt(parts[1]); int control = 1;
                if (numberOfRooms<=0) {
                    System.out.println("Ошибка ввода: numberOfRooms не является положительным числом");
                    return 1;
                }
                if (control != 0) {
                    Remove_Greater rg = new Remove_Greater(flats, numberOfRooms);
                    rg.execute();
                    System.out.println(rg.getInfo());
                    Server.setHistory(rg.toStrings());
                    return 1;
                }
            } catch (Exception e) {
                System.out.println("Ошибка ввода: numberOfRooms не является целым числом");
                return 1;
            }
        }

        if (parts[0].equalsIgnoreCase("remove_lower")) {
            try {
                int numberOfRooms = Integer.parseInt(parts[1]); int control = 1;
                if (numberOfRooms<=0) {
                    System.out.println("Ошибка ввода: numberOfRooms не является положительным числом");
                    return 1;
                }
                if (control != 0) {
                    Remove_Lower rl = new Remove_Lower(flats, numberOfRooms);
                    rl.execute();
                    System.out.println(rl.getInfo());
                    Server.setHistory(rl.toStrings());
                    return 1;
                }
            } catch (Exception e) {
                System.out.println("Ошибка ввода: numberOfRooms не является целым числом");
                return 1;
            }
        }

        if (userCommand.equalsIgnoreCase("history")) {
            History his = new History(Server.getHistory());
            his.execute();
            System.out.println(his.getInfo());
            Server.setHistory(his.toStrings());
            return 1;
        }

        if (userCommand.equalsIgnoreCase("average_of_number_of_rooms")) {
            Average_Of_Number_Of_Rooms avg = new Average_Of_Number_Of_Rooms(flats);
            avg.execute();
            System.out.println(avg.getInfo());
            Server.setHistory(avg.toStrings());
            return 1;
        }

        if (parts[0].equalsIgnoreCase("filter_by_number_of_rooms")) {
            try {
                int numberOfRooms = Integer.parseInt(parts[1]); int control = 1;
                if (numberOfRooms<=0) {
                    System.out.println("Ошибка ввода: numberOfRooms не является положительным числом");
                    return 1;
                }
                if (control != 0) {
                    Filter_By_Number_Of_Rooms filt = new Filter_By_Number_Of_Rooms(flats, numberOfRooms);
                    filt.execute();
                    System.out.println(filt.getInfo());
                    Server.setHistory(filt.toStrings());
                    return 1;
                }
            } catch (Exception e) {
                System.out.println("Ошибка ввода: numberOfRooms не является целым числом");
                return 1;
            }
        }

        if (userCommand.equalsIgnoreCase("print_ascending")) {
            Print_Ascending pr = new Print_Ascending(flats);
            pr.execute();
            System.out.println(pr.getInfo());
            Server.setHistory(pr.toString());
            return 1;
        }

        if (parts[0].equalsIgnoreCase("execute_script")) {
            Execute_Script es = new Execute_Script(parts[1], Server.getHistory());
            ArrayList<String> scripts = Server.getScripts(); scripts.add(parts[1]);
            es.setScripts(scripts);
            es.getCommandsFromFile();
            ScriptInfo.setStartInfo("");
            es.setFields(flats, today);
            es.execute();
            System.out.println(es.getInfo());
            Server.setHistory(es.toString());
            scripts.clear();
            return 1;
        }

        return 0;
    }
}
