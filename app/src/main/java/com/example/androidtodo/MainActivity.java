package com.example.androidtodo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    protected static final String       KEY_ITEM_TEXT     = "item_text";
    protected static final String       KEY_ITEM_POSITION = "itemPosition";
    protected static final int          EDIT_TEXT_CODE    = 20;

    private                Button       btnAdd;
    private                EditText     etItem;
    private                ItemsAdapter itemsAdapter;
    private                List<String> itemList;
    private                RecyclerView rvItems;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //showing ui of application
        setContentView(R.layout.activity_main);
        btnAdd  = findViewById(R.id.btnAdd);
        etItem  = findViewById(R.id.editText);
        rvItems = findViewById(R.id.rvItems);
        loadItems();

        itemsAdapter = new ItemsAdapter(itemList, getRemoveClickListener(), getEditClickListener());


        rvItems.setAdapter(itemsAdapter);
        rvItems.setLayoutManager(new LinearLayoutManager(this));

        btnAdd.setOnClickListener(getAddClickListener());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        if(resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE)
        {
            //Extract the original position of the edited item from the position key
            final int    position = data.getExtras().getInt(KEY_ITEM_POSITION);

            //Retrieve the updated text value
            final String itemText = data.getStringExtra(KEY_ITEM_TEXT).toUpperCase();

            if(itemList.contains(itemText) && itemList.indexOf(itemText) != position)
            {
                Toast.makeText(getApplicationContext(), "Item already exists in Todo List!", Toast.LENGTH_SHORT).show();
            }
            else
            {
                //update the model at the right position with new item text
                itemList.set(position, itemText);

                //notify adapter
                itemsAdapter.notifyItemChanged(position);

                //persist the changes
                saveItems();

                //Speech bubble (Toast) to tell user EDIT was successful!
                Toast.makeText(getApplicationContext(), "Item updated successfully!", Toast.LENGTH_SHORT);
            }
        }
        else
        {
            Log.w("MainActivity", "Unknown call onActivityResult");
        }
    }

    /*
     * Returns File Object of the storage text file.
     *
     * @return
     */
    private File getDataFile()
    {
        return new File(getFilesDir(), "data.txt");
    }

    /*
     * Initilizes the itemList (Model) with data from storage file. (See getDataFile())
     * If IOException occurs, information will be logged on situation and itemList will be inilitized to an empty file.
     *
     */
    private void loadItems()
    {
        try
        {
            itemList = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        }
        catch (IOException e)
        {
            Log.e("MainActivity", "Error reading items", e);
            itemList = new ArrayList<>();
        }
    }


    //This function saves items by writing them into the data file
    /*
     * Copies contents of storage file to itemList (MODEL). (See getDataFile());
     * If IOException occurs, information will be logged on situation.
     *
     */
    private void saveItems()
    {
        try
        {
            FileUtils.writeLines(getDataFile(), itemList);
        }
        catch (IOException e)
        {
            Log.e("MainActivity", "Error writing items", e);
        }
    }

    /*
     * Sets itemsList with initial list items preloaded for demo purposes.
     *
     */
    private void setDemoData()
    {
        itemList = new ArrayList<String>();

        itemList.add("Buy Milk");
        itemList.add("Go to Landlord's Office");
        itemList.add("Program");
    }

    private View.OnClickListener getAddClickListener()
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final String todoItem = etItem.getText().toString().toUpperCase();
                if(todoItem.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Please add non-empty item!", Toast.LENGTH_SHORT).show();
                }
                else if(itemList.contains(todoItem.toUpperCase()))
                {
                    Toast.makeText(getApplicationContext(), "Item already exists in Todo List!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //add item to the model
                    itemList.add(todoItem);

                    //Notify adapter that an item is inserted
                    itemsAdapter.notifyItemInserted(itemList.size() - 1);

                    etItem.setText("");
                    Toast.makeText(getApplicationContext(), "Item was added!", Toast.LENGTH_SHORT).show();
                    saveItems();
                }
            }
        };
    }

    private IOnLongClickListener getRemoveClickListener()
    {
        return new IOnLongClickListener()
        {
            @Override
            public void onItemLongClicked(int position)
            {
                //delete the item on the model
                itemList.remove(position);

                //notify the adapter
                itemsAdapter.notifyItemRemoved(position);

                //notify user via speech bubble (Toast) that Item was removed!
                Toast.makeText(getApplicationContext(), "Item was removed!", Toast.LENGTH_SHORT).show();

                //save items
                saveItems();
            }
        };
    }

    private IOnClickListener getEditClickListener()
    {
        return new IOnClickListener()
        {
            @Override
            public void onItemClicked(int position)
            {
                Log.d("MainActivity", "Single click on position " + position);

                Intent extraIntent = new Intent(MainActivity.this, EditActivity.class);


                extraIntent.putExtra(KEY_ITEM_TEXT    , itemList.get(position));
                extraIntent.putExtra(KEY_ITEM_POSITION, position);

                startActivityForResult(extraIntent, EDIT_TEXT_CODE);
            }
        };
    }
}
