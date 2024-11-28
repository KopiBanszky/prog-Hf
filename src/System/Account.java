package System;

import Config.FileNames;

import java.io.*;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Account implements Serializable {
    private String name;
    private int id;
    private String hash;
    private String salt;

    public Account() {};

    public String getName() {
        return name;
    }

    /**
     * @param name;
     * @param pasw - password;
     * @throws Exception if the account does not exist
     * @throws Exception if the password is incorrect
     * logs in the user with the given name and password
     */
    public void login(String name, String pasw) throws Exception{

        if(this.name != null) {
            throw new Exception("Account is already logged in");
        }

        ArrayList<Account> accounts = getAllAccounts();

        if(accounts.stream().noneMatch(a -> a.name.equals(name))) {
            throw new Exception("Account does not exist");
        }

        Account account = null;
        for(Account a : accounts) {
            if(a.getName().equals(name) && a.validatePassword(pasw)) {
                account = a;
                break;
            }
        }
        if(account == null) {
            throw new Exception("Password is incorrect");
        }


        this.name = account.name;
        this.id = account.id;
        this.hash = account.hash;
        this.salt = account.salt;

    };

    /**
     * @param name
     * @param password
     * @throws Exception if the account already exists
     * @throws Exception if the account creation fails
     * @throws Exception if name or password is not long enough (min 4 characters)
     * creates a new account with the given name and password, if successful, it saves to file
     */
    public void createAccount(String name, String password) throws Exception{
        if(name.length() < 4 || password.length() < 4) {
            throw new Exception("Name or password is too short");
        }

        ArrayList<Account> accounts = getAllAccounts();

        if(accounts.stream().anyMatch(a -> a.name.equals(name))) {
            throw new Exception("Account already exists");
        }

        this.name = name;
        this.hash = getHash(password);
        try {
            this.id = accounts.stream().max((a1, a2) -> Math.max(a1.id, a2.id)).get().id + 1;
        } catch (Exception e) {
            this.id = 0;
        }

        saveToFile();

    };


    /**
     * @return all accounts from file
     * @throws Exception if the read fails
     */
    public static ArrayList<Account> getAllAccounts() throws Exception {

        File file = new File(FileNames.FOLDER + "/" + FileNames.ACCOUNT);
        ArrayList<Account> results;
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            results = (ArrayList<Account>) ois.readObject();
            ois.close();
        } catch (FileNotFoundException e) {
            results = new ArrayList<>();
        }
        return results;
    }

    /**
     * @throws Exception if the save fails
     * saves the current account to file
     */
    public void saveToFile() throws Exception {
        saveToFile(this);
    };

    /**
     * @param account
     * @throws Exception if the account is not initialized
     * @throws Exception if the save fails
     * saves the given account to file
     */
    public static void saveToFile(Account account) throws Exception {
        if(account.name == null) {
            throw new Exception("Account is not initialized");
        }

        ArrayList<Account> allAccounts = getAllAccounts();
        if(account.isIn((new HashSet<Account>(allAccounts)))) {
            allAccounts.removeIf(a -> a.equals(account));
        }
        allAccounts.add(account);

        File file = new File(FileNames.FOLDER + "/" + FileNames.ACCOUNT);

        ObjectOutputStream bos = new ObjectOutputStream(new FileOutputStream(file));
        bos.writeObject(allAccounts);
        bos.close();
    }

    /**
     * set file to accountlist
     * @param account
     */
    public static void saveToFile(ArrayList<Account> account) throws Exception {
        File file = new File(FileNames.FOLDER + "/" + FileNames.ACCOUNT);

        ObjectOutputStream bos = new ObjectOutputStream(new FileOutputStream(file));
        bos.writeObject(account);
        bos.close();
    }

    //endregion

    /**
     * @param password
     * @return the hash of the password
     */
    private String getHash(String password)  {
        salt = createSalt(4);
        String saltedPassword = salt + password;
        String hash;
        try {
            MessageDigest md  = MessageDigest.getInstance("SHA-256");
            md.update(saltedPassword.getBytes());
            hash = Base64.getEncoder().encodeToString(md.digest());
            return hash;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param password
     * @return true if the password is correct
     */
    private boolean validatePassword(String password) {
        String saltedPassword = salt + password;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(saltedPassword.getBytes());
            String hash = Base64.getEncoder().encodeToString(md.digest());
            return hash.equals(this.hash);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * @param length
     * @return a random string of the given length
     */
    private String createSalt(int len) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder salt = new StringBuilder();
        for(int i = 0; i < len; i++) {
            salt.append(chars.charAt((int) Math.floor(Math.random() * chars.length())));
        }
        return salt.toString();
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * @param accounts
     * @return true if the account is in the given set
     */
    public boolean isIn(Set<Account> accounts) {
        return accounts.stream().anyMatch(a -> a.name.equals(name));
    }

    /**
     * @param account
     * @return true if the account names are equal
     */
    public boolean equals(Account account) {
        return account.name.equals(name);
    }
}
