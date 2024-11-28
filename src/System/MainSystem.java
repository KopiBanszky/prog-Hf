package System;

import Config.FileNames;
import Config.Permission;
import System.Tree.Comparators.NameComparatorAsc;
import System.Tree.Comparators.TypeComparator;
import System.Tree.Container;
import System.Tree.Folder;
import System.Tree.Item;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;


public class MainSystem {
    private Account account;
    private ArrayList<Container> containers;
    private Folder currentParent;
    private Comparator<Container> comparator;
    private HashMap<String, Container> suggestions;

    /**
     * initializes the system with the given account
     * @param account;
     * @throws Exception
     */
    public MainSystem(Account account) throws Exception {
        this.account = account;
        File folder = new File(FileNames.FOLDER);
        if(!folder.exists()) {
            folder.mkdir();
        }
        folder = new File(FileNames.FOLDER + "/" + FileNames.IMGS_FOLDER);
        if(!folder.exists()) {
            folder.mkdir();
        }
        folder = new File(FileNames.FOLDER + "/" + FileNames.TEXT_FOLDER);
        if(!folder.exists()) {
            folder.mkdir();
        }
        currentParent = Container.getFullDataFromFile();
        containers = (ArrayList<Container>) currentParent.getChildren().clone();
        comparator = new NameComparatorAsc();

        loadSuggestions();
        sort();
    }

    //region getters

    public Account getAccount() {
        return account;
    }

    public Folder getCurrentParent() {
        return currentParent;
    }

    public ArrayList<Container> getContainers() {
        return containers;
    }

    public void addContainer(Container container) {
        containers.add(container);
    }

    public Comparator<Container> getComparator() {
        return comparator;
    }
    //endregion

    private void loadSuggestions() {
        suggestions = new HashMap<>();
        suggestions = getChildrenNames(currentParent);
    }

    private HashMap<String, Container> getChildrenNames(Folder parent) {
        HashMap<String, Container> result = new HashMap<>();
        for(Container container : parent.getChildren()) {
            if(!account.isIn(container.getPermissions().keySet())) {
                continue;
            }
            if(container instanceof Folder) {
                result.putAll(getChildrenNames((Folder) container));
            }
            result.put(container.getName(), container);
        }
        return result;
    }

    public ArrayList<String> getSuggestions() {
        return new ArrayList<>(suggestions.keySet());
    }

    //region container management

    /**
     * get the containers from the given parent that the account has access to
     * if the parent is null, it will return the root containers
     * @param parent;
     * @return ArrayList&lt;Container@gt;
     */
    public ArrayList<Container> getContainers(Folder parent) {
        ArrayList<Container> result = new ArrayList<>();
        sort();
        for(Container container : (parent == null ? containers : parent.getChildren())) {
            if(account.isIn(container.getPermissions().keySet())) {
                result.add(container);
            }
        }
        return result;
    }

    /**
     * open a container with the given name from the current parent
     * if the name is null or empty, it will return the current parent's children
     * if the name is "..", it will move back to the parent
     * if the account is not in the container's permissions, it will throw an exception
     * if the account is in the container's permissions, it will return the children of the container
     * @param name;
     * @return ArrayList&lt;Container&gt;
     * @throws Exception
     */
    public ArrayList<Container> openContainer(String name, String query) throws Exception {
        if(query != null && !query.isEmpty()) {
            ArrayList<Container> result = new ArrayList<>();
            for(String key : suggestions.keySet()) {
                if(key.contains(query)) {
                    result.add(suggestions.get(key));
                }
            }
            return result;
        }
        if(name == null || name.isEmpty()) {
            loadSuggestions();
            return getContainers( currentParent );
        }
        if(name.equals("..")) {
            if(currentParent.getPath().isEmpty()) return getContainers(null);
            currentParent = currentParent.getPath().getLast();
            loadSuggestions();
            return getContainers(currentParent);
        }
        for(Container container : currentParent.getChildren()) {
            if(container.getName().equals(name)) {
                if(!account.isIn(container.getPermissions().keySet())) {
                    throw new Exception("permission");
                }
                if(container instanceof Folder) {
                    currentParent = (Folder) container;
                    loadSuggestions();
                    return getContainers(currentParent);
                }
                if(container instanceof Item) {
                    throw new Exception("item");
                }
            }
        }
        return getContainers(currentParent);
    }
    /**
     * open a container with the given name from the current parent
     * if the name is null or empty, it will return the current parent's children
     * if the name is "..", it will move back to the parent
     * if the account is not in the container's permissions, it will throw an exception
     * if the account is in the container's permissions, it will return the children of the container
     * @param name;
     * @throws Exception
     */
    public ArrayList<Container> openContainer(String name) throws Exception {
        return openContainer(name, null);
    }


