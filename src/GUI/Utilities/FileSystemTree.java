package GUI.Utilities;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;

import java.io.File;

public class FileSystemTree extends JFrame {

    public FileSystemTree() {
        super();
        setTitle("File System Viewer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create the root node representing the file system root
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("File System");

        // Populate the tree with file system nodes
        File[] roots = File.listRoots();
        if (roots != null) {
            for (File rootFile : roots) {
                DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(rootFile.getAbsolutePath());
                root.add(rootNode);
                populateTree(rootNode, rootFile);
            }
        }

        // Create the JTree
        JTree fileTree = new JTree(root);
        fileTree.setRootVisible(true); // Show the root

        // Add the tree to a scroll pane
        JScrollPane scrollPane = new JScrollPane(fileTree);

        // Add the scroll pane to the frame
        add(scrollPane);
        setSize(600, 400);


    }

    // Recursively populate the tree with directories and files
    private static void populateTree(DefaultMutableTreeNode parentNode, File file) {
        File[] files = file.listFiles();
        if (files != null) {
            for (File childFile : files) {
                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(childFile.getName());
                parentNode.add(childNode);
                if (childFile.isDirectory()) {
                    populateTree(childNode, childFile); // Recursive call for directories
                }
            }
        }
    }
}
