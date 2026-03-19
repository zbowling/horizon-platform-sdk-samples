/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package horizon.platform.sample.languagepack

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import horizon.platform.languagepack.LanguagePack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LanguagePackUiState(
    val isLoading: Boolean = false,
    val resultMessage: String? = null,
    val errorMessage: String? = null,
)

class LanguagePackViewModel : ViewModel() {

  private val languagePack = LanguagePack()

  private val _uiState = MutableStateFlow(LanguagePackUiState())
  val uiState: StateFlow<LanguagePackUiState> = _uiState

  fun checkInitialization() {
    _uiState.update { it.copy(isLoading = true, resultMessage = null, errorMessage = null) }
    viewModelScope.launch(Dispatchers.IO) {
      try {
        Log.d(TAG, "LanguagePack service initialized: $languagePack")
        _uiState.update {
          it.copy(
              isLoading = false,
              resultMessage = "LanguagePack service initialized successfully.",
          )
        }
      } catch (e: Exception) {
        Log.e(TAG, "LanguagePack initialization failed", e)
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
    private const val TAG = "LanguagePackVM"
  }
}
