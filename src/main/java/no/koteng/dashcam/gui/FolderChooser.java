package no.koteng.dashcam.gui;

import javax.swing.*;

public class FolderChooser extends JFileChooser {

    public FolderChooser(String dialogText, String okText){
        this.setDialogTitle(dialogText);
        this.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        this.setAcceptAllFileFilterUsed(false);
        this.showDialog(null, okText);
        this.requestFocus();
    }
}
