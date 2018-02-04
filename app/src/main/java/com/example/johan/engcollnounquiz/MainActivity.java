package com.example.johan.engcollnounquiz;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import java.util.concurrent.ThreadLocalRandom;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.CheckBox;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    int currentScore = 0;                   //*Keeps current score
    int questionsTotal = 0;                 //*Keeps running total of questions
    int answerPosition = 0;                 //*Used to assign answer to radioButton/checkBox
    int currentQuestion = 0;                //*Marks current question/answer in array
    int picID;                              //*Saves ID for background drawable
    int[] questionsAsked = new int[SIZE];   //*Array of questions
    int[] answersArray = new int[SIZE];     //*Array of answers, matching questionsAsked
    int[] decoysUsed = new int[]{0,0,0};    //*Tracks decoys used in radio/CB

    static int SIZE = 50;                   //*Size of question/answer arrays

    /**
     *Method overridden to provide for state-saving of global variables and view states
     *
     * @param savedInstanceState is used to load the saved values
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null && savedInstanceState.getSerializable("myObj") != null) {
            //*Restore the state
            TextView tView1 = findViewById(R.id.question1);
            tView1.setText(savedInstanceState.getString("Q1"));

            TextView tView2 = findViewById(R.id.question2);
            tView1.setText(savedInstanceState.getString("Q2"));

            TextView tView3 = findViewById(R.id.question3);
            tView1.setText(savedInstanceState.getString("Q3"));

            TextView tView4 = findViewById(R.id.score_text);
            tView1.setText(savedInstanceState.getString("Score"));

            Button b1 = findViewById(R.id.buttonNext1);
            b1.setEnabled(savedInstanceState.getBoolean("b1Bool"));

            Button b2 = findViewById(R.id.buttonNext2);
            b1.setEnabled(savedInstanceState.getBoolean("b2Bool"));

            Button b3 = findViewById(R.id.buttonSubmit);
            b1.setEnabled(savedInstanceState.getBoolean("b3Bool"));

            questionsTotal = savedInstanceState.getInt("qAsked");
            currentScore = savedInstanceState.getInt("score");
            currentQuestion = savedInstanceState.getInt("qMarker");
            answerPosition = savedInstanceState.getInt("ansPos");
            questionsAsked = savedInstanceState.getIntArray("qArray");
            answersArray = savedInstanceState.getIntArray("ansArray");
            decoysUsed = savedInstanceState.getIntArray("dArray");
            picID = savedInstanceState.getInt("background");

            ScrollView scroll = findViewById(R.id.scrollView);
            scroll.setBackground(ContextCompat.getDrawable(this, picID));
            Log.d(" picID recovered", String.valueOf(picID));

        }else {
        //*Starts up the app
            checkDuplicates(1);
            showQuestion(1);
            showAnswers(1);

        }
    }

    /**
     * Method overridden to provide for sate-saving of global variables and view states
     *
     * @param outState is used to save values
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {

        int resID;
        String objectID;
        String tag;

        super.onSaveInstanceState(outState);

        TextView tView1 = findViewById(R.id.question1);
        outState.putString("Q1", String.valueOf(tView1.getText()));

        TextView tView2 = findViewById(R.id.question2);
        outState.putString("Q2", String.valueOf(tView2.getText()));

        TextView tView3 = findViewById(R.id.question3);
        outState.putString("Q3", String.valueOf(tView3.getText()));

        TextView tView4 = findViewById(R.id.score_text);
        outState.putString("Score", String.valueOf(tView4.getText()));

        Button b1 = findViewById(R.id.buttonNext1);
        outState.putBoolean("b1Bool", b1.isEnabled());

        Button b2 = findViewById(R.id.buttonNext2);
        outState.putBoolean("b2Bool", b2.isEnabled());

        Button b3 = findViewById(R.id.buttonSubmit);
        outState.putBoolean("b3Bool", b3.isEnabled());

        //*Allow Quiz to continue where it left off.
        outState.putInt("qAsked", questionsTotal);
        outState.putInt("score", currentScore);
        outState.putInt("qMarker", currentQuestion);
        outState.putInt("ansPos", answerPosition);
        outState.putIntArray("qArray", questionsAsked);
        outState.putIntArray("ansArray", answersArray);
        outState.putIntArray("dArray", decoysUsed);
        outState.putInt("background", picID);
        Log.d(" picID", String.valueOf(picID));

    }

    /**
     * Method used to allow user to dismiss on-screen keyboard by tapping elsewhere on the screen.
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View view = getCurrentFocus();
        if (view != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) && view instanceof EditText && !view.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            view.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + view.getLeft() - scrcoords[0];
            float y = ev.getRawY() + view.getTop() - scrcoords[1];
            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom())
                ((InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * Method used to generate and display question based on current position in array.
     *
     * @param toggle allows for 1:RadioButtons, 2:CheckBoxes, 3:EditText switching
     */
    private void showQuestion(int toggle){

        String pic;                                 //*Holds string for background pic
        String questionText;                        //*Holds string corresponding to question
        int questionID;                             //*Holds int identifier of string resource

        questionText = "question" + questionsAsked[currentQuestion];
        questionID = MainActivity.this.getResources()
                .getIdentifier(questionText, "string", MainActivity.this.getPackageName());
        questionText = MainActivity.this.getResources()
                .getString(questionID);

        //*Assigns TextView using identifier and sets text field to selected question
        if (toggle == 1) {
            TextView view = findViewById(R.id.question1);
            view.setText(questionText);
        } else if (toggle == 2) {
            TextView view = findViewById(R.id.question2);
            view.setText(questionText);
        } else {
            TextView view = findViewById(R.id.question3);
            view.setText(questionText);
        }

        //*Display appropriate picture
        pic = "pic" + questionsAsked[currentQuestion];
        picID = MainActivity.this.getResources()
                .getIdentifier(pic, "drawable", MainActivity.this.getPackageName());

        ScrollView scrollView = (ScrollView)this.findViewById(R.id.scrollView);
        scrollView.setBackground(ContextCompat.getDrawable(this, picID));

    }

    /**
     * Method used to generate and display answers and decoys, based on current array position
     * Only displays answers/decoys for radio/CB, not editText
     *
     * @param toggle allows for 1:RadioButtons, 2:CheckBoxes switching
     */
    private void showAnswers(int toggle) {

        String answerText;                          //*Holds string corresponding to answer
        String decoyText;                           //*Holds string corresponding to answer
        String objectID;                            //*Holds string corresponding to correct object

        int answerID;                               //*Holds int identifier for correct answer
        int correctPos;                             //*Holds number of button for correct answer
        int decoyID;                                //*Holds number of button for decoy answer

        answerText = "answer" + answersArray[currentQuestion];      //*String to call string resource

        answerID = MainActivity.this.getResources()
                .getIdentifier(answerText, "string", MainActivity.this.getPackageName());
        answerText = MainActivity.this.getResources()
                .getString(answerID);

        correctPos = randomNum(1,4);          //*Number to assign correct answer button
        answerPosition = correctPos;

        for (int marker = 1; marker <= 4; marker++) {
            if (String.valueOf(marker) == String.valueOf(correctPos)) {
                if (toggle == 1) {
                    objectID = "radio" + marker;
                    displayRadio(objectID, answerText);
                } else if (toggle == 2) {
                    objectID = "check" + marker;
                    displayCheck(objectID, answerText);
                }
            }else {
                checkDuplicates(2);
                for (int i = 0; i <= 2; i++) {
                    decoyText = "answer" + String.valueOf(decoysUsed[i]);
                    Log.d(" ArrayDecoy", decoyText);
                    decoyID = MainActivity.this.getResources()
                            .getIdentifier(decoyText, "string", MainActivity.this.getPackageName());
                    decoyText = MainActivity.this.getResources()
                            .getString(decoyID);

                    if (toggle == 1) {
                        objectID = "radio" + marker;
                        displayRadio(objectID, decoyText);
                    } else if (toggle == 2) {
                        objectID = "check" + marker;
                        displayCheck(objectID, decoyText);
                    }
                }
            }
        }
        currentQuestion++;                          //*Increments array marker
        questionsTotal = currentQuestion;           //*Updates running question total
    }

    /**
     * Method used to display a radioButton
     *
     * @param name is used to generate the radioButton's ID
     * @param text is used to set the radioButton's text
     */
    private void displayRadio (String name, String text){

        int resID;

        resID = getResources().getIdentifier(name, "id", getPackageName());

        RadioButton correctObject = (RadioButton) this.findViewById(resID);
        correctObject.setText(text);

    }

    /**
     * Method used to display a checkBox
     *
     * @param name is used to generate the checkBox's ID
     * @param text is used to set the checkBox's text
     */
    private void displayCheck (String name, String text){

        int resID;

        resID = getResources().getIdentifier(name, "id", getPackageName());

        CheckBox correctObject = (CheckBox) this.findViewById(resID);
        correctObject.setText(text);

    }

    /**
     * Method used to check if the radioButton pressed, is the correct one
     *
     * @param view facilitates retrieving the ID of the pressed radioButton
     */
    public void checkRadioAnswer(View view) {

        String radioName;                           //*String holds radioButton's name
        int wrongID;                                //*Int identifier; changes wrong button's colour
        int checkID;                                //*Int identifier; changes wrong button's colour

        radioName = "radio" + answerPosition;
        checkID = getResources().getIdentifier(radioName, "id", getPackageName());

        //*Assigns button to change colour
        RadioButton correctButton = (RadioButton)this.findViewById(checkID);

        //*Compares button ID with answerPosition
        if (view.getId() == checkID) {
            correctButton.setTextColor(getResources().getColor(R.color.text_highlight));
            correctButton.setBackgroundColor(getResources().getColor(R.color.button_correct));
            currentScore++;                         //*Question answered correctly ;)
        }else {
            //*Changes colour of correct button
            correctButton.setTextColor(getResources().getColor(R.color.text_highlight));
            correctButton.setBackgroundColor(getResources().getColor(R.color.button_correct));

            wrongID = view.getId();                 //*Assign ID of current button

            //*Assigns button to change colour (incorrect answer)
            RadioButton wrongButton = (RadioButton)this.findViewById(wrongID);
            wrongButton.setTextColor(getResources().getColor(R.color.text_highlight));
            wrongButton.setBackgroundColor(getResources().getColor(R.color.button_wrong));
        }
        disableRadio();                             //*Prevents guessing
    }

    /**
     * Method used to advance to the next question/answers set for the radioButtons
     * Total of 8 questions displayed
     *
     * @param view facilitates setting the button's attributes
     */
    public void nextRadio (View view) {

        if (checkBlanks(1)) {                   //*Did user select a radioButton?
            return;
        }else {

            //*If 8 questions answered, disable button, force user to scroll down
            if (questionsTotal == 8) {
                Button nextButton = (Button)this.findViewById(R.id.buttonNext1);
                nextButton.setTextColor(getResources().getColor(R.color.text_highlight));
                nextButton.setBackgroundColor(getResources().getColor(R.color.button_wrong));
                nextButton.setEnabled(false);
                showQuestion(2);                //*Toggle 2 second question textView
                showAnswers(2);                 //*Toggle 2 for checkBoxes
                displayScore();                        //*Displays score update with Toast
            } else {
                showQuestion(1);                //*Display next question in sequence
                resetRadio();                          //*Clears radioButtons
                showAnswers(1);                 //*Loads answer and decoys
            }
        }
    }

    /**
     * Method used to check if a single checkBox pressed
     * User must select "incorrect" options: if the "answer" is selected, score is not incremented
     *
     * @param view facilitates retrieving the ID of the pressed radioButton
     */
    public void checkSingleCB (View view) {

        String checkName;                               //*Holds checkBox name
        int checkID;                                    //*Holds checkBox ID

        checkName = "check" + answerPosition;

        checkID = getResources().getIdentifier(checkName, "id", getPackageName());
        CheckBox tempCB = (CheckBox)this.findViewById(checkID);

        if (view.getId() == checkID) {                  //*Question wrongly answered

            disableCB();
            tempCB.setTextColor(getResources().getColor(R.color.text_highlight));
            tempCB.setBackgroundColor(getResources().getColor(R.color.button_wrong));

            Button button = (Button)findViewById(R.id.buttonNext2);
            button.setEnabled(false);

            showQuestion(3);                    //*Question failed, advance to last question

        }else {
            //*Disable to prevent guessing, show correct option selected
            view.setEnabled(false);
            view.setBackgroundColor(getResources().getColor(R.color.button_correct));

        }

    }

    /**
     * Method used to check if the correct checkBoxes were selected
     * The user must select all the checkBoxes with "incorrect" options (3) to pass this question
     *
     * @param view facilitates retrieving the ID of the pressed checkBoxes
     */
    public void checkCBAnswers(View view) {

        String checkName;
        String tempName;
        int checkID;
        int tempID;
        boolean correct;

        if (checkBlanks(2)){                       //*Did the user answer the question?
            return;
        }
        correct = true;

        checkName = "check" + answerPosition;
        checkID = getResources().getIdentifier(checkName, "id", getPackageName());
        CheckBox checkBox = (CheckBox)findViewById(checkID);

        for (int marker = 1; marker <=4; marker++) {
            if (marker == answerPosition && checkBox.isChecked()) {
                correct = false;                         //*Wrong option selected
            }else if (marker == answerPosition && !checkBox.isChecked()){
                break;                                  //*Correct state
            }else {                                     //*Check remaining checkBoxes
                tempName = "check" + marker;
                tempID = getResources().getIdentifier(tempName, "id", getPackageName());
                CheckBox tempCB = (CheckBox)findViewById(tempID);
                if (!tempCB.isChecked()){               //*"Answer" missed, thus incorrect
                    correct = false;
                }
            }
        }

        if (correct) {                                  //*Correctly answered
            currentScore++;
        }

        Button button = (Button)findViewById(R.id.buttonNext2);
        button.setEnabled(false);
        showQuestion(3);

        displayScore();                                 //*Displays score update with Toast
    }

    /**
     * Method used to check for correct answer from editText
     * User types answer to last question into editText
     *
     * @param view facilitates checking text of editText
     */
    public void checkText (View view){

        String tempName;                                    //*Holds editText name
        String answerText;                                  //*Holds text from editText
        int answerID;                                       //*Holds editText ID

        EditText eText = (EditText)findViewById(R.id.text_answer);
        tempName = eText.getText().toString();
        tempName = tempName.toUpperCase();                  //*toUpperCase for validation purposes

        if (checkBlanks(3)) {                       //*Did the user answer the question?
            return;
        }

        answerText = "answer" + questionsAsked[currentQuestion];
        answerID = MainActivity.this.getResources()
                .getIdentifier(answerText, "string", MainActivity.this.getPackageName());
        answerText = MainActivity.this.getResources().getString(answerID);
        answerText = answerText.toUpperCase();              //*toUpperCase for validation purposes

        if (tempName.matches(answerText)) {                 //*Answered correctly
            currentScore++;
        }else {                                             //*Answered incorrectly, display answer
            tempName = tempName.toLowerCase();
            answerText = answerText.toLowerCase();
            eText.setText(tempName + " " + answerText);
            eText.setBackgroundColor(getResources().getColor(R.color.button_wrong));
            eText.setTextColor(getResources().getColor(R.color.text_main));
        }
        dismissKeyboard(this);                      //*Get rid of on-screen keyboard
        displayScore();                                    //*Final question, display score
    }

    /**
     * Method used to check for unanswered/blank questions
     * Displays message prompting user to select option
     *
     * @param toggle allows for 1:RadioButtons, 2:CheckBoxes, 3:EditText switching
     *
     * @return state of skipped flag
     */
    public boolean checkBlanks (int toggle) {

        String tempName;                            //*Holds name of widget
        int tempID;                                 //*Holds ID of widget
        int skippedToggle = 0;                      //*Determines message displayed
        boolean skipped = false;                    //*Flag to show question unanswered

        if (toggle == 3) {                          //*Check editText
            EditText view = (EditText)findViewById(R.id.text_answer);
            tempName = view.getText().toString();
            if (tempName.matches("")) {
                skipped = true;
                skippedToggle = 3;
            }
        }else {   //*Cycles four times, checking either radio/CB, as indicated by the toggle
            for (int marker = 1; marker <=4; marker++) {
                if (toggle == 1) {

                    tempName = "radio" + marker;
                    tempID = getResources().getIdentifier(tempName, "id", getPackageName());
                    RadioButton tempRadio = (RadioButton) this.findViewById(tempID);

                    if (tempRadio.isChecked()) {
                        return false;
                    }else {
                        skipped = true;
                        skippedToggle = 1;
                    }
                } else if (toggle == 2) {
                    //*Check all checkBoxes except correct answer, which must remain empty
                    if (marker != answerPosition) {
                        tempName = "check" + marker;
                        tempID = getResources().getIdentifier(tempName, "id", getPackageName());
                        CheckBox tempCB = (CheckBox) this.findViewById(tempID);

                        if (!tempCB.isChecked()) {
                            skipped = true;
                            skippedToggle = 2;
                        }
                    }
                }
            }
        }
        switch (skippedToggle) {                            //*Displays relevant messages
            case 1:
                Toast.makeText(getApplicationContext(),
                        "Please select the correct answer to continue", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Toast.makeText(getApplicationContext(),
                        "Please select the incorrect answers to continue", Toast.LENGTH_SHORT).show();
                break;
            case 3:
                Toast.makeText(getApplicationContext(),
                        "Please type the correct answer to continue", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return skipped;
    }

    /**
     * Method disables radioButtons
     */
    private void disableRadio () {

        String radioName;                                          //*Holds radioButton name
        int radioID;                                               //*Holds radioButton ID

        for (int marker = 1; marker <= 4; marker++) {

            radioName = "radio" + marker;

            radioID = getResources().getIdentifier(radioName, "id", getPackageName());
            RadioButton radio = (RadioButton)this.findViewById(radioID);
            radio.setEnabled(false);
        }
    }

    /**
     * Method disables checkBoxes
     */
    private void disableCB () {

        String checkBoxName;                                    //*Holds checkBox name
        int checkBoxID;                                         //*Holds checkBox ID

        for (int marker = 1; marker <= 4; marker++) {

            checkBoxName = "check" + marker;

            checkBoxID = getResources().getIdentifier(checkBoxName, "id", getPackageName());
            CheckBox checkBox = (CheckBox)this.findViewById(checkBoxID);
            checkBox.setEnabled(false);
        }
    }

    /**
     * Method used to clear text values and colours from all radioButtons before displaying
     * next question and options
     */
    public void resetRadio() {

        String radioName;                                   //*Holds radioButtons name
        int radioID;                                        //*Holds radioButtons ID

        for (int marker = 1; marker <= 4; marker++) {       //*Resets radioButtons

            radioName = "radio" + marker;

            radioID = getResources().getIdentifier(radioName, "id", getPackageName());
            RadioButton radio = (RadioButton)this.findViewById(radioID);

            radio.setEnabled(true);
            radio.setChecked(false);
            radio.setTextColor(getResources().getColor(R.color.text_main));
            radio.setBackgroundColor(Color.TRANSPARENT);
        }
        //*Enables button, resets text and background
        Button next1 = (Button)findViewById(R.id.buttonNext1);
        next1.setEnabled(true);
        next1.setTextColor(getResources().getColor(R.color.text_main));
        next1.setBackgroundColor(getResources().getColor(R.color.button_background));
    }

    /**
     * Method used to clear text values and colours from all checkBoxes before displaying
     * next question and options
     */
    private void resetCB() {

        String checkBoxName;                                    //*Holds checkBox name
        int checkBoxID;                                         //*Holds checkBox ID

        for (int marker = 1; marker <= 4; marker++) {

            checkBoxName = "check" + marker;

            checkBoxID = getResources().getIdentifier(checkBoxName, "id", getPackageName());
            CheckBox checkBox = (CheckBox)this.findViewById(checkBoxID);

            checkBox.setEnabled(true);
            checkBox.setText("");
            checkBox.setTextColor(getResources().getColor(R.color.text_main));
            checkBox.setBackgroundColor(Color.TRANSPARENT);
            checkBox.setChecked(false);
        }
        //*Resets button
        Button next2 = (Button)findViewById(R.id.buttonNext2);
        next2.setEnabled(true);
        next2.setTextColor(getResources().getColor(R.color.text_main));
        next2.setBackgroundColor(getResources().getColor(R.color.button_background));
    }

    /**
     * Overarching method called to clear quiz for next round
     * Calls resetRadio() and resetCheck()
     * Clears editText, question texts, score, global variables and arrays
     * Calls showQuestion(1) and showAnswers(1) to initiate the next round
     *
     * @param view facilitate resetting views
     */
    public void resetAll (View view) {

        resetRadio();                                       //*Clears radioButton
        resetCB();                                          //*Clears checkBoxes

        //*Clears and resets editText
        EditText eText = (EditText)findViewById(R.id.text_answer);
        eText.setText(getString(R.string.blank));
        eText.setBackgroundColor(Color.TRANSPARENT);
        eText.setTextColor(getResources().getColor(R.color.text_main));

        Button submit = (Button)findViewById(R.id.buttonSubmit);        //*Enables submit button
        submit.setEnabled(true);

        //*As this method calls showQuestion later, question 1 doesn't have to be cleared
        TextView textView = (TextView)findViewById(R.id.question2);     //*Clears question 2
        textView.setText(R.string.question_here);

        TextView textView2 = (TextView)findViewById(R.id.question3);    //*Clears question 2
        textView2.setText(R.string.question_here);

        TextView textView3 = (TextView)findViewById(R.id.score_text);   //*Clears score
        textView3.setText(R.string.score_here);

        //*Reinitialize global variables
        currentScore = 0;
        questionsTotal = 0;
        answerPosition = 0;
        currentQuestion = 0;

        //*Construct new question and answer arrays
        questionsAsked = newQList();
        shuffleLast(questionsAsked);
        answersArray = questionsAsked;

        //*Show question and options for first part (radioButtons) of next round
        showQuestion(1);
        showAnswers(1);
    }

    /**
     * Method displays score with both Toast and textView
     */
    private void displayScore (){

        String message;                                         //*Holds score message

        message = "Your score is " + currentScore + " out of 10.";
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        TextView view = (TextView) findViewById(R.id.score_text);
        view.setText(message);
    }

    /**
     * Method programatically removes on-screen keyboard from view
     *
     * @param activity facilitates reference to InputMethodManager and CurrentFocus
     */
    public void dismissKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null != activity.getCurrentFocus())
            imm.hideSoftInputFromWindow(activity.getCurrentFocus()
                    .getApplicationWindowToken(), 0);
    }

    /**
     * Method used to generate random number, to randomize question/answer arrays
     *
     * @param min is the minimum possible value
     * @param max is the maximum possible value
     *
     * @return the generated number
     */
    private int randomNum(int min, int max){

        int result;

        result = ThreadLocalRandom.current().nextInt(min, max + 1);;
        return(result);
    }

    /**
     * Method generates random array of questions and corresponding array of answers
     * to prevent a question from being repeated in a session
     * Also prevents a decoy option from repeating in a single question's options
     *
     * @param toggle allows for 1:(question array) and 2:(decoy array) generation
     */
    private void checkDuplicates (int toggle) {

        int number;
        boolean duplicate;

        if (toggle == 1) {                              //*Builds non-repeating random array
            questionsAsked = newQList();
            shuffleLast(questionsAsked);
            answersArray = questionsAsked;
        }else {
            /*Generates non-repeating decoy array. Decoys combined with answer,
                forming options for current question*/
            decoysUsed = newDList();

            for (int i = 0; i <= 2; i++) {
                duplicate = false;
                number = randomNum(1, 50);

                if (number != questionsAsked[currentQuestion]) {        //*Checks for duplicates
                    switch (i) {
                        case 0:
                            if (number == decoysUsed[i]) {
                                duplicate = true;
                            }else {
                                decoysUsed[i] = number;
                            }
                            break;
                        case 1:
                            if (number == decoysUsed[i]) {
                                duplicate = true;
                            }else {
                                decoysUsed[i] = number;
                            }
                            break;
                        case 2:
                            if (number == decoysUsed[i]) {
                                duplicate = true;
                            }else {
                                decoysUsed[i] = number;
                            }
                            break;
                    }
                    if (!duplicate) {
                        decoysUsed[i] = number;
                    }
                }
            }
        }
    }

    /**
     * Method generates sequential array (1-50), used as basis for random array
     *
     * @return the generated array
     */
    private int[] newQList() {
        int[] questions = new int[SIZE];
        for (int marker = 0; marker < questions.length; marker++) {
            questions[marker] = marker + 1;
        }
        return questions;
    }

    /**
     * Method generates random array index, retrieves the corresponding value from the initial
     * array, then swaps it (by calling arrSwap(swapArray, swapArray - 1 - i, k) with the value
     * in the last position
     * Random range is decreased by 1
     * Process repeats until all values have been shuffled
     *
     * @param swapArray is the array to be shuffled
     */
    private void shuffleLast (int[] swapArray) {

        int l = 0;

        for (int i = 0; i < swapArray.length; ++i) {
            int k = randomNum(1, swapArray.length - 1);
            l = k;
            arrSwap(swapArray, swapArray.length - 1 - i, k);
        }
    }

    /**
     * Method used to exchange array values
     *
     * @param questions is the relevant array
     * @param i is the first value for the swap
     * @param j is the second value for the swap
     */
    private void arrSwap(int[] questions, int i, int j) {
        int tmp = questions[i];
        questions[i] = questions[j];
        questions[j] = tmp;
    }

    /**
     * Method generates initial decoy array to be checked and shuffled
     *
     * @return the generates decoy array
     */
    private int[] newDList() {
        int[] decoys = new int[3];
        for (int marker = 0; marker <= 2; marker++) {
            if (questionsAsked[currentQuestion] != 1) {
                decoys[marker] = 1;
                Log.d(" arrDecoys", String.valueOf(decoys[marker]));
            }
        }
        return decoys;
    }

}