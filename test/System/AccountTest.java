package System;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    Account account;

    @BeforeEach
    void setUp() {
        account = new Account();
        try{
            account.createAccount("vendeg", "vendeg");
        } catch (Exception e) {
            e.printStackTrace();
        }
}

    @AfterEach
    void tearDown() {

    }

    @Test
    void getName() {
    }

    @Test
    void login() {
        assertThrowsExactly(
                Exception.class,
                () -> account.login("vendeg", "vendeg")
        );
    }

    @Test
    void createAccount() {
    }

    @Test
    void getAllAccounts() {
    }

    @Test
    void saveToFile() {
    }

    @Test
    void testSaveToFile() {
    }

    @Test
    void testToString() {
    }

    @Test
    void isIn() {
    }
}