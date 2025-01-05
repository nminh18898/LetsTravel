package com.minhhnn18898.manage_trip.trip_detail.presentation.expense_tab.manage_member

import androidx.annotation.DrawableRes
import com.minhhnn18898.manage_trip.R

interface ManageMemberResourceProvider {

    @DrawableRes
    fun getAvatarResource(avatarId: Int): Int
}

class ManageMemberResourceProviderImpl: ManageMemberResourceProvider {

    private val memberAvatarResourceList = listOf(
        R.drawable.avatar_skunk,
        R.drawable.avatar_koala,
        R.drawable.avatar_porcupine,
        R.drawable.avatar_deer,
        R.drawable.avatar_otter,
        R.drawable.avatar_wild_boar,
        R.drawable.avatar_hippo,
        R.drawable.avatar_penguin,
        R.drawable.avatar_panda,
        R.drawable.avatar_platypus
    )

    override fun getAvatarResource(avatarId: Int): Int {
        return memberAvatarResourceList[avatarId % memberAvatarResourceList.size]
    }

}