package cs699_a2021.invertedstack.reviewsx;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import io.noties.markwon.Markwon;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = findViewById(R.id.main_text);
        final Markwon markwon = Markwon.create(getApplicationContext());
        markwon.setMarkdown(textView, "**Hello there!** ~~Welcome to markdown~~");
    }
}