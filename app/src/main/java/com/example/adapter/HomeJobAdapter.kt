package com.example.adapter

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.item.ItemJob
import com.example.util.Constant
import com.example.util.PopUpAds
import com.example.util.RvOnClickListener
import com.jellysoft.sundigitalindia.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class HomeJobAdapter(private val mContext: Context, private val dataList: ArrayList<*>?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var clickListener: RvOnClickListener? = null
    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_job_item, parent, false)
        return ItemRowHolder(v)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val holder = viewHolder as ItemRowHolder
        val singleItem = dataList!![position] as ItemJob
        holder.jobTitle.text = singleItem.jobName
        holder.jobid.text = "JD" + singleItem.id
        holder.company.text = singleItem.jobCompanyName
        holder.vacancy.text = singleItem.jobVacancy
        holder.city.text = singleItem.city
        Log.d("value21", isNullOrEmpty(singleItem.jobArea).toString() + singleItem.jobArea)
        if (!isNullOrEmpty(singleItem.jobArea)){
            holder.areaLinear.visibility = View.VISIBLE
            holder.area.text = singleItem.jobArea
        } else {
            holder.areaLinear.visibility = View.GONE
        }
        holder.salary.text = singleItem.jobSalary
        holder.jobType.text = singleItem.jobType
        Picasso.get().load(singleItem.jobLogo).placeholder(R.drawable.placeholder)
            .into(holder.jobImage)
        holder.lyt_parent.setOnClickListener {
            PopUpAds.showInterstitialAds(
                mContext,
                holder.adapterPosition,
                clickListener
            )
        }
        holder.btnApplyJob.setOnClickListener {
            PopUpAds.showInterstitialAds(
                mContext,
                holder.adapterPosition,
                clickListener
            )
        }
        holder.share.setOnClickListener { view: View? ->
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(
                Intent.EXTRA_TEXT,
                """
                ${singleItem.jobName}
                ${mContext.getString(R.string.job_company_lbl)}${singleItem.jobCompanyName}
                ${mContext.getString(R.string.job_designation_lbl)}${singleItem.jobDesignation}
                ${mContext.getString(R.string.job_phone_lbl)}${singleItem.jobPhoneNumber}
                ${mContext.getString(R.string.job_address_lbl)}${singleItem.city}
                
                "View details here: https://www.sundigitalindiajobs.com/job?id=${singleItem.id}"
                
                Download Application here https://play.google.com/store/apps/details?id=com.jellysoft.sundigitalindia
                """.trimIndent()
            )
            sendIntent.type = "text/plain"
            mContext.startActivity(sendIntent)
        }
        holder.call.setOnClickListener {
            Log.d("bvm", singleItem.jobPhoneNumber.toString())
            val phone = singleItem.jobPhoneNumber
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$phone")
            mContext.startActivity(intent)
        }
        holder.whatsapp.setOnClickListener {
            val phone = singleItem.jobPhoneNumber
            val url = "https://api.whatsapp.com/send?phone=" + phone.substring(1)
            val pm = mContext.packageManager
            try {
                pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            mContext.startActivity(i)
        }
        when (singleItem.jobType) {
            Constant.JOB_TYPE_HOURLY -> {
                holder.jobType.setTextColor(mContext.resources.getColor(R.color.hourly_time_text))
                holder.cardViewType.setCardBackgroundColor(mContext.resources.getColor(R.color.hourly_time_bg))
            }
            Constant.JOB_TYPE_HALF -> {
                holder.jobType.setTextColor(mContext.resources.getColor(R.color.half_time_text))
                holder.cardViewType.setCardBackgroundColor(mContext.resources.getColor(R.color.half_time_bg))
            }
            Constant.JOB_TYPE_FULL -> {
                holder.jobType.setTextColor(mContext.resources.getColor(R.color.full_time_text))
                holder.cardViewType.setCardBackgroundColor(mContext.resources.getColor(R.color.full_time_bg))
            }
        }
    }

    private fun isNullOrEmpty(value: String): Boolean {
        return value.equals("null") or value.isEmpty()
    }

    override fun getItemCount(): Int {
        return dataList?.size ?: 0
    }

    fun setOnItemClickListener(clickListener: RvOnClickListener?) {
        this.clickListener = clickListener
    }

    fun hideHeader() {
        JobAdapter.ProgressViewHolder.progressBar.visibility = View.GONE
    }

    internal inner class ItemRowHolder @RequiresApi(api = Build.VERSION_CODES.O) constructor(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        var jobTitle: TextView
        var jobType: TextView
        var company: TextView
        var vacancy: TextView
        var area: TextView
        var jobid: TextView
        var city: TextView
        var salary: TextView
        var call: TextView
        var whatsapp: TextView
        var areaLabel: TextView
        var lyt_parent: LinearLayout
        var btnApplyJob: Button
        var cardViewType: CardView
        var jobImage: CircleImageView
        var share: ImageView
        var areaLinear: LinearLayout

        init {
            jobTitle = itemView.findViewById(R.id.text_job_title)
            jobType = itemView.findViewById(R.id.text_job_type)
            city = itemView.findViewById(R.id.city)
            area = itemView.findViewById(R.id.area)
            areaLabel = itemView.findViewById(R.id.areaLabel)
            salary = itemView.findViewById(R.id.salary)
            vacancy = itemView.findViewById(R.id.text_vacancy)
            company = itemView.findViewById(R.id.company)
            areaLinear = itemView.findViewById(R.id.areaLinear)
            jobid = itemView.findViewById(R.id.text_job_id)
            lyt_parent = itemView.findViewById(R.id.rootLayout)
            cardViewType = itemView.findViewById(R.id.cardJobType)
            jobImage = itemView.findViewById(R.id.image_job)
            share = itemView.findViewById(R.id.share)
            btnApplyJob = itemView.findViewById(R.id.btn_apply_job)
            call = itemView.findViewById(R.id.call)
            whatsapp = itemView.findViewById(R.id.whatsapp)
        }
    }
}