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


        addVoteButton.setOnClickListener {
            val intent = Intent(this, ActivityVote::class.java)

            // Read new voting options input
            val votingOptInput = votingOpt.text.toString()

            // Check if input is empty (avoid empty string)
            if (votingOptInput.isEmpty()) {
                val toast = Toast.makeText(this, "No voting options given!", Toast.LENGTH_SHORT)
                toast.show()
            } else {
                // Save given input (only if there's actual input)
                val sharedPref = getSharedPreferences("votingPref", MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putString("votingOption", votingOptInput)
                    apply()
                }

                intent.putExtra("votingOpts", votingOptInput)
                startActivity(intent)
            }
        }
    }

        override fun onSaveInstanceState(outState: Bundle) {
            outState.putString("message", "This is my message to be reloaded");
            super.onSaveInstanceState(outState);
        }
    }