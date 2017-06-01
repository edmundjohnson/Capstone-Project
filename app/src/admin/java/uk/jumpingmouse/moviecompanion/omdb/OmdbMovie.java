package uk.jumpingmouse.moviecompanion.omdb;

import android.support.annotation.Nullable;

/**
 * A class representing a movie as returned by the OMDb API.
 * @author Edmund Johnson
 */
public class OmdbMovie {
    // Many attribute names must have a leading uppercase letter to match the JSON
    // returned by the OMDb API.

    // The title, e.g. "The Handmaiden"
    private String Title;
    // The year of the movie's release, e.g. "2017"
    private String Year;
    // The release date, e.g. "21 Apr 2017"
    private String Released;
    // The length in minutes, e.g. "144 min"
    private String Runtime;
    // A comma-separated list of genres, e.g. "Drama, Mystery, Romance"
    private String Genre;
    // The poster URL, formatted as "[path]/[name].jpg
    private String Poster;
    // The IMDb id, e.g. "tt4016934"
    private String imdbID;

    private OmdbMovie() {
    }

    //---------------------------------------------------------------
    // Getters and setters

    @Nullable
    public String getTitle() {
        return Title;
    }
    public void setTitle(@Nullable String Title) {
        this.Title = Title;
    }

    @Nullable
    public String getYear() {
        return Year;
    }
    public void setYear(@Nullable String Year) {
        this.Year = Year;
    }

    @Nullable
    public String getReleased() {
        return Released;
    }
    public void setReleased(@Nullable String Released) {
        this.Released = Released;
    }

    @Nullable
    public String getRuntime() {
        return Runtime;
    }
    public void setRuntime(@Nullable String Runtime) {
        this.Runtime = Runtime;
    }

    @Nullable
    public String getGenre() {
        return Genre;
    }
    public void setGenre(@Nullable String Genre) {
        this.Genre = Genre;
    }

    @Nullable
    public String getPoster() {
        return Poster;
    }
    public void setPoster(@Nullable String Poster) {
        this.Poster = Poster;
    }

    @Nullable
    public String getImdbID() {
        return imdbID;
    }
    public void setImdbID(@Nullable String imdbID) {
        this.imdbID = imdbID;
    }

    //---------------------------------------------------------------
    // Override object methods

    @Override
    public String toString() {
        return "OmdbMovie{"
                + "Title=" + Title
                + ", Year=" + Year
                + ", Released=" + Released
                + ", Runtime=" + Runtime
                + ", Genre=" + Genre
                + ", Poster=" + Poster
                + ", imdbID=" + imdbID
                + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof OmdbMovie) {
            OmdbMovie that = (OmdbMovie) o;
            return  ((this.Title == null) ? (that.Title == null) : this.Title.equals(that.Title))
                    && ((this.Year == null) ? (that.Year == null) : this.Year.equals(that.Year))
                    && ((this.Released == null) ? (that.Released == null)
                            : this.Released.equals(that.Released))
                    && ((this.Runtime == null) ? (that.Runtime == null) : this.Runtime.equals(that.Runtime))
                    && ((this.Genre == null) ? (that.Genre == null) : this.Genre.equals(that.Genre))
                    && ((this.Poster == null) ? (that.Poster == null) : this.Poster.equals(that.Poster))
                    && ((this.imdbID == null) ? (that.imdbID == null) : this.imdbID.equals(that.imdbID));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.Title.hashCode();
        h *= 1000003;
        h ^= (Year == null) ? 0 : this.Year.hashCode();
        h *= 1000003;
        h ^= (Released == null) ? 0 : this.Released.hashCode();
        h *= 1000003;
        h ^= (Runtime == null) ? 0 : this.Runtime.hashCode();
        h *= 1000003;
        h ^= (Genre == null) ? 0 : this.Genre.hashCode();
        h *= 1000003;
        h ^= (Poster == null) ? 0 : this.Poster.hashCode();
        h *= 1000003;
        h ^= this.imdbID.hashCode();
        return h;
    }

}
