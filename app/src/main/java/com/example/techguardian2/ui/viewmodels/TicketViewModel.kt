package com.example.techguardian2.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.techguardian2.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TicketViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

    fun submitTicket(assetId: String, description: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            // Simulamos sacar el token guardado y el ID del técnico
            val fakeToken = "token_guardado_en_datastore"
            val techId = 1

            // Convertimos el texto del ID a número de forma segura
            val idAsInt = assetId.toIntOrNull() ?: 0

            // Intentamos enviar el ticket
            val success = repository.sendMaintenanceTicket(
                token = fakeToken,
                assetId = idAsInt,
                description = description,
                techId = techId
            )

            // Avisamos a la pantalla si funcionó o no
            onResult(success)
        }
    }
}