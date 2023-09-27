package com.example.w1820984_dicegame

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random


class MainActivity2 : AppCompatActivity() {
    private lateinit var backButton: Button

    //Score record
    private var targetScore: Int = 101
    private lateinit var scoreRecord: TextView
    private var playerRecord: Int = 0
    private var aiRecord: Int = 0
    private lateinit var prefs: SharedPreferences

    //AI dices
    private lateinit var aiDiceOne: ImageView
    private lateinit var aiDiceTwo: ImageView
    private lateinit var aiDiceThree: ImageView
    private lateinit var aiDiceFour: ImageView
    private lateinit var aiDiceFive: ImageView

    //Player dices
    private lateinit var playerDiceOne: ImageView
    private lateinit var playerDiceTwo: ImageView
    private lateinit var playerDiceThree: ImageView
    private lateinit var playerDiceFour: ImageView
    private lateinit var playerDiceFive: ImageView

    //Player dice select text
    private lateinit var playerSelectDiceOne: TextView
    private lateinit var playerSelectDiceTwo: TextView
    private lateinit var playerSelectDiceThree: TextView
    private lateinit var playerSelectDiceFour: TextView
    private lateinit var playerSelectDiceFive: TextView

    //Throw button, Score button, ReRoll button
    private lateinit var throwButton: Button
    private lateinit var scoreButton: Button
    private lateinit var reRollButton: Button

    //AI and player score text, and values
    private lateinit var aiScoreText: TextView
    private lateinit var playerScoreText: TextView
    private var aiScore = 0
    private var playerScore = 0

    private val aiSet = mutableListOf<Int>()
    private val playerSet = mutableListOf<Int>()
    private val imageMap = HashMap<Int, Int>()

    private lateinit var aiImagesSet: List<ImageView>
    private var aiReRollCounter = 0
    private lateinit var playerImagesSet : List<ImageView>

    private var scoreTie = false
    private var winner: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        //Get the extra value from MainActivity to set new target score if chosen by the player
        val selectedNumber = intent.getIntExtra("selectedNumber", 0)
        if (selectedNumber > 0) {
            targetScore = selectedNumber
        }

        ///Reference: https://stackoverflow.com/questions/62886482/how-do-i-save-variable-values-across-activities-in-android-studio
        // Update the score record TextView
        prefs = getSharedPreferences("myPrefs", MODE_PRIVATE)
        playerRecord = prefs.getInt("playerRecord", 0)
        aiRecord = prefs.getInt("aiRecord", 0)
        scoreRecord = findViewById(R.id.score_record)
        scoreRecord.text = "H:$playerRecord/C:$aiRecord"
        ///Reference: https://stackoverflow.com/questions/62886482/how-do-i-save-variable-values-across-activities-in-android-studio


        backButton = findViewById(R.id.back_button)

        aiDiceOne = findViewById(R.id.ai_dice_one)
        aiDiceTwo = findViewById(R.id.ai_dice_two)
        aiDiceThree = findViewById(R.id.ai_dice_three)
        aiDiceFour = findViewById(R.id.ai_dice_four)
        aiDiceFive = findViewById(R.id.ai_dice_five)

        playerDiceOne = findViewById(R.id.player_dice_one)
        playerDiceTwo = findViewById(R.id.player_dice_two)
        playerDiceThree = findViewById(R.id.player_dice_three)
        playerDiceFour = findViewById(R.id.player_dice_four)
        playerDiceFive = findViewById(R.id.player_dice_five)

        //Player select a dice
        playerSelectDiceOne = findViewById(R.id.dice_one_select)
        playerSelectDiceTwo = findViewById(R.id.dice_two_select)
        playerSelectDiceThree = findViewById(R.id.dice_three_select)
        playerSelectDiceFour = findViewById(R.id.dice_four_select)
        playerSelectDiceFive = findViewById(R.id.dice_five_select)

        playerSelectDiceOne.setText(" ")
        playerSelectDiceTwo.setText(" ")
        playerSelectDiceThree.setText(" ")
        playerSelectDiceFour.setText(" ")
        playerSelectDiceFive.setText(" ")


