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

    private var seekBarValues: IntArray = IntArray(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_vote)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var sliderContainer: LinearLayout = findViewById<LinearLayout>(R.id.sliderContainer)

        val b: Bundle? = intent.extras
        val votingOptInput: String = b?.getString("votingOpts").toString()

        // Split the input into array
        val splitVotingInput: Array<String> = splitAndUppercase(votingOptInput)
        // DEBUG TOOL - TO BE REMOVED
        val toast = Toast.makeText(this, splitVotingInput[0], Toast.LENGTH_SHORT)
        toast.show()

        generateSeekBars(splitVotingInput)

        val cancelButton: Button = findViewById<Button>(R.id.cancelButton)
        cancelButton.setOnClickListener{
            finish()
        }

        val confirmVoteButton: Button = findViewById<Button>(R.id.confirmVoteButton)
        confirmVoteButton.setOnClickListener {

            getSeekbarValues(sliderContainer)

            if (isArrayUnique(seekBarValues)) {
                // get results
                val toast2 = Toast.makeText(this, seekBarValues[1].toString(), Toast.LENGTH_SHORT)
                toast2.show()


                val data: Intent = Intent()
                data.putExtra("votingResults", seekBarValues)
                setResult(RESULT_OK, data)
                finish()

            }else{
                val notUniqueTxt = Toast.makeText(this, "The votes are not unique!", Toast.LENGTH_SHORT)
                notUniqueTxt.show()
            }
        }
    }

    private fun getSeekbarValues(sliderContainer: LinearLayout) {
        seekBarValues = IntArray(sliderContainer.childCount / 2) // Assuming each pair is label-seekBar
        for (i in 0 until sliderContainer.childCount / 2) {
            val seekBar = sliderContainer.getChildAt(i * 2 + 1) as SeekBar
            seekBarValues[i] = seekBar.progress
        }
    }

    private fun isArrayUnique(array: IntArray): Boolean {
        return array.toSet().size == array.size
    }


    private fun splitAndUppercase(input: String): Array<String> {
        return input.split(",")
            .map { word ->
                word.trim().replaceFirstChar { it.uppercase() }
            }
            .toTypedArray()
    }

    private fun generateSeekBars(splitVotingInput: Array<String>) {
        val sliderContainer = findViewById<LinearLayout>(R.id.sliderContainer)
        if (splitVotingInput.isNotEmpty()) {
            for (votingOption in splitVotingInput) {
                val optionLabel = TextView(this)
                optionLabel.text = votingOption
                optionLabel.textSize = 20f

                val seekBar = SeekBar(this)
                seekBar.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

                // Add OnSeekBarChangeListener to collect seek bar value
                seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                        // Handle progress change (optional)
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