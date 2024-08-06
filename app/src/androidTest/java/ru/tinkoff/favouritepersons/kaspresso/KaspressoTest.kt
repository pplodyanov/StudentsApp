package ru.tinkoff.favouritepersons.kaspresso

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.okJson
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.github.tomakehurst.wiremock.stubbing.Scenario
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.rules.RuleChain
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import ru.tinkoff.favouritepersons.fileToString
import ru.tinkoff.favouritepersons.kaspresso.screen.ActivityMainScreen
import ru.tinkoff.favouritepersons.kaspresso.screen.PersonItemActivityScreen
import ru.tinkoff.favouritepersons.presentation.activities.MainActivity
import ru.tinkoff.favouritepersons.rules.LocalhostPreferenceRule

@RunWith(Enclosed::class)
class KaspressoTest {

    class NotParameterizedTestPart : TestCase(kaspressoBuilder) {
        @get:Rule
        val ruleChain: RuleChain = RuleChain.outerRule(LocalhostPreferenceRule())
            .around(WireMockRule(5000))
            .around(ActivityScenarioRule(MainActivity::class.java))

        @After
        fun enableNetwork() {
            InstrumentationRegistry.getInstrumentation().getUiAutomation()
                .executeShellCommand("svc wifi enable")
            InstrumentationRegistry.getInstrumentation().getUiAutomation()
                .executeShellCommand("svc data enable")
        }

        @Test
        fun testStudentsAbsenseTextIsInvisible() {
            stubFor(
                get(urlPathMatching("/api/"))
                    .willReturn(
                        okJson(fileToString("mock/random-user-api-success.json"))
                    )
            )
            ActivityMainScreen {
                fabAddPerson.click()
                fabAddPersonByNetwork.click()
                twNoPersons.isInvisible()
            }
        }

        @Test
        fun testStudentsRemoving() {
            stubFor(
                get(urlPathMatching("/api/"))
                    .willReturn(
                        okJson(fileToString("mock/random-user-api-success.json"))
                    )
            )
            val personsCount = 3
            ActivityMainScreen {
                fabAddPerson.click()
                repeat(personsCount) {
                    fabAddPersonByNetwork.click()
                }
                assertPersonListSizeIs(personsCount)
                removePersonCard(1)
                assertPersonListSizeIs(personsCount - 1)
            }
        }

        @Test
        fun testDefaultSorting() {
            ActivityMainScreen {
                actionItemSort.click()
                bsdRbDefault.isChecked()
            }
        }

        @Test
        fun testSortingByAge() {
            val personsCount = 3
            generatePersons(personsCount)

            ActivityMainScreen {
                fabAddPerson.click()
                repeat(personsCount) {
                    fabAddPersonByNetwork.click()
                }
                assertPersonListSizeIs(personsCount)
                actionItemSort.click()
                bsdRbAge.click()
                for (i in 0 until personsCount) {
                    getPersonCard(i) {
                        checkPersonAge(personsCount - 1 - i)
                    }
                }
            }
        }

        @Test
        fun testAddPersonManually() {

            val name = "Billy"
            val surname = "Herrington"
            val gender = "М"
            val birthDate = "1969-07-14"
            val email = "goodnightsweetprince@test.com"
            val phone = "88005553535"
            val address = "Палм-Спрингс, Риверсайд, Калифорния, США"
            val imageUrl =
                "https://avatars.mds.yandex.net/get-kinopoisk-image/1599028/4120d16f-9a21-4972-8d35-a1564b9a9911/1920x"
            val score = "99"

            ActivityMainScreen {
                fabAddPerson.click()
                fabAddPersonManually.click()
            }
            PersonItemActivityScreen {
                createPerson(
                    name = name,
                    surname = surname,
                    gender = gender,
                    birthDate = birthDate,
                    email = email,
                    phone = phone,
                    address = address,
                    imageUrl = imageUrl,
                    score = score
                )
            }

            ActivityMainScreen {
                getPersonCard(0) {
                    checkPersonName(name, surname)
                    checkPersonPrivateInfo(gender, birthDate)
                    checkPersonEmail(email)
                    checkPersonPhone(phone)
                    checkPersonAddress(address)
                    checkPersonScore(score)
                }
            }
        }

        @Test
        fun testAddPersonManuallyGenderError() {
            ActivityMainScreen {
                fabAddPerson.click()
                fabAddPersonManually.click()
            }
            PersonItemActivityScreen {
                submitButton.click()
                checkGenderErrorText()
            }
        }

