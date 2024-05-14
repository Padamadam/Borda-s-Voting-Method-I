package de.fra_uas.fb2.mobiledevices.bordasvotingmethod

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
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

        val cancelButton: Button = findViewById<Button>(R.id.cancelButton);
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
}