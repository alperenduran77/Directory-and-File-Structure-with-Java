import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.sql.Timestamp;

public class FileSystem {
    private Directory root;
    private Directory currentDirectory;

    public Directory getRoot() {
        return root;
    }

    // This method starts the process and is called to print the whole directory
    // tree.
    public void printTree() {
        System.out.println("Path to current directory from root:");
        printDirectoryTree(root, "", currentDirectory);
    }

    public void printWholeTree() {
        System.out.println("Whole tree from root:");
        printWholeTree(root, "", currentDirectory);
    }

    private void printWholeTree(Directory dir, String prefix, Directory current) {
        // Print the directory path
        System.out.println(prefix + "* " + dir.getName() + (dir == current ? "/ (Current Directory)" : "/"));

        // If this directory has children, recurse into them
        for (FileSystemElement element : dir.getChildren()) {
            if (element instanceof Directory) {
                // Recursively call this method with updated prefix
                printDirectoryTree((Directory) element, prefix + "    ", current);
            } else {
                // It's a file, print with the appropriate prefix
                System.out.println(prefix + "    " + element.getName());
            }
        }
    }
    
    private void printDirectoryTree(Directory dir, String prefix, Directory current) {
        // Print the directory path
        System.out.println(prefix + "* " + dir.getName() + (dir == current ? "/ (Current Directory)" : "/"));

        // Only recurse into subdirectories if dir is not the current directory
        if (dir != current) {
            for (FileSystemElement element : dir.getChildren()) {
                if (element instanceof Directory) {
                    // Recursively call this method with updated prefix
                    printDirectoryTree((Directory) element, prefix + "    ", current);
                }
            }
        } else {
            // If this is the current directory, print its contents (both files and
            // subdirectories)
            for (FileSystemElement element : dir.getChildren()) {
                System.out.println(prefix + "    * " + element.getName() + (element instanceof Directory ? "/" : ""));
            }
        }
    }
 
    public FileSystem() {
        this.root = new Directory("root", null, null);
        this.currentDirectory = root; // Start with the root as the current directory
    }

    // Change the current directory to the one specified by the path
    public Directory changeDirectory(String path) {
        if (path.equals("/")) {
            return root; // Return root without changing currentDirectory
        }

        String[] parts = path.substring(1).split("/");
        Directory current = root;

        for (String part : parts) {
            if (part.isEmpty()) {
                continue; // Skip any empty parts resulting from consecutive slashes
            }
            Directory next = current.getSubdirectory(part);
            if (next == null) {
                // Directory not found at this part of the path
                return null;
            }
            current = next;
        }

        return current; // Return the new directory without changing currentDirectory
    }

    // Create a new file in the current directory
    public void createFile(String name) {
        // Check if the file already exists in the current directory
        if (currentDirectory.findElement(name) != null) {
            throw new IllegalArgumentException("File already exists.");
        }
        Timestamp dateCreated = new Timestamp(System.currentTimeMillis());
        File file = new File(name, currentDirectory, dateCreated);
        currentDirectory.addElement(file);
    }

    // Create a new directory in the current directory
    public void createDirectory(String name) {
        // Check if the directory already exists in the current directory
        if (currentDirectory.findElement(name) != null) {
            throw new IllegalArgumentException("Directory already exists.");
        }
        Timestamp dateCreated = new Timestamp(System.currentTimeMillis());
        Directory newDir = new Directory(name, currentDirectory, dateCreated);
        currentDirectory.addElement(newDir);
    }
    
    // List the contents of the current directory
    public void listDirectoryContents() {
        System.out.println("Contents of " + currentDirectory.getName() + ":");
        currentDirectory.getChildren().forEach(fse -> {
            System.out.println(fse.getName() + (fse instanceof Directory ? "/" : ""));
        });
    }

    // Search for a file or directory in the entire file system for help deleting files
    private FileSystemElement searchEntireFileSystem(Directory dir, String name) {
        if (dir.getName().equals(name)) {
            return dir;
        }

        for (FileSystemElement fse : dir.getChildren()) {
            if (fse.getName().equals(name)) {
                return fse;
            }
            if (fse instanceof Directory) {
                FileSystemElement searchResult = searchEntireFileSystem((Directory) fse, name);
                if (searchResult != null) {
                    return searchResult;
                }
            }
        }
        return null; // Not found in this branch of the file system
    }
    
    // Delete a file or directory from the file system
    public void deleteFileOrDirectory(String name) {
        FileSystemElement elementToDelete = searchEntireFileSystem(root, name);
        if (elementToDelete == null) {
            throw new IllegalArgumentException("File or directory does not exist.");
        }

        // Before deletion, check if the current directory is the one being deleted
        // or if it is a subdirectory of the directory being deleted
        boolean isCurrentOrDescendant = isDescendant(currentDirectory, elementToDelete);

        deleteRecursively(elementToDelete);

        // If the element has a parent, remove it from the parent's children list
        if (elementToDelete.getParent() != null) {
            elementToDelete.getParent().removeElement(elementToDelete);
        }

        if (isCurrentOrDescendant) {
            // If current directory was deleted or was a subdirectory of the deleted
            // directory,
            // set the current directory to the root or the nearest existing parent.
            setCurrentDirectoryToNearestValid(elementToDelete);
        }
    }

