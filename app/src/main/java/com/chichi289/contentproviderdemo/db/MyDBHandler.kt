package com.chichi289.contentproviderdemo.db

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.chichi289.contentproviderdemo.model.Product
import com.chichi289.contentproviderdemo.provider.MyContentProvider

class MyDBHandler(
    context: Context?, name: String?,
    factory: SQLiteDatabase.CursorFactory? = null,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {

    companion object {
        const val DATABASE_NAME = "product_database"
        const val TABLE_PRODUCTS = "products"

        const val COLUMN_ID = "id"
        const val COLUMN_PRODUCT_NAME = "product_name"
        const val COLUMN_QUANTITY = "quantity"
    }

    private var contentResolver: ContentResolver? = null

    init {
        contentResolver = context?.contentResolver
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "create table $TABLE_PRODUCTS " +
                    "($COLUMN_ID integer primary key, $COLUMN_PRODUCT_NAME text,$COLUMN_QUANTITY integer)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PRODUCTS")
        onCreate(db)
    }

    fun addProduct(product: Product) {
        val values = ContentValues()
        values.put(COLUMN_PRODUCT_NAME, product.productName)
        values.put(COLUMN_QUANTITY, product.quantity)
        contentResolver?.insert(MyContentProvider.CONTENT_URI, values)
    }

    fun findProduct(productName: String): Product? {
        val projection = arrayOf(COLUMN_ID, COLUMN_PRODUCT_NAME, COLUMN_QUANTITY)

        val selection = "product_name = \"$productName\""

        val cursor = contentResolver?.query(
            MyContentProvider.CONTENT_URI,
            projection, selection, null, null
        )

        var product: Product? = null

        if (cursor?.moveToFirst() == true) {
            cursor.moveToFirst()
            val mId = Integer.parseInt(cursor.getString(0))
            val mProductName = cursor.getString(1)
            val mQuantity = Integer.parseInt(cursor.getString(2))

            product = Product(mId, mProductName, mQuantity)
            cursor.close()
        }
        return product
    }

    fun deleteProduct(productName: String): Boolean {

        var result = false

        val selection = "product_name = \"$productName\""

        val rowsDeleted = contentResolver?.delete(
            MyContentProvider.CONTENT_URI,
            selection, null
        )

        if (rowsDeleted != null) {
            if (rowsDeleted > 0)
                result = true
        }

        return result
    }
}