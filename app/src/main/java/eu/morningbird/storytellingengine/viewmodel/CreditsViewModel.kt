package eu.morningbird.storytellingengine.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import eu.morningbird.storytellingengine.model.repository.CreditRepository
import eu.morningbird.storytellingengine.model.repository.MemberRepository
import eu.morningbird.storytellingengine.model.repository.PlotRepository
import eu.morningbird.storytellingengine.model.util.NavigationDirections
import eu.morningbird.storytellingengine.util.Event
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class CreditsViewModel : ViewModel() {
    val navigationEvent: MutableLiveData<Event<NavigationDirections>> = MutableLiveData()
    val isLoaded: MutableLiveData<Boolean> = MutableLiveData(false)
    val credits: MutableLiveData<List<Any>> = MutableLiveData()

    init {
        GlobalScope.launch {
            if (PlotRepository.load()) {
                val creditsWithMembers: MutableList<Any> = ArrayList()
                val credits = CreditRepository.get()
                credits?.let {
                    for (credit in credits) {
                        creditsWithMembers.add(credit)
                        val members = MemberRepository.get(credit)
                        if (!members.isNullOrEmpty()) creditsWithMembers.addAll(members)
                    }
                }
                this@CreditsViewModel.credits.postValue(creditsWithMembers)
                isLoaded.postValue(true)
            }
        }
    }

}