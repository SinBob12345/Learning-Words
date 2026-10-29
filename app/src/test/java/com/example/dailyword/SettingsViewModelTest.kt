package com.example.dailyword

import com.example.dailyword.viewmodel.SettingsViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SettingsViewModelTest {

    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setUp() {
        viewModel = SettingsViewModel()
    }

    @Test
    fun defaultSettings_areCorrect() {
        assertEquals("medium", viewModel.selectedLevel.value)
        assertFalse(viewModel.showPhonetic.value) // Free tier defaults phonetics to false
        assertTrue(viewModel.showDefinition.value)
        assertTrue(viewModel.showExample.value)
        assertFalse(viewModel.autoPlayAudio.value)
        assertFalse(viewModel.isPremium.value)
        assertFalse(viewModel.isDarkMode.value)
    }

    @Test
    fun setLevel_freeTierBlocksHardLevel() {
        viewModel.setLevel("easy")
        assertEquals("easy", viewModel.selectedLevel.value)

        viewModel.setLevel("hard")
        assertEquals("easy", viewModel.selectedLevel.value) // Blocked for Free tier

        viewModel.upgradeToPremium()
        viewModel.setLevel("hard")
        assertEquals("hard", viewModel.selectedLevel.value) // Allowed after Premium upgrade
    }

    @Test
    fun togglePhonetic_onlyAvailableForPremium() {
        viewModel.togglePhonetic()
        assertFalse(viewModel.showPhonetic.value) // Blocked for Free tier

        viewModel.upgradeToPremium()
        assertTrue(viewModel.showPhonetic.value) // Automatically enabled on upgrade

        viewModel.togglePhonetic()
        assertFalse(viewModel.showPhonetic.value) // Toggleable when Premium
    }

    @Test
    fun toggleDefinition_togglesState() {
        viewModel.toggleDefinition()
        assertFalse(viewModel.showDefinition.value)
    }

    @Test
    fun toggleExample_togglesState() {
        viewModel.toggleExample()
        assertFalse(viewModel.showExample.value)
    }

    @Test
    fun toggleAutoPlayAudio_togglesState() {
        viewModel.toggleAutoPlayAudio()
        assertTrue(viewModel.autoPlayAudio.value)
        viewModel.toggleAutoPlayAudio()
        assertFalse(viewModel.autoPlayAudio.value)
    }

    @Test
    fun darkMode_onlyAvailableForPremium() {
        viewModel.toggleDarkMode()
        assertFalse(viewModel.isDarkMode.value) // Free tier cannot toggle dark mode

        viewModel.upgradeToPremium()
        viewModel.toggleDarkMode()
        assertTrue(viewModel.isDarkMode.value) // Premium tier toggles dark mode
    }
}
