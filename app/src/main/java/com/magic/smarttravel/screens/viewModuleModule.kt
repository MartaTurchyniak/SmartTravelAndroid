package com.magic.smarttravel.screens

import com.magic.smarttravel.screens.auth.SignInViewModel
import com.magic.smarttravel.screens.auth.UserEditViewModel
import com.magic.smarttravel.screens.chat.GroupChatViewModel
import com.magic.smarttravel.screens.groups.GroupsViewModel
import com.magic.smarttravel.screens.groups.group_details.GroupDetailsViewModel
import com.magic.smarttravel.screens.new_group.NewGroupViewModel
import com.magic.smarttravel.screens.profile.ProfileViewModel
import com.magic.smarttravel.screens.tracking.MapViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Created by Marta Turchyniak on 12/6/20.
 */
val viewModelModule = module {
    viewModel { UserEditViewModel(get(), get(), get(), get()) }
    viewModel { ProfileViewModel(get(), get()) }
    viewModel { NewGroupViewModel(get(), get(), get(), get()) }
    viewModel { GroupsViewModel(get(), get(), get()) }
    viewModel { MapViewModel(get(), get(), get()) }
    viewModel { GroupChatViewModel(get(), get(), get(), get(), get()) }
    viewModel { GroupDetailsViewModel(get(), get(), get()) }
    viewModel { SignInViewModel(get(), get()) }
}