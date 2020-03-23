package dev.shroysha.widgets.mp3rename.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SongFile {

    private final File songFile;
    private SongInterface moreSpecific;

    public SongFile(File file) throws FileNotFoundException {
        super();
        songFile = file;
        init();
    }

    private void init() throws FileNotFoundException {
        String name = songFile.getPath();
        String withoutExtension = name.substring(0, name.lastIndexOf('.'));
        String extension = name.replaceFirst(withoutExtension, "");

        if (extension.equals(".mp3")) {
            moreSpecific = new MP3File(this);
        } else {
            throw new IllegalArgumentException("The file extension " + extension + " is not supported.");
        }

    }

    public File getSongFile() {
        return songFile;
    }

    public String getTitle() {
        return moreSpecific.getTitle();
    }

    public String getArtist() {
        return moreSpecific.getArtist();
    }

    public String getAlbum() {
        return moreSpecific.getAlbum();
    }

    public String getYear() {
        return moreSpecific.getYear();
    }

    public String getGenre() {
        return moreSpecific.getGenre();
    }

    public String getComments() {
        return moreSpecific.getComment();
    }

    public String getContents() throws FileNotFoundException {

        FileInputStream stream = new FileInputStream(songFile);
        try {
            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            /* Instead of using default, pass in a decoder. */
            return Charset.defaultCharset().decode(bb).toString();
        } catch (IOException ex) {
            Logger.getLogger(SongFile.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                stream.close();
            } catch (IOException ex) {
                Logger.getLogger(SongFile.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        throw new IllegalArgumentException("There was an unexpected error while reading the file " + songFile.getName() + ".");
    }
}

