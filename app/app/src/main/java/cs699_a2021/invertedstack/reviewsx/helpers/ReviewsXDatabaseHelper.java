package cs699_a2021.invertedstack.reviewsx.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLiteDatabase helper for the entire app.
 * This class is responsible for ALL the database operations done by the app.
 */
public class ReviewsXDatabaseHelper extends SQLiteOpenHelper {
    // ref - http://www.codebind.com/android-tutorials-and-examples/android-sqlite-tutorial-example/
    /**
     * Name of the column (in above table) for storing names of the collections
     */
    public static String COLLECTION_NAME = "Name";
    /**
     * Name of the database file
     */
    private static String DATABASE_NAME = "ReviewsX.db";
    /**
     * Name of the table for storing paper related data
     */
    private static String PAPERS_TABLE_NAME = "Papers";
    /**
     * Name of the column (in above table) for storing paper ID
     */
    private static String PAPERS_PAPER_ID = "Paper_ID";
    /**
     * Name of the column (in above table) for storing paper title
     */
    private static String PAPERS_TITLE = "Paper_Title";
    /**
     * Name of the column (in above table) for storing paper authors (HTML)
     */
    private static String PAPERS_AUTHORS = "Paper_Authors";
    /**
     * Name of the column (in above table) for storing other info related to paper (HTML)
     */
    private static String PAPERS_BODY = "Paper_Body";
    /**
     * Name of the column (in above table) for storing conference the paper belongs to
     */
    private static String PAPERS_CONF = "Paper_Conf";
    /**
     * Name of the column (in above table) for storing the year of the conference the paper belongs to
     */
    private static String PAPERS_YEAR = "Paper_Year";
    /**
     * Name of the column (in above table) for storing the category of the paper in the conference it belongs to
     */
    private static String PAPERS_CATEGORY = "Paper_Category";
    /**
     * Name of the table for storing comments (discussion) related data
     */
    private static String COMMENTS_TABLE_NAME = "Comments";
    /**
     * Name of the column (in above table) for storing paper ID
     */
    private static String COMMENTS_PAPER_ID = "Paper_ID";
    /**
     * Name of the column (in above table) for storing discussion (JSON) that you get from the server. The entire raw JSON string is stored
     */
    private static String COMMENTS_COMMENT_JSON = "Comments_JSON";
    /**
     * Name of the table for storing notes related data
     */
    private static String NOTES_TABLE_NAME = "Notes";
    /**
     * Name of the column (in above table) for storing paper ID
     */
    private static String NOTES_PAPER_ID = "Paper_ID";
    /**
     * Name of the column (in above table) for storing actual notes string
     */
    private static String NOTES_NOTE_MD = "Note_MD";
    /**
     * Name of the table that hosts names of the collections. This just has one column.
     * In turn, we create a table for each collection separately. There are ALWAYS 4 default collections
     * avaibale - "Favourites", "Wishlist", "Already read" and "Currently reading"
     */
    private static String COLLECTIONS_TABLE_NAME = "Collections";
    /**
     * Name of the column in individual collection table for storing paper ID
     */
    private static String COLLECTION_PAPER_ID = "Paper_ID";

    /**
     * Constructor of the class
     *
     * @param context
     */
    public ReviewsXDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    /**
     * onCreate for the database helper. This method wil create the database file if it doesn't exist,
     * create appropriate tables if they don't exists and also will add the 4 default collections to the DB
     *
     * @param db
     */
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

    /**
     * onUpgrade method for the class. This will delete all the collections and all the tables from the DB
     *
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PAPERS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + COMMENTS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + NOTES_TABLE_NAME);
        // Delete all collection tables too
        Cursor cursor = this.getAllCollections(db);
        while (cursor.moveToNext()) {
            String name = cursor.getString(0);
            this.deleteCollection(db, name);
        }
        db.execSQL("DROP TABLE IF EXISTS " + COLLECTIONS_TABLE_NAME);
        onCreate(db);
    }

    /**
     * Method for adding collection with name `name` to the DB. The caller MUST verify that `name` is
     * NOT a pre-existing collection name
     *
     * @param name
     */
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

    /**
     * Method for adding collection with name `name` to the DB. The caller MUST verify that `name` is
     * NOT a pre-existing collection name. The additional parameter `db` prevents recursive handle generation for the DB
     *
     * @param db
     * @param name
     */
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

