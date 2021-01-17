package be.intecbrussel.data.fileio;

import be.intecbrussel.data.FolderStructureDao;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.TreeSet;

public class FolderStructureDaoImpl implements FolderStructureDao {
    TreeSet<String> folderNameSet = new TreeSet<>();

    @Override
    public void sortFolder() {

        Path pathToWalk = Paths.get("resources/unsorted/");

        try {
            Files.walk(pathToWalk).forEach(path -> setDirectoryName(path));
            Files.createDirectory(Path.of("resources/sorted_folder"));
            createDirectories();
            copyFilesIntoDirectories();
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

        String fullName = path.getFileName()
                .toString()
                .replaceAll(" ", "");
        int index = fullName.lastIndexOf(".");

        if (index != -1) {
            fullName = fullName.substring(index + 1);
            if(fullName.equalsIgnoreCase("sqlite3")){
                folderNameSet.add("database");
            } else {
                folderNameSet.add(fullName);
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

    private void copyFilesIntoDirectories(){

    };

    @Override
    public void summarizeFolder() {

    }
}
