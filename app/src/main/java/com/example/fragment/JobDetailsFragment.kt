package com.example.fragment

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.item.ItemJob
import com.jellysoft.sundigitalindia.R

class JobDetailsFragment : Fragment() {
    //    WebView webView;
    lateinit var textJobQualification: TextView
    lateinit var textWorkDay: TextView
    lateinit var textWorkTime: TextView
    lateinit var textExp: TextView
    lateinit var textType: TextView
    lateinit var marital: TextView
    lateinit var text_desc: TextView
    lateinit var textSkills: TextView
    lateinit var text_website: TextView
    lateinit var itemJob: ItemJob

    lateinit var qualCard: CardView
    lateinit var jobDescCard: CardView
    lateinit var expCard: CardView
    lateinit var skillCard: CardView
    lateinit var ageCard: CardView
    lateinit var genderCard: CardView
    lateinit var maritalCard: CardView
    lateinit var webCard: CardView

    lateinit var btn_call: Button
    lateinit var btn_whatsapp: Button
    lateinit var btn_back: Button
    lateinit var btn_download: Button
    lateinit var mSkills: ArrayList<String>
    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_job_details, container, false)
        if (arguments != null) {
            itemJob = (requireArguments().getSerializable("itemJob") as ItemJob?)!!
        }
        mSkills = ArrayList()
        text_desc = rootView.findViewById(R.id.text_desc)
        textJobQualification = rootView.findViewById(R.id.text_job_qualification)
        textWorkDay = rootView.findViewById(R.id.text_job_work_day)
        textWorkTime = rootView.findViewById(R.id.text_job_work_time)
        textExp = rootView.findViewById(R.id.text_job_exp)
        textType = rootView.findViewById(R.id.text_job_type)
        textSkills = rootView.findViewById(R.id.textSkills)
        text_website = rootView.findViewById(R.id.text_website)
        marital = rootView.findViewById(R.id.marital)

        qualCard = rootView.findViewById(R.id.qualCard)
        expCard = rootView.findViewById(R.id.expCard)
        skillCard = rootView.findViewById(R.id.skillCard)
        ageCard = rootView.findViewById(R.id.ageCard)
        genderCard = rootView.findViewById(R.id.genderCard)
        maritalCard = rootView.findViewById(R.id.maritalCard)
        webCard = rootView.findViewById(R.id.webCard)
        jobDescCard = rootView.findViewById(R.id.jobDescCard)

        btn_call = rootView.findViewById(R.id.btn_call)
        btn_whatsapp = rootView.findViewById(R.id.btn_whats)
        btn_back = rootView.findViewById(R.id.btn_back)
        btn_download = rootView.findViewById(R.id.btn_download)

        if(!isNullOrEmpty(itemJob.jobSkill)) {
            textSkills.text = itemJob.jobSkill
            skillCard.visibility = View.VISIBLE
        } else {
            skillCard.visibility = View.GONE
        }

        if(!isNullOrEmpty(itemJob.jobDesc)) {
            text_desc.text = itemJob.jobDesc
            jobDescCard.visibility = View.VISIBLE
        } else {
            jobDescCard.visibility = View.GONE
        }

        if(!isNullOrEmpty(itemJob.age)) {
            textWorkDay.text = itemJob.age
            ageCard.visibility = View.VISIBLE
        } else {
            ageCard.visibility = View.GONE
        }

        if(!isNullOrEmpty(itemJob.sex)) {
            textWorkTime.text = itemJob.sex
            genderCard.visibility = View.VISIBLE
        } else {
            genderCard.visibility = View.GONE
        }

        if(!isNullOrEmpty(itemJob.jobQualification)) {
            textJobQualification.text = itemJob.jobQualification
            qualCard.visibility = View.VISIBLE
        } else {
            qualCard.visibility = View.GONE
        }

        if(!isNullOrEmpty(itemJob.marital)) {
            marital.text = itemJob.marital
            maritalCard.visibility = View.VISIBLE
        } else {
            maritalCard.visibility = View.GONE
        }

        if(!isNullOrEmpty(itemJob.jobExperience)) {
            textExp.text = itemJob.jobExperience
            expCard.visibility = View.VISIBLE
        } else {
            expCard.visibility = View.GONE
        }

        if(!isNullOrEmpty(itemJob.jobWebsite)) {
            text_website.text = itemJob.jobWebsite
            text_website.setTextColor(Color.BLUE)
            text_website.setOnClickListener {
                try {
                    val myIntent = Intent(Intent.ACTION_VIEW, Uri.parse(text_website.text.toString()))
                    startActivity(myIntent)
                } catch (e: Exception) {
                    Toast.makeText(context, "Make sure url is correct", Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                }
            }
            webCard.visibility = View.VISIBLE
        } else {
            webCard.visibility = View.GONE
        }

        textType.text = itemJob.jobType

        btn_back.setOnClickListener { requireActivity().onBackPressed() }
        btn_call.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_DIAL,
                Uri.fromParts("tel", itemJob.jobPhoneNumber, null)
            )
            startActivity(intent)
        }
        btn_download.setOnClickListener {
            Toast.makeText(context, "Download started...", Toast.LENGTH_LONG).show()
            val request = DownloadManager.Request(
                Uri.parse(
                    itemJob.jobPdf
                )
            )
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(false)
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    itemJob.jobName + ".pdf"
                )
            val downloadManager =
                requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val downloadID = downloadManager.enqueue(request)
        }
        btn_whatsapp.setOnClickListener {
            val phone = itemJob!!.jobPhoneNumber
            val url = "https://api.whatsapp.com/send?phone=" + phone.replace("+91", "")
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
        fun newInstance(itemJob: ItemJob?): JobDetailsFragment {
            val f = JobDetailsFragment()
            val args = Bundle()
            args.putSerializable("itemJob", itemJob)
            f.arguments = args
            return f
        }
    }

    private fun isNullOrEmpty(value: String): Boolean {
        return value.equals("null") or value.isEmpty()
    }
}