package com.minhhnn18898.manage_trip.trip_detail.presentation.expense_tab.mange_bill

import androidx.annotation.DrawableRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.minhhnn18898.app_navigation.destination.ManageBillDestination
import com.minhhnn18898.app_navigation.destination.ManageBillDestinationParameters
import com.minhhnn18898.app_navigation.mapper.CustomNavType
import com.minhhnn18898.core.utils.DateTimeProvider
import com.minhhnn18898.core.utils.isNotBlankOrEmpty
import com.minhhnn18898.core.utils.safeDiv
import com.minhhnn18898.manage_trip.trip_detail.domain.default_bill_owner.GetTripDefaultBillOwnerStreamUseCase
import com.minhhnn18898.manage_trip.trip_detail.domain.member_info.GetAllMembersUseCase
import com.minhhnn18898.manage_trip.trip_detail.presentation.expense_tab.main.MemberInfoSelectionUiState
import com.minhhnn18898.manage_trip.trip_detail.presentation.expense_tab.main.MemberInfoUiState
import com.minhhnn18898.manage_trip.trip_detail.presentation.expense_tab.main.ReceiptPayerInfoUiState
import com.minhhnn18898.manage_trip.trip_detail.presentation.expense_tab.main.toMemberInfoUiState
import com.minhhnn18898.manage_trip.trip_detail.presentation.expense_tab.manage_member.ManageMemberResourceProvider
import com.minhhnn18898.manage_trip.trip_detail.presentation.trip.TripDetailDateTimeFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.typeOf

data class ReceiptUiState(
    val name: String = "",
    val description: String = "",
    val prices: String = "",
    val formattedDate: String = "",
    val dateCreated: Long? = null,
    val timeCreated: Pair<Int, Int> = Pair(0, 0),
    @DrawableRes val receiptOwnerAvatar: Int = 0
)

data class UpdateReceiptOwnerUiState(
    val listMemberReceiptOwnerSelection: List<MemberInfoSelectionUiState> = emptyList()
)

data class ReceiptSplittingUiState(
    val splittingMode: ManageReceiptViewModel.SplittingMode = ManageReceiptViewModel.SplittingMode.EVENLY,
    val payers: List<ReceiptPayerInfoUiState> = emptyList()
)

data class ManageReceiptUiState(
    val receiptUiState: ReceiptUiState = ReceiptUiState(),
    val updateReceiptOwnerUiState: UpdateReceiptOwnerUiState = UpdateReceiptOwnerUiState(),
    val receiptOwner: MemberInfoUiState? = null,
    val isLoading: Boolean = false,
    val isNotFound: Boolean = false,
    val canDelete: Boolean = false,
    val isShowDeleteConfirmation: Boolean = false,
    val showError: ManageReceiptViewModel.ErrorType = ManageReceiptViewModel.ErrorType.ERROR_MESSAGE_NONE,
    val isCreated: Boolean = false,
    val isUpdated: Boolean = false,
    val isDeleted: Boolean = false,
    val allowSaveContent: Boolean = false
)

