package my.edu.utar.individualpracticalassignment;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ChooseLevelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_level);

        Button easyButton = findViewById(R.id.easyButton);
        Button mediumButton = findViewById(R.id.mediumButton);
        Button hardButton = findViewById(R.id.hardButton);

        final Class<?> targetActivity = (Class<?>) getIntent().getSerializableExtra("targetActivity");

        easyButton.setOnClickListener(v -> startTargetActivity("easy", targetActivity));

        mediumButton.setOnClickListener(v -> startTargetActivity("medium", targetActivity));

        hardButton.setOnClickListener(v -> startTargetActivity("hard", targetActivity));
    }

    private void startTargetActivity(String difficultyLevel, Class<?> targetActivity) {
        Intent intent = new Intent(ChooseLevelActivity.this, targetActivity);
        intent.putExtra("difficultyLevel", difficultyLevel);
        startActivity(intent);
    }

    public void showRules(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Difficulty Levels:");
        builder.setMessage("The game offers different difficulty levels, such as easy, medium, and hard.\n\n" +
                "Easy: Suitable for children aged 1-4. \nIn the easy level, you will be presented with small numbers (0 to 9).\n\n" +
                "Medium: Suitable for children aged 5-7. \nIn the medium level, numbers may be larger (0 to 99).\n\n" +
                "Hard: Suitable for children aged above 8. \nIn the hard level, numbers can be challenging (0 to 999).");

        // Add a button to close the dialog
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.setCancelable(false);

        // Create and show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void goHomePage(View view) {
        View dialogView = getLayoutInflater().inflate(R.layout.home_alert_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();

        TextView yesBtn = dialogView.findViewById(R.id.yes);
        TextView noBtn = dialogView.findViewById(R.id.no);

        yesBtn.setOnClickListener(v -> {
            alertDialog.dismiss();
            finish();
            Intent intent = new Intent(ChooseLevelActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        noBtn.setOnClickListener(v -> alertDialog.dismiss());
        alertDialog.show();
    }
}