
import java.sql.Timestamp;

public class File extends FileSystemElement {
    
    public File(String name, Directory parent, Timestamp dateCreated) {
        super(name, parent, dateCreated);
    }

    @Override
    public void display(String prefix, boolean isCurrent) {
        System.out.println(prefix + this.name);
    }

}
