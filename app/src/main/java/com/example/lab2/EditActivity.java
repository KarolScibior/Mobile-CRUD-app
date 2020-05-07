package com.example.lab2;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.regex.Pattern;

public class EditActivity extends Activity {
    private EditText marka, model, android, www;
    private Button zapisz, anuluj, wwwButton;
    private long idWiersza;
    boolean czyMarkaOkej=false;
    boolean czyModelOkej=false;
    boolean czyAndroidOkej=false;
    boolean czyWwwOkej=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        marka=(EditText)findViewById(R.id.Marka);
        model=(EditText)findViewById(R.id.Model);
        android=(EditText)findViewById(R.id.Android);
        www=(EditText)findViewById(R.id.WWW);
        zapisz=(Button)findViewById(R.id.zapisz);
        zapisz.setEnabled(false);
        anuluj=(Button)findViewById(R.id.anuluj);
        wwwButton=(Button)findViewById(R.id.wwwButton);
        wwwButton.setEnabled(false);

        marka.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        String text = marka.getText().toString();
                        if (text.isEmpty()) {
                            Toast grzanka = Toast.makeText(getApplicationContext(), "Marka nie może być pusta", Toast.LENGTH_SHORT);
                            grzanka.show();
                            czyMarkaOkej = false;
                        } else {
                            czyMarkaOkej = true;
                        }
                        walidacja();
                    }
                }
        );

        model.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        String text = model.getText().toString();
                        if (text.isEmpty()) {
                            Toast grzanka = Toast.makeText(getApplicationContext(), "Model nie może być pusty", Toast.LENGTH_SHORT);
                            grzanka.show();
                            czyModelOkej = false;
                        } else if (!Pattern.matches("^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$", text)) {
                            Toast grzanka = Toast.makeText(getApplicationContext(), "Nieprawidłowy format", Toast.LENGTH_SHORT);
                            grzanka.show();
                            czyModelOkej = false;
                        } else {
                            czyModelOkej = true;
                        }
                        walidacja();
                    }
                }
        );

        android.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        String text = android.getText().toString();
                        if (text.isEmpty()) {
                            Toast grzanka = Toast.makeText(getApplicationContext(), "Android nie może być pusty", Toast.LENGTH_SHORT);
                            grzanka.show();
                            czyAndroidOkej = false;
                        } else if (!Pattern.matches("^\\d*\\.?\\d*$", text)) {
                            Toast grzanka = Toast.makeText(getApplicationContext(), "Nieprawidłowy format", Toast.LENGTH_SHORT);
                            grzanka.show();
                            czyAndroidOkej = false;
                        } else {
                            czyAndroidOkej = true;
                        }
                        walidacja();
                    }
                }
        );

        www.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        String text = marka.getText().toString();
                        if (text.isEmpty()) {
                            Toast grzanka = Toast.makeText(getApplicationContext(), "Www nie może być puste", Toast.LENGTH_SHORT);
                            grzanka.show();
                            czyWwwOkej = false;
                        } else {
                            czyWwwOkej = true;
                            wwwButton.setEnabled(true);
                        }
                        walidacja();
                    }
                }
        );

        idWiersza = -1;
        if(savedInstanceState != null) {
            idWiersza = savedInstanceState.getLong(PomocnikBD.ID);
        } else {
            Bundle tobolek = getIntent().getExtras();
            if(tobolek != null) {
                idWiersza = tobolek.getLong(PomocnikBD.ID);
            }
        }
        if (idWiersza != -1) {
            String[] projekcja = {PomocnikBD.MARKA, PomocnikBD.MODEL, PomocnikBD.ANDROID, PomocnikBD.WWW};
            Cursor kursor = getContentResolver().query(ContentUris.withAppendedId(Provider.URI_ZAWARTOSCI, idWiersza), projekcja,null,null,null);
            kursor.moveToFirst();
            int indeks = kursor.getColumnIndexOrThrow(PomocnikBD.MARKA);
            String tekst = kursor.getString(indeks);
            marka.setText(tekst);
            model.setText(kursor.getString(kursor.getColumnIndexOrThrow(PomocnikBD.MODEL)));
            android.setText(kursor.getString(kursor.getColumnIndexOrThrow(PomocnikBD.ANDROID)));
            www.setText(kursor.getString(kursor.getColumnIndexOrThrow(PomocnikBD.WWW)));
            kursor.close();
        }

        zapisz.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ContentValues wartosci = new ContentValues();
                        wartosci.put(PomocnikBD.MARKA,marka.getText().toString());
                        wartosci.put(PomocnikBD.MODEL,model.getText().toString());
                        wartosci.put(PomocnikBD.ANDROID,android.getText().toString());
                        wartosci.put(PomocnikBD.WWW,www.getText().toString());
                        if(idWiersza == -1) {
                            Uri uri = getContentResolver().insert(Provider.URI_ZAWARTOSCI, wartosci);
                            idWiersza = Integer.parseInt(uri.getLastPathSegment());
                        } else {
                            getContentResolver().update(ContentUris.withAppendedId(Provider.URI_ZAWARTOSCI, idWiersza), wartosci,null,null);
                        }
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
        );

        anuluj.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                }
        );
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(PomocnikBD.ID, idWiersza);
    }

    public void otworzWWW(View view) {
        String url = www.getText().toString();
        if(!url.startsWith("http://")){
            url="http://"+url;
        }
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(url));
        startActivity(intent);
    }

    public void walidacja() {
        if(czyMarkaOkej & czyModelOkej & czyAndroidOkej & czyWwwOkej) {
            zapisz.setEnabled(true);
        }
    }
}
