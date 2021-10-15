package cs699_a2021.invertedstack.reviewsx;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.concurrent.Executors;

import io.noties.markwon.Markwon;
import io.noties.markwon.editor.MarkwonEditor;
import io.noties.markwon.editor.MarkwonEditorTextWatcher;
import io.noties.markwon.ext.latex.JLatexMathPlugin;
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin;
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = findViewById(R.id.main_text);
        //final Markwon markwon = Markwon.create(getApplicationContext());
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
                .build();
        markwon.setMarkdown(textView, "**Hello there!** \n $$ \\LaTeX \\text{is working !!!!!}$$ \n ~~strikethrough doesn't work ?~~");

        EditText editText = findViewById(R.id.main_edit_text);
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
                markwon.setMarkdown(textView, editText.getText().toString());
            }
        });

    }
}