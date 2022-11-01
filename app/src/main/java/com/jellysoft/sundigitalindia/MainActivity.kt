package com.jellysoft.sundigitalindia

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.ViewPager
import com.example.adapter.MyAdapter
import com.example.fragment.FilterSearchFragment
import com.example.item.ItemAdmin
import com.example.util.API
import com.example.util.Constant
import com.example.util.IsRTL
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import com.squareup.picasso.Picasso
import cz.msebera.android.httpclient.Header
import de.hdodenhof.circleimageview.CircleImageView
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var fragmentManager: FragmentManager
    private lateinit var navigationView: NavigationView
    lateinit var toolbar: Toolbar
    lateinit var MyApp: MyApplication
    lateinit var phone: TextView
    lateinit var phone1: TextView
    lateinit var viewer: TextView
    private var doubleBackToExitPressedOnce = false
    private lateinit var versionName: String
    lateinit var tabLayout: TabLayout
    lateinit var viewPager: ViewPager
    lateinit var adapter: MyAdapter
    lateinit var vi: String

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val data: Uri? = intent.data

        if(data != null ) {
            Log.d("data2", data.getQueryParameter("id").toString())
        } else {
            Log.d("data2", "null")
        }

        IsRTL.ifSupported(this)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        MyApp = MyApplication.getInstance()
        fragmentManager = supportFragmentManager
        navigationView = findViewById(R.id.navigation_view)
        drawerLayout = findViewById(R.id.drawer_layout)
        admin
        phone = findViewById(R.id.phone)
        phone1 = findViewById(R.id.phone1)
        phone.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:" + phone.getText().toString())
            startActivity(intent)
        }
        phone1.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:" + phone1.getText().toString())
            startActivity(intent)
        }
        try {
            versionName = packageManager.getPackageInfo(packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        val mAdViewLayout = findViewById<LinearLayout>(R.id.adView)
        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.menu_home)))
        tabLayout.addTab(tabLayout.newTab().setText("வேலை வாய்ப்பு"))
        tabLayout.addTab(tabLayout.newTab().setText("பொருட்கள்"))
        tabLayout.addTab(tabLayout.newTab().setText("வேலை ஆட்கள்"))
        tabLayout.addTab(tabLayout.newTab().setText("திருமண வரன்கள்"))
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.menu_two)))
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.menu_one)))
        tabLayout.addTab(tabLayout.newTab().setText("Products Categories"))
        tabLayout.addTab(tabLayout.newTab().setText("Products City"))
        tabLayout.addTab(tabLayout.newTab().setText("சேவை வகைகள்"))
        tabLayout.addTab(tabLayout.newTab().setText("சேவை ஊர்"))
        tabLayout.addTab(tabLayout.newTab().setText("மணமகள்"))
        tabLayout.addTab(tabLayout.newTab().setText("மணமகன்"))
        tabLayout.addTab(tabLayout.newTab().setText("ஊர்"))
        tabLayout.addTab(tabLayout.newTab().setText("மணமகள்"))
        tabLayout.addTab(tabLayout.newTab().setText("மணமகன்"))
        tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        adapter = MyAdapter(supportFragmentManager)
        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        navigationView.setNavigationItemSelectedListener(NavigationView.OnNavigationItemSelectedListener { item ->
            drawerLayout.closeDrawers()
            when (item.itemId) {
                R.id.menu_go_home -> {
                    viewPager.currentItem = 0
                    return@OnNavigationItemSelectedListener false
                }
                R.id.menu_go_job_opp -> {
                    viewPager.currentItem = 1
                    return@OnNavigationItemSelectedListener false
                }
                R.id.menu_go_product -> {
                    viewPager.currentItem = 2
                    return@OnNavigationItemSelectedListener false
                }
                R.id.menu_go_service -> {
                    viewPager.currentItem = 3
                    return@OnNavigationItemSelectedListener false
                }
                R.id.menu_go_matrimony -> {
                    viewPager.currentItem = 4
                    return@OnNavigationItemSelectedListener false
                }
                R.id.menu_go_contact -> {
                    val intent2 = Intent(this@MainActivity, contact::class.java)
                    startActivity(intent2)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.menu_go_setting -> {
                    val intent5 = Intent(this@MainActivity, devcontact::class.java)
                    startActivity(intent5)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.menu_go_privacy -> {
                    val intent = Intent(this@MainActivity, privacy::class.java)
                    startActivity(intent)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.menu_go_about -> {
                    val intent7 = Intent(this@MainActivity, about::class.java)
                    startActivity(intent7)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.menu_go_logout -> {
                    onBackPressed()
                    finish()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.menu_go_enquiry -> {
                    val intent8 = Intent(this@MainActivity, provider::class.java)
                    startActivity(intent8)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.menu_go_share -> {
                    val text = """
                        சன் டிஜிட்டல் இந்தியா வேலை வாய்ப்பு
                        வேலை வாய்ப்புகளை பயன்படுத்திக்கொள்ள இப்போதே
                        அப்பை டவுன்லோட் செய்யுங்கள்  https://play.google.com/store/apps/details?id=com.jellysoft.sundigitalindia
                        """.trimIndent()
                    val sendIntent = Intent()
                    sendIntent.action = Intent.ACTION_SEND
                    sendIntent.putExtra(
                        Intent.EXTRA_TEXT,
                        text
                    )
                    sendIntent.type = "text/plain"
                    startActivity(sendIntent)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })
        val actionBarDrawerToggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.drawer_open,
            R.string.drawer_close
        ) {
            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
            }

            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                viewer = drawerView.findViewById(R.id.views)
                viewer.text = vi
            }
        }
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        toolbar.setNavigationIcon(R.drawable.ic_side_nav)
    }

    fun loadFrag(f1: Fragment?, name: String?, fm: FragmentManager) {
        for (i in 0 until fm.backStackEntryCount) {
            fm.popBackStack()
        }
        val ft = fm.beginTransaction()
        ft.replace(R.id.Container, f1!!, name)
        ft.commit()
        setToolbarTitle(name)
    }

    fun setToolbarTitle(Title: String?) {
        if (supportActionBar != null) {
            supportActionBar!!.title = Title
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.search -> {
                val filterSearchFragment = FilterSearchFragment()
                filterSearchFragment.show(fragmentManager!!, "Square")
            }
            else -> return super.onOptionsItemSelected(menuItem)
        }
        return true
    }

    private fun Logout() {
        AlertDialog.Builder(this@MainActivity)
            .setTitle(getString(R.string.menu_logout))
            .setMessage(getString(R.string.logout_msg))
            .setPositiveButton(android.R.string.yes) { dialog, which ->
                MyApp!!.saveIsLogin(false)
                val intent = Intent(applicationContext, SignInActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            }
            .setNegativeButton(android.R.string.no) { dialog, which ->
                // do nothing
            } //  .setIcon(R.drawable.ic_logout)
            .show()
    }

    override fun onResume() {
        super.onResume()
        if (navigationView != null) {
            setHeader()
        }
    }

    private fun setHeader() {
        if (MyApp.isLogin) {
            val header = navigationView.getHeaderView(0)
            val imageUser = header.findViewById<CircleImageView>(R.id.header_image)
            if (MyApp.userImage.isNotEmpty()) {
                Picasso.get().load(MyApp.userImage).into(imageUser)
            }
        } else {
        }
    }

    override fun onBackPressed() {
        if(viewPager.currentItem == 0) {
            finish()
        }else {
            viewPager.currentItem = 0
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else if (fragmentManager.backStackEntryCount != 0) {
                val tag = fragmentManager.fragments[fragmentManager.backStackEntryCount - 1].tag
                setToolbarTitle(tag)
                super.onBackPressed()
            } else {
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed()
                    finish()
                }
                doubleBackToExitPressedOnce = true
                Toast.makeText(this, getString(R.string.back_key), Toast.LENGTH_SHORT).show()
                Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
            }
        }
    }

    private val admin: Unit
        private get() {
            val client = AsyncHttpClient()
            val params = RequestParams()
            val jsObj = Gson().toJsonTree(API()) as JsonObject
            jsObj.addProperty("method_name", "get_admin")
            params.put("data", API.toBase64(jsObj.toString()))
            client.post(Constant.API_URL, params, object : AsyncHttpResponseHandler() {
                override fun onStart() {
                    super.onStart()
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
                        for (i in 0 until jsonArray.length()) {
                            val objItem = ItemAdmin()
                            objJson = jsonArray.getJSONObject(i)
                            objItem.phone = objJson.getString("phone")
                            objItem.phone1 = objJson.getString("phone1")
                            objItem.address = objJson.getString("address")
                            phone!!.text = "+91 " + objJson.getString("phone")
                            phone1!!.text = "+91 " + objJson.getString("phone1")
                            vi = objJson.getString("views")
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
                    Log.d("raky", "fail")
                }
            })
        }
}