/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package horizon.platform.sample.grouppresence

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import horizon.platform.grouppresence.GroupPresence
import horizon.platform.grouppresence.options.GroupPresenceOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GroupPresenceUiState(
    val isLoading: Boolean = false,
    val resultMessage: String? = null,
    val errorMessage: String? = null,
)

class GroupPresenceViewModel : ViewModel() {

  private val groupPresence = GroupPresence()

  private val _uiState = MutableStateFlow(GroupPresenceUiState())
  val uiState: StateFlow<GroupPresenceUiState> = _uiState

  fun setGroupPresence() {
    _uiState.update { it.copy(isLoading = true, resultMessage = null, errorMessage = null) }
    viewModelScope.launch(Dispatchers.IO) {
      try {
        val options =
            GroupPresenceOptions.builder()
                .withDeeplinkMessageOverride("deeplink_msg_for_test")
                .withDestinationApiName("test_destination")
                .withIsJoinable(true)
                .withLobbySessionId("lobby_12345")
                .withMatchSessionId("match_67890")
                .build()
        groupPresence.set(options)
        _uiState.update {
          it.copy(
              isLoading = false,
              resultMessage =
                  """
                  setGroupPresence success!
                  - Destination: test_destination
                  - Joinable: true
                  - Lobby: lobby_12345
                  - Match: match_67890
                  """
                      .trimIndent(),
          )
        }
      } catch (e: Exception) {
        Log.e(TAG, "setGroupPresence failed", e)
        _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
      }
    }
  }

  fun clearGroupPresence() {
    _uiState.update { it.copy(isLoading = true, resultMessage = null, errorMessage = null) }
    viewModelScope.launch(Dispatchers.IO) {
      try {
        groupPresence.clear()
        _uiState.update {
          it.copy(isLoading = false, resultMessage = "clearGroupPresence success!")
        }
      } catch (e: Exception) {
        Log.e(TAG, "clearGroupPresence failed", e)
        _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
      }
    }
  }

  fun joinIntentReceived() {
    _uiState.update { it.copy(isLoading = true, resultMessage = null, errorMessage = null) }
    viewModelScope.launch(Dispatchers.IO) {
      try {
        val details = groupPresence.joinIntentReceived().first()
        _uiState.update {
          it.copy(
              isLoading = false,
              resultMessage =
                  """
                  joinIntentReceived!
                  - deeplink: ${details.deeplinkMessage}
                  - destination: ${details.destinationApiName}
                  - lobby: ${details.lobbySessionId}
                  - match: ${details.matchSessionId}
                  """
                      .trimIndent(),
          )
        }
      } catch (e: Exception) {
        Log.e(TAG, "joinIntentReceived failed", e)
        _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
      }
    }
  }

  fun setDestination() {
    _uiState.update { it.copy(isLoading = true, resultMessage = null, errorMessage = null) }
    viewModelScope.launch(Dispatchers.IO) {
      try {
        groupPresence.setDestination("test_destination")
        _uiState.update { it.copy(isLoading = false, resultMessage = "setDestination success!") }
      } catch (e: Exception) {
        Log.e(TAG, "setDestination failed", e)
        _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
      }
    }
  }

  fun setIsJoinable() {
    _uiState.update { it.copy(isLoading = true, resultMessage = null, errorMessage = null) }
    viewModelScope.launch(Dispatchers.IO) {
      try {
        groupPresence.setIsJoinable(true)
        _uiState.update {
          it.copy(isLoading = false, resultMessage = "setIsJoinable(true) success!")
        }
      } catch (e: Exception) {
        Log.e(TAG, "setIsJoinable failed", e)
        _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
      }
    }
  }

  companion object {
    private const val TAG = "GroupPresenceViewModel"
  }
}
