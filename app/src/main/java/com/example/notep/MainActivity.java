package com.example.notep;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener {


    MySql mySql;
    private ListView mylistview;
    private Button createButton;
    private MyBaseAdapter myBaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        createButton = findViewById(R.id.createButton);
        mylistview = findViewById(R.id.list_view);
        createButton.setOnClickListener(this);
        final List<Note> noteList = new ArrayList<>();
        mySql = new MySql(this);
        SQLiteDatabase db = mySql.getReadableDatabase();
        Cursor cursor = db.query(MySql.TABLE_NAME_RECORD, null, null, null, null, null, null + "DESC");
        if (cursor.moveToFirst()) {
            Note note;
            while (!cursor.isAfterLast()) {
                note = new Note();
                note.setId(
                        Integer.valueOf(cursor.getString(cursor.getColumnIndex(MySql.ID))));
                note.setTitleName(
                        cursor.getString(cursor.getColumnIndex(MySql.TITLE))
                );
                note.setTextBody(
                        cursor.getString(cursor.getColumnIndex(MySql.ARTICLE))
                );
                noteList.add(note);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();

        myBaseAdapter = new MyBaseAdapter(this, noteList, R.layout.list_item);
        mylistview.setAdapter(myBaseAdapter);
        mylistview.setOnItemClickListener(this);
        mylistview.setOnItemLongClickListener(this);
    }
        @Override
        public void onClick (View view) {
            switch (view.getId()) {
                    case R.id.createButton:

                    Intent intent = new Intent(MainActivity.this, EditActivity.class);
                    startActivity(intent);
                    MainActivity.this.finish();
                    break;

                    default:
                    break;
            }
        }

        @Override
        public void onItemClick (AdapterView < ? > adapterView, View view,int i, long l){
            Intent intent = new Intent(MainActivity.this,AmendActivity.class);
            Note note = (Note) mylistview.getItemAtPosition(i);
            intent.putExtra(MySql.TITLE,note.getTitleName().trim());
            intent.putExtra(MySql.ARTICLE,note.getTextBody().trim());
            intent.putExtra(MySql.ID,note.getId().toString().trim());

            this.startActivity(intent);
            MainActivity.this.finish();
        }

        @Override
        public boolean onItemLongClick (AdapterView < ? > adapterView, View view,int i, long l){
            Note note = (Note) mylistview.getItemAtPosition(i);
            showDialog(note,i);
            return true;
        }
    void showDialog(final Note note,final int position){

        final AlertDialog.Builder dialog =
                new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("是否删除？");
        String textBody = note.getTextBody();
        dialog.setMessage(
                textBody.length()>150?textBody.substring(0,150)+"...":textBody);
        dialog.setPositiveButton("删除",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDatabase db = mySql.getWritableDatabase();
                        db.delete(MySql.TABLE_NAME_RECORD,
                                MySql.ID +"=?",
                                new String[]{String.valueOf(note.getId())});
                        db.close();
                        myBaseAdapter.removeItem(position);
                        mylistview.post(new Runnable() {
                            @Override
                            public void run() {
                                myBaseAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
        dialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        dialog.show();
    }


    class MyBaseAdapter extends BaseAdapter {
            private List<Note> noteList;
            private Context context;
            private int layoutId;

            public MyBaseAdapter(Context context, List<Note> noteList, int layoutId) {
                this.context = context;
                this.noteList = noteList;
                this.layoutId = layoutId;
            }

            @Override
            public int getCount() {
                if (noteList != null && noteList.size() > 0)
                    return noteList.size();
                else
                    return 0;
            }

            @Override
            public Object getItem(int position) {
                if (noteList != null && noteList.size() > 0)
                    return noteList.get(position);
                else
                    return null;
            }

            public void removeItem(int position) {
                this.noteList.remove(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @SuppressLint("SetTextI18n")
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder viewHolder;
                if (convertView == null) {
                    convertView = LayoutInflater.from(
                            getApplicationContext()).inflate(R.layout.list_item, parent,
                            false);
                    viewHolder = new ViewHolder();
                    viewHolder.titleView = convertView.findViewById(R.id.list_item_title);
                    viewHolder.bodyView = convertView.findViewById(R.id.list_item_body);
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }
                Note note = noteList.get(position);
                String tile = note.getTitleName();
                viewHolder.titleView.setText((position + 1) + "." + (tile.length() > 7 ? tile.substring(0, 7) + "..." : tile));
                String body = note.getTextBody();
                viewHolder.bodyView.setText(body.length() > 13 ? body.substring(0, 12) + "..." : body);

                return convertView;
            }
//
            class ViewHolder {
                TextView titleView;
                TextView bodyView;
            }
        }
    }
