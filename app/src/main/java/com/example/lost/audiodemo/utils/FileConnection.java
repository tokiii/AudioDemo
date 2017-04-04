package com.example.lost.audiodemo.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by lost on 2017/3/22.
 */

public class FileConnection {
    private String fileName;
    private File _File = null;
    private long _fileSize = -999;

    public FileConnection(String name){
        fileName = name;
    }

    public boolean exists(){

        if( _File == null)
            _File = new File(fileName);

        if (!_File.exists() || !_File.isFile()) {
            return false;
        }

        return true;
    }

    public long fileSize(){
        //if( _fileSize == -999)
        {
            if( _File == null)
                _File = new File(fileName);

            if (!_File.exists() || !_File.isFile()) {
                _fileSize =  -1;
            }

            //Here we get the actual size
            _fileSize =  _File.length();
        }

        return _fileSize;
    }

    public void create() {

    }


    InputStream is = null;
    public InputStream openInputStream() throws Exception {
        if( is == null){
            is = new FileInputStream(_File);
        }

        return is;
    }

    OutputStream outputStream = null;
    public OutputStream openOutputStream() throws Exception {
        if (outputStream == null) {
            outputStream = new FileOutputStream(_File);
        }

        return outputStream;
    }

    public void close() throws Exception {
        if( is != null){
            is.close();
        }
    }
}
