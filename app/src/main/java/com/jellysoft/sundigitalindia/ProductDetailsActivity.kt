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
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.db.DatabaseHelper
import com.example.fragment.ProductDetailsFragment.Companion.newInstance
import com.example.item.ItemProduct
import com.example.util.*
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.android.synthetic.main.fragment_product_details.*
import org.json.JSONException
import org.json.JSONObject

class ProductDetailsActivity : AppCompatActivity() {
    lateinit var mProgressBar: ProgressBar
    lateinit var lyt_not_found: LinearLayout
    lateinit var areaLinear: LinearLayout
    lateinit var mailLinear: LinearLayout
    lateinit var pno2Linear: LinearLayout
    lateinit var addLinear: LinearLayout
    lateinit var objBean: ItemProduct
    lateinit var productTitle: TextView
    lateinit var companyTitle: TextView
    lateinit var productDate: TextView
    lateinit var text_product_address: TextView
    lateinit var productAddress: TextView
    lateinit var productPhone: TextView
    lateinit var productPhone2: TextView
    lateinit var productMail: TextView
    lateinit var text_product_id: TextView
    lateinit var text_product_category: TextView
    lateinit var text_city: TextView
    lateinit var last_date: TextView
    lateinit var whatsapp_num: TextView
    lateinit var mail_id: TextView
    lateinit var text_area: TextView
    lateinit var text_price: TextView
    lateinit var text_selling_price: TextView
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
    lateinit var videoView: WebView
    var isJobSaved = false
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_product)
        IsRTL.ifSupported(this)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }
        objBean = ItemProduct()
        val data: Uri? = intent.data

        val i = intent
        Id = i.getStringExtra("Id").toString()
        if (i.hasExtra("isNotification")) {
            isFromNotification = true
        }

        if(data != null ) {
            Id = data.getQueryParameter("id").toString()
        }
        title = getString(R.string.tool_product_details)
        databaseHelper = DatabaseHelper(applicationContext)
        MyApp = MyApplication.getInstance()
        mProgressBar = findViewById(R.id.progressBar1)
        lyt_not_found = findViewById(R.id.lyt_not_found)
        videoView = findViewById(R.id.videoView)
        mAdViewLayout = findViewById(R.id.adView)
        btn_whats = findViewById(R.id.btn_whats)

        areaLinear = findViewById(R.id.areaLinear)
        mailLinear = findViewById(R.id.mailLinear)
        pno2Linear = findViewById(R.id.pno2Linear)
        addLinear = findViewById(R.id.addLinear)

        image = findViewById(R.id.image_slider)
        productTitle = findViewById(R.id.text_job_title)
        companyTitle = findViewById(R.id.text_job_company)
        productDate = findViewById(R.id.text_job_date)
        productAddress = findViewById(R.id.text_job_address)
        productPhone = findViewById(R.id.text_phone)
        productPhone2 = findViewById(R.id.text_phone2)
        productMail = findViewById(R.id.text_email)
        lytParent = findViewById(R.id.lytParent)
        text_product_id = findViewById(R.id.text_job_id)
        btnApplyJob = findViewById(R.id.btn_apply_job)
        viewPager = findViewById(R.id.viewpager)
        tabLayout = findViewById(R.id.tabs)
        tabLayout.setupWithViewPager(viewPager)
        text_product_category = findViewById(R.id.text_job_category)
        text_city = findViewById(R.id.text_city)
        text_area = findViewById(R.id.text_area)
        text_price = findViewById(R.id.text_price)
        text_selling_price = findViewById(R.id.text_selling_price)
        last_date = findViewById(R.id.last_date)
        whatsapp_num = findViewById(R.id.whatsapp_num)
        mail_id = findViewById(R.id.text_email)
        if (NetworkUtils.isConnected(this@ProductDetailsActivity)) {
            details
        } else {
            showToast(getString(R.string.conne_msg1))
        }
        productPhone.setOnClickListener(View.OnClickListener { v: View? ->
            dialNumber(
                objBean!!.productPhoneNumber
            )
        })
        productPhone2.setOnClickListener(View.OnClickListener { v: View? ->
            dialNumber(
                objBean!!.productPhoneNumber2
            )
        })
        btnApplyJob.setOnClickListener(View.OnClickListener { v: View? ->
            dialNumber(
                objBean!!.productPhoneNumber
            )
        })
    }

    private val details: Unit
        private get() {
            val client = AsyncHttpClient()
            val params = RequestParams()
            val jsObj = Gson().toJsonTree(API()) as JsonObject
            jsObj.addProperty("method_name", "get_single_product")
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
                    Log.d("result", result)
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
                                    objBean!!.id = objJson.getString(Constant.PRODUCT_ID)
                                    objBean!!.productName = objJson.getString(Constant.PRODUCT_NAME)
                                    objBean!!.productAddress =
                                        objJson.getString(Constant.PRODUCT_ADDRESS)
                                    objBean!!.productImage =
                                        objJson.getString(Constant.PRODUCT_IMAGE)
                                    objBean!!.productArea = objJson.getString(Constant.PRODUCT_AREA)
                                    objBean!!.productPrice =
                                        objJson.getString(Constant.PRODUCT_PRICE)
                                    objBean!!.productSellingPrice =
                                        objJson.getString(Constant.PRODUCT_SELLING_PRICE)
                                    objBean!!.productPhoneNumber =
                                        objJson.getString(Constant.PRODUCT_PHONE_NO)
                                    objBean!!.productPhoneNumber2 =
                                        objJson.getString(Constant.PRODUCT_PHONE_NO2)
                                    objBean!!.productMail = objJson.getString(Constant.PRODUCT_MAIL)
                                    objBean!!.productNegotiable =
                                        objJson.getString(Constant.PRODUCT_NEGOTIABLE)
                                    objBean!!.productCompanyWebsite =
                                        objJson.getString(Constant.PRODUCT_SITE)
                                    objBean!!.WebsiteLink = objJson.getString(Constant.WEBSITE_LINK)
                                    objBean!!.productDesc = objJson.getString(Constant.PRODUCT_DESC)
                                    objBean!!.productDoc = objJson.getString(Constant.PRODUCT_DOC)
                                    objBean!!.productDate =
                                        objJson.getString(Constant.PRODUCT_START_DATE)
                                    objBean!!.pLate = objJson.getString(Constant.PRODUCT_END_DATE)
                                    objBean!!.City = objJson.getString(Constant.CITY_NAME)
                                    objBean!!.url = objJson.getString("url")
                                    objBean!!.productMail = objJson.getString(Constant.PRODUCT_MAIL)
                                    objBean!!.productCategoryName =
                                        objJson.getString(Constant.CATEGORY_NAME)
                                    objBean.productType = objJson.getString(Constant.PRODUCT_TYPE)

                                    images.add(SlideModel(
                                        objJson.getString(Constant.PRODUCT_IMAGE).replace(" ", "%20"),
                                        ScaleTypes.FIT)
                                    )

                                    if (!isNullOrEmpty(objJson.getString(Constant.PRODUCT_IMAGE2))) {
                                        images.add(SlideModel(
                                            objJson.getString(Constant.PRODUCT_IMAGE2).replace(" ", "%20"),
                                            ScaleTypes.FIT
                                        )
                                        )
                                    }

                                    if (!isNullOrEmpty(objJson.getString(Constant.PRODUCT_IMAGE3))) {
                                        images.add(SlideModel(
                                            objJson.getString(Constant.PRODUCT_IMAGE3).replace(" ", "%20"),
                                            ScaleTypes.FIT
                                        )
                                        )
                                    }

                                    if (!isNullOrEmpty(objJson.getString(Constant.PRODUCT_IMAGE4))) {
                                        images.add(SlideModel(
                                            objJson.getString(Constant.PRODUCT_IMAGE4).replace(" ", "%20"),
                                            ScaleTypes.FIT
                                        )
                                        )
                                    }

                                    if (!isNullOrEmpty(objJson.getString(Constant.PRODUCT_IMAGE5))) {
                                        images.add(SlideModel(
                                            objJson.getString(Constant.PRODUCT_IMAGE5).replace(" ", "%20"),
                                            ScaleTypes.FIT
                                        )
                                        )
                                    }

                                    objBean!!.productBanner = images
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

    @SuppressLint("SetTextI18n")
    private fun setResult() {
        firstFavourite()
        productTitle.text = objBean!!.productName
        text_product_id!!.text = "PD" + objBean!!.id
        text_product_category!!.text = objBean!!.productCategoryName
        text_city!!.text = objBean!!.City

        if (!objBean.productAddress?.let { isNullOrEmpty(it) }!!){
            productAddress.text = objBean.productAddress
            addLinear.visibility = View.VISIBLE
        } else {
            addLinear.visibility = View.GONE
        }

        if (!objBean.productArea?.let { isNullOrEmpty(it) }!!){
            text_area.text = objBean.productArea
            areaLinear.visibility = View.VISIBLE
        } else {
            areaLinear.visibility = View.GONE
        }

        if (!objBean.productPhoneNumber2?.let { isNullOrEmpty(it) }!!){
            val content2 = SpannableString(objBean.productPhoneNumber2)
            content2.setSpan(UnderlineSpan(), 0, content2.length, 0)
            productPhone2.text = content2
            pno2Linear.visibility = View.VISIBLE
        } else {
            pno2Linear.visibility = View.GONE
        }

        if (!objBean.productMail?.let { isNullOrEmpty(it) }!!){
            val content3 = SpannableString(objBean.productMail)
            content3.setSpan(UnderlineSpan(), 0, content3.length, 0)
            mail_id.text = content3
            mailLinear.visibility = View.VISIBLE
        } else {
            mailLinear.visibility = View.GONE
        }

        text_price.text = "Rs. " + objBean.productPrice!!.replace(".00", "")
        text_selling_price.text = "Rs. " + objBean.productSellingPrice!!.replace(".00", "")
        companyTitle.text = objBean.productCompanyName
        productDate.text = objBean.productDate
        last_date.text = objBean.pLate

        val content = SpannableString(objBean!!.productPhoneNumber)
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        productPhone!!.text = content
        whatsapp_num!!.text = content



        objBean.productBanner?.let {
            image.setImageList(it, ScaleTypes.FIT)
        }

        if (objBean.url != null && !objBean.url!!.isEmpty()) {
            videoView!!.settings.javaScriptEnabled = true
            videoView.settings.pluginState = WebSettings.PluginState.ON
            videoView.loadUrl(objBean.url!!)
            videoView.webChromeClient = WebChromeClient()
        } else {
            videoView.visibility = View.GONE
        }
        setupViewPager(viewPager)
        tabLayout.setupWithViewPager(viewPager)
        btn_whats.setOnClickListener {
            val phone = "91" + objBean.productPhoneNumber!!.replace("+91", "")
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
        whatsapp_num.setOnClickListener {
            val phone = "91" + objBean.productPhoneNumber!!.replace("+91", "")
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
        mail_id.setOnClickListener { openEmail() }
    }

    private fun setupViewPager(viewPager: ViewPager?) {
        val adapter = ViewPagerAdapter(
            supportFragmentManager
        )
        adapter.addFragment(newInstance(objBean), getString(R.string.tab_product_details))
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
        Toast.makeText(this@ProductDetailsActivity, msg, Toast.LENGTH_SHORT).show()
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
                        ${objBean!!.productName}
                        ${getString(R.string.job_company_lbl)}${objBean!!.productCompanyName}
                        ${getString(R.string.job_designation_lbl)}${objBean!!.productDesignation}
                        ${getString(R.string.job_phone_lbl)}${objBean!!.productPhoneNumber}
                        ${getString(R.string.job_address_lbl)}${objBean!!.City}
                        
                        "View details here: https://www.sundigitalindiajobs.com/product?id=${objBean.id}
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
                Uri.parse(addHttp(objBean!!.productCompanyWebsite))
            )
        )
    }

    private fun openEmail() {
        val emailIntent = Intent(
            Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", objBean!!.productMail, null
            )
        )
        emailIntent
            .putExtra(Intent.EXTRA_SUBJECT, "Apply for the post " + objBean!!.productDesignation)
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

    private fun isNullOrEmpty(value: String): Boolean {
        return value.equals("null") or value.isEmpty()
    }

    private fun firstFavourite() {}
    override fun onBackPressed() {
        finish()
    }
}