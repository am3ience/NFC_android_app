package com.example.paul.nfc2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity
{

    private Button _buttonWriteTag;
    private Button _buttonReadTag;
    private Button _buttonBeam;
    private Button buttonfile;
    private Button buttonBeam2;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Write to tag
        _buttonWriteTag = (Button) findViewById(R.id.buttonWriteTag);
        _buttonWriteTag.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(MainActivity.this, WriteTagActivity.class));
            }
        });

        //Read tag
        _buttonReadTag = (Button) findViewById(R.id.buttonReadTag);
        _buttonReadTag.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(MainActivity.this, ReadTagActivity.class));
            }
        });

        //Beam messages to someone else
        _buttonBeam = (Button) findViewById(R.id.buttonBeam);
        _buttonBeam.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(MainActivity.this, BeamActivity.class));
            }
        });

        //Beam a file to someone
        buttonfile = (Button) findViewById(R.id.buttonfile);
        buttonfile.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(MainActivity.this, FileTransfer.class));
            }
        });
    }
}