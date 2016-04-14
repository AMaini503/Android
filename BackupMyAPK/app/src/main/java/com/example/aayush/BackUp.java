package com.example.aayush;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.SingleLineTransformationMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.aayush.backupmyapk.R;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.PermissionCollection;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class BackUp extends AppCompatActivity {

    private TextView txt_info;
    private Process root_process;
    private DataOutputStream os;
    private List<String> al_file_names;
    private final static int requestCodeID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_back_up);

        this.txt_info = (TextView)findViewById(R.id.txt_info);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED)
                {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, requestCodeID);
        }

        GetFileList();
    }
    private void GetFileList()
    {
        try {
            root_process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(root_process.getOutputStream());

            //1. Delete the old temp.txt
            os.writeBytes("rm -rf " + Environment.getExternalStorageDirectory().toString() + "/temp.txt \n");

            //2. Command to list the files
            os.writeBytes("ls /data/app/ > " + Environment.getExternalStorageDirectory().toString() +  "/temp.txt\n");

            //3.Exit the Process
            os.writeBytes("exit \n");

            //4. Flush all the commands
            os.flush();

            //5. wait for the process to finish
            try {
                root_process.waitFor();
                if(root_process.exitValue() != 0) {
                    txt_info.setText("Root process exited abnormally(1)");
                }
            }
            catch(InterruptedException e) {
                txt_info.setText(e.toString());
            }
            os.close();

        }
        catch(IOException e) {
            txt_info.setText("Exception while running process as root(1)");
        }

        //Reading the file List
        BufferedReader br = null;
        al_file_names = new ArrayList<String>();
        try {
            String file_name;
            br = new BufferedReader(new FileReader(Environment.getExternalStorageDirectory() + "/temp.txt"));
            while((file_name = br.readLine())!=null) {
                al_file_names.add(file_name);
            }

            ListView list_vw_files = (ListView) findViewById(R.id.list_vw_files);
            ArrayAdapter<String> adapter_file_names = new ArrayAdapter<String>(this, R.layout.list_vw_apks, R.id.list_vw_apks_item, al_file_names);
            list_vw_files.setAdapter(adapter_file_names);
        }
        catch(IOException e) {
            e.printStackTrace();
        }


    }
    @Override
    public void onRequestPermissionsResult(int resultCode, String[] permissions, int[] grantResults){
        switch(resultCode){
            case(requestCodeID): {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    GetFileList();
                }
                else {
                    txt_info.setText("Permission denied to read SD Card");
                }
            }
            default: {
                txt_info.setText("Invalid Code ID");
            }
        }
    }
    public void startBackup(View view) {

        try {
            root_process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(root_process.getOutputStream());

            //1. Command to clear the old apks
            os.writeBytes("rm -rf " + Environment.getExternalStorageDirectory() + "/Apks/* \n");

            for (String file_name: al_file_names)
            {
                //2^.Create a folder for the app
                os.writeBytes("mkdir " + Environment.getExternalStorageDirectory().toString() + "/Apks/" + file_name + " \n");

                //2^. Command to copy the existing apks
                os.writeBytes("cp -rf /data/app/" + file_name + "/*.apk "
                        + Environment.getExternalStorageDirectory() + "/Apks/" + file_name +  "/ \n");
            }


            //3. Command to exit process
            os.writeBytes("exit \n");

            //4. Flush the commands
            os.flush();
//
//            try {
//                root_process.waitFor();
//                if(root_process.exitValue() !=0 ) {
//                    txt_info.setText("Root Process exited abnormally(2)");
//                }
//            }
//            catch (InterruptedException e) {
//                txt_info.setText(e.toString());
//            }
            os.close();
        }
        catch(IOException e) {
            txt_info.setText("Exception while running process as root(2)");
        }
    }
}