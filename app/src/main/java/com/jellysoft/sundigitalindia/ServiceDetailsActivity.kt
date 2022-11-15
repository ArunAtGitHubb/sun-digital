package com.jellysoft.sundigitalindia

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.db.DatabaseHelper
import com.example.fragment.ServiceDetailsFragment.Companion.newInstance
import com.example.item.ItemService
import com.example.util.*
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import com.squareup.picasso.Picasso
import cz.msebera.android.httpclient.Header
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import org.json.JSONException
import org.json.JSONObject

class ServiceDetailsActivity : AppCompatActivity() {
    private lateinit var mProgressBar: ProgressBar
    private lateinit var lyt_not_found: LinearLayout
    private lateinit var objBean: ItemService
    private lateinit var serviceTitle: TextView
    private lateinit var companyTitle: TextView
    private lateinit var serviceDate: TextView
    private lateinit var text_product_address: TextView
    private lateinit var serviceAddress: TextView
    private lateinit var servicePhone: TextView
    private lateinit var servicePhone2: TextView
    private lateinit var text_service_id: TextView
    private lateinit var text_service_category: TextView
    private lateinit var text_city: TextView
    private lateinit var last_date: TextView
    private lateinit var whatsapp_num: TextView
    private lateinit var mail_id: TextView
    private lateinit var serviceCost: TextView
    private lateinit var text_area: TextView
    private lateinit var serviceTime: TextView
    private lateinit var image: ImageView
    private lateinit var Id: String
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var btnSave: Button
    private lateinit var mAdViewLayout: LinearLayout
    private lateinit var areaLinear: LinearLayout
    private lateinit var mailLinear: LinearLayout
    private lateinit var pno2Linear: LinearLayout
    private lateinit var addLinear: LinearLayout
    private lateinit var btnApplyJob: Button
    private lateinit var btn_whats: Button
    private lateinit var MyApp: MyApplication
    private var isFromNotification = false
    private lateinit var lytParent: CoordinatorLayout
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager
    private lateinit var videoView: WebView
    var isJobSaved = false
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_service)
        IsRTL.ifSupported(this)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }
        objBean = ItemService()
        val data: Uri? = intent.data

        val i = intent
        Id = i.getStringExtra("Id").toString()
        if (i.hasExtra("isNotification")) {
            isFromNotification = true
        }

        if(data != null ) {
            Id = data.getQueryParameter("id").toString()
        }
        title = getString(R.string.tool_service_details)
        databaseHelper = DatabaseHelper(applicationContext)
        MyApp = MyApplication.getInstance()
        mProgressBar = findViewById(R.id.progressBar1)
        lyt_not_found = findViewById(R.id.lyt_not_found)
        videoView = findViewById(R.id.videoView)
        mAdViewLayout = findViewById(R.id.adView)
        serviceCost = findViewById(R.id.text_job_wage)
        areaLinear = findViewById(R.id.areaLinear)
        mailLinear = findViewById(R.id.mailLinear)
        pno2Linear = findViewById(R.id.pno2Linear)
        addLinear = findViewById(R.id.addLinear)
        btn_whats = findViewById(R.id.btn_whats)
        image = findViewById(R.id.image)
        serviceTitle = findViewById(R.id.text_job_title)
        companyTitle = findViewById(R.id.text_job_company)
        serviceDate = findViewById(R.id.text_job_date)
        serviceAddress = findViewById(R.id.text_job_address)
        servicePhone = findViewById(R.id.text_phone)
        servicePhone2 = findViewById(R.id.text_phone2)
        serviceTime = findViewById(R.id.text_job_work_time)
        lytParent = findViewById(R.id.lytParent)
        text_service_id = findViewById(R.id.text_job_id)
        btnApplyJob = findViewById(R.id.btn_apply_job)
        viewPager = findViewById(R.id.viewpager)
        tabLayout = findViewById(R.id.tabs)
        tabLayout.setupWithViewPager(viewPager)
        text_service_category = findViewById(R.id.text_job_category)
        text_city = findViewById(R.id.text_city)
        text_area = findViewById(R.id.text_area)
        last_date = findViewById(R.id.last_date)
        whatsapp_num = findViewById(R.id.whatsapp_num)
        mail_id = findViewById(R.id.mail_id)
        if (NetworkUtils.isConnected(this@ServiceDetailsActivity)) {
            details
        } else {
            showToast(getString(R.string.conne_msg1))
        }
        servicePhone.setOnClickListener(View.OnClickListener { v: View? ->
            dialNumber(
                objBean!!.servicePhoneNumber
            )
        })
        servicePhone2.setOnClickListener(View.OnClickListener { v: View? ->
            dialNumber(
                objBean!!.servicePhoneNumber2
            )
        })
        btnApplyJob.setOnClickListener(View.OnClickListener { v: View? ->
            dialNumber(
                objBean!!.servicePhoneNumber
            )
        })
    }

    private val details: Unit
        private get() {
            val client = AsyncHttpClient()
            val params = RequestParams()
            val jsObj = Gson().toJsonTree(API()) as JsonObject
            jsObj.addProperty("method_name", "get_single_service")
            jsObj.addProperty("job_id", Id)
            jsObj.addProperty("user_id", UserUtils.getUserId())
            params.put("data", API.toBase64(jsObj.toString()))
            client.post(Constant.API_URL, params, object : AsyncHttpResponseHandler() {
                override fun onStart() {
                    super.onStart()
                    mProgressBar.visibility = View.VISIBLE
                    lytParent.visibility = View.GONE
                }

                override fun onSuccess(
                    statusCode: Int,
                    headers: Array<Header>,
                    responseBody: ByteArray
                ) {
                    mProgressBar.visibility = View.GONE
                    lytParent.visibility = View.VISIBLE
                    val result = String(responseBody)
                    Log.d("result", result)
                    try {
                        val mainJson = JSONObject(result)
                        val jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME)
                        if (jsonArray.length() > 0) {
                            var objJson: JSONObject
                            for (i in 0 until jsonArray.length()) {
                                objJson = jsonArray.getJSONObject(i)
                                if (objJson.has(Constant.STATUS)) {
                                    lyt_not_found.visibility = View.VISIBLE
                                } else {
                                    objBean.id = objJson.getString(Constant.SERVICE_ID)
                                    objBean.serviceName = objJson.getString(Constant.SERVICE_NAME)
                                    objBean.serviceArea = objJson.getString(Constant.SERVICE_AREA)
                                    objBean.serviceSkill = objJson.getString(Constant.SERVICE_SKILL)
                                    objBean.serviceDesc = objJson.getString(Constant.SERVICE_DESC)
                                    objBean.serviceTime = objJson.getString(Constant.SERVICE_TIME)
                                    objBean.serviceCost = objJson.getString(Constant.SERVICE_COST)
                                    objBean.servicePhoneNumber = objJson.getString(Constant.SERVICE_PHONE_NO)
                                    objBean.servicePhoneNumber2 = objJson.getString(Constant.SERVICE_PHONE_NO2)
                                    objBean.serviceMail = objJson.getString(Constant.SERVICE_MAIL)
                                    objBean.serviceAddress = objJson.getString(Constant.SERVICE_ADDRESS)
                                    objBean.serviceDate =
                                        objJson.getString(Constant.SERVICE_START_DATE)
                                    objBean.pLate = objJson.getString(Constant.SERVICE_END_DATE)
                                    objBean.serviceImage =
                                        objJson.getString(Constant.SERVICE_IMAGE)
                                    objBean.WebsiteLink = objJson.getString(Constant.WEBSITE_LINK)
                                    objBean.url = objJson.getString("url")
                                    objBean.City = objJson.getString(Constant.CITY_NAME)
                                    objBean.cid = objJson.getString(Constant.CATEGORY_CID)
                                    objBean.serviceCategoryName = objJson.getString(Constant.CATEGORY_NAME)
                                    objBean.serviceCategoryImage = objJson.getString(Constant.CATEGORY_IMAGE)
                                    setResult()
                                }
                            }
                        } else {
                            mProgressBar!!.visibility = View.GONE
                            lytParent!!.visibility = View.GONE
                            lyt_not_found!!.visibility = View.VISIBLE
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(
                    statusCode: Int,
                    headers: Array<Header>,
                    responseBody: ByteArray,
                    error: Throwable
                ) {
                    mProgressBar!!.visibility = View.GONE
                    lytParent!!.visibility = View.GONE
                    lyt_not_found!!.visibility = View.VISIBLE
                }
            })
        }

    @SuppressLint("SetTextI18n")
    private fun setResult() {
        firstFavourite()
        serviceTitle!!.text = objBean!!.serviceName
        companyTitle!!.text = objBean!!.serviceCompanyName
        text_service_category!!.text = objBean!!.serviceCategoryName
        text_city!!.text = objBean!!.City
        serviceCost!!.text = "Rs. " + objBean!!.serviceCost
        serviceTime!!.text = objBean!!.serviceTime
        text_service_id!!.text = "AZ" + objBean!!.id
        serviceDate!!.text = objBean!!.serviceDate
        last_date!!.text = objBean!!.pLate

        if (!objBean.serviceAddress?.let { isNullOrEmpty(it) }!!){
            serviceAddress!!.text = objBean.serviceAddress
            addLinear.visibility = View.VISIBLE
        } else {
            addLinear.visibility = View.GONE
        }

        if (!objBean.serviceArea?.let { isNullOrEmpty(it) }!!){
            text_area.text = objBean.serviceArea
            areaLinear.visibility = View.VISIBLE
        } else {
            areaLinear.visibility = View.GONE
        }

        if (!objBean.servicePhoneNumber2?.let { isNullOrEmpty(it) }!!){
            val content2 = SpannableString(objBean.servicePhoneNumber2)
            content2.setSpan(UnderlineSpan(), 0, content2.length, 0)
            servicePhone2.text = content2
            pno2Linear.visibility = View.VISIBLE
        } else {
            pno2Linear.visibility = View.GONE
        }

        if (!objBean.serviceMail?.let { isNullOrEmpty(it) }!!){
            val content3 = SpannableString(objBean.serviceMail)
            content3.setSpan(UnderlineSpan(), 0, content3.length, 0)
            mail_id.text = content3
            mailLinear.visibility = View.VISIBLE
        } else {
            mailLinear.visibility = View.GONE
        }

        val content = SpannableString(objBean!!.servicePhoneNumber)
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        servicePhone.text = content
        whatsapp_num.text = content


        Picasso.get().load(objBean!!.serviceImage).into(image)
        if (objBean!!.url != null && !objBean!!.url!!.isEmpty()) {
            videoView!!.settings.javaScriptEnabled = true
            videoView!!.settings.pluginState = WebSettings.PluginState.ON
            videoView!!.loadUrl(objBean!!.url!!)
            videoView!!.webChromeClient = WebChromeClient()
        } else {
            videoView!!.visibility = View.GONE
        }
        setupViewPager(viewPager)
        tabLayout!!.setupWithViewPager(viewPager)
        btn_whats!!.setOnClickListener { view: View? ->
            val phone = "91" + objBean!!.servicePhoneNumber!!.replace("+91", "")
            val url = "https://api.whatsapp.com/send?phone=$phone"
            val pm = packageManager
            try {
                pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }
        whatsapp_num!!.setOnClickListener { view: View? ->
            val phone = "91" + objBean!!.servicePhoneNumber!!.replace("+91", "")
            val url = "https://api.whatsapp.com/send?phone=$phone"
            val pm = packageManager
            try {
                pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }
        mail_id!!.setOnClickListener { view: View? -> openEmail() }
    }

    private fun setupViewPager(viewPager: ViewPager?) {
        val adapter = ViewPagerAdapter(
            supportFragmentManager
        )
        adapter.addFragment(newInstance(objBean), getString(R.string.tab_service_details))
        //        adapter.addFragment(SimilarJobFragment.newInstance(Id), getString(R.string.tab_job_similar));
        viewPager!!.adapter = adapter
        viewPager.offscreenPageLimit = 1
    }

    internal inner class ViewPagerAdapter constructor(manager: FragmentManager) :
        FragmentStatePagerAdapter(manager) {
        private val mFragmentList: MutableList<Fragment> = ArrayList()
        private val mFragmentTitleList: MutableList<String> = ArrayList()
        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getItemPosition(`object`: Any): Int {
            return POSITION_NONE
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }
    }

    fun showToast(msg: String?) {
        Toast.makeText(this@ServiceDetailsActivity, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_share, menu)
        return true
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.menu_edit -> {
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    """
                        ${objBean!!.serviceName}
                        ${getString(R.string.job_company_lbl)}${objBean!!.serviceCompanyName}
                        ${getString(R.string.job_designation_lbl)}${objBean!!.serviceDesignation}
                        ${getString(R.string.job_phone_lbl)}${objBean!!.servicePhoneNumber}
                        ${getString(R.string.job_address_lbl)}${objBean!!.City}
                        
                        "View details here: https://www.sundigitalindiajobs.com/service?id=${objBean.id}"
                        
                        Download Application here https://play.google.com/store/apps/details?id=com.jellysoft.sundigitalindia
                        """.trimIndent()
                )
                sendIntent.type = "text/plain"
                startActivity(sendIntent)
            }
            else -> return super.onOptionsItemSelected(menuItem)
        }
        return true
    }

    private fun openWebsite() {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(addHttp(objBean!!.WebsiteLink))
            )
        )
    }

    private fun openEmail() {
        val emailIntent = Intent(
            Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", objBean!!.serviceMail, null
            )
        )
        emailIntent
            .putExtra(Intent.EXTRA_SUBJECT, "Apply for the post " + objBean!!.serviceDesignation)
        startActivity(Intent.createChooser(emailIntent, "Send suggestion..."))
    }

    protected fun addHttp(string1: String?): String {
        // TODO Auto-generated method stub
        return if (string1!!.startsWith("http://")) string1.toString() else "http://$string1"
    }

    private fun dialNumber(phone: String?) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null))
        startActivity(intent)
    }

    private fun firstFavourite() {}
    override fun onBackPressed() {
        if (isFromNotification) {
            val intent = Intent(this@ServiceDetailsActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        } else {
            super.onBackPressed()
        }
    }

    private fun isNullOrEmpty(value: String): Boolean {
        return value.equals("null") or value.isEmpty()
    }
}