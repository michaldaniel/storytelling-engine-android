package eu.morningbird.storytellingengine.model.repository

import eu.morningbird.storytellingengine.MyApplication
import eu.morningbird.storytellingengine.model.Credit

object CreditRepository {
    suspend fun get(): List<Credit>? {
        return MyApplication.database.creditDao().get()?.sortedBy { it.order }
    }

    suspend fun save(credit: Credit, order: Int) {
        credit.order = order
        MyApplication.database.creditDao().insert(credit)
        credit.members?.let { members ->
            for ((index, member) in members.withIndex()) {
                MemberRepository.save(member, credit, index)
            }
        }

    }
}