    /**
     * Method for deleting collection with name `name` from the DB. The caller MUST verify that collection with name
     * `name` actually exists
     *
     * @param name
     */
    public void deleteCollection(String name) {
        // The caller should make sure that collection with name exists
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(COLLECTIONS_TABLE_NAME, COLLECTION_NAME + " = ?", new String[]{name});
        db.execSQL("DROP TABLE IF EXISTS Collection_" + name.replaceAll("\\s+", "_"));
    }

    /**
     * Method for deleting collection with name `name` from the DB. The caller MUST verify that collection with name
     * `name` actually exists
     *
     * @param db
     * @param name
     */
    public void deleteCollection(SQLiteDatabase db, String name) {
        // The caller should make sure that collection with name exists
        db.delete(COLLECTIONS_TABLE_NAME, COLLECTION_NAME + " = ?", new String[]{name});
        db.execSQL("DROP TABLE IF EXISTS Collection_" + name.replaceAll("\\s+", "_"));
    }

    /**
     * Method for renaming the collection with name `old_name` to name `new_name`. The caller MUST ensure that collection
     * with name `old_name` exists and collection with name `new_name` does NOT exist
     *
     * @param old_name
     * @param new_name
     */
    public void renameCollection(String old_name, String new_name) {
        // The caller should make sure that collection with old_name exists and that no collection with new_name exists
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("ALTER TABLE Collection_" + old_name.replaceAll("\\s+", "_") + " RENAME TO Collection_" + new_name.replaceAll("\\s+", "_"));
    }

