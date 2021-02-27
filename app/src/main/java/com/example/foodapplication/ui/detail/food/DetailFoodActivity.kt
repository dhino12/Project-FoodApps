package com.example.foodapplication.ui.detail.food

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.foodapplication.R
import com.example.foodapplication.core.data.Resource
import com.example.foodapplication.core.data.source.local.entity.ArticleDetailEntity
import com.example.foodapplication.core.data.source.local.entity.CookingDetailEntity
import com.example.foodapplication.core.domain.model.Article
import com.example.foodapplication.core.domain.model.Cooking
import com.example.foodapplication.core.util.DataToView
import com.example.foodapplication.databinding.ActivityDetailFoodBinding
import com.example.foodapplication.ui.ViewModelFactory
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks
import com.github.ksoichiro.android.observablescrollview.ScrollState
import com.github.ksoichiro.android.observablescrollview.ScrollUtils
import com.nineoldandroids.view.ViewHelper
import kotlinx.android.synthetic.main.activity_detail_food.*


class DetailFoodActivity : AppCompatActivity(), ObservableScrollViewCallbacks {
    private lateinit var binding:ActivityDetailFoodBinding

    private lateinit var viewModel:DetailViewModel
    private lateinit var dataToView:DataToView

    companion object{
        const val EXTRA_TITLE_COOKING = "cooking_title_key"
        const val EXTRA_ID_COOKING = "cooking_id_key"
        const val EXTRA_ARTICLE_TITLE = "article_title_key"
        const val EXTRA_ARTICLE_ID = "article_id_key"
        const val EXTRA_ARTICLE_TAG = "article_tag_key"
    }

    private fun setWindowFlag(activity: Activity, bits: Int, on: Boolean){
        val win = activity.window
        val winParams =  win.attributes
        if(on){
            winParams.flags = winParams.flags or bits
        }else{
            winParams.flags = winParams.flags and bits.inv()
        }
        win.attributes = winParams
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarOverlay)
        val toolbars = supportActionBar
        if(toolbars != null) supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            if (Build.VERSION.SDK_INT >= 19) {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            }
            //make fully Android Transparent Status bar
            if (Build.VERSION.SDK_INT >= 21) {
                setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
                window.statusBarColor = Color.TRANSPARENT
            }
        }

        binding.titles.text = title
        title = null

        val extras = intent.extras
        val keyCollection = mutableListOf<String>()
        val titleCooking = extras?.getString(EXTRA_TITLE_COOKING)
        val idCooking = extras?.getString(EXTRA_ID_COOKING)
        val titleArticle = extras?.getString(EXTRA_ARTICLE_TITLE)
        val idArticle = extras?.getString(EXTRA_ARTICLE_ID)
        val tagArticle = extras?.getString(EXTRA_ARTICLE_TAG)

        Log.e("error TitleContent", titleCooking.toString())
        Log.e("error IDContent", idCooking.toString())

        val factory = ViewModelFactory.getInstance(this,this)
        viewModel = ViewModelProvider(this, factory)[DetailViewModel::class.java]

        dataToView = DataToView.getInstance(binding,this,viewModel)

        if(titleCooking != null || idCooking != null){
            if(keyCollection.isNullOrEmpty()){
                keyCollection.add(titleCooking.toString())
                keyCollection.add(idCooking.toString())
            }else{
                keyCollection[0] = titleCooking.toString()
                keyCollection[1] = idCooking.toString()
            }

            viewModel.setSelectedCook(titleCookingDb = keyCollection)
            viewModel.cookingDetail.observe(this) { titleKey ->
                if (titleKey != null) {
                    when (titleKey) {
                        is Resource.Loading -> {
                            showLoading(true)
                        }
                        is Resource.Success -> {
                            showLoading(false)
                            dataToView.implementToView(cookingDetailEntity = titleKey.data?.get(0))
                            // implementToView(cookingDetailEntity = titleKey.data)
                        }
                        is Resource.Error -> {
                            Toast.makeText(this, titleKey.message.toString(), Toast.LENGTH_LONG).show()
                        }
                    }
                }else{
                    Toast.makeText(this,(titleKey == null).toString(),Toast.LENGTH_SHORT).show()
                }
            }
        }else if(titleArticle != null && idArticle != null && tagArticle != null){
            if(keyCollection.isNullOrEmpty()){
                keyCollection.add(titleArticle)
                keyCollection.add(idArticle)
                keyCollection.add(tagArticle)
            }else{
                keyCollection[0] = titleArticle
                keyCollection[1] = idArticle
                keyCollection[2] = tagArticle
            }
            viewModel.setSelectedCook(titleArticleDb = keyCollection)
            viewModel.foodDetail<Article>(false).observe(this) { titleKey ->
                if (titleKey != null) {
                    when (titleKey) {
                        is Resource.Loading -> {
                            showLoading(true)
                        }
                        is Resource.Success -> {
                            showLoading(false)
                            dataToView.implementToView(articleDetailEntity = titleKey.data)
                            // implementToView(articleDetailEntity = titleKey.data)
                        }
                        is Resource.Error -> {
                            Toast.makeText(this, titleKey.message.toString(), Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }

        binding.scroll.setScrollViewCallbacks(this)

        ScrollUtils.addOnGlobalLayoutListener(binding.scroll){
            onScrollChanged(0, false, false)
        }
    }

    override fun onScrollChanged(scrollY: Int, firstScroll: Boolean, dragging: Boolean) {
        dataToView.updateFlexibleView(scrollY)
    }

    private fun showLoading(switch: Boolean){
        if(switch){
            binding.loadingBar.visibility = View.VISIBLE
            binding.bgColorView.visibility = View.VISIBLE
        }else{
            binding.loadingBar.visibility = View.GONE
            binding.bgColorView.visibility = View.GONE
        }
    }

    override fun onDownMotionEvent() {}
    override fun onUpOrCancelMotionEvent(scrollState: ScrollState?) {}

}