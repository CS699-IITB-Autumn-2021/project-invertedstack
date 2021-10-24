package cs699_a2021.invertedstack.reviewsx;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.NotSerializableException;

public class ReviewsXDatabaseHelper extends SQLiteOpenHelper {
    // ref - http://www.codebind.com/android-tutorials-and-examples/android-sqlite-tutorial-example/
    private static String DATABASE_NAME = "ReviewsX.db";
    // TODO: All the tables MUST also have a key = name of the conference + year + type of the paper (Oral/Spotlight etc.)
    // TODO: Paper PDF link too maybe ?
    private static String PAPERS_TABLE_NAME = "Papers";
    private static String PAPERS_PAPER_ID = "Paper_ID";
    private static String PAPERS_TITLE = "Paper_Title";
    private static String PAPERS_AUTHORS = "Paper_Authors";
    private static String PAPERS_BODY = "Paper_Body";

    private static String COMMENTS_TABLE_NAME = "Comments";
    private static String COMMENTS_PAPER_ID = "Paper_ID";
    private static String COMMENTS_COMMENT_JSON = "Comments_JSON";

    private static String NOTES_TABLE_NAME = "Notes";
    private static String NOTES_PAPER_ID = "Paper_ID";
    private static String NOTES_NOTE_MD = "Note_MD";

    public ReviewsXDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS " + PAPERS_TABLE_NAME + " (" +
                      PAPERS_PAPER_ID + " TEXT NOT NULL PRIMARY KEY, " +
                      PAPERS_TITLE + " TEXT NOT NULL, " +
                      PAPERS_AUTHORS + " TEXT NOT NULL, " +
                      PAPERS_BODY + " TEXT NOT NULL" +
                ")"
        );
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS " + COMMENTS_TABLE_NAME + " (" +
                      COMMENTS_PAPER_ID + " TEXT NOT NULL PRIMARY KEY, " +
                      COMMENTS_COMMENT_JSON + " TEXT NOT NULL " +
                ")"
        );
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS " + NOTES_TABLE_NAME + " (" +
                      NOTES_PAPER_ID + " TEXT NOT NULL PRIMARY KEY, " +
                      NOTES_NOTE_MD + " TEXT NOT NULL" +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PAPERS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + COMMENTS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + NOTES_TABLE_NAME);
        onCreate(db);
    }

    public boolean updatePaperData(String id, String title, String authors, String body) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PAPERS_PAPER_ID, id);
        contentValues.put(PAPERS_TITLE, title);
        contentValues.put(PAPERS_AUTHORS, authors);
        contentValues.put(PAPERS_BODY, body);
        Cursor cursor = db.query(PAPERS_TABLE_NAME, new String[]{PAPERS_PAPER_ID}, PAPERS_PAPER_ID + " = ?", new String[]{id}, null, null, null);
        long result;
        if(cursor.getCount() == 0) {
            result = db.insert(PAPERS_TABLE_NAME, null, contentValues);
        }
        else {
            result = db.update(PAPERS_TABLE_NAME, contentValues, PAPERS_PAPER_ID + " = ?", new String[]{id});
        }
        return result != -1;
    }

    public boolean updateCommentsData(String id, String comment_json) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COMMENTS_PAPER_ID, id);
        contentValues.put(COMMENTS_COMMENT_JSON, comment_json);
        Cursor cursor = db.query(COMMENTS_TABLE_NAME, new String[]{COMMENTS_PAPER_ID}, COMMENTS_PAPER_ID + " = ?", new String[]{id}, null, null, null);
        long result;
        if(cursor.getCount() == 0) {
            result = db.insert(COMMENTS_TABLE_NAME, null, contentValues);
        }
        else {
            result = db.update(COMMENTS_TABLE_NAME, contentValues, COMMENTS_PAPER_ID + "= ?", new String[]{id});
        }
        return result != -1;
    }


    public boolean updateNotesData(String id, String note_markdown) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NOTES_PAPER_ID, id);
        contentValues.put(NOTES_PAPER_ID, note_markdown);
        Cursor cursor = db.query(NOTES_TABLE_NAME, new String[]{NOTES_PAPER_ID}, NOTES_PAPER_ID + " = ?", new String[]{id}, null, null, null);
        long result;
        if(cursor.getCount() == 0) {
            result = db.insert(NOTES_TABLE_NAME, null, contentValues);
        }
        else {
            result = db.update(NOTES_TABLE_NAME, contentValues, NOTES_PAPER_ID + "= ?", new String[]{id});
        }
        return result != -1;
    }

    public Cursor getAllPapers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(PAPERS_TABLE_NAME, null, null, null, null, null, null);
    }

    public Cursor getPaperByDataID(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(PAPERS_TABLE_NAME, null, PAPERS_PAPER_ID + " = ?", new String[]{id}, null, null, null);
    }

    public Cursor getCommentsForPaperID(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(COMMENTS_TABLE_NAME, null, COMMENTS_PAPER_ID + " = ?", new String[]{id}, null, null, null);
    }

    public boolean deleteAllPapers() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.rawQuery("DELETE FROM " + PAPERS_TABLE_NAME, null);
        return true;
    }
}
