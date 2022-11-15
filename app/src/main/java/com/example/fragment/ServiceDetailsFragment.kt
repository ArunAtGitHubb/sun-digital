package com.example.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.item.ItemService
import com.jellysoft.sundigitalindia.R

class ServiceDetailsFragment : Fragment() {
    lateinit var webView: WebView
    lateinit var textDesc: TextView
    lateinit var textSkills: TextView
    lateinit var textWebsite: TextView
    lateinit var descCard: CardView
    lateinit var webCard: CardView
    lateinit var itemJob: ItemService
    lateinit var btn_call: Button
    lateinit var btn_whatsapp: Button
    lateinit var btn_back: Button
    var mSkills: ArrayList<String>? = null
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_service_details, container, false)
        if (arguments != null) {
            itemJob = (requireArguments().getSerializable("itemJob") as ItemService?)!!
        }
        mSkills = ArrayList()
        textDesc = rootView.findViewById(R.id.text_desc)
        textSkills = rootView.findViewById(R.id.textSkills)
        textWebsite = rootView.findViewById(R.id.text_website)
        btn_call = rootView.findViewById(R.id.btn_call)
        btn_whatsapp = rootView.findViewById(R.id.btn_whats)
        btn_back = rootView.findViewById(R.id.btn_back)
        descCard = rootView.findViewById(R.id.descCard)
        webCard = rootView.findViewById(R.id.webCard)
        textSkills.setText(itemJob!!.serviceSkill)

        if (!itemJob!!.serviceDesc?.let { isNullOrEmpty(it) }!!){
            descCard.visibility = View.VISIBLE
            textDesc.text = itemJob!!.serviceDesc
        } else {
            descCard.visibility = View.GONE
        }

        if (!itemJob.WebsiteLink?.let { isNullOrEmpty(it) }!!){
            webCard.visibility = View.VISIBLE
            textWebsite.text = itemJob.WebsiteLink
            textWebsite.setTextColor(Color.BLUE)
            textWebsite.setOnClickListener(View.OnClickListener { view: View? ->
                try {
                    val myIntent =
                        Intent(Intent.ACTION_VIEW, Uri.parse(textWebsite.getText().toString()))
                    startActivity(myIntent)
                } catch (e: Exception) {
                    Toast.makeText(context, "Make sure url is correct", Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                }
            })
        } else {
            webCard.visibility = View.GONE
        }



        btn_back.setOnClickListener(View.OnClickListener { view: View? -> requireActivity().onBackPressed() })
        btn_call.setOnClickListener(View.OnClickListener { view: View? ->
            val intent =
                Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", itemJob!!.servicePhoneNumber, null))
            startActivity(intent)
        })
        btn_whatsapp.setOnClickListener(View.OnClickListener { view: View? ->
            val phone = "91" + itemJob!!.servicePhoneNumber!!.replace("+91", "")
            val url = "https://api.whatsapp.com/send?phone=$phone"
            val pm = requireContext().packageManager
            try {
                pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        })
        return rootView
    }

    private fun isNullOrEmpty(value: String): Boolean {
        return value.equals("null") or value.isEmpty()
    }

    companion object {
        @JvmStatic
        fun newInstance(itemProduct: ItemService?): ServiceDetailsFragment {
            val f = ServiceDetailsFragment()
            val args = Bundle()
            args.putSerializable("itemJob", itemProduct)
            f.arguments = args
            return f
        }
    }
}