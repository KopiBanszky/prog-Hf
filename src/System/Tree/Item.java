package System.Tree;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import Config.FileNames;
import Config.Permission;
import System.Account;

public class Item extends Container {
    private String description;
    private ArrayList<Comment> comments;
    private Integer imgs;

    Item(String name, Folder parent, HashMap<Account, Permission> permissions, String description) throws Exception {
        this.name = name;
        this.permissions = permissions;
        this.path = (ArrayList<Folder>) parent.path.clone();
        this.path.add(parent);
        this.description = description;
        this.comments = new ArrayList<Comment>();
        this.imgs = 0;
        this.id = newId(parent.getChildren());
        this.lastModified = new Date();
    }

    Item(String name, Folder parent, HashMap<Account, Permission> permissions, String description, String longText) throws Exception{
        this.name = name;
        this.permissions = permissions;
        this.path = (ArrayList<Folder>) parent.path.clone();
        this.path.add(parent);
        this.description = description;
        this.comments = new ArrayList<Comment>();
        this.imgs = 0;
        this.id = newId(parent.getChildren());
        this.lastModified = new Date();
        saveTextToFile(longText);
    }

    Item(String name, Folder parent, HashMap<Account, Permission> permissions, String description, Integer imgs) {
        this.name = name;
        this.permissions = permissions;
        this.path = (ArrayList<Folder>) parent.path.clone();
        this.path.add(parent);
        this.description = description;
        this.comments = new ArrayList<Comment>();
        this.imgs = imgs;
        this.id = newId(parent.getChildren());
        this.lastModified = new Date();
    }

    Item(String name, Folder parent, HashMap<Account, Permission> permissions, String description, String longText, Integer imgs) throws Exception {
        this.name = name;
        this.permissions = permissions;
        this.path = (ArrayList<Folder>) parent.path.clone();
        this.path.add(parent);
        this.description = description;
        this.comments = new ArrayList<Comment>();
        this.imgs = imgs;
        this.id = newId(parent.getChildren());
        this.lastModified = new Date();

        saveTextToFile(longText);
    }

    /**
     * Get the description of the item
     * if the account does not have permission, it will throw an exception
     * @param account
     * @return the description
     * @throws Exception
     */
    public String getDescription(Account account) throws Exception {
        if(!account.isIn(permissions.keySet())) {
            throw new Exception("You do not have permission to read this item");
        }
        return description;
    }

    /**
     * Set the description of the item
     * if the account does not have permission, it will throw an exception
     * it wont save to the file
     * @param description
     * @param account
     * @throws Exception
     */
    public void setDescription(String description, Account account) throws Exception {
        if(getPermission(account).getValue() < 2) {
            throw new Exception("You do not have permission to edit this item");
        }
        this.description = description;
        lastModified.setTime(System.currentTimeMillis());
    }

    /**
     * Get the text of the item
     * if the account does not have permission, it will throw an exception
     * @param account;
     * @return the text
     * @throws Exception;
     */
    public String getText(Account account) throws Exception {
        if(!account.isIn(permissions.keySet())) {
            throw new Exception("You do not have permission to read this item");
        }
        return readTextFromFile();
    }

    /**
     * Set the text of the item
     * if the account does not have permission, it will throw an exception
     * @param text;
     * @param account;
     * @throws Exception;
     */
    public void setText(String text, Account account) throws Exception {
        if(getPermission(account).getValue() < 2) {
            throw new Exception("You do not have permission to edit this item");
        }
        saveTextToFile(text);
        lastModified.setTime(System.currentTimeMillis());
    }

