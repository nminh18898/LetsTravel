package com.minhhnn18898.letstravel.app.appbar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.minhhnn18898.core.utils.isNotBlankOrEmpty
import com.minhhnn18898.signin.data.model.UserInfo
import com.minhhnn18898.ui_components.R
import com.minhhnn18898.core.R.string as CommonStringRes
import com.minhhnn18898.ui_components.R.drawable as CommonDrawableRes

@Composable
fun AppDrawer(
    onNavigateToSignInScreen: () -> Unit,
    viewModel: AppDrawerViewModel = hiltViewModel()
) {
    ModalDrawerSheet(modifier = Modifier) {
        DrawerHeader(viewModel.userInfo)
        Spacer(modifier = Modifier.padding(4.dp))
        AuthDrawerItem(
            userInfo = viewModel.userInfo,
            onClickSignIn = onNavigateToSignInScreen,
            onClickSignOut = {
                viewModel.onClickSignOut()
            }
        )
    }
}

@Composable
fun DrawerHeader(
    userInfo: UserInfo?,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
        modifier = modifier
            .background(MaterialTheme.colorScheme.secondary)
            .padding(16.dp)
            .fillMaxWidth()
    ) {

        Image(
            painterResource(id = R.drawable.default_profile_picture),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .size(68.dp)
        )
        Spacer(modifier = Modifier.padding(4.dp))

        if(userInfo != null) {
            val displayName = userInfo.displayName
            val email = userInfo.email

            if(displayName.isNotBlankOrEmpty()) {
                Spacer(modifier = Modifier.padding(4.dp))

                Text(
                    text = displayName,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }

            if(email.isNotBlankOrEmpty()) {
                Spacer(modifier = Modifier.padding(4.dp))

                Text(
                    text = email,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }
    }
}

@Composable
private fun AuthDrawerItem(
    userInfo: UserInfo?,
    onClickSignIn: () -> Unit,
    onClickSignOut: () -> Unit
) {
    val isLoggedIn = userInfo != null

    NavigationDrawerItem(
        label = {
            Text(
                text = stringResource(id = if(isLoggedIn) CommonStringRes.sign_out else CommonStringRes.sign_in),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        },
        selected = false,
        onClick = {
            if(isLoggedIn) onClickSignOut.invoke() else onClickSignIn.invoke()
        },
        icon = {
            Icon(
                painter = painterResource(id = if(isLoggedIn) CommonDrawableRes.logout_24 else CommonDrawableRes.login_24),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    )
}