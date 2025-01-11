package com.minhhnn18898.manage_trip.trip_detail.presentation.expense_tab.main

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.minhhnn18898.architecture.ui.UiState
import com.minhhnn18898.core.utils.isNotBlankOrEmpty
import com.minhhnn18898.manage_trip.R
import com.minhhnn18898.ui_components.base_components.CreateNewDefaultButton
import com.minhhnn18898.ui_components.base_components.DefaultEmptyView
import com.minhhnn18898.ui_components.theme.typography

fun LazyListScope.renderExpenseTabScreen(
    memberInfoContentState: UiState<List<MemberInfoUiState>>,
    receiptInfoUiState: UiState<List<ReceiptWithAllPayersInfoUiState>>,
    onNavigateManageMemberScreen: () -> Unit,
    onNavigateManageReceiptScreen: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    item {
        MemberList(
            memberInfoContentState = memberInfoContentState,
            onClickMemberList = onNavigateManageMemberScreen,
            modifier = modifier
        )
    }

    renderReceiptInfoSection(
        receiptInfoUiState = receiptInfoUiState,
        onClickReceiptDescription = onNavigateManageReceiptScreen,
        onClickCreateNewReceipt = {
            onNavigateManageReceiptScreen(0L)
        },
        modifier = modifier
    )
}

private fun LazyListScope.renderReceiptInfoSection(
    receiptInfoUiState: UiState<List<ReceiptWithAllPayersInfoUiState>>,
    onClickReceiptDescription: (Long) -> Unit,
    onClickCreateNewReceipt: () -> Unit,
    modifier: Modifier = Modifier
) {
    when(receiptInfoUiState) {
        is UiState.Loading -> {
            // Handle loading state (optional)
        }

        is UiState.Error -> {
            // Handle error state (optional)
        }

        is UiState.Success -> {
            renderReceiptContentList(
                receiptInfoUiState = receiptInfoUiState.data,
                onClickReceiptDescription = onClickReceiptDescription,
                onClickCreateNewReceipt = onClickCreateNewReceipt,
                modifier = modifier
            )
        }
    }
}

fun LazyListScope.renderReceiptContentList(
    receiptInfoUiState: List<ReceiptWithAllPayersInfoUiState>,
    onClickCreateNewReceipt: () -> Unit,
    onClickReceiptDescription: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    if(receiptInfoUiState.isEmpty()) {
        item {
            DefaultEmptyView(
                text = stringResource(id = R.string.add_your_receipts),
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth(),
                onClick = onClickCreateNewReceipt
            )
        }
    } else {
        item {
            CreateNewDefaultButton(
                text = stringResource(id = R.string.add_new_receipt),
                modifier = modifier.padding(start = 16.dp),
                onClick = onClickCreateNewReceipt
            )
        }

        items(receiptInfoUiState) { item ->
            ReceiptInfoItem(
                receiptInfoUiState = item,
                onClickReceiptDescription = onClickReceiptDescription,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun ReceiptInfoItem(
    receiptInfoUiState: ReceiptWithAllPayersInfoUiState,
    onClickReceiptDescription: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val stroke = Stroke(
        width = 3f
    )
    val color = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp)
            .clickable {
                onClickReceiptDescription(receiptInfoUiState.receiptInfo.receiptId)
            }
            .drawBehind {
                drawRoundRect(
                    color = color,
                    style = stroke,
                    cornerRadius = CornerRadius(16.dp.toPx())
                )
            }
            .padding(12.dp)
    ) {
        Column {
            ReceiptNameAndAmount(
                receiptOwner = receiptInfoUiState.receiptOwner,
                receiptInfo = receiptInfoUiState.receiptInfo
            )

            Spacer(modifier = Modifier.height(8.dp))

            ReceiptDescriptionAndPayers(
                receiptInfo = receiptInfoUiState.receiptInfo,
                receiptPayers = receiptInfoUiState.receiptPayers
            )
        }
    }
}

@Composable
private fun ReceiptNameAndAmount(
    receiptOwner: MemberInfoUiState,
    receiptInfo: ReceiptInfoUiState,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(receiptOwner.avatarRes),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = receiptInfo.name,
                style = typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2
            )
        }

        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "$${receiptInfo.price}",
                style = typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.tertiary,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(4.dp))


            val pricePerPerson = if(receiptInfo.pricePerPersonDescription.isNotBlankOrEmpty()) {
                "$${receiptInfo.pricePerPersonDescription}/person"
            } else {
                stringResource(id = R.string.no_split)
            }

            Text(
                text = pricePerPerson,
                style = typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary,
                maxLines = 3
            )
        }
    }
}

