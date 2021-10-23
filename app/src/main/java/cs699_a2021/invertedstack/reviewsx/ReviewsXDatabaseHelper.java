package cs699_a2021.invertedstack.reviewsx;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ReviewsXDatabaseHelper extends SQLiteOpenHelper {
    // ref - http://www.codebind.com/android-tutorials-and-examples/android-sqlite-tutorial-example/
    private static String DATABASE_NAME = "ReviewsX.db";

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
                      PAPERS_TITLE + " TEXT, " +
                      PAPERS_AUTHORS + " TEXT, " +
                      PAPERS_BODY + " TEXT" +
                ")"
        );
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS " + COMMENTS_TABLE_NAME + " (" +
                      COMMENTS_PAPER_ID + " TEXT NOT NULL PRIMARY KEY, " +
                      COMMENTS_COMMENT_JSON + " TEXT " +
                ")"
        );
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS " + NOTES_TABLE_NAME + " (" +
                      NOTES_PAPER_ID + " TEXT NOT NULL PRIMARY KEY, " +
                      NOTES_NOTE_MD + " TEXT" +
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

    public boolean insertPaperData(String id, String title, String authors, String body) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PAPERS_PAPER_ID, id);
        contentValues.put(PAPERS_TITLE, title);
        contentValues.put(PAPERS_AUTHORS, authors);
        contentValues.put(PAPERS_AUTHORS, body);
        long result = db.insert(PAPERS_TABLE_NAME, null, contentValues);
        return result != -1;
    }
}
