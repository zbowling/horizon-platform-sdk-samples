/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package horizon.platform.sample.abusereport

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import horizon.platform.abusereport.AbuseReport
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AbuseReportUiState(
    val isLoading: Boolean = false,
    val resultMessage: String? = null,
    val errorMessage: String? = null,
)

class AbuseReportViewModel : ViewModel() {

  private val abuseReport = AbuseReport()

  private val _uiState = MutableStateFlow(AbuseReportUiState())
  val uiState: StateFlow<AbuseReportUiState> = _uiState

  fun checkInitialization() {
    _uiState.update { it.copy(isLoading = true, resultMessage = null, errorMessage = null) }
    viewModelScope.launch(Dispatchers.IO) {
      try {
        Log.d(TAG, "AbuseReport service initialized: $abuseReport")
        _uiState.update {
          it.copy(
              isLoading = false,
              resultMessage = "AbuseReport service initialized successfully.",
          )
        }
      } catch (e: Exception) {
        Log.e(TAG, "AbuseReport initialization failed", e)
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
    private const val TAG = "AbuseReportViewModel"
  }
}
