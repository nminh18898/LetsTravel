package com.minhhnn18898.letstravel.tripdetail.presentation.activity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.minhhnn18898.ui_components.R
import com.minhhnn18898.ui_components.theme.typography

@Composable
fun TripActivityScreen(modifier: Modifier) {

    Column(modifier = modifier.padding(horizontal = 16.dp)) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = "https://www.yttags.com/blog/wp-content/uploads/2023/02/image-urls-for-testing.webp",
                contentDescription = "",
                modifier = Modifier
                    .width(132.dp)
                    .aspectRatio(1.2f),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.image_placeholder),
                error = painterResource(id = R.drawable.empty_image_bg)
            )

            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = "Chao Phraya Princess Dinner Cruise",
                    style = typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "Embark on an unforgettable evening with the Chao Phraya Princess Dinner Cruise departing from ICONSIAM Pier. This world-class dinner cruise takes you on a 2-hour journey along the Chao Phraya River, offering breathtaking views of Bangkok’s iconic landmarks, including the Temple of Dawn and The Grand Palace, beautifully illuminated under the night sky.",
                    style = typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column(modifier = Modifier.wrapContentSize()) {

                Text(
                    text = "Wed, 16 August",
                    style = typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                HorizontalDivider(
                    thickness = 1.dp,
                    modifier = Modifier
                        .width(60.dp)
                        .padding(vertical = 8.dp)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "2 hours",
                        style = typography.bodyMedium,
                        color = MaterialTheme.colorScheme.tertiary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "8:00 - 10:00",
                        style = typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Text(
                text = "2,500,000",
                style = typography.bodyLarge,
                color = MaterialTheme.colorScheme.tertiary,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
        }

    }
}