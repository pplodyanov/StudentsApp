package ru.tinkoff.favouritepersons.kaspresso.screen

import android.view.View
import androidx.test.espresso.action.ViewActions
import io.github.kakaocup.kakao.check.KCheckBox
import io.github.kakaocup.kakao.recycler.KRecyclerItem
import io.github.kakaocup.kakao.recycler.KRecyclerView
import io.github.kakaocup.kakao.text.KButton
import io.github.kakaocup.kakao.text.KTextView
import org.hamcrest.Matcher
import org.junit.Assert
import ru.tinkoff.favouritepersons.R
import ru.tinkoff.favouritepersons.domain.DATE_FORMAT
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Locale

class ActivityMainScreen : BaseScreen() {

    // FABs
    val fabAddPerson = KButton { withId(R.id.fab_add_person) }
    val fabAddPersonByNetwork = KButton { withId(R.id.fab_add_person_by_network) }
    val fabAddPersonManually = KButton { withId(R.id.fab_add_person_manually) }

    val twNoPersons = KTextView { withId(R.id.tw_no_persons) }
    val snackBarError = KTextView { withText("Internet error! Check your connection") }

    // Sorting menu
    val actionItemSort = KButton { withId(R.id.action_item_sort) }
    val bsdRbDefault = KCheckBox { withId(R.id.bsd_rb_default) }
    val bsdRbAge = KCheckBox { withId(R.id.bsd_rb_age) }

    // RecyclerView
    val rvPersonList = KRecyclerView(
        builder = { withId(R.id.rv_person_list) },
        itemTypeBuilder = { itemType(::PersonItemScreen) }
    )

    class PersonItemScreen(matcher: Matcher<View>) : KRecyclerItem<PersonItemScreen>(matcher) {
        val personName = KTextView(matcher) { withId(R.id.person_name) }
        val personPrivateInfo = KTextView(matcher) { withId(R.id.person_private_info) }
        val personEmail = KTextView(matcher) { withId(R.id.person_email) }
        val personPhone = KTextView(matcher) { withId(R.id.person_phone) }
        val personAddress = KTextView(matcher) { withId(R.id.person_address) }
        val personScore = KTextView(matcher) { withId(R.id.person_rating) }

        fun checkPersonAge(lastDigit: Int) = personPrivateInfo.hasText("Male, 3$lastDigit")
        fun checkPersonName(name: String, surname: String) = personName.hasText("$name $surname")
        fun checkPersonPrivateInfo(gender: String, birthDate: String) {
            var info = ""
            info += when (gender) {
                "М" -> "Male"
                else -> "Female"
            } + ", "
            info += computeAgeFromBirthDate(birthDate)
            personPrivateInfo.hasText(info)
        }

        fun checkPersonEmail(email: String) = personEmail.hasText(email)
        fun checkPersonPhone(phone: String) = personPhone.hasText(phone)
        fun checkPersonAddress(address: String) = personAddress.hasText(address)
        fun checkPersonScore(score: String) = personScore.hasText(score)

        private fun computeAgeFromBirthDate(birthdate: String?, datePattern: String = DATE_FORMAT) =
            Period.between(
                LocalDate.parse(
                    birthdate,
                    DateTimeFormatter.ofPattern(datePattern).withLocale(Locale.US)
                ), LocalDate.now()
            ).years
    }

    fun removePersonCard(index: Int) {
        rvPersonList.childAt<PersonItemScreen>(index) {
            view.perform(ViewActions.swipeLeft())
        }
    }

    fun clickPersonCard(index: Int) {
        rvPersonList.childAt<PersonItemScreen>(index) {
            view.perform(ViewActions.click())
        }
    }

    fun getPersonCard(index: Int, function: PersonItemScreen.() -> Unit) {
        return rvPersonList.childAt<PersonItemScreen>(index) {
            function()
        }
    }

    // Assertions
    fun assertPersonListSizeIs(size: Int) = Assert.assertEquals(size, rvPersonList.getSize())
    fun checkSnackBarText() = snackBarError.isVisible()
    fun checkPersonName(index: Int, name: String, surname: String) {
        rvPersonList.childAt<PersonItemScreen>(index) {
            checkPersonName(name, surname)
        }
    }


    companion object {
        inline operator fun invoke(crossinline block: ActivityMainScreen.() -> Unit) =
            ActivityMainScreen().block()
    }
}