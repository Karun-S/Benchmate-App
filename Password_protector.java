package com.example.android.benchmate;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Set;



public class Password_protector extends AppCompatActivity {

    Hashtable hash = new Hashtable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_protector);
        Context context = this;
        File path = context.getFilesDir();
        File yourFile = new File(path, "credentials.txt");
        if(!yourFile.exists()) {
            try {
                yourFile.createNewFile();
            } catch (IOException e) {
                Log.e("Cannot create file", "File not found: " + e.toString());
            }
        }
        readFromFile();
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void writeToFile(View view) {


        EditText domainField = (EditText) findViewById(R.id.Domain_field);
        final String domain = domainField.getText().toString();
        EditText passwordField = (EditText) findViewById(R.id.Password_field);
        final String password = passwordField.getText().toString();

        if(domain.isEmpty())
        {
            display("Enter domain");
            return;
        }

        if(password.isEmpty())
        {
            display("Enter password");
            return;
        }
        boolean exists = hash.containsKey(domain);
        if(exists)
        {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Domain already in database");
            alertDialog.setMessage("Do you want to reset the password?");
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Reset the password",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getApplicationContext(), "Password reset", Toast.LENGTH_SHORT).show();
                            hash.put(domain, password);

                            }
                    });
            alertDialog.show();
        }
        else
        {
                hash.put(domain, password);
            Toast.makeText(Password_protector.this, "Domain registered", Toast.LENGTH_SHORT).show();
        }
        Set<String> keys = hash.keySet();
        for(String key: keys){
            //stringBuilder.append("The password for " + key + " is " + hash.get(key) + "\n");
            Context context = this;
            File path = context.getFilesDir();
            File yourFile = new File(path, "credentials.txt");
            try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(yourFile, true)))) {
                out.println(key +'\t' + hash.get(key));
            }catch (IOException e) {
                //exception handling left as an exercise for the reader
                Log.e("hash to file activity", "File not found: " + e.toString());
            }
        }

    }



    private void readFromFile() {

        try {
                Context context = this;
                File path = context.getFilesDir();
                File yourFile = new File(path, "credentials.txt");
                BufferedReader bufferedReader = new BufferedReader(new FileReader(yourFile));
                String receiveString = "";

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    String parts[] = receiveString.split("\t");
                    hash.put(parts[0], parts[1]);
                }

        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return;



    }

    public void displayHash(View view){
        Button button1 = (Button) findViewById(R.id.button1);
       // StringBuilder stringBuilder = new StringBuilder();
        Set<String> keys = hash.keySet();
          //  stringBuilder.append("The password for " + key + " is " + hash.get(key) + "\n");
            PopupMenu popup = new PopupMenu(Password_protector.this, button1);
            for(String key: keys) {
                popup.getMenu().add(key);
            }
            //registering popup with OnMenuItemClickListener
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    //Toast.makeText(Password_protector.this, "You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                    display(String.valueOf(hash.get(item.getTitle())));
                    return true;
                }
            });
            popup.show();
      //  display(stringBuilder);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void deleteDomain(View view) {
        Button button2 = (Button) findViewById(R.id.button2);
         final StringBuilder stringBuilder = new StringBuilder();
        final Context context = this;
        Set<String> keys = hash.keySet();
        //  stringBuilder.append("The password for " + key + " is " + hash.get(key) + "\n");
        PopupMenu popup = new PopupMenu(Password_protector.this, button2);
        for (String key : keys) {
            popup.getMenu().add(key);
        }
        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(Password_protector.this, "Domain deleted", Toast.LENGTH_SHORT).show();
                //display(String.valueOf(hash.get(item.getTitle())));
                hash.remove(item.getTitle());
                Set<String> keys1 = hash.keySet();
                File path = context.getFilesDir();
                File yourFile = new File(path, "credentials.txt");
                yourFile.delete();
                try {
                    if(!yourFile.createNewFile()) display("exists");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                for (String key : keys1) {
                    //stringBuilder.append("The password for " + key + " is " + hash.get(key) + "\n");
                    try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(yourFile, true)))) {
                        out.println(key + '\t' + hash.get(key));
            //            stringBuilder.append("The password for " + key + " is " + hash.get(key) + "\n");
             //           display(stringBuilder);

                    } catch (IOException e) {
                        //exception handling left as an exercise for the reader
                        Log.e("hash to file activity", "File not found: " + e.toString());
                    }
                }
                return true;
            }
        });
        popup.show();



          }


    private void display(StringBuilder text) {
        TextView quantityTextView = (TextView) findViewById(
                R.id.price_text_view);
        quantityTextView.setText("" + text);
    }

    private void display(String text) {
        TextView quantityTextView = (TextView) findViewById(
                R.id.price_text_view);
        quantityTextView.setText("" + text);
    }
}
