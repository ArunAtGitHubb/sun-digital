package com.example.fragment

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.item.ItemProduct
import com.jellysoft.sundigitalindia.R

class ProductDetailsFragment : Fragment() {
    lateinit var text_desc: TextView
    lateinit var text_website: TextView
    lateinit var text_doc: TextView
    lateinit var descCard: CardView
    lateinit var webCard: CardView
    lateinit var itemJob: ItemProduct
    lateinit var btn_call: Button
    lateinit var btn_whatsapp: Button
    lateinit var btn_back: Button
    lateinit var mSkills: ArrayList<String>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_product_details, container, false)
        if (arguments != null) {
            itemJob = (requireArguments().getSerializable("itemJob") as ItemProduct?)!!
        }
        mSkills = ArrayList()
        text_desc = rootView.findViewById(R.id.text_desc)
        text_website = rootView.findViewById(R.id.text_website)
        descCard = rootView.findViewById(R.id.descCard)
        webCard = rootView.findViewById(R.id.webCard)
        text_doc = rootView.findViewById(R.id.text_doc)
        descCard = rootView.findViewById(R.id.descCard)
        btn_call = rootView.findViewById(R.id.btn_call)
        btn_whatsapp = rootView.findViewById(R.id.btn_whats)
        btn_back = rootView.findViewById(R.id.btn_back)
        text_doc.text = itemJob.productDoc

        if (!itemJob.productDesc?.let { isNullOrEmpty(it) }!!){
            descCard.visibility = View.VISIBLE
            text_desc.text = itemJob.productDesc
        } else {
            descCard.visibility = View.GONE
        }

        if (!itemJob.WebsiteLink?.let { isNullOrEmpty(it) }!!){
            webCard.visibility = View.VISIBLE
            text_website.text = itemJob.WebsiteLink
            text_website.setTextColor(Color.BLUE)
            text_website.setOnClickListener {
                try {
                    val myIntent =
                        Intent(Intent.ACTION_VIEW, Uri.parse(text_website.text.toString()))
                    startActivity(myIntent)
                } catch (e: Exception) {
                    Toast.makeText(context, "Make sure url is correct", Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                }
            }
        } else {
            webCard.visibility = View.GONE
        }


        btn_back.setOnClickListener { requireActivity().onBackPressed() }
        btn_call.setOnClickListener {
            val intent =
                Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", itemJob.productPhoneNumber, null))
            startActivity(intent)
        }
        btn_whatsapp.setOnClickListener {
            val phone = "91" + itemJob.productPhoneNumber!!.replace("+91", "")
            val url = "https://api.whatsapp.com/send?phone=$phone"
            val pm = requireActivity().packageManager
            try {
                pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }
        return rootView
    }

    companion object {
        @JvmStatic
        fun newInstance(itemProduct: ItemProduct?): ProductDetailsFragment {
            val f = ProductDetailsFragment()
            val args = Bundle()
            args.putSerializable("itemJob", itemProduct)
            f.arguments = args
            return f
        }
    }

    private fun isNullOrEmpty(value: String): Boolean {
        return value.equals("null") or value.isEmpty()
    }
}