package my.edu.utar.individualpracticalassignment;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button compareNumbersButton = findViewById(R.id.compareNumbersButton);
        Button orderNumbersButton = findViewById(R.id.orderNumbersButton);
        Button composeNumbersButton = findViewById(R.id.composeNumbersButton);
        Button signOutButton = findViewById(R.id.signOutButton);

        signOutButton.setOnClickListener(v -> finish());

        compareNumbersButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ChooseLevelActivity.class);
            intent.putExtra("targetActivity", CompareNumbersActivity.class);
            startActivity(intent);
        });

        orderNumbersButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ChooseLevelActivity.class);
            intent.putExtra("targetActivity", OrderNumbersActivity.class);
            startActivity(intent);

        });

        composeNumbersButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ChooseLevelActivity.class);
            intent.putExtra("targetActivity", ComposeNumbersActivity.class);
            startActivity(intent);
        });
    }
}