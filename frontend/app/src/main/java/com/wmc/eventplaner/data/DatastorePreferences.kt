package com.wmc.eventplaner.data

/**
 * Created by Ahmad Fawad ali
 * AIS company,
 * Islamabad, Pakistan.
 */

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.ArrayList

@Singleton
class DatastorePreferences @Inject constructor(
    @ApplicationContext private val context: Context
)  {
    private val Context.preferences: DataStore<Preferences> by preferencesDataStore(name = "settings")

    private var DEFAULT_APP_IMAGEDATA_DIRECTORY: String = ""

    /**
     * Returns the String path of the last saved image
     * @return string path of the last saved image
     */
    var savedImagePath = ""
        private set

    /**
     * Decodes the Bitmap from 'path' and returns it
     * @param path image path
     * @return the Bitmap from 'path'
     */
    fun getImage(path: String?): Bitmap? {
        var bitmapFromPath: Bitmap? = null
        try {
            bitmapFromPath = BitmapFactory.decodeFile(path)
        } catch (e: Exception) {
            // TODO: handle exception
            e.printStackTrace()
        }
        return bitmapFromPath
    }

    /**
     * Saves 'theBitmap' into folder 'theFolder' with the name 'theImageName'
     * @param theFolder the folder path dir you want to save it to e.g "DropBox/WorkImages"
     * @param theImageName the name you want to assign to the image file e.g "MeAtLunch.png"
     * @param theBitmap the image you want to save as a Bitmap
     * @return returns the full path(file system address) of the saved image
     */
    fun putImage(theFolder: String?, theImageName: String?, theBitmap: Bitmap?): String? {
        if (theFolder == null || theImageName == null || theBitmap == null) return null
        DEFAULT_APP_IMAGEDATA_DIRECTORY = theFolder
        val mFullPath = setupFullPath(theImageName)
        if (mFullPath != "") {
            savedImagePath = mFullPath
            saveBitmap(mFullPath, theBitmap)
        }
        return mFullPath
    }

    /**
     * Saves 'theBitmap' into 'fullPath'
     * @param fullPath full path of the image file e.g. "Images/MeAtLunch.png"
     * @param theBitmap the image you want to save as a Bitmap
     * @return true if image was saved, false otherwise
     */
    fun putImageWithFullPath(fullPath: String?, theBitmap: Bitmap?): Boolean {
        return !(fullPath == null || theBitmap == null) && saveBitmap(fullPath, theBitmap)
    }

    /**
     * Creates the path for the image with name 'imageName' in DEFAULT_APP.. directory
     * @param imageName name of the image
     * @return the full path of the image. If it failed to create directory, return empty string
     */
    private fun setupFullPath(imageName: String): String {
        val mFolder = File(context.filesDir, DEFAULT_APP_IMAGEDATA_DIRECTORY)
        if (isExternalStorageReadable && isExternalStorageWritable && !mFolder.exists()) {
            if (!mFolder.mkdirs()) {
                Log.e("ERROR", "Failed to setup folder")
                return ""
            }
        }
        return mFolder.path + '/' + imageName
    }

    /**
     * Saves the Bitmap as a PNG file at path 'fullPath'
     * @param fullPath path of the image file
     * @param bitmap the image as a Bitmap
     * @return true if it successfully saved, false otherwise
     */
    private fun saveBitmap(fullPath: String?, bitmap: Bitmap?): Boolean {
        if (fullPath == null || bitmap == null) return false
        var fileCreated = false
        var bitmapCompressed = false
        var streamClosed = false
        val imageFile = File(fullPath)
        if (imageFile.exists()) if (!imageFile.delete()) return false
        try {
            fileCreated = imageFile.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        var out: FileOutputStream? = null
        try {
            out = FileOutputStream(imageFile)
            bitmapCompressed = bitmap.compress(CompressFormat.PNG, 100, out)
        } catch (e: Exception) {
            e.printStackTrace()
            bitmapCompressed = false
        } finally {
            if (out != null) {
                try {
                    out.flush()
                    out.close()
                    streamClosed = true
                } catch (e: IOException) {
                    e.printStackTrace()
                    streamClosed = false
                }
            }
        }
        return fileCreated && bitmapCompressed && streamClosed
    }

    // Getters
    /**
     * Get int value from DataStore at 'key'. If key not found, return 0
     * @param key DataStore key
     * @return int value at 'key' or 0 if key not found
     */
    fun getInt(key: String): Int {
        var value = 0
        runBlocking {
            value = context.preferences.data.first()[intPreferencesKey(key)] ?: 0
        }
        return value
    }

    /**
     * Get parsed ArrayList of Integers from DataStore at 'key'
     * @param key DataStore key
     * @return ArrayList of Integers
     */
    fun getListInt(key: String): ArrayList<Int> {
        var value = ""
        runBlocking {
            value = context.preferences.data.first()[stringPreferencesKey(key)] ?: ""
        }
        val p = value
        val myList = TextUtils.split(p, "‚‗‚")
        val arrayToList = ArrayList(listOf(*myList))
        val newList = ArrayList<Int>()
        for (item in arrayToList) newList.add(item.toInt())
        return newList
    }

    /**
     * Get long value from DataStore at 'key'. If key not found, return 0
     * @param key DataStore key
     * @return long value at 'key' or 0 if key not found
     */
    fun getLong(key: String): Long {
        var value: Long
        runBlocking {
            value = context.preferences.data.first()[longPreferencesKey(key)] ?: 0
        }
        return value
    }

    /**
     * Get float value from DataStore at 'key'. If key not found, return 0
     * @param key DataStore key
     * @return float value at 'key' or 0 if key not found
     */
    fun getFloat(key: String): Float {
        var value: Float
        runBlocking {
            value =  context.preferences.data.first()[floatPreferencesKey(key)] ?: 0F
        }
        return value
    }

    /**
     * Get double value from DataStore at 'key'. If exception thrown, return 0
     * @param key DataStore key
     * @return double value at 'key' or 0 if exception is thrown
     */
    fun getDouble(key: String): Double {
        val number = getString(key)
        return try {
            number.toDouble()
        } catch (e: NumberFormatException) {
            0.0
        }
    }

    /**
     * Get parsed ArrayList of Double from DataStore at 'key'
     * @param key DataStore key
     * @return ArrayList of Double
     */
    fun getListDouble(key: String): ArrayList<Double> {
        var value = ""
        runBlocking {
            value = context.preferences.data.first()[stringPreferencesKey(key)] ?: ""
        }
        val p = value
        val myList = TextUtils.split(p, "‚‗‚")
        val arrayToList = ArrayList(listOf(*myList))
        val newList = ArrayList<Double>()
        for (item in arrayToList) newList.add(item.toDouble())
        return newList
    }

    /**
     * Get parsed ArrayList of Integers from DataStore at 'key'
     * @param key DataStore key
     * @return ArrayList of Longs
     */
    fun getListLong(key: String): ArrayList<Long> {
        var value = ""
        runBlocking {
            value = context.preferences.data.first()[stringPreferencesKey(key)] ?: ""
        }
        val p = value
        val myList = TextUtils.split(p, "‚‗‚")
        val arrayToList = ArrayList(listOf(*myList))
        val newList = ArrayList<Long>()
        for (item in arrayToList) newList.add(item.toLong())
        return newList
    }

    /**
     * Get String value from DataStore at 'key'. If key not found, return ""
     * @param key DataStore key
     * @return String value at 'key' or "" (empty String) if key not found
     */
    fun getString(key: String): String {
        var value = ""
        runBlocking {
            value = context.preferences.data.first()[stringPreferencesKey(key)]?: ""
        }
        return value
    }

    /**
     * Get parsed ArrayList of String from DataStore at 'key'
     * @param key DataStore key
     * @return ArrayList of String
     */
    fun getListString(key: String): ArrayList<String> {
        var value = ""
        runBlocking {
            value = context.preferences.data.first()[stringPreferencesKey(key)] ?: ""
        }
        val p = value
        return ArrayList(listOf(*TextUtils.split(p, "‚‗‚")))
    }

    /**
     * Get boolean value from DataStore at 'key'. If key not found, return false
     * @param key DataStore key
     * @return boolean value at 'key' or false if key not found
     */
    fun getBoolean(key: String): Boolean {
        var value = false
        runBlocking(Dispatchers.IO) {
            value = context.preferences.data.map {
                it[booleanPreferencesKey(key)] ?: false
            }.first()
        }
        return value
    }

    fun getBooleanWithFlow(key: String): Flow<Boolean> {
        return context.preferences.data.map {
            it[booleanPreferencesKey(key)] ?: false
        }
    }

    /**
     * Get parsed ArrayList of Boolean from DataStore at 'key'
     * @param key DataStore key
     * @return ArrayList of Boolean
     */
    fun getListBoolean(key: String): ArrayList<Boolean> {
        val myList = getListString(key)
        val newList = ArrayList<Boolean>()
        for (item in myList) {
            newList.add(item == "true")
        }
        return newList
    }

    inline fun <reified T> getListObject(key: String): ArrayList<T> {
        val gson = Gson()
        val objStrings = getListString(key)
        val objects = arrayListOf<T>()
        for (jObjString in objStrings) {
            val value = gson.fromJson(jObjString, T::class.java)
            objects.add(value)
        }
        return objects
    }

    fun <T> getObject(key: String, classOfT: Class<T>?): T? {
        val json = getString(key)
        try {
            val value: T = Gson().fromJson(json, classOfT) ?: throw NullPointerException()
            return value
        }catch (e:Exception)
        {
            return null
        }
    }
    fun <T> getObjectNullable(key: String, classOfT: Class<T>?): T? {
        val json = getString(key)

        return runCatching {
            Gson().fromJson(json, classOfT)
        }.onFailure {
            it.printStackTrace() // Handle or log the exception as needed
        }.getOrNull()
    }

    // Put methods
    /**
     * Put int value into DataStore with 'key' and save
     * @param key DataStore key
     * @param value int value to be added
     */
    fun putInt(key: String, value: Int) {
        checkForNullKey(key)
        runBlocking {
            context.preferences.edit {
                it[intPreferencesKey(key)] = value
            }
        }
    }

    /**
     * Put ArrayList of Integer into DataStore with 'key' and save
     * @param key DataStore key
     * @param intList ArrayList of Integer to be added
     */
    fun putListInt(key: String, intList: ArrayList<Int>) {
        checkForNullKey(key)
        val myIntList = intList.toTypedArray()
        runBlocking {
            context.preferences.edit {
                it[stringPreferencesKey(key)] = TextUtils.join("‚‗‚", myIntList)
            }
        }
    }

    /**
     * Put long value into DataStore with 'key' and save
     * @param key DataStore key
     * @param value long value to be added
     */
    fun putLong(key: String, value: Long) {
        checkForNullKey(key)
        runBlocking {
            context.preferences.edit { it[longPreferencesKey(key)] = value }
        }
    }

    /**
     * Put ArrayList of Long into DataStore with 'key' and save
     * @param key DataStore key
     * @param longList ArrayList of Long to be added
     */
    fun putListLong(key: String, longList: ArrayList<Long>) {
        checkForNullKey(key)
        val myLongList = longList.toTypedArray()

        runBlocking {
            context.preferences.edit {
                it[stringPreferencesKey(key)] = TextUtils.join("‚‗‚", myLongList)
            }
        }
    }

    /**
     * Put float value into DataStore with 'key' and save
     * @param key DataStore key
     * @param value float value to be added
     */
    fun putFloat(key: String, value: Float) {
        checkForNullKey(key)
        runBlocking {
            context.preferences.edit {
                it[floatPreferencesKey(key)] = value
            }
        }
    }

    /**
     * Put double value into DataStore with 'key' and save
     * @param key DataStore key
     * @param value double value to be added
     */
    fun putDouble(key: String, value: Double) {
        checkForNullKey(key)
        putString(key, value.toString())
    }

    /**
     * Put ArrayList of Double into DataStore with 'key' and save
     * @param key DataStore key
     * @param doubleList ArrayList of Double to be added
     */
    fun putListDouble(key: String, doubleList: ArrayList<Double>) {
        checkForNullKey(key)
        val myDoubleList = doubleList.toTypedArray()
        runBlocking {
            context.preferences.edit {
                it[stringPreferencesKey(key)] = TextUtils.join("‚‗‚", myDoubleList)
            }
        }
    }

    /**
     * Put String value into DataStore with 'key' and save
     * @param key DataStore key
     * @param value String value to be added
     */
    fun putString(key: String, value: String) {
        checkForNullKey(key)
        checkForNullValue(value)
        runBlocking {
            context.preferences.edit {
                it[stringPreferencesKey(key)] = value
            }
        }
    }

    /**
     * Put ArrayList of String into DataStore with 'key' and save
     * @param key DataStore key
     * @param stringList ArrayList of String to be added
     */
    fun putListString(key: String, stringList: ArrayList<String>) {
        checkForNullKey(key)
        val myStringList = stringList.toTypedArray()
        runBlocking {
            context.preferences.edit {
                it[stringPreferencesKey(key)] = TextUtils.join("‚‗‚", myStringList)
            }
        }
    }

    /**
     * Put boolean value into DataStore with 'key' and save
     * @param key DataStore key
     * @param value boolean value to be added
     */
    fun putBoolean(key: String, value: Boolean) {
        checkForNullKey(key)
        runBlocking {
            context.preferences.edit {
                it[booleanPreferencesKey(key)] = value
            }
        }
    }

    /**
     * Put ArrayList of Boolean into DataStore with 'key' and save
     * @param key DataStore key
     * @param boolList ArrayList of Boolean to be added
     */
    fun putListBoolean(key: String, boolList: ArrayList<Boolean>) {
        checkForNullKey(key)
        val newList = ArrayList<String>()
        for (item in boolList) {
            if (item) {
                newList.add("true")
            } else {
                newList.add("false")
            }
        }
        putListString(key, newList)
    }

    /**
     * Put ObJect any type into SharedPrefrences with 'key' and save
     * @param key DataStore key
     * @param obj is the Object you want to put
     */
    fun putObject(key: String, obj: Any?) {
        checkForNullKey(key)
        val gson = Gson()
        putString(key, gson.toJson(obj))
    }

    fun <T> putListObject(key: String, objArray: ArrayList<T>) {
        checkForNullKey(key)
        val gson = Gson()
        val objStrings = ArrayList<String>()
        for (obj in objArray) {
            objStrings.add(gson.toJson(obj))
        }
        putListString(key, objStrings)
    }

    /**
     * Remove DataStore item with 'key'
     * @param key DataStore key
     */
    fun remove(key: String) {
        runBlocking {
            context.preferences.edit {
                it.remove(stringPreferencesKey(key))
            }
        }
    }

    /**
     * Clear DataStore (remove everything)
     */
    fun clear() {
        runBlocking {
            context.preferences.edit { it.clear() }
        }
    }
    suspend fun clearByKey(keyName: String) {
        context.preferences.edit { preferences ->
            val key = stringPreferencesKey(keyName)
            preferences.remove(key)
        }
    }


    /**
     * null keys would corrupt the shared pref file and make them unreadable this is a preventive measure
     * @param key the pref key to check
     */
    private fun checkForNullKey(key: String?) {
        if (key == null) {
            throw NullPointerException()
        }
    }

    /**
     * null keys would corrupt the shared pref file and make them unreadable this is a preventive measure
     * @param value the pref value to check
     */
    private fun checkForNullValue(value: String?) {
        if (value == null) {
            throw NullPointerException()
        }
    }

    /**
     * Check if external storage is writable or not
     * @return true if writable, false otherwise
     */
    val isExternalStorageWritable: Boolean get() = Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()

    /**
     * Check if external storage is readable or not
     * @return true if readable, false otherwise
     */
    val isExternalStorageReadable: Boolean get() {
        val state = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == state || Environment.MEDIA_MOUNTED_READ_ONLY == state
    }

    companion object {
        @Volatile
        private var INSTANCE: DatastorePreferences? = null
        fun getInstance(context: Context): DatastorePreferences = INSTANCE ?: synchronized(this) {
            INSTANCE ?: DatastorePreferences(context).also { INSTANCE = it }
        }
    }
}