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

    private static String PAPERS_TABLE_NAME = "Papers";
    private static String PAPERS_PAPER_ID = "Paper_ID";
    private static String PAPERS_TITLE = "Paper_Title";
    private static String PAPERS_AUTHORS = "Paper_Authors";
    private static String PAPERS_BODY = "Paper_Body";
    private static String PAPERS_CONF = "Paper_Conf";
    private static String PAPERS_YEAR = "Paper_Year";
    private static String PAPERS_CATEGORY = "Paper_Category";

    private static String COMMENTS_TABLE_NAME = "Comments";
    private static String COMMENTS_PAPER_ID = "Paper_ID";
    private static String COMMENTS_COMMENT_JSON = "Comments_JSON";

    private static String NOTES_TABLE_NAME = "Notes";
    private static String NOTES_PAPER_ID = "Paper_ID";
    private static String NOTES_NOTE_MD = "Note_MD";

    private static String COLLECTIONS_TABLE_NAME = "Collections";
    public static String COLLECTION_NAME = "Name";
    private static String COLLECTION_PAPER_ID = "Paper_ID";

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
                      PAPERS_BODY + " TEXT NOT NULL," +
                      PAPERS_CONF + " TEXT NOT NULL," +
                      PAPERS_YEAR + " TEXT NOT NULL," +
                      PAPERS_CATEGORY + " TEXT NOT NULL" +
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
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS " + COLLECTIONS_TABLE_NAME + " (" +
                      COLLECTION_NAME + " TEXT NOT NULL PRIMARY KEY " +
                ")"
        );
        this.addCollection(db, "Favourites");
        this.addCollection(db, "Wishlist");
        this.addCollection(db, "Currently reading");
        this.addCollection(db, "Already read");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PAPERS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + COMMENTS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + NOTES_TABLE_NAME);
        // Delete all collection tables too
        Cursor cursor = this.getAllCollections(db);
        while(cursor.moveToNext()) {
            String name = cursor.getString(0);
            this.deleteCollection(db, name);
        }
        db.execSQL("DROP TABLE IF EXISTS "+ COLLECTIONS_TABLE_NAME);
        onCreate(db);
    }

    public void addCollection(String name) {
        // The caller should make sure that name is NOT in the pre-existing collections
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLLECTION_NAME, name);
        db.insert(COLLECTIONS_TABLE_NAME, null, contentValues);
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS " + "Collection_" + name.replaceAll("\\s+", "_") + " (" +
                      COLLECTION_PAPER_ID + " TEXT NOT NULL PRIMARY KEY " +
                ")"
        );
    }

    public void addCollection(SQLiteDatabase db, String name) {
        // The caller should make sure that name is NOT in the pre-existing collections
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLLECTION_NAME, name);
        db.insert(COLLECTIONS_TABLE_NAME, null, contentValues);
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS " + "Collection_" + name.replaceAll("\\s+", "_") + " (" +
                        COLLECTION_PAPER_ID + " TEXT NOT NULL PRIMARY KEY " +
                        ")"
        );
    }

    public void deleteCollection(String name) {
        // The caller should make sure that collection with name exists
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(COLLECTIONS_TABLE_NAME, COLLECTION_NAME + " = ?", new String[]{name.replaceAll("\\s+", "_")});
        db.execSQL("DROP TABLE IF EXISTS Collection_" + name.replaceAll("\\s+", "_"));
    }

    public void deleteCollection(SQLiteDatabase db, String name) {
        // The caller should make sure that collection with name exists
        db.delete(COLLECTIONS_TABLE_NAME, COLLECTION_NAME + " = ?", new String[]{name.replaceAll("\\s+", "_")});
        db.execSQL("DROP TABLE IF EXISTS Collection_" + name.replaceAll("\\s+", "_"));
    }
    public void renameCollection(String old_name, String new_name) {
        // The caller should make sure that collection with old_name exists and that no collection with new_name exists
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("ALTER TABLE Collection_" + old_name.replaceAll("\\s+", "_") + " RENAME TO Collection_" + new_name.replaceAll("\\s+", "_"));
    }

    public boolean addPaperToCollection(String name, String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query("Collection_" + name.replaceAll("\\s+", "_"), null, COLLECTION_PAPER_ID + " = ?", new String[]{id}, null, null, null);
        if(cursor.getCount() == 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLLECTION_PAPER_ID, id);
            return db.insert("Collection_" + name.replaceAll("\\s+", "_"), null, contentValues) != -1;
        }
        return true;
    }

    public void deletePaperFromCollection(String name, String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query("Collection_" + name.replaceAll("\\s+", "_"), null, COLLECTION_PAPER_ID + " = ?", new String[]{id}, null, null, null);
        if(cursor.getCount() != 0) {
            db.delete("Collection_" + name.replaceAll("\\s+", "_"), COLLECTION_PAPER_ID + " = ?", new String[]{id});
        }
    }

    public boolean updatePaperData(String id, String title, String authors, String body, String conf, String year, String cat) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PAPERS_PAPER_ID, id);
        contentValues.put(PAPERS_TITLE, title);
        contentValues.put(PAPERS_AUTHORS, authors);
        contentValues.put(PAPERS_BODY, body);
        contentValues.put(PAPERS_CONF, conf);
        contentValues.put(PAPERS_YEAR, year);
        contentValues.put(PAPERS_CATEGORY, cat);
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
        contentValues.put(NOTES_NOTE_MD, note_markdown);
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

    public Cursor getPapersByConfYearCat(String conf, String year, String cat) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(PAPERS_TABLE_NAME, null, PAPERS_CONF + " = ? AND " + PAPERS_YEAR + " = ? AND " + PAPERS_CATEGORY + " = ?", new String[]{conf, year, cat}, null, null, null);
    }

    public Cursor getPaperByDataID(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(PAPERS_TABLE_NAME, null, PAPERS_PAPER_ID + " = ?", new String[]{id}, null, null, null);
    }

    public Cursor getCommentsForPaperID(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(COMMENTS_TABLE_NAME, null, COMMENTS_PAPER_ID + " = ?", new String[]{id}, null, null, null);
    }

    public Cursor getNotesForPaperID(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(NOTES_TABLE_NAME, null, NOTES_PAPER_ID + " = ?", new String[]{id}, null, null, null);
    }

    public Cursor getAllCollections() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(COLLECTIONS_TABLE_NAME, null, null, null, null, null, null);
    }

    public Cursor getAllCollections(SQLiteDatabase db) {
        return db.query(COLLECTIONS_TABLE_NAME, null, null, null, null, null, null);
    }

    public boolean isPaperInCollection(String name, String id) {
        // Caller must verify that ocllection with name exists
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query("Collection_" + name.replaceAll("\\s+", "_"), null, COLLECTION_PAPER_ID + " = ?", new String[]{id}, null, null, null).getCount() != 0;
    }

    public Cursor getAllPapersInCollection(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query("Collection_" + name.replaceAll("\\s+", "_"), null, null, null, null, null, null);
    }

    public void deleteAllPapers() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.rawQuery("DELETE FROM " + PAPERS_TABLE_NAME, null);
    }
}
