package com.example.ticktapp.mvvm.view

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bikomobile.circleindicatorpager.CircleIndicatorPager
import com.bumptech.glide.Glide
import com.example.ticktapp.R

class DialogImageViewPostActivity : AppCompatActivity(), View.OnClickListener {
    private var pos = 0
    private var iv_car_close: ImageView? = null
    private var imageView: ImageView? = null
    private var profile_image: ArrayList<String>? = null
    private var circleIndicatorPager: CircleIndicatorPager? = null
    private var viewPager2: ViewPager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.dialog_image_post_view)
        getIntentData()
        initialize()
        setOnClick()
        val adapter = SlidingImage_Adapter(this, profile_image)
        viewPager2?.adapter = adapter
        viewPager2?.currentItem = pos
        circleIndicatorPager?.setViewPager(viewPager2)
        if (profile_image != null && profile_image!!.size == 1) {
            circleIndicatorPager?.setVisibility(View.GONE)
        } else {
            circleIndicatorPager?.setVisibility(View.VISIBLE)
        }
    }

    private fun getIntentData() {
        profile_image = intent.getSerializableExtra("photos") as ArrayList<String>
        pos = intent.getIntExtra("pos", 0)

    }

    private fun setOnClick() {
        iv_car_close!!.setOnClickListener(this)
    }

    private fun initialize() {
        iv_car_close = findViewById(R.id.iv_car_close)
        viewPager2 = findViewById(R.id.view_image)
        circleIndicatorPager = findViewById(R.id.circleIndicatorPager)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_car_close -> onBackPressed()
        }
    }

    inner class SlidingImage_Adapter(
        private val context: Context,
        private val postImageModels: List<String>?
    ) : PagerAdapter() {
        private val inflater: LayoutInflater = LayoutInflater.from(context)
        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

        override fun getCount(): Int {
            return postImageModels!!.size
        }

        override fun instantiateItem(view: ViewGroup, position: Int): Any {
            val imageLayout: View = inflater.inflate(R.layout.layout_slider, view, false)!!
            imageView = imageLayout
                .findViewById<View>(R.id.im_slider) as ImageView
            Glide.with(context).load(profile_image!![position])
                .placeholder(R.drawable.bg_drawable_rect_dfe5ef)
                .into(imageView!!)
            view.addView(imageLayout, 0)
            return imageLayout
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object`
        }

        override fun restoreState(state: Parcelable?, loader: ClassLoader?) {}
        override fun saveState(): Parcelable? {
            return null
        }

    }
}