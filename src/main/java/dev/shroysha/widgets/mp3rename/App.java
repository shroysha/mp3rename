package dev.shroysha.widgets.mp3rename;

import org.farng.mp3.AbstractMP3Tag;
import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;


public class App {

    public static final String SONG = "%Song", ARTIST = "%Artist", ALBUM = "%Album", TRACKNUM = "%#";
    private static String mp3RenameFormat = ARTIST + " - " + SONG;


    public static void main(String[] args) {
        try {
            File[] file = openFiles(null);
            assert file != null;
            System.out.println(file.length);
            Queue<MP3File> files = new LinkedList<>();

            for (File f : file) {
                files.add(new MP3File(f));
            }

            Exception[] exs = renameMP3s(files);

            for (Exception ex : exs) {
                if (ex != null)
                    ex.printStackTrace(System.err);
            }

        } catch (IOException | TagException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static File[] openFiles(Component parent) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Choose MP3 Files");
        chooser.setMultiSelectionEnabled(true);

        int n = chooser.showOpenDialog(parent);

        if (n == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFiles();
        } else {
            return null;
        }
    }

    public static String getMP3RenameFormat() {
        return mp3RenameFormat;
    }

    public static void setMP3RenameFormat(String format) {
        mp3RenameFormat = format;
    }

    public static Exception[] renameMP3s(Queue<MP3File> files) {
        Exception[] worked = new Exception[files.size()];

        for (int i = 0; !files.isEmpty(); i++) {
            MP3File next = files.poll();
            try {
                renameMP3(next);
                worked[i] = null;
            } catch (Exception ex) {
                worked[i] = ex;
            }
        }

        return worked;
    }

    public static void renameMP3(MP3File mp3File) throws Exception {
        File file = mp3File.getMp3file();
        String oldName = mp3File.getMp3file().getName();
        String path = mp3File.getMp3file().getPath().replace(oldName, "");
        String newName = getNewName(mp3File);
        boolean worked = file.renameTo(new File(path + newName));

        if (!worked)
            throw new IOException("Rename failed");
    }

    private static String getNewName(MP3File file) {
        AbstractMP3Tag tag = getTag(file);

        String start = file.getMp3file().getName();

        assert tag != null;
        String repSong = mp3RenameFormat.replaceAll(SONG, tag.getSongTitle());
        System.out.println("Song title " + tag.getSongTitle());
        String repArtist = repSong.replaceAll(ARTIST, tag.getLeadArtist());
        System.out.println("Artist " + tag.getLeadArtist());
        String repAlbum = repArtist.replaceAll(ALBUM, tag.getAlbumTitle());
        System.out.println("Album " + tag.getAlbumTitle());
        String repTrack = repAlbum.replaceAll(TRACKNUM, tag.getTrackNumberOnAlbum());
        System.out.println("Track num " + tag.getTrackNumberOnAlbum());

        System.out.println("Renamed: " + start);
        System.out.println("To: " + repTrack);

        return repTrack + ".mp3";
    }

    public static AbstractMP3Tag getTag(MP3File file) {
        if (file.hasID3v1Tag()) {
            return file.getID3v1Tag();
        } else if (file.hasID3v2Tag()) {
            return file.getID3v2Tag();
        } else if (file.hasFilenameTag()) {
            return file.getFilenameTag();
        } else if (file.hasLyrics3Tag()) {
            return file.getLyrics3Tag();
        } else
            return null;
    }
}
