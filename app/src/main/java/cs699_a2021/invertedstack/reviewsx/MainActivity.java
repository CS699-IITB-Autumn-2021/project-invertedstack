package cs699_a2021.invertedstack.reviewsx;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.TextView;

import io.noties.markwon.Markwon;
import io.noties.markwon.ext.latex.JLatexMathPlugin;
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
                .build();
        markwon.setMarkdown(textView, "**Hello there!** \n $$ \\LaTeX \\text{is working !!!!!}$$");
    }
}