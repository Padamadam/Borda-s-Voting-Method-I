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
//            if (votingOptInput.isEmpty()) {
//                val toast = Toast.makeText(this, getString(R.string.no_voting_options), Toast.LENGTH_SHORT)
//                toast.show()
//            } else
            if (votingOptNum.text.toString().isEmpty()) {
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
            // clear number of given votes
            voteCnt = 0
            votesCntView.text = voteCnt.toString()

            // clear voting options number
            votingOptNum.text.clear()

            // clear all voting options
            votingOpt.text.clear()

            // clear voting results
            totalVotingResult.clear()

            // update display
            displayTotalResults(showVotingResults, showScoreField)

        }



        getResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {

                val intent = result.data
                val b: Bundle? = intent?.extras
                val votingResultsKeys = b?.getStringArray("votingResultsKeys")
                val votingResultsValues = b?.getIntArray("votingResultsValues")

                if (votingResultsKeys != null && votingResultsValues != null &&
                    votingResultsKeys.size == votingResultsValues.size) {
                    val obtainedVote = HashMap<String, Int>()
                    for (i in votingResultsKeys.indices) {
                        obtainedVote[votingResultsKeys[i]] = votingResultsValues[i]
                    }

                    // Call updateTotalResults to process the obtained votes
                    updateTotalResults(obtainedVote)
                    voteCnt += 1
                    votesCntView.text = voteCnt.toString()

                    // if the showScoreField was previously checked, update the display
                    displayTotalResults(showVotingResults, showScoreField)

                } else {
                    Toast.makeText(this, "Invalid voting results data", Toast.LENGTH_SHORT).show()
                }
            }
        }

        showVotingResults.setOnClickListener {
            displayTotalResults(showVotingResults, showScoreField)
        }
    }

    private fun displayTotalResults(showVotingResults: Switch, showScoreField: TextView) {
        if(showVotingResults.isChecked) {
            var message = ""
            if (totalVotingResult.isEmpty()) {
                message = getString(R.string.no_results_yet)
            } else {
                val sortedBordaPoints = totalVotingResult.entries.sortedBy { it.value }.toMutableList()
                for (entry in sortedBordaPoints) {
                    val key = entry.key
                    val value = entry.value
                    message += if (value == -1) {
                        "$key ${getString(R.string.not_unique)}\n"
                    } else {
                        "$key --> $value\n"
                    }
                }
            }
            showScoreField.text = message
            showScoreField.setPadding(8, 8, 0, 0)
        }else{
            // Clear the result view if showVotingResults is off
            showScoreField.text = ""
        }
    }

    private fun splitAndUppercase(input: String): Array<String> {
        return input.split(",")
            .map { word ->
                word.trim().replaceFirstChar { it.uppercase() }
            }
            .toTypedArray()
    }

    private fun updateTotalResults(obtainedVote: HashMap<String, Int>) {
        for (candidate in obtainedVote.keys) {
            // Check if candidate already exists in totalVotingResult
            if (totalVotingResult.containsKey(candidate)) {
                // Add the new vote count to the existing count
                totalVotingResult[candidate] = totalVotingResult[candidate]!! + obtainedVote[candidate]!!
            } else {
                // Add the new candidate and vote count to totalVotingResult
                totalVotingResult[candidate] = obtainedVote[candidate]!!
            }
        }
    }
}