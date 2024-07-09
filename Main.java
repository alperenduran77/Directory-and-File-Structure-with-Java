import java.util.Scanner;

public class Main {
    private static FileSystem fs = new FileSystem();
    private static Scanner scanner = new Scanner(System.in);
    @SuppressWarnings("unused")
    private static Directory currentDirectory;

    public static void main(String[] args) {
        currentDirectory = fs.getRoot();
        int option;
        do {
            System.out.println("===== File System Management Menu =====");
            System.out.println("0. Print Whole System Tree");
            System.out.println("1. Change directory");
            System.out.println("2. List directory contents");
            System.out.println("3. Create file/directory");
            System.out.println("4. Delete file/directory");
            System.out.println("5. Move file/directory");
            System.out.println("6. Search file/directory");
            System.out.println("7. Print directory tree");
            System.out.println("8. Sort contents by date created");
            System.out.println("9. Exit");
            System.out.print("Please select an option: ");

            option = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (option) {
                case 0:
                    fs.printWholeTree();
                    break;
                case 1:

                    handleChangeDirectory();
                    break;
                case 2:

                    handleListDirectoryContents();
                    break;
                case 3:

                    handleCreateFileOrDirectory();
                    break;
                case 4:

                    handleDeleteFileOrDirectory();
                    break;
                case 5:
                    handleMoveFileOrDirectory();
                    break;
                case 6:
                    handleSearchFileOrDirectory();
                    break;
                case 7:
                    fs.printTree();
                    break;
                case 8:

                    handleSortContentsByDate();
                    break;
                case 9:
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid option, please try again.");
            }
        } while (option != 9);
    }

    private static void handleChangeDirectory() {
        System.out.println("Current directory: " + fs.getCurrentPath(fs.getCurrentDirectory()));
        System.out.print("Enter the directory path to change to: ");
        String path = scanner.nextLine();
        Directory newDir = fs.changeDirectory(path);
        if (newDir != null) {
            fs.setCurrentDirectory(newDir); // Explicitly change the current directory here
            System.out.println("Directory changed to: " + fs.getCurrentPath(newDir));
        } else {
            System.out.println("Directory not found.");
        }
    }

    private static void handleListDirectoryContents() {
        System.out.println("Current directory: " + fs.getCurrentPath(fs.getCurrentDirectory()));
        System.out.println("Contents of " + fs.getCurrentPath(fs.getCurrentDirectory()) + ":");
        for (FileSystemElement fse : fs.getCurrentDirectory().getChildren()) {
            System.out
                    .println(fse.getName() + (fse instanceof Directory ? "/" : "") );
        }
    }

    private static void handleCreateFileOrDirectory() {
        System.out.println("Current directory: " + fs.getCurrentPath(fs.getCurrentDirectory()));
        System.out.print("Create file (f) or directory (d)? ");
        String type = scanner.nextLine();
        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        if ("f".equalsIgnoreCase(type)) {
            fs.createFile(name);
            System.out.println("File created: " + name);
        } else if ("d".equalsIgnoreCase(type)) {
            fs.createDirectory(name);
            System.out.println("Directory created: " + name);
        } else {
            System.out.println("Invalid type entered. Please enter 'f' for file or 'd' for directory.");
        }
    }

    private static void handleDeleteFileOrDirectory() {
        System.out.println("Current directory: " + fs.getCurrentPath(fs.getCurrentDirectory()));
        System.out.print("Enter the name of the file/directory to delete: ");
        String name = scanner.nextLine();
        try {
            fs.deleteFileOrDirectory(name);
            System.out.println(name + " deleted.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void handleMoveFileOrDirectory() {
        System.out.println("Current directory: " + fs.getCurrentPath(fs.getCurrentDirectory()));
        System.out.print("Enter the name of the file/directory to move: ");
        String name = scanner.nextLine();
        System.out.print("Enter the new directory path to move to: ");
        String newPath = scanner.nextLine();
        try {
            fs.moveFileOrDirectory(name, newPath);
            System.out.println(name + " moved to " + newPath);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void handleSearchFileOrDirectory() {
        System.out.print("Enter the search query: ");
        String query = scanner.nextLine();
        System.out.println("Searching from root...");
        String result = fs.searchFileOrDirectory(query);
        if (!result.isEmpty()) {
            System.out.println("Found: " + result);
        } else {
            System.out.println("File or directory not found.");
        }
    }

    private static void handleSortContentsByDate() {
        System.out.println("Current directory: " + fs.getCurrentPath(fs.getCurrentDirectory()));
        System.out.println("Sorting contents of the directory by date created...");
        fs.sortContentsByDate(fs.getCurrentDirectory());
       
        for (FileSystemElement fse : fs.getCurrentDirectory().getChildren()) {
            System.out.println(fse.getName() + (fse instanceof Directory ? "/" : "") + " - " + "(" + fse.getDateCreated() + ")");

        }
    }

}