    /**
     * open an item with the given name from the current parent
     * if the account is not in the current parent's permissions, it will throw an exception
     * if the account is in the current parent's permissions, it will return the item
     * if the container is a folder, it will throw an exception
     * if the container is not found, it will return null
     * @param name;
     * @return Item;
     * @throws Exception
     */
    public Item openItem(String name) throws Exception {
        for(Container container : currentParent.getChildren()) {
            if(container.getName().equals(name)) {
                if(container instanceof Item) {
                    if(!account.isIn(container.getPermissions().keySet())) throw new Exception("permission");
                    return (Item) container;
                }
                else if(container instanceof Folder) {
                    throw new Exception("folder");
                }
            }
        }
        throw new Exception("not found");
    }

    /**
     * add a folder to the current parent
     * if current parent is root, the account will have edit permission
     * if the account is not in the current parent's permissions, it will throw an exception
     * if the account is in the current parent's permissions, it will have the same permission as the parent
     * @param name
     * @param permissions
     * @throws Exception
     * @return new folder;
     */
    public Folder addFolderToCurrentParent(
            String name,
            HashMap<Account, Permission> permissions
    ) throws Exception {
        HashMap<Account, Permission> permissions_n = (HashMap<Account, Permission>) permissions.clone();

        if(currentParent.getName().equals("root")) {
            if(!account.isIn(permissions_n.keySet()))
                permissions_n.put(account, Permission.ADMIN);
        } else {
            if(!account.isIn(currentParent.getPermissions().keySet())) {
                throw new Exception("permission denied");
            }
            if(!account.isIn(permissions_n.keySet())) {
                permissions_n.put(account, currentParent.getPermissions().get(account));
            }
        }
        Folder folder = currentParent.newFolder(name, containers, permissions_n,account);
        sort();
        return folder;
    }

    /**
     * add an item to the current parent
     * if current parent is root, the account will have edit permission
     * if the account is not in the current parent's permissions, it will throw an exception
     * if the account is in the current parent's permissions, it will have the same permission as the parent
     * @param name
     * @param description
     * @param longText
     * @param imgs
     * @param permissions
     * @throws Exception
     */
    public Item addItemToCurrentParent(
            String name,
            String description,
            String longText,
            ArrayList<String> imgs,
            HashMap<Account, Permission> permissions) throws Exception {
        HashMap<Account, Permission> permissions_n = (HashMap<Account, Permission>) permissions.clone();
        if(currentParent.getName().equals("root")) {
            if(!account.isIn(permissions_n.keySet()))
                permissions_n.put(account, Permission.ADMIN);
        } else {
            if(!account.isIn(currentParent.getPermissions().keySet())) {
                throw new Exception("permission");
            }
            if(!account.isIn(permissions_n.keySet())) {
                permissions_n.put(account, currentParent.getPermissions().get(account));
            }
        }
        Item item = currentParent.newItem(name, description, longText, imgs, permissions_n, containers, account);
        sort();
        return item;
    }

    //endregion

    //region utility

    /**
     * log out the account
     */
    public void logout() {
        account = null;
        containers = null;
        currentParent = null;
        comparator = null;
    }

