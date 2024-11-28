package System.Tree;

import Config.FileNames;
import Config.Permission;
import System.Account;

import java.io.*;
import java.util.*;

enum ContainerType {
    FOLDER,
    FILE
}

public abstract class Container implements Serializable {
    protected String name;
    protected int id;
    protected HashMap<Account, Permission> permissions;
    protected ArrayList<Folder> path;
    protected ContainerType type;
    protected Date lastModified;

    protected static final String[] disabledWords = {"root", "..", "/", "\\\\\\", "///" };



    public int getId() {
        return id;
    }

    public Date getModificationDate() {
        return lastModified;
    }

    //region Name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.lastModified.setTime(System.currentTimeMillis());
        try {
            saveFullDataToFile(this.path.getFirst());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //endregion

    //region Permissions
    public HashMap<Account, Permission> getPermissions() {
        return permissions;
    }

    public void addPermission(Account account, Permission permission) throws Exception {
        permissions.put(account, permission);
    }

    public void removePermission(Account account) {
        permissions.remove(account);
    }

    public void setPermissions(HashMap<Account, Permission> permissions) {
        this.permissions = permissions;
        try {
            saveFullDataToFile(this.path.getFirst());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //endregion

    //region Path
    public ArrayList<Folder> getPath() {
        return path;
    }

    /**
     * sets the path of the container
     * to move the folder to a different location
     * @param path
     * @throws Exception
     */
    public void setPath(ArrayList<Folder> path, Account account) throws Exception {
        if(path.contains(this)) {
            throw new Exception("Cannot move to a child folder");
        }
        if(permissions.get(account).getValue() < Permission.EDIT.getValue()) {
            throw new Exception("Permission denied");
        }
        this.path = path;
    }

    /**
     * gives the path of the container as a string in the format of folders
     * @return example: "FolFolFol"
     *
     * needs to be overridden in the Item class
     */
    public String getReadableId() {
        return pathFolders();
    }

    /**
     * gives the path of the container as a string in the format of folders
     * @return example: "FolFolFol"
     */
    protected String pathFolders() {
        StringBuilder folderBuilder = new StringBuilder();
        for (Folder folder : path) {
            if(folder.name.equals("root")) continue;
            String folderNameLow = folder.getName().toLowerCase().substring(1, 3);
            folderBuilder.append(
                    folder
                            .getName()
                            .substring(0, 1)
                            .toUpperCase()
                    )
                    .append(folderNameLow);
        }
        return folderBuilder.toString();
    }

    /**
     * @return the path of the container as a string in the format of ids
     * @return example: "1-2-3"
     */
    protected String pathIds() {
        StringBuilder folderBuilder = new StringBuilder();
        for (Folder folder : path) {
            if (folder.name.equals("root")) continue;
            folderBuilder.append(folder.getId()).append("-");
        }
        return folderBuilder.toString();
    }
    //endregion

    //region utility
    //endregion

    //region file management

    /**
     * saves the container to the file
     * @throws Exception
     * @deprecated this works only if the container is not in the file and if the container is in the root. should not be used
     */
    public void saveToFile() throws Exception {
        Folder containers = getFullDataFromFile();
        containers.getChildren().add(this);
        saveFullDataToFile(containers);
    }

    /**
     * gets all the data from the file
     * @return all the data from the file
     * @throws Exception
     */
    public static Folder getFullDataFromFile() throws Exception {
        try {
            //System.out.println(FileNames.FILE);
            File file = new File(FileNames.FOLDER + "/" + FileNames.FILE);
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream( file ));
            Folder results = (Folder) ois.readObject();
            ois.close();
            if(results == null) {
                return new Folder();
            }
            return results;
        } catch (FileNotFoundException e) {
            return new Folder();
        }
    }

    /**
     * saves the given data to the file
     * @param root the data to be saved to the file
     * @throws Exception
     */
    public static void saveFullDataToFile(Folder root) throws Exception {
        ObjectOutputStream bos = new ObjectOutputStream(new FileOutputStream(FileNames.FOLDER + "/" + FileNames.FILE));
        bos.writeObject(root);
        bos.close();
    }

    //endregion

    /**
     * counts the number of containers in the file and returns the next id
     * @param containers
     * @return next id
     */
    protected static int newId(ArrayList<Container> containers) {
        int max = 0;
        for (Container container : containers) {
            if (container.getId() > max) {
                max = container.getId();
            }
        }
        return max + 1;
    }

    /**
     * @return the last modified date in a readable format
     */
    public String getReadableLastModified() {
        return lastModified.toString();
    }

    /**
     * @param account
     * @return the type of the container
     */
    public Permission getPermission(Account account) {
        for (Account acc : permissions.keySet()) {
            if(acc.equals(account)) {
                return permissions.get(acc);
            }
        }
        return null;
    }
}
