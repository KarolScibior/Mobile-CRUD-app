package com.example.lab2;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Provider extends ContentProvider {

    private PomocnikBD pomocnikBD;
    private static final String IDENTYFIKATOR = "com.example.lab2.Provider";
    public static final Uri URI_ZAWARTOSCI = Uri.parse("content://" + IDENTYFIKATOR + "/" + PomocnikBD.NAZWA_TABELI);
    private static final int CALA_TABELA = 1;
    private static final int WYBRANY_WIERSZ = 2;
    private static final UriMatcher dopasowanieUri = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        dopasowanieUri.addURI(IDENTYFIKATOR, PomocnikBD.NAZWA_TABELI, CALA_TABELA);
        dopasowanieUri.addURI(IDENTYFIKATOR, PomocnikBD.NAZWA_TABELI + "/#", WYBRANY_WIERSZ);
    }

    @Override
    public boolean onCreate() {
        pomocnikBD = new PomocnikBD(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        int typUri = dopasowanieUri.match(uri);
        SQLiteDatabase db = pomocnikBD.getWritableDatabase();
        Cursor kursor = null;
        switch(typUri) {
            case CALA_TABELA:
                kursor = db.query(false, PomocnikBD.NAZWA_TABELI, projection, selection, selectionArgs, null, null, sortOrder, null, null);
                break;

            case WYBRANY_WIERSZ:
                kursor = db.query(false, PomocnikBD.NAZWA_TABELI, projection, dodajID(selection, uri), selectionArgs, null, null, sortOrder, null, null);
                break;

            default:
                throw new IllegalArgumentException("Nieznane URI: " + uri);
        }
        kursor.setNotificationUri(getContext().getContentResolver(), uri);
        return kursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int typUri = dopasowanieUri.match(uri);
        SQLiteDatabase db = pomocnikBD.getWritableDatabase();
        long dodany = 0;
        switch(typUri) {
            case CALA_TABELA:
                dodany = db.insert(PomocnikBD.NAZWA_TABELI, null, values);
                break;

            default:
                throw new IllegalArgumentException("Nieznane URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(PomocnikBD.NAZWA_TABELI + "/" + dodany);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int typUri = dopasowanieUri.match(uri);
        SQLiteDatabase db = pomocnikBD.getWritableDatabase();
        int usuniete = 0;
        switch(typUri) {
            case CALA_TABELA:
                usuniete = db.delete(PomocnikBD.NAZWA_TABELI, selection, selectionArgs);
                break;

            case WYBRANY_WIERSZ:
                usuniete = db.delete(PomocnikBD.NAZWA_TABELI, dodajID(selection, uri), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Nieznane URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return usuniete;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int typUri = dopasowanieUri.match(uri);
        SQLiteDatabase db = pomocnikBD.getWritableDatabase();
        int zaktualizowane = 0;
        switch(typUri) {
            case CALA_TABELA:
                zaktualizowane = db.update(PomocnikBD.NAZWA_TABELI, values, selection, selectionArgs);
                break;

            case WYBRANY_WIERSZ:
                zaktualizowane = db.update(PomocnikBD.NAZWA_TABELI, values, dodajID(selection, uri), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Nieznane URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return zaktualizowane;
    }

    private String dodajID(String selekcja, Uri uri) {
        if(selekcja != null && !selekcja.equals("")) {
            selekcja = selekcja + " and " + PomocnikBD.ID + "=" + uri.getLastPathSegment();
        } else {
            selekcja = PomocnikBD.ID + "=" + uri.getLastPathSegment();
        }
        return selekcja;
    }
}
