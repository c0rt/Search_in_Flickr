package com.example.searchinflickr;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;
import com.flickr4java.flickr.photos.SearchParameters;

class FlickModel {
    private Flickr flickrClient;

    FlickModel() {
        REST rest = new REST();
        String apiKey = "6fbaad746d09f98da99e21071bd934f1";
        String sharedSecret = "6149a19c78ef61ee";
        flickrClient = new Flickr(apiKey, sharedSecret, rest);
    }

    class MySearchRunnable implements Runnable {
        private String query;
        private PhotoList<Photo> photos;

        MySearchRunnable(String _query) {
            this.query = _query;
        }

        @Override
        public void run() {
            try {
                SearchParameters searchParameters = new SearchParameters();
                searchParameters.setMedia("photos");
                searchParameters.setText(query);

                photos = flickrClient.getPhotosInterface().search(searchParameters, 5, 1);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        PhotoList<Photo> getPhotos() {
            return this.photos;
        }
    }

    PhotoList<Photo> searchPhotos(String query) {
        MySearchRunnable threadCore = new MySearchRunnable(query);
        Thread thread = new Thread(threadCore);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return threadCore.getPhotos();
    }

    class MyUrlRunnable implements Runnable {
        private Photo photo;
        private String url;

        MyUrlRunnable(Photo p) {
            this.photo = p;
        }

        @Override
        public void run() {
            try {
                Photo nfo = flickrClient.getPhotosInterface().getInfo(photo.getId(), null);
                if (nfo.getOriginalSecret().isEmpty()) {
                    url = photo.getMedium640Url();
                } else {
                    photo.setOriginalSecret(nfo.getOriginalSecret());
                    url = photo.getMedium640Url();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String getUrl() {
            return url;
        }
    }

    String getImageUrl(final Photo p) {
        MyUrlRunnable urlRunnable = new MyUrlRunnable(p);
        Thread thread = new Thread(urlRunnable);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return urlRunnable.getUrl();
    }
}
