package de.fra_uas.fb2.mobiledevices.bordasvotingmethod

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.setPadding


class MainActivity : AppCompatActivity() {

    private lateinit var getResult: ActivityResultLauncher<Intent>
    private var totalVotingResult = HashMap<String, Int>()
    private var toVotingActivity = Bundle()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var voteCnt = 0
        val votingOpt = findViewById<EditText>(R.id.editVotingOpt)
        val votingOptNum = findViewById<EditText>(R.id.editOptNumber)
        val addVoteButton = findViewById<Button>(R.id.addVoteButton)
        val startOverButton = findViewById<Button>(R.id.startButton)
        val showVotingResults = findViewById<Switch>(R.id.showSwitch)
        val votesCntView = findViewById<TextView>(R.id.votesNumberView)
        val showScoreField = findViewById<TextView>(R.id.resultView)

        addVoteButton.setOnClickListener {
            val intent = Intent(this, ActivityVote::class.java)

            // Read new voting options input
            val votingOptInput = votingOpt.text.toString()

            // Check if input is empty (avoid empty string)
            if (votingOptInput.isEmpty()) {
                val toast = Toast.makeText(this, getString(R.string.no_voting_options), Toast.LENGTH_SHORT)
                toast.show()
            } else if (votingOptNum.text.toString().isEmpty()) {
                val toast =
                    Toast.makeText(this, getString(R.string.no_voting_opt_number), Toast.LENGTH_SHORT)
                toast.show()
            } else {
                val votingOptArray: Array<String> = splitAndUppercase(votingOptInput)
                toVotingActivity.putStringArray("votingOpts", votingOptArray)
                toVotingActivity.putString("votingOptNum", votingOptNum.text.toString())
                intent.putExtras(toVotingActivity)
                getResult.launch(intent)
            }
        }

        startOverButton.setOnClickListener{
            voteCnt = 0
            votesCntView.text = ""
        }



        getResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {

                val intent = result.data
                val b: Bundle? = intent?.extras
                val votingResultsKeys = b?.getStringArray("votingResultsKeys")
                val votingResultsValues = b?.getIntArray("votingResultsValues")

                voteCnt += 1
                votesCntView.text = voteCnt.toString()

                if (votingResultsKeys == null) {
                    Toast.makeText(this, "YOU GOT NULL RESULT", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, votingResultsKeys.elementAt(3), Toast.LENGTH_SHORT).show()
                }
            }
        }

        showVotingResults.setOnClickListener {
            val showResults = showVotingResults.isChecked
            if (showResults) {
                // Display existing total voting results if available
                displayTotalResults(showScoreField)
            } else {
                // Clear the result view if showVotingResults is off
                showScoreField.text = ""
            }
        }
    }

        private fun displayTotalResults(showScoreField: TextView) {
            if (totalVotingResult.isEmpty()) {
                showScoreField.text = getString(R.string.no_results_yet)
                showScoreField.setPadding(8, 8, 0, 0)
            } else {
                // Build a string to display results (consider formatting)
            }
        }

        private fun splitAndUppercase(input: String): Array<String> {
            return input.split(",")
                .map { word ->
                    word.trim().replaceFirstChar { it.uppercase() }
                }
                .toTypedArray()
        }

//        private fun updateTotalScore(obtainedVote: HashMap<String, Int>){
//
//        }
//        override fun onSaveInstanceState(outState: Bundle) {
//            outState.putString("message", "This is my message to be reloaded")
//            super.onSaveInstanceState(outState)
//        }
    }