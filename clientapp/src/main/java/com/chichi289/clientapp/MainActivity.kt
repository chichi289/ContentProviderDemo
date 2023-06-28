package com.chichi289.clientapp

import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Button
import android.widget.TextView
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

    private val resultTextView by lazy {
        findViewById<TextView>(R.id.tvResult)
    }

    private val btnContacts by lazy {
        findViewById<Button>(R.id.btnContacts)
    }

    private val btnProducts by lazy {
        findViewById<Button>(R.id.btnProducts)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnProducts.setOnClickListener {
            getDataFromProviderApp()
        }

        btnContacts.setOnClickListener {
            getContacts()
        }
    }

    private fun getDataFromProviderApp() {
        val cursor = contentResolver.query(
            CONTENT_URI,
            null,
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
            resultTextView.text = getString(R.string.txt_no_records_found)
        }
        cursor?.close()
    }

    /**
     * @method getContacts need runtime permission of android.permission.READ_CONTACTS
     * in order to check this demo please grant this permission manually from settings
     * */
    private fun getContacts() {

        val resolver = contentResolver

        // content://com.android.contacts/data/phones
        val uri: Uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI

        // table column name
        // passing null will return all the columns
        val project: Array<String> = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        // SELECT * FROM contacts
        val selection: String? = null

        val selectionArgs: Array<String>? = null

        val sortOrder: String? = null

        val cursor: Cursor? = resolver.query(uri, project, selection, selectionArgs, sortOrder)
        val stringBuilder = StringBuilder()
        while (cursor?.moveToNext() == true) {
            val name: String =
                cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            val number: String =
                cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
            stringBuilder.append("$name - $number\n")
        }
        resultTextView.text = stringBuilder.toString()
        cursor?.close()
    }
}