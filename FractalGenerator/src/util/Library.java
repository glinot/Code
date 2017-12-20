/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.net.URL;
import java.util.ArrayList;

/**
 *
 * @author Gregoire
 */
public class Library {

    private URL url;
    private boolean isZipped;
    private ArrayList<String> files = new ArrayList<>();

    public Library(URL url, boolean isZipped, ArrayList<String> files) {
        this.url = url;
        this.isZipped = isZipped;
        this.files = files;
    }

    public Library() {

    }

    public Library(URL url, boolean isZipped) {
        this.url = url;
        this.isZipped = isZipped;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public boolean isIsZipped() {
        return isZipped;
    }

    public void setIsZipped(boolean isZipped) {
        this.isZipped = isZipped;
    }

    public ArrayList<String> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<String> files) {
        this.files = files;
    }

    @Override
    public String toString() {
        return "Library{" + "url=" + url + ", isZipped=" + isZipped + ", files=" + files + '}';
    }

}