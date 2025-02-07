package com.example.firstpage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Random;

public class GameView extends View {

    int dWidth, dHeight;
    Bitmap background, heartImage;
    Bitmap[] healthyFoods, harmfulFoods;
    Bitmap[] currentFoods; // Array to hold multiple food items
    Handler handler;
    Runnable runnable;

    long UPDATE_MILLIS = 30;
    int[] foodX, foodY; // Arrays to hold multiple food positions
    Random random;
    int points = 0;
    float TEXT_SIZE = 50;
    Paint textPaint;
    int life = 3;
    Context context;
    int foodSpeed;
    boolean[] isHealthy; // Array to determine if each food item is healthy or harmful
    MediaPlayer mpPoint, mpLoseLife;

    SharedPreferences sharedPreferences;
    int highScore;

    // Firebase Firestore instance
    FirebaseFirestore db;
    String username;

    public GameView(Context context) {
        super(context);
        this.context = context;
        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        dWidth = size.x;
        dHeight = size.y;

        sharedPreferences = context.getSharedPreferences("GamePreferences", Context.MODE_PRIVATE);
        highScore = sharedPreferences.getInt("highScore", 0);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Retrieve the username from Firebase Auth
        username = FirebaseAuth.getInstance().getCurrentUser().getDisplayName(); // Dynamically fetch username

        background = BitmapFactory.decodeResource(getResources(), R.drawable.background);
        background = Bitmap.createScaledBitmap(background, dWidth, dHeight, false);

        heartImage = BitmapFactory.decodeResource(getResources(), R.drawable.heart);
        heartImage = Bitmap.createScaledBitmap(heartImage, 100, 100, false);

        healthyFoods = new Bitmap[] {
                BitmapFactory.decodeResource(getResources(), R.drawable.apple),
                BitmapFactory.decodeResource(getResources(), R.drawable.broccoli),
                BitmapFactory.decodeResource(getResources(), R.drawable.carrot)
        };

        harmfulFoods = new Bitmap[] {
                BitmapFactory.decodeResource(getResources(), R.drawable.donut),
                BitmapFactory.decodeResource(getResources(), R.drawable.fries),
                BitmapFactory.decodeResource(getResources(), R.drawable.hamburger)
        };

        handler = new Handler();
        runnable = this::invalidate;
        random = new Random();

        resetFood();
        textPaint = new Paint();
        textPaint.setTextSize(TEXT_SIZE);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setColor(0xFFFFFFFF);

        mpPoint = MediaPlayer.create(context, R.raw.point);
        mpLoseLife = MediaPlayer.create(context, R.raw.pop);
    }

    private void resetFood() {
        int numberOfFoods = 2; // Adjust this to control how many food items appear

        foodX = new int[numberOfFoods];
        foodY = new int[numberOfFoods];
        isHealthy = new boolean[numberOfFoods];
        currentFoods = new Bitmap[numberOfFoods];

        for (int i = 0; i < numberOfFoods; i++) {
            foodX[i] = random.nextInt(dWidth - 100);
            foodY[i] = 0;
            isHealthy[i] = random.nextBoolean();
            foodSpeed = 15 + random.nextInt(10);
            currentFoods[i] = isHealthy[i] ? healthyFoods[random.nextInt(healthyFoods.length)] : harmfulFoods[random.nextInt(harmfulFoods.length)];
        }
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(background, 0, 0, null);

        for (int i = 0; i < foodX.length; i++) {
            foodY[i] += foodSpeed;

            if (foodY[i] >= dHeight) {
                if (isHealthy[i]) {
                    life--; // Lose life if healthy food is missed
                    if (mpLoseLife != null) mpLoseLife.start();
                    if (life == 0) {
                        updateHighScore();
                        saveGameDataToFirestore();  // Save to Firestore when the game ends
                        Intent intent = new Intent(context, GameOver.class);
                        intent.putExtra("points", points);
                        context.startActivity(intent);
                        ((Activity) context).finish();
                    }
                }
                // Reset this specific food item
                foodX[i] = random.nextInt(dWidth - 100);
                foodY[i] = 0;
                isHealthy[i] = random.nextBoolean();
                currentFoods[i] = isHealthy[i] ? healthyFoods[random.nextInt(healthyFoods.length)] : harmfulFoods[random.nextInt(harmfulFoods.length)];
            }

            canvas.drawBitmap(currentFoods[i], foodX[i], foodY[i], null);
        }

        canvas.drawText("Score: " + points, 20, TEXT_SIZE, textPaint);
        canvas.drawText("High Score: " + highScore, 20, TEXT_SIZE * 2, textPaint);

        for (int i = 0; i < life; i++) {
            canvas.drawBitmap(heartImage, dWidth - 120 - (i * 90), 30, null);
        }

        if (life != 0) handler.postDelayed(runnable, UPDATE_MILLIS);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                float touchX = event.getX();
                float touchY = event.getY();

                for (int i = 0; i < foodX.length; i++) {
                    int foodWidth = currentFoods[i].getWidth();
                    int foodHeight = currentFoods[i].getHeight();

                    if (touchX >= foodX[i] && touchX <= (foodX[i] + foodWidth) &&
                            touchY >= foodY[i] && touchY <= (foodY[i] + foodHeight)) {
                        if (isHealthy[i]) {
                            points++;
                            if (mpPoint != null) mpPoint.start();
                        } else {
                            life--;
                            if (mpLoseLife != null) mpLoseLife.start();
                            if (life == 0) {
                                updateHighScore();
                                saveGameDataToFirestore();  // Save to Firestore when the game ends
                                Intent intent = new Intent(context, GameOver.class);
                                intent.putExtra("points", points);
                                context.startActivity(intent);
                                ((Activity) context).finish();
                            }
                        }
                        // Reset this specific food item
                        foodX[i] = random.nextInt(dWidth - 100);
                        foodY[i] = 0;
                        isHealthy[i] = random.nextBoolean();
                        currentFoods[i] = isHealthy[i] ? healthyFoods[random.nextInt(healthyFoods.length)] : harmfulFoods[random.nextInt(harmfulFoods.length)];
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();  // Log the exception to help with debugging
        }
        return true;
    }

    public void updateHighScore() {
        if (points > highScore) {
            highScore = points;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("highScore", highScore);
            editor.apply();
        }
    }

    // Save game data to Firestore
    private void saveGameDataToFirestore() {
        DocumentReference gameRef = db.collection("Games").document(username);
        gameRef.set(new GameData(points, highScore))
                .addOnSuccessListener(aVoid -> {
                    // Handle success
                    System.out.println("Game data saved successfully!");
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    System.err.println("Error saving game data: " + e.getMessage());
                });
    }

    // Game data model class for Firestore
    public static class GameData {
        public int points;
        public int highScore;

        public GameData(int points, int highScore) {
            this.points = points;
            this.highScore = highScore;
        }
    }
}
