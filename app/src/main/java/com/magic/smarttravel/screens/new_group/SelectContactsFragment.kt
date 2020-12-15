package com.magic.smarttravel.screens.new_group

import android.Manifest
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.magic.smarttravel.R
import com.magic.smarttravel.util.Permissions
import com.magic.smarttravel.util.permissionsGranted
import com.magic.smarttravel.util.requestPermissions
import kotlinx.android.synthetic.main.fragment_select_contacts.*
import kotlinx.android.synthetic.main.fragment_select_contacts.view.*
import pub.devrel.easypermissions.AfterPermissionGranted
import java.io.InputStream
import java.io.Serializable

const val SELECTED_CONTACTS = "selected_contacts"
class SelectContactsFragment : Fragment() {

    lateinit var selectedContactsList: ContactList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedContactsList = ContactList(mutableListOf())
    }

    @AfterPermissionGranted(Permissions.REQUEST_CODE_CONTACTS)
    private fun showContacts() {
        context?.let {
            if (it.permissionsGranted(Permissions.PERMISSIONS_CONTACT_STORAGE)) {
                val list =  getContacts(it)
                initSelectedValues(list)
                initAdapter(list)
            } else {
            requestPermissions(
                Permissions.PERMISSIONS_CONTACT_STORAGE,
                Permissions.REQUEST_CODE_CONTACTS,
                getString(R.string.general_storage_rationale)
            )
        }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            showContacts()
        }
    }

    private fun initSelectedValues(list: MutableList<ContactModel>?) {
        if (selectedContactsList.list.size > 0) {
            list?.forEachIndexed { index, contactModel ->
                selectedContactsList.list.forEach { item ->
                    if (index == item.index) {
                        contactModel.isSelected = true
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.arrow.setOnClickListener {
            activity?.onBackPressed()
        }
        if (selectedContactsList.list.size > 0){
            toolbar.counter.text = selectedContactsList.list.size.toString()
        }
        showContacts()
        actionButton.setOnClickListener {
            if(selectedContactsList.list.size > 0) {
                actionButton.findNavController().navigate(R.id.newGroupFragment, Bundle().apply {
                    putSerializable(SELECTED_CONTACTS, selectedContactsList)
                })
            }
        }
    }

    private fun initAdapter(list: MutableList<ContactModel>?) {
        rvContacts.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val adapter = list?.let {
            ContactsAdapter(it,true, object : ContactsAdapter.InteractionListener {
                override fun onClick(contact: ContactModel) {
                    contact.isSelected = !contact.isSelected!!
                    if(contact.isSelected!!) {
                        contact.index = list.indexOf(contact)
                        selectedContactsList.list.add(contact)
                    } else{
                        selectedContactsList.list.remove(contact)
                    }
                    toolbar.counter.text = selectedContactsList.list.size.toString()
                }
            })
        }
        rvContacts.adapter = adapter
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_select_contacts, container, false)
    }



    fun getContacts(ctx: Context): MutableList<ContactModel>? {
        val list: MutableList<ContactModel> = ArrayList()
        val contentResolver: ContentResolver = ctx.contentResolver
        val cursor =
            contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
        if (cursor!!.count > 0) {
            while (cursor.moveToNext()) {
                val id =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    val cursorInfo = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf(id),
                        null
                    )
                    val inputStream: InputStream? =
                        ContactsContract.Contacts.openContactPhotoInputStream(
                            ctx.contentResolver,
                            ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id.toLong())
                        )
                    val person =
                        ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id.toLong())
                    val pURI = Uri.withAppendedPath(
                        person,
                        ContactsContract.Contacts.Photo.CONTENT_DIRECTORY
                    )
                    var photo: Bitmap? = null
                    if (inputStream != null) {
                        photo = BitmapFactory.decodeStream(inputStream)
                    }
                    while (cursorInfo!!.moveToNext()) {
                        val info = ContactModel()
                        info.id = id
                        info.name =
                            cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                        info.mobileNumber =
                            cursorInfo.getString(cursorInfo.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        info.index = cursor.position
                        list.add(info)
                    }
                    cursorInfo.close()
                }
            }
            cursor.close()
        }
        return list
    }


    companion object {

        @JvmStatic
        fun newInstance() =
            SelectContactsFragment()
    }
}

data class ContactModel (
    var id: String? = "",
    var name: String? = "",
    var mobileNumber: String? = "",
    var isSelected: Boolean? = false,
    var index: Int? = -1
): Serializable

data class ContactList (val list: MutableList<ContactModel>): Serializable
