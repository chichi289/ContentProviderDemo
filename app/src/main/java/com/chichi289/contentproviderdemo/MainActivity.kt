package com.chichi289.contentproviderdemo

import android.content.ContentValues
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.chichi289.contentproviderdemo.provider.MyContentProvider

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val edtName = findViewById<EditText>(R.id.edtName)
        val edtQty = findViewById<EditText>(R.id.edtQty)
        val btnSave = findViewById<Button>(R.id.btnSave)

        btnSave.setOnClickListener {
            val name = edtName.text.toString().trim()
            val qty = edtQty.text.toString().trim()
            if (name.isEmpty()) {
                toast("Please enter product name")
            } else if (qty.isEmpty()) {
                toast("Please enter product quantity")
            } else {
                val uri =
                    contentResolver.insert(MyContentProvider.CONTENT_URI, ContentValues().apply {
                        put("product_name", name)
                        put("quantity", qty.toInt())
                    })
                toast("$uri")
            }
        }
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}