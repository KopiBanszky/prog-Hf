import Config.FileNames;
import Config.Permission;
import GUI.MainPage;
import System.Account;
import System.MainSystem;
import System.Tree.Container;
import System.Account;
import System.Tree.Item;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {
    public static String config =" new Config()";

    public static void main(String[] args) {
        System.out.println("Register/Login: ");
        MainPage mainPage = new MainPage();
        Account account = new Account();
        mainPage.navigator("/loginPage");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        String type = "";
        try {
            type = reader.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.print("Kérem adja meg a felhasználónevét: ");

        try {
            String name = reader.readLine();

            System.out.print("Kérem adja meg a jelszavát: ");
            String password = reader.readLine();
            if(type.equals("login")) {
                account.login(name, password);
            } else if(type.equals("register")) {
                account.createAccount(name, password);
            }
            System.out.println(account);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            if(e.getMessage().equals("Account does not exist")) {
                return;
            }
        }

        MainSystem mainSystem = null;
        try {
            mainSystem = new MainSystem(account);
        } catch (InvalidClassException e){
            System.out.println("There was an error with the file, the data is corrupted. Please remove " + FileNames.FOLDER + "/" + FileNames.FILE + " and restart");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        mainPage.setTitle(account.getName());
        String ln = "";
        ArrayList<Container> containers = mainSystem.getContainers(null);
        while (ln != null) {
            System.out.println("Típus: név   -> elérési út");
            containers.stream().forEach(c -> System.out.println(c));
            try {
                ln = reader.readLine();
                String[] cmd = ln.split(" ");

                switch (cmd[0]) {
                    case "open":
                        try {
                            containers = mainSystem.openContainer(cmd[1]);
                        } catch (Exception e) {
                            if(e.getMessage().equals("item")) {
                                Item item = mainSystem.openItem(cmd[1]);
                                System.out.println(item);
                                System.out.println(item.getDescription(account));
                                System.out.println(item.getImgs(account));
                                System.out.println(item.getText(account));
                            }
                        }
                        break;
                    case "new":
                        mainSystem.addFolderToCurrentParent(cmd[1], new HashMap<Account, Permission>());
                        containers = mainSystem.openContainer(null);
                        break;
                    case "newItem":
                        mainSystem.addItemToCurrentParent(
                                cmd[1],
                                "description",
                                "longText",
                                new ArrayList<String>(),
                                new HashMap<Account, Permission>()
                        );
                        containers = mainSystem.openContainer(null);
                        break;
                    case "permissions": {
                        Container container = mainSystem.getCurrentParent();
                        System.out.println("Permissions for " + container.getName());
                        container.getPermissions().entrySet().stream()
                                .forEach(e -> System.out.println(e.getKey().getName() + " -> " + e.getValue())
                                );
                        break;
                    }
                    case "delete":
                        mainSystem.deleteCurrent();
                        break;
                    case "exit":
                        ln = null;
                        break;
                    default:
                        System.out.println("Nem létező parancs");
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }


        //MainSystem mainSystem = new MainSystem(account);

    }


}