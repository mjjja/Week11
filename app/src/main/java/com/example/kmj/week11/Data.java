package com.example.kmj.week11;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by KMJ on 2017-05-11.
 */


public class Data {
    public String title = "";
    public String text = "";

    public Data (String title, String text){
        this.title=title;
        this.text=text;
    }

    public Data (File file) {
        title = file.getName();
        try {
            BufferedReader br = new BufferedReader(new
                    FileReader(file));
            String str = null;
            while ((str = br.readLine()) != null) text += str + "\n";
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String toString(){
        return title;
    }
}