        imageMap[1] = R.drawable.dice_six_faces_one
        imageMap[2] = R.drawable.dice_six_faces_two
        imageMap[3] = R.drawable.dice_six_faces_three
        imageMap[4] = R.drawable.dice_six_faces_four
        imageMap[5] = R.drawable.dice_six_faces_five
        imageMap[6] = R.drawable.dice_six_faces_six


        //Initialize AI and player score text
        aiScoreText = findViewById(R.id.ai_score_text)
        playerScoreText = findViewById(R.id.player_score_text)

        //Initialize throw, score, and reroll buttons
        throwButton = findViewById(R.id.throwButton)
        scoreButton = findViewById(R.id.scoreButton)
        reRollButton = findViewById(R.id.rerollButton)

        //Hide score and reroll buttons
        scoreButton.visibility = INVISIBLE
        scoreButton.visibility = GONE
        reRollButton.visibility = INVISIBLE
        reRollButton.visibility = GONE


        //Click throw button
        throwButton.setOnClickListener {
            if (!scoreTie) {
                throwButton.visibility = INVISIBLE
                throwButton.visibility = GONE

                scoreButton.visibility = VISIBLE
                reRollButton.visibility = VISIBLE

                aiThrow()
                playerThrow()
            } else {
                aiThrow()
                playerThrow()

                settleTieScore()
            }
        }

