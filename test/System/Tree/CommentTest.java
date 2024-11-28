package System.Tree;


import Config.FileNames;
import Config.Permission;
import org.junit.jupiter.api.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import System.Account;

import static org.junit.jupiter.api.Assertions.*;

class CommentTest {
    private Item item;
    private Folder root;
    private Account account;
    private static String oldName;


    @BeforeAll
    static void init() {
        oldName = FileNames.ACCOUNT;
        FileNames.ACCOUNT = "testAccounts.ser";
    }

    @AfterAll
    static void clean() {
        FileNames.ACCOUNT = oldName;
    }

    @BeforeEach
    void setUp() throws Exception {
        root = new Folder();
        item = new Item("testItem", root, new HashMap<Account, Permission>(), "");
        account = new Account();
        try {
            account.createAccount("test", "test");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void tearDown() {
        try {
            Files.deleteIfExists(Paths.get(FileNames.FOLDER, FileNames.ACCOUNT));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void getComment() {
        Comment comment = new Comment("test", account);
        assertEquals("test", comment.getComment());
    }

    @Test
    void getWriter() {
        Comment comment = new Comment("test", account);
        assertEquals(account, comment.getWriter());
    }

    @Test
    void isResolved() {
        Comment comment = new Comment("test", account);
        assertFalse(comment.isResolved());
    }

    @Test
    void resolve() {
        Comment comment = new Comment("test", account);
        assertThrowsExactly(
                IllegalArgumentException.class,
                () -> comment.resolve(account, item.getPermissions())
        );
        assertFalse(comment.isResolved());
        assertDoesNotThrow(() -> item.addPermission(account, Permission.EDIT));
        assertDoesNotThrow(() -> comment.resolve(account, item.getPermissions()));
        assertTrue(comment.isResolved());
    }
}