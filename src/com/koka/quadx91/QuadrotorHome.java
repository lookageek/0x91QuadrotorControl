package com.koka.quadx91;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class QuadrotorHome extends Activity implements OnClickListener, DialogInterface{
	
	ListView devlv;
    Button bt;
    String[] devicelist;
    ArrayList<String> devarraylist;
    ArrayAdapter<String> arrayadap;
    String selectedFromList;
    Context cont = this;
    BtSerialInter btInter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //et = (EditText) findViewById(R.id.sendtext);
        //bt = (Button) findViewById(R.id.sendbutton);
        btInter = new BtSerialInter(getApplicationContext());
        devicelist = btInter.list();
        //Thread btthread = new Thread(btInter);
        //btthread.start();
        devlv = (ListView) findViewById(R.id.devicelist);
        bt = (Button) findViewById(R.id.startDiscovery);
        
    }
    //JAY: try doing asynctask for the lines on btInter and deviceList if performance is slow pg 67 android book
    public void onClick(View v)
    {
    	devarraylist = new ArrayList<String>();
    	for(String s : devicelist)
    		devarraylist.add(s);
    	arrayadap = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,devarraylist);
    	devlv.setAdapter(arrayadap);
    	devlv.setClickable(true);
    	devlv.setOnItemClickListener(new OnItemClickListener(){
    		 public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
    		         selectedFromList =(String) (devlv.getItemAtPosition(myItemInt));
    		         AlertDialog ad = new AlertDialog.Builder(cont).create();  
    		         ad.setTitle("Confirm Pairing");
    		         ad.setMessage("Pair with "+selectedFromList+" ?");  
    		         ad.setButton("OK", new DialogInterface.OnClickListener() {
    		         public void onClick(DialogInterface dialog, int which) {  
    		             btInter.connect(selectedFromList);
    		             dialog.dismiss();
    		             Intent intent = new Intent(QuadrotorHome.this, ChatWindow.class);
    		             startActivity(intent);
    		             }  
    		         });  
    		         ad.show();  
    		 }
    		 });
    	
    }
    
    public void dismiss()
    {
    	
    }

    public void cancel()
    {
    	
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
