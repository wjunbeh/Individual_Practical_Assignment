package my.edu.utar.individualpracticalassignment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ComposeNumbersActivity extends AppCompatActivity {

    private TextView targetTextView, correctAnswerTextView, questionNumber;
    private LinearLayout randomNumbersLayout, queueLayout;
    private Button submitButton, nextButton;
    private List<Integer> randomNumbers;
    private int targetNumber, targetNum1, targetNum2;
    private String difficultyLevel;
    private int score = 0;
    private int questionsAsked = 1;
    private static final int MAX_QUESTIONS = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_numbers);

        targetTextView = findViewById(R.id.targetTextView);
        randomNumbersLayout = findViewById(R.id.randomNumbersLayout);
        queueLayout = findViewById(R.id.queueLayout);
        submitButton = findViewById(R.id.submitButton);
        nextButton = findViewById(R.id.nextButton);
        correctAnswerTextView = findViewById(R.id.correctAnswerTextView);
        questionNumber = findViewById(R.id.questionNumberTextView);

        difficultyLevel = getIntent().getStringExtra("difficultyLevel");
        generateRandomNumbers(difficultyLevel);
        updateQuestionNumber();
        displayNumberButtons();

        submitButton.setOnClickListener(v -> submitAnswer());

        nextButton.setOnClickListener(v -> nextQuestion());
    }

    @SuppressLint("DefaultLocale")
    private void updateQuestionNumber() {
        questionNumber.setText(String.format("Question %d/%d", questionsAsked, MAX_QUESTIONS));
    }

    @SuppressLint("DefaultLocale")
    private void generateRandomNumbers(String difficultyLevel) {
        randomNumbers = new ArrayList<>();

        Random random = new Random();
        int maxRange = getMaxRange(difficultyLevel);
        targetNumber = random.nextInt(maxRange - 2) + 2;
        targetTextView.setText(String.format("Target Number: %d", targetNumber));

        //Generate correct answer
        targetNum1 = random.nextInt(targetNumber - 1) + 1;
        targetNum2 = targetNumber - targetNum1;
        randomNumbers.add(targetNum1);
        randomNumbers.add(targetNum2);


        for (int i = 0; i < 3; i++) {
            randomNumbers.add(random.nextInt(maxRange) + 1);
        }

        Collections.shuffle(randomNumbers);
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
                return 100; //default
        }
    }

    private void displayNumberButtons() {
        randomNumbersLayout.removeAllViews();
        queueLayout.removeAllViews();

        int buttonWidth = getResources().getDimensionPixelSize(R.dimen.button_width);
        int buttonHeight = getResources().getDimensionPixelSize(R.dimen.button_height);

        // Display numbers in randomNumbersLayout
        for (int number : randomNumbers) {
            Button numberButton = new Button(this);
            numberButton.setText(String.valueOf(number));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(buttonWidth, buttonHeight);
            layoutParams.setMargins(10, 0, 0, 0);
            numberButton.setLayoutParams(layoutParams);
            numberButton.setBackgroundColor(getColor(android.R.color.darker_gray));
            numberButton.setOnClickListener(v -> {
                if (randomNumbersLayout.indexOfChild(v) != -1) {
                    moveNumberToQueue((Button) v);
                } else {
                    moveNumberToRandom((Button) v);
                }
            });
            randomNumbersLayout.addView(numberButton);
        }
    }

    private void moveNumberToQueue(Button button) {
        // Remove the button from its current parent
        ViewGroup parentLayout = (ViewGroup) button.getParent();
        if (parentLayout != null) {
            parentLayout.removeView(button);
        }

        // Reattach the click listener
        button.setOnClickListener(v -> moveNumberToRandom((Button) v));

        // Move selected number button to queueLayout
        queueLayout.addView(button);
        button.setBackgroundColor(getColor(android.R.color.holo_blue_light));
    }

    private void moveNumberToRandom(Button button) {
        // Remove the button from its current parent
        ViewGroup parentLayout = (ViewGroup) button.getParent();
        if (parentLayout != null) {
            parentLayout.removeView(button);
        }

        // Reattach the click listener
        button.setOnClickListener(v -> moveNumberToQueue((Button) v));

        // Move selected number button back to randomNumbersLayout
        randomNumbersLayout.addView(button);
        button.setBackgroundColor(getColor(android.R.color.darker_gray));
    }

    @SuppressLint("SetTextI18n")
    private void submitAnswer() {
        if (queueLayout.getChildCount() <= 1) {
            Toast.makeText(this, "Incomplete. Please select two numbers.", Toast.LENGTH_SHORT).show();
            return;
        }

        disableButtonsInLayout(randomNumbersLayout);
        disableButtonsInLayout(queueLayout);

        int sum = 0;
        for (int i = 0; i < queueLayout.getChildCount(); i++) {
            Button button = (Button) queueLayout.getChildAt(i);
            sum += Integer.parseInt(button.getText().toString());
        }

        if (sum == targetNumber) {
            score++;
            correctAnswerTextView.setText("Congratulations, you are correct!");
            correctAnswerTextView.setTextColor(getColor(android.R.color.holo_green_dark));
            correctAnswerTextView.setVisibility(View.VISIBLE);
        } else {
            String correctAnswer = "Sorry, the answer is wrong. \nCorrect order: " + targetNum1 + ", " +
                    targetNum2;

            correctAnswerTextView.setText(correctAnswer);
            correctAnswerTextView.setTextColor(getColor(android.R.color.holo_red_dark));
            correctAnswerTextView.setVisibility(View.VISIBLE);
        }

        submitButton.setVisibility(View.GONE);
        nextButton.setVisibility(View.VISIBLE);
    }

    private void disableButtonsInLayout(LinearLayout layout) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            if (child instanceof Button && child.isEnabled()) {
                child.setEnabled(false);
            }
        }
    }

    private void nextQuestion() {
        if (questionsAsked < MAX_QUESTIONS) {
            generateRandomNumbers(difficultyLevel);
            displayNumberButtons();

            questionsAsked++;
            updateQuestionNumber();
            correctAnswerTextView.setText("");
            correctAnswerTextView.setVisibility(View.INVISIBLE);

            submitButton.setVisibility(View.VISIBLE);
            nextButton.setVisibility(View.GONE);

        } else {
            showFinalScore();
        }
    }

    private void showFinalScore() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Game Over");
        builder.setMessage("Congratulations! You've completed all questions.\nYour score: " + score + "/" + MAX_QUESTIONS);

        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();
            Intent intent = new Intent(ComposeNumbersActivity.this, MainActivity.class);
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
                "You are presented with a target number to compose.\n\n" +
                "Below the target number, several smaller numbers are provided.\n\n" +
                "You need to select combinations of these smaller numbers that, when added together, equal the target number.");

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
            Intent intent = new Intent(ComposeNumbersActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        noBtn.setOnClickListener(v -> alertDialog.dismiss());
        alertDialog.show();
    }
}
