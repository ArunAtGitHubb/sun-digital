package com.example.fragment

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.item.ItemMatrimony
import com.jellysoft.sundigitalindia.R

class MatrimonyDetailsFragment : Fragment() {
    lateinit var webView: WebView
    lateinit var text_matrimony_qualification: TextView
    lateinit var text_matrimony_dob: TextView
    lateinit var marital: TextView
    lateinit var text_expectation: TextView
    lateinit var matrimonyContact: TextView
    lateinit var matrimonyDesc: TextView
    lateinit var itemMatrimony: ItemMatrimony
    lateinit var qualCard: CardView
    lateinit var dobCard: CardView
    lateinit var contactCard: CardView
    lateinit var descCard: CardView
    lateinit var btn_call: Button
    lateinit var btn_whatsapp: Button
    lateinit var btn_back: Button
    lateinit var btn_download: Button
    var mSkills: ArrayList<String>? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_matrimony_details, container, false)
        if (arguments != null) {
            itemMatrimony = (requireArguments().getSerializable("itemJob") as ItemMatrimony?)!!
        }
        mSkills = ArrayList()
        text_matrimony_qualification = rootView.findViewById(R.id.text_matrimony_qualification)
        text_matrimony_dob = rootView.findViewById(R.id.text_dob)
        marital = rootView.findViewById(R.id.marital)
        text_expectation = rootView.findViewById(R.id.text_expectation)
        matrimonyContact = rootView.findViewById(R.id.text_contact_person)
        matrimonyDesc = rootView.findViewById(R.id.text_desc)
        btn_call = rootView.findViewById(R.id.btn_call)
        btn_whatsapp = rootView.findViewById(R.id.btn_whats)
        btn_back = rootView.findViewById(R.id.btn_back)
        btn_download = rootView.findViewById(R.id.btn_download)
        qualCard = rootView.findViewById(R.id.qualCard)
        dobCard = rootView.findViewById(R.id.dobCard)
        contactCard = rootView.findViewById(R.id.contactCard)
        descCard = rootView.findViewById(R.id.descCard)

        text_expectation.text = itemMatrimony.matrimonyPartnerExpect
        marital.text = itemMatrimony.matrimonyMaritalStatus

        if (!itemMatrimony.matrimonyEducation?.let { isNullOrEmpty(it) }!!){
            qualCard.visibility = View.VISIBLE
            text_matrimony_qualification.text = itemMatrimony.matrimonyEducation
        } else {
            qualCard.visibility = View.GONE
        }

        if (!itemMatrimony.matrimonyPersonName?.let { isNullOrEmpty(it) }!!){
            contactCard.visibility = View.VISIBLE
            matrimonyContact.text = itemMatrimony.matrimonyPersonName
        } else {
            contactCard.visibility = View.GONE
        }

        if (!itemMatrimony.matrimonyDob?.let { isNullOrEmpty(it) }!!){
            dobCard.visibility = View.VISIBLE
            text_matrimony_dob.text = itemMatrimony.matrimonyDob
        } else {
            dobCard.visibility = View.GONE
        }

        if (!itemMatrimony.matrimonyDesc?.let { isNullOrEmpty(it) }!!){
            descCard.visibility = View.VISIBLE
            matrimonyDesc.text = itemMatrimony.matrimonyDesc
        } else {
            descCard.visibility = View.GONE
        }

        btn_download.setOnClickListener {

            if (!itemMatrimony.matrimonyHoroscope?.let { isNullOrEmpty(it) }!!){
                descCard.visibility = View.VISIBLE
                Toast.makeText(context, "Download started...", Toast.LENGTH_LONG).show()
                val url = Uri.parse(itemMatrimony.matrimonyHoroscope)
                val paths = url.pathSegments
                val request = DownloadManager.Request(url)
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(false)
                    .setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_DOWNLOADS,
                        itemMatrimony!!.matrimonyName + "_horoscope_" + paths[paths.size - 1]
                    )
                val downloadManager =
                    requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                val downloadID = downloadManager.enqueue(request)
            } else {
                Toast.makeText(context, "Horoscope not available", Toast.LENGTH_LONG).show()
            }
        }

        btn_back.setOnClickListener(View.OnClickListener { view: View? -> requireActivity().onBackPressed() })
        btn_call.setOnClickListener(View.OnClickListener { view: View? ->
            val intent = Intent(
                Intent.ACTION_DIAL,
                Uri.fromParts("tel", itemMatrimony!!.matrimonyPhoneNumber, null)
            )
            startActivity(intent)
        })
        btn_whatsapp.setOnClickListener(View.OnClickListener { view: View? ->
            val phone = "91" + itemMatrimony!!.matrimonyPhoneNumber!!.replace("+91", "")
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

    companion object {
        @JvmStatic
        fun newInstance(itemProduct: ItemMatrimony?): MatrimonyDetailsFragment {
            val f = MatrimonyDetailsFragment()
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