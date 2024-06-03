package com.example.food

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.food.adapters.CategoryMealsAdapter
import com.example.food.databinding.ActivityCategoryMealsBinding
import com.example.food.videoModel.CategoryMealsViewModel

class CategoryMealsActivity : AppCompatActivity() {

    lateinit var binding: ActivityCategoryMealsBinding
    lateinit var categoryMealsViewModel: CategoryMealsViewModel
    lateinit var categoryMealsAdapter: CategoryMealsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryMealsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prepareRecyclerView()

        categoryMealsViewModel = ViewModelProvider(this)[CategoryMealsViewModel::class.java]

        val categoryName = intent.getStringExtra(HomeFragment.CATEGORY_NAME)
        categoryName?.let {
            categoryMealsViewModel.getMealsByCategory(it)
        }

        categoryMealsViewModel.observeMealsLiveData().observe(this, Observer { mealsList ->
            binding.tvCategoryCount.text = mealsList.size.toString()
            categoryMealsAdapter.setMealsList(mealsList)
        })

        categoryMealsAdapter.onItemClick = { meal ->
            val intent = Intent(this, MealDetailActivity::class.java)
            intent.putExtra("MEAL_ID", meal.idMeal)
            startActivity(intent)
        }

    }

    private fun prepareRecyclerView() {
        categoryMealsAdapter = CategoryMealsAdapter()
        binding.rvMeals.apply {
            layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
            adapter = categoryMealsAdapter
        }
    }
}



