package System.Tree;

import java.util.*;

import Config.Permission;
import System.Account;

public class Folder extends Container {
    private ArrayList<Container> children;

    public Folder(String name, Folder parent, HashMap<Account, Permission> permissions) throws Exception{
        if(Arrays.stream(disabledWords).toList().contains(name)) throw new Exception("Disabled name");
        this.name = name;
        this.permissions = permissions;
        this.path = (ArrayList<Folder>) parent.getPath().clone();
        this.path.add(parent);
        this.id = newId(parent.getChildren());
        this.lastModified = new Date();
        children = new ArrayList<>();
        this.type = ContainerType.FOLDER;
        this.lastModified = new Date();
    }

    public Folder() {
        this.name = "root";
        this.permissions = new HashMap<Account, Permission>();
        this.path = new ArrayList<>();
        //this.id = -1;
        this.children = new ArrayList<Container>();
    }

    public Folder(String name, int id, ArrayList<Folder> path) {
        this.name = name;
        //this.id = id;
        this.path = path;
    }

    public ArrayList<Container> getChildren() {
        return children;
    }

    public void addChild(Container child) throws Exception {
        children.add(child);
    }

    public void addChild(ArrayList<Container> children) throws Exception {
        this.children.addAll(children);
    }

    /**
     * Create a new folder in the folder
     * if the account does not have permission, it will throw an exception
     * it will save to the file
     * @param name
     * @param allContainers
     * @param permissions
     * @param account
     * @throws Exception
     * @return the new folder
     */
    public Folder newFolder(String name, ArrayList<Container> allContainers, HashMap<Account, Permission> permissions, Account account) throws Exception{
        HashMap<Account, Permission> permissions_n = (HashMap<Account, Permission>) permissions.clone();

        Folder folder = new Folder(name, this, permissions_n);
        if(this.name.equals("root")) {
            allContainers.add(folder);
            addChild(folder);
        } else
            addChild(folder);
        saveFullDataToFile(allContainers.getFirst().getPath().getFirst());
        return folder;
    }

    /**
     * Create a new item in the folder
     * if the account does not have permission, it will throw an exception
     * it will save to the file
     * @param name
     * @param description
     * @param longText
     * @param imgs
     * @param permissions
     * @param allContainers
     * @param account
     * @throws Exception
     */
    public Item newItem(String name, String description, String longText, ArrayList<String> imgs, HashMap<Account, Permission> permissions ,ArrayList<Container> allContainers, Account account) throws Exception {
        HashMap<Account, Permission> permissions_n = (HashMap<Account, Permission>) permissions.clone();
        Item item = longText != null ? new Item(name, this, permissions_n, description, longText) :
                new Item(name, this, permissions_n, description);
        if(this.name.equals("root")) {
            allContainers.add(item);
            addChild(item);
        } else
            addChild(item);
        item.addImgs(imgs, account);
        saveFullDataToFile(allContainers.getFirst().getPath().getFirst());
        return item;
    }

    /**
     * Sort the children of the folder
     * wont save sortings
     * @param comparator
     */
    public void sortChildren(Comparator<Container> comparator) {
        children.sort(comparator);
    }

    /**
     * Remove a child from the folder
     * if the account does not have permission, it will throw an exception
     * @param child;
     * @param account;
     * @throws Exception
     */
    public void removeChild(Item child, Account account) throws Exception {
        if(!account.isIn(child.getPermissions().keySet())) {
            throw new Exception("Permission denied");
        }
        try {
            child.delete(account);
        } catch (Exception e) {
            if(e.getMessage().equals("You do not have permission to delete this item")) {
                throw new Exception(e.getMessage());
            }
            if(e.getMessage().equals("You do not have permission to edit this item")) {
                throw new Exception("You do not have permission to delete this item");
            }
        }
        children.remove(child);
        if(!this.name.equals("root")) {
            saveFullDataToFile(path.getFirst());
        }
        saveFullDataToFile(this);
    }

    /**
     * Delete all children recursively, wont remove the containers from the file
     * if the account does not have permission, it will throw an exception
     * @param account;
     * @return the parent of the folder
     * @throws Exception
     */
    public Folder delete(Account account) throws Exception {
        if(!isPermissionGrantedInTree(account, Permission.EDIT)) {
            throw new Exception("Permission denied");
        }
        Folder parent = this.path.get(this.path.size() - 1);
        for(Container child : children ) {
            if(child instanceof Folder) {
                ((Folder)child).delete(account);

            }
            else if (child instanceof Item) {
                try {
                    ((Item)child).delete(account);
                } catch (Exception e) {
                    if(e.getMessage().equals("You do not have permission to delete this item")) {
                        throw new Exception(e.getMessage());
                    }
                    if(e.getMessage().equals("You do not have permission to edit this item")) {
                        throw new Exception("You do not have permission to delete this item");
                    }
                }
            }
        }

        //parent.getChildren().remove(this);
        return parent;
    }

    /**
     * Check if the account has all permissions in the tree
     * @param account;
     * @param permission;
     * @return true if the account has all permissions in the tree
     */
    public boolean isPermissionGrantedInTree(Account account, Permission permission) {
        boolean res = true;
        for(Container child : children) {
            Permission storedPermission = Permission.READ;
            for(Account acc : child.getPermissions().keySet()) {
                if(acc.getName().equals(account.getName())) {
                    storedPermission = child.getPermissions().get(acc);
                }
            }
            if(storedPermission == null || storedPermission.lessThan(permission)) {
                res = false;
                break;
            } else if(child instanceof Folder) {
                res = ((Folder) child).isPermissionGrantedInTree(account, permission);
                if(!res) break;
            }
        }
        return res;
    }

    @Override
    public String toString() {
        return "Folder: " + name + " -> " + getReadableId();
    }
}
