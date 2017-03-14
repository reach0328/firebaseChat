package com.android.jh.friebasechat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    EditText editId,editpass;
    Button button,btn_sign;
    DatabaseReference userRef;
    FirebaseDatabase database;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("User");
        btn_sign = (Button) findViewById(R.id.btn_sign);
        btn_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeAlert();
            }
        });
        editId = (EditText) findViewById(R.id.edit_id);
        editpass = (EditText) findViewById(R.id.edit_pass);
        button = (Button) findViewById(R.id.btn_login);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String id = editId.getText().toString();
                final String pass = editpass.getText().toString();

                Log.w("MainActivity","id===================="+id);
                userRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.w("MainActivity","dataSnapshot===================="+dataSnapshot);
                        if(dataSnapshot.getChildrenCount()>0){
                            String fbpass = dataSnapshot.child("password").getValue().toString();
                            Log.w("MainActivity","fbpass===================="+fbpass);
                            String username = dataSnapshot.child("name").getValue().toString();
                            if(fbpass.equals(pass)){
                                Toast.makeText(MainActivity.this,"로그인되었습니다", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MainActivity.this,RoomListActivity.class);
                                intent.putExtra("id",id);
                                intent.putExtra("username", username);
                                startActivity(intent);
                            } else {
                                Toast.makeText(MainActivity.this,"비밀번호가 틀렸습니다", Toast.LENGTH_SHORT).show();
                            }
                        } else{
                            Toast.makeText(MainActivity.this,"user가 없습니다", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }
    private void makeAlert() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View alert_view = inflater.inflate(R.layout.alert_sign, null);
        //멤버의 세부내역 입력 Dialog 생성 및 보이기
        AlertDialog.Builder buider = new AlertDialog.Builder(this); //AlertDialog.Builder 객체 생성
        final EditText edit_id = (EditText)alert_view.findViewById(R.id.edit_signid);
        final EditText edit_name = (EditText) alert_view.findViewById(R.id.edit_signName) ;
        final EditText edit_pass = (EditText) alert_view.findViewById(R.id.edit_signpassword);
        Button btn = (Button) alert_view.findViewById(R.id.btn_signsave);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = edit_id.getText().toString();
                String name = edit_name.getText().toString();
                String pass = edit_pass.getText().toString();
                Map<String,String> userMap = new HashMap<>();
                userMap.put("name",name);
                userMap.put("password",pass);
//                if(userRef.child(id).toString())
                userRef.child(id).setValue(userMap);
                dialog.dismiss();
            }
        });
        buider.setView(alert_view);
        dialog = buider.create();
        dialog.show();
    }
}
