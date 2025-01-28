package com.minhhnn18898.manage_trip.trip_detail.domain.expense_tab.member_info

import com.minhhnn18898.manage_trip.trip_detail.data.model.expense.MemberInfo
import com.minhhnn18898.manage_trip.trip_detail.data.repo.expense.MemberInfoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllMembersUseCase @Inject constructor(private val repository: MemberInfoRepository) {

    fun execute(tripId: Long) : Flow<List<MemberInfo>> = repository.getAllMemberInfoStream(tripId)
}