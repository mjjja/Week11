package com.example.kmj.week11;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Boolean editmode=false;
    int index;
    String filetodelete="";
    String year="",month="",day="";
    Button bt;
    TextView tv;
    EditText et;
    ListView lv;
    DatePicker dp;
    LinearLayout ll1,ll2;
    ArrayList<Data> data = new ArrayList<>();
    ArrayAdapter<Data> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    void setListView(){
        lv=(ListView)findViewById(R.id.listview);
        adapter=new ArrayAdapter<Data>(this,android.R.layout.simple_list_item_1,data);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editmode=true;
                bt.setText("수정");
                ll1.setVisibility(View.GONE);
                ll2.setVisibility(View.VISIBLE);
                String path =getExternalPath();
                index=position;
                filetodelete=data.get(position).toString();
                String part[] = data.get(position).toString().split("-");
                year="20"+part[0];
                month=part[1];
                day=part[2].substring(0,2);
                dp.updateDate(Integer.parseInt(year),Integer.parseInt(month),Integer.parseInt(day));
                et.setText(data.get(position).text);
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                final int pos = position;
                dlg.setTitle("삭제하시겠습니까?")
                        .setMessage("삭제확인")
                        .setPositiveButton("닫기",null)
                        .setNegativeButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String path =getExternalPath();
                                filetodelete=path+ "diary/" + data.get(pos).toString();
                                File file = new File(filetodelete);
                                file.delete();
                                data.remove(pos);
                                adapter.notifyDataSetChanged();
                                tv.setText("등록된 메모 개수: "+data.size()+"개");
                                Toast.makeText(getApplicationContext(), "삭제되었습니다", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
                return true;
            }
        });
    }

    void init(){
        //Clear();
        setListView();
        tv = (TextView)findViewById(R.id.tvCount);
        et = (EditText)findViewById(R.id.et);
        bt = (Button)findViewById(R.id.btnsave);
        dp = (DatePicker)findViewById(R.id.dp);
        ll1 = (LinearLayout)findViewById(R.id.memolist);
        ll2 = (LinearLayout)findViewById(R.id.memowrite);
        loadData();
        checkpermission();
        tv.setText("등록된 메모 개수: "+data.size()+"개");
    }

    void OnClick(View v){
        if (v.getId()==R.id.btn1){
            bt.setText("등록");
            ll1.setVisibility(View.GONE);
            ll2.setVisibility(View.VISIBLE);
        }
        else if (v.getId()==R.id.btnsave){
            try {
                if (editmode){
                    //수정모드일때 수정전 파일을 삭제하는기능
                    String temp = getData();
                    String path = getExternalPath();
                    String already=path + "diary/" + temp;
                    File alreay = new File(already);
                    alreay.delete();
                    eraseItem(temp);
                    //해당날짜의 원래 메모를 삭제하는 기능
                    File file = new File(path+"diary/"+filetodelete);
                    eraseItem(filetodelete);
                    adapter.notifyDataSetChanged();
                    file.delete();
                }
                String path =getExternalPath();
                File file = new File(path+"diary/"+getData());
                file.delete();
                eraseItem(getData());
                String filename = getData();
                String filetext = et.getText().toString();
                BufferedWriter bw = new BufferedWriter(new FileWriter(path + "diary/" +
                        filename, false));
                bw.write(filetext);
                bw.close();
                data.add(new Data(filename, filetext));
                Toast.makeText(this, "저장완료", Toast.LENGTH_SHORT).show();
                et.setText("");
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, e.getMessage() + ":" + getFilesDir(),
                        Toast.LENGTH_SHORT).show();
            }

            editmode=false;
            adapter.notifyDataSetChanged();
            bt.setText("등록");
            tv.setText("등록된 메모 개수: "+data.size()+"개");
            ll1.setVisibility(View.VISIBLE);
            ll2.setVisibility(View.GONE);
        }
        else if(v.getId()==R.id.btncancel){
            et.setText("");
            editmode=false;
            adapter.notifyDataSetChanged();
            bt.setText("수정");
            tv.setText("등록된 메모 개수: "+data.size()+"개");
            ll1.setVisibility(View.VISIBLE);
            ll2.setVisibility(View.GONE);
        }
        /*
        else if (v.getId()==R.id.outread){
            try {
                String path =getExternalPath();
                BufferedReader br = new BufferedReader(new
                        FileReader(path + "test.txt"));
                String readStr = "";
                String str = null;
                while ((str = br.readLine()) != null) readStr += str + "\n";
                br.close();
                Toast.makeText(this, readStr.substring(0, readStr.length() - 1),
                        Toast.LENGTH_SHORT).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "File not found",
                        Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (v.getId()==R.id.outwrite){
            try {
                String path =getExternalPath();
                BufferedWriter bw = new BufferedWriter(new FileWriter(path + "diary/" +
                        "test.txt", true));
                bw.write("안녕하세요 SDCard Hello");
                bw.close();
                Toast.makeText(this, "저장완료", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, e.getMessage() + ":" + getFilesDir(),
                        Toast.LENGTH_SHORT).show();
            }
        }
        else if (v.getId()==R.id.mkdir){
            String path =getExternalPath();
            File file = new File(path + "diary");
            file.mkdir();
            String msg = "디렉터리 생성";
            if(file.isDirectory() == false) msg="디렉터리 생성 오류";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
        else if (v.getId()==R.id.dirfiles){
            String path =getExternalPath();
            File[] files =
                    new File(path+"diary").listFiles();
            String str = "";
            for(File f:files) {
                str += f.getName() + "\n";
                Toast.makeText(this,str,Toast.LENGTH_SHORT).show();
            }
            //et.setText(str);
        }
        */
    }

    void eraseItem(String temp){
        for (int i=0;i<data.size();i++){
            if (data.get(i).toString().equals(temp)){
                data.remove(i);
            }
        }
    }

    String getData(){
        return (dp.getYear()-2000)+"-"+(dp.getMonth()<9?("0"):(""))+(dp.getMonth()+1)+"-"+dp.getDayOfMonth()+".memo";
    }

    void Clear(){
        String path=getExternalPath();
        File[] files=new File(path+"diary").listFiles();
        for (File f:files) {
            f.delete();
        }
    }

    void loadData(){
        String path=getExternalPath();
        File[] files=new File(path+"diary").listFiles();
        for (File f:files){
            data.add(new Data(f));
        }
    }

    public String getExternalPath(){
        String sdPath = "";
        String ext = Environment.getExternalStorageState();
        if(ext.equals(Environment.MEDIA_MOUNTED)) {
            sdPath =
                    Environment.getExternalStorageDirectory ().getAbsolutePath() + "/";
            //sdPath = "/mnt/sdcard/";
        }else
            sdPath = getFilesDir() + "";
        Toast.makeText(getApplicationContext(),
                sdPath, Toast.LENGTH_SHORT).show();
        return sdPath;
    }

    void checkpermission(){
        int permissioninfo = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(permissioninfo == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(getApplicationContext(),
                    "SDCard 쓰기 권한 있음",Toast.LENGTH_SHORT).show();
        }
        else {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                Toast.makeText(getApplicationContext(),
                        "권한의 필요성 설명",Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        100);
            }
            else{
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        100);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        String str = null;
        if (requestCode == 100){
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                str = "SD Card 쓰기권한 승인";
            else{
                str = "SD Card 쓰기권한 거부";
            }
            Toast.makeText(this, str
                    ,Toast.LENGTH_SHORT).show();
        }
    }
}