    /**
     * Add paper with ID `id` to the collection with name `name`. Return `true` if successful
     *
     * @param name
     * @param id
     * @return
     */
    public boolean addPaperToCollection(String name, String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query("Collection_" + name.replaceAll("\\s+", "_"), null, COLLECTION_PAPER_ID + " = ?", new String[]{id}, null, null, null);
        if (cursor.getCount() == 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLLECTION_PAPER_ID, id);
            return db.insert("Collection_" + name.replaceAll("\\s+", "_"), null, contentValues) != -1;
        }
        return true;
    }

    /**
     * Delete paper with ID `id` from the collection with name `name`
     *
     * @param name
     * @param id
     */
    public void deletePaperFromCollection(String name, String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (name.equals("All notes")) {
            Cursor cursor = db.query(NOTES_TABLE_NAME, null, NOTES_PAPER_ID + " = ?", new String[]{id}, null, null, null);
            if (cursor.getCount() != 0) {
                db.delete(NOTES_TABLE_NAME, NOTES_PAPER_ID + " = ?", new String[]{id});
            }

        } else {
            Cursor cursor = db.query("Collection_" + name.replaceAll("\\s+", "_"), null, COLLECTION_PAPER_ID + " = ?", new String[]{id}, null, null, null);
            if (cursor.getCount() != 0) {
                db.delete("Collection_" + name.replaceAll("\\s+", "_"), COLLECTION_PAPER_ID + " = ?", new String[]{id});
            }
        }
    }

    /**
     * Insert a row into table for papers data
     *
     * @param id      - ID of the paper
     * @param title   - title of the paper
     * @param authors - authors of the paper
     * @param body    - more info regarding the paper (one line summary etc. -- whatever came from the server pretty much)
     * @param conf    - conference in which the paper belongs
     * @param year    - year of the conference in which the paper belongs
     * @param cat     - category of the paper in the conference
     * @return
     */
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
        if (cursor.getCount() == 0) {
            result = db.insert(PAPERS_TABLE_NAME, null, contentValues);
        } else {
            result = db.update(PAPERS_TABLE_NAME, contentValues, PAPERS_PAPER_ID + " = ?", new String[]{id});
        }
        return result != -1;
    }

    /**
     * Insert a row into table for the discussion/comments data
     *
     * @param id           - ID of the paper
     * @param comment_json - JSON response received from the server
     * @return
     */
    public boolean updateCommentsData(String id, String comment_json) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COMMENTS_PAPER_ID, id);
        contentValues.put(COMMENTS_COMMENT_JSON, comment_json);
        Cursor cursor = db.query(COMMENTS_TABLE_NAME, new String[]{COMMENTS_PAPER_ID}, COMMENTS_PAPER_ID + " = ?", new String[]{id}, null, null, null);
        long result;
        if (cursor.getCount() == 0) {
            result = db.insert(COMMENTS_TABLE_NAME, null, contentValues);
        } else {
            result = db.update(COMMENTS_TABLE_NAME, contentValues, COMMENTS_PAPER_ID + "= ?", new String[]{id});
        }
        return result != -1;
    }

    /**
     * Insert a row into table for the notes
     *
     * @param id            - ID of the paper
     * @param note_markdown - Markdown string corresponding to notes of that paper
     * @return
     */
    public boolean updateNotesData(String id, String note_markdown) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NOTES_PAPER_ID, id);
        contentValues.put(NOTES_NOTE_MD, note_markdown);
        Cursor cursor = db.query(NOTES_TABLE_NAME, new String[]{NOTES_PAPER_ID}, NOTES_PAPER_ID + " = ?", new String[]{id}, null, null, null);
        long result;
        if (cursor.getCount() == 0) {
            result = db.insert(NOTES_TABLE_NAME, null, contentValues);
        } else {
            result = db.update(NOTES_TABLE_NAME, contentValues, NOTES_PAPER_ID + "= ?", new String[]{id});
        }
        return result != -1;
    }

    /**
     * Get all papers from the papers table
     *
     * @return
     */
    public Cursor getAllPapers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(PAPERS_TABLE_NAME, null, null, null, null, null, null);
    }

    /**
     * Get all papers corresponding to a specific conference year and category
     *
     * @param conf
     * @param year
     * @param cat
     * @return
     */
    public Cursor getPapersByConfYearCat(String conf, String year, String cat) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(PAPERS_TABLE_NAME, null, PAPERS_CONF + " = ? AND " + PAPERS_YEAR + " = ? AND " + PAPERS_CATEGORY + " = ?", new String[]{conf, year, cat}, null, null, null);
    }

    /**
     * Get paper corresponding to paper ID `id`
     *
     * @param id
     * @return
     */
    public Cursor getPaperByDataID(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(PAPERS_TABLE_NAME, null, PAPERS_PAPER_ID + " = ?", new String[]{id}, null, null, null);
    }

    /**
     * Get comments (discussion) corresponding to paper ID `id`
     *
     * @param id
     * @return
     */
    public Cursor getCommentsForPaperID(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(COMMENTS_TABLE_NAME, null, COMMENTS_PAPER_ID + " = ?", new String[]{id}, null, null, null);
    }

    /**
     * Get notes corresponding to paper ID `id`
     *
     * @param id
     * @return
     */
    public Cursor getNotesForPaperID(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(NOTES_TABLE_NAME, null, NOTES_PAPER_ID + " = ?", new String[]{id}, null, null, null);
    }

    /**
     * Get all collections in the database
     *
     * @return
     */
    public Cursor getAllCollections() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(COLLECTIONS_TABLE_NAME, null, null, null, null, null, null);
    }

    /**
     * Get all collections in the database
     *
     * @param db
     * @return
     */
    public Cursor getAllCollections(SQLiteDatabase db) {
        return db.query(COLLECTIONS_TABLE_NAME, null, null, null, null, null, null);
    }

    /**
     * Return `true` if paper with ID `id` belongs to a collection with name `name`
     *
     * @param name
     * @param id
     * @return
     */
    public boolean isPaperInCollection(String name, String id) {
        // Caller must verify that ocllection with name exists
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query("Collection_" + name.replaceAll("\\s+", "_"), null, COLLECTION_PAPER_ID + " = ?", new String[]{id}, null, null, null).getCount() != 0;
    }

    /**
     * Get all papers in a collection with name `name`
     *
     * @param name
     * @return
     */
    public Cursor getAllPapersInCollection(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        if (name.equals("All notes")) {
            return db.query(NOTES_TABLE_NAME, null, null, null, null, null, null);
        } else {
            return db.query("Collection_" + name.replaceAll("\\s+", "_"), null, null, null, null, null, null);
        }
    }

    /**
     * Delete all papers from the database
     */
    public void deleteAllPapers() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.rawQuery("DELETE FROM " + PAPERS_TABLE_NAME, null);
    }
}
