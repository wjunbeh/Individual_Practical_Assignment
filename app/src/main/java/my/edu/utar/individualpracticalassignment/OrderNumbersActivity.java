package my.edu.utar.individualpracticalassignment;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ViewGroup;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class OrderNumbersActivity extends AppCompatActivity {

    private TextView instructionTextView, correctAnswerTextView, questionNumber;
    private LinearLayout randomNumbersLayout, queueLayout;
    private Button submitButton, nextButton;
    private List<Integer> numbers;
    private boolean question;
    private String difficultyLevel;
    private int score = 0;
    private int questionsAsked = 1;
    private static final int MAX_QUESTIONS = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_numbers);

        instructionTextView = findViewById(R.id.orderTextView);
        randomNumbersLayout = findViewById(R.id.randomNumbersLayout);
        queueLayout = findViewById(R.id.queueLayout);
        submitButton = findViewById(R.id.submitButton);
        nextButton = findViewById(R.id.nextButton);
        correctAnswerTextView = findViewById(R.id.correctAnswerTextView);
        questionNumber = findViewById(R.id.questionNumberTextView);

        difficultyLevel = getIntent().getStringExtra("difficultyLevel");
        generateRandomNumbers(difficultyLevel);
        updateQuestionNumber();
        displayNumbers();

        submitButton.setOnClickListener(v -> submitAnswer());

        nextButton.setOnClickListener(v -> nextQuestion());
    }

    @SuppressLint("DefaultLocale")
    private void updateQuestionNumber() {
        questionNumber.setText(String.format("Question %d/%d", questionsAsked, MAX_QUESTIONS));
    }

    @SuppressLint("SetTextI18n")
    private void generateRandomNumbers(String difficultyLevel) {
        numbers = new ArrayList<>();
        Random random = new Random();

        // Randomly decide whether to display in ascending or descending order
        question = random.nextBoolean();
        int maxRange = getMaxRange(difficultyLevel);
        for (int i = 0; i < 5; i++) {
            numbers.add(random.nextInt(maxRange));
        }

        // Sort numbers based on ascending or descending order
        if (!question) {
            instructionTextView.setText("Please order the numbers in descending order:");
        } else {
            instructionTextView.setText("Please order the numbers in ascending order:");
        }
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

    private void displayNumbers() {
        randomNumbersLayout.removeAllViews();
        queueLayout.removeAllViews();

        int buttonWidth = getResources().getDimensionPixelSize(R.dimen.button_width);
        int buttonHeight = getResources().getDimensionPixelSize(R.dimen.button_height);

        // Display numbers in randomNumbersLayout
        for (int number : numbers) {
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
        if (queueLayout.getChildCount() != 5) {
            Toast.makeText(this, "Incomplete. Please order all 5 numbers.", Toast.LENGTH_SHORT).show();
            return;
        }

        numbers.sort(question ? null : Collections.reverseOrder());

        List<Integer> sortedNumbers = new ArrayList<>();
        for (int i = 0; i < queueLayout.getChildCount(); i++) {
            Button button = (Button) queueLayout.getChildAt(i);
            sortedNumbers.add(Integer.parseInt(button.getText().toString()));
        }

        boolean isCorrect = true;
        for (int i = 0; i < sortedNumbers.size(); i++) {
            if (!sortedNumbers.get(i).equals(numbers.get(i))) {
                isCorrect = false;
                break;
            }
        }

        disableButtonsInLayout(queueLayout);

        if (isCorrect) {
            correctAnswerTextView.setText("Congratulations, you are correct!");
            correctAnswerTextView.setTextColor(getColor(android.R.color.holo_green_dark));
            correctAnswerTextView.setVisibility(View.VISIBLE);
            score++;
        } else {
            StringBuilder correctOrder = new StringBuilder("Sorry, the answer is wrong. \nCorrect order: ");
            for (int number : numbers) {
                correctOrder.append(number).append(", ");
            }
            correctOrder.delete(correctOrder.length() - 2, correctOrder.length());

            correctAnswerTextView.setText(correctOrder.toString());
            correctAnswerTextView.setTextColor(getColor(android.R.color.holo_red_dark));
            correctAnswerTextView.setVisibility(View.VISIBLE);
        }

        // Hide submitButton and show nextButton
        submitButton.setVisibility(View.GONE);
        nextButton.setVisibility(View.VISIBLE);
    }

    private void disableButtonsInLayout(LinearLayout layout) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            if (child instanceof Button) {
                child.setEnabled(false);
            }
        }
    }

    private void nextQuestion() {
        if (questionsAsked < MAX_QUESTIONS) {
            correctAnswerTextView.setText("");
            correctAnswerTextView.setVisibility(View.VISIBLE);

            generateRandomNumbers(difficultyLevel);
            displayNumbers();
            questionsAsked++;
            updateQuestionNumber();

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
            Intent intent = new Intent(OrderNumbersActivity.this, MainActivity.class);
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
                "You will be presented with a set of (random) numbers on the screen.\n\n" +
                "Your task is to rearrange these numbers in the correct order according to the instructions given.\n\n" +
                "Instructions will specify whether you need to arrange the numbers in ascending or descending order.\n");

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
            Intent intent = new Intent(OrderNumbersActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        noBtn.setOnClickListener(v -> alertDialog.dismiss());
        alertDialog.show();
    }
}