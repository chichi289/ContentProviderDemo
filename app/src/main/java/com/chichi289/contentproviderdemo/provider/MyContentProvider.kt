package com.chichi289.contentproviderdemo.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.text.TextUtils
import com.chichi289.contentproviderdemo.db.MyDBHandler

class MyContentProvider : ContentProvider() {

    companion object {

        // defining authority so that other application can access it
        private const val AUTHORITY =
            "com.chichi289.contentproviderdemo"

        // defining content URI
        private const val URI = "content://$AUTHORITY/${MyDBHandler.TABLE_PRODUCTS}"

        val CONTENT_URI: Uri = Uri.parse(URI)

        private const val PRODUCTS = 1
        private const val PRODUCTS_ID = 2

    }

    private var myDB: MyDBHandler? = null
    private val mUriMatcher = UriMatcher(UriMatcher.NO_MATCH)

    init {
        mUriMatcher.addURI(AUTHORITY, MyDBHandler.TABLE_PRODUCTS, PRODUCTS)
        mUriMatcher.addURI(AUTHORITY, "${MyDBHandler.TABLE_PRODUCTS}/#", PRODUCTS_ID)
    }

    override fun onCreate(): Boolean {
        myDB = MyDBHandler(context = context, name = MyDBHandler.DATABASE_NAME, version = 1)
        return true
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val uriType = mUriMatcher.match(uri)

        val sqlDB = myDB!!.writableDatabase

        val id: Long
        when (uriType) {
            PRODUCTS -> id = sqlDB.insert(MyDBHandler.TABLE_PRODUCTS, null, values)
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        context?.contentResolver?.notifyChange(uri, null)
        return Uri.parse("${MyDBHandler.TABLE_PRODUCTS}/$id")
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        val queryBuilder = SQLiteQueryBuilder()
        queryBuilder.tables = MyDBHandler.TABLE_PRODUCTS

        when (mUriMatcher.match(uri)) {
            PRODUCTS_ID -> queryBuilder.appendWhere(
                MyDBHandler.COLUMN_ID + "="
                        + uri.lastPathSegment
            )

            PRODUCTS -> {
            }

            else -> throw IllegalArgumentException("Unknown URI")
        }

        val cursor = queryBuilder.query(
            myDB?.readableDatabase,
            projection, selection, selectionArgs, null, null,
            sortOrder
        )
        cursor.setNotificationUri(
            context?.contentResolver,
            uri
        )
        return cursor
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        val uriType = mUriMatcher.match(uri)
        val sqlDB: SQLiteDatabase = myDB!!.writableDatabase
        val rowsUpdated: Int

        when (uriType) {
            PRODUCTS -> rowsUpdated = sqlDB.update(
                MyDBHandler.TABLE_PRODUCTS,
                values,
                selection,
                selectionArgs
            )

            PRODUCTS_ID -> {
                val id = uri.lastPathSegment
                if (TextUtils.isEmpty(selection)) {

                    rowsUpdated = sqlDB.update(
                        MyDBHandler.TABLE_PRODUCTS,
                        values,
                        MyDBHandler.COLUMN_ID + "=" + id, null
                    )

                } else {
                    rowsUpdated = sqlDB.update(
                        MyDBHandler.TABLE_PRODUCTS,
                        values,
                        MyDBHandler.COLUMN_ID + "=" + id
                                + " and "
                                + selection,
                        selectionArgs
                    )
                }
            }

            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        context?.contentResolver?.notifyChange(uri, null)
        return rowsUpdated
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val uriType = mUriMatcher.match(uri)
        val sqlDB = myDB!!.writableDatabase
        val rowsDeleted: Int

        when (uriType) {
            PRODUCTS -> rowsDeleted = sqlDB.delete(
                MyDBHandler.TABLE_PRODUCTS,
                selection,
                selectionArgs
            )

            PRODUCTS_ID -> {
                val id = uri.lastPathSegment
                rowsDeleted = if (TextUtils.isEmpty(selection)) {
                    sqlDB.delete(
                        MyDBHandler.TABLE_PRODUCTS,
                        MyDBHandler.COLUMN_ID + "=" + id,
                        null
                    )
                } else {
                    sqlDB.delete(
                        MyDBHandler.TABLE_PRODUCTS,
                        MyDBHandler.COLUMN_ID + "=" + id
                                + " and " + selection,
                        selectionArgs
                    )
                }
            }

            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
        context?.contentResolver?.notifyChange(uri, null)
        return rowsDeleted
    }

    override fun getType(uri: Uri): String? {
        TODO(
            "Implement this to handle requests for the MIME type of the data" +
                    "at the given URI"
        )
    }


}