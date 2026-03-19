/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package horizon.platform.sample.notifications

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import horizon.platform.notifications.Notifications
import horizon.platform.notifications.NotificationsException
import horizon.platform.notifications.configs.DeviceNotificationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NotificationsUiState(
    val isLoading: Boolean = false,
    val resultMessage: String? = null,
    val errorMessage: String? = null,
)

class NotificationsViewModel : ViewModel() {

  private val notifications = Notifications()

  private val _uiState = MutableStateFlow(NotificationsUiState())
  val uiState: StateFlow<NotificationsUiState> = _uiState

  fun deviceNotification() {
    executeAction("deviceNotification") {
      val config =
          DeviceNotificationConfig.builder()
              .withTitle("Title from Notifications Sample App")
              .withMessage("Message from Notifications Sample App")
              .build()
      notifications.deviceNotification(config)
      "Successfully sent notification"
    }
  }

  fun toastOnly() {
    executeAction("toastOnly") {
      val config =
          DeviceNotificationConfig.builder()
              .withTitle("Device Notification Toast Only")
              .withMessage("This notification is a toast only")
              .withIsToastOnly(true)
              .build()
      notifications.deviceNotification(config)
      "Successfully sent toast-only notification"
    }
  }

  private fun executeAction(actionName: String, action: suspend () -> String) {
    _uiState.update { it.copy(isLoading = true, resultMessage = null, errorMessage = null) }
    viewModelScope.launch(Dispatchers.IO) {
      try {
        val result = action()
        Log.d(TAG, "$actionName result: $result")
        _uiState.update { it.copy(isLoading = false, resultMessage = result) }
      } catch (e: NotificationsException) {
        Log.e(TAG, "$actionName failed", e)
        _uiState.update {
          it.copy(
              isLoading = false,
              errorMessage = "Failed: ${e.requestName}, code=${e.statusCode}, message=${e.message}",
          )
        }
      } catch (e: Exception) {
        Log.e(TAG, "$actionName failed", e)
        _uiState.update {
          it.copy(isLoading = false, errorMessage = "Error: ${e.message ?: "Unknown error"}")
        }
      }
    }
  }

  companion object {
    private const val TAG = "NotificationsViewModel"
  }
}
