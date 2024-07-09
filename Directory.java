
import java.util.LinkedList;
import java.sql.Timestamp;

public class Directory extends FileSystemElement {

    private LinkedList<FileSystemElement> children = new LinkedList<>();

    public Directory(String name, Directory parent, Timestamp dateCreated) {
        super(name, parent, dateCreated);
        children = new LinkedList<>(); // Initialize the list of children
    }

    public void addElement(FileSystemElement element) {
        children.add(element);      // Add the element to the list of children
        element.setParent(this); // Set the parent of the element to this directory
    }

    public void removeElement(FileSystemElement element) {
        children.remove(element);
        // Note: Setting the parent to null should be done in moveFileOrDirectory method
    }

    public LinkedList<FileSystemElement> getChildren() {
        return children; // Return the list of children
    }

    @Override
    public void display(String prefix, boolean isCurrent) {

        /*
         * if (isCurrent) {
         * System.out.println(prefix + this.name + "/ (Current Directory)");
         * } else {
         * System.out.println(prefix + this.name + "/");
         * }
         * 
         * for (FileSystemElement element : children) {
         * element.display(prefix + "    ", false);
         * }
         */
    }

    public Directory getSubdirectory(String name) {
        for (FileSystemElement element : this.children) {
            if (element instanceof Directory && element.getName().equals(name)) {
                return (Directory) element; // Cast the element to Directory
            }
        }
        return null; // Subdirectory not found
    }

    // Method to find an element by name within the current directory

    public FileSystemElement findElement(String name) {
        for (FileSystemElement element : this.children) {
            if (element.getName().equals(name)) {
                return element; // Element found
            }
        }
        return null; // Element not found
    }

    public Directory getParent() {
        return (Directory) super.getParent(); // Cast the parent to Directory
    }

}
