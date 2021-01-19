package be.intecbrussel.app;

import be.intecbrussel.data.FolderStructureDao;
import be.intecbrussel.data.fileio.FolderStructureDaoImpl;

public class App {
    public static void main(String[] args) {
        FolderStructureDao folderStructureDao = new FolderStructureDaoImpl();

        folderStructureDao.sortFolder();
        folderStructureDao.summarizeFolder();

    }
}
