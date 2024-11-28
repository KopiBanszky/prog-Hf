package System;

import Config.FileNames;
import Config.Permission;
import System.Tree.Comparators.NameComparatorAsc;
import System.Tree.Comparators.NameComparatorDesc;
import System.Tree.Container;
import System.Tree.Folder;
import System.Tree.Item;
import org.junit.jupiter.api.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;


public class MainSystemTest {
    private static MainSystem system;
    private static Account account;
    private static String accountFile;
    private static String containerFile;
    private static String textFolder;
    private static String imgFolder;

    @BeforeAll
    static void setUpAll() {
        accountFile = FileNames.ACCOUNT;
        FileNames.ACCOUNT = "testAccounts.ser";
        containerFile = FileNames.FILE;
        FileNames.FILE = "testContainers.ser";
        textFolder = FileNames.TEXT_FOLDER;
        FileNames.TEXT_FOLDER = "testTexts";
        imgFolder = FileNames.IMGS_FOLDER;
        FileNames.IMGS_FOLDER = "testImgs";
        account = new Account();
        try {
            account.createAccount("testAcc", "testAcc");
        } catch (Exception e) {
            e.printStackTrace();
            assumeTrue(false, "Account creation failed");
        }
    }

    @AfterAll
    static void tearDownAll() {
        account = new Account();
        try {
            account.login("testAcc", "testAcc");
            MainSystem system = new MainSystem(account);
            system.deleteAccount();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Files.delete(Paths.get("./", FileNames.FOLDER,  FileNames.ACCOUNT));
            File txtFolder = new File("./" + FileNames.FOLDER + "/" + FileNames.TEXT_FOLDER);
            String[] txts = txtFolder.list();
            for (String s : txts) {
                File currentFile = new File(txtFolder.getPath(), s);
                currentFile.delete();
            }
            txtFolder.delete();
            //Files.delete(Paths.get("./", FileNames.FOLDER,  FileNames.TEXT_FOLDER));
            File imgFolder = new File("./" + FileNames.FOLDER + "/" + FileNames.IMGS_FOLDER);
            String[] imgs = imgFolder.list();
            for (String s : imgs) {
                File currentFile = new File(imgFolder.getPath(), s);
                currentFile.delete();
            }
            imgFolder.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FileNames.ACCOUNT = accountFile;
        FileNames.FILE = containerFile;
        FileNames.TEXT_FOLDER = textFolder;
        FileNames.IMGS_FOLDER = imgFolder;
    }

    @BeforeEach
    void setUp() {
        account = new Account();
        try {
            account.login("testAcc", "testAcc");
            system = new MainSystem(account);
        } catch (Exception e) {
            e.printStackTrace();
            assumeTrue(false, "Account login failed");
        }
    }

    @AfterEach
    void tearDown() {
        system.logout();
        account = new Account();
        try {
            Files.delete(Paths.get("./", FileNames.FOLDER, FileNames.FILE));
        } catch (Exception e) {
            System.out.println("File deletion failed");
        }
    }

    @Test
    void getAccount() {
        assertEquals(account, system.getAccount());
    }

    @Test
    void getCurrentParent() {
        assertEquals("root", system.getCurrentParent().getName());
    }

    @Test
    void openAndGetInnerParent(){
        HashMap<Account, Permission> permissions = new HashMap<>();

        Folder folder = null;
        try {
            folder = new Folder("testFolder", system.getCurrentParent(), permissions);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            folder = system.addFolderToCurrentParent("testFolder", permissions);
            assertEquals("testFolder",
                    folder.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            ArrayList<Container> containers = new ArrayList<Container>();
            assertEquals(containers, system.openContainer("testFolder"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertEquals(folder.getName(), system.getCurrentParent().getName());

        permissions.put(account, Permission.ADMIN);
        assertArrayEquals(permissions.values().toArray(new Permission[0]), system.getCurrentParent().getPermissions().values().toArray(new Permission[0]));
    }

    @Test
    void getContainers() {
        assertArrayEquals(new Container[0], system.getContainers().toArray(new Container[0]));

        HashMap<Account, Permission> permissions = new HashMap<>();
        Folder folder = null;
        try {
            folder = system.addFolderToCurrentParent("testFolder", permissions);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(1, system.getContainers().size());
        assertEquals(folder, system.getContainers().get(0));
    }

    @Test
    void getComparator() {
        assertEquals(new NameComparatorAsc(), system.getComparator());

        system.sortBy(new NameComparatorDesc());
        assertEquals(new NameComparatorDesc(), system.getComparator());
    }

    @Test
    void getSuggestions() {
        system.getSuggestions();
        assertEquals(0, system.getSuggestions().size());

        try {
            system.addFolderToCurrentParent("testFolder", new HashMap<>());
            system.openContainer(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(1, system.getSuggestions().size());
        try {
            system.openContainer("testFolder");
            system.addItemToCurrentParent("testItem", "", "", new ArrayList<>(), new HashMap<>());
            system.openContainer(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(1, system.getSuggestions().size());

        try {
            system.openContainer("..");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(2, system.getSuggestions().size());


        Account account2 = new Account();
        MainSystem system2 = null;
        try {
            account2.createAccount("testAcc2", "testAcc2");
            system2 = new MainSystem(account2);
            system2.addFolderToCurrentParent("testFolder2", new HashMap<>());
            system2.openContainer(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(2, system.getSuggestions().size());

        ArrayList<String> suggestions = new ArrayList<>();
        suggestions.add("testItem");
        suggestions.add("testFolder");
        assertArrayEquals(suggestions.toArray(), system.getSuggestions().toArray());

        assert system2 != null;
        assertEquals(1, system2.getSuggestions().size());
        MainSystem finalSystem = system2;
        assertDoesNotThrow(
                () -> finalSystem.openContainer("testFolder2")
        );
        assertEquals(0, finalSystem.getSuggestions().size());
        system2.deleteAccount();
    }

    @Test
    void testGetContainers() {
        assertEquals(0, system.getContainers().size());
        HashMap<Account, Permission> permissions = new HashMap<>();
        Folder folder = null;
        try {
            folder = system.addFolderToCurrentParent("testFolder", permissions);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(1, system.getContainers().size());
        assert folder != null;
        assertEquals(folder.getName(), system.getContainers().getFirst().getName());

        try {
            system.addFolderToCurrentParent("testFolder2", permissions);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(2, system.getContainers().size());
        assertEquals("testFolder2", system.getContainers().getLast().getName());

        try {
            system.openContainer("testFolder");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(2, system.getContainers().size());
    }

    @Test
    void openContainer() {

        HashMap<Account, Permission> permissions = new HashMap<>();
        Folder folder = null;
        try {
            folder = system.addFolderToCurrentParent("testFolder", permissions);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertDoesNotThrow(
                () -> system.openContainer("testFolder")
        );
        assertEquals(folder.getName(), system.getCurrentParent().getName());
        assertEquals(0, system.getCurrentParent().getChildren().size());

        assertDoesNotThrow(
                () -> system.openContainer("..")
        );
        assertEquals("root", system.getCurrentParent().getName());
        try {
            system.addItemToCurrentParent("item","", "", new ArrayList<>(), permissions);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertThrowsExactly(
                Exception.class,
                () -> system.openContainer("item")
        );
        system.getCurrentParent().getChildren().getFirst().setPermissions(new HashMap<Account, Permission>());
        assertThrowsExactly(
                Exception.class,
                () -> system.openContainer("testFolder")
        );
    }

    @Test
    void testOpenContainerWithQuery() {
        HashMap<Account, Permission> permissions = new HashMap<>();
        Folder folder = null;
        try {
            folder = system.addFolderToCurrentParent("testFolder", permissions);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertDoesNotThrow(
                () -> system.openContainer("testFolder")
        );
        assertEquals(folder.getName(), system.getCurrentParent().getName());
        assertEquals(0, system.getCurrentParent().getChildren().size());

        assertDoesNotThrow(
                () -> system.openContainer("..")
        );
        assertEquals("root", system.getCurrentParent().getName());
        assertEquals(1, system.getContainers().size());
        try {
            system.addItemToCurrentParent("item","", "", new ArrayList<>(), permissions);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertThrowsExactly(
                Exception.class,
                () -> system.openContainer("item")
        );
        assertDoesNotThrow(
                () -> system.openContainer("testFolder")
        );

        try {
            system.addItemToCurrentParent("testItem", "", "", new ArrayList<>(), permissions);
            system.addItemToCurrentParent("testItem2", "", "", new ArrayList<>(), permissions);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertDoesNotThrow(
                () -> system.openContainer("..")
        );
        try {
            ArrayList<Container> containers = system.openContainer("", "test");
            assertEquals(3, containers.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void openItem() {
        HashMap<Account, Permission> permissions = new HashMap<>();
        Folder folder = null;
        try {
            folder = system.addFolderToCurrentParent("testFolder", permissions);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertDoesNotThrow(
                () -> system.openContainer("testFolder")
        );
        assertEquals(folder.getName(), system.getCurrentParent().getName());
        assertEquals(0, system.getCurrentParent().getChildren().size());

        AtomicReference<Item> newItem = new AtomicReference<>();
        assertDoesNotThrow(() -> {
                system.addItemToCurrentParent("testItem", "", "", new ArrayList<>(), permissions);
        });
        assertDoesNotThrow(
                () -> {
                    newItem.set((Item) system.openItem("testItem"));
                }
        );
        assertEquals("testItem", newItem.get().getName());

    }

    @Test
    void addFolderToCurrentParent() {
        HashMap<Account, Permission> permissions = new HashMap<>();
        assertDoesNotThrow(() -> {
                    system.addFolderToCurrentParent("testFolder", permissions);
                });
        assertEquals("root", system.getCurrentParent().getName());
        assertEquals(1, system.getCurrentParent().getChildren().size());
        assertEquals("testFolder", system.getCurrentParent().getChildren().getFirst().getName());

        assertDoesNotThrow(() -> system.addFolderToCurrentParent("testFolder2", permissions));
        assertEquals(2, system.getCurrentParent().getChildren().size());
        assertEquals("testFolder2", system.getCurrentParent().getChildren().getLast().getName());
        assertDoesNotThrow(() -> system.openContainer("testFolder2"));
        system.getCurrentParent().setPermissions(new HashMap<Account, Permission>());

        assertThrowsExactly(
                Exception.class,
                () -> system.addFolderToCurrentParent("testFolder3", permissions)
        );

    }

    @Test
    void addItemToCurrentParent() {
        assertDoesNotThrow(
                () -> system.addItemToCurrentParent("TestItem", "", "", new ArrayList<String>(), new HashMap<Account, Permission>())
        );
        assertEquals(1, system.getCurrentParent().getChildren().size());
        assertEquals("TestItem", system.getCurrentParent().getChildren().getFirst().getName());
        assertDoesNotThrow(
                () -> system.addFolderToCurrentParent("testFolder", new HashMap<Account, Permission>())
        );
        assertDoesNotThrow(
                () -> system.openContainer("testFolder")
        );
        system.getCurrentParent().setPermissions(new HashMap<Account, Permission>());
        assertThrowsExactly(
                Exception.class,
                () -> system.addItemToCurrentParent("TestItem2", "", "", new ArrayList<String>(), new HashMap<Account, Permission>())
        );
    }

    @Test
    void logout() {
        system.logout();
        assertNull(system.getAccount());
        assertNull(system.getCurrentParent());
        assertNull(system.getComparator());
        assertNull(system.getContainers());
    }

    @Test
    void deleteAccount() {
        assertDoesNotThrow(
                () -> system.deleteAccount()
        );
        assertNull(system.getAccount());
        assertNull(system.getCurrentParent());
        assertNull(system.getComparator());
        assertNull(system.getContainers());
        try {
            ArrayList<Account> accounts = Account.getAllAccounts();
            assertEquals(0, accounts.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        account = new Account();
        assertDoesNotThrow(
                () -> account.createAccount("testAcc", "testAcc")
        );
    }

    @Test
    void sortBy() {
        assertDoesNotThrow(
                () -> system.sortBy(new NameComparatorDesc())
        );
        assertEquals(new NameComparatorDesc(), system.getComparator());
        assertDoesNotThrow(
                () -> system.sortBy(new NameComparatorAsc())
        );
        assertEquals(new NameComparatorAsc(), system.getComparator());
    }

    @Test
    void sort() {
        assertDoesNotThrow(
                () -> system.addFolderToCurrentParent("1testFolder", new HashMap<Account, Permission>())
        );
        assertDoesNotThrow(
                () -> system.addFolderToCurrentParent("2testFolder", new HashMap<Account, Permission>())
        );
        assertDoesNotThrow(
                () -> system.addFolderToCurrentParent("3testFolder", new HashMap<Account, Permission>())
        );

        assertDoesNotThrow(
                () -> system.sort()
        );
        assertEquals("1testFolder", system.getContainers().getFirst().getName());
        assertEquals("3testFolder", system.getContainers().getLast().getName());

        assertDoesNotThrow(
                () -> system.sortBy(new NameComparatorDesc())
        );
        assertDoesNotThrow(
                () -> system.sort()
        );
        assertEquals("3testFolder", system.getContainers().getFirst().getName());
        assertEquals("1testFolder", system.getContainers().getLast().getName());
    }

    @Test
    void deleteCurrent() {
        assertDoesNotThrow(
                () -> system.addFolderToCurrentParent("testFolder", new HashMap<Account, Permission>())
        );
        assertDoesNotThrow(
                () -> system.openContainer("testFolder")
        );
        assertDoesNotThrow(
                () -> system.addFolderToCurrentParent("testFolder2", new HashMap<Account, Permission>())
        );
        HashMap<Account, Permission> permissions = (HashMap<Account, Permission>) system.getCurrentParent().getChildren().getFirst().getPermissions().clone();
        system.getCurrentParent().getChildren().getFirst().setPermissions(new HashMap<Account, Permission>());
        assertDoesNotThrow(
                () -> system.openContainer("..")
        );
        assertThrowsExactly(
                Exception.class,
                () -> system.deleteCurrent()
        );
        assertDoesNotThrow(
                () -> system.openContainer("testFolder")
        );
        system.getCurrentParent().getChildren().getFirst().setPermissions(permissions);
        /*assertDoesNotThrow(
                () -> system.openContainer("..")
        );*/
        assertDoesNotThrow(
                () -> system.deleteCurrent()
        );
    }

    @Test
    void addTree() {
        assertDoesNotThrow(
                () -> system.addTree("database/", null)
        );
        assertEquals(1, system.getContainers().size());
        assertEquals("database", system.getContainers().getFirst().getName());
        assertDoesNotThrow(
                () -> system.openContainer("database")
        );
        assertEquals(1, system.getContainers().size());
        assertEquals("texts", system.getCurrentParent().getName());
    }
}
