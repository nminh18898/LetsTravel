package com.minhhnn18898.manage_trip.trip_detail.presentation.expense_tab.mange_bill

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.minhhnn18898.manage_trip.R
import com.minhhnn18898.manage_trip.trip_detail.presentation.expense_tab.main.memberAvatarList
import com.minhhnn18898.ui_components.theme.typography

@Composable
fun ManageBillView(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(16.dp)) {
        BillInfo(Modifier.padding(horizontal = 8.dp))
        
        Spacer(modifier = Modifier.height(24.dp))

        BillSplitSectionDescription()

        Spacer(modifier = Modifier.height(8.dp))

        BillSplitOptions()
    }
}

@Composable
private fun BillInfo(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BillHeaderIcon()

            Spacer(modifier = Modifier.height(4.dp))

            BillNameAndDate()

            Spacer(modifier = Modifier.height(16.dp))

            BillPriceAndDescription()

            Spacer(modifier = Modifier.height(20.dp))

            PaidByBadge()
        }
    }
}

@Composable
private fun BillHeaderIcon() {
    Box(modifier = Modifier
        .size(48.dp)
        .clip(CircleShape)
        .background(color = Color(0xB300AB41)),
        contentAlignment = Alignment.Center
    ) {

        Icon(
            modifier = Modifier.size(28.dp),
            painter = painterResource(id = R.drawable.receipt_long_24),
            contentDescription = "",
            tint = Color.White
        )
    }
}

@Composable
private fun BillNameAndDate(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            singleLine = true,
            modifier = modifier.fillMaxWidth(),
            value = "",
            onValueChange = {

            },
            label = { Text(stringResource(com.minhhnn18898.core.R.string.receipt_name)) },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.receipt_24),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "12 Dec 2024 | 09:30",
                style = typography.titleSmall,
                color = MaterialTheme.colorScheme.secondary,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                painter = painterResource(id = R.drawable.edit_calendar_24),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.secondary
            )
        }

    }
}

@Composable
private fun BillPriceAndDescription(
    modifier: Modifier = Modifier
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Pricing and details of the receipt:",
            style = typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )

        var priceText by remember { mutableStateOf(TextFieldValue("")) }
        TextField(
            value = priceText,
            onValueChange = { newText ->
                priceText = newText
            },
            modifier = modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = {
                Icon(
                    painter = painterResource(id = com.minhhnn18898.ui_components.R.drawable.attach_money_24),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.secondary
                )
            },
            placeholder = {
                Text(
                    text= "00.00",
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)
                )
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        var descriptionText by remember { mutableStateOf(TextFieldValue("")) }
        TextField(
            value = descriptionText,
            onValueChange = { newText ->
                descriptionText = newText
            },
            modifier = modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.text_snippet_24),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.secondary
                )
            },
            placeholder = {
                Text(
                    text= "Add description here...",
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f),
                    style = typography.bodyMedium,
                )
            }
        )
    }
}

@Composable
private fun PaidByBadge() {
    val stroke = Stroke(width = 4f)
    val color = Color(0xFF00AB41)

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopEnd
    ) {
        Row(
            modifier = Modifier
                .border(
                    width = 2.dp,
                    shape = RoundedCornerShape(corner = CornerSize(16.dp)),
                    color = color
                )
                .padding(vertical = 4.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Paid by",
                style = typography.titleSmall,
                color = color
            )

            Spacer(modifier = Modifier.width(8.dp))

            Image(
                painter = painterResource(memberAvatarList.first()),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Icon(
                modifier = Modifier.size(20.dp),
                painter = painterResource(id = com.minhhnn18898.ui_components.R.drawable.arrow_drop_down_24),
                contentDescription = "",
                tint = color
            )
        }
    }

}

@Composable
fun BillSplitSectionDescription() {
    Text(
        text = "Choose how you want to split this receipt",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.secondary
    )
}

@Composable
fun BillSplitOptions() {
    val radioOptions = listOf("Split evenly among all members", "Adjust amounts for each member", "Do not split")
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }

    Column(Modifier.selectableGroup()) {
        radioOptions.forEach { text ->
            val isSelected = text == selectedOption

            Row(
                Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .selectable(
                        selected = isSelected,
                        onClick = { onOptionSelected(text) },
                        role = Role.RadioButton
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (text == selectedOption),
                    onClick = null,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = MaterialTheme.colorScheme.primary,
                        unselectedColor = MaterialTheme.colorScheme.secondary
                    )
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 8.dp),
                    color = if(isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}