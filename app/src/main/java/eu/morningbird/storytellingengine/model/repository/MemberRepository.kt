package eu.morningbird.storytellingengine.model.repository

import eu.morningbird.storytellingengine.MyApplication
import eu.morningbird.storytellingengine.model.Credit
import eu.morningbird.storytellingengine.model.Member

object MemberRepository {
    suspend fun save(name: String, credit: Credit, order: Int) {
        val member = Member(null, order, name, credit.order)
        MyApplication.database.memberDao().insert(member)
    }

    suspend fun get(credit: Credit): List<Member>? {
        return MyApplication.database.memberDao().get(credit.order)?.sortedBy { it.order }
    }

}