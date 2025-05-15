package com.example.pricecomparator.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import java.net.URL;
import org.springframework.stereotype.Service;

@Service
public class FileService {
    public List<String> getFileNames(String directoryPath, String store, String date) {
        List<String> fileNames = new ArrayList<>();
        URL url = getClass().getClassLoader().getResource(directoryPath);
        if (url == null) {
            throw new IllegalArgumentException("The resource directory does not exist: " + directoryPath);
        }

        File directory = new File(url.getFile());

        // verify if file exists
        if(!directory.exists() || !directory.isDirectory()) {
            throw new IllegalArgumentException("The directory does not exist or is not a directory.");
        }

        // receive all files from directory
        File[] files = directory.listFiles((dir, name) -> 
            name.toLowerCase().contains(store.toLowerCase()) &&
            name.toLowerCase().contains(date.toLowerCase()) &&
            name.endsWith(".csv")
        );

        // add files to the list if they exist
        if(files != null) {
            for(File file : files) {
                fileNames.add(directoryPath + "/" + file.getName());   // csv/lidl_2025-05-01.csv
             }
        }
        return fileNames;
    }

}
