package com.android.jh.friebasechat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RoomListActivity extends AppCompatActivity {
    ListView listView;
    List<Room> list = new ArrayList<>();
    ListAdapter adapter;
    FloatingActionButton fab_add;
    DatabaseReference roomRef;
    FirebaseDatabase database;
    Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);
        Intent intent = getIntent();
        final String userid = intent.getExtras().getString("id");
        final String name = intent.getExtras().getString("username");
        database = FirebaseDatabase.getInstance();
        roomRef = database.getReference("room");
        fab_add = (FloatingActionButton) findViewById(R.id.fab_add);
        listView = (ListView) findViewById(R.id.listview);
        adapter = new ListAdapter(list,this);
        listView.setAdapter(adapter);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeAlert();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Room room = list.get(position);
                Intent intent = new Intent(RoomListActivity.this, RoomActivity.class);
                intent.putExtra("id",userid);
                intent.putExtra("username",name);
                intent.putExtra("key",room.getKey());
                intent.putExtra("title",room.getTitle());
                startActivity(intent);
            }
        });
        roomRef.addValueEventListener(roomListener);
    }

    private void makeAlert() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View alert_view = inflater.inflate(R.layout.alert, null);
        //멤버의 세부내역 입력 Dialog 생성 및 보이기
        AlertDialog.Builder buider = new AlertDialog.Builder(this); //AlertDialog.Builder 객체 생성
        final EditText edit = (EditText)alert_view.findViewById(R.id.edit_addtoroom);
        Button btn = (Button) alert_view.findViewById(R.id.btn_ok);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String roomname = edit.getText().toString();
                String key = roomRef.push().getKey();
                HashMap<String,String> roomMap = new HashMap<String, String>();
                for(Room room : list){
                    roomMap.put(room.getKey(),room.getTitle());
                }
                roomMap.put(key,roomname);
                roomRef.setValue(roomMap);
                dialog.dismiss();
            }
        });
        buider.setView(alert_view);
        dialog = buider.create();
        dialog.show();
    }

    ValueEventListener roomListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            list.clear();
            for(DataSnapshot snapshot :dataSnapshot.getChildren()) {
                Room room = new Room();
                room.setKey(snapshot.getKey());
                room.setTitle(snapshot.getValue().toString());
                list.add(room);
            }
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.w("MAIN", "loadPost:onCancelled", databaseError.toException());
        }
    };
}

class ListAdapter extends BaseAdapter {
    Context context;
    List<Room> roomList;
    LayoutInflater inflater;
    public ListAdapter(List<Room> roomList, Context context) {
        this.roomList = roomList;
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return roomList.size();
    }

    @Override
    public Object getItem(int position) {
        return roomList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
            convertView = inflater.inflate(R.layout.roomlistitem,null);
        TextView textTitle= (TextView) convertView.findViewById(R.id.text_title);
        Room room = roomList.get(position);
        textTitle.setText(room.getTitle());
        return convertView;
    }
}
