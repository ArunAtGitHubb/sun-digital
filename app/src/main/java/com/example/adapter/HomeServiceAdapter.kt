package com.example.adapter

import android.annotation.SuppressLint
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
import androidx.recyclerview.widget.RecyclerView
import com.example.item.ItemService
import com.example.util.PopUpAds
import com.example.util.RvOnClickListener
import com.jellysoft.sundigitalindia.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class HomeServiceAdapter(private val mContext: Context, private val dataList: ArrayList<*>?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var clickListener: RvOnClickListener? = null
    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.row_service_item, parent, false)
        return ItemRowHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val holder = viewHolder as ItemRowHolder
        val singleItem = dataList!![position] as ItemService
        holder.jobTitle.text = singleItem.serviceName
        holder.text_job_category.text = singleItem.serviceCategoryName
        holder.jobid.text = "AZ" + singleItem.id
        holder.city.text = singleItem.City
        if (!singleItem.serviceArea?.let { isNullOrEmpty(it) }!!){
            holder.areaLinear.visibility = View.VISIBLE
            holder.area.text = singleItem.serviceArea
        } else {
            holder.areaLinear.visibility = View.GONE
        }
        holder.salary.text = "Rs. " + singleItem.serviceCost
        holder.workTime.text = singleItem.serviceTime
        Picasso.get().load(singleItem.serviceLogo).placeholder(R.drawable.placeholder)
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
                ${singleItem.serviceName}
                ${mContext.getString(R.string.job_company_lbl)}${singleItem.serviceCompanyName}
                ${mContext.getString(R.string.job_designation_lbl)}${singleItem.serviceDesignation}
                ${mContext.getString(R.string.job_phone_lbl)}${singleItem.servicePhoneNumber}
                ${mContext.getString(R.string.job_address_lbl)}${singleItem.City}
                
                "View details here: https://www.sundigitalindiajobs.com/service?id=${singleItem.id}
                """.trimIndent()
            )
            sendIntent.type = "text/plain"
            mContext.startActivity(sendIntent)
        }
        holder.call.setOnClickListener { view: View? ->
            Log.d("bvm", singleItem.servicePhoneNumber.toString())
            val phone = singleItem.servicePhoneNumber
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$phone")
            mContext.startActivity(intent)
        }
        holder.whatsapp.setOnClickListener { view: View? ->
            val phone = singleItem.servicePhoneNumber
            val url = "https://api.whatsapp.com/send?phone=" + phone!!.substring(1)
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
    }

    override fun getItemCount(): Int {
        return dataList?.size ?: 0
    }

    fun setOnItemClickListener(clickListener: RvOnClickListener?) {
        this.clickListener = clickListener
    }

    private fun isNullOrEmpty(value: String): Boolean {
        return value.equals("null") or value.isEmpty()
    }

    internal inner class ItemRowHolder @RequiresApi(api = Build.VERSION_CODES.O) constructor(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        var jobTitle: TextView
        var workTime: TextView
        var jobid: TextView
        var text_job_category: TextView
        var city: TextView
        var salary: TextView
        var call: TextView
        var whatsapp: TextView
        var area: TextView
        var lyt_parent: LinearLayout
        var areaLinear: LinearLayout
        var btnApplyJob: Button
        var jobImage: CircleImageView
        var share: ImageView

        init {
            jobTitle = itemView.findViewById(R.id.text_job_title)
            text_job_category = itemView.findViewById(R.id.text_job_category)
            city = itemView.findViewById(R.id.city)
            area = itemView.findViewById(R.id.area)
            areaLinear = itemView.findViewById(R.id.areaLinear)
            salary = itemView.findViewById(R.id.salary)
            workTime = itemView.findViewById(R.id.work_time)
            jobid = itemView.findViewById(R.id.text_job_id)
            lyt_parent = itemView.findViewById(R.id.rootLayout)
            jobImage = itemView.findViewById(R.id.image_job)
            share = itemView.findViewById(R.id.share)
            btnApplyJob = itemView.findViewById(R.id.btn_apply_job)
            call = itemView.findViewById(R.id.call)
            whatsapp = itemView.findViewById(R.id.whatsapp)
        }
    }
}