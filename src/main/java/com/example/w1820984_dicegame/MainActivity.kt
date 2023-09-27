package com.example.w1820984_dicegame

///---------------------------------------------------------
//LINK FOR VIDEO: https://drive.google.com/file/d/1A_-3nz8Toi3B83BXqN-Dq3y-N0r1YBzP/view?usp=share_link
///---------------------------------------------------------

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ///Reference: https://developer.android.com/develop/ui/views/components/dialogs
        val newGameButton = findViewById<Button>(R.id.button)
        newGameButton.setOnClickListener {
            //Create a dialog to confirm if the user wants to start a new game
            val newGameDialog = AlertDialog.Builder(this)
                .setTitle("New Game")
                .setMessage("Default target score is 101. Do you want a different target score for this game?")
                .setPositiveButton("Yes") { _, _ ->
                    //If the user clicks "Yes", create a dialog to select a number
                    val numberDialog = AlertDialog.Builder(this)
                        .setTitle("Select a target score between 200 and 900")
                        .setItems(Array(8) { ((it+2)*100).toString() }) { _, which ->
                        //Handle the selected number
                            val selectedNumber = (which + 2) * 100
                            //Start MainActivity2 and pass the selected number as an extra
                            val intent = Intent(this, MainActivity2::class.java)
                            intent.putExtra("selectedNumber", selectedNumber)
                            startActivity(intent)
                        }
                        .create()
                    numberDialog.show()
                }
                .setNegativeButton("No") { _, _ ->
                    // If the user clicks "No", call activity2 without passing a number as an extra
                    val intent = Intent(this, MainActivity2::class.java)
                    startActivity(intent)
                }
                .create()
            newGameDialog.show()
        }


        val aboutButton = findViewById<Button>(R.id.button2)
        aboutButton.setOnClickListener {
            val aboutDialog = AlertDialog.Builder(this)
                .setTitle("About")
                .setMessage("StudentID: w1820984 \n" +
                        "Name: Nipun Luca Muhudugama \n" + "\n" +
                        "I confirm that I understand what plagiarism is and have read and understood the section on Assessment Offences in the Essential Information for Students.  The work that I have submitted is entirely my own. Any work from other authors is duly referenced and acknowledged.")
                .setPositiveButton("CLOSE") { _, _ -> }
                .create()
            aboutDialog.show()
        }
        ///Reference: https://developer.android.com/develop/ui/views/components/dialogs
    }
}


