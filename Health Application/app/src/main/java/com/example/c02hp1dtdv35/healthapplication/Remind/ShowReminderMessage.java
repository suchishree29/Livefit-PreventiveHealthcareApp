package com.example.c02hp1dtdv35.healthapplication.Remind;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.example.c02hp1dtdv35.healthapplication.R;


public class ShowReminderMessage extends AppCompatActivity {

    private TextView txvMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_msg);

        txvMsg = findViewById(R.id.reminder_msg);

        Bundle extras = getIntent().getExtras();
        String msg = extras.getString("ReminderMsg");
        txvMsg.setText(msg);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.go_to_home) {
            startActivity(new Intent(getBaseContext(), ReminderActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
