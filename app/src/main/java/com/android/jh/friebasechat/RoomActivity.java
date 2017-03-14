package com.android.jh.friebasechat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RoomActivity extends AppCompatActivity {
    TextView textTitle;
    RecyclerView recyclerView;
    DatabaseReference roomRef;
    FirebaseDatabase database;
    EditText editChat;
    Button btn_send;
    List<Message> list = new ArrayList<>();
    String userid = "aaa";
    String username = "홍길동";
    custumAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        textTitle = (TextView) findViewById(R.id.textroomTitle);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        editChat = (EditText) findViewById(R.id.editChat);
        btn_send = (Button)findViewById(R.id.btn_send);

        Intent intent = getIntent();
        String key = intent.getExtras().getString("key");
        String title = intent.getExtras().getString("title");
        userid = intent.getExtras().getString("id");
        username = intent.getExtras().getString("username");
        database = FirebaseDatabase.getInstance();
        roomRef = database.getReference("chat").child(key);
        adapter = new custumAdapter(this, list, userid);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        roomRef.addValueEventListener(valueEventListener);
        btn_send.setOnClickListener(sendListener);

    }

    // 채팅 목록 처리
    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            list.clear();
            for(DataSnapshot snapshot :dataSnapshot.getChildren()) {
                String key = snapshot.getKey();
                Message msg = snapshot.getValue(Message.class);
                msg.setKey(key);
                list.add(msg);
            }
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    // 전송 처리
    View.OnClickListener sendListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String msgkey = roomRef.push().getKey();
            String msg = editChat.getText().toString();
            DatabaseReference msgRef = roomRef.child(msgkey);
            HashMap<String,String> msgMap= new HashMap<>();
            msgMap.put("userid",userid);
            msgMap.put("username",username);
            msgMap.put("message",msg);
            msgRef.setValue(msgMap);
        }
    };
}
class custumAdapter extends RecyclerView.Adapter<custumAdapter.Holder> {

    Context context;
    List<Message> msgList;
    String userid;

    public custumAdapter(Context context, List<Message> msgList,String userid){
        this.context = context;
        this.msgList = msgList;
        this.userid = userid;
    }
    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.room_item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        Message msg = msgList.get(position);
        holder.textusername.setText(msg.getUsername());
        holder.textchat.setText(msg.getMessage());
        if(userid.equals(msg.getUserid())){
            holder.layout.setGravity(Gravity.RIGHT);
        }else{
            holder.layout.setGravity(Gravity.LEFT);
        }
    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }
    public class Holder extends RecyclerView.ViewHolder{
        LinearLayout layout;
        TextView textusername,textchat;

        public Holder(View itemView) {
            super(itemView);
            layout = (LinearLayout) itemView.findViewById(R.id.layout);
            textusername = (TextView) itemView.findViewById(R.id.username);
            textchat = (TextView) itemView.findViewById(R.id.textchat);
        }
    }
}