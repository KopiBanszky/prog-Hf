package System.Tree;

import Config.FileNames;
import Config.Permission;
import org.junit.jupiter.api.*;

import System.Account;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class ContainerTest {
    private static String accountFile;
    private static String containerFile;
    Folder root;
    Container container;

    @BeforeAll
    static void setUp() {
        accountFile = FileNames.ACCOUNT;
        FileNames.ACCOUNT = "testAccounts.ser";
        containerFile = FileNames.FILE;
        FileNames.FILE = "testContainers.ser";
    }

    @AfterAll
    static void tearDown() {
        FileNames.ACCOUNT = accountFile;
        FileNames.FILE = containerFile;
        try {
            Files.deleteIfExists(Paths.get(FileNames.FOLDER, FileNames.ACCOUNT));
            Files.deleteIfExists(Paths.get(FileNames.FOLDER, FileNames.FILE));
        } catch (Exception e) {
        }
    }

    @BeforeEach
    void init() {
        root = new Folder();
        try {
            container = new Folder("testFolder", root, new HashMap<Account, Permission>());
            root.addChild(container);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void clean() {
        root = null;
        container = null;
        try {
            Files.deleteIfExists(Paths.get(FileNames.FOLDER, FileNames.ACCOUNT));
            Files.deleteIfExists(Paths.get(FileNames.FOLDER, FileNames.FILE));
        } catch (Exception e) {
        }
    }

    @Test
    void getId() {
        assertEquals(0, root.getId());
        assertEquals(1, container.getId());
    }

    @Test
    void getModificationDate() {
        Date oldDate = (Date) container.getModificationDate().clone();

        try {
            Thread.sleep(10);
            container.setName("newName");
            assertNotEquals(oldDate.getTime(), container.getModificationDate().getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void getName() {
        assertEquals("root", root.getName());
        assertEquals("testFolder", container.getName());
    }

    @Test
    void setName() {
        assertEquals("testFolder", container.getName());
        container.setName("newName");
        assertEquals("newName", container.getName());
    }

    @Test
    void getPermissions() {
        assertEquals(0, root.getPermissions().size());
        assertEquals(0, container.getPermissions().size());
    }

    @Test
    void addPermission() {
        Account account = new Account();
        try {
            account.createAccount("test", "test");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Permission permission = Permission.READ;

        try {
            container.addPermission(account, permission);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertEquals(1, container.getPermissions().size());
    }

    @Test
    void removePermission() {
        Account account = new Account();
        try {
            account.createAccount("test", "test");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Permission permission = Permission.READ;

        try {
            container.addPermission(account, permission);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(1, container.getPermissions().size());

        try {
            container.removePermission(account);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(0, container.getPermissions().size());
    }

    @Test
    void setPermissions() {
        Account account = new Account();
        try {
            account.createAccount("test", "test");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Permission permission = Permission.READ;

        HashMap<Account, Permission> permissions = new HashMap<>();
        permissions.put(account, permission);

        container.setPermissions(permissions);
        assertEquals(1, container.getPermissions().size());

        container.setPermissions(new HashMap<>());
        assertEquals(0, container.getPermissions().size());
    }

    @Test
    void getPath() {
        ArrayList<Folder> path = container.getPath();
        assertEquals(1, path.size());
        assertEquals("root", path.getFirst().getName());
    }

    @Test
    void getReadableId() {
        assertEquals("", root.getReadableId());
        assertEquals("", container.getReadableId());
        Folder innerFolder = null;
        try {
            innerFolder = new Folder("innerFolder", (Folder) container, new HashMap<Account, Permission>());
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert innerFolder != null;
        assertEquals("Tes", innerFolder.getReadableId());
    }

    @Test
    void pathFolders() {
        assertEquals(0, root.getPath().size());
        ArrayList<Folder> path = new ArrayList<>();
        path.add(root);
        assertEquals(path, container.getPath());
        Folder innerFolder = null;
        try {
            innerFolder = new Folder("innerFolder", (Folder) container, new HashMap<Account, Permission>());
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert innerFolder != null;
        path.add((Folder)container);
        assertEquals(path, innerFolder.getPath());
    }

    @Test
    void pathIds() {
        assertEquals("", root.pathIds());
        assertEquals("", container.pathIds());
        Folder innerFolder = null;
        try {
            innerFolder = new Folder("innerFolder", (Folder) container, new HashMap<Account, Permission>());
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert innerFolder != null;
        assertEquals("1-", innerFolder.pathIds());
    }

    @Test
    void getFullDataFromFile() {

        try {
            Folder fileRoot = Container.getFullDataFromFile();
            assertEquals(root.getName(), fileRoot.getName());
            assertEquals(0, fileRoot.getChildren().size());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Container.saveFullDataToFile(root);
            Folder fileRoot = Container.getFullDataFromFile();
            assertEquals(root.getName(), fileRoot.getName());
            assertEquals(1, fileRoot.getChildren().size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void newId() {
        ArrayList<Container> children = new ArrayList<>();
        assertEquals(1, Container.newId(children));
        children.add(container);
        assertEquals(2, Container.newId(children));
    }

}