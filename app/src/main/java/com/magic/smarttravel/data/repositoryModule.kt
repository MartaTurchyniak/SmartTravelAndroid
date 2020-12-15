package com.magic.smarttravel.data

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Created by Marta Turchyniak on 12/6/20.
 */
val repositoryModule = module {
    single { UserRepository() }
    single { GroupRepository() }
    single { InviteRepository() }
    single { GroupMessageRepository() }
    single { AuthorizationManager() }
    single { SharedPrefs(androidContext()) }
}