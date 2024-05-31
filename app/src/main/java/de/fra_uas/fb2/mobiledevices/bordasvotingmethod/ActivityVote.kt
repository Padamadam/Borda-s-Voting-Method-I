package de.fra_uas.fb2.mobiledevices.bordasvotingmethod

import android.content.Intent
import android.os.Bundle
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

        val b: Bundle? = intent.extras
        val votingOptInput: Array<out String>? = b?.getStringArray("votingOpts")
        val votingOptNum: Int? = b?.getString("votingOptNum")?.toInt()

        // DEBUG TOOL - TO BE REMOVED
        val toast = Toast.makeText(this, votingOptInput?.get(0), Toast.LENGTH_SHORT)
        toast.show()

        if (votingOptNum != null) {
            generateSeekBars(votingOptInput, votingOptNum, sliderContainer)
        }

        val cancelButton: Button = findViewById(R.id.cancelButton)
        cancelButton.setOnClickListener{
            finish()
        }

        val confirmVoteButton: Button = findViewById(R.id.confirmVoteButton)
        confirmVoteButton.setOnClickListener {
            if (isArrayUnique(bordaPoints.values.toIntArray())) {
                val data = Intent()
                data.putExtra("votingResultsKeys", bordaPoints.keys.toTypedArray())
                data.putExtra("votingResultsValues", bordaPoints.values.toIntArray())
                setResult(RESULT_OK, data)
                finish()

            }else{
                val notUniqueTxt = Toast.makeText(this, getString(R.string.not_unique_toast), Toast.LENGTH_SHORT)
                notUniqueTxt.show()
            }
        }
    }

    private fun getSeekbarValues(sliderContainer: LinearLayout, votingOptNum: Int): HashMap<String, Int> {
        val votingOptionsMap = HashMap<String, Int>()

        for (i in 0..<votingOptNum) {
            val optionLabel = sliderContainer.getChildAt(i * 2) as TextView
            val seekBar = sliderContainer.getChildAt(i * 2 + 1) as SeekBar
            votingOptionsMap[optionLabel.text.toString()] = seekBar.progress
        }

        // Ascending order
        val sortedEntries = votingOptionsMap.toList().sortedBy{ (_, value) -> value}.toMap()

        val labels = sortedEntries.keys
        val sliderScore = sortedEntries.values
        val duplicates = getDuplicates(sliderScore)

        for(i in 0..<votingOptNum){
            if(!duplicates.contains(sliderScore.elementAt(i))){
                bordaPoints[labels.elementAt(i)] = i
            }else {
                bordaPoints[labels.elementAt(i)] = -1   // -1 means the value is being repeated
            }
        }
        return bordaPoints
    }

    private fun isArrayUnique(array: IntArray): Boolean {
        return array.toSet().size == array.size
    }

    private fun generateSeekBars(splitVotingInput: Array<out String>?, votingOptNum: Int, sliderContainer: LinearLayout) {
        if (splitVotingInput != null) {
            if (splitVotingInput.isNotEmpty()) {
                for(i in 0..<votingOptNum){
                    val optionLabel = TextView(this)
                    if(i+1 <= splitVotingInput.size) {
                        optionLabel.text = splitVotingInput.get(i)
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
                            // Handle progress change (optional)
                            bordaPoints = getSeekbarValues(sliderContainer, votingOptNum)
                            printResults(votingOptNum)
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

    private fun printResults(votingOptNum: Int){
        val resultsText = findViewById<TextView>(R.id.votePoints)
        var message = ""
        for(i in 0..<votingOptNum) {
            message = if (bordaPoints.values.elementAt(i) == -1) {
                message + bordaPoints.keys.elementAt(i).toString() +
                        getString(R.string.not_unique) + "\n"
            } else {
                message + bordaPoints.keys.elementAt(i).toString() + " --> " +
                        bordaPoints.values.elementAt(i) + "\n"
            }
        }
        resultsText.text = message
        resultsText.setPadding(8, 0, 0, 0)
    }
}