    // Helper method to check if 'current' is a descendant of 'elementToDelete'
    private boolean isDescendant(Directory current, FileSystemElement elementToDelete) {
        // Check if current directory is a descendant of the directory to delete
        FileSystemElement currentParent = current;
        while (currentParent != null) {
            if (currentParent == elementToDelete) {
                return true;
            }
            currentParent = currentParent.getParent();
        }
        return false;
    }

    // Helper method to set the current directory to the nearest valid parent
    private void setCurrentDirectoryToNearestValid(FileSystemElement deletedElement) {
        // Find the nearest valid parent that is not the deleted element
        FileSystemElement nearestValidParent = deletedElement.getParent();
        while (nearestValidParent != null && nearestValidParent == deletedElement) {
            nearestValidParent = nearestValidParent.getParent();
        }

        // Update currentDirectory to the nearest valid parent if it exists,
        // otherwise, fall back to the root directory.
        currentDirectory = nearestValidParent instanceof Directory ? (Directory) nearestValidParent : root;
    }
    
    // Recursively delete a file or directory and its children
    private void deleteRecursively(FileSystemElement fse) {
        if (fse instanceof Directory) {
            Directory dir = (Directory) fse;
            List<FileSystemElement> childrenCopy = new ArrayList<>(dir.getChildren());
            for (FileSystemElement child : childrenCopy) {
                deleteRecursively(child); // Recursively delete children
                dir.removeElement(child); // Remove each child after deletion
            }
        }
    }

    // Move a file or directory to a new path
    public void moveFileOrDirectory(String name, String newPathString) {
        // First, find the file/directory to move within the current directory
        FileSystemElement elementToMove = currentDirectory.findElement(name);
        if (elementToMove == null) {
            throw new IllegalArgumentException("The file/directory to move does not exist.");
        }

        // Find the target directory where we want to move the file/directory
        Directory newParentDirectory = changeDirectory(newPathString);
        if (newParentDirectory == null) {
            throw new IllegalArgumentException("The new path for the file/directory does not exist.");
        }

        // Prevent moving a directory into itself or its subdirectories
        if (elementToMove instanceof Directory && (elementToMove == newParentDirectory
                || isDescendant((Directory) elementToMove, newParentDirectory))) {
            throw new IllegalArgumentException("Cannot move a directory inside itself or its subdirectories.");
        }

        // If the element has a parent, remove it from the parent's children list
        Directory oldParent = (Directory) elementToMove.getParent(); // Cast to Directory
        if (oldParent != null) {
            oldParent.removeElement(elementToMove);
        }

        // Set the new parent for the element
        elementToMove.setParent(newParentDirectory);

        // Add the element to the new parent directory's children list
        newParentDirectory.addElement(elementToMove);
    }

    // Helper method to check if 'target' is a descendant of 'dir'
    private boolean isDescendant(Directory dir, Directory target) {
        while (target != null) {
            if (target == dir) {
                return true;
            }
            target = target.getParent();
        }
        return false;
    }
    
    public void setCurrentDirectory(Directory newCurrentDirectory) {
        this.currentDirectory = newCurrentDirectory;
    }
    
    // Search for a file or directory in the entire file system
    public String searchFileOrDirectory(String name) {
        return searchRecursive(root, name, "");
    }
    
    // Recursive method to search for a file or directory in the file system
    private String searchRecursive(Directory current, String name, String path) {
        // Check all elements in the current directory
        for (FileSystemElement element : current.getChildren()) {
            // If the name matches, return the full path to the element
            if (element.getName().equals(name)) {
                return path + "/" + name;
            }
            // If it's a directory, go deeper
            if (element instanceof Directory) {
                String result = searchRecursive((Directory) element, name, path + "/" + element.getName());
                if (!result.isEmpty()) {
                    return result; // If the element is found in a subdirectory, return the result
                }
            }
        }
        return ""; // Return an empty string if the element is not found
    }

    public String getCurrentPath(Directory dir) {
        LinkedList<String> pathComponents = new LinkedList<>();
        FileSystemElement current = dir;

        // Traverse up the tree, prepending each directory name to the path
        while (current != null) {
            pathComponents.addFirst(current.getName());
            current = current.getParent();
        }

        // Special case for the root directory (which should have a name of "" for this
        // to work)
        if (pathComponents.size() == 1 && "".equals(pathComponents.peekFirst())) {
            return "/";
        }

        // Join the path components
        return String.join("/", pathComponents);
    }

    // Sort contents of a directory by date created
    public void sortContentsByDate(Directory dir) {
        Collections.sort(dir.getChildren(), new Comparator<FileSystemElement>() {
            @Override
            public int compare(FileSystemElement fse1, FileSystemElement fse2) {
                // Assuming FileSystemElement has a getDateCreated() method that returns a
                // Timestamp.
                // The Timestamp implements Comparable, so we can directly use compareTo here.
                return fse1.getDateCreated().compareTo(fse2.getDateCreated());
            }
        });
    }

    public Directory getCurrentDirectory() {
        return currentDirectory; // It should return the current state of the directory
    }

    // Additional helper methods can be added as needed
}
