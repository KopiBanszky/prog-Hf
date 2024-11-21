package System.Tree;

import java.io.Serializable;
import java.util.HashMap;

import Config.Permission;
import System.Account;

public class Comment implements Serializable {
    private String comment;
    private Account writer;
    private boolean resolved = false;


    public Comment(String comment, Account writer) {
        this.comment = comment;
        this.writer = writer;
    }

    public String getComment() {
        return comment;
    }

    public Account getWriter() {
        return writer;
    }

    public boolean isResolved() {
        return resolved;
    }

    /**
     * Resolves the comment
     * can be ran once
     * @param account the account that wants to resolve the comment
     * @param permissions of the item that the comment is on
     * @throws IllegalArgumentException if the account doesn't have permission to resolve the comment
     */
    public void resolve(Account account, HashMap<Account, Permission> permissions) {
        if(!account.isIn(permissions.keySet())) {
            throw new IllegalArgumentException("You don't have permission to resolve this comment");
        }
        resolved = true;
    }
}
