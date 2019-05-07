package no.koteng.dashcam.gui;

import no.koteng.dashcam.model.CombinedVideo;
import no.koteng.dashcam.util.Util;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class MainFrame extends JFrame {
    private static final String NOT_SELECTED = "-";
    private final JButton createVideoButton = new JButton("start");

    private JButton selectVideoInputFolderButton = new JButton("select");
    private JLabel selectedInputLabel = getLabel("-", 450);

    private JButton selectVideoOutputFolder = new JButton("select");
    private JLabel selectedVideoOutPutFolderLabel = getLabel("-", 450);
    private final TextArea debugInfo = new TextArea(7, 80);

    public MainFrame() throws HeadlessException {
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle("Tesla dash cam video combiner");
        this.setLocation(300, 300);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        selectVideoFolderButtonListener();
        selectVideoOutputFolderListener();
        createVideoButtonListener();

        addFolderSettings(mainPanel);
        addStartVideoPanel(mainPanel);

        this.add(mainPanel);
        this.pack();
    }

    private void createVideoButtonListener() {
        createVideoButton.addActionListener(new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (NOT_SELECTED.equalsIgnoreCase(selectedInputLabel.getText())) {
                    showAlertDialog("Please select input folder", "Missing camera folder");
                } else if (NOT_SELECTED.equalsIgnoreCase(selectedVideoOutPutFolderLabel.getText())) {
                    showAlertDialog("Please select output folder", "Missing output folder");
                } else {
                    debugInfo.setText("");
                    debugInfo.append("Starting to create movie!");
                    debugInfo.append("\n");
                    createVideoButton.setEnabled(false);

                    SwingWorker<Void, String> swingWorker = new SwingWorker<Void, String>() {
                        @Override
                        protected Void doInBackground() {
                            createMovie(debugInfo);
                            return null;
                        }

                        @Override
                        protected void done() {
                            super.done();
                            debugInfo.append("\n");
                            debugInfo.append("Movie created in selected output folder  :) :)");
                            createVideoButton.setEnabled(true);
                        }
                    };

                    swingWorker.execute();
                }
            }
        });
    }

    private void showAlertDialog(String message, String title) {
        JOptionPane
                .showMessageDialog(
                        null,
                        message,
                        title,
                        JOptionPane.WARNING_MESSAGE);
    }

    private void createMovie(TextArea debugInfo) {
        File folder = new File(selectedInputLabel.getText());
        File[] allFilesInFolder = folder.listFiles();
        if (allFilesInFolder == null || allFilesInFolder.length == 0) {
            showAlertDialog("Folder does not contain any files", "Empty folder");
            return;
        }

        List<CombinedVideo> combinedVideosForFolder = Util.getCombinedVideosForFolder(allFilesInFolder);
        for (CombinedVideo video : combinedVideosForFolder) {
            try {
                Util.combineMovies(video, selectedVideoOutPutFolderLabel.getText(), debugInfo);
            } catch (URISyntaxException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void selectVideoOutputFolderListener() {
        selectVideoOutputFolder.addActionListener(new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                FolderChooser folderChooser = new FolderChooser("Select output folder", "Ok");
                File selectedFolder = folderChooser.getSelectedFile();

                if (selectedFolder == null) {
                    selectedVideoOutPutFolderLabel.setText(NOT_SELECTED);
                } else {
                    selectedVideoOutPutFolderLabel.setText(selectedFolder.getAbsolutePath());
                }
            }
        });
    }

    private void selectVideoFolderButtonListener() {
        selectVideoInputFolderButton.addActionListener(new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                FolderChooser folderChooser = new FolderChooser("Choose video folder", "Ok");
                File selectedFolder = folderChooser.getSelectedFile();

                if (selectedFolder == null) {
                    selectedInputLabel.setText(NOT_SELECTED);
                } else {
                    selectedInputLabel.setText(selectedFolder.getAbsolutePath());
                }
            }
        });
    }

    private void addFolderSettings(JPanel mainPanel) {
        JPanel chooseFoldersWidgetsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        chooseFoldersWidgetsPanel.add(getLabel("Select video folder:", 150));
        chooseFoldersWidgetsPanel.add(selectVideoInputFolderButton);
        chooseFoldersWidgetsPanel.add(selectedInputLabel);

        JPanel chooseFoldersWidgetsPanel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        chooseFoldersWidgetsPanel2.add(getLabel("Select output folder:", 150));
        chooseFoldersWidgetsPanel2.add(selectVideoOutputFolder);
        chooseFoldersWidgetsPanel2.add(selectedVideoOutPutFolderLabel);

        JPanel chooseFoldersLayoutPanel = new JPanel();
        chooseFoldersLayoutPanel.setLayout(new BoxLayout(chooseFoldersLayoutPanel, BoxLayout.Y_AXIS));
        chooseFoldersLayoutPanel.setBorder(createPanelBorder("Input and output settings"));
        chooseFoldersLayoutPanel.add(chooseFoldersWidgetsPanel);
        chooseFoldersLayoutPanel.add(chooseFoldersWidgetsPanel2);

        mainPanel.add(chooseFoldersLayoutPanel);
    }

    private void addStartVideoPanel(JPanel mainPanel) {
        JPanel createOneFileSettingsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        createOneFileSettingsPanel.add(getLabel("Info:", 150));
        debugInfo.setEditable(false);
        createOneFileSettingsPanel.add(debugInfo);


        JPanel startConvertingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        startConvertingPanel.add(getLabel("Start combining:", 150));
        startConvertingPanel.add(createVideoButton);

        JPanel startVideoWidgetsPanel = new JPanel();
        startVideoWidgetsPanel.setLayout(new BoxLayout(startVideoWidgetsPanel, BoxLayout.Y_AXIS));
        startVideoWidgetsPanel.setBorder(createPanelBorder("Video"));
        startVideoWidgetsPanel.add(startConvertingPanel);
        startVideoWidgetsPanel.add(createOneFileSettingsPanel);

        mainPanel.add(startVideoWidgetsPanel);
    }

    private TitledBorder createPanelBorder(String video) {
        TitledBorder startVideoBorder = new TitledBorder(video);
        startVideoBorder.setTitleJustification(TitledBorder.LEFT);
        startVideoBorder.setTitlePosition(TitledBorder.TOP);
        return startVideoBorder;
    }

    private JLabel getLabel(String s, int i) {
        JLabel label = new JLabel(s);
        label.setPreferredSize(new Dimension(i, 20));
        return label;
    }
}