    /*
     * remove current account from the file
     */
    public void deleteAccount() {
        try {
            ArrayList<Account> accounts = Account.getAllAccounts();
            for (Account account : accounts) {
                if (account.equals(this.account)) {
                    accounts.remove(account);
                    break;
                }
            }
            Account.saveToFile(accounts);
        } catch (Exception e) {
            e.printStackTrace();
        }
        logout();
    }

    /**
     * set the default sorting method and sort the containers
     *
     * @param comparator;
     */
    public void sortBy(Comparator<Container> comparator) {
        this.comparator = comparator;
        sort();
    }

    /**
     * sort the containers
     */
    protected void sort() {
        containers.sort(comparator);
        containers.sort(new TypeComparator());
        currentParent.sortChildren(comparator);
        currentParent.sortChildren(new TypeComparator());
    }

    /**
     * delete the current container
     * and delete all children recursively
     * @throws Exception
     */
    public void deleteCurrent() throws Exception {
        Folder parent = currentParent.delete(account);
        parent.getChildren().remove(currentParent);
        if(parent.getName().equals("root")) {
            containers.remove(currentParent);
        }
        currentParent = parent;
        Container.saveFullDataToFile(currentParent);
    }

    /**
     * add a full tree to the current parent from the file system
     * if the account is not in the current parent's permissions, it will throw an exception
     * if the account is in the current parent's permissions, it will have the same permission as the parent
     * @param root absolute path to the root folder
     * @param innerParent innitially null
     * @throws Exception
     */
    public void addTree(String root, Folder innerParent) throws Exception {
        File parent = new File(root);
        if(!parent.exists()) {
            throw new Exception("Folder does not exist");
        }
        if(parent.isFile()) {
            throw new Exception("Not a folder");
        }
        if(innerParent == null) {
            innerParent = currentParent;
        }
        Permission perm = Permission.READ;
        for(Account account : innerParent.getPermissions().keySet()) {
            if(account.equals(this.account)) {
                perm = innerParent.getPermissions().get(account);
                break;
            }
        }
        if(innerParent.getName().equals("root")) {
            perm = Permission.ADMIN;
        }
        if(perm.lessThan(Permission.EDIT)) {
            throw new Exception("Permission denied");
        }

        Folder rootFolder = addFolderToCurrentParent(parent.getName(), currentParent.getPermissions());
        openContainer(parent.getName());

        ArrayList<Item> items = new ArrayList<>();

        for(File file : parent.listFiles()) {
            if(file.isDirectory()) {
                addTree(file.getAbsolutePath(), rootFolder);
            } else {
                Item item_M = items.stream().filter(i -> i.getName().equals(file.getName().split("\\.")[0])).findFirst().orElse(null);
                if(item_M != null) {
                    if(file.getName().endsWith(".txt")) {
                        StringBuilder longText = new StringBuilder();
                        try (BufferedReader reader = new BufferedReader(new FileReader(file.getAbsolutePath()))) {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                longText.append(line).append("\n");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        item_M.setText(longText.toString(), account);
                    } else if(FileNames.IMG_EXTENSIONS.contains(
                            file.getName().substring(file.getName().lastIndexOf(".")))
                    ) {
                        item_M.addImg(file.getAbsolutePath(), account);
                    }
                    continue;
                }
                ArrayList<String> imgs = new ArrayList<>();
                StringBuilder longText = new StringBuilder();
                if(file.getName().endsWith(".txt")) {
                    try (BufferedReader reader = new BufferedReader(new FileReader(file.getAbsolutePath()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            longText.append(line).append("\n");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if(FileNames.IMG_EXTENSIONS.contains(
                        file.getName().substring(file.getName().lastIndexOf(".")))
                ) {
                    imgs.add(file.getAbsolutePath());
                }
                imgs.add(file.getAbsolutePath());
                Item item = addItemToCurrentParent(file.getName().split("\\.")[0],
                        "",
                        longText.toString(),
                        imgs,
                        currentParent.getPermissions());
                items.add(item);
            }
        }


    }


    //endregion

}
