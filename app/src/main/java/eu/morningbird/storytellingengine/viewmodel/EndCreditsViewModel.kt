package eu.morningbird.storytellingengine.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import eu.morningbird.storytellingengine.model.Credit
import eu.morningbird.storytellingengine.model.Member
import eu.morningbird.storytellingengine.model.Settings
import eu.morningbird.storytellingengine.model.repository.CreditRepository
import eu.morningbird.storytellingengine.model.repository.MemberRepository
import eu.morningbird.storytellingengine.model.repository.PlotRepository
import eu.morningbird.storytellingengine.model.util.NavigationDirections
import eu.morningbird.storytellingengine.util.Event
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class EndCreditsViewModel : ViewModel() {
    val navigationEvent: MutableLiveData<Event<NavigationDirections>> = MutableLiveData()

    val credit: MutableLiveData<Pair<Credit, List<Member>>> = MutableLiveData()

    val isLoaded: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val isFinished: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val showThankYou: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val showEngineInfo: MutableLiveData<Event<Boolean>> = MutableLiveData()

    val music: MutableLiveData<String> = MutableLiveData(Settings.Assets.CREDITS_MUSIC)

    private val credits: MutableList<Pair<Credit, List<Member>>> = ArrayList()
    private var currentCredit = 0

    init {
        GlobalScope.launch {
            if (PlotRepository.load()) {
                credits.clear()
                currentCredit = 0
                val credits = CreditRepository.get()
                credits?.let {
                    for (credit in credits) {
                        val members = MemberRepository.get(credit)
                        if (!members.isNullOrEmpty()) this@EndCreditsViewModel.credits.add(
                            Pair(
                                credit,
                                members
                            )
                        )
                    }
                }
                isLoaded.postValue(Event(true))
            }
        }
    }

    fun advance() {
        when {
            currentCredit < credits.size -> {
                credit.postValue(credits[currentCredit])
                currentCredit += 1
            }
            currentCredit == credits.size -> {
                showEngineInfo.postValue(Event(true))
                currentCredit += 1
            }
            currentCredit == credits.size + 1 -> {
                showThankYou.postValue(Event(true))
                currentCredit += 1
            }
            else -> {
                isFinished.postValue(Event(true))
            }
        }
    }
}