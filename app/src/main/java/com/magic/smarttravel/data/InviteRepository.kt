package com.magic.smarttravel.data

import com.magic.smarttravel.entity.GroupInvite

/**
 * Created by Marta Turchyniak on 12/8/20.
 */
class InviteRepository : FirebaseRepository<GroupInvite>(GroupInvite::class.java) {

    override fun table() = "invites"
}