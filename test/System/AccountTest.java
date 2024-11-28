package System;

import Config.FileNames;
import Config.Permission;
import org.junit.jupiter.api.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {
    private static String FileName;
    private static Account account;


    @BeforeAll
    static void setUpAll() {
        FileName = FileNames.ACCOUNT;
        FileNames.ACCOUNT = "testAccounts.ser";
        account = new Account();
        try {
            account.createAccount("testAcc", "testAcc");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BeforeEach
    void setUp() {
        try {
            account.login("testAcc", "testAcc");
        } catch (Exception e) {
            System.out.println("Account is already logged in, first test");
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        FileNames.ACCOUNT = FileName;
    }

    @AfterEach
    void tearDown() {
        account = new Account();
    }

    @Test
    void getName() {
        assertEquals("testAcc", account.getName());
    }

    @Test
    void loginAgain() {
        assertThrowsExactly(
                Exception.class,
                () -> account.login("testAcc", "testAcc")
        );
    }

    @Test
    void login() {
        account = new Account();

        assertDoesNotThrow(
                () -> account.login("testAcc", "testAcc")
        );
    }

    @Test
    void createAccount() {
        Account newAccount = new Account();
        assertDoesNotThrow(
                () -> newAccount.createAccount("testAcc2", "testAcc2")
        );
    }

    @Test
    void createAccountAgain() {
        Account newAccount = new Account();
        assertThrowsExactly(
                Exception.class,
                () -> newAccount.createAccount("testAcc", "testAcc")
        );
    }

    @Test
    void saveToFile() {
        assertDoesNotThrow(
                () -> account.saveToFile()
        );
    }

    @Test
    void testGetAllAccounts() {
        try {
            ArrayList<Account> accounts = Account.getAllAccounts();
            System.out.println(accounts);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        assertDoesNotThrow(
                Account::getAllAccounts
        );
    }

    @Test
    void testSaveToFile() {
        assertDoesNotThrow(
                () -> Account.saveToFile(account)
        );
    }

    @Test
    void testSaveToFileNoPermission() {
        assertThrowsExactly(
                Exception.class,
                () -> Account.saveToFile(new Account())
        );
    }

    @Test
    void testToString() {
        assertEquals("testAcc", account.toString());
    }

    @Test
    void isIn() {
        HashMap<Account, Permission> permissions = new HashMap<>();
        permissions.put(account, Permission.ADMIN);
        assertTrue(account.isIn(permissions.keySet()));
    }

    @Test
    void notIsIn() {
        HashMap<Account, Permission> permissions = new HashMap<>();
        assertFalse(account.isIn(permissions.keySet()));
    }
}