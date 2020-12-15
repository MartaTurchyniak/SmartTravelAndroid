package com.magic.smarttravel.screens.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.magic.smarttravel.R
import com.magic.smarttravel.screens.groups.GroupsActivity
import com.magic.smarttravel.screens.groups.NoGroupsAvailableActivity
import kotlinx.android.synthetic.main.signin_activity.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

@Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
class SignInActivity : AppCompatActivity(), View.OnClickListener {

    private var verificationInProgress = false
    private var storedVerificationId: String? = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    private val viewModel: SignInViewModel by viewModel()
    private val auth: FirebaseAuth by lazy { Firebase.auth }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signin_activity)

        // Restore instance state
        savedInstanceState?.let { onRestoreInstanceState(it) }

        this.attachListeners()
        this.observeViewModel()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun observeViewModel() {
        viewModel.signInCompletedAction().observe(this, Observer { nexStep ->
            when (nexStep) {
                SignInNextStep.USER_EDIT -> {
                    UserEditActivity.startActivity(this)
                }
                SignInNextStep.NO_GROUPS -> {
                    NoGroupsAvailableActivity.startActivity(this)
                }
                SignInNextStep.GROUPS -> {
                    GroupsActivity.startActivity(this)
                }
            }
            finish()
        })

        viewModel.startAuthorizationFlowAction().observe(this, Observer {
            startAuthorizationFlow()
        })
    }

    private fun startAuthorizationFlow() {
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted:$credential")
                verificationInProgress = false
                updateUI(STATE_VERIFY_SUCCESS, null, credential)
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.w(TAG, "onVerificationFailed", e)
                verificationInProgress = false

                if (e is FirebaseAuthInvalidCredentialsException) {
                    etPhone.error = "Invalid phone number."
                } else if (e is FirebaseTooManyRequestsException) {
                    Snackbar.make(
                        findViewById(android.R.id.content), "Quota exceeded.",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }

                updateUI(STATE_VERIFY_FAILED)
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d(TAG, "onCodeSent:$verificationId")

                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token

                updateUI(STATE_CODE_SENT)
            }
        }
    }

    private fun attachListeners() {
        btnVerify.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)

        if (verificationInProgress && validatePhoneNumber()) {
            startPhoneNumberVerification(etPhone.text.toString())
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, verificationInProgress)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        verificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS)
    }

    private fun startPhoneNumberVerification(phoneNumber: String) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,
            60,
            TimeUnit.SECONDS,
            this,
            callbacks
        )
        verificationInProgress = true
    }

    private fun verifyPhoneNumberWithCode(verificationId: String, code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        signInWithPhoneAuthCredential(credential)
    }

//    private fun resendVerificationCode(
//        phoneNumber: String,
//        token: PhoneAuthProvider.ForceResendingToken?
//    ) {
//        PhoneAuthProvider.getInstance().verifyPhoneNumber(
//            phoneNumber, // Phone number to verify
//            60, // Timeout duration
//            TimeUnit.SECONDS, // Unit of timeout
//            this, // Activity (for callback binding)
//            callbacks, // OnVerificationStateChangedCallbacks
//            token
//        )
//    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")

                    val user = task.result?.user
                    updateUI(STATE_SIGNIN_SUCCESS, user)

                    viewModel.onAuthorized()
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        etPhone.error = "Invalid code."
                    }
                    // Update UI
                    updateUI(STATE_SIGNIN_FAILED)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            updateUI(STATE_SIGNIN_SUCCESS, user)
        } else {
            updateUI(STATE_INITIALIZED)
        }
    }

    private fun updateUI(
        uiState: Int,
        user: FirebaseUser? = auth.currentUser,
        cred: PhoneAuthCredential? = null
    ) {
        when (uiState) {
            STATE_INITIALIZED -> {
                enableViews(true)
                initViews(true)
                tvDetail.text = null
            }
            STATE_CODE_SENT -> {
                initViews(false)
                etPhone.text = null
                tvDetail.setText(R.string.status_code_sent)
            }
            STATE_VERIFY_FAILED -> {
                initViews(true)
                tvDetail.setText(R.string.status_verification_failed)
            }
            STATE_VERIFY_SUCCESS -> {
                tvDetail.setText(R.string.status_verification_succeeded)
                if (cred != null) {
                    if (cred.smsCode != null) {
                        etPhone.setText(cred.smsCode)
                    } else {
                        etPhone.setText(R.string.instant_validation)
                    }
                }
            }
            STATE_SIGNIN_FAILED ->
                tvDetail.setText(R.string.status_sign_in_failed)
            STATE_SIGNIN_SUCCESS -> {
            }
        }

        if (user == null) {
//            tvStatus.setText(R.string.signed_out)
        } else {
            enableViews(false)
            etPhone.text = null
//            tvStatus.setText(R.string.signed_in)
            tvDetail.text = getString(R.string.firebase_status_fmt, user.uid)
        }
    }

    private fun enableViews(visible: Boolean) {
        btnVerify.visibility = if (visible) View.VISIBLE else View.GONE
        etPhone.visibility = if (visible) View.VISIBLE else View.GONE
        tvDescription.visibility = if (visible) View.VISIBLE else View.GONE
    }

    private fun initViews(initial: Boolean) {
        btnVerify.text = if (initial) "SEND CODE" else "VERIFY"
        etPhone.hint = if (initial) "Phone number" else " Code"
        ivVerify.setImageResource(if (initial) R.drawable.verify_number else R.drawable.verify_code)
    }

    private fun validatePhoneNumber(): Boolean {
        val phoneNumber = etPhone.text.toString()
        if (TextUtils.isEmpty(phoneNumber)) {
            etPhone.error = "Invalid phone number."
            return false
        }

        return true
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnVerify -> {
                if (btnVerify.text.toString() == "SEND CODE") {
                    if (!validatePhoneNumber()) {
                        return
                    }

                    startPhoneNumberVerification(etPhone.text.toString())
                } else {
                    val code = etPhone.text.toString()
                    if (TextUtils.isEmpty(code)) {
                        etPhone.error = "Cannot be empty."
                        return
                    }

                    verifyPhoneNumberWithCode(storedVerificationId!!, code)
                }
            }
//            R.id.buttonResend -> resendVerificationCode(fieldPhoneNumber.text.toString(), resendToken)
        }
    }

    companion object {

        fun start(context: Context) {
            val intent = Intent(context, SignInActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }

        private const val TAG = "PhoneAuthActivity"
        private const val KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress"
        private const val STATE_INITIALIZED = 1
        private const val STATE_VERIFY_FAILED = 3
        private const val STATE_VERIFY_SUCCESS = 4
        private const val STATE_CODE_SENT = 2
        private const val STATE_SIGNIN_FAILED = 5
        private const val STATE_SIGNIN_SUCCESS = 6
    }
}