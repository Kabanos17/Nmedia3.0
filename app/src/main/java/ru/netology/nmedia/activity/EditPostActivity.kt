package ru.netology.nmedia.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContract
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.ActivityEditPostBinding

class EditPostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityEditPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        val text = intent.getStringExtra(Contract.EXTRA_TEXT)
        if (text != null) {
            binding.content.setText(text)
        }
        binding.content.requestFocus()

        binding.save.setOnClickListener {
            if (binding.content.text.isNullOrBlank()) {
                setResult(Activity.RESULT_CANCELED)
            } else {
                val result = Intent().putExtra(Contract.EXTRA_TEXT, binding.content.text.toString())
                setResult(Activity.RESULT_OK, result)
            }
            finish()
        }
    }

    companion object {
        object Contract : ActivityResultContract<String?, String?>() {
            const val EXTRA_TEXT = "EXTRA_TEXT"

            override fun createIntent(context: Context, input: String?): Intent {
                val intent = Intent(context, EditPostActivity::class.java)
                intent.putExtra(EXTRA_TEXT, input)
                return intent
            }

            override fun parseResult(resultCode: Int, intent: Intent?): String? {
                return if (resultCode == Activity.RESULT_OK) {
                    intent?.getStringExtra(EXTRA_TEXT)
                } else {
                    null
                }
            }
        }
    }
}