@HiltViewModel
class ManageReceiptViewModel@Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val resourceProvider: ManageMemberResourceProvider,
    private val getAllMembersUseCase: GetAllMembersUseCase,
    private val getTripDefaultBillOwnerStreamUseCase: GetTripDefaultBillOwnerStreamUseCase,
    private val dateTimeFormatter: TripDetailDateTimeFormatter,
    private val dateTimeProvider: DateTimeProvider
): ViewModel() {

    private val parameters = savedStateHandle.toRoute<ManageBillDestination>(
        typeMap = mapOf(typeOf<ManageBillDestinationParameters>() to CustomNavType(ManageBillDestinationParameters::class.java, ManageBillDestinationParameters.serializer()))
    ).parameters

    val tripId = parameters.tripId
    val receiptId = parameters.receiptId

    private val _receiptInfoUiState = MutableStateFlow(
        ManageReceiptUiState(
            receiptUiState = ReceiptUiState(
                formattedDate = dateTimeFormatter.getFormattedReceiptCreatedDate(dateTimeProvider.currentTimeMillis())
            )
        )
    )
    val receiptInfoUiState: StateFlow<ManageReceiptUiState> = _receiptInfoUiState.asStateFlow()

    private val _receiptSplittingUiState = MutableStateFlow(ReceiptSplittingUiState())
    val receiptSplittingUiState: StateFlow<ReceiptSplittingUiState> = _receiptSplittingUiState.asStateFlow()

    init {
        loadListMemberInfo()
    }

    private fun checkAllowSaveContent() {
        _receiptInfoUiState.update {
            it.copy(allowSaveContent = isAllowSave())
        }
    }

    private fun isAllowSave(): Boolean {
        return receiptInfoUiState.value.receiptUiState.name.isNotBlankOrEmpty()
                && receiptInfoUiState.value.receiptUiState.prices.isNotBlankOrEmpty()
    }

    fun onNameUpdated(value: String) {
        _receiptInfoUiState.update {
            it.copy(
                receiptUiState = it.receiptUiState.copy(name = value)
            )
        }
        checkAllowSaveContent()
    }

    fun onDescriptionUpdated(value: String) {
        _receiptInfoUiState.update {
            it.copy(
                receiptUiState = it.receiptUiState.copy(description = value)
            )
        }
    }

    fun onPricesUpdated(value: String) {
        _receiptInfoUiState.update {
            it.copy(
                receiptUiState = it.receiptUiState.copy(prices = value)
            )
        }
        updatePayersAmount(receiptSplittingUiState.value.splittingMode)
        checkAllowSaveContent()
    }

    fun onDateUpdated(value: Long?) {
        _receiptInfoUiState.update { state ->
            state.copy(
                receiptUiState = state.receiptUiState.copy(
                    dateCreated = value,
                    formattedDate = dateTimeFormatter.getFormattedReceiptCreatedDate(
                        getDateTimeMillis(
                            date = value ?: dateTimeProvider.currentTimeMillis(),
                            time = state.receiptUiState.timeCreated
                        )
                    )
                )
            )
        }
    }

    fun onTimeUpdated(value: Pair<Int, Int>) {
        _receiptInfoUiState.update { state ->
            state.copy(
                receiptUiState = state.receiptUiState.copy(
                    timeCreated = value,
                    formattedDate = dateTimeFormatter.getFormattedReceiptCreatedDate(
                        getDateTimeMillis(
                            date = state.receiptUiState.dateCreated ?: dateTimeProvider.currentTimeMillis(),
                            time = value
                        )
                    )
                )
            )
        }
    }

    private fun getDateTimeMillis(date: Long, time: Pair<Int, Int>): Long {
        return dateTimeFormatter.combineHourMinutesDayToMillis(date, time.first, time.second)
    }

    fun onSelectNewReceiptOwner(member: MemberInfoUiState) {
        val currentUiState = receiptInfoUiState.value.updateReceiptOwnerUiState
        val updatedList = currentUiState.listMemberReceiptOwnerSelection.map {
            it.copy(isSelected = it.memberInfo.memberId == member.memberId)
        }

        val updatedReceiptOwnerUiState = currentUiState.copy(
            listMemberReceiptOwnerSelection = updatedList
        )

        _receiptInfoUiState.update { state ->
            state.copy(
                updateReceiptOwnerUiState = updatedReceiptOwnerUiState,
                receiptOwner = member
            )
        }
    }

    private fun loadListMemberInfo() {
        viewModelScope.launch {
            getAllMembersUseCase
                .execute(tripId)
                .combine(getTripDefaultBillOwnerStreamUseCase.execute(tripId)) { memberInfo, defaultBillOwner ->
                    Pair(memberInfo, defaultBillOwner)
                }
                .collect { (memberInfo, defaultBillOwner) ->
                    val listMember = memberInfo.map {
                        it.toMemberInfoUiState(
                            manageMemberResourceProvider = resourceProvider,
                            isDefaultBillOwner = defaultBillOwner?.memberId == it.memberId
                        )
                    }

                    _receiptInfoUiState.update { state ->
                        state.copy(
                            updateReceiptOwnerUiState = UpdateReceiptOwnerUiState(
                                listMemberReceiptOwnerSelection = listMember.map {
                                    MemberInfoSelectionUiState(
                                        memberInfo = it,
                                        isSelected = it.isDefaultBillOwner
                                    )
                                }
                            ),
                            receiptOwner = listMember.firstOrNull { it.isDefaultBillOwner }
                        )
                    }

                    _receiptSplittingUiState.update { state ->
                        state.copy(
                            splittingMode = SplittingMode.EVENLY,
                            payers = listMember
                                .toReceiptPayerInfoUiState(receiptInfoUiState
                                .value
                                .receiptUiState
                                .prices
                                .toLongOrNull() ?: 0L
                            )
                        )
                    }
                }
        }
    }

    private fun updatePayersAmount(splittingMode: SplittingMode) {
        when(splittingMode) {
            SplittingMode.EVENLY -> {
                updatePayerAmountModeEvenly()
            }

            SplittingMode.CUSTOM -> {
                updatePayerAmountModeCustom()
            }

            SplittingMode.NO_SPLIT -> {
                updatePayerAmountModeNoSplit()
            }
        }
    }

    private fun updatePayerAmountModeEvenly() {
        val currentPayers = receiptSplittingUiState.value.payers

        val totalAmount = receiptInfoUiState.value.receiptUiState.prices.toLongOrNull() ?: 0L
        val amountPerPerson = totalAmount.safeDiv(currentPayers.size)

        _receiptSplittingUiState.update { state ->
            state.copy(
                splittingMode = SplittingMode.EVENLY,
                payers = currentPayers.map {
                    it.copy(
                        payAmount = amountPerPerson.toString()
                    )
                }
            )
        }
    }

    private fun updatePayerAmountModeCustom() {
        _receiptSplittingUiState.update { state ->
            state.copy(
                splittingMode = SplittingMode.CUSTOM
            )
        }
    }

    private fun updatePayerAmountModeNoSplit() {
        val currentPayers = receiptSplittingUiState.value.payers

        _receiptSplittingUiState.update { state ->
            state.copy(
                splittingMode = SplittingMode.NO_SPLIT,
                payers = currentPayers.map {
                    it.copy(
                        payAmount = 0L.toString()
                    )
                }
            )
        }
    }

    fun onUpdateReceiptSplittingOption(splittingMode: SplittingMode) {
        if(splittingMode == receiptSplittingUiState.value.splittingMode) {
            return
        }

        updatePayersAmount(splittingMode)
    }

    fun onUpdateCustomAmount(memberId: Long, customAmount: String) {
        val uiState = receiptSplittingUiState.value
        val receiptPrices = receiptInfoUiState.value.receiptUiState.prices.toLongOrNull() ?: 0L
        val currentPayers = uiState.payers

        // Return early if the splitting mode doesn't allow amount changes
        if (!uiState.splittingMode.canChangeAmount()) return

        // Find the most frequent pay amount and its frequency
        val mostFrequentPayAmount = findMostFrequentPayAmount(currentPayers)

        // Calculate the total excluding the most frequent amount
        val totalExcludingMostFrequent = calculateTotalExcludingMostFrequent(
            currentPayers,
            memberId,
            customAmount,
            mostFrequentPayAmount?.first
        )

        // Calculate the new most frequent amount
        val newMostFrequentAmount = calculateNewMostFrequentAmount(
            totalPrices = receiptPrices,
            totalExcludingMostFrequent = totalExcludingMostFrequent,
            frequency = mostFrequentPayAmount?.second
        )

        // Update the UI state with the new amounts
        updateUiStateWithNewAmounts(
            currentPayers,
            memberId,
            customAmount,
            mostFrequentPayAmount?.first ?: 0L,
            newMostFrequentAmount
        )
    }

    private fun findMostFrequentPayAmount(payers: List<ReceiptPayerInfoUiState>): Pair<Long, Int>? {
        return payers
            .groupBy { it.payAmount }
            .maxByOrNull { it.value.size }
            ?.let { (key, value) -> (key.toLongOrNull() ?: 0L) to value.size }
    }

    private fun calculateTotalExcludingMostFrequent(
        payers: List<ReceiptPayerInfoUiState>,
        memberId: Long,
        customAmount: String,
        mostFrequentAmount: Long?
    ): Long {
        return payers
            .map { it.copy(payAmount = if (it.memberInfo.memberId == memberId) customAmount else it.payAmount) }
            .filter { it.payAmount.toLongOrNull() != mostFrequentAmount }
            .sumOf { it.payAmount.toLongOrNull() ?: 0L }
    }

    private fun calculateNewMostFrequentAmount(totalPrices: Long, totalExcludingMostFrequent: Long, frequency: Int?): Long {
        return (totalPrices - totalExcludingMostFrequent).safeDiv(frequency ?: 1)
    }

    private fun updateUiStateWithNewAmounts(
        payers: List<ReceiptPayerInfoUiState>,
        memberId: Long,
        customAmount: String,
        mostFrequentAmount: Long,
        newMostFrequentAmount: Long
    ) {
        _receiptSplittingUiState.update { state ->
            state.copy(
                payers = payers.map {
                    it.copy(
                        payAmount = when {
                            it.memberInfo.memberId == memberId -> customAmount
                            it.payAmount.toLongOrNull() == mostFrequentAmount -> newMostFrequentAmount.toString()
                            else -> it.payAmount
                        }
                    )
                }
            )
        }
    }

    enum class ErrorType {
        ERROR_MESSAGE_NONE,
        ERROR_MESSAGE_CAN_NOT_CREATE_RECEIPT,
        ERROR_MESSAGE_CAN_NOT_UPDATE_RECEIPT,
        ERROR_MESSAGE_CAN_NOT_DELETE_RECEIPT,
        ERROR_MESSAGE_CAN_NOT_UPDATE_RECEIPT_OWNER
    }

    enum class SplittingMode {
        EVENLY,
        CUSTOM,
        NO_SPLIT
    }
}

private fun List<MemberInfoUiState>.toReceiptPayerInfoUiState(
    payAmount: Long
): List<ReceiptPayerInfoUiState> {
    return this.map {
        ReceiptPayerInfoUiState(
            memberInfo = it,
            payAmount = payAmount
                .safeDiv(this.size)
                .toString()
        )
    }
}

fun ManageReceiptViewModel.SplittingMode.canChangeAmount(): Boolean {
    return this == ManageReceiptViewModel.SplittingMode.CUSTOM
}