    /**
     * Get the comments of the item
     * if the account does not have permission, it will throw an exception
     * @param account
     * @return imgs relative paths
     * @throws Exception
     */
    public ArrayList<String> getImgs(Account account) throws Exception {
        if(!account.isIn(permissions.keySet())) {
            throw new Exception("You do not have permission to read this item");
        }
        ArrayList<String> imgs = new ArrayList<String>();
        File folder = new File(FileNames.FOLDER + "/" + FileNames.IMGS_FOLDER);
        File[] f_imgs = folder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith("img_" + getUniqueID() + "_");
            }
        });
        for(File f_img : f_imgs) {
            imgs.add(FileNames.FOLDER + "/" + FileNames.IMGS_FOLDER + "/" + f_img.getName());
        }
        return imgs;
    }


    /**
     * Set the imgs of the item
     * if the account does not have permission, it will throw an exception
     * it will save to the file
     * @param imgs absolute paths
     * @throws Exception;
     */
    public void setImgs(ArrayList<String> imgs, Account account) throws Exception {
        if(getPermission(account).getValue() < 2) {
            throw new Exception("You do not have permission to edit this item");
        }
        removeImgs(account);
        addImgs(imgs, account);
        lastModified.setTime(System.currentTimeMillis());
        saveFullDataToFile(path.getFirst());
    }

    /**
     * Add an img to the item
     * if the account does not have permission, it will throw an exception
     * it will save to the file
     * @param img absolute path
     * @param account
     * @throws Exception
     */
    public void addImg(String img, Account account) throws Exception {
        if(getPermission(account).getValue() < 2) {
            throw new Exception("You do not have permission to edit this item");
        }
        this.imgs++;
        Path source = Paths.get(img);
        Path destination = Paths.get(FileNames.FOLDER + "/" + FileNames.IMGS_FOLDER + "/" +
                "img_" + getUniqueID() + "_" + this.imgs +
                "." + img.substring(img.lastIndexOf(".") + 1));
        Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
        lastModified.setTime(System.currentTimeMillis());
        saveFullDataToFile(path.getFirst());
    }

    /**
     * Add imgs to the item
     * if the account does not have permission, it will throw an exception
     * it will save to the file
     * @param imgs absolute paths
     * @param account
     * @throws Exception
     */
    public void addImgs(ArrayList<String> imgs, Account account) throws Exception {
        for(String img : imgs) {
            addImg(img, account);
        }
        lastModified.setTime(System.currentTimeMillis());
        saveFullDataToFile(path.getFirst());
    }

    /**
     * Remove an img from the item
     * if the account does not have permission, it will throw an exception
     * it will remove the file
     * @param img name;
     * @param account;
     * @throws Exception;
     */
    public void removeImg(String img, Account account) throws Exception{
        if(getPermission(account).getValue() < 2) {
            throw new Exception("You do not have permission to edit this item");
        }
        this.imgs--;
        Files.delete(Path.of(img));
        lastModified.setTime(System.currentTimeMillis());
    }

    /**
     * Remove all imgs from the item
     * if the account does not have permission, it will throw an exception
     * it will remove the files
     * @param account;
     * @throws Exception;
     */
    public void removeImgs(Account account) throws Exception {
        if(getPermission(account).getValue() < 2) {
            throw new Exception("You do not have permission to edit this item");
        }
        File folder = new File(FileNames.FOLDER + "/" + FileNames.IMGS_FOLDER);
        File[] f_imgs = folder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith("img_" + getUniqueID() + "_");
            }
        });
        for(File f_img : f_imgs) {
            f_img.delete();
        }
        lastModified.setTime(System.currentTimeMillis());
    }

    /**
     * Remove the longText file
     */
    private void removeLongText() throws Exception {
        File file = new File(FileNames.FOLDER + "/" + FileNames.TEXT_FOLDER + "/" + "longtext_" + getUniqueID() + ".txt");
        Files.delete(Path.of(file.getPath()));
        lastModified.setTime(System.currentTimeMillis());
    }

    /**
     * delete the item with all files
     * @param account
     * @throws Exception
     */
    public void delete(Account account) throws Exception {
        if(getPermission(account).getValue() < 2) {
            throw new Exception("You do not have permission to delete this item");
        }
        removeImgs(account);
        removeLongText();
    }

    /**
     * Save the longText to a file
     * @param longText
     * @throws Exception
     */
    private void saveTextToFile(String longText) throws Exception {
        File file = new File(FileNames.FOLDER + "/" + FileNames.TEXT_FOLDER + "/" + "longtext_" + getUniqueID() + ".txt");
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(longText);
        writer.close();
    }

    /**
     * Read the longText from a file
     * @return
     * @throws Exception
     */
    private String readTextFromFile() throws Exception {
        File file = new File(FileNames.FOLDER + "/" + FileNames.TEXT_FOLDER + "/" + "longtext_" + getUniqueID() + ".txt");
        BufferedReader reader = new BufferedReader(new java.io.FileReader(file));
        StringBuilder text = new StringBuilder();
        String line;
        while((line = reader.readLine()) != null) {
            text.append(line);
            text.append("\n");
        }
        return text.toString();
    }

    /**
     * Get the readable id of the item
     * @return format: "FolFolFol1"
     */
    @Override
    public String getReadableId() {
        return pathFolders() + id;
    }

    /**
     * Get the unique id of the item
     * @return format: "1-2-3-1"
     */
    public String getUniqueID() {
        return pathIds() + "-" + id;
    }

    /**
     * Add a comment to the item
     * if the account does not have permission, it will throw an exception
     * @param comment
     * @param account
     */
    public Comment addComment(String comment, Account account) {
        if(!account.isIn(permissions.keySet())) {
            throw new IllegalArgumentException("You do not have permission to comment on this item");
        }
        Comment com = new Comment(comment, account);
        comments.add(com);
        try {
            Container.saveFullDataToFile(path.getFirst());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return com;
    }

    public void resolveComment(Comment comment, Account account) {
        if(!account.isIn(permissions.keySet())) {
            throw new IllegalArgumentException("You do not have permission to resolve comments on this item");
        }
        comment.resolve(account, permissions);
        try {
            Container.saveFullDataToFile(path.getFirst());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the comments of the item
     * if the account does not have permission, it will throw an exception
     * @param account;
     * @return the comments
     */
    public ArrayList<Comment> getComments(Account account) {
        if(!account.isIn(permissions.keySet())) {
            throw new IllegalArgumentException("You do not have permission to read the comments of this item");
        }
        return comments;
    }

    @Override
    public String toString() {
        return "Item: " + name + " -> " + getReadableId() ;
    }

}
