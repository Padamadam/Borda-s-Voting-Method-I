package de.fra_uas.fb2.mobiledevices.bordasvotingmethod

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val votingOpt = findViewById<EditText>(R.id.editVotingOpt)

        if (savedInstanceState != null) {
            val votingOptText = savedInstanceState.getString("votingOpt")
            votingOpt.setText(votingOptText)
        }



        val addVoteButton: Button = findViewById<Button>(R.id.addVoteButton)



        addVoteButton.setOnClickListener{
            val intent = Intent(this, ActivityVote::class.java)

            val votingOptInput = votingOpt.text.toString()
            val splitVotingInput: Array<String> = splitAndUppercase(votingOptInput)
            val toast = Toast.makeText(this, splitVotingInput[0], Toast.LENGTH_SHORT)
            toast.show()

            val sharedPref = getSharedPreferences("votingPref", MODE_PRIVATE)
            with(sharedPref.edit()){
                putString("votingOpt", votingOptInput)
                apply()
            }
            startActivity(intent)
        }
    }

    fun splitAndUppercase(input: String): Array<String> {
        return input.split(",")
            .map { word ->
                word.trim().replaceFirstChar { it.uppercase() }
            }
            .toTypedArray()
    }


    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("message", "This is my message to be reloaded");
        super.onSaveInstanceState(outState);
    }

}