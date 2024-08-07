package ru.tinkoff.favouritepersons.kaspresso.screen

import io.github.kakaocup.kakao.edit.KEditText
import io.github.kakaocup.kakao.edit.KTextInputLayout
import io.github.kakaocup.kakao.text.KButton
import io.github.kakaocup.kakao.text.KTextView
import ru.tinkoff.favouritepersons.R
import ru.tinkoff.favouritepersons.presentation.PersonErrorMessages
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class PersonItemActivityScreen : BaseScreen() {

    val twPersonScreenTitle = KTextView { withId(R.id.tw_person_screen_title) }
    val etName = KEditText { withId(R.id.et_name) }
    val etSurname = KEditText { withId(R.id.et_surname) }
    val etGender = KEditText { withId(R.id.et_gender) }
    val tilGender = KTextInputLayout { withId(R.id.til_gender) }
    val etBirthdate = KEditText { withId(R.id.et_birthdate) }
    val etEmail = KEditText { withId(R.id.et_email) }
    val etPhone = KEditText { withId(R.id.et_phone) }
    val etAddress = KEditText { withId(R.id.et_address) }
    val etImage = KEditText { withId(R.id.et_image) }
    val etScore = KEditText { withId(R.id.et_score) }

    val submitButton = KButton { withId(R.id.submit_button) }

    // Actions
    fun createPerson(
        name: String,
        surname: String,
        gender: String,
        birthDate: String,
        email: String,
        phone: String,
        address: String,
        imageUrl: String,
        score: String
    ) {
        setPersonName(name)
        setPersonSurname(surname)
        setPersonGender(gender)
        setPersonBirthDate(birthDate)
        setPersonEmail(email)
        setPersonPhoneNumber(phone)
        setPersonAddress(address)
        setPersonImageUrl(imageUrl)
        setPersonTotalScore(score)
        submitButton.click()
    }

    fun setPersonName(value: String) = etName.replaceText(value)
    fun setPersonSurname(value: String) = etSurname.replaceText(value)
    fun setPersonGender(value: String) = etGender.replaceText(value)
    fun setPersonBirthDate(value: String) = etBirthdate.replaceText(value)
    fun setPersonEmail(value: String) = etEmail.replaceText(value)
    fun setPersonPhoneNumber(value: String) = etPhone.replaceText(value)
    fun setPersonAddress(value: String) = etAddress.replaceText(value)
    fun setPersonImageUrl(value: String) = etImage.replaceText(value)
    fun setPersonTotalScore(value: String) = etScore.replaceText(value)

    // Assertions
    fun checkScreenOpened() = twPersonScreenTitle.isVisible()

    fun checkPersonName(name: String) = etName.hasText(name)

    fun checkPersonSurname(surname: String) = etSurname.hasText(surname)

    fun checkPersonGender(gender: String) = etGender.hasText(gender)
    fun checkGenderErrorText() {
        tilGender.isErrorEnabled()
        tilGender.hasError(PersonErrorMessages.GENDER.errorMessage)
    }

    fun checkGenderErrorAbsence() {
        tilGender.hasNoError()
    }

    fun checkPersonBirthDate(birthDate: String) = etBirthdate.hasText(dobDateToBirthDate(birthDate))

    companion object {
        inline operator fun invoke(crossinline block: PersonItemActivityScreen.() -> Unit) =
            PersonItemActivityScreen().block()
    }

    private fun dobDateToBirthDate(dobDate: String): String {
        return LocalDateTime.parse(
            dobDate, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withLocale(
                Locale.US
            )
        ).toLocalDate().toString()
    }
}