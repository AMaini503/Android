package com.example.aayush.redirectotp;

import android.Manifest;
import android.app.DialogFragment;
import android.database.MatrixCursor;
import android.support.v4.app.LoaderManager;
import android.content.ContentResolver;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.net.URI;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    public final static int requestCodeID = 1;
    private Button btn_inbox;
    private final static int LOADER_ID = 1;
    private LoaderManager.LoaderCallbacks<Cursor> mcallbacks;
    private SimpleCursorAdapter madapter;
    private final static Uri inboxUri = Uri.parse("content://sms/inbox");
    private final static String []Projection = {"address", "body"};
    private final static int[] viewIDs = {R.id.msg_address, R.id.msg_body};
    private ArrayList<String> original_address;
    private ArrayList<String> original_body;
    private ArrayList<String> original_ids;
    private Cursor FullCursor;
    private MatrixCursor SearchResultsCursor;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        //Request Permissions to read Messages
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, requestCodeID);
        }

        madapter = new SimpleCursorAdapter(this, R.layout.list_vw_messages_row, null,
                Projection, viewIDs, 0);
        ListView list_vw_msgs = (ListView) findViewById(R.id.list_vw_messages);
        list_vw_msgs.setAdapter(madapter);
        mcallbacks = this;

        LoaderManager lm = getSupportLoaderManager();
        lm.initLoader(LOADER_ID, null, mcallbacks);

        //Adding text changed listener for the search_box
        final EditText search_box = (EditText) findViewById(R.id.search_box);
        search_box.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String search_string = search_box.getText().toString();
                if(search_string.length() == 0) {
                    FullCursor = madapter.getCursor();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String search_string = search_box.getText().toString();
                int text_length = search_string.length();

                if(text_length == 0) {
                    madapter.swapCursor(FullCursor);
                }
                else {
                    //Clear the Search Results Cursor
                    SearchResultsCursor = new MatrixCursor(new String[]{"_id", "address", "body"});
                    FullCursor.moveToPosition(-1);
                    String address;
                    while(FullCursor.moveToNext()) {
                        address = FullCursor.getString(FullCursor.getColumnIndex("address")).toString().toLowerCase();
                        if(address.contains(search_string.toLowerCase())) {
                            SearchResultsCursor.addRow(new Object[]{
                                    FullCursor.getString(0),
                                    FullCursor.getString(1),
                                    FullCursor.getString(2)
                            });
                        }
                    }
                    FullCursor.moveToFirst();
                    madapter.swapCursor(SearchResultsCursor);
                }
            }
            @Override
            public void afterTextChanged(Editable s){
            }
        });

        original_address = new ArrayList<String>();
        original_body = new ArrayList<String>();
        original_ids = new ArrayList<String>();
    }
    @Override
    public void onRequestPermissionsResult(int resultCode, String []permissions, int[] grantResults){
        switch(resultCode) {
            case(requestCodeID): {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                }

            }
            default:
                DialogFragment pmd = new PermissionsDialog();
                pmd.show(getFragmentManager(), "permissions_error");
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch(id) {
            case LOADER_ID: {
                return new CursorLoader(this, inboxUri, new String[]{"_id", "address", "body"}, null, null, null);
            }
            default: {
                return null;
            }
        }

    }
    @Override public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch(loader.getId()) {
            case LOADER_ID: {
                madapter.swapCursor(cursor);

//                //move the cursor to the (before) first row
//                cursor.moveToFirst();
//
//                //clear the original lists
//                if(original_address != null){
//                    original_address.clear();
//                }
//                if(original_body != null) {
//                    original_body.clear();
//                }
//                if(original_ids != null) {
//                    original_ids.clear();
//                }
//                while(cursor.moveToNext()){
//                    original_address.add(cursor.getString(cursor.getColumnIndex("address")));
//                    original_body.add(cursor.getString(cursor.getColumnIndex("body")));
//                    original_ids.add(cursor.getString(cursor.getColumnIndex("_id")));
//                }
//
//                //reset the position of cursor to the first row
//                cursor.moveToFirst();
//                LastCursor = cursor;
                break;
            }
        }
    }
    @Override
    public void onLoaderReset(Loader loader) {
        madapter.swapCursor(null);
    }
}
