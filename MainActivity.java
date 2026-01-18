
// MainActivity.java
package com.example.krestikinoliki;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;

/**
 * Основной Activity для игры "Крестики-нолики".
 * Реализует логику игрового поля, обработку ходов и определение победителя.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * Двумерный массив кнопок, представляющий игровое поле.
     */
    private Button[][] buttons = new Button[3][3];

    /**
     * TextView для отображения счета первого игрока.
     */
    private TextView textViewPlayerOne;

    /**
     * TextView для отображения счета второго игрока.
     */
    private TextView textViewPlayerTwo;

    /**
     * Флаг, определяющий, чей сейчас ход (true - первого игрока, false - второго).
     */
    private boolean playerOneTurn = true;

    /**
     * Счетчик ходов в текущей игре.
     */
    private int roundCount;

    /**
     * Количество очков, набранных первым игроком.
     */
    private int playerOnePoints;

    /**
     * Количество очков, набранных вторым игроком.
     */
    private int playerTwoPoints;

    /**
     * Метод вызывается при создании Activity.
     * Инициализирует UI элементы и устанавливает обработчики событий.
     *
     * @param savedInstanceState Объект Bundle, содержащий состояние Activity, если он был ранее уничтожен.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Получаем ID layout-файла динамически, чтобы избежать жесткой привязки
        int layoutId = getResources().getIdentifier("activity_main", "layout", getPackageName());
        setContentView(layoutId);

        // Получаем ID TextView для первого игрока и инициализируем его
        int textViewPlayerOneId = getResources().getIdentifier("text_view_p1", "id", getPackageName());
        textViewPlayerOne = findViewById(textViewPlayerOneId);

        // Получаем ID TextView для второго игрока и инициализируем его
        int textViewPlayerTwoId = getResources().getIdentifier("text_view_p2", "id", getPackageName());
        textViewPlayerTwo = findViewById(textViewPlayerTwoId);

        // Инициализируем кнопки игрового поля
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String buttonID = "button_" + i + j;
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                buttons[i][j] = findViewById(resID);
                buttons[i][j].setOnClickListener(this); // Устанавливаем this (MainActivity) как обработчик кликов
            }
        }

        // Инициализируем кнопку сброса игры
        int buttonResetId = getResources().getIdentifier("button_reset", "id", getPackageName());
        Button buttonReset = findViewById(buttonResetId);

        // Устанавливаем обработчик кликов для кнопки сброса
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetGame(); // Вызываем метод для сброса игры
            }
        });

    }

    /**
     * Обработчик кликов по кнопкам игрового поля.
     *
     * @param v View, по которой был произведен клик (в данном случае - кнопка игрового поля).
     */
    @Override
    public void onClick(View v) {
        // Проверяем, что кнопка пуста
        if (!((Button) v).getText().toString().equals("")){
            return; // Если кнопка не пуста, ничего не делаем
        }

        // Устанавливаем текст кнопки в зависимости от того, чей сейчас ход
        if (playerOneTurn){
            ((Button) v).setText("X");
        } else {
            ((Button) v).setText("O");
        }

        roundCount++; // Увеличиваем счетчик ходов

        // Проверяем, есть ли победитель
        if (checkForWin()){
            if (playerOneTurn){
                playerOneWins(); // Если победил первый игрок
            } else {
                playerTwoWins(); // Если победил второй игрок
            }
        } else if (roundCount == 9){
            draw(); // Если ничья
        } else {
            playerOneTurn = !playerOneTurn; // Передаем ход другому игроку
        }
    }

    /**
     * Проверяет, есть ли победитель на игровом поле.
     *
     * @return true, если есть победитель, false - в противном случае.
     */
    private boolean checkForWin() {
        // Создаем двумерный массив строк, представляющий текущее состояние игрового поля
        String[][] field = new String[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                field[i][j] = buttons[i][j].getText().toString(); // Заполняем массив значениями кнопок
            }
        }

        // Проверяем строки на наличие победителя
        for (int i = 0; i < 3; i++) {
            if (field[i][0].equals(field[i][1]) && field[i][0].equals(field[i][2]) && !field[i][0].equals("")) {
                return true; // Если в строке три одинаковых символа (X или O), возвращаем true
            }
        }

        // Проверяем столбцы на наличие победителя
        for (int i = 0; i < 3; i++) {
            if (field[0][i].equals(field[1][i]) && field[0][i].equals(field[2][i]) && !field[0][i].equals("")) {
                return true; // Если в столбце три одинаковых символа (X или O), возвращаем true
            }
        }

        // Проверяем диагонали на наличие победителя
        if (field[0][0].equals(field[1][1]) && field[0][0].equals(field[2][2]) && !field[0][0].equals("")) {
            return true; // Если на главной диагонали три одинаковых символа (X или O), возвращаем true
        }

        if (field[0][2].equals(field[1][1]) && field[0][2].equals(field[2][0]) && !field[0][2].equals("")) {
            return true; // Если на побочной диагонали три одинаковых символа (X или O), возвращаем true
        }

        // Если победителя нет, возвращаем false
        return false;
    }

    /**
     * Метод вызывается, когда побеждает первый игрок.
     * Обновляет счет, отображает сообщение и сбрасывает игровое поле.
     */
    private void playerOneWins(){
        playerOnePoints++; // Увеличиваем счет первого игрока
        // Динамически получаем текст для Toast
        int playerOneWinsStringId = getResources().getIdentifier("player_one_wins", "string", getPackageName());
        String playerOneWinsText = getString(playerOneWinsStringId);
        Toast.makeText(this, playerOneWinsText, Toast.LENGTH_SHORT).show(); // Отображаем сообщение о победе
        updatePointsText(); // Обновляем текст с очками игроков
        resetBoard(); // Сбрасываем игровое поле
    }

    /**
     * Метод вызывается, когда побеждает второй игрок.
     * Обновляет счет, отображает сообщение и сбрасывает игровое поле.
     */
    private void playerTwoWins(){
        playerTwoPoints++; // Увеличиваем счет второго игрока
        int playerTwoWinsStringId = getResources().getIdentifier("player_two_wins", "string", getPackageName());
        String playerTwoWinsText = getString(playerTwoWinsStringId);
        Toast.makeText(this, playerTwoWinsText, Toast.LENGTH_SHORT).show(); // Отображаем сообщение о победе
        updatePointsText(); // Обновляем текст с очками игроков
        resetBoard(); // Сбрасываем игровое поле
    }

    /**
     * Метод вызывается, когда игра заканчивается вничью.
     * Отображает сообщение и сбрасывает игровое поле.
     */
    private void draw(){
        int drawStringId = getResources().getIdentifier("draw_game", "string", getPackageName());
        String drawText = getString(drawStringId);
        Toast.makeText(this, drawText, Toast.LENGTH_SHORT).show(); // Отображаем сообщение о ничьей
        resetBoard(); // Сбрасываем игровое поле
    }

    /**
     * Обновляет текст TextView с очками игроков.
     */
    private void updatePointsText(){
        textViewPlayerOne.setText("Игрок А: " + playerOnePoints); // Обновляем текст для первого игрока
        textViewPlayerTwo.setText("Игрок В: " + playerTwoPoints); // Обновляем текст для второго игрока
    }

    /**
     * Сбрасывает игровое поле, очищая текст кнопок и устанавливая ход первого игрока.
     */
    private void resetBoard(){
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText(""); // Очищаем текст кнопки
            }
        }
        roundCount = 0; // Сбрасываем счетчик ходов
        playerOneTurn = true; // Устанавливаем ход первого игрока
    }

    /**
     * Сбрасывает всю игру, включая очки игроков и игровое поле.
     */
    private void resetGame(){
        playerOnePoints = 0; // Сбрасываем очки первого игрока
        playerTwoPoints = 0; // Сбрасываем очки второго игрока
        updatePointsText(); // Обновляем текст с очками игроков
        resetBoard(); // Сбрасываем игровое поле
    }
}
