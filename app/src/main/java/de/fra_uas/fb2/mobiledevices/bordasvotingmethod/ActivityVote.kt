package de.fra_uas.fb2.mobiledevices.bordasvotingmethod

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ActivityVote : AppCompatActivity() {

    private var bordaPoints = HashMap<String, Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_vote)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sliderContainer: LinearLayout = findViewById(R.id.sliderContainer)
        val cancelButton: Button = findViewById(R.id.cancelButton)
        val confirmVoteButton: Button = findViewById(R.id.confirmVoteButton)

        val b: Bundle? = intent.extras
        val votingOptInput: Array<out String>? = b?.getStringArray("votingOpts")
        val votingOptNum: Int? = b?.getString("votingOptNum")?.toInt()

        if (votingOptNum != null) {
            generateSeekBars(votingOptInput, votingOptNum, sliderContainer)
        }

        cancelButton.setOnClickListener{
            Toast.makeText(this, getString(R.string.vote_cancelled), Toast.LENGTH_SHORT).show()
            finish()
        }

        confirmVoteButton.setOnClickListener {
            if (isArrayUnique(bordaPoints.values.toIntArray())) {
                val data = Intent()

                val sortedBordaPoints = bordaPoints.entries.sortedByDescending { it.value }.toMutableList()
                val sortedKeys = sortedBordaPoints.map { it.key }.toTypedArray()
                val sortedValues = sortedBordaPoints.map { it.value }.toIntArray()

                data.putExtra("votingResultsKeys", sortedKeys)
                data.putExtra("votingResultsValues", sortedValues)
                setResult(RESULT_OK, data)
                finish()

            }else{
                val notUniqueTxt = Toast.makeText(this, getString(R.string.not_unique_toast), Toast.LENGTH_SHORT)
                notUniqueTxt.show()
            }
        }
    }

    private fun getSeekbarValues(sliderContainer: LinearLayout, votingOptNum: Int){
        val votingOptionsMap = HashMap<String, Int>()

        for (i in 0..<votingOptNum) {
            val optionLabel = sliderContainer.getChildAt(i * 2) as TextView
            val seekBar = sliderContainer.getChildAt(i * 2 + 1) as SeekBar
            votingOptionsMap[optionLabel.text.toString()] = seekBar.progress
        }

        // Descending order
        val sortedEntries = votingOptionsMap.entries.sortedByDescending { it.value }.toMutableList()

        val duplicates = getDuplicates(sortedEntries.map { it.value })

        for (i in sortedEntries.indices) {
            val entry = sortedEntries[i]
            val key = entry.key
            val value = entry.value

            if (!duplicates.contains(value)) {
                bordaPoints[key] = i
            } else {
                bordaPoints[key] = -1
            }
        }
    }

    private fun isArrayUnique(array: IntArray): Boolean {
        return array.toSet().size == array.size
    }

    private fun generateSeekBars(splitVotingInput: Array<out String>?, votingOptNum: Int, sliderContainer: LinearLayout) {
        if (splitVotingInput != null) {
            if (splitVotingInput.isNotEmpty()) {
                for(i in 0..<votingOptNum){
                    val optionLabel = TextView(this)
                    // access 0 in order to prevent out of bound access
                    if(splitVotingInput[0].isEmpty() && i == 0){
                        optionLabel.text = getString(R.string.option) + " 0"
                    }else if(i+1 <= splitVotingInput.size) {
                        optionLabel.text = splitVotingInput[i]
                    }else{
                        val message = getString(R.string.option) + " " + (i+1).toString()
                        optionLabel.text = message
                    }

                    optionLabel.textSize = 16f
                    optionLabel.setPadding(8, 0, 0, 0)

                    val seekBar = SeekBar(this)
                    seekBar.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )

                    // Add OnSeekBarChangeListener to collect seek bar value
                    seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                            // update bordaPoints map
                            getSeekbarValues(sliderContainer, votingOptNum)
                            printResults()
                        }

                        override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                    })
                    sliderContainer.addView(optionLabel)
                    sliderContainer.addView(seekBar)
                }
            }
        }
    }

    private fun getDuplicates(sliderScore: Collection<Int>): MutableList<Int> {
        val valueCounts = HashMap<Int, Int>()
        val repeatedValues = mutableListOf<Int>()

        for (value in sliderScore) {
            val currentCount = valueCounts.getOrDefault(value, 0) // Get existing count (0 if not present)
            valueCounts[value] = currentCount + 1 // Update count

            if (currentCount > 0) { // Check if this is the second or more occurrence
                repeatedValues.add(value)
            }
        }
        return repeatedValues
    }

    private fun printResults() {
        val resultsText = findViewById<TextView>(R.id.votePoints)
        var message = ""

        val sortedBordaPoints = bordaPoints.entries.sortedByDescending { it.value }.toMutableList()

        for (entry in sortedBordaPoints) {
            val key = entry.key
            val value = entry.value
            message += if (value == -1) {
                "$key ${getString(R.string.not_unique)}\n"
            } else {
                "$key --> $value\n"
            }
        }
        resultsText.text = message
        resultsText.setPadding(8, 0, 0, 0)
        resultsText.setGravity(Gravity.CENTER_HORIZONTAL)
    }
}