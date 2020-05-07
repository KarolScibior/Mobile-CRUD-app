package com.example.lab2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private SimpleCursorAdapter adapterKursora;
    private ListView lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        lista = (ListView)findViewById(R.id.lista);
        LoaderManager.getInstance(this).initLoader(0, null, this);
        String[] mapujZ = new String[]{PomocnikBD.MARKA, PomocnikBD.MODEL};
        int[] mapujDo = new int[]{R.id.Marka, R.id.Model};
        adapterKursora = new SimpleCursorAdapter(this, R.layout.wiersz_listy, null, mapujZ, mapujDo, 0);
        lista.setAdapter(adapterKursora);
        lista.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        lista.setMultiChoiceModeListener(wielokrotnyWybor());
        lista.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(MainActivity.this, EditActivity.class);
                        intent.putExtra(PomocnikBD.ID, id);
                        startActivityForResult(intent, 0);
                    }
                }
        );

    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projekcja = {PomocnikBD.ID, PomocnikBD.MARKA, PomocnikBD.MODEL};
        CursorLoader loader = new CursorLoader(this, Provider.URI_ZAWARTOSCI, projekcja, null, null, null);
        return loader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        adapterKursora.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        adapterKursora.swapCursor(null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LoaderManager.getInstance(this).restartLoader(0, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pasek_kontekstowy_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.dodaj) {
            Intent intent = new Intent(MainActivity.this, EditActivity.class);
            intent.putExtra(PomocnikBD.ID, (long) -1);
            startActivityForResult(intent, 0);
        }
        return super.onOptionsItemSelected(item);
    }

    private AbsListView.MultiChoiceModeListener wielokrotnyWybor() {
        return new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater pompka = mode.getMenuInflater();
                pompka.inflate(R.menu.pasek_kontekstowy_listy, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                if(item.getItemId() == R.id.usun){
                    long[] zaznaczone = lista.getCheckedItemIds();

                    for(int i = 0; i < zaznaczone.length; i++) {
                        getContentResolver().delete(ContentUris.withAppendedId(Provider.URI_ZAWARTOSCI, zaznaczone[i]), null, null);
                    }
                    return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        };
    }
}
