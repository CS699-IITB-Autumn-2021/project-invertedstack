package cs699_a2021.invertedstack.reviewsx;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import cs699_a2021.invertedstack.reviewsx.helpers.ReviewsXDatabaseHelper;
import io.noties.markwon.Markwon;
import io.noties.markwon.editor.MarkwonEditor;
import io.noties.markwon.editor.MarkwonEditorTextWatcher;
import io.noties.markwon.ext.latex.JLatexMathPlugin;
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin;
import io.noties.markwon.ext.tables.TablePlugin;
import io.noties.markwon.ext.tasklist.TaskListPlugin;
import io.noties.markwon.html.HtmlPlugin;
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin;
import io.noties.markwon.linkify.LinkifyPlugin;

/**
 * This activity is responsible for actually taking notes
 * The activity contains an instance of `markwon` library that handles the markdown
 * The activity also implements a nice animated live editor for markdown
 */
public class NoteTakingActivity extends AppCompatActivity {
    /**
     * The default string to use in case there are no notes corresponding to `paper_id` in the DB
     */
    String notes_string = "# Notes";
    /**
     * The ID of the paper for which we're showing the notes
     */
    String data_id = null;
    /**
     * Title of the paper -- this will be concatenated with `notes_string` in case there are no notes corresponding
     * to `paper_id` in the DB
     */
    String paper_title = null;
    /**
     * The TextView that will hold (markdown) notes
     */
    TextView textView;
    /**
     * The EditText that will be used to edit (markdown) notes
     */
    EditText editText;
    /**
     * Instance of database helper
     */
    ReviewsXDatabaseHelper db;

    /**
     * `onCreate` method for NoteTakingActivity. This method is responsible for setting up `markwon` and
     * using it to render markdown. There is lot more scope to do more fun things with Markdown here
     * The activity MUST be called with parameters `paper_id` and `paper_title`
     * The activity will check if notes exist for `paper_id`, if they don't we present a boilerplate using `paper_title`
     * Else we fetch from the DB and show it here
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_taking);
        Toolbar toolbar = findViewById(R.id.notetaking_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("List of papers");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle b = getIntent().getExtras();
        if(b == null) { // or some error such as data_id being NULL
            // TODO: Do a nice fancy graphical error here
            return;
        }
        else {
            data_id = b.getString("data_id");
            paper_title = b.getString("paper_title");
            notes_string += " on " + paper_title + "\n\nThis is just default note template. Note that this is NOT saved on the disk by default\n\nGet started by clicking on the _edit_ icon to view the markdown source for these notes. Then click on _save_ icon to save the note to the device. After saving, this note will ALWAYS be available on the device";
        }
        // Do we already have the notes for this `data_id`
        db = new ReviewsXDatabaseHelper(NoteTakingActivity.this);
        Cursor data = db.getNotesForPaperID(data_id);
        if(data.getCount() != 0) {
            data.moveToFirst();
            notes_string = data.getString(1);
            Log.d("NoteTakingActivity", "Got notes from DB");
        }
        // TODO: Change orientation of the parent LinearLayout depending on the aspect ratio
        textView = findViewById(R.id.main_text);
        // https://noties.io/Markwon/docs/v4/ext-latex/#blocks
        final Markwon markwon = Markwon.builder(this)
                .usePlugin(MarkwonInlineParserPlugin.create())
                .usePlugin(JLatexMathPlugin.create(textView.getTextSize(),
                        builder -> {
                            // enable inlines (require `MarkwonInlineParserPlugin`), by default `false`
                            builder.inlinesEnabled(true);

                            // use pre-4.3.0 LaTeX block parsing (by default `false`)
                            builder.blocksLegacy(true);

                            // by default true
                            builder.blocksEnabled(true);

                            // @since 4.3.0
                            builder.errorHandler(new JLatexMathPlugin.ErrorHandler() {
                                @Nullable
                                @Override
                                public Drawable handleError(@NonNull String latex, @NonNull Throwable error) {
                                    // Receive error and optionally return drawable to be displayed instead
                                    return null;
                                }
                            });
                        }))
                .usePlugin(StrikethroughPlugin.create())
                .usePlugin(TablePlugin.create(this))
                .usePlugin(TaskListPlugin.create(this))
                .usePlugin(HtmlPlugin.create())
                .usePlugin(LinkifyPlugin.create())
                .build();
        markwon.setMarkdown(textView, notes_string);
        editText = findViewById(R.id.main_edit_text);
        // https://noties.io/Markwon/docs/v4/editor/#getting-started-with-editor
        final MarkwonEditor editor = MarkwonEditor.create(markwon);
        editText.addTextChangedListener(MarkwonEditorTextWatcher.withProcess(editor));
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                notes_string = editText.getText().toString();
                markwon.setMarkdown(textView, notes_string);
            }
        });
    }

    /**
     * Menu inflater -- responsible for inflating the toggleable "edit" and "save" menu
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notetaking_menu, menu);
        return true;
    }

    /**
     * Click handler for the menu. The functionality is quite simple and implemented quite elegantly too
     * (edit) -> click -> (open editText, switch to "save" icon)
     * (save) -> click -> (close editText, update notes, switch to "edit" icon)
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.notetaking_action_edit:
                editText.setText(notes_string);
                // https://stackoverflow.com/a/50225618
                ViewGroup editor_view = findViewById(R.id.main_layout_for_edittext);
                ViewGroup text_view = findViewById(R.id.main_layout_for_text);
                editor_view.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
                text_view.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) editor_view.getLayoutParams();
                float new_weight = params.weight != 0 ? 0 : 1;
                editor_view.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        0,
                        new_weight
                ));
                // change icon https://stackoverflow.com/a/19882555
                int new_icon = new_weight == 1 ? R.drawable.ic_baseline_save_24 : R.drawable.ic_baseline_edit_24;
                item.setIcon(getDrawable(new_icon));
                // Save the note depending on state
                if(new_icon == R.drawable.ic_baseline_edit_24) {
                    db.updateNotesData(data_id, notes_string);
                    Log.d("NoteTakingActivity", "Saving to DB when new_weight = " + new_weight);
                }
                return true;
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}