package com.example.fromnone

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.example.fromnone.databinding.ActivityMainBinding
import com.example.fromnone.utils.ActivityForResultUtils
import com.fromnone.lfyrepositoryx.PermissionX
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.runBlocking
import okhttp3.*
import org.json.JSONArray
import org.xml.sax.Attributes
import org.xml.sax.InputSource
import org.xml.sax.helpers.DefaultHandler
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.io.StringReader
import java.lang.Exception
import java.lang.StringBuilder
import javax.xml.parsers.SAXParserFactory

class MainActivity : BaseActivity() {
    private lateinit var binding:ActivityMainBinding
    private val activityForResultUtils = ActivityForResultUtils(this)

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        init()
//        activityForResultUtils.createCropPictureLauncher(){
//            binding.ivMain.setImageURI(it)
//        }.createTakePhotoAndSaveLauncher(){
//            it?.let { activityForResultUtils.cropPicture(it) }
//        }.createSelectPhotoLauncher(){
//            it?.let { activityForResultUtils.cropPicture(it) }
//        }
//
//        binding.btnPermission.setOnClickListener {
//            activityForResultUtils.selectPhoto()
//        }
//        ActivityUtils.startActivity<SecondActivity>(this){
//            putExtra("","")
//        }
    }
    private fun init(){
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://10.0.2.2/get_data.json")
            .build()
        Log.e(TAG, "init: ", )
        val call = client.newCall(request)
        findViewById<Button>(R.id.btn_call).setOnClickListener {
            if (!call.isExecuted()) {
                call.enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        Log.e(TAG, "onFailure: $e",)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        Log.e(TAG, "onResponse: ",)
                        val responseData = response.body?.string()
                        responseData?.let {
                            parseJSONWithGSON(it)
                        }
                    }
                })
            }
        }

        doCoroutines(call,object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "onFailureByC: $e",)
            }

            override fun onResponse(call: Call, response: Response) {
                Log.e(TAG, "onResponseByC: ",)
                val responseData = response.body?.string()
                responseData?.let {
                    parseJSONWithGSON(it)
                }
            }
        })

    }

    private fun doCoroutines(call: Call,callback: Callback) {
        runBlocking {
            call.enqueue(callback)
        }
    }

    private fun parseXmlWithPull(xmlData: String) {
        Log.e(TAG, "parseXmlWithPull: ")
        try {
            val factory = XmlPullParserFactory.newInstance()
            val xmlPullParser = factory.newPullParser()
            xmlPullParser.setInput(StringReader(xmlData))
            var eventType = xmlPullParser.eventType
            var id = ""
            var name = ""
            var version = ""
            while (eventType != XmlPullParser.END_DOCUMENT) {
                val nodeName = xmlPullParser.name
                when (eventType) {
                    //????????????????????????
                    XmlPullParser.START_TAG -> {
                        when (nodeName) {
                            "id" -> id = xmlPullParser.nextText()
                            "name" -> name = xmlPullParser.nextText()
                            "version" -> version = xmlPullParser.nextText()
                        }
                    }
                    //????????????????????????
                    XmlPullParser.END_TAG -> {
                        if ("app" == nodeName) {
                            Log.e(TAG, "parseXmlWithPull: $id + $name + $version",)
                        }
                    }
                }
                eventType = xmlPullParser.next()
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun parseXmlWithSAX(xmlData: String){
        try {
            val factory = SAXParserFactory.newInstance()
            val xmlReader = factory.newSAXParser().xmlReader
            val handler = ContentHandler()
            // ???ContentHandler??????????????????XMLReader???
            xmlReader.contentHandler = handler
            // ????????????
            xmlReader.parse(InputSource(StringReader(xmlData)))
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    class ContentHandler:DefaultHandler(){
        private var nodeName = ""
        private lateinit var id:StringBuilder
        private lateinit var name:StringBuilder
        private lateinit var version:StringBuilder

        //???????????????
        override fun startDocument() {
            id = StringBuilder()
            name = StringBuilder()
            version = StringBuilder()
        }

        //???????????????????????????
        override fun startElement(
            uri: String,
            localName: String,
            qName: String,
            attributes: Attributes
        ) {
            //?????????????????????
            nodeName = localName
            Log.e("lfy", "startElement: $uri + $localName + $qName + $attributes" )
        }

        //?????????????????????????????????
        override fun characters(ch: CharArray?, start: Int, length: Int) {
            //????????????????????????????????????????????????StringBuilder?????????
            when(nodeName){
                "id" -> id.append(ch,start,length)
                "name" -> name.append(ch,start,length)
                "version" -> version.append(ch,start,length)
            }
        }

        //?????????????????????????????????
        override fun endElement(uri: String?, localName: String?, qName: String?) {
            if ("app" == localName) {
                Log.e("lfy", "endElement: ${id.toString().trim()} + ${name.toString().trim()} + ${version.toString().trim()}")
                //????????????StringBuilder??????
                id.setLength(0)
                name.setLength(0)
                version.setLength(0)
            }
        }

        //?????????????????????????????????
        override fun endDocument() {
            super.endDocument()
        }
    }

    private fun parseJSONWithJSONObject(jsonData:String){
        try {
            val jsonArray = JSONArray(jsonData)
            for(i in 0 until jsonArray.length()){
                val jsonObject = jsonArray.getJSONObject(i)
                val id = jsonObject.getString("id")
                val version = jsonObject.getString("version")
                val name = jsonObject.getString("name")
                Log.e(TAG, "parseJSONWithJSONObject: $id + $version + $name", )
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun parseJSONWithGSON(jsonData: String){
        try {
            val gson = Gson()
            //???????????????
            //val data = gson.fromJson(jsonData,Data::class.java)

            //???????????????
            val typeOf = object :TypeToken<List<Data>>() {}.type
            val dataList = gson.fromJson<List<Data>>(jsonData,typeOf)
            for(m in dataList){
                Log.e(TAG, "parseJSONWithGSON: ${m.id} + ${m.version} + ${m.name}", )
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    data class Data(val id:String,val version:String,val name:String)
}