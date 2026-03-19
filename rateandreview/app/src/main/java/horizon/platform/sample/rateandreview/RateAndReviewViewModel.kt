/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package horizon.platform.sample.rateandreview

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RateAndReviewUiState(
    val isLoading: Boolean = false,
    val resultMessage: String? = null,
    val errorMessage: String? = null,
)

class RateAndReviewViewModel : ViewModel() {

  private val _uiState = MutableStateFlow(RateAndReviewUiState())
  val uiState: StateFlow<RateAndReviewUiState> = _uiState

  fun checkInitialization() {
    _uiState.update { it.copy(isLoading = true, resultMessage = null, errorMessage = null) }
    viewModelScope.launch(Dispatchers.IO) {
      try {
        Log.d(TAG, "RateAndReview sample initialized")
        _uiState.update {
          it.copy(
              isLoading = false,
              resultMessage =
                  "Rate and Review sample initialized. " +
                      "The RateAndReview API will be available in a future SDK release.",
          )
        }
      } catch (e: Exception) {
        Log.e(TAG, "Initialization failed", e)
        _uiState.update {
          it.copy(
              isLoading = false,
              errorMessage = "Error: ${e.message}",
          )
        }
      }
    }
  }

  companion object {
    private const val TAG = "RateAndReviewViewModel"
  }
}
