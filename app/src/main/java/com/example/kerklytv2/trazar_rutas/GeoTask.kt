package com.example.kerklytv2.trazar_rutas

import android.app.ProgressDialog
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import com.example.kerklytv2.vista.fragments.MapsFragment
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class GeoTask(var mContext: MapsFragment) : AsyncTask<String?, Void?, String?>() {
        var progressDialog: ProgressDialog? = null
        var geo1: Geo

        override fun onPreExecute() {
            super.onPreExecute()
            progressDialog = ProgressDialog(mContext.requireContext())
            progressDialog!!.setMessage("Loading")
            progressDialog!!.setCancelable(false)
            progressDialog!!.show()
        }

        override fun onPostExecute(aDouble: String?) {
            super.onPostExecute(aDouble)
            if (aDouble != null) {
                geo1.setDouble(aDouble)
                progressDialog!!.dismiss()
            } else Toast.makeText(
                mContext.requireContext(),
                "Error",
                Toast.LENGTH_SHORT
            ).show()
        }

        override fun doInBackground(vararg params: String?): String? {
            try {
                val url = URL(params[0])
                val con = url.openConnection() as HttpURLConnection
                con.requestMethod = "GET"
                con.connect()
                val statuscode = con.responseCode
                if (statuscode == HttpURLConnection.HTTP_OK) {
                    val br = BufferedReader(InputStreamReader(con.inputStream))
                    val sb = StringBuilder()
                    var line = br.readLine()
                    while (line != null) {
                        sb.append(line)
                        line = br.readLine()
                    }
                    val json = sb.toString()
                    val root = JSONObject(json)
                    val array_rows = root.getJSONArray("rows")
                    val object_rows = array_rows.getJSONObject(0)
                    val array_elements = object_rows.getJSONArray("elements")
                    val object_elements = array_elements.getJSONObject(0)
                    val object_duration = object_elements.getJSONObject("duration")
                    val object_distance = object_elements.getJSONObject("distance")
                    return object_duration.getString("value") + "," + object_distance.getString("value")
                }
            } catch (e: MalformedURLException) {
                Log.d("error", "error1")
            } catch (e: IOException) {
                Log.d("error", "error2")
            } catch (e: JSONException) {
                Log.d("error", "error3")
            }
            return null
        }

        interface Geo {
            fun setDouble(min: String?)
        }

        init {
            geo1 = mContext as Geo
        }
}
