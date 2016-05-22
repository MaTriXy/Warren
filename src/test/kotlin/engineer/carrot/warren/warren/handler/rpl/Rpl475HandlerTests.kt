package engineer.carrot.warren.warren.handler.rpl

import engineer.carrot.warren.kale.irc.message.rpl.Rpl475Message
import engineer.carrot.warren.kale.irc.message.utility.CaseMapping
import engineer.carrot.warren.warren.state.CaseMappingState
import engineer.carrot.warren.warren.state.ChannelsState
import engineer.carrot.warren.warren.state.JoiningChannelLifecycle
import engineer.carrot.warren.warren.state.JoiningChannelState
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class Rpl475HandlerTests {

    lateinit var handler: Rpl475Handler
    lateinit var channelsState: ChannelsState

    @Before fun setUp() {
        channelsState = ChannelsState(joined = mutableMapOf())
        val caseMappingState = CaseMappingState(mapping = CaseMapping.RFC1459)
        handler = Rpl475Handler(channelsState, caseMappingState)
    }

    @Test fun test_handle_NonexistentChannel_DoesNothing() {
        handler.handle(Rpl475Message(source = "", target = "", channel = "#somewhere", contents = ""))

        assertEquals(ChannelsState(joining = mutableMapOf(), joined = mutableMapOf()), channelsState)
    }

    @Test fun test_handle_ValidChannel_SetsStatusToFailed() {
        channelsState.joining["#channel"] = JoiningChannelState("#channel", status = JoiningChannelLifecycle.JOINING)

        handler.handle(Rpl475Message(source = "", target = "", channel = "#channel", contents = ""))

        val expectedChannelState = JoiningChannelState("#channel", status = JoiningChannelLifecycle.FAILED)

        assertEquals(ChannelsState(joining = mutableMapOf("#channel" to expectedChannelState), joined = mutableMapOf()), channelsState)
    }

}