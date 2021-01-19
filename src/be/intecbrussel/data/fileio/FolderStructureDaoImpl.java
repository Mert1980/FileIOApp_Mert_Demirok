package be.intecbrussel.data.fileio;

import be.intecbrussel.data.FolderStructureDao;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.TreeSet;

public class FolderStructureDaoImpl implements FolderStructureDao {
    TreeSet<String> folderNameSet = new TreeSet<>();

    @Override
    public void sortFolder() {

        Path pathToWalk = Paths.get("resources/unsorted/");

        try {
            Files.createDirectory(Path.of("resources/sorted_folder"));

            // set directory names
            Files.walk(pathToWalk).forEach(path -> {
                setDirectoryName(path);
            });

            // create directories
            createDirectories();

            // copy files into directories
            Files.walk(pathToWalk).forEach(path -> {
              copyFiles(path);
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // add directory names inside folderNameSet
    private void setDirectoryName(Path path) {

        if(path.toFile().getName().equals(".gitignore")){
            folderNameSet.add("gitignore");
        }

        if(path.toFile().isHidden()){
            folderNameSet.add("hidden");
        } else {
            setNonHiddenDirectoryNames(path);
        }
    }

    // add non hidden directory names into folderNameSet
    private void setNonHiddenDirectoryNames(Path path) {
        folderNameSet.add("summary");
        setExtensionsAsFolderNames(path);
    }

    // add extensions of files into folderNameSet as folder names
    private void setExtensionsAsFolderNames(Path path) {
        String fullName = path.getFileName()
                .toString()
                .replaceAll(" ", "");
        int index = fullName.lastIndexOf(".");

        if (index != -1) {
            String extension = fullName.substring(index + 1);
            if(extension.equalsIgnoreCase("sqlite3")){
                folderNameSet.add("database");
            } else {
                folderNameSet.add(extension);
            }
        }
    }

    // create directories using the names in folderNameSet
    private void createDirectories(){
        folderNameSet.forEach(name -> {
            try {
                Files.createDirectory(Path.of("resources/sorted_folder/" + name));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    // copy files into directories
    private void copyFiles(Path path) {
        folderNameSet.forEach(name -> copyFileIntoDirectory(path, name));
    }

    // copy each file into related directory
    private void copyFileIntoDirectory(Path path, String name){
        if(path.getFileName().toString().contains(name)){
            Path p1 = Paths.get("resources/sorted_folder");
            Path p2 = p1.resolve(name);
            Path copyDestination = p2.resolve(path.getFileName());
            copy(path, copyDestination);
        } else if (path.getFileName().toString().contains("sqlite3")){
            copy(path, Path.of("resources/sorted_folder/database/mov_db.sqlite3"));
        }
    }

    // copy from source path to destination path
    private void copy(Path path, Path copyDestination) {
        try {
            Files.copy(path, copyDestination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // summarize sorted_folder
    @Override
    public void summarizeFolder() {
        String summary = createSummaryText();
        System.out.println(summary);
        try(BufferedWriter writer = new BufferedWriter(new FileWriter("resources/sorted_folder/summary/summary.txt", false))){
            writer.write(summary);
        } catch (IOException ex){
            System.out.println("Oops, something went wrong!");
            System.out.println(ex.getMessage());
        }
    }

    // create summary text to be copied into summary.txt
    private String createSummaryText() {
        StringBuilder headLine = new StringBuilder();

        defineHeadLine(headLine);

        // do not include "summary" folder in summary.txt file
        StringBuilder lines = new StringBuilder();
        folderNameSet.stream().filter(name -> !name.equalsIgnoreCase("summary"))
                            .forEach(name -> handleFileInfo(name, lines));

        return headLine.append(lines.toString()).toString();
    }

    // define head line of the summary
    private void defineHeadLine(StringBuilder headLine) {
        String nameHeader = "name";
        String readable = "readable";
        String writable = "writable";
        String space = "";

        headLine.append(String.format("%-50s | %-5s  %-10s    | %5s  %-10s     |\n\n", nameHeader, space, readable,
                space,
                writable));
    }

    // handle each file case by case and append to folder name
    private void handleFileInfo(String name, StringBuilder lines) {
        lines.append(name).append(":\n").append("-----------").append("\n");

        Path pathToWalk = Paths.get("resources/sorted_folder/");
        try {
            Files.walk(pathToWalk).forEach(path ->
                setFileInfo(path, name, lines));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // append file info on its own line
    private void setFileInfo(Path path, String name, StringBuilder lines) {
        String check = "X";
        String uncheck = "/";
        String space = "";

        if(path.getFileName().toString().contains("." + name)){
            appendProperties(path, lines, check, uncheck, space);
        } else if (path.getFileName().toString().contains(".sqlite3") && name.equalsIgnoreCase("database")){
            appendProperties(path, lines, check, uncheck, space);
        } else if (path.toFile().isHidden() && !path.getFileName().toString().contains(".gitignore")){
            appendProperties(path, lines, check, uncheck, space);
        }
    }

    // append file properties (readable/writable) into line
    private void appendProperties(Path path, StringBuilder lines, String check, String uncheck, String space) {
        if (Files.isReadable(path) && Files.isWritable(path)) {
            lines.append(String.format("%-50s |    %-5s  %-10s |     %5s  %-10s |\n", path.getFileName(), space,
                    check, space, check));
        } else {
            lines.append(String.format("%-50s |    %-5s  %-10s |     %5s  %-10s |\n", path.getFileName(), space,
                    check, space, uncheck));
        }
    }
}
