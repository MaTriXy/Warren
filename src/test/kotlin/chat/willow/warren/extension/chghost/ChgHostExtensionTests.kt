package chat.willow.warren.extension.chghost

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import chat.willow.kale.IKale
import chat.willow.kale.irc.message.utility.CaseMapping
import chat.willow.warren.state.CaseMappingState
import chat.willow.warren.state.JoinedChannelsState
import org.junit.Before
import org.junit.Test

class ChgHostExtensionTests {

    private lateinit var sut: ChgHostExtension
    private lateinit var mockKale: IKale
    private lateinit var channelsState: JoinedChannelsState

    @Before fun setUp() {
        mockKale = mock()
        channelsState = JoinedChannelsState(CaseMappingState(CaseMapping.RFC1459))

        sut = ChgHostExtension(mockKale, channelsState)
    }

    @Test fun test_setUp_RegistersCorrectHandlers() {
        sut.setUp()

        verify(mockKale).register(any<ChgHostHandler>())
    }

    @Test fun test_tearDown_UnregistersCorrectHandlers() {
        sut.tearDown()

        verify(mockKale).unregister(any<ChgHostHandler>())
    }

}