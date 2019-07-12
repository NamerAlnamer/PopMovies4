package com.example.popmovies4.model;


import java.io.Serializable;
import java.util.List;


public class MoviesDB {


    private int page;
    private String currentSortBy;



    private List<Movie> results;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getCurrentSortBy() {
        return currentSortBy;
    }

    public void setCurrentSortBy(String currentSortBy) {
        this.currentSortBy = currentSortBy;
    }

    public List<Movie> getResults() {
        return results;
    }

    public void setResults(List<Movie> results) {
        this.results = results;
    }

    public static class Movie implements Serializable {

        private String id;
        private long _id;
        private String poster_path;
        private String overview;
        private String release_date;
        private String original_title;
        private double vote_average;
        private double popularity;

        public String getPoster_path() {
            return poster_path;
        }

        public long get_id() {
            return _id;
        }

        public void set_id(long _id) {
            this._id = _id;
        }

        public void setPoster_path(String poster_path) {
            this.poster_path = poster_path;
        }

        public String getOverview() {
            return overview;
        }

        public void setOverview(String overview) {
            this.overview = overview;
        }

        public String getRelease_date() {
            return release_date.substring(0, 4);
        }

        public void setRelease_date(String release_date) {
            this.release_date = release_date;
        }

        public String getOriginal_title() {
            return original_title;
        }

        public void setOriginal_title(String original_title) {
            this.original_title = original_title;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public double getPopularity() {
            return popularity;
        }

        public void setPopularity(double popularity) {
            this.popularity = popularity;
        }

        public String getVote_average() {
            return vote_average + "/10";
        }

        public void setVote_average(double vote_average) {
            this.vote_average = vote_average;
        }
    }
}
