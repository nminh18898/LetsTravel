package com.minhhnn18898.app_navigation.destination

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
data class BillSplitManageMemberDestination(val parameters: BillSplitManageMemberDestinationParameters)

@Serializable
@Parcelize
data class BillSplitManageMemberDestinationParameters(
    val tripId: Long,
    val tripName: String,
): Parcelable

@Serializable
data class ManageBillDestination(val parameters: ManageBillDestinationParameters) {
    companion object {
        val title = com.minhhnn18898.core.R.string.receipt
    }
}

@Serializable
@Parcelize
data class ManageBillDestinationParameters(
    val tripId: Long,
    val receiptId: Long = 0
): Parcelable