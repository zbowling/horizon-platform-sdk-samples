/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package horizon.platform.sample.assetfile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import horizon.platform.assetfile.AssetFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AssetFileUiState(
    val isLoading: Boolean = false,
    val resultMessage: String? = null,
    val errorMessage: String? = null,
)

class AssetFileViewModel : ViewModel() {

  private val assetFile = AssetFile()

  private val _uiState = MutableStateFlow(AssetFileUiState())
  val uiState: StateFlow<AssetFileUiState> = _uiState

  fun checkInitialization() {
    _uiState.update { it.copy(isLoading = true, resultMessage = null, errorMessage = null) }
    viewModelScope.launch(Dispatchers.IO) {
      try {
        Log.d(TAG, "AssetFile service initialized: $assetFile")
        _uiState.update {
          it.copy(
              isLoading = false,
              resultMessage = "AssetFile service initialized successfully.",
          )
        }
      } catch (e: Exception) {
        Log.e(TAG, "AssetFile initialization failed", e)
        _uiState.update {
          it.copy(isLoading = false, errorMessage = "Error: ${e.message ?: "Unknown error"}")
        }
      }
    }
  }

  companion object {
    private const val TAG = "AssetFileViewModel"
  }
}
