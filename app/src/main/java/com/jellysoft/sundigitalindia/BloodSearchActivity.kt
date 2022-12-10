package com.jellysoft.sundigitalindia

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.adapter.FoodsAdapter
import com.example.item.ItemFood
import com.example.util.*
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.android.synthetic.main.activity_provider.*
import java.util.*


class BloodSearchActivity : AppCompatActivity() {
    var progressBar: ProgressBar? = null
    var adapter: FoodsAdapter? = null
    var mList: ArrayList<ItemFood>? = null
    var MyApp: MyApplication? = null
    lateinit var bloodFilter: Spinner
    lateinit var cityFilter: AutoCompleteTextView
    lateinit var areaFilter: AutoCompleteTextView
    lateinit var table: TableLayout
    lateinit var tableRow: TableRow
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
        progressBar = findViewById(R.id.progressBar2)
        bloodFilter = findViewById(R.id.bloodFilter)
        cityFilter = findViewById(R.id.cityFilter)
        areaFilter = findViewById(R.id.areaFilter)
        table = findViewById(R.id.tableLayout)
        val names = arrayOf("John", "Sam", "Hari", "Babu", "Ram", "John",
            "Sam", "Hari", "Babu", "Ram", "John", "Sam", "Hari", "Babu",
            "Ram", "John", "Sam", "Hari", "Babu", "Ram", "John", "Sam", "Hari",
            "Babu", "Ram", "John","Sam", "Hari", "Babu", "Ram", "John", "Sam",
            "Hari", "Babu","Ram", "John", "Sam", "Hari", "Babu", "Ram")
        for (i in names.indices) {
            val newRow = TableRow(applicationContext)
            newRow.layoutParams =
                TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT
                )

            val num = TextView(applicationContext)
            num.text = "${i + 1}"
            num.textAlignment = View.TEXT_ALIGNMENT_CENTER
            num.setTextColor(Color.BLACK)
            num.textSize = 16f

            val name = TextView(applicationContext)
            name.text = names[i]
            name.textAlignment = View.TEXT_ALIGNMENT_CENTER
            name.setTextColor(Color.BLACK)
            name.textSize = 16f

            val mobile = TextView(applicationContext)
            mobile.text = "8967324519"
            mobile.textAlignment = View.TEXT_ALIGNMENT_CENTER
            mobile.setTextColor(Color.BLACK)
            mobile.textSize = 16f

            val action = LinearLayout(applicationContext)
            val callBtn = ImageButton(applicationContext)
            callBtn.minimumHeight = 100
            callBtn.minimumWidth = 100
            callBtn.setBackgroundResource(R.drawable.ic_call)
            callBtn.setOnClickListener {
                Log.d("btnClick", "Cliked")
            }
            action.addView(callBtn)
            val whatsapp = ImageButton(applicationContext)
            whatsapp.minimumHeight = 100
            whatsapp.minimumWidth = 100
            whatsapp.setBackgroundResource(R.drawable.whatsapp)
            whatsapp.setOnClickListener {
                Log.d("btnClick", "Cliked")
            }
            action.addView(whatsapp)
            val details = ImageButton(applicationContext)
            details.minimumHeight = 100
            details.minimumWidth = 100
            details.setBackgroundResource(R.drawable.ic_search)
            details.setOnClickListener {
                Log.d("btnClick", "Cliked")
            }
            action.addView(details)

            newRow.setBackgroundResource(R.color.row_color)
            newRow.addView(num, TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, .3f))
            newRow.addView(name, TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.0f))
            newRow.addView(mobile, TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.0f))
            newRow.addView(action, TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.0f))
            table.addView(newRow)
        }

        val COUNTRIES = arrayOf(
            "Belgium", "France", "Italy", "Germany", "Spain",
            "Belgium1", "France", "Italy", "Germany", "Spain",
            "Belgium2", "France", "Italy", "Germany", "Spain"
        )
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            android.R.layout.simple_dropdown_item_1line, COUNTRIES
        )

        val bloods = arrayOf("B+", "B-", "O+", "O-", "AB-", "AB+")

        val bloodAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(applicationContext, android.R.layout.simple_spinner_item, bloods)
        bloodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        bloodFilter.adapter = bloodAdapter

        cityFilter.setAdapter(adapter)
        areaFilter.setAdapter(adapter)

        if (NetworkUtils.isConnected(this@BloodSearchActivity)) {
            details
        } else {
            Toast.makeText(this, "No Network Connection!!!", Toast.LENGTH_SHORT).show()
        }
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
}