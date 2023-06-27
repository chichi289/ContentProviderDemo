package com.chichi289.clientapp

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {


    companion object {
        // defining authority so that other application can access it
        private const val AUTHORITY =
            "com.chichi289.contentproviderdemo"

        private const val PRODUCTS_TABLE = "products"

        // This is the URI that we will query against all
        private const val URI = "content://$AUTHORITY/$PRODUCTS_TABLE"

        // This is the URI that we will query against id
        //private const val URI = "content://$AUTHORITY/$PRODUCTS_TABLE/2"

        val CONTENT_URI: Uri = Uri.parse(URI)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val resultTextView = findViewById<TextView>(R.id.tvHelloWorld)

        val cursor = contentResolver.query(
            CONTENT_URI,
            null,
            null,
            null
        )

        if (cursor?.moveToFirst() == true) {
            val strBuild = StringBuilder()

            val idColumnIndex = cursor.getColumnIndex("id")
            val productNameColumnIndex = cursor.getColumnIndex("product_name")
            val quantityColumnIndex = cursor.getColumnIndex("quantity")

            while (!cursor.isAfterLast) {
                strBuild.append(
                    "${cursor.getString(idColumnIndex)}-${cursor.getString(productNameColumnIndex)}-${
                        cursor.getInt(
                            quantityColumnIndex
                        )
                    }\n"
                )
                cursor.moveToNext()
            }
            resultTextView.text = strBuild.toString()
        } else {
            resultTextView.text = "No Records Found"
        }

        cursor?.close()

    }
}