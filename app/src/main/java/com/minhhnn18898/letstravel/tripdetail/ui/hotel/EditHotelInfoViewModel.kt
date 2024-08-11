package com.minhhnn18898.letstravel.tripdetail.ui.hotel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.minhhnn18898.core.utils.DateTimeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditHotelInfoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dateTimeUtils: DateTimeUtils = DateTimeUtils()
): ViewModel() {
}