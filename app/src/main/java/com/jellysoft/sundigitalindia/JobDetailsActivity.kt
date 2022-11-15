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
import android.view.KeyEvent
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
import com.example.fragment.JobDetailsFragment.Companion.newInstance
import com.example.item.ItemJob
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
import kotlinx.android.synthetic.main.fragment_job_details.*
import org.json.JSONException
import org.json.JSONObject

class JobDetailsActivity : AppCompatActivity() {
    private lateinit var mProgressBar: ProgressBar
    private lateinit var lyt_not_found: LinearLayout
    private lateinit var objBean: ItemJob
    private lateinit var jobTitle: TextView
    private lateinit var companyTitle: TextView
    private lateinit var jobDate: TextView
    private lateinit var jobDesignation: TextView
    private lateinit var salary: TextView
    private lateinit var work_time: TextView
    private lateinit var jobAddress: TextView
    private lateinit var jobVacancy: TextView
    private lateinit var jobPhone: TextView
    private lateinit var jobMail: TextView
    private lateinit var jobWebsite: TextView
    private lateinit var text_job_id: TextView
    private lateinit var text_job_category: TextView
    private lateinit var text_city: TextView
    private lateinit var last_date: TextView
    private lateinit var whatsapp_num: TextView
    private lateinit var mail_id: TextView
    private lateinit var text_area: TextView
    private lateinit var jobPhone2: TextView
    private lateinit var jobTimeLabel: TextView
    private lateinit var areaLabel: TextView
    private lateinit var emailLabel: TextView
    private lateinit var pno2Label: TextView
    private lateinit var mailLinear: LinearLayout
    private lateinit var  pno2Linear: LinearLayout
    private lateinit var  jobTimeLinear: LinearLayout
    private lateinit var  areaLinear: LinearLayout
    private lateinit var addLabel: TextView
    private lateinit var image: ImageView
    private lateinit var Id: String
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var btnSave: Button
    private lateinit var mAdViewLayout: LinearLayout
    private lateinit var btnApplyJob: Button
    private lateinit var btn_whats: Button
    private lateinit var MyApp: MyApplication
    private lateinit var lytParent: CoordinatorLayout
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager
    private lateinit var videoView: WebView
    var isJobSaved = false
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (event.action == KeyEvent.KEYCODE_BACK) {
            finish()
        }
        return super.onKeyDown(keyCode, event)
    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        IsRTL.ifSupported(this)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }
        objBean = ItemJob()
        val data: Uri? = intent.data

        val i = intent
        Id = i.getStringExtra("Id").toString()

        if(data != null ) {
            Id = data.getQueryParameter("id").toString()
        }

        title = getString(R.string.tool_job_details)
        databaseHelper = DatabaseHelper(applicationContext)
        MyApp = MyApplication.getInstance()
        mProgressBar = findViewById(R.id.progressBar1)
        lyt_not_found = findViewById(R.id.lyt_not_found)
        videoView = findViewById(R.id.videoView)
        mailLinear = findViewById(R.id.mailLinear)
        pno2Linear = findViewById(R.id.pno2Linear)
        jobTimeLinear = findViewById(R.id.jobTimeLinear)
        areaLinear = findViewById(R.id.areaLinear)
        mAdViewLayout = findViewById(R.id.adView)
        jobTimeLabel = findViewById(R.id.jobTimeLabel)
        areaLabel = findViewById(R.id.areaLabel)
        emailLabel = findViewById(R.id.emailLabel)
        pno2Label = findViewById(R.id.pno2Label)
        addLabel = findViewById(R.id.addLabel)
        btn_whats = findViewById(R.id.btn_whats)
        image = findViewById(R.id.image)
        salary = findViewById(R.id.salary)
        jobTitle = findViewById(R.id.text_job_title)
        text_area = findViewById(R.id.text_area)
        companyTitle = findViewById(R.id.text_job_company)
        jobDate = findViewById(R.id.text_job_date)
        jobDesignation = findViewById(R.id.text_job_designation)
        work_time = findViewById(R.id.work_time)
        jobAddress = findViewById(R.id.text_job_address)
        jobPhone = findViewById(R.id.text_phone)
        jobPhone2 = findViewById(R.id.text_phone2)
        jobMail = findViewById(R.id.text_email)
        jobVacancy = findViewById(R.id.text_vacancy)
        lytParent = findViewById(R.id.lytParent)
        text_job_id = findViewById(R.id.text_job_id)
        btnApplyJob = findViewById(R.id.btn_apply_job)
        viewPager = findViewById(R.id.viewpager)
        tabLayout = findViewById(R.id.tabs)
        tabLayout.setupWithViewPager(viewPager)
        text_job_category = findViewById(R.id.text_job_category)
        text_city = findViewById(R.id.text_city)
        last_date = findViewById(R.id.last_date)
        whatsapp_num = findViewById(R.id.whatsapp_num)
        mail_id = findViewById(R.id.text_email)
        if (NetworkUtils.isConnected(this@JobDetailsActivity)) {
            details
        } else {
            showToast(getString(R.string.conne_msg1))
        }
        jobPhone.setOnClickListener(View.OnClickListener { v: View? ->
            dialNumber(
                objBean!!.jobPhoneNumber
            )
        })
        jobPhone2.setOnClickListener(View.OnClickListener { v: View? ->
            dialNumber(
                objBean!!.jobPhoneNumber2
            )
        })
        btnApplyJob.setOnClickListener(View.OnClickListener { v: View? ->
            dialNumber(
                objBean!!.jobPhoneNumber
            )
        })
    }

    private val details: Unit
        private get() {
            val client = AsyncHttpClient()
            val params = RequestParams()
            val jsObj = Gson().toJsonTree(API()) as JsonObject
            jsObj.addProperty("method_name", "get_single_job")
            jsObj.addProperty("job_id", Id)
            jsObj.addProperty("user_id", UserUtils.getUserId())
            params.put("data", API.toBase64(jsObj.toString()))
            client.post(Constant.API_URL, params, object : AsyncHttpResponseHandler() {
                override fun onStart() {
                    super.onStart()
                    mProgressBar!!.visibility = View.VISIBLE
                    lytParent.visibility = View.GONE
                }

                override fun onSuccess(
                    statusCode: Int,
                    headers: Array<Header>,
                    responseBody: ByteArray
                ) {
                    mProgressBar!!.visibility = View.GONE
                    lytParent!!.visibility = View.VISIBLE
                    val result = String(responseBody)
                    Log.d("result", result)
                    try {
                        val mainJson = JSONObject(result)
                        isJobSaved = mainJson.getBoolean(Constant.JOB_ALREADY_SAVED)
                        val jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME)
                        if (jsonArray.length() > 0) {
                            var objJson: JSONObject
                            for (i in 0 until jsonArray.length()) {
                                objJson = jsonArray.getJSONObject(i)
                                if (objJson.has(Constant.STATUS)) {
                                    lyt_not_found!!.visibility = View.VISIBLE
                                } else {
                                    objBean!!.id = objJson.getString(Constant.JOB_ID)
                                    objBean!!.jobName = objJson.getString(Constant.JOB_NAME)
                                    objBean!!.jobCompanyName =
                                        objJson.getString(Constant.JOB_COMPANY_NAME)
                                    objBean!!.jobDesignation =
                                        objJson.getString(Constant.JOB_DESIGNATION)
                                    objBean!!.jobAddress = objJson.getString(Constant.JOB_ADDRESS)
                                    objBean!!.jobImage = objJson.getString(Constant.JOB_IMAGE)
                                    objBean!!.jobArea = objJson.getString(Constant.JOB_AREA)
                                    objBean!!.jobVacancy = objJson.getString(Constant.JOB_VACANCY)
                                    objBean!!.jobPhoneNumber =
                                        objJson.getString(Constant.JOB_PHONE_NO)
                                    objBean!!.jobPhoneNumber2 =
                                        objJson.getString(Constant.JOB_PHONE_NO2)
                                    objBean!!.jobMail = objJson.getString(Constant.JOB_MAIL)
                                    objBean!!.jobCompanyWebsite =
                                        objJson.getString(Constant.JOB_SITE)
                                    objBean!!.jobWebsite = objJson.getString(Constant.WEBSITE_LINK)
                                    objBean!!.jobDesc = objJson.getString(Constant.JOB_DESC)
                                    objBean!!.jobSkill = objJson.getString(Constant.JOB_SKILL)
                                    objBean!!.jobQualification =
                                        objJson.getString(Constant.JOB_QUALIFICATION)
                                    objBean!!.jobSalary = objJson.getString(Constant.JOB_SALARY)
                                    objBean!!.jobTime = objJson.getString(Constant.JOB_TIME)
                                    objBean!!.age = objJson.getString("job_age")
                                    objBean!!.sex = objJson.getString("job_sex")
                                    objBean!!.jobPdf = objJson.getString(Constant.JOB_PDF)
                                    objBean!!.jobDate = objJson.getString(Constant.JOB_DATE)
                                    objBean!!.setpLate(objJson.getString("job_date1"))
                                    objBean!!.marital = objJson.getString("job_marital")
                                    objBean!!.city = objJson.getString(Constant.CITY_NAME)
                                    objBean!!.url = objJson.getString("url")
                                    objBean!!.jobMail = objJson.getString(Constant.JOB_MAIL)
                                    objBean!!.jobCategoryName =
                                        objJson.getString(Constant.CATEGORY_NAME)
                                    objBean!!.jobExperience = objJson.getString(Constant.JOB_EXP)
                                    objBean!!.jobType = objJson.getString(Constant.JOB_TYPE)
                                }
                            }
                            setResult()
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

    private fun setResult() {
        firstFavourite()
        salary.text = objBean.jobSalary
        jobTitle!!.text = objBean.jobName
        text_job_id.text = "JD" + objBean!!.id
        text_job_category.text = objBean.jobCategoryName
        text_city.text = objBean.city
        companyTitle.text = objBean.jobCompanyName
        jobDate.text = objBean.jobDate
        last_date.text = objBean.getpLate()

        if(!isNullOrEmpty(objBean.jobAddress)) {
            jobAddress.text = objBean.jobAddress
            addLabel.visibility = View.VISIBLE
        } else {
            addLabel.visibility = View.GONE
        }

        if(!isNullOrEmpty(objBean.jobTime)) {
            work_time.text = objBean.jobTime
            jobTimeLinear.visibility = View.VISIBLE
        } else {
            jobTimeLinear.visibility = View.GONE
        }

        if(!isNullOrEmpty(objBean.jobMail)) {
            jobMail.text = objBean.jobMail
            mailLinear.visibility = View.VISIBLE
        } else {
            mailLinear.visibility = View.GONE
        }

        if(!isNullOrEmpty(objBean!!.jobArea)) {
            text_area!!.text = objBean!!.jobArea
            areaLinear.visibility = View.VISIBLE
        } else {
            areaLinear.visibility = View.GONE
        }

        if(!isNullOrEmpty(objBean!!.jobPhoneNumber2)) {
            val content2 = SpannableString(objBean!!.jobPhoneNumber2)
            content2.setSpan(UnderlineSpan(), 0, content2.length, 0)
            jobPhone2.text = content2
            pno2Linear.visibility = View.VISIBLE
        } else {
            pno2Linear.visibility = View.GONE
        }

        val content = SpannableString(objBean!!.jobPhoneNumber)
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        jobPhone!!.text = content
        whatsapp_num!!.text = content
        jobDesignation!!.text = objBean!!.jobDesignation
        jobVacancy!!.text = objBean!!.jobVacancy
        val content3 = SpannableString(objBean!!.jobMail)
        content3.setSpan(UnderlineSpan(), 0, content3.length, 0)
        mail_id!!.text = content3
        Picasso.get().load(objBean!!.jobImage).into(image)
        if (objBean!!.url != null && !objBean!!.url.isEmpty()) {
            videoView!!.settings.javaScriptEnabled = true
            videoView!!.settings.pluginState = WebSettings.PluginState.ON
            videoView!!.loadUrl(objBean!!.url)
            videoView!!.webChromeClient = WebChromeClient()
        } else {
            videoView!!.visibility = View.GONE
        }
        setupViewPager(viewPager)
        tabLayout!!.setupWithViewPager(viewPager)
        btn_whats!!.setOnClickListener { view: View? ->
            val phone = "91" + objBean!!.jobPhoneNumber.replace("+91", "")
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
            val phone = "91" + objBean!!.jobPhoneNumber.replace("+91", "")
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
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(newInstance(objBean), getString(R.string.tab_job_details))
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
        Toast.makeText(this@JobDetailsActivity, msg, Toast.LENGTH_SHORT).show()
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
                        ${objBean!!.jobName}
                        ${getString(R.string.job_company_lbl)}${objBean!!.jobCompanyName}
                        ${getString(R.string.job_designation_lbl)}${objBean!!.jobDesignation}
                        ${getString(R.string.job_phone_lbl)}${objBean!!.jobPhoneNumber}
                        ${getString(R.string.job_address_lbl)}${objBean!!.city}
                        
                        "View details here: https://www.sundigitalindiajobs.com/job?id=${objBean.id}
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
                Uri.parse(addHttp(objBean!!.jobCompanyWebsite))
            )
        )
    }

    private fun openEmail() {
        val emailIntent = Intent(
            Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", objBean!!.jobMail, null
            )
        )
        emailIntent
            .putExtra(Intent.EXTRA_SUBJECT, "Apply for the post " + objBean!!.jobDesignation)
        startActivity(Intent.createChooser(emailIntent, "Send suggestion..."))
    }

    protected fun addHttp(string1: String): String {
        // TODO Auto-generated method stub
        return if (string1.startsWith("http://")) string1 else "http://$string1"
    }

    private fun dialNumber(phone: String) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null))
        startActivity(intent)
    }

    private fun firstFavourite() {}
    override fun onBackPressed() {
        finish()
    }
    private fun isNullOrEmpty(value: String): Boolean {
        return value.equals("null") or value.isEmpty()
    }
}
