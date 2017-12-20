/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.audio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 *
 * @author Gregoire
 */
public class MinimLoader {

    public String sketchPath(String fileName) {
        return new File(fileName).getAbsolutePath();

    }

    public InputStream createInput(String fileName) throws FileNotFoundException {
        return new FileInputStream(new File(fileName));
    }
}
