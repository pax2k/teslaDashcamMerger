package no.koteng.dashcam.util;

import no.koteng.dashcam.main.Startup;
import no.koteng.dashcam.model.CombinedVideo;
import no.koteng.dashcam.model.Position;
import no.koteng.dashcam.model.Video;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class Util {

    public static List<CombinedVideo> getCombinedVideosForFolder(File[] allFilesInFolder) {
        List<CombinedVideo> combinedVideoList = new ArrayList<>();
        for (File file : allFilesInFolder) {
            String currentFile = file.getAbsolutePath();

            if (currentFile.contains("front.mp4")) {
                int lastDash = currentFile.lastIndexOf("-");
                String fileNameBase = currentFile.substring(0, lastDash);

                Video leftVideo = new Video(new File(fileNameBase + "-left_repeater.mp4"), Position.LEFT);
                Video middleVideo = new Video(new File(fileNameBase + "-front.mp4"), Position.MIDDLE);
                Video rightVideo = new Video(new File(fileNameBase + "-right_repeater.mp4"), Position.RIGHT);

                validateFile(leftVideo.getAcualFile());
                validateFile(middleVideo.getAcualFile());
                validateFile(rightVideo.getAcualFile());

                String justFileName = file.getName();
                int lastIndexOf = justFileName.lastIndexOf("-");
                String fileNameBaseWithoutPath = justFileName.substring(0, lastIndexOf) + "_COMBINED_VIDEO";
                combinedVideoList.add(new CombinedVideo(fileNameBaseWithoutPath, leftVideo, middleVideo, rightVideo));
            }
        }

        return combinedVideoList;
    }

    public static void combineMovies(CombinedVideo combinedVideo, String outputFolder, TextArea debugInfo) throws IOException {
        final InputStream ffmpegFromJar;
        String suffix = "";
        if (isWindows()) {
            ffmpegFromJar = Startup.class.getClassLoader().getResourceAsStream("ffmpeg.exe");
            suffix = ".exe";
        } else {
            ffmpegFromJar = Startup.class.getClassLoader().getResourceAsStream("ffmpeg");
        }

        File ffmpegTemp = File.createTempFile("ffmpeg", suffix);
        Files.copy(ffmpegFromJar, Paths.get(ffmpegTemp.getAbsolutePath()), StandardCopyOption.REPLACE_EXISTING);

        if (!isWindows()) {
            makeFileExecutalble(ffmpegTemp);
        }


        String ffmpegPath = ffmpegTemp.getAbsolutePath();
        String left = combinedVideo.getLeft().getAcualFile().getAbsolutePath();
        String middle = combinedVideo.getMiddle().getAcualFile().getAbsolutePath();
        String right = combinedVideo.getRight().getAcualFile().getAbsolutePath();
        String finalName = outputFolder + "/" + combinedVideo.getBaseName() + ".mp4";

        if (new File(finalName).exists()) {
            debugInfo.setText("File with filename: " + combinedVideo.getBaseName() + " already exists in output folder! Aborting...");
            return;
        }

        String arguments = "-i " + left + " -i " + middle + " -i " + right + " -filter_complex \"[1:v][0:v]scale2ref=oh*mdar:ih[1v][0v];[2:v][0v]scale2ref=oh*mdar:ih[2v][0v];[0v][1v][2v]hstack=3,scale='2*trunc(iw/2)':'2*trunc(ih/2)'\" " + finalName;
        String ffmpegWithArgs = ffmpegPath + " " + arguments;

        System.out.println("Ready to convert: " + ffmpegWithArgs);
        debugInfo.append("Ready to convert: " + ffmpegWithArgs + "\n");

        List<String> commands = new ArrayList<>();
        if (isWindows()) {
            commands.add("CMD");
            commands.add("/C");
        } else {
            commands.add("/bin/sh");
            commands.add("-c");
        }

        commands.add(ffmpegWithArgs);

        ProcessBuilder builder = new ProcessBuilder(commands);
        builder.redirectErrorStream(true);
        Process p = builder.start();

        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
        int counter = 0;
        while (true) {
            String line = r.readLine();
            if (line == null) {
                break;
            }

            counter++;
            if (counter == 3) {
                debugInfo.append(line + "\n");
                counter = 0;
            }
        }
    }

    private static void makeFileExecutalble(File ffmpegTemp) throws IOException {
        List<String> commands = new ArrayList<>();
        commands.add("/bin/sh");
        commands.add("-c");
        commands.add("chmod +x " + ffmpegTemp.getAbsolutePath());

        ProcessBuilder builder = new ProcessBuilder(commands);
        builder.redirectErrorStream(true);
        Process p = builder.start();

        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
        while (true) {
            String line = r.readLine();
            if (line == null) {
                break;
            }

            System.out.println(line);
        }

    }

    private static void validateFile(File fileToValidate) {
        if (!fileToValidate.exists()) {
            System.out.println("Can not find file: " + fileToValidate);
            throw new IllegalStateException("Can not find file: " + fileToValidate);
        }
    }

    private static boolean isWindows() {
        String osName = System.getProperty("os.name").toLowerCase();
        return !osName.isEmpty() && osName.startsWith("win");
    }
}