        @Test
        fun testAddPersonManuallyErrorHiding() {

            val name = "Billy"
            val surname = "Herrington"
            val gender = "aboba"
            val birthDate = "1969-07-14"
            val email = "goodnightsweetprince@test.com"
            val phone = "88005553535"
            val address = "Палм-Спрингс, Риверсайд, Калифорния, США"
            val imageUrl =
                "https://avatars.mds.yandex.net/get-kinopoisk-image/1599028/4120d16f-9a21-4972-8d35-a1564b9a9911/1920x"
            val score = "99"

            ActivityMainScreen {
                fabAddPerson.click()
                fabAddPersonManually.click()
            }
            PersonItemActivityScreen {
                createPerson(
                    name = name,
                    surname = surname,
                    gender = gender,
                    birthDate = birthDate,
                    email = email,
                    phone = phone,
                    address = address,
                    imageUrl = imageUrl,
                    score = score
                )

                checkGenderErrorText()
                setPersonGender("")
                checkGenderErrorAbsence()
            }
        }

        @Test
        fun testDisabledNetworkError() {
            InstrumentationRegistry.getInstrumentation().uiAutomation
                .executeShellCommand("svc wifi disable")
            InstrumentationRegistry.getInstrumentation().uiAutomation
                .executeShellCommand("svc data disable")
            WireMock.shutdownServer()

            ActivityMainScreen {
                fabAddPerson.click()
                Thread.sleep(500)
                fabAddPersonByNetwork.click()
                Thread.sleep(500)
                checkSnackBarText()
            }
        }
    }

    @RunWith(Parameterized::class)
    class ParameterizedPersonsTest(
        private val personsCount: Int,
        private val expectedName: String,
        private val expectedSurname: String,
        private val expectedGender: String,
        private val expectedBirthDate: String
    ) : TestCase(kaspressoBuilder) {
        companion object {
            @JvmStatic
            @Parameters
            fun data(): Iterable<Array<Any>> {
                return arrayListOf(
                    arrayOf(1, "Van", "Darkholm", "М", "1986-09-24T07:07:13.755Z"),
                    arrayOf(2, "Julio", "Rodríguez", "М", "1999-08-03T08:25:18.845Z"),
                    arrayOf(3, "Gertrude", "Bradley", "Ж", "1983-09-20T19:19:24.956Z"),
                    arrayOf(4, "Chaithra", "Pujari", "Ж", "1947-04-27T13:28:17.924Z")
                )
            }
        }

        @get:Rule
        val ruleChain: RuleChain = RuleChain.outerRule(LocalhostPreferenceRule())
            .around(WireMockRule(5000))
            .around(ActivityScenarioRule(MainActivity::class.java))

        @Test
        fun checkOpenPersonItemActivityScreen() = run {
            getRandomPersons(personsCount)

            ActivityMainScreen {
                fabAddPerson.click()
                repeat(personsCount) {
                    fabAddPersonByNetwork.click()
                }
                assertPersonListSizeIs(personsCount)
                clickPersonCard(0)
            }
            PersonItemActivityScreen {
                checkScreenOpened()
                checkPersonName(expectedName)
                checkPersonSurname(expectedSurname)
                checkPersonGender(expectedGender)
                checkPersonBirthDate(expectedBirthDate)
            }
        }

        @Test
        fun checkEditPersonName() = run {
            getRandomPersons(personsCount)

            val newName = "Иосиф"

            ActivityMainScreen {
                fabAddPerson.click()
                repeat(personsCount) {
                    fabAddPersonByNetwork.click()
                }
                assertPersonListSizeIs(personsCount)
                clickPersonCard(0)
            }
            PersonItemActivityScreen {
                checkScreenOpened()
                checkPersonName(expectedName)
                setPersonName(newName)
                submitButton.click()
            }
            ActivityMainScreen {
                checkPersonName(0, newName, expectedSurname)
            }
        }
    }
}

private fun getRandomPersons(personsCount: Int) {

    val personsStubs = listOf(
        "mock/random-user-api-success.json",
        "mock/random-user-1.json",
        "mock/random-user-2.json",
        "mock/random-user-3.json"
    )

    for (i in 0 until personsCount) {
        stubFor(
            get(urlPathMatching("/api/"))
                .inScenario("GetRandomPersons")
                .whenScenarioStateIs(
                    when (i) {
                        0 -> Scenario.STARTED
                        else -> "Step $i"
                    }
                )
                .willSetStateTo("Step ${i + 1}")
                .willReturn(
                    okJson(
                        fileToString(personsStubs[i])
                    )
                )
        )
    }
}

private fun generatePersons(personsCount: Int) {
    val ages = mutableListOf<Int>()
    repeat(personsCount) { index ->
        ages.add(index)
    }
    ages.shuffle()

    for (i in 0 until personsCount) {
        stubFor(
            get(urlPathMatching("/api/"))
                .inScenario("SortByAge")
                .whenScenarioStateIs(
                    when (i) {
                        0 -> Scenario.STARTED
                        else -> "Step $i"
                    }
                )
                .willSetStateTo("Step ${i + 1}")
                .willReturn(
                    okJson(
                        fileToString("mock/random-user-api-success.json").replace(
                            "\"age\": 37",
                            "\"age\": 3${ages[i]}"
                        )
                    )
                )
        )
    }
}

