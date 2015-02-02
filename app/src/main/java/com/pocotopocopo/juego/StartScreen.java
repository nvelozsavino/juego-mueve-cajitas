package com.pocotopocopo.juego;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class StartScreen extends ActionBarActivity {
    private EditText cols;
    private EditText rows;
    private Button start;
    private static final String TAG = "Juego.StartScreen";
    public static final String COLS_KEY = "colsNumber";
    public static final String ROWS_KEY = "rowsNumber";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);
        cols = (EditText)findViewById(R.id.widthEdit);
        rows = (EditText)findViewById(R.id.heightEdit);
        start = (Button) findViewById(R.id.startButton);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "por sia");
                int colsNumber = 4;
                int rowsNumber = 4;
                try {
                    colsNumber = Integer.parseInt(cols.getText().toString());
                    rowsNumber = Integer.parseInt(rows.getText().toString());
                    Log.d(TAG, "cols = " + colsNumber);
                    if (colsNumber>2 && rowsNumber>2 ) {
                        Intent startGame = new Intent(getApplicationContext(), MainActivity.class);
                        startGame.putExtra(COLS_KEY, colsNumber);
                        startGame.putExtra(ROWS_KEY, rowsNumber);
                        startActivity(startGame);
                    }else{
                        throw new Exception("Invalid Board Size");
                    }

                }catch (Exception e){
                    Log.d(TAG,e.toString());
                    Toast toast = Toast.makeText(getApplicationContext(),"Invalid Board Size",Toast.LENGTH_SHORT);
                    toast.show();

                }




            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
