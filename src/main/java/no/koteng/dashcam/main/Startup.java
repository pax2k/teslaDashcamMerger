package no.koteng.dashcam.main;

import no.koteng.dashcam.gui.MainFrame;

import java.io.IOException;
import java.net.URISyntaxException;

public class Startup {

    public static void main(String[] args) throws IOException, URISyntaxException {
        MainFrame frame = new MainFrame();
        frame.setVisible(true);

     /*   FolderChooser folderChooser = new FolderChooser();

        File selectedFolder = folderChooser.getSelectedFile();
        if (selectedFolder == null) {
            System.out.println("No folder selected");
            return;
        }

        File[] allFilesInFolder = selectedFolder.listFiles();
        if (allFilesInFolder == null || allFilesInFolder.length == 0) {
            System.out.println("No files in selected folder");
        }

        List<CombinedVideo> combinedVideosForFolder = Util.getCombinedVideosForFolder(allFilesInFolder);
        System.out.println("Found " + combinedVideosForFolder.size() + " videos in folder.");

        for (CombinedVideo video : combinedVideosForFolder) {
            System.out.println("Base name: " + video.getBaseName());
            Util.combineMovies(video);
        }

        System.out.println("Video created! Enjoy :) ");
*/
    }
}
