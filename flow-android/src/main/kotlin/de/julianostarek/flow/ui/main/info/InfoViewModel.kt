package de.julianostarek.flow.ui.main.info

import android.app.Application
import androidx.lifecycle.*
import de.julianostarek.flow.viewmodel.LoadingLiveData
import de.julianostarek.flow.viewmodel.LocationViewModel
import de.jlnstrk.transit.common.extensions.optional
import de.jlnstrk.transit.common.model.Line
import de.jlnstrk.transit.common.model.Message
import de.jlnstrk.transit.common.response.base.ServiceResult
import de.jlnstrk.transit.common.service.StatusInformationResult
import de.jlnstrk.transit.common.service.StatusInformationService

class InfoViewModel(application: Application) : LocationViewModel(application) {
    val city = MutableLiveData<String?>(null)

    val messagesLoading = LoadingLiveData()
    val messagesRefreshing = LoadingLiveData()

    val messagesResult: LiveData<StatusInformationResult> = profileConfig.switchMap { profileConfig ->
        liveData {
            messagesLoading.pushLoading()
            val response = profileConfig.provider
                .optional<StatusInformationService>()
                ?.statusInformation(filterProducts = productFilter.value)
            if (response is ServiceResult.Failure) {
                response.exception?.printStackTrace()
            }
            emit(response ?: ServiceResult.noResult())
            messagesLoading.popLoading()
        }
    }

    val messages: LiveData<List<Message>> = messagesResult.map { response ->
        (response as? ServiceResult.Success)?.result?.messages ?: emptyList()
    }
    val groupedMessages: LiveData<Map<Line, List<Message>>> = messages.map { messages ->
        messages.flatMap { message -> message.affectedLines.toList() }
            .toSortedSet()
            .associateWithTo(LinkedHashMap()) { line -> messages.filter { message -> message.affectedLines.contains(line) } }
    }

    val selectedLine = MutableLiveData<Line?>()
    val selectedLineMessages: LiveData<List<Message>> = selectedLine.map { line ->
        groupedMessages.value?.get(line) ?: emptyList()
    }

    val selectedMessage = MutableLiveData<Message?>()

}