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
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.db.DatabaseHelper
import com.example.fragment.MatrimonyDetailsFragment.Companion.newInstance
import com.example.item.ItemMatrimony
import com.example.util.*
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import org.json.JSONException
import org.json.JSONObject

class MatrimonyDetailsActivity : AppCompatActivity() {
    lateinit var mProgressBar: ProgressBar
    lateinit var lyt_not_found: LinearLayout
    lateinit var objBean: ItemMatrimony
    lateinit var matrimonyName: TextView
    lateinit var matrimonyDate: TextView
    lateinit var matrimonySalary: TextView
    lateinit var matrimonyPhone: TextView
    lateinit var matrimonyReligion: TextView
    lateinit var matrimonyPhone2: TextView
    lateinit var matrimonyJob: TextView
    lateinit var matrimonyAge: TextView
    lateinit var text_city: TextView
    lateinit var last_date: TextView
    lateinit var whatsapp_num: TextView
    lateinit var text_area: TextView
    lateinit var matrimonyCaste: TextView
    lateinit var text_matrimony_id: TextView
    lateinit var text_gender: TextView
    private lateinit var areaLinear: LinearLayout
    private lateinit var pno2Linear: LinearLayout
    lateinit var image: ImageSlider
    lateinit var Id: String
    lateinit var databaseHelper: DatabaseHelper
    lateinit var btnSave: Button
    lateinit var mAdViewLayout: LinearLayout
    lateinit var btnApplyJob: Button
    lateinit var btn_whats: Button
    lateinit var MyApp: MyApplication
    var isFromNotification = false
    lateinit var lytParent: CoordinatorLayout
    lateinit var tabLayout: TabLayout
    lateinit var viewPager: ViewPager
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_matrimony)
        IsRTL.ifSupported(this)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }
        objBean = ItemMatrimony()

        val data: Uri? = intent.data

        val i = intent
        Id = i.getStringExtra("Id").toString()
        if (i.hasExtra("isNotification")) {
            isFromNotification = true
        }

        if(data != null ) {
            Id = data.getQueryParameter("id").toString()
        }

        title = getString(R.string.tool_matrimony_details)
        databaseHelper = DatabaseHelper(applicationContext)
        MyApp = MyApplication.getInstance()
        mProgressBar = findViewById(R.id.progressBar1)
        lyt_not_found = findViewById(R.id.lyt_not_found)
        mAdViewLayout = findViewById(R.id.adView)
        matrimonyName = findViewById(R.id.text_name)
        matrimonyReligion = findViewById(R.id.text_matrimony_religion)
        text_gender = findViewById(R.id.text_gender)
        text_city = findViewById(R.id.text_city)
        text_area = findViewById(R.id.text_area)
        matrimonySalary = findViewById(R.id.text_salary)
        matrimonyJob = findViewById(R.id.text_job)
        matrimonyCaste = findViewById(R.id.text_caste)
        matrimonyAge = findViewById(R.id.text_age)
        matrimonyDate = findViewById(R.id.text_job_date)
        text_matrimony_id = findViewById(R.id.text_matrimony_id)
        areaLinear = findViewById(R.id.areaLinear)
        pno2Linear = findViewById(R.id.pno2Linear)
        last_date = findViewById(R.id.last_date)
        btn_whats = findViewById(R.id.btn_whats)
        image = findViewById(R.id.image_slider)
        matrimonyPhone = findViewById(R.id.text_phone)
        matrimonyPhone2 = findViewById(R.id.text_phone2)
        lytParent = findViewById(R.id.lytParent)
        btnApplyJob = findViewById(R.id.btn_apply_job)
        viewPager = findViewById(R.id.viewpager)
        tabLayout = findViewById(R.id.tabs)
        tabLayout.setupWithViewPager(viewPager)
        whatsapp_num = findViewById(R.id.whatsapp_num)
        if (NetworkUtils.isConnected(this@MatrimonyDetailsActivity)) {
            details
        } else {
            showToast(getString(R.string.conne_msg1))
        }
        matrimonyPhone.setOnClickListener(View.OnClickListener { v: View? ->
            dialNumber(
                objBean!!.matrimonyPhoneNumber
            )
        })
        matrimonyPhone2.setOnClickListener(View.OnClickListener { v: View? ->
            dialNumber(
                objBean!!.matrimonyPhoneNumber2
            )
        })
        btnApplyJob.setOnClickListener(View.OnClickListener { v: View? ->
            dialNumber(
                objBean!!.matrimonyPhoneNumber
            )
        })
    }

    private val details: Unit
        private get() {
            val client = AsyncHttpClient()
            val params = RequestParams()
            val jsObj = Gson().toJsonTree(API()) as JsonObject
            jsObj.addProperty("method_name", "get_single_matrimony")
            jsObj.addProperty("job_id", Id)
            jsObj.addProperty("user_id", UserUtils.getUserId())
            params.put("data", API.toBase64(jsObj.toString()))
            client.post(Constant.API_URL, params, object : AsyncHttpResponseHandler() {
                override fun onStart() {
                    super.onStart()
                    mProgressBar!!.visibility = View.VISIBLE
                    lytParent!!.visibility = View.GONE
                }

                override fun onSuccess(
                    statusCode: Int,
                    headers: Array<Header>,
                    responseBody: ByteArray
                ) {
                    mProgressBar!!.visibility = View.GONE
                    lytParent!!.visibility = View.VISIBLE
                    val result = String(responseBody)
                    Log.d("result", "$result $Id")
                    try {
                        val mainJson = JSONObject(result)
                        val jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME)
                        if (jsonArray.length() > 0) {
                            var objJson: JSONObject
                            for (i in 0 until jsonArray.length()) {
                                objJson = jsonArray.getJSONObject(i)
                                if (objJson.has(Constant.STATUS)) {
                                    lyt_not_found!!.visibility = View.VISIBLE
                                } else {
                                    val images = ArrayList<SlideModel>()
                                    objBean!!.id = objJson.getString(Constant.SERVICE_ID)
                                    objBean!!.matrimonyName =
                                        objJson.getString(Constant.MATRIMONY_NAME)
                                    objBean!!.matrimonyGender =
                                        objJson.getString(Constant.MATRIMONY_GENDER)
                                    objBean!!.matrimonyReligion =
                                        objJson.getString(Constant.MATRIMONY_RELIGION)
                                    objBean!!.matrimonyArea =
                                        objJson.getString(Constant.MATRIMONY_AREA)
                                    objBean!!.matrimonyMaritalStatus =
                                        objJson.getString(Constant.MATRIMONY_MARITAL_STATUS)
                                    objBean!!.matrimonyDob =
                                        objJson.getString(Constant.MATRIMONY_DOB)
                                    objBean!!.matrimonyAge =
                                        objJson.getString(Constant.MATRIMONY_AGE)
                                    objBean!!.matrimonyEducation =
                                        objJson.getString(Constant.MATRIMONY_EDUCATION)
                                    objBean!!.matrimonyCareer =
                                        objJson.getString(Constant.MATRIMONY_CAREER)
                                    objBean!!.matrimonySalary =
                                        objJson.getString(Constant.MATRIMONY_SALARY)
                                    objBean!!.matrimonyDesc =
                                        objJson.getString(Constant.MATRIMONY_DESC)
                                    objBean!!.matrimonyHoroscope =
                                        objJson.getString(Constant.MATRIMONY_HOROSCOPE)
                                    objBean!!.matrimonyPartnerExpect =
                                        objJson.getString(Constant.MATRIMONY_PARTNER_EXPECT)
                                    objBean!!.matrimonyPersonName =
                                        objJson.getString(Constant.MATRIMONY_PERSON_NAME)
                                    objBean!!.matrimonyPhoneNumber =
                                        objJson.getString(Constant.MATRIMONY_PHONE_NUMBER)
                                    objBean!!.matrimonyPhoneNumber2 =
                                        objJson.getString(Constant.MATRIMONY_PHONE_NUMBER2)
                                    objBean!!.matrimonyImage =
                                        objJson.getString(Constant.MATRIMONY_IMAGE)
                                    objBean!!.matrimonyLogo =
                                        objJson.getString(Constant.MATRIMONY_LOGO)
                                    objBean!!.matrimonyImage2 =
                                        objJson.getString(Constant.MATRIMONY_IMAGE2)
                                    objBean!!.matrimonyImage3 =
                                        objJson.getString(Constant.MATRIMONY_IMAGE3)
                                    objBean!!.matrimonyImage4 =
                                        objJson.getString(Constant.MATRIMONY_IMAGE4)
                                    objBean!!.matrimonySDate =
                                        objJson.getString(Constant.MATRIMONY_START_DATE)
                                    objBean!!.matrimonyEDate =
                                        objJson.getString(Constant.MATRIMONY_END_DATE)
                                    objBean!!.url = objJson.getString("url")
                                    objBean!!.categoryName =
                                        objJson.getString(Constant.CATEGORY_NAME)
                                    objBean!!.city = objJson.getString(Constant.CITY_NAME)
                                    objBean!!.cid = objJson.getString(Constant.CATEGORY_CID)
                                    images.add(
                                        SlideModel(
                                            objJson.getString(Constant.MATRIMONY_IMAGE),
                                            ScaleTypes.FIT
                                        )
                                    )
                                    images.add(
                                        SlideModel(
                                            objJson.getString(Constant.MATRIMONY_IMAGE2),
                                            ScaleTypes.FIT
                                        )
                                    )
                                    images.add(
                                        SlideModel(
                                            objJson.getString(Constant.MATRIMONY_IMAGE3),
                                            ScaleTypes.FIT
                                        )
                                    )
                                    images.add(
                                        SlideModel(
                                            objJson.getString(Constant.MATRIMONY_IMAGE4),
                                            ScaleTypes.FIT
                                        )
                                    )
                                    objBean!!.matrimonyBanner = images
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
        matrimonyName!!.text = objBean!!.matrimonyName
        matrimonyCaste!!.text = objBean!!.categoryName
        text_gender!!.text = objBean!!.matrimonyGender
        matrimonyAge!!.text = objBean!!.matrimonyAge
        matrimonyReligion!!.text = objBean!!.matrimonyReligion
        text_city!!.text = objBean!!.city
        matrimonySalary!!.text = objBean!!.matrimonySalary
        matrimonyJob!!.text = objBean!!.matrimonyCareer
        matrimonyDate!!.text = objBean!!.matrimonySDate
        last_date!!.text = objBean!!.matrimonyEDate
        text_matrimony_id!!.text = "MM" + objBean!!.id


        if (!objBean.matrimonyArea?.let { isNullOrEmpty(it) }!!){
            text_area.text = objBean.matrimonyArea
            areaLinear.visibility = View.VISIBLE
        } else {
            areaLinear.visibility = View.GONE
        }

        if (!objBean.matrimonyPhoneNumber2?.let { isNullOrEmpty(it) }!!){
            val content2 = SpannableString(objBean.matrimonyPhoneNumber2)
            content2.setSpan(UnderlineSpan(), 0, content2.length, 0)
            matrimonyPhone2!!.text = content2
            pno2Linear.visibility = View.VISIBLE
        } else {
            pno2Linear.visibility = View.GONE
        }


        val content = SpannableString(objBean!!.matrimonyPhoneNumber)
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        matrimonyPhone!!.text = content
        whatsapp_num!!.text = content

        image!!.setImageList(objBean!!.matrimonyBanner!!, ScaleTypes.FIT)
        setupViewPager(viewPager)
        tabLayout!!.setupWithViewPager(viewPager)
        btn_whats!!.setOnClickListener { view: View? ->
            val phone = "91" + objBean!!.matrimonyPhoneNumber!!.replace("+91", "")
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
            val phone = "91" + objBean!!.matrimonyPhoneNumber!!.replace("+91", "")
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
    }

    private fun setupViewPager(viewPager: ViewPager?) {
        val adapter = ViewPagerAdapter(
            supportFragmentManager
        )
        adapter.addFragment(newInstance(objBean), getString(R.string.tab_matrimony_details))
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

    private fun isNullOrEmpty(value: String): Boolean {
        return value.equals("null") or value.isEmpty()
    }

    fun showToast(msg: String?) {
        Toast.makeText(this@MatrimonyDetailsActivity, msg, Toast.LENGTH_SHORT).show()
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
                        ${objBean!!.matrimonyName}
                        ${getString(R.string.job_phone_lbl)}${objBean!!.matrimonyPhoneNumber}
                        ${getString(R.string.job_address_lbl)}${objBean!!.city}
                        
                        "View details here: https://www.sundigitalindiajobs.com/matrimony?id=${objBean.id}
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
//        startActivity(new Intent(
//                Intent.ACTION_VIEW,
//                Uri.parse(addHttp(objBean.getWebsiteLink()))));
    }

    private fun openEmail() {
//        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
//                "mailto", objBean.getMatrimonyMail(), null));
//        emailIntent
//                .putExtra(Intent.EXTRA_SUBJECT, "Apply for the post " + objBean.getMatrimonyDesignation());
//        startActivity(Intent.createChooser(emailIntent, "Send suggestion..."));
    }

    protected fun addHttp(string1: String): String {
        // TODO Auto-generated method stub
        return if (string1.startsWith("http://")) string1 else "http://$string1"
    }

    private fun dialNumber(phone: String?) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null))
        startActivity(intent)
    }

    private fun firstFavourite() {}
    override fun onBackPressed() {
        if (isFromNotification) {
            val intent = Intent(this@MatrimonyDetailsActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        } else {
            super.onBackPressed()
        }
    }
}