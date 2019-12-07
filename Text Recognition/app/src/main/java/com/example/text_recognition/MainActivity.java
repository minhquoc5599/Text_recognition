package com.example.text_recognition;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.text_recognition.module.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    SessionManager sessionManager;
    FloatingActionButton btn;
    ListView lvDocument;
    ArrayList<Document> arrayDocument;
    DocumentAdapter adapter;
    Toolbar toolbar;
    static boolean count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        count = false;
        sessionManager = new SessionManager(this);

        Connect();

        Mapping();

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId()== R.id.menuLogout)
                {
                    sessionManager.Clear();
                    sessionManager.SetLogin(false);

                    Intent comeback = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(comeback);
                }
                return false;
            }
        });

        adapter = new DocumentAdapter(this, R.layout.document_row, arrayDocument);

        lvDocument.setAdapter(adapter);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MainActivity.this, OcrActivity.class);
                startActivity(intent);
            }
        });

        lvDocument.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), EditActivity.class);

                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        count = false;
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if(!count)
        {
            count = true;
            Toast.makeText(this, "Quay về lần nữa để thoát chương trình", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        }
    }


    private void Connect() {
        toolbar = findViewById(R.id.toolbarMain);
        btn =findViewById(R.id.icAdd);
        lvDocument = findViewById(R.id.lvDocumnet);
        toolbar.inflateMenu(R.menu.menu_logout);
    }

    private void Mapping() {
        arrayDocument = new ArrayList<>();
        arrayDocument.add(new Document("document",R.drawable.common_full_open_on_phone));
        arrayDocument.add(new Document("document",R.drawable.common_full_open_on_phone));
        arrayDocument.add(new Document("document",R.drawable.common_full_open_on_phone));
        arrayDocument.add(new Document("document",R.drawable.common_full_open_on_phone));
        arrayDocument.add(new Document("document",R.drawable.common_full_open_on_phone));
        arrayDocument.add(new Document("document",R.drawable.common_full_open_on_phone));
        arrayDocument.add(new Document("document",R.drawable.common_full_open_on_phone));
        arrayDocument.add(new Document("document",R.drawable.common_full_open_on_phone));
        arrayDocument.add(new Document("document",R.drawable.common_full_open_on_phone));
        arrayDocument.add(new Document("document",R.drawable.common_full_open_on_phone));

    }


}
