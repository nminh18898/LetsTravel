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
import com.minhhnn18898.ui_components.base_components.DefaultEmptyView
import com.minhhnn18898.ui_components.theme.typography

fun LazyListScope.renderExpenseTabScreen(
    memberInfoContentState: UiState<List<MemberInfoUiState>>,
    receiptInfoUiState: UiState<List<ReceiptWithAllPayersInfoUiState>>,
    onNavigateManageMemberScreen: () -> Unit,
    onNavigateManageBillScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    item {
        MemberList(
            memberInfoContentState = memberInfoContentState,
            onClickMemberList = onNavigateManageMemberScreen,
            modifier = modifier
        )
    }

    item {
        ReceiptInfo(
            receiptInfoUiState = receiptInfoUiState,
            onClickBillDescription = onNavigateManageBillScreen,
            modifier = modifier
        )
    }
}

@Composable
private fun ReceiptInfo(
    receiptInfoUiState: UiState<List<ReceiptWithAllPayersInfoUiState>>,
    onClickBillDescription: () -> Unit,
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
            ReceiptInfoContent(
                receiptInfoUiState = receiptInfoUiState.data,
                onClickBillDescription = onClickBillDescription,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun ReceiptInfoContent(
    receiptInfoUiState: List<ReceiptWithAllPayersInfoUiState>,
    onClickBillDescription: () -> Unit,
    modifier: Modifier = Modifier
) {
    if(receiptInfoUiState.isEmpty()) {
        DefaultEmptyView(
            text = stringResource(id = R.string.add_your_receipts),
            modifier = Modifier
                .height(100.dp)
                .fillMaxWidth(),
            onClick = onClickBillDescription
        )
    } else {
        ReceiptInfoList(
            receiptInfoUiState = receiptInfoUiState,
            onClickBillDescription = onClickBillDescription,
            modifier = modifier
        )
    }
}

@Composable
private fun ReceiptInfoList(
    receiptInfoUiState: List<ReceiptWithAllPayersInfoUiState>,
    onClickBillDescription: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stroke = Stroke(
        width = 2f
    )
    val color = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp)
            .drawBehind {
                drawRoundRect(
                    color = color,
                    style = stroke,
                    cornerRadius = CornerRadius(16.dp.toPx())
                )
            }
            .padding(12.dp)
            .clickable {
                onClickBillDescription()
            }
    ) {
        Column {
            BillHeader()
            Spacer(modifier = Modifier.height(8.dp))
            BillDescription()
        }
    }
}

@Composable
private fun BillHeader(modifier: Modifier = Modifier) {
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
                painter = painterResource(memberAvatarList.first()),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do",
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
                text = "$35.22",
                style = typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.tertiary,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "$11.74/person",
                style = typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun BillDescription(
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
            Text(
                text = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book",
                style = typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
                overflow = TextOverflow.Ellipsis,
                maxLines = 3
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "17/11/2024 - 10:52",
                style = typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
                maxLines = 1
            )
        }

        Row(
            modifier = Modifier
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            memberAvatarList.forEach {
                MemberItem(it, 24.dp)
            }
        }
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
                onClickMemberList = onClickMemberList
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

    val maxItemDisplay = 4
    val itemCount = memberInfos.size
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
                MemberItemMoreDisplay(
                    itemSize = 48.dp,
                    moreCount = itemCount - maxItemDisplay
                )
            }
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
private fun MemberItemMoreDisplay(
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
            Icon(
                painter = painterResource(id = com.minhhnn18898.ui_components.R.drawable.group_24),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(24.dp)
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

val memberAvatarList = listOf(
    R.drawable.avatar_skunk,
    R.drawable.avatar_porcupine,
    R.drawable.avatar_deer,
    R.drawable.avatar_otter
)