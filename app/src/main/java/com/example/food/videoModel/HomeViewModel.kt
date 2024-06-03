package com.example.food.videoModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.food.HomeFragment
import com.example.food.db.MealDatabase
import com.example.food.pojo.Category
import com.example.food.pojo.CategoryList
import com.example.food.pojo.MealsByCategoryList
import com.example.food.pojo.MealsByCategory

import com.example.food.pojo.Meal
import com.example.food.pojo.MealList
import com.example.food.retrofit.RetrofitInstance
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel(
    private val mealDatabase: MealDatabase
):ViewModel() {
    private var randomMealLiveData = MutableLiveData<Meal>()
    private var popularItemLiveData = MutableLiveData<List<MealsByCategory>>()
    private var categoriesLiveData = MutableLiveData<List<Category>>()
    private var favoritesMealsLiveData = mealDatabase.mealDao().getAllMeals()
    private var searchedMealsLiveData = MutableLiveData<List<Meal>>()

    fun  getRandomMeal(){
        RetrofitInstance.api.getRandomMeal().enqueue(object : Callback<MealList> {
            override fun onResponse(call: Call<MealList>, response: Response<MealList>) {
                if(response.body() !=null){
                    val randomMeal: Meal = response.body()!!.meals[0]
                    randomMealLiveData.value = randomMeal
                }else{
                    return
                }
            }

            override fun onFailure(call: Call<MealList>, t: Throwable) {
                Log.d("Home Fragment", t.message.toString())
            }
        })
    }

    fun getPopularItems(){
        RetrofitInstance.api.getPopularItems("Seafood").enqueue(object:Callback<MealsByCategoryList>{
            override fun onResponse(call: Call<MealsByCategoryList>, response: Response<MealsByCategoryList>) {
                if (response.body()!=null)
                    popularItemLiveData.value = response.body()!!.meals
            }

            override fun onFailure(call: Call<MealsByCategoryList>, t: Throwable) {
                Log.d("HomeFragmet",t.message.toString())
            }

        })

    }

    fun getCategories(){
        RetrofitInstance.api.getCategories().enqueue(object :Callback<CategoryList>{
            override fun onResponse(call: Call<CategoryList>, response: Response<CategoryList>) {
                response.body()?.let {categoryList ->
                    categoriesLiveData.postValue(categoryList.categories)
                }
            }

            override fun onFailure(call: Call<CategoryList>, t: Throwable) {
                Log.e("HomeViewModel",t.message.toString())
            }

        })
    }

    fun deleteMeal(meal:Meal){
        viewModelScope.launch {
            mealDatabase.mealDao().delete(meal)

        }

    }
    fun insertMeal(meal:Meal){
        viewModelScope.launch {
            mealDatabase.mealDao().upsert(meal)

        }
    }

    fun observeSearchedMealsLiveData():LiveData<List<Meal>> = searchedMealsLiveData

    fun searchMeals(searchQuery:String) = RetrofitInstance.api.searchMeals(searchQuery).enqueue(
        object : Callback<MealList>{
            override fun onResponse(call: Call<MealList>, response: Response<MealList>) {
                val mealsList = response.body()?.meals
                mealsList?.let{
                    searchedMealsLiveData.postValue(it)

                }
            }

            override fun onFailure(call: Call<MealList>, t: Throwable) {
                TODO("Not yet implemented")
            }

        }
    )


    fun observeRandomMealLivedata():LiveData<Meal>{
        return randomMealLiveData
    }

    fun observePopularItemsLiveDate():LiveData<List<MealsByCategory>>{
        return popularItemLiveData
    }

    fun observeCategoriesLiveData():LiveData<List<Category>>{
        return categoriesLiveData
    }

    fun observeFavoritesMealsLiveData():LiveData<List<Meal>>{
        return favoritesMealsLiveData
    }
}