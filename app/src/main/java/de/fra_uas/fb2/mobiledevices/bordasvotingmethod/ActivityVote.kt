package de.fra_uas.fb2.mobiledevices.bordasvotingmethod

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ActivityVote : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_vote)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val votingOptInput:String = intent.getStringExtra("votingOpts").toString()
        // Split the input into array
        val splitVotingInput: Array<String> = splitAndUppercase(votingOptInput)
        // DEBUG TOOL - TO BE REMOVED
        val toast = Toast.makeText(this, splitVotingInput[0], Toast.LENGTH_SHORT)
        toast.show()

        generateSeekBars(splitVotingInput)

        val cancelButton: Button = findViewById<Button>(R.id.cancelButton)
        cancelButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            retrieveView()
            startActivity(intent)
        }

        val confirmVoteButton: Button = findViewById<Button>(R.id.confirmVoteButton);
        confirmVoteButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            retrieveView()
            startActivity(intent)
        }

    }

    fun retrieveView(){
        val sharedPref = getSharedPreferences("votingPref", MODE_PRIVATE)
        val savedVotingOpt = sharedPref.getString("votingOption", "")
        findViewById<EditText>(R.id.editVotingOpt).setText(savedVotingOpt)
    }

    fun splitAndUppercase(input: String): Array<String> {
        return input.split(",")
            .map { word ->
                word.trim().replaceFirstChar { it.uppercase() }
            }
            .toTypedArray()
    }

    fun generateSeekBars(splitVotingInput: Array<String>){
        val sliderContainer = findViewById<LinearLayout>(R.id.sliderContainer)
        if(splitVotingInput.isNotEmpty()){
            for (votingOption in splitVotingInput) {
                val optionLabel = TextView(this)
                optionLabel.text = votingOption
                optionLabel.textSize = 20f

                val seekBar = SeekBar(this)
                seekBar.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

                sliderContainer.addView(optionLabel)
                sliderContainer.addView(seekBar)
            }
        }
    }

}