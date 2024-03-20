package my.edu.utar.individualpracticalassignment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;

public class CompareNumbersActivity extends AppCompatActivity {

    private Button numberButton1, numberButton2, nextButton;
    private int num1, num2;
    private boolean question;
    private TextView comparisonTextView, correctTextView, questionNumber;
    private int score = 0;
    private int questionsAsked = 0;
    private static final int MAX_QUESTIONS = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare_numbers);

        comparisonTextView = findViewById(R.id.comparisonTextView);
        numberButton1 = findViewById(R.id.numberButton1);
        numberButton2 = findViewById(R.id.numberButton2);
        nextButton = findViewById(R.id.nextButton);
        correctTextView = findViewById(R.id.correctTextView);
        questionNumber = findViewById(R.id.questionNumberTextView);

        String difficultyLevel = getIntent().getStringExtra("difficultyLevel");
        generateRandomNumbers(difficultyLevel);
        updateQuestionNumber();

        // Set onClickListener for number buttons
        numberButton1.setOnClickListener(v -> {
            compareNumbers(num1, num2);
            disableNumberButtons();
        });

        numberButton2.setOnClickListener(v -> {
            compareNumbers(num2, num1);
            disableNumberButtons();
        });

        // Set onClickListener for nextButton
        nextButton.setOnClickListener(v -> {
            correctTextView.setVisibility(View.INVISIBLE);
            enableNumberButtons();
            // Generate new random numbers and update the UI
            generateRandomNumbers(difficultyLevel);
            updateQuestionNumber();

            nextButton.setVisibility(View.INVISIBLE);

        });
    }

    @SuppressLint("DefaultLocale")
    private void updateQuestionNumber() {
        questionNumber.setText(String.format("Question %d/%d", questionsAsked + 1, MAX_QUESTIONS));
    }

    private void disableNumberButtons() {
        numberButton1.setEnabled(false);
        numberButton2.setEnabled(false);
    }

    private void enableNumberButtons() {
        numberButton1.setEnabled(true);
        numberButton2.setEnabled(true);
    }

    @SuppressLint("DefaultLocale")
    private void generateRandomNumbers(String difficultyLevel) {
        // Generate random numbers
        Random random = new Random();
        int maxRange = getMaxRange(difficultyLevel);

        do {
            num1 = random.nextInt(maxRange);
            num2 = random.nextInt(maxRange);
        } while (num1 == num2);

        question = random.nextBoolean();

        // Update the UI with new numbers
        if(question) {
            comparisonTextView.setText(String.format("Which number is greater?\n%d or %d", num1, num2));
        } else {
            comparisonTextView.setText(String.format("Which number is smaller?\n%d or %d", num1, num2));
        }
        numberButton1.setText(String.valueOf(num1));
        numberButton2.setText(String.valueOf(num2));
    }

    private int getMaxRange(String difficultyLevel) {
        switch (difficultyLevel) {
            case "easy":
                return 10;
            case "medium":
                return 100;
            case "hard":
                return 1000;
            default:
                return 100; // Default to medium difficulty
        }
    }

    @SuppressLint("DefaultLocale")
    private void compareNumbers(int selectedNumber, int otherNumber) {
        String message;

        if (question) {
            if (selectedNumber > otherNumber) {
                message = "Congratulations, you are correct!";
                correctTextView.setTextColor(getColor(android.R.color.holo_green_dark));
                score++;
            } else {
                message = String.format("Sorry, your answer is wrong. \n%d is not greater than %d", selectedNumber, otherNumber);
                correctTextView.setTextColor(getColor(android.R.color.holo_red_dark));
            }
        } else {
            if (selectedNumber < otherNumber) {
                message = "Congratulations, you are correct!";
                correctTextView.setTextColor(getColor(android.R.color.holo_green_dark));
                score++;
            } else {
                message = String.format("Sorry, your answer is wrong. \n%d is not less than %d", selectedNumber, otherNumber);
                correctTextView.setTextColor(getColor(android.R.color.holo_red_dark));
            }
        }

        // Update the correctTextView with the message
        correctTextView.setText(message);
        correctTextView.setVisibility(View.VISIBLE);

        questionsAsked++;

        if (questionsAsked >= MAX_QUESTIONS) {
            nextButton.setVisibility(View.INVISIBLE);
            showFinalScore();

        } else {
            nextButton.setVisibility(View.VISIBLE);
        }
    }

    private void showFinalScore() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Game Over");
        builder.setMessage("Congratulations! You've completed all questions.\nYour score: " + score + "/" + MAX_QUESTIONS);

        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();
            Intent intent = new Intent(CompareNumbersActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
        builder.setCancelable(false);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showRules(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Rules of the Game");
        builder.setMessage("Gameplay:\n" +
                "\n" +
                "You will be presented with two numbers on the screen.\n\n" +
                "Your task is to determine which number meets the requirements based on the question.\n\n" +
                "You can do this by tapping on the appropriate button indicating your choice.");

        // Add a button to close the dialog
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.setCancelable(false);

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
            Intent intent = new Intent(CompareNumbersActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        noBtn.setOnClickListener(v -> alertDialog.dismiss());
        alertDialog.show();
    }
}