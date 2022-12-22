package com.jellysoft.sundigitalindia

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.get
import com.example.adapter.FoodsAdapter
import com.example.item.ItemCity
import com.example.item.ItemFood
import com.example.util.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.android.synthetic.main.activity_blood_search.view.*
import kotlinx.android.synthetic.main.activity_contact.*
import kotlinx.android.synthetic.main.activity_details_cart.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.activity_provider.*
import kotlinx.android.synthetic.main.details_popup.view.*
import org.json.JSONObject
import java.util.*


class BloodSearchActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    var progressBar: ProgressBar? = null
    var adapter: FoodsAdapter? = null
    var mList: ArrayList<ItemFood>? = null
    var MyApp: MyApplication? = null
    lateinit var bloodFilter: Spinner
    lateinit var bloodDropDown: TextView
    private lateinit var cityFilter: TextView
    private lateinit var searchBtn: Button
    private lateinit var resetBtn: Button
    lateinit var table: TableLayout
    lateinit var layout: ConstraintLayout
    var bloods = ArrayList<String>()
    var cities: ArrayList<ItemCity> = arrayListOf()
    var mCityName: ArrayList<String> = arrayListOf()
    lateinit var dialog: Dialog
    var city = "தூத்துக்குடி"
    var cityId = ""
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("state2", "create")
        setContentView(R.layout.activity_blood_search)
        IsRTL.ifSupported(this)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }
        title = "Blood Bank"

        MyApp = MyApplication.getInstance()
        mList = ArrayList()
        layout = findViewById(R.id.layout)
        progressBar = findViewById(R.id.progressBar2)
        bloodFilter = findViewById(R.id.bloodFilter)
        bloodDropDown = findViewById(R.id.textView3)
        cityFilter = findViewById(R.id.cityFilter)
        searchBtn = findViewById(R.id.searchBtn)
        resetBtn = findViewById(R.id.resetBtn)
        table = findViewById(R.id.tableLayout)

        dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_searchable_spinner)
        dialog.window?.setLayout(750, 800)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        cityFilter.setOnFocusChangeListener { v, _ -> openCityPopup(v) }
        cityFilter.setOnClickListener { openCityPopup(it) }

        searchBtn.setOnClickListener {
            clearTable()
            getDonors()
        }

        resetBtn.setOnClickListener {
            cityFilter.text = ""
            cityId = ""
            table.visibility = View.GONE
            clearTable()
        }

        if (NetworkUtils.isConnected(this@BloodSearchActivity)) {
            getBloods()
            listCities()

            bloodFilter.onItemSelectedListener = this

            details
        } else {
            Toast.makeText(this, "No Network Connection!!!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearTable() {
        val view = table[0]
        table.removeAllViews()
        table.addView(view)
    }

    private fun openCityPopup(view: View) {
        dialog.show()

        // Initialize and assign variable
        val editText: EditText = dialog.findViewById(R.id.edit_text)
        val listView: ListView = dialog.findViewById(R.id.list_view)

        // Initialize array adapter
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            view.context,
            android.R.layout.simple_list_item_1, mCityName
        )

        // set adapter
        listView.adapter = adapter
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                adapter.filter.filter(s)
            }

            override fun afterTextChanged(s: Editable) {}
        })
        listView.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ -> // when item selected from list
                // set selected item on textView
                cityFilter.text = adapter.getItem(position)
                cityId = cities[position].cityId

                // Dismiss dialog
                dialog.dismiss()
            }
    }

    private fun openPopup(view: View, donor: JSONObject) {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView: View = inflater.inflate(R.layout.details_popup, null)
        val btn: ImageButton = popupView.findViewById(R.id.closeBtn)

        // create the popup window
        val width = 950
        val height = 1150
        val focusable = true // lets taps outside the popup also dismiss it
        Log.d("donor", donor.getString("blood_phone_number"))
        popupView.name.text = donor.getString("blood_name")
        popupView.gender.text = donor.getString("blood_gender")
        popupView.donor_phone.text = donor.getString("blood_phone_number")
        popupView.donor_phone2.text = donor.getString("blood_phone_number2")
        popupView.district.text = donor.getString("city_name")
        popupView.city.text = donor.getString("blood_area")
        popupView.address.text = donor.getString("blood_address")

        popupView.donor_phone.setOnClickListener{
            call(donor.getString("blood_phone_number"))
        }
        popupView.donor_phone2.setOnClickListener{
            call(donor.getString("blood_phone_number2"))
        }

        val popupWindow = PopupWindow(popupView, width, height, focusable)

        popupWindow.animationStyle = R.style.popup_window_animation
        popupWindow.isOutsideTouchable = false
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)

        btn.setOnClickListener {
            popupWindow.dismiss();
        }
    }

    private fun getBloods() {
        val client = AsyncHttpClient()
        val params = RequestParams()
        val jsObj = Gson().toJsonTree(API()) as JsonObject
        jsObj.addProperty("method_name", "get_bloodcategory")
        jsObj.addProperty("user_id", UserUtils.getUserId())
        params.put("data", API.toBase64(jsObj.toString()))
        client.post(Constant.API_URL, params, object : AsyncHttpResponseHandler() {
            override fun onStart() {
                super.onStart()
                bloodFilter.visibility = View.GONE
                bloodDropDown.visibility = View.GONE
                progressBar?.visibility = View.VISIBLE
            }

            override fun onFinish() {
                super.onFinish()
                bloodFilter.visibility = View.VISIBLE
                bloodDropDown.visibility = View.VISIBLE
                progressBar?.visibility = View.GONE
            }

            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray
            ) {
                val result = String(responseBody)
                Log.d("result", result)
                try {
                    val mainJson = JSONObject(result)
                    val jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME)
                    var objJson: JSONObject
                    bloods.add(0, "இரத்தப் பிரிவைத் தேர்ந்தெடு")
                    for (i in 0 until jsonArray.length()) {
                        objJson = jsonArray.getJSONObject(i)
                        val blood = objJson.getString("category_name")
                        bloods.add(i + 1, blood)
                    }

                    val bloodAdapter: ArrayAdapter<String> =
                        ArrayAdapter<String>(applicationContext, android.R.layout.simple_spinner_item, bloods)
                    bloodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    bloodFilter.adapter = bloodAdapter

                } catch (e: Exception) {
                    Log.d("result", e.toString())
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?) {
            }
        })
    }

    private fun listCities() {
        val client = AsyncHttpClient()
        val params = RequestParams()
        val jsObj = Gson().toJsonTree(API()) as JsonObject
        jsObj.addProperty("method_name", "get_list")
        jsObj.addProperty("user_id", UserUtils.getUserId())
        params.put("data", API.toBase64(jsObj.toString()))
        client.post(Constant.API_URL, params, object : AsyncHttpResponseHandler() {
            override fun onStart() {
                super.onStart()
                progressBar?.visibility = View.VISIBLE
            }

            override fun onFinish() {
                super.onFinish()
                progressBar?.visibility = View.GONE
            }

            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray
            ) {
                val result = String(responseBody)
                Log.d("cities", result)
                try {
                    val mainJson = JSONObject(result)
                    val jobAppJson = mainJson.getJSONObject(Constant.ARRAY_NAME)
                    val cityArray = jobAppJson.getJSONArray("city_list")
                    Log.d("cities2", cityArray.toString())
                    for (i in 0 until cityArray.length()) {
                        val jsonObject = cityArray.getJSONObject(i)
                        val objItem = ItemCity()
                        objItem.cityId = jsonObject.getString(Constant.CITY_ID)
                        objItem.cityName = jsonObject.getString(Constant.CITY_NAME)
                        mCityName.add(jsonObject.getString(Constant.CITY_NAME))
                        cities.add(objItem)
                    }

                } catch (e: java.lang.Exception) {
                    Log.e("err", e.toString())
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray,
                error: Throwable
            ) {
            }
        })
    }

    private fun getDonors() {
        var selectedCity = cityFilter.text.ifBlank { "தூத்துக்குடி" }
        var blood = bloodFilter.selectedItem.toString()
        val client = AsyncHttpClient()
        val params = RequestParams()
        val jsObj = Gson().toJsonTree(API()) as JsonObject
        jsObj.addProperty("method_name", "get_blooddonors")
        jsObj.addProperty("user_id", UserUtils.getUserId())
        jsObj.addProperty("job_id", blood)
        jsObj.addProperty("city_id", selectedCity.trim().toString())
        params.put("data", API.toBase64(jsObj.toString()))
        Log.d("donors", blood)
        Log.d("donors", selectedCity.trim().toString())
        client.post(Constant.API_URL, params, object : AsyncHttpResponseHandler() {
            override fun onStart() {
                super.onStart()
                progressBar?.visibility = View.VISIBLE
            }
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray
            ) {
                val result = String(responseBody)
                Log.d("donors", result)
                try {
                    val mainJson = JSONObject(result)
                    val donors = mainJson.getJSONArray(Constant.ARRAY_NAME)
                    val typeface = resources.getFont(R.font.calibril)

                    for (i in 0 until donors.length()) {
                        val donor = donors.getJSONObject(i)
                        Log.d("donor", donor.getString("blood_name"))
                        val newRow = TableRow(applicationContext)
                        newRow.layoutParams =
                            TableRow.LayoutParams(
                                TableRow.LayoutParams.MATCH_PARENT,
                                TableRow.LayoutParams.WRAP_CONTENT
                            )

                        val num = TextView(applicationContext)
                        num.typeface = typeface
                        num.text = "${i + 1}"
                        num.textAlignment = View.TEXT_ALIGNMENT_CENTER
                        num.setTextColor(Color.BLACK)
                        num.textSize = 16f

                        val name = TextView(applicationContext)
                        name.typeface = typeface
                        name.text = donor.getString("blood_name")
                        name.textAlignment = View.TEXT_ALIGNMENT_CENTER
                        name.setTextColor(Color.BLACK)
                        name.textSize = 16f

                        val mobile = TextView(applicationContext)
                        mobile.typeface = typeface
                        mobile.text = donor.getString("blood_phone_number")
                        mobile.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
                        if(donor.getString("blood_name").length < 13) {
                            mobile.setPadding(0, 10, 0, 0)
                        }
                        mobile.setTextColor(Color.BLACK)
                        mobile.textSize = 16f
                        mobile.setOnClickListener {
                            val phone = donor.getString("blood_phone_number")
                            call(phone)
                        }

                        val action = LinearLayout(applicationContext)
                        val callBtn = ImageButton(applicationContext)
                        callBtn.adjustViewBounds = true
                        callBtn.maxHeight = 100
                        callBtn.maxWidth = 100
                        callBtn.setBackgroundResource(R.drawable.phone1)
                        callBtn.setOnClickListener {
                            val phone = donor.getString("blood_phone_number")
                            call(phone)
                        }
                        action.addView(callBtn)
                        val whatsapp = ImageButton(applicationContext)
                        whatsapp.adjustViewBounds = true
                        whatsapp.maxHeight = 100
                        whatsapp.maxWidth = 100
                        whatsapp.setBackgroundResource(R.drawable.whatsapp1)
                        whatsapp.setOnClickListener {
                            val phone = "91" + donor.getString("blood_phone_number").replace("+91", "")
                            val url = "https://api.whatsapp.com/send?phone=$phone"
                            val pm = packageManager
                            try {
                                pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
                            } catch (e: PackageManager.NameNotFoundException) {
                                e.printStackTrace()
                            }
                            val indent = Intent(Intent.ACTION_VIEW)
                            indent.data = Uri.parse(url)
                            startActivity(indent)
                        }
                        action.addView(whatsapp)
                        val details = ImageButton(applicationContext)
                        details.adjustViewBounds = true
                        details.maxHeight = 100
                        details.maxWidth = 100
                        details.setBackgroundResource(R.drawable.search1)
                        details.setOnClickListener {
                            openPopup(it, donor)
                        }
                        action.addView(details)

                        newRow.setBackgroundResource(R.color.row_color)
                        newRow.addView(num, TableRow.LayoutParams(15, TableRow.LayoutParams.WRAP_CONTENT, .3f))
                        newRow.addView(name, TableRow.LayoutParams(120, TableRow.LayoutParams.WRAP_CONTENT, 1.0f))
                        newRow.addView(mobile, TableRow.LayoutParams(100, TableRow.LayoutParams.WRAP_CONTENT, 1.0f))
                        newRow.addView(action, TableRow.LayoutParams(100, TableRow.LayoutParams.WRAP_CONTENT, 1.0f))

                        val v = View(applicationContext);
                        v.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, 1);
                        v.setBackgroundColor(Color.rgb(255, 255, 255));
                        table.addView(newRow)
                        table.addView(v)
                    }
                } catch (e: java.lang.Exception) { Log.e("err", e.toString()) }
            }

            override fun onFinish() {
                super.onFinish()
                progressBar?.visibility = View.GONE
                table.visibility = View.VISIBLE
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray,
                error: Throwable
            ) {
                progressBar?.visibility = View.VISIBLE
            }
        })
    }

    private fun call(phone: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$phone")
        startActivity(intent)
    }

    private val details: Unit
        private get() {
        }

    @SuppressLint("SetTextI18n")
    private fun setResult() {
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_share, menu)
        return true
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            android.R.id.home -> onBackPressed()
            else -> return super.onOptionsItemSelected(menuItem)
        }
        return true
    }

    override fun onBackPressed() {
        finish()
        val intent = Intent(this@BloodSearchActivity, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }
}