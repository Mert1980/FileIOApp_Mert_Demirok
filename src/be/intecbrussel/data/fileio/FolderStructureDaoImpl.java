package be.intecbrussel.data.fileio;

import be.intecbrussel.data.FolderStructureDao;

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
                if(path.toFile().isHidden()){
                    System.out.println(path);
                }
                //System.out.println(path.getFileName());
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

    private void setNonHiddenDirectoryNames(Path path) {
        folderNameSet.add("summary");
        setExtensionsAsFolderNames(path);
    }

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

    private void createDirectories(){
        folderNameSet.forEach(name -> {
            try {
                Files.createDirectory(Path.of("resources/sorted_folder/" + name));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void copyFiles(Path path) {
        folderNameSet.forEach(name -> copyFileIntoDirectory(path, name));
    }

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

    private void copy(Path path, Path copyDestination) {
        try {
            Files.copy(path, copyDestination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void summarizeFolder() {

    }
}
