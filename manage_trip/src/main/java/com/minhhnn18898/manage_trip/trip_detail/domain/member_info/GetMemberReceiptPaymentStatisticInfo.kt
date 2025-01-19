package com.minhhnn18898.manage_trip.trip_detail.domain.member_info

import com.minhhnn18898.manage_trip.trip_detail.data.model.expense.MemberReceiptPaymentStatisticInfo
import com.minhhnn18898.manage_trip.trip_detail.data.repo.expense.MemberInfoRepository
import com.minhhnn18898.manage_trip.trip_detail.data.repo.expense.ReceiptRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetMemberReceiptPaymentStatisticInfo @Inject constructor(
    private val memberRepository: MemberInfoRepository,
    private val receiptRepository: ReceiptRepository
) {

    fun execute(tripId: Long): Flow<List<MemberReceiptPaymentStatisticInfo>> {
        return memberRepository.getAllMemberInfoStream(tripId)
            .combine(receiptRepository.getReceiptsStream(tripId)) { members, receipts ->
                // Create mappings for member names and payment info
                val memberNameMappingInfo = members.associateBy({ it.memberId }, { it.memberName })
                val memberPaymentMappingInfo = members.associateBy({ it.memberId }, { Pair(0L, 0L) }).toMutableMap()

                // Process receipts to update payment info
                receipts.forEach { receipt ->
                    // Increase paid amount for the receipt owner
                    memberPaymentMappingInfo.increaseFirstValue(receipt.receiptOwner.memberId, receipt.receiptInfo.price)

                    // Increase owned amounts for the receipt payers
                    receipt.receiptPayers.forEach { payer ->
                        memberPaymentMappingInfo.increaseSecondValue(payer.memberId, payer.payAmount)
                    }
                }

                // Map results to MemberReceiptPaymentStatisticInfo
                memberPaymentMappingInfo.map { (memberId, amounts) ->
                    MemberReceiptPaymentStatisticInfo(
                        memberId = memberId,
                        memberName = memberNameMappingInfo[memberId] ?: "",
                        paidAmount = amounts.first,
                        ownedAmount = amounts.second
                    )
                }
            }
    }
}

private fun MutableMap<Long, Pair<Long, Long>>.increaseFirstValue(id: Long, amount: Long) {
    this[id] = this[id]?.let {
        it.copy(first = it.first + amount)
    } ?: Pair(amount, 0L)
}

private fun MutableMap<Long, Pair<Long, Long>>.increaseSecondValue(id: Long, amount: Long) {
    this[id] = this[id]?.let {
        it.copy(second = it.second + amount)
    } ?: Pair(amount, 0L)
}