@Composable
private fun ReceiptDescriptionAndPayers(
    receiptInfo: ReceiptInfoUiState,
    receiptPayers: List<ReceiptPayerInfoUiState>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(modifier = Modifier
            .weight(1f)
            .padding(end = 8.dp)
        ) {
            if(receiptInfo.description.isNotBlankOrEmpty()) {
                Text(
                    text = receiptInfo.description,
                    style = typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            Text(
                text = receiptInfo.createdTime,
                style = typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
                maxLines = 1
            )
        }

        MemberListAvatarInReceipt(
            memberInfos = receiptPayers.map { it.memberInfo },
            modifier = modifier
        )
    }
}

@Composable
private fun MemberList(
    memberInfoContentState: UiState<List<MemberInfoUiState>>,
    onClickMemberList: () -> Unit,
    modifier: Modifier = Modifier
) {
    when(memberInfoContentState) {
        is UiState.Loading -> {
            // Handle loading state (optional)
        }

        is UiState.Error -> {
            // Handle error state (optional)
        }

        is UiState.Success -> {
            MemberInfoContent(
                memberInfos = memberInfoContentState.data,
                onClickMemberList = onClickMemberList,
                modifier = modifier
            )
        }
    }

}

@Composable
private fun MemberInfoContent(
    memberInfos: List<MemberInfoUiState>,
    onClickMemberList: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (memberInfos.isEmpty()) {
        DefaultEmptyView(
            text = stringResource(id = R.string.add_member_to_your_trip),
            modifier = Modifier
                .height(100.dp)
                .fillMaxWidth(),
            onClick = onClickMemberList
        )
    } else {
        MemberListPreview(
            memberInfos = memberInfos,
            onClickMemberList = onClickMemberList,
            modifier = modifier
        )
    }
}

@Composable
private fun MemberListPreview(
    memberInfos: List<MemberInfoUiState>,
    onClickMemberList: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stroke = Stroke(
        width = 3f,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f))
    )
    val color = MaterialTheme.colorScheme.outline
    val itemCount = memberInfos.size
    val maxItemDisplay = 4
    val needDisplayMoreItem = itemCount > maxItemDisplay

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .drawBehind {
                drawRoundRect(
                    color = color,
                    style = stroke,
                    cornerRadius = CornerRadius(100.dp.toPx())
                )
            }
            .clickable {
                onClickMemberList()
            },
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy((8).dp)
        ) {
            memberInfos
                .take(maxItemDisplay)
                .forEach {
                    MemberItem(
                        drawable = it.avatarRes,
                        itemSize = 48.dp,
                        memberName = it.memberName
                    )
                }

            if(needDisplayMoreItem) {
                MemberItemMoreDisplayWithIcon(
                    itemSize = 48.dp,
                    iconSize = 24.dp,
                    moreCount = itemCount - maxItemDisplay
                )
            }
        }
    }
}

@Composable
private fun MemberListAvatarInReceipt(
    memberInfos: List<MemberInfoUiState>,
    modifier: Modifier = Modifier
) {
    val itemCount = memberInfos.size
    val maxItemDisplay = 3
    val needDisplayMoreItem = itemCount > maxItemDisplay

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy((4).dp)
    ) {
        memberInfos
            .take(maxItemDisplay)
            .forEach {
                MemberItem(
                    drawable = it.avatarRes,
                    itemSize = 28.dp,
                    memberName = ""
                )
            }

        if(needDisplayMoreItem) {
            MemberItemMoreDisplayWithText(
                itemSize = 28.dp,
                moreCount = itemCount - maxItemDisplay
            )
        }
    }
}

@Composable
private fun MemberItem(
    @DrawableRes drawable: Int,
    itemSize: Dp,
    memberName: String = ""
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(itemSize)
    ) {
        Image(
            painter = painterResource(drawable),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(itemSize)
                .clip(CircleShape)
        )

        if(memberName.isNotBlankOrEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = memberName,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.secondary,
            )
        }
    }
}

@Composable
private fun MemberItemMoreDisplayWithIcon(
    itemSize: Dp,
    iconSize: Dp,
    moreCount: Int,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(itemSize)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(itemSize)
                .border(1.5.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f), CircleShape)
                .clip(CircleShape)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White,
                            MaterialTheme.colorScheme.tertiaryContainer
                        )
                    )
                )

        ) {
            Icon(
                painter = painterResource(id = com.minhhnn18898.ui_components.R.drawable.group_24),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(iconSize)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "+$moreCount",
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.tertiary,
        )

    }
}

@Composable
private fun MemberItemMoreDisplayWithText(
    itemSize: Dp,
    moreCount: Int,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(itemSize)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(itemSize)
                .border(1.5.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f), CircleShape)
                .clip(CircleShape)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White,
                            MaterialTheme.colorScheme.tertiaryContainer
                        )
                    )
                )

        ) {
            Text(
                text = "+$moreCount",
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.tertiary,
            )
        }
    }
}