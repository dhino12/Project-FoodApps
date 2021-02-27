package com.example.foodapplication.ui.category

import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.ViewModel
import com.example.foodapplication.core.data.CookingRepository
import com.example.foodapplication.core.domain.usecase.FoodUseCase

class ListCategoryViewModel(foodUseCase: FoodUseCase):ViewModel() {
    val listCategory = LiveDataReactiveStreams.fromPublisher(foodUseCase.getListCategory())
}