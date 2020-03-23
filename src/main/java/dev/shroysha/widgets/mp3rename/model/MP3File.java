package dev.shroysha.widgets.mp3rename.model;

import java.io.FileNotFoundException;

public class MP3File implements SongInterface {

    private final SongFile songFile;
    private ID3Tag tag;

    public MP3File(SongFile file) throws FileNotFoundException {
        songFile = file;
        init();
    }

    private void init() throws FileNotFoundException {
        String contents = songFile.getContents();

        final int id3v1TagLocation = contents.length() - 128;
        final int tagLocation = contents.lastIndexOf("TAG");

        if (id3v1TagLocation == tagLocation) {
            String metadata = ID3V1Tag.getMetadata(contents, tagLocation);
            tag = new ID3V1Tag(metadata);
        } else {
            throw new IllegalArgumentException("Cannot find tag data for file " + songFile.getSongFile().getName() + ".");
        }


    }

    public String getTitle() {
        return tag.getTitle();
    }

    public String getArtist() {
        return tag.getArtist();
    }

    public String getAlbum() {
        return tag.getAlbum();
    }

    public String getYear() {
        return tag.getYear();
    }

    public String getGenre() {
        return tag.getGenre();
    }

    public String getComment() {
        return tag.getComment();
    }

}
