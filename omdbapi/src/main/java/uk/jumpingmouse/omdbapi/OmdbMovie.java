package uk.jumpingmouse.omdbapi;

import android.support.annotation.Nullable;

/**
 * A class representing a movie as returned by the OMDb API.
 * @author Edmund Johnson
 */
public final class OmdbMovie {
    public static final int RUNTIME_UNKNOWN = -1;
    public static final int RELEASED_UNKNOWN = -1;

    // Many attribute names must have a leading uppercase letter to match the JSON
    // returned by the OMDb API.

    // The title, e.g. "The Handmaiden"
    private String Title;
    // The year of the movie's release, e.g. "2016"
    private String Year;
    // The US rating, e.g. "R"
    private String Rated;
    // The release date, e.g. "01 Jun 2016"
    private String Released;
    // The length in minutes, e.g. "144 min"
    private String Runtime;
    // A comma-separated list of genres, e.g. "Drama, Mystery, Romance"
    private String Genre;
    // The director, e.g. "Chan-wook Park"
    private String Director;
    // The writer, e.g. "Sarah Waters"
    private String Writer;
    // A comma-separated list of actors, e.g. "Min-hee Kim, Tae-ri Kim, Jung-woo Ha, Jin-woong Jo"
    private String Actors;
    // The short-form plot, e.g. "A woman is hired as a handmaiden to a Japanese heiress, but
    // secretly she is involved in a plot to defraud her."
    private String Plot;
    // A comma-separated list of languages, e.g. "Korean, Japanese"
    private String Language;
    // The country, e.g. "South Korea"
    private String Country;
    // The poster URL, formatted as "[protocol]:[full path]/[name].jpg
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
    public String getRated() {
        return Rated;
    }
    public void setRated(@Nullable String Rated) {
        this.Rated = Rated;
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

    public String getDirector() {
        return Director;
    }
    public void setDirector(@Nullable String director) {
        Director = director;
    }

    @Nullable
    public String getWriter() {
        return Writer;
    }
    public void setWriter(@Nullable String writer) {
        Writer = writer;
    }

    @Nullable
    public String getActors() {
        return Actors;
    }
    public void setActors(@Nullable String actors) {
        Actors = actors;
    }

    @Nullable
    public String getPlot() {
        return Plot;
    }
    public void setPlot(@Nullable String plot) {
        Plot = plot;
    }

    @Nullable
    public String getLanguage() {
        return Language;
    }
    public void setLanguage(@Nullable String language) {
        Language = language;
    }

    @Nullable
    public String getCountry() {
        return Country;
    }
    public void setCountry(@Nullable String country) {
        Country = country;
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
                + ", Rated=" + Rated
                + ", Released=" + Released
                + ", Runtime=" + Runtime
                + ", Genre=" + Genre
                + ", Director=" + Director
                + ", Writer=" + Writer
                + ", Actors=" + Actors
                + ", Plot=" + Plot
                + ", Language=" + Language
                + ", Country=" + Country
                + ", Poster=" + Poster
                + ", imdbID=" + imdbID
                + "}";
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof OmdbMovie) {
            OmdbMovie that = (OmdbMovie) o;
            return  ((this.Title == null) ? (that.Title == null) : this.Title.equals(that.Title))
                    && ((this.Year == null) ? (that.Year == null) : this.Year.equals(that.Year))
                    && ((this.Rated == null) ? (that.Rated == null) : this.Rated.equals(that.Rated))
                    && ((this.Released == null) ? (that.Released == null)
                            : this.Released.equals(that.Released))
                    && ((this.Runtime == null) ? (that.Runtime == null) : this.Runtime.equals(that.Runtime))
                    && ((this.Genre == null) ? (that.Genre == null) : this.Genre.equals(that.Genre))
                    && ((this.Director == null) ? (that.Director == null) : this.Director.equals(that.Director))
                    && ((this.Writer == null) ? (that.Writer == null) : this.Writer.equals(that.Writer))
                    && ((this.Actors == null) ? (that.Actors == null) : this.Actors.equals(that.Actors))
                    && ((this.Plot == null) ? (that.Plot == null) : this.Plot.equals(that.Plot))
                    && ((this.Language == null) ? (that.Language == null) : this.Language.equals(that.Language))
                    && ((this.Country == null) ? (that.Country == null) : this.Country.equals(that.Country))
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
        h ^= (Rated == null) ? 0 : this.Rated.hashCode();
        h *= 1000003;
        h ^= (Released == null) ? 0 : this.Released.hashCode();
        h *= 1000003;
        h ^= (Runtime == null) ? 0 : this.Runtime.hashCode();
        h *= 1000003;
        h ^= (Genre == null) ? 0 : this.Genre.hashCode();
        h *= 1000003;
        h ^= (Director == null) ? 0 : this.Director.hashCode();
        h *= 1000003;
        h ^= (Writer == null) ? 0 : this.Writer.hashCode();
        h *= 1000003;
        h ^= (Actors == null) ? 0 : this.Actors.hashCode();
        h *= 1000003;
        h ^= (Plot == null) ? 0 : this.Plot.hashCode();
        h *= 1000003;
        h ^= (Language == null) ? 0 : this.Language.hashCode();
        h *= 1000003;
        h ^= (Country == null) ? 0 : this.Country.hashCode();
        h *= 1000003;
        h ^= (Poster == null) ? 0 : this.Poster.hashCode();
        h *= 1000003;
        h ^= this.imdbID.hashCode();
        return h;
    }

}
