package com.midas.whatsapp.Model.data

data class SettingsItem(
    val iconResId: Int,
    val title: String,
    val subtitle: String? = null,
    val actionType: String  // eg., "Profile", "Account", "Notification"
)


