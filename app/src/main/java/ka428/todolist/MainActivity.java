package ka428.todolist;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    //References that are needed out of scope
    List<String> titlearr;
    List<String> descarr;
    ListView listView;
    CustomAdapter customAdapter;
    String cachename = "todolistcache.txt";
    String filename = "todolist.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initializing variables
        titlearr = new ArrayList<String>();
        descarr = new ArrayList<String>();
        listView = (ListView) findViewById(R.id.listView);
        customAdapter = new CustomAdapter();
        final Button addButton = (Button) findViewById(R.id.add_button);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN); //hide keyboard in the beginning
        setTitle("To Do List"); //sets title of app
        onStartRead(); //check if anything is cached, if so, load them into listview


        addButton.setOnLongClickListener(new View.OnLongClickListener() { //Long click listener for "ADD"
            @Override
            public boolean onLongClick(View v) {
                writeData(filename); //saves data to a text file
                return true;
            }
        });


        addButton.setOnClickListener(new View.OnClickListener() { //Short click listener for "ADD"
            @Override
            public void onClick(View v) {
                //onclick
                shortClick(); //adds a title and description to the listview
            }
        });



        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() { //ListView long click listener
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                titlearr.remove(position); //Removes a title and description from the data arrays and updates list view
                descarr.remove(position);
                customAdapter.notifyDataSetChanged();
                cacheThread newcache = new cacheThread(); //Creates a new thread that saves the current string data array to a cache file
                newcache.start();
                return false;
            }
        });

    }

    public void shortClick() { //appends a title and description to list
        EditText titleText = (EditText) findViewById(R.id.editTitle);
        EditText descText = (EditText) findViewById(R.id.editDesc);
        //both text fields are empty
        if (TextUtils.isEmpty(titleText.getText()) || TextUtils.isEmpty(descText.getText())) {
            Toast.makeText(this, "Either field is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        titlearr.add(titleText.getText().toString());
        descarr.add(descText.getText().toString());
        AppendData(cachename, titlearr.size() - 1);
        listView.setAdapter(customAdapter); //updates the ListView for the added data

    }

    public void AppendData(String datafile, int count){
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput(datafile, Context.MODE_APPEND);
                outputStream.write(titlearr.get(count).getBytes());
                outputStream.write('\n');
                outputStream.write(descarr.get(count).getBytes());
                outputStream.write('\n');
                outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }



    public void writeData(String datafile){ //writes listview data (item and subitem) to a file. Input = filename
        FileOutputStream outputStream;
        int count = 0;

        try {
            outputStream = openFileOutput(datafile, Context.MODE_PRIVATE);
            while(count < titlearr.size()) {
                outputStream.write(titlearr.get(count).getBytes());
                outputStream.write('\n');
                outputStream.write(descarr.get(count).getBytes());
                outputStream.write('\n');
                count++;
            }
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(datafile == filename){
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
        }

    return;
    }


    public void onStartRead(){ //read whats cached and load it into listview
        String buff;
        String path = getApplicationContext().getFilesDir().toString() + "/" + cachename;
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(path)));
            while ((buff = br.readLine())  != null) {
                titlearr.add(buff);
                buff = br.readLine();
                if (buff == null) {
                    return;
                }
                descarr.add(buff);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(titlearr.size()!=0){
            listView.setAdapter(customAdapter);
        }
        return;
    }




    class cacheThread extends Thread{ //Thread to write data
        @Override
        public void run(){
            writeData(cachename);
        }
    }


    class CustomAdapter extends BaseAdapter{ //custom adapter for listview

        @Override
        public int getCount() {
            return titlearr.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.list, parent, false);
            TextView title = (TextView)convertView.findViewById(R.id.title);
            TextView desc = (TextView)convertView.findViewById(R.id.description);
            title.setText(titlearr.get(position));
            desc.setText(descarr.get(position));
            return convertView;
        }
    }
}
