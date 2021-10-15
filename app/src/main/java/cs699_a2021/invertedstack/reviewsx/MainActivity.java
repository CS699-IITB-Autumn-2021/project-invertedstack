package cs699_a2021.invertedstack.reviewsx;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.animation.LayoutTransition;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Set;
import java.util.concurrent.Executors;

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
import io.noties.prism4j.GrammarLocator;
import io.noties.prism4j.Prism4j;

public class MainActivity extends AppCompatActivity {

    String notes_string = "**Hello there!** \n $$ \\LaTeX \\text{is working !!!!!}$$ \n ~~strikethrough doesn't work ?~~";
    TextView textView;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // TODO: Change orientation of the parent LinearLayout depending on the aspect ratio
        textView = findViewById(R.id.main_text);
        // https://noties.io/Markwon/docs/v4/ext-latex/#blocks
        final Markwon markwon = Markwon.builder(this)
                .usePlugin(MarkwonInlineParserPlugin.create())
                .usePlugin(JLatexMathPlugin.create(textView.getTextSize(),
                        new JLatexMathPlugin.BuilderConfigure() {
                            @Override
                            public void configureBuilder(@NonNull JLatexMathPlugin.Builder builder) {
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
                            }
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

        /*
        Button button = findViewById(R.id.dummy_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 * References:
                 * https://developer.android.com/guide/topics/graphics/prop-animation#layout
                 * https://stackoverflow.com/a/49159490
                 *//*
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
            }
        });
        */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notetaking_menu, menu);
        return true;
    }



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
                int new_icon = new_weight == 1 ? android.R.drawable.ic_menu_save : android.R.drawable.ic_menu_edit;
                item.setIcon(getDrawable(new_icon));
                // Save the note depending on state
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}