        //Click back button
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }


    ///Reference: https://stackoverflow.com/questions/20700565/how-to-use-onsaveinstancestate
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt("targetScore", targetScore)
        outState.putInt("playerRecord", playerRecord)
        outState.putInt("aiRecord", aiRecord)
        outState.putInt("aiScore", aiScore)
        outState.putInt("playerScore", playerScore)
        outState.putBoolean("scoreTie", scoreTie)
        outState.putBoolean("winner", winner)

        // Save player selected dices
        outState.putString("playerSelectDiceOne", playerSelectDiceOne.text.toString())
        outState.putString("playerSelectDiceTwo", playerSelectDiceTwo.text.toString())
        outState.putString("playerSelectDiceThree", playerSelectDiceThree.text.toString())
        outState.putString("playerSelectDiceFour", playerSelectDiceFour.text.toString())
        outState.putString("playerSelectDiceFive", playerSelectDiceFive.text.toString())

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        targetScore = savedInstanceState.getInt("targetScore")
        playerRecord = savedInstanceState.getInt("playerRecord")
        aiRecord = savedInstanceState.getInt("aiRecord")
        aiScore = savedInstanceState.getInt("aiScore")
        playerScore = savedInstanceState.getInt("playerScore")
        scoreTie = savedInstanceState.getBoolean("scoreTie")
        winner = savedInstanceState.getBoolean("winner")

        // Restore player selected dices
        playerSelectDiceOne.text = savedInstanceState.getString("playerSelectDiceOne")
        playerSelectDiceTwo.text = savedInstanceState.getString("playerSelectDiceTwo")
        playerSelectDiceThree.text = savedInstanceState.getString("playerSelectDiceThree")
        playerSelectDiceFour.text = savedInstanceState.getString("playerSelectDiceFour")
    }
    ///Reference: https://stackoverflow.com/questions/20700565/how-to-use-onsaveinstancestate



    private fun aiThrow() {
        aiImagesSet = listOf(aiDiceOne, aiDiceTwo, aiDiceThree, aiDiceFour, aiDiceFive)

        for ((index) in aiImagesSet.withIndex()) {
            val randomIndex = (1..6).random()
            val imageResourceId = imageMap[randomIndex]
            aiSet.add(randomIndex)
            aiImagesSet[index].setImageResource(imageResourceId!!)
        }
    }

    private fun aiReRoll(aiImagesSet: List<ImageView>) {
        //If AI decides to reroll is true
        val aiSetValueToReRoll = mutableListOf<Int>()

        // Determine which dice to reroll based on their current value
        for ((index, value) in aiSet.withIndex()) {
            if (value < 4) { // reroll any dice with a value less than 4
                aiSetValueToReRoll.add(index)
            }
        }

        // Reroll the selected dice
        for ((indexToRemove) in aiSetValueToReRoll.withIndex()) {
            val newRandomIndex = (1..6).random()
            val newImageResourceId = imageMap[newRandomIndex]

            val selectedIndex = aiSetValueToReRoll[indexToRemove]
            aiSet[selectedIndex] = newRandomIndex
            aiImagesSet[selectedIndex].setImageResource(newImageResourceId!!)
        }

        aiReRollCounter++

    }


    private fun playerThrow() {
        playerImagesSet = listOf(playerDiceOne, playerDiceTwo, playerDiceThree, playerDiceFour, playerDiceFive)
        val playerSelectedImages = mutableListOf<Int>()

        for ((index) in playerImagesSet.withIndex()) {
            val randomIndex = (1..6).random()
            val imageResourceId = imageMap[randomIndex]
            playerSet.add(randomIndex)
            playerImagesSet[index].setImageResource(imageResourceId!!)
        }

        //Toggle first selection, otherwise the user would have to click twice for to select the dice
        for (imageView in playerImagesSet) {
            imageView.isSelected = true

            imageView.setOnClickListener {
                // Retrieve the selected image and store its value in the list
                val index = playerImagesSet.indexOf(imageView)
                if (imageView.isSelected) {
                    playerSelectedImages.add(index)
                    playerSelectText(true, index + 1)
                } else {
                    playerSelectedImages.remove(index)
                    playerSelectText(false, index + 1)
                }
                imageView.isSelected = !imageView.isSelected
            }
        }


        //Click ReRoll button
        var reRoll = 0
        reRollButton.setOnClickListener {
            //AI decision to reroll
            val aiReRoll = Random.nextBoolean()
            if (aiReRoll && aiReRollCounter != 2) {
                aiReRoll(aiImagesSet)
            }

            if (reRoll != 2) {
                for ((indexToRemove) in playerSelectedImages.withIndex()) {
                    val newRandomIndex = (1..6).random()
                    val newImageResourceId = imageMap[newRandomIndex]

                    val selectedIndex = playerSelectedImages[indexToRemove]
                    playerSet[selectedIndex] = newRandomIndex
                    playerImagesSet[selectedIndex].setImageResource(newImageResourceId!!)
                }
                reRoll++
            }

            if (reRoll == 2) {
                score()  //Update the scores

                if (!winner) { //Last reroll button pressed for winning, if true don't display buttons
                    //Show throw button
                    throwButton.visibility = VISIBLE
                    //Hide score and reroll buttons
                    scoreButton.visibility = INVISIBLE
                    scoreButton.visibility = GONE
                    reRollButton.visibility = INVISIBLE
                    reRollButton.visibility = GONE
                } else {
                    //Hide score and reroll buttons
                    scoreButton.visibility = INVISIBLE
                    scoreButton.visibility = GONE
                    reRollButton.visibility = INVISIBLE
                    reRollButton.visibility = GONE
                }
            }
        }


        //Click score button
        scoreButton.setOnClickListener {
            //Show throw button
            throwButton.visibility = VISIBLE
            //Hide score and reroll buttons
            scoreButton.visibility = INVISIBLE
            scoreButton.visibility = GONE
            reRollButton.visibility = INVISIBLE
            reRollButton.visibility = GONE

            score()
        }
    }


    private fun playerSelectText(checkSelection: Boolean, diceNumber: Int) {
        if (checkSelection) {
            if (diceNumber == 1) {
                playerSelectDiceOne.setText("Selected")
            } else if (diceNumber == 2) {
                playerSelectDiceTwo.setText("Selected")
            } else if (diceNumber == 3) {
                playerSelectDiceThree.setText("Selected")
            } else if (diceNumber == 4) {
                playerSelectDiceFour.setText("Selected")
            } else if (diceNumber == 5) {
                playerSelectDiceFive.setText("Selected")
            }
        } else {
            if (diceNumber == 1) {
                playerSelectDiceOne.setText(" ")
            } else if (diceNumber == 2) {
                playerSelectDiceTwo.setText(" ")
            } else if (diceNumber == 3) {
                playerSelectDiceThree.setText(" ")
            } else if (diceNumber == 4) {
                playerSelectDiceFour.setText(" ")
            } else if (diceNumber == 5) {
                playerSelectDiceFive.setText(" ")
            }
        }
    }

    private fun unSelectText() {
        playerSelectDiceOne.setText(" ")
        playerSelectDiceTwo.setText(" ")
        playerSelectDiceThree.setText(" ")
        playerSelectDiceFour.setText(" ")
        playerSelectDiceFive.setText(" ")
    }

    private fun score() {
        //Dices are not selectable till the throw button is clicked
        for (imageView in playerImagesSet)
            imageView.isClickable = false

        //AI decision to reroll, complete the rest of the ai's rerolls
        while (aiReRollCounter != 2) {
            val aiReRoll = Random.nextBoolean()
            if (aiReRoll) {
                aiReRoll(aiImagesSet)
            }
        }
        aiReRollCounter = 0
        unSelectText()

        //AI score
        for (value in aiSet) {
            aiScore += value
        }
        aiScoreText.setText("AI score: $aiScore")
        aiSet.clear()

        //Player score
        for (value in playerSet) {
            playerScore += value
        }
        playerScoreText.setText("Player score: $playerScore")
        playerSet.clear()


    ///Reference: https://developer.android.com/develop/ui/views/components/dialogs
        //Win conditions
        if (aiScore >= targetScore && playerScore >= targetScore && aiScore == playerScore) {
            showMessage("Tie", Color.GRAY)

            scoreTie = true
        } else if (aiScore >= targetScore && aiScore > playerScore) {
            showMessage("You lose", Color.RED)
            updateScoreRecord(false)

            winner = true
            scoreTie = false
            throwButton.visibility = INVISIBLE
            throwButton.visibility = GONE
        } else if (playerScore >= targetScore && aiScore < playerScore) {
            showMessage("You win!", Color.GREEN)
            updateScoreRecord(true)

            winner = true
            scoreTie = false
            throwButton.visibility = INVISIBLE
            throwButton.visibility = GONE
        }
    }

    private fun showMessage(message: String, color: Int) {
        val builder = AlertDialog.Builder(this)
        val messageView = TextView(this)
        messageView.text = message
        messageView.textSize = 24f
        messageView.gravity = Gravity.CENTER
        messageView.setTextColor(color)
        builder.setView(messageView)
        builder.setPositiveButton("OK") { _, _ ->}
        val dialog = builder.create()
        dialog.show()
    }
    ///Reference: https://developer.android.com/develop/ui/views/components/dialogs

    private fun settleTieScore() {
        //AI score
        for (value in aiSet) {
            aiScore += value
        }
        aiScoreText.setText("AI score: $aiScore")
        aiSet.clear()

        //Player score
        for (value in playerSet) {
            playerScore += value
        }
        playerScoreText.setText("Player score: $playerScore")
        playerSet.clear()

        //Dices are not selectable till the throw button is clicked
        for (imageView in playerImagesSet)
            imageView.isClickable = false

        if (aiScore > playerScore) {
            showMessage("You lose", Color.RED)
            updateScoreRecord(false)

            winner = true
            scoreTie = false
            throwButton.visibility = INVISIBLE
            throwButton.visibility = GONE
        } else if (aiScore < playerScore) {
            showMessage("You win!", Color.GREEN)
            updateScoreRecord(true)

            winner = true
            scoreTie = false
            throwButton.visibility = INVISIBLE
            throwButton.visibility = GONE
        }
    }


    ///Reference: https://stackoverflow.com/questions/62886482/how-do-i-save-variable-values-across-activities-in-android-studio
    //Update the total win record for both AI and player
    private fun updateScoreRecord(winner: Boolean) {
        val prefs = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val playerRecord = prefs.getInt("playerRecord", 0)
        val aiRecord = prefs.getInt("aiRecord", 0)
        val editor = prefs.edit()

        if (winner) {
            // Player wins
            editor.putInt("playerRecord", playerRecord + 1)
            scoreRecord.text = "H:${playerRecord + 1}/C:$aiRecord"
        } else {
            // AI wins
            editor.putInt("aiRecord", aiRecord + 1)
            scoreRecord.text = "H:$playerRecord/C:${aiRecord + 1}"
        }
        editor.apply()
    }
    ///Reference: https://stackoverflow.com/questions/62886482/how-do-i-save-variable-values-across-activities-in-android-studio
}