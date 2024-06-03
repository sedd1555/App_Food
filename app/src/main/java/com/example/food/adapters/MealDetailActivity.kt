package com.example.food

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.food.databinding.ActivityMealDetailBinding
import com.example.food.db.MealDatabase
import com.example.food.pojo.Meal
import com.example.food.videoModel.MealViewModel
import com.example.food.videoModel.MealViewModelFactory

class MealDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMealDetailBinding
    private lateinit var mealViewModel: MealViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMealDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mealId = intent.getStringExtra("MEAL_ID")

        val mealDatabase = MealDatabase.getInstance(this)
        val viewModelFactory = MealViewModelFactory(mealDatabase)
        mealViewModel = ViewModelProvider(this, viewModelFactory)[MealViewModel::class.java]

        mealViewModel.getMealDetail(mealId!!)
        observeMealDetailsLiveData()
    }

    private fun observeMealDetailsLiveData() {
        mealViewModel.observerMealDetailsLiveData().observe(this, Observer { meal ->
            meal?.let {
                updateUI(it)
            }
        })
    }

    private fun updateUI(meal: Meal) {
        Glide.with(applicationContext)
            .load(meal.strMealThumb)
            .into(binding.imgMealDetail)

        binding.collapsingToolbar.title = meal.strMeal
        binding.tvCategory.text = "Category: ${meal.strCategory}"
        binding.tvArea.text = "Area: ${meal.strArea}"
        binding.tvInstructions.text = meal.strInstructions
    }
}
