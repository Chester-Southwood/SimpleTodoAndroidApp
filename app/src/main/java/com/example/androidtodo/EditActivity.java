package com.example.androidtodo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditActivity extends AppCompatActivity
{
    private Button   btnSave;
    private EditText etItem;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        btnSave = findViewById(R.id.editSubmit);
        etItem  = findViewById(R.id.editText);

        getSupportActionBar().setTitle("Edit item");

        etItem.setText(getIntent().getStringExtra(MainActivity.KEY_ITEM_TEXT));

        btnSave.setOnClickListener(getOnClickEditSubmit());
    }

    private View.OnClickListener getOnClickEditSubmit()
    {
        return new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                if(etItem.getText().toString().isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Please make non-empty edits!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Intent intent = new Intent();
                    intent.putExtra(MainActivity.KEY_ITEM_TEXT, etItem.getText().toString());
                    intent.putExtra(MainActivity.KEY_ITEM_POSITION, getIntent().getExtras().getInt(MainActivity.KEY_ITEM_POSITION));

                    setResult(RESULT_OK, intent);

                    finish();
                }
            }
        };
    }
}
