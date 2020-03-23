package dev.shroysha.widgets.mp3rename.model;


public class ID3V1Tag implements ID3Tag {

    private final String metadata;
    private String title, artist, album, year, genre, comment;

    public ID3V1Tag(String meta) {
        metadata = meta;
        init();
    }

    public static String getMetadata(String contents, int tagLocation) {
        return contents.substring(tagLocation);
    }

    private void init() {
        final int TITLE_LENGTH = 30;
        final int ARTIST_LENGTH = 30;
        final int ALBUM_LENGTH = 30;
        final int YEAR_LENGTH = 4;
        final int COMMENT_LENGTH = 30;
        final int GENRE_LENGTH = 1;

        String toUse = metadata.replaceFirst("TAG", "");

        title = toUse.substring(0, TITLE_LENGTH);
        toUse = toUse.replaceFirst(title, "");

        artist = toUse.substring(0, ARTIST_LENGTH);
        toUse = toUse.replaceFirst(artist, "");

        album = toUse.substring(0, ALBUM_LENGTH);
        toUse = toUse.replaceFirst(album, "");

        year = toUse.substring(0, YEAR_LENGTH);
        toUse = toUse.replaceFirst(year, "");

        comment = toUse.substring(0, COMMENT_LENGTH);
        toUse = toUse.replaceFirst(comment, "");

        genre = toUse.substring(0, GENRE_LENGTH);
    }

    public String getAlbum() {
        return album;
    }

    public String getArtist() {
        return artist;
    }

    public String getGenre() {
        return genre;
    }

    public String getTitle() {
        return title;
    }

    public String getYear() {
        return year;
    }

    public String getComment() {
        return comment;
    }
}
