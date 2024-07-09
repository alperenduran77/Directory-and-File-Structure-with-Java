import java.sql.Timestamp;

public abstract class FileSystemElement {
    protected String name;
    protected Timestamp dateCreated;
    protected Directory parent; // Ensure this is of type Directory

    public FileSystemElement(String name, Directory parent, Timestamp dateCreated) {
        this.name = name;
        this.parent = parent;
        this.dateCreated = new Timestamp(System.currentTimeMillis());
    }

    public String getName() {
        return name;
    }

    public Directory getParent() {
        if (this.parent == null) { 
            return null; // Return null if the parent is null
        }
        return this.parent; // Return the parent directory

    }

    public void setParent(Directory parent) {
        this.parent = parent;
    }

    public abstract void display(String prefix, boolean isCurrent);

    public Timestamp getDateCreated() {
        return dateCreated; // Return the date created